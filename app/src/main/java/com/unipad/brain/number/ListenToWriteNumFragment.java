package com.unipad.brain.number;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.unipad.brain.R;
import com.unipad.brain.number.view.KeyboardDialog;
import com.unipad.common.Constant;
import com.unipad.utils.ToastUtil;

/**
 * 听记数字界面
 */
public class ListenToWriteNumFragment extends NumberRightFragment{
    private KeyboardDialog mKeyboardDialog;
    private View mMemoryLayout;
    private TextView musicName;

    @Override
    public boolean isNeedShowCurrent() {
        return true;
    }

    @Override
    public String getCompeteItem() {
        return Constant.getProjectName(mActivity.getProjectId());
    }

    @Override
    public void initAnswerView() {
        mKeyboardDialog = new KeyboardDialog(mActivity);
        mKeyboardDialog.setKeyboardClickListener(this);
        mMemoryLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewStub mStubListen = (ViewStub) mViewParent.findViewById(R.id.view_listen);
            mMemoryLayout = mStubListen.inflate();
        musicName = (TextView) mMemoryLayout.findViewById(R.id.music_name);
    }

    @Override
    public void initMemoryView() {
        final AnimationDrawable animationDrawable = (AnimationDrawable) mMemoryLayout.findViewById(R.id.play_music).getBackground();
        mMemoryLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                animationDrawable.start();
                return true;
            }
        });

    }

    @Override
    public void startMemory() {
        super.startMemory();
        mMemoryLayout.setVisibility(View.VISIBLE);
        String str = "第一轮";
        if(service.round == 1){
            str = "第一轮";
        }else if(service.round == 2){
            str = "第二轮";
        }else if(service.round == 3){
            str = "第三轮";
        }
        musicName.setText(str);
    }

    @Override
    public void startRememory() {
        super.startRememory();
        if (mKeyboardDialog != null) {
            mKeyboardDialog.show();
        }
    }

    @Override
    public void initDataFinished() {
        super.initDataFinished();
        if (mKeyboardDialog != null) {
            mKeyboardDialog.dismiss();
        }
    }

    @Override
    public void rememoryTimeToEnd(int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        getAnswer();
        mKeyboardDialog.dismiss();
        if(isMatchMode()){
            if (service.isLastRound()){
                ToastUtil.showToast("本场比赛结束，退出比赛");
                mActivity.finish();
            }else {
                ToastUtil.createTipDialog(mActivity, Constant.SHOW_GAME_PAUSE, "开始准备下一轮",R.drawable.wait_next_turn).show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        service.parseDataByNextRound();
                    }
                }.start();
            }
        }

    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
        //第一轮的时候，会自动发一个消息给管控端，告诉管控端已结束记忆，准备开始答题
        //进度需要初始化。
        if (service.round != 1){
            progress = 100;
            sendMsgToPreper();
        }
    }

}
