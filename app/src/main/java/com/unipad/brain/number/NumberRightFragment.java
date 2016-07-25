package com.unipad.brain.number;

import android.app.Service;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.number.dao.NumService;
import com.unipad.brain.number.view.KeyboardDialog;
import com.unipad.brain.number.view.NumberMemoryLayout;
import com.unipad.brain.number.view.NumberRememoryLayout;
import com.unipad.common.BasicCommonFragment;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.io.mina.SocketThreadManager;
import com.unipad.utils.LogUtil;
import com.unipad.utils.ToastUtil;

public abstract class NumberRightFragment extends BasicCommonFragment implements KeyboardDialog.KeyboardClickListener {
    private static final String TAG = NumberRightFragment.class.getSimpleName();

    /**
     * 回忆界面父布局
     */
    private RelativeLayout mRememoryLayout;
    /**
     * 包裹数字回忆界面布局的ScrollView布局
     */
    protected ScrollView mScrollAnswerView;
    /**
     * 数字回忆界面布局
     */
    protected NumberRememoryLayout mNumberRememoryLayout;
    /**
     * 界面底部布局
     */
    protected ViewGroup mLayoutBottom;
    /**
     * 遮罩层
     */
    private ViewStub mStubShade;
    /**
     * 记录mScrollAnswerView滑动了多少次
     */
    private int mScrollCount = 1;
    private int mRows;
    private int mLines;
    /**
     * 数字总数
     */
    private int mTotalNumbers;
    private SparseArray<String> mNumberArray = new SparseArray<>();

