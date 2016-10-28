package com.unipad.common;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.IOperateGame;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.R;
import com.unipad.brain.dialog.SettingDialog;
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
import com.unipad.utils.SharepreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.Map;

/**
 * Created by Wbj on 2016/4/7.
 */
public class PractiseCommLeftFragment extends Fragment implements View.OnClickListener, CountDownTime.TimeListener, IOperateGame {
    private static final int[] COLORS = {R.color.bg_one, R.color.bg_two, R.color.bg_three};
    private PractiseGameActivity mActivity;
    private ViewGroup mParentLayout;
    private TextView mTextName, mTextAgeAds, mTextTime, mTextCompeteProcess;
    private Button mBtnCompeteMode;
    private CountDownTime mCountDownTime;
    private ImageView mIconImageView;
    /**
     * 是否处于回忆模式，只有两种模式且先记忆再回忆；默认为false，即处于记忆模式；
     */
    private boolean isRememoryStatus;
    private CommonFragment.ICommunicate mICommunicate;
    private SparseArray mColorArray = new SparseArray();
    private int memoryTime;
    private int reMemoryTime;
    private SettingDialog settingDialog;
    private Button settingButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentLayout = (ViewGroup) inflater.inflate(R.layout.common_frg_left, container, false);
        return mParentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.e("CommonFragment", "----onActivityCreated--");
        mActivity = (PractiseGameActivity) getActivity();

        mTextName = (TextView) mParentLayout.findViewById(R.id.user_name);
        mTextAgeAds = (TextView) mParentLayout.findViewById(R.id.user_age_ads);
        mTextTime = (TextView) mParentLayout.findViewById(R.id.text_time);
        mTextCompeteProcess = (TextView) mParentLayout.findViewById(R.id.memory_stats);
        mBtnCompeteMode = (Button) mParentLayout.findViewById(R.id.btn_compete_process);
        mBtnCompeteMode.setOnClickListener(this);
        mParentLayout.findViewById(R.id.text_exit).setOnClickListener(this);

        mTextAgeAds.setSelected(true);
        mTextAgeAds.setText((getString(R.string.person_level) + AppContext.instance().loginUser.getLevel()));
        ((TextView) mParentLayout.findViewById(R.id.txt_user_group_match)).setText("" + DateUtil.getMatchGroud(mActivity));
        mTextName.setSelected(true);
        this.getBgColorArray(mParentLayout);

        mCountDownTime = new CountDownTime(0, false);
        mCountDownTime.setTimeListener(this);
        mTextTime.setText(mCountDownTime.getTimeString());
        mTextName.setText(AppContext.instance().loginUser.getUserName());
        mIconImageView = (ImageView) mParentLayout.findViewById(R.id.user_photo);
        ImageOptions imageOptions =new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                .setRadius(DensityUtil.dip2px(360))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        // .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
                        // .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
                .build();
        if (!TextUtils.isEmpty(AppContext.instance().loginUser.getPhoto())) {
            x.image().bind(mIconImageView, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto(), imageOptions);
        } else {
            mIconImageView.setImageResource(R.drawable.set_headportrait);
        }

        //if (CompeteItemEntity.getInstance().getCompeteItem().equals(getString(R.string.project_9))) {
        //  mTextCompeteProcess.setText(R.string.playing_voice);
        //}

