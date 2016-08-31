package com.unipad.common;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.IOperateGame;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.home.bean.RuleGame;
import com.unipad.brain.home.dao.HomeGameHandService;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.io.mina.SocketThreadManager;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.CountDownTime;
import com.unipad.utils.DateUtil;
import com.unipad.utils.LogUtil;
import com.unipad.utils.PicUtil;
import com.unipad.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.x;

import java.util.Map;
/**
 * Created by Wbj on 2016/4/7.
 */
public class CommonFragment extends Fragment implements View.OnClickListener, CountDownTime.TimeListener,IDataObserver,IOperateGame{
    private static final int[] COLORS = {R.color.bg_one, R.color.bg_two, R.color.bg_three};
    private static final String TAG = "CommonFragment";
    private CommonActivity mActivity;
    private RelativeLayout mParentLayout;
    private TextView mTextName, mTextAgeAds, mTextTime, mTextCompeteProcess;
    private Button mBtnCompeteMode;
    private CountDownTime mCountDownTime;
    private ImageView mIconImageView;
    /**
     * 是否处于回忆模式，只有两种模式且先记忆再回忆；默认为false，即处于记忆模式；
     */
    private boolean isRememoryStatus;
    private ICommunicate mICommunicate;
    private SparseArray mColorArray = new SparseArray();
    private int memoryTime;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentLayout = (RelativeLayout) inflater.inflate(R.layout.common_frg_left, container, false);
        return mParentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.e("CommonFragment", "----onActivityCreated--");
        mActivity = (CommonActivity) getActivity();

        mTextName = (TextView) mParentLayout.findViewById(R.id.user_name);
        mTextAgeAds = (TextView) mParentLayout.findViewById(R.id.user_age_ads);
        mTextTime = (TextView) mParentLayout.findViewById(R.id.text_time);
        mTextCompeteProcess = (TextView) mParentLayout.findViewById(R.id.memory_stats);
        mBtnCompeteMode = (Button) mParentLayout.findViewById(R.id.btn_compete_process);
        mBtnCompeteMode.setOnClickListener(this);
        mParentLayout.findViewById(R.id.text_exit).setOnClickListener(this);

        mTextAgeAds.setSelected(true);
        mTextName.setSelected(true);
        this.getBgColorArray(mParentLayout);

        mCountDownTime = new CountDownTime(0, false);
        mCountDownTime.setTimeListener(this);
        mTextTime.setText(mCountDownTime.getTimeString());
        mTextName.setText(AppContext.instance().loginUser.getUserName()  + DateUtil.getMatchGroud(mActivity));
        mTextAgeAds.setText(getString(R.string.person_level) + AppContext.instance().loginUser.getLevel());

        mIconImageView = (ImageView) mParentLayout.findViewById(R.id.user_photo);
        ((HomeGameHandService) AppContext.instance().getService(Constant.HOME_GAME_HAND_SERVICE)).registerObserver(HttpConstant.GET_RULE_NOTIFY, this);
       // x.image().bind(mIconImageView, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto());
        //if (CompeteItemEntity.getInstance().getCompeteItem().equals(getString(R.string.project_9))) {
          //  mTextCompeteProcess.setText(R.string.playing_voice);
        //}

        if (!TextUtils.isEmpty(AppContext.instance().loginUser.getPhoto())) {
            x.image().bind(mIconImageView, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto(), new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap map = PicUtil.drawableToBitmap(drawable);
                    mIconImageView.setImageBitmap(PicUtil.getRoundedCornerBitmap(map, 360));
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    mIconImageView.setImageResource(R.drawable.set_headportrait);
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }else {
            mIconImageView.setImageResource(R.drawable.set_headportrait);
        }
    }



    /**
     * 获取背景颜色集合并设置示例图片背景
     */
    private void getBgColorArray(View view) {
        ImageView viewImg;
        for (int i = 0; i < COLORS.length; i++) {
            viewImg = (ImageView) view.findViewById(R.id.text_change_layout_img_bg1 + i);
            viewImg.setBackgroundColor(mActivity.getResources().getColor(COLORS[i]));
            viewImg.setOnClickListener(this);

            mColorArray.put(R.id.text_change_layout_img_bg1 + i, COLORS[i]);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mCountDownTime.resumeCountTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCountDownTime.pauseCountTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG,"onDestory");
        mActivity = null;
        mICommunicate = null;
        ((HomeGameHandService)AppContext.instance().getService(Constant.HOME_GAME_HAND_SERVICE)).unRegisterObserve(HttpConstant.GET_RULE_NOTIFY, this);
        mCountDownTime.stopCountTime();
        mColorArray.clear();
    }

