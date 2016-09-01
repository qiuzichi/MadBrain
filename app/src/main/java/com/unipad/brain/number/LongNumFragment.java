package com.unipad.brain.number;

import android.view.View;
import android.widget.ScrollView;

import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.number.view.KeyboardDialog;
import com.unipad.brain.number.view.NumberMemoryLayout;
import com.unipad.brain.number.view.NumberRememoryLayout;
import com.unipad.common.Constant;
import com.unipad.utils.LogUtil;
import com.unipad.utils.SharepreferenceUtils;

/**
 * Created by gongkan on 2016/7/4.
 */
public class LongNumFragment extends NumberRightFragment {
    private KeyboardDialog mKeyboardDialog;

    @Override
    public void pauseGame() {
        super.pauseGame();
        if (mKeyboardDialog != null && mKeyboardDialog.isShowing()) {
            mKeyboardDialog.dismiss();
        }
    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        if (service.state == AbsBaseGameService.GO_IN_MATCH_START_RE_MEMORY){
            mKeyboardDialog.show();
        }else {
            if (mKeyboardDialog != null){
                mKeyboardDialog.dismiss();
            }
        }
    }

    @Override
    public boolean isNeedShowCurrent() {
        return true;
    }


    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
    }

    @Override
    public String getCompeteItem() {
        return Constant.getProjectName(mActivity.getProjectId());
    }

    @Override
    public void initAnswerView() {
        mKeyboardDialog = new KeyboardDialog(mActivity);
        mKeyboardDialog.setKeyboardClickListener(this);
    }

    @Override
    public void startMemory() {
        super.startMemory();
    }

    private void initDialog() {
        if (mKeyboardDialog == null) {
            mKeyboardDialog = new KeyboardDialog(mActivity);
            mKeyboardDialog.setKeyboardClickListener(this);
            mKeyboardDialog.show();
        } else if (!mKeyboardDialog.isShowing()) {
            mKeyboardDialog.show();
        }
    }

    @Override
    public void initMemoryView() {
        frameLayout.removeAllViews();
        frameLayout.addView(new NumberMemoryLayout(getActivity(), service.lineNumbers, SharepreferenceUtils.getInt(mActivity.getProjectId() + "_linemode", 0)));
    }

    @Override
    public void startRememory() {
        super.startRememory();
        initDialog();
    }

    @Override
    public void rememoryTimeToEnd(int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        if (mKeyboardDialog != null && mKeyboardDialog.isShowing()) {
            mKeyboardDialog.dismiss();
        }
        if (isMatchMode()) {
            getAnswer();
        } else {
            showAnswer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mKeyboardDialog != null) {
            mKeyboardDialog.setKeyboardClickListener(null);
            if (mKeyboardDialog.isShowing()) {
                mKeyboardDialog.dismiss();
            }
            mKeyboardDialog = null;
        }
    }
}