    private String mCompeteItem = "";
    protected NumService service;
    private FrameLayout frameLayout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = (NumService) mActivity.getService();
        mCompeteItem = Constant.getProjectName(mActivity.getProjectId());
        mRememoryLayout = (RelativeLayout) mViewParent.findViewById(R.id.answer_layout);
        frameLayout = (FrameLayout) mViewParent.findViewById(R.id.binary_rememory_layout);
        mLayoutBottom = (ViewGroup) mRememoryLayout.findViewById(R.id.bottom_layout);
        mStubShade = (ViewStub) mViewParent.findViewById(R.id.view_shade);
    }

    @Override
    public void initDataFinished() {
        if (null != service.lineNumbers && service.lineNumbers.size() != 0) {
            mLines = service.lineNumbers.size();
            mRows = service.lineNumbers.valueAt(0).length();
            for (int i = 0; i < mLines; i++) {
                mTotalNumbers += service.lineNumbers.valueAt(i).length();
            }
            frameLayout.removeAllViews();
            frameLayout.addView(new NumberMemoryLayout(mActivity, service.lineNumbers));
            mStubShade.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startMemory() {
        super.startMemory();
        mStubShade.setVisibility(View.GONE);
    }

    @Override
    public void pauseGame() {
        mStubShade.setVisibility(View.VISIBLE);

    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        mStubShade.setVisibility(View.GONE);

    }

    @Override
    public int getLayoutId() {
        return R.layout.number_frg_right;
    }


    /**
     * 开始答题
     */


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ibtn_binary_0:
            case R.id.ibtn_binary_1:
                String text = NumService.TEXT_ONE;
                if (R.id.ibtn_binary_0 == view.getId()) {
                    text = NumService.TEXT_ZERO;
                }
                this.setGridText(text);
                break;
            case R.id.btn_vibrate:
                Vibrator vibrator = (Vibrator) mActivity.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);//设备震动
                Animation shakeAnim = AnimationUtils.loadAnimation(mActivity, R.anim.shake);
                mViewParent.startAnimation(shakeAnim); //控件震动的动画效果
                break;
            case R.id.btn_delete:
                this.deleteGirdText();
                break;
            case R.id.btn_go_top:
                mScrollAnswerView.smoothScrollTo(0, 0);
                break;
            case R.id.listen_keyboard_0:
            case R.id.listen_keyboard_1:
            case R.id.listen_keyboard_2:
            case R.id.listen_keyboard_3:
            case R.id.listen_keyboard_4:
            case R.id.listen_keyboard_5:
            case R.id.listen_keyboard_6:
            case R.id.listen_keyboard_7:
            case R.id.listen_keyboard_8:
            case R.id.listen_keyboard_9:
                progress = 100 +mCursorPosition*100/mTotalNumbers;
                LogUtil.e("","Quick num:"+progress);
                this.setGridText(mNumberArray.get(view.getId()));
                break;
            case R.id.listen_keyboard_delete:
                this.deleteGirdText();
                break;
            default:
                break;
        }
    }

    /**
     * 设置格子中的数字
     *
     * @param numberText 数字文字
     */
    private void setGridText(String numberText) {
        if (mCursorPosition >= mTotalNumbers) {
            //ToastUtil.showToast("您已填满所有空白");
            return;
        }

        mNumberRememoryLayout.setGridText(mCursorPosition++, numberText);

        int lineIndex = mCursorPosition / mRows;
        if ((lineIndex % 8 == 0) && lineIndex / 8 == mScrollCount) {// 每8行mScrollAnswerView向上移动一次
            mScrollAnswerView.smoothScrollTo(0,
                    (App.screenHeight / 2 - 35) * (mScrollCount++));
        }

        //快速随机、马拉松数字项目的回忆界面需要显示光标
        if (isNeedShowCurrent()) {
            mNumberRememoryLayout.setCursorPosition(mCursorPosition);
        }

    }

    public abstract boolean isNeedShowCurrent();

    /**
     * 删除格子中的数字
     */
    private void deleteGirdText() {
        --mCursorPosition;
        if (mCursorPosition < 0) {
            mCursorPosition = 0;
            return;
        }
        mNumberRememoryLayout.setGridText(mCursorPosition, "");

        //快速随机、马拉松数字项目的回忆界面需要显示光标
        if (isNeedShowCurrent()) {
            mNumberRememoryLayout.setCursorPosition(mCursorPosition);
        }
    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
        this.inAnswerMode();
    }

    public abstract String getCompeteItem();

    public abstract void initAnswerView();

    private NumberRememoryLayout createReMemoryLayout() {
        return new NumberRememoryLayout(mActivity, getCompeteItem(), mRows, mLines, mTotalNumbers, new IInitRememoryCallBack() {
            @Override
            public void begin() {
                ToastUtil.createWaitingDlg(mActivity,"加载答题卡中",Constant.INIT_REMEMORY_DLG).show();
            }

            @Override
            public void loading(int progress) {
                LogUtil.e("","loading :" + progress);
                        ((TextView) HIDDialog.getExistDialog(Constant.INIT_REMEMORY_DLG).findViewById(R.id.dialog_text)).setText("加载答题卡中:" + progress);
            }

            @Override
            public void finish() {
                LogUtil.e("","finish...");
                    HIDDialog.dismissDialog(Constant.INIT_REMEMORY_DLG);
                    sendMsgToPreper();
            }
        });
    }

    private void inAnswerMode() {
        // mViewParent.removeView(mMemoryLayout);// 先移除记忆界面
        // mStubShade.setVisibility(View.GONE);// 隐藏遮罩层
        // 再加载回忆界面
        mScrollAnswerView = (ScrollView) mRememoryLayout
                .findViewById(R.id.scroll_rememory_layout);
        frameLayout.removeAllViews();
        if (mNumberRememoryLayout == null){
            mNumberRememoryLayout = createReMemoryLayout();
        } else {
            mNumberRememoryLayout.clearText();
        }
        frameLayout.addView(mNumberRememoryLayout);
        initAnswerView();
        if (mCompeteItem.equals(getString(R.string.project_3))
                || mCompeteItem.equals(getString(R.string.project_5))) {
            LogUtil.e("--", "");

        } else if (mCompeteItem.equals(getString(R.string.project_2))) {

        } else if (mCompeteItem.equals(getString(R.string.project_9))) {
            View.inflate(mActivity, R.layout.listen_v_bottom, mLayoutBottom);
            mLayoutBottom.findViewById(R.id.listen_keyboard_0).setOnClickListener(this);
            mLayoutBottom.findViewById(R.id.listen_keyboard_delete).setOnClickListener(this);
            int number = 0;
            mNumberArray.put(R.id.listen_keyboard_0, (number++) + "");
            for (int id = R.id.listen_keyboard_1; id <= R.id.listen_keyboard_9; ++id) {
                mNumberArray.put(id, (number++) + "");
                mLayoutBottom.findViewById(id).setOnClickListener(this);
            }
        }
    }

    @Override
    public void rememoryTimeToEnd(final int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        //mStubShade.setVisibility(View.VISIBLE);
    }
    public void showAnswer(){
        mNumberRememoryLayout.showAnswer(service.lineNumbers, service.answer);
    }

    public void getAnswer(){
        mNumberRememoryLayout.getAnswer(service.answer);
    }

    @Override
    public void numberKey(String keyValue) {
        progress = 100 +mCursorPosition*100/mTotalNumbers;
        LogUtil.e("","Quick num:"+progress);
        this.setGridText(keyValue);
    }

    /**
     * 光标的位置，从0开始，0表示第一个，如果存在光标则默认在第一个显示
     */
    private int mCursorPosition = 0;

    @Override
    public void upKey() {
        int tempPosition = mCursorPosition;
        mCursorPosition -= mRows;
        if (mCursorPosition >= 0) {
            mNumberRememoryLayout.setCursorPosition(mCursorPosition);
        } else {
            mCursorPosition = tempPosition;
            ToastUtil.showToast(R.string.first_line);
        }
    }

    @Override
    public void downKey() {
        int tempPosition = mCursorPosition;
        mCursorPosition += mRows;
        if (mCursorPosition < mTotalNumbers) {
            mNumberRememoryLayout.setCursorPosition(mCursorPosition);
        } else {
            mCursorPosition = tempPosition;
            ToastUtil.showToast(R.string.last_line);
        }
    }

    @Override
    public void leftKey() {
        --mCursorPosition;
        if (mCursorPosition >= 0) {
            mNumberRememoryLayout.setCursorPosition(mCursorPosition);
        } else {
            mCursorPosition = 0;
        }
    }

    @Override
    public void rightKey() {
        ++mCursorPosition;
        if (mCursorPosition < mTotalNumbers) {
            mNumberRememoryLayout.setCursorPosition(mCursorPosition);
        } else {
            mCursorPosition = mTotalNumbers - 1;
        }
    }

    @Override
    public void deleteKey() {
        this.deleteGirdText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRememoryLayout) {
            mRememoryLayout.removeAllViews();
        }
    }

}