    /**
     * 切换比赛进程：记忆模式-->回忆模式-->提交答案
     */
    private void switchCompeteProcess() {
        int takeTIme = mCountDownTime.stopCountTime();
        if (!isRememoryStatus) {//切换到回忆模式
            isRememoryStatus = true;
            mTextCompeteProcess.setText(R.string.rememorying);
            mBtnCompeteMode.setText(R.string.commit_answer);
            if (mICommunicate != null) {
                startRememoryTimeCount();
                memoryTime = takeTIme;
                mActivity.getService().state = AbsBaseGameService.GO_IN_MATCH_END_MEMORY;
                mICommunicate.memoryTimeToEnd(memoryTime);

            }

        } else {//回忆模式下才可以提交答案

            this.commitAnswer(takeTIme);
        }
    }
    private void reset(){
        isRememoryStatus = false;
        mBtnCompeteMode.setText(R.string.end_memory);
        mBtnCompeteMode.setEnabled(true);
        mTextCompeteProcess.setText(R.string.memorying);
        mTextTime.setText(mCountDownTime.setNewSeconds(mActivity.getService().rule.getMemoryTime()[mActivity.getService().round - 1], false));

    }
    private void startRememoryTimeCount(){
        mTextTime.setText(mCountDownTime.setNewSeconds(mActivity.getService().rule.getReMemoryTime()[mActivity.getService().round - 1], false));
        LogUtil.e("CommonFragment", "第" + (mActivity.getService().round - 1) + "轮的回忆时间为=" + mActivity.getService().rule.getReMemoryTime()[mActivity.getService().round - 1]);
    }
    /**
     * 提交答案
     */
    private void commitAnswer(final  int rememoryTime) {
        mBtnCompeteMode.setEnabled(false);
        if (mICommunicate != null) {
            final int round = mActivity.getService().round;
            mActivity.getService().state = AbsBaseGameService.GO_IN_MATCH_END_RE_MEMORY;
            mICommunicate.rememoryTimeToEnd(rememoryTime);
            if (mActivity.getService().isLastRound()) {
                mActivity.getService().state = AbsBaseGameService.GO_IN_MATCH_OVER;
                ToastUtil.createOnlyOkDialog(mActivity, Constant.MATCH_OVER_DLG, "比赛结束", "请等待裁判公布结果", null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HIDDialog.dismissAll();
                        mICommunicate.exitActivity();
                    }
                }).show();
            }
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    SocketThreadManager.sharedInstance().finishedGameByUser(mActivity.getMatchId(),mActivity.getService().getScore(),memoryTime,rememoryTime,mActivity.getService().getAnswerData(),round);
                }
            }.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_compete_process:
                this.switchCompeteProcess();
                break;
            case R.id.text_change_layout_img_bg1:
            case R.id.text_change_layout_img_bg2:
            case R.id.text_change_layout_img_bg3:
                if (mICommunicate != null) {
                    mICommunicate.changeBg(mActivity.getResources().getColor((Integer) mColorArray.get(v.getId())));
                }
                break;
            case R.id.text_exit:
                if (mICommunicate != null) {
                    mICommunicate.exitActivity();
                }
            default:
                break;
        }
    }

    @Override
    public void endOfTime() {
        if (isRememoryStatus) {
            mTextTime.setText(R.string.time_end);
        }
        this.switchCompeteProcess();
    }

    @Override
    public void getTime(String hour, String minute, String second) {
        mTextTime.setText(mCountDownTime.getTimeString());
    }

    public void setICommunicate(ICommunicate iCommunicate) {
        mICommunicate = iCommunicate;
    }

    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.GET_RULE_NOTIFY:
                mActivity.getService().rule = (RuleGame) o;
                mActivity.getService().allround =  mActivity.getService().rule.getCountRecall();
                mTextTime.setText( mCountDownTime.setNewSeconds(mActivity.getService().rule.getMemoryTime()[mActivity.getService().round-1], false));
                break;
            default:
                break;
        }
    }

    @Override
    public void initDataFinished() {
       reset();
    }

    @Override
    public void downloadingQuestion(Map<String,String> data) {

    }

    @Override
    public void startMemory() {
        mBtnCompeteMode.setEnabled(true);
        mCountDownTime.startCountTime();
    }

    @Override
    public void startRememory() {
        mCountDownTime.startCountTime();
    }



    @Override
    public void pauseGame() {
        mCountDownTime.pauseCountTime();
    }

    @Override
    public void reStartGame() {
        if (mICommunicate != null){
            BasicCommonFragment basicCommonFragment = (BasicCommonFragment)mICommunicate;
            if (!basicCommonFragment.isNeedStartGame()) {
                mCountDownTime.resumeCountTime();
            }
        }

    }

    @Override
    public void finishGame() {

    }
    /**
     * CommonFragment对外通讯接口
     */
    public interface ICommunicate {
        /**
         * 更换背景
         *
         * @param color 颜色值
         */
        void changeBg(int color);

        /**
         * 当记忆时间到时调用的方法
         */
        void memoryTimeToEnd(int memoryTime);

        /**
         * 当回忆时间到时调用的方法
         */
        void rememoryTimeToEnd(int answerTime);

        /**
         * 退出当前Activity
         */
        void exitActivity();


    }

}