        memoryTime = (int) SharepreferenceUtils.readLong(
                mActivity.getProjectId() + "_memoryTime",
                300);
        reMemoryTime = (int) SharepreferenceUtils.readLong(
                mActivity.getProjectId() + "_re_memoryTime",
                300);
        String projectId = mActivity.getProjectId();
        settingButton = (Button)((ViewStub)mParentLayout.findViewById(R.id.button_viewstub)).inflate();
        if (projectId.equals(Constant.GAME_BINARY_NUM) ||
                projectId.equals(Constant.GAME_LONG_NUM) ||
                projectId.equals(Constant.GAME_RANDOM_NUM) ||
                projectId.equals(Constant.GAME_LONG_POCKER)){
            settingButton.setVisibility(View.VISIBLE);
            settingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (settingDialog == null){
                        settingDialog = new SettingDialog(mActivity, mActivity.getProjectId(), false) {
                            @Override
                            public void updateOtherView(int pre,int next) {
                                if (mICommunicate != null){
                                    mICommunicate.updateView(pre,next);
                                }
                            }

                            @Override
                            public void updateBefore() {
                                if (mICommunicate != null){
                                    mICommunicate.updatePreper();
                                }
                            }

                            @Override
                            public void updateAfter() {
                                if (mICommunicate != null){
                                    mICommunicate.updateAfter();
                                }
                            }
                        };
                    }
                    if (mActivity.getService().state == AbsBaseGameService.GO_IN_MATCH_START_MEMORY){
                        settingDialog.setIsNeedUpdateView(true);
                    }else{
                        settingDialog.setIsNeedUpdateView(false);
                    }
                    settingDialog.show();
                }
            });
        }else{
            settingButton.setVisibility(View.INVISIBLE);
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
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                settingButton.setVisibility(View.INVISIBLE);
                mICommunicate.memoryTimeToEnd(memoryTime);
            }

        } else {//回忆模式下才可以提交答案

            this.commitAnswer(takeTIme);
        }
    }

    private void reset() {
        isRememoryStatus = false;
        mBtnCompeteMode.setText(R.string.end_memory);
        mBtnCompeteMode.setEnabled(true);
        mTextCompeteProcess.setText(R.string.memorying);
        mTextTime.setText(mCountDownTime.setNewSeconds(memoryTime, false));

    }

    public void startRememoryTimeCount() {
        mTextTime.setText(mCountDownTime.setNewSeconds(reMemoryTime, false));
    }

    /**
     * 提交答案
     */
    private void commitAnswer(final int rememoryTime) {
        mBtnCompeteMode.setEnabled(false);
        if (mICommunicate != null) {
            mActivity.getService().state = AbsBaseGameService.GO_IN_MATCH_END_RE_MEMORY;
            mICommunicate.rememoryTimeToEnd(rememoryTime);
            final HIDDialog WaitingDialog = new HIDDialog(mActivity, R.style.dialog_wait, Constant.MATCH_RESULT_DLG);
            WaitingDialog.setContentView(R.layout.ui_waiting_confirm);

            // 必须放在设置 view之后,自定义的view是不能设置该方法的，注释掉
            final TextView contentView = (TextView) WaitingDialog.findViewById(R.id.dialog_text);
            Button confirmButton = (Button) WaitingDialog.findViewById(R.id.confirm_btn);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WaitingDialog.dismiss();
                    //mActivity.finish();
                    ;
                }
            });
            contentView.setText(mActivity.getResources().getString(R.string.commit_score));

            Window win = WaitingDialog.getWindow();
            WindowManager.LayoutParams lp = win.getAttributes();
            win.setGravity(Gravity.CENTER_VERTICAL);

            win.setAttributes(lp);
            WaitingDialog.show();
            mActivity.sendMsgGetSocre(memoryTime, rememoryTime, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            LogUtil.e("",Constant.getProjectName(mActivity.getProjectId())+result);
                            String content = "";

                            JSONObject jsObj = null;
                            try {
                                jsObj = new JSONObject(result);

                                if (jsObj != null && jsObj.toString().length() != 0) {
                                    if (jsObj.optInt("ret_code", -1) == 0) {
                                        content = mActivity.getResources().getString(R.string.memory_score, memoryTime, rememoryTime, jsObj.getString("data"));
                                    }else{
                                        content = jsObj.getString("ret_msg");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            contentView.setText(content);
                            WaitingDialog.findViewById(R.id.wait_progress).setVisibility(View.GONE);
                        }


                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            new Exception(ex).printStackTrace();
                            WaitingDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            WaitingDialog.dismiss();
                        }

                        @Override
                        public void onFinished() {

                        }
                    }

            );
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

    public void setICommunicate(CommonFragment.ICommunicate iCommunicate) {
        mICommunicate = iCommunicate;
    }


    @Override
    public void initDataFinished() {
        reset();
    }

    @Override
    public void downloadingQuestion(Map<String, String> data) {

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

    }

    @Override
    public void reStartGame() {

    }

    @Override
    public void finishGame() {

    }
}
