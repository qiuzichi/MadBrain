package com.unipad.common;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unipad.IOperateGame;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.common.widget.HIDDialog;
import com.unipad.utils.LogUtil;
import com.unipad.utils.ToastUtil;

import java.util.Map;

public abstract class BasicCommonFragment extends Fragment implements
        View.OnClickListener, CommonFragment.ICommunicate, IOperateGame {
    protected ViewGroup mViewParent;
    private Handler handler;
    private static final int MSG_PROGRESS = 0;
    protected int progress = 100;
    protected AbsMatchActivity mActivity;
    /**
     * 记忆结束时，弹出dialog，等待裁判开始统一记忆，如果这个时候裁判点击暂停，那个dialog消失，出现
     * 暂停的dialog，恢复开始的时候，原来等待裁判开始的dialog又得显示出来，倒计时不能开始
     */
    private boolean isNeedStartGame;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (AbsMatchActivity) getActivity();
        mViewParent = (ViewGroup) inflater.inflate(getLayoutId(), container, false);
        if (isMatchMode()) {
            handler = new Handler() {
                @Override
                public void dispatchMessage(Message msg) {
                    ((CommonActivity) getActivity()).progressGame(progress);
                    if (progress == 100 || progress == 200 || progress == 201) {

                    } else {
                        LogUtil.e("", "Rememory progress :" + progress);
                        handler.sendEmptyMessageDelayed(MSG_PROGRESS, 10000);
                    }
                }
            };
        }
        return mViewParent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isMatchMode()) {
            ((CommonActivity) getActivity()).getCommonFragment().setICommunicate(this);
        } else {
            ((PractiseGameActivity) getActivity()).getCommonFragment().setICommunicate(this);
        }

    }

    /**
     * 是否是比赛模式
     **/

    protected boolean isMatchMode() {
        return true;
    }

    @Override
    public void changeBg(int color) {
        mViewParent.setBackgroundColor(color);
    }

    @Override
    public void exitActivity() {
        getActivity().finish();
    }

    public abstract int getLayoutId();

    @Override
    public void pauseGame() {

    }

    @Override
    public void startMemory() {

    }

    @Override
    public void startRememory() {
        if (isMatchMode()) {
            isNeedStartGame = false;
            progress = 101;
            handler.sendEmptyMessage(MSG_PROGRESS);
        }

    }

    @Override
    public void reStartGame() {
        if (isMatchMode()) {
            if (isNeedStartGame) {
                ToastUtil.createTipDialog(getActivity(), Constant.SHOW_GAME_PAUSE, "等待裁判开始答题", R.drawable.wait_rememory).show();
                return;
            }

            switch (mActivity.getService().state) {
                case AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY:
                case AbsBaseGameService.GO_IN_MATCH_START_MEMORY:
                    HIDDialog.dismissAll();
                    break;
                case AbsBaseGameService.GO_IN_MATCH_DOWNLOADED:
                case AbsBaseGameService.GO_IN_MATCH_END_MEMORY:
                    ToastUtil.createTipDialog(getActivity(), Constant.SHOW_GAME_PAUSE, "等待裁判开始答题",R.drawable.wait_memory).show();
                    break;
            }
        }
    }

    @Override
    public void initDataFinished() {
        if (isMatchMode()) {
            progress = 201;
            handler.sendEmptyMessage(MSG_PROGRESS);
        }
    }

    @Override
    public void finishGame() {

    }

    @Override
    public void downloadingQuestion(Map<String, String> map) {

    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {

    }

    @Override
    public void rememoryTimeToEnd(int answerTime) {
        if (isMatchMode()) {
            progress = 200;
            handler.sendEmptyMessage(MSG_PROGRESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(MSG_PROGRESS);
        }
    }

    public void sendMsgToPreper() {
        if (isMatchMode()) {
            isNeedStartGame = true;
            progress = 100;
            handler.sendEmptyMessage(MSG_PROGRESS);
            if (!mActivity.getService().isPause) {
                ToastUtil.createTipDialog(mActivity, Constant.SHOW_GAME_PAUSE, "等待裁判开始答题",R.drawable.wait_rememory).show();
            }
        }
    }

    public boolean isNeedStartGame() {
        return isNeedStartGame;

    }

    @Override
    public void updateAfter() {

    }

    @Override
    public void updatePreper() {

    }

    @Override
    public void updateView(int pre, int next) {

    }
}
