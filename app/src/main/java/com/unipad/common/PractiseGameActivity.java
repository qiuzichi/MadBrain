package com.unipad.common;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.unipad.AppContext;
import com.unipad.IOperateGame;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.absPic.view.PractiseFigureFragment;
import com.unipad.brain.longPoker.view.LongPokerPracticeFragment;
import com.unipad.brain.number.BinaryPracticeFragment;
import com.unipad.brain.number.ListenPracticeNumFragment;
import com.unipad.brain.number.LongPracticeNumFragment;
import com.unipad.brain.number.PractiseQuickRandomFragment;
import com.unipad.brain.portraits.view.HeadPracticeFragment;
import com.unipad.brain.quickPoker.view.PrictisePokerFragment;
import com.unipad.brain.virtual.VirtualPracticeFragment;
import com.unipad.brain.words.view.WordPracticeFragment;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HitopGetRandomQuestion;
import com.unipad.http.HitopRequest;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.LogUtil;
import com.unipad.utils.ToastUtil;

import org.json.JSONException;
import org.xutils.common.Callback;

import java.util.Map;

/**
 * Created by gongkan on 2016/6/27.
 */
public class PractiseGameActivity extends AbsMatchActivity implements IDataObserver,IOperateGame{
    /**
     * Created by Wbj on 2016/4/7.
     */
    private static final String TAG = "PractiseGameActivity";
    private PractiseCommLeftFragment mCommonFragment = new PractiseCommLeftFragment();
    private AbsBaseGameService service;

    private Handler handler;
    private BasicCommonFragment gameFragment;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String questionId;

    private String projectId;

    long startTime;

