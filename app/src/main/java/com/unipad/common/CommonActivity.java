package com.unipad.common;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.IOperateGame;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.App;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.absPic.view.AbsFigureFragment;
import com.unipad.brain.home.bean.RuleGame;
import com.unipad.brain.home.dao.HomeGameHandService;
import com.unipad.brain.longPoker.view.LongPokerRightFragment;
import com.unipad.brain.number.BinaryRightFragment;
import com.unipad.brain.number.LongNumFragment;
import com.unipad.brain.number.NumberRightFragment;
import com.unipad.brain.number.QuickRandomNumFragment;
import com.unipad.brain.portraits.view.HeadPortraitFragment;
import com.unipad.brain.quickPoker.view.QuickPokerRightFragment;
import com.unipad.brain.virtual.VirtualRightFragment;
import com.unipad.brain.words.view.WordRightFragment;
import com.unipad.common.widget.HIDDialog;
import com.unipad.io.mina.SocketThreadManager;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.LogUtil;
import com.unipad.utils.ToastUtil;

import java.util.Map;

/**
 * Created by Wbj on 2016/4/7.
 */
public class CommonActivity extends BasicActivity implements IDataObserver,IOperateGame{
    private CommonFragment mCommonFragment = new CommonFragment();

    private AbsBaseGameService service;
    public String getMatchId() {
        return matchId;
    }
    private Handler handler;
    private BasicCommonFragment gameFragment;
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String matchId;

    private String projectId;

    long startTime;



    private static final int DOWNLOAD_QUESTION = 1;
    private static final int PAUSE_GAME = 2;
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
        matchId = getIntent().getStringExtra("matchId");
        projectId = getIntent().getStringExtra("projectId");
        service = (AbsBaseGameService) AppContext.instance().getGameServiceByProject(projectId);
        service.setOperateGame(this);
        handler  = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                int what = msg.what;
                switch (msg.what) {
                    case STRAT_MEMORY:
                        HIDDialog.dismissAll();
                        gameFragment.startMemory();
                        mCommonFragment.startMemory();
                        break;
                    case STRAT_REMEMORY:
                        HIDDialog.dismissAll();
                        gameFragment.startRememory();
                        mCommonFragment.startRememory();
                        break;
                    case DOWNLOAD_QUESTION:
                        ToastUtil.createTipDialog(CommonActivity.this, Constant.SHOW_GAME_PAUSE, "下载试题中").show();
                        break;
                    case PAUSE_GAME:
                        HIDDialog.dismissAll();
                        ToastUtil.createTipDialog(CommonActivity.this, Constant.SHOW_GAME_PAUSE, "比赛暂停").show();
                        gameFragment.pauseGame();
                        mCommonFragment.pauseGame();
                        break;
                    case RESTAT_GAME:
                        HIDDialog.dismissAll();
                        gameFragment.reStartGame();
                        mCommonFragment.reStartGame();
                        break;
                    case FINISH_GAME:
                        gameFragment.finishGame();
                        mCommonFragment.finishGame();
                        break;
                    case DLG_DELAY_DISMISS:
                        HIDDialog dialog = HIDDialog.getExistDialog(Constant.SHOW_GAME_PAUSE);
                        if (dialog == null) {
                            HIDDialog.dismissAll();
                            ToastUtil.createTipDialog(CommonActivity.this, Constant.SHOW_GAME_PAUSE, "等待裁判准备开始").show();
                        } else {
                            ((TextView) dialog.findViewById(R.id.dialog_tip_content)).setText("等待裁判准备开始");
                        }
                        break;
                    case INIT_DATA_FINISH:
                        gameFragment.initDataFinished();
                        mCommonFragment.initDataFinished();
                        handler.sendEmptyMessageDelayed(DLG_DELAY_DISMISS, 5000);

                        new Thread() {
                           @Override
                           public void run() {
                               SocketThreadManager.sharedInstance().
                                       downLoadQuestionOK(matchId, 100);
                               LogUtil.e("", "-------------下载完成-----------");
                           }
                       }.start();;
                        break;
                }
            }
        };

       /** handler.post(new Runnable() {
                         @Override
                         public void run() {
                             dialog = ToastUtil.createTipDialog(CommonActivity.this, Constant.SHOW_GAME_PAUSE, "签到，等待裁判下发试题");
                             dialog.show();
                         }
                     }
        );*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ((HomeGameHandService) AppContext.instance().getService(Constant.HOME_GAME_HAND_SERVICE)).getRule(matchId);
                        SocketThreadManager.sharedInstance().setService(service);
                        SocketThreadManager.sharedInstance().setMatchId(matchId);
                        try {
                            Thread.sleep(300);
                            SocketThreadManager.sharedInstance().startThreads();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    }
                }).start();


    }

    public AbsBaseGameService getService() {
        return service;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = startTime- System.currentTimeMillis();
        LogUtil.e("-------", "time = " + startTime);
    }
    @Override
    public void initData() {
        FrameLayout view = (FrameLayout) findViewById(R.id.common_rfg_container);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.common_lfg_container, mCommonFragment);
        String competeItem = Constant.getProjectName(projectId);
        if (competeItem.equals(getString(R.string.project_2))) {
            gameFragment = new BinaryRightFragment();
        } else if (competeItem.equals(getString(R.string.project_6))) {
            gameFragment = new VirtualRightFragment();
        } else if (competeItem.equals(getString(R.string.project_8))) {
            gameFragment = new WordRightFragment();
        } else if (competeItem.equals(getString(R.string.project_1))) {
            gameFragment = new HeadPortraitFragment();
        } else if (competeItem.equals(getString(R.string.project_4))) {
            gameFragment = new AbsFigureFragment();
        } else if (competeItem.equals(getString(R.string.project_3))){
            gameFragment = new LongNumFragment();
        } else if (competeItem.equals(getString(R.string.project_5))) {
            gameFragment = new QuickRandomNumFragment();
        }  else if (competeItem.equals(getString(R.string.project_9))) {

        } else if (competeItem.equals(getResources().getString(R.string.project_10))){
            gameFragment = new QuickPokerRightFragment();
        } else if (competeItem.equals(getResources().getString(R.string.project_7))){
            gameFragment = new LongPokerRightFragment();
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
        AppContext.instance().clearService(service);
        SocketThreadManager.sharedInstance().releaseInstance();
    }


    public CommonFragment getCommonFragment() {
        return mCommonFragment;
    }

    @Override
    public void update(int key, Object o) {

    }

    @Override
    public void initDataFinished() {
        handler.sendEmptyMessage(INIT_DATA_FINISH);
    }

    @Override
    public void downloadingQuestion(Map<String,String> data) {
        handler.sendEmptyMessage(DOWNLOAD_QUESTION);

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
        handler.sendEmptyMessage(PAUSE_GAME);

    }

    @Override
    public void reStartGame() {
        handler.sendEmptyMessage(RESTAT_GAME);

    }

    @Override
    public void finishGame() {
        handler.sendEmptyMessage(FINISH_GAME);

    }

    public void progressGame(final int progress){
        new Thread(){
            @Override
            public void run() {
                super.run();
                SocketThreadManager.sharedInstance().progressGame(matchId,progress,service.round);
            }
        }.start();
    }
}