    private static final int DOWNLOAD_QUESTION = 1;
    private static final int SHOW_WAIT_DLG= 2;
    private static final int RESTAT_GAME = 3;
    private static final int FINISH_GAME = 4;
    private static final int INIT_DATA_FINISH = 5;
    private static final int STRAT_REMEMORY = 6;
    private static final int STRAT_MEMORY = 7;
    private static final int DLG_DELAY_DISMISS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_aty);
        startTime = System.currentTimeMillis();
        projectId = getIntent().getStringExtra("projectId");
        service = (AbsBaseGameService) AppContext.instance().getGameServiceByProject(projectId);
        service.setOperateGame(this);
        service.registerObserver(HttpConstant.GET_RANDOM_QUESTION_ERR, this);
        service.registerObserver(HttpConstant.GET_RANDOM_QUESTION_OK,this);
        handler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                int what = msg.what;
                switch (msg.what) {
                    case STRAT_MEMORY:
                        LogUtil.e(TAG, "STRAT_MEMORY");
                        HIDDialog.dismissAll();
                        gameFragment.startMemory();
                        mCommonFragment.startMemory();
                        break;
                    case STRAT_REMEMORY:
                        LogUtil.e(TAG, "STRAT_REMEMORY");
                        HIDDialog.dismissAll();
                        gameFragment.startRememory();
                        mCommonFragment.startRememory();
                        break;
                    case DOWNLOAD_QUESTION:
                        LogUtil.e(TAG, "DOWNLOAD_QUESTION");
                        ToastUtil.createTipDialog(PractiseGameActivity.this, Constant.SHOW_GAME_PAUSE, "下载试题中",R.drawable.wait_match_loading).show();
                        break;
                    case RESTAT_GAME:
                        LogUtil.e(TAG, "RESTAT_GAME");
                        HIDDialog.dismissAll();
                        gameFragment.reStartGame();
                        mCommonFragment.reStartGame();
                        break;
                    case FINISH_GAME:
                        LogUtil.e(TAG, "FINISH_GAME");
                        gameFragment.finishGame();
                        mCommonFragment.finishGame();
                        break;
                    case DLG_DELAY_DISMISS:
                        LogUtil.e("", "DLG_DELAY_DISMISS");
                            HIDDialog.dismissAll();
                        break;
                    case INIT_DATA_FINISH:
                        LogUtil.e("", "INIT_DATA_FINISH");
                        gameFragment.initDataFinished();
                        mCommonFragment.initDataFinished();
                        service.startMemory(1);
                        break;
                    case SHOW_WAIT_DLG:
                        LogUtil.e(TAG, "SHOW_WAIT_DLG");
                        ToastUtil.createTipDialog(PractiseGameActivity.this, Constant.SHOW_GAME_PAUSE, "下载试题中",R.drawable.wait_match_loading).show();
                        break;
                }
            }
        };

                HitopGetRandomQuestion randomQuestion = new HitopGetRandomQuestion(projectId);
                randomQuestion.setService(service);
                randomQuestion.post();

        handler.sendEmptyMessage(SHOW_WAIT_DLG);

        /** handler.post(new Runnable() {
        @Override public void run() {
        dialog = ToastUtil.createTipDialog(CommonActivity.this, Constant.SHOW_GAME_PAUSE, "签到，等待裁判下发试题");
        dialog.show();
        }
        }
         );*/


    }
    public AbsBaseGameService getService() {
        return service;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = startTime - System.currentTimeMillis();
        LogUtil.e("-------", "time = " + startTime);
    }

    @Override
    public void initData() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.common_lfg_container, mCommonFragment);
        if (Constant.GAME_BINARY_NUM.equals(projectId)) {
            gameFragment = new BinaryPracticeFragment();
        } else if (Constant.GAME_VIRTUAL_DATE.equals(projectId)) {
            gameFragment = new VirtualPracticeFragment();
        } else if (Constant.GAME_RANDOM_WORDS.equals(projectId)) {
            gameFragment = new WordPracticeFragment();
        } else if (Constant.GAME_PORTRAITS.equals(projectId)) {
            gameFragment = new HeadPracticeFragment();

        } else if (Constant.GAME_ABS_PICTURE.equals(projectId)) {
            gameFragment = new PractiseFigureFragment();
        } else if (Constant.GAME_LONG_NUM.equals(projectId)) {
            gameFragment = new LongPracticeNumFragment();
        } else if (Constant.GAME_QUICKIY_POCKER.equals(projectId)) {
            gameFragment = new PrictisePokerFragment();
        } else if (Constant.GAME_LONG_POCKER.equals(projectId)) {
            gameFragment = new LongPokerPracticeFragment();
        } else if (Constant.GAME_RANDOM_NUM.equals(projectId)) {
            gameFragment = new PractiseQuickRandomFragment();
        }else if(Constant.GAME_LISTON_AND_MEMORY_WORDS.equals(projectId)){
            gameFragment = new ListenPracticeNumFragment();
        }
        fragmentTransaction.replace(R.id.common_rfg_container, gameFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameFragment = null;
        mCommonFragment = null;
        service.unRegisterObserve(HttpConstant.GET_RANDOM_QUESTION_ERR, this);
        service.unRegisterObserve(HttpConstant.GET_RANDOM_QUESTION_OK, this);
        AppContext.instance().clearService(service);
    }

    public PractiseCommLeftFragment getCommonFragment() {
        return mCommonFragment;
    }

    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.GET_RANDOM_QUESTION_ERR:
                HIDDialog.dismissAll();
                handler.removeMessages(SHOW_WAIT_DLG);
                HIDDialog dialog = ToastUtil.createOnlyOkDialog(this, Constant.GET_RANDOM_QUESTION_ERROR_DLG, "error", (String) o, null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HIDDialog.dismissAll();
                    }
                });
                dialog.show();
                break;
            case  HttpConstant.GET_RANDOM_QUESTION_OK:
                questionId = (String) o;
                break;
            default:
                break;
        }
    }

    @Override
    public void initDataFinished() {
        handler.sendEmptyMessage(INIT_DATA_FINISH);
    }

    @Override
    public void downloadingQuestion(Map<String, String> data) {

    }

    @Override
    public void startMemory() {
        handler.sendEmptyMessage(STRAT_MEMORY);
    }

    @Override
    public void startRememory() {
        handler.sendEmptyMessage(STRAT_REMEMORY);
    }

    @Override
    public void pauseGame() {

    }

    @Override
    public void reStartGame() {

    }

    @Override
    public void finishGame() {

    }

    public void sendMsgGetSocre(int memoryTime,int rememory,Callback.CommonCallback<String> callback){
        HitopRequest<String> request = new HitopRequest<String>("/api/practice/save") {
            @Override
            public String buildRequestURL() {
                return null;
            }

            @Override
            public String handleJsonData(String json) throws JSONException {
                return null;
            }
        };
        request.buildRequestParams("userId",AppContext.instance().loginUser.getUserId());
        request.buildRequestParams("userName",AppContext.instance().loginUser.getUserName());
        request.buildRequestParams("projectId",projectId);
        request.buildRequestParams("questionId",questionId);
        request.buildRequestParams("memtime",memoryTime+"");
        request.buildRequestParams("rectime",rememory+"");
        request.buildRequestParams("content",service.getAnswerData());
        request.post(callback);
    }
}

