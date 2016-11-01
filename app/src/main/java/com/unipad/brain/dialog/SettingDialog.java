package com.unipad.brain.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.unipad.brain.R;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.utils.SharepreferenceUtils;
import com.unipad.utils.ToastUtil;


/**
 * Created by gongkan on 2016/10/11.
 */
public abstract class SettingDialog extends BaseDialog{
    private String projectId;
    private Context mContext;
    private ViewGroup contentView;
    private Handler handler;
    private static final int MSG_LINE_FOR_NUM = 1;
    private static final int MSG_GROUP_GOR_LONGPOKER = 2;
    private static final int MSG_DISSMISS_WAITING_DLG = 3;
    private boolean isNeedUpdateView;
    public SettingDialog(final Context context, final String projectId,boolean isMatch) {
        super(context);
        this.mContext = context;
        this.projectId = projectId;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.setting_in_game, null);
        setContentView(contentView);
        ImageView closeButton = (ImageView) contentView.findViewById(R.id.img_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        handler = new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);

                switch (msg.what){
                    case  MSG_LINE_FOR_NUM:
                        int next = (int) msg.obj;
                        updateOtherView(SharepreferenceUtils.getInt(projectId + "_linemode", 0), next);
                        SharepreferenceUtils.writeint(projectId + "_linemode", next);
                        handler.sendEmptyMessage(MSG_DISSMISS_WAITING_DLG);
                        break;
                    case  MSG_GROUP_GOR_LONGPOKER:
                        int nextPoker = (int) msg.obj;
                        updateOtherView(SharepreferenceUtils.getInt(projectId + "_dividemode", 0), nextPoker);
                        SharepreferenceUtils.writeint(projectId + "_dividemode", nextPoker);
                        handler.sendEmptyMessageDelayed(MSG_DISSMISS_WAITING_DLG,300);
                        break;
                    case MSG_DISSMISS_WAITING_DLG:
                        updateAfter();
                        HIDDialog.dismissDialog(Constant.CREATE_SETTING_WATI_DLG);
                        break;
                }
            }
        };
        initView();

    }

    @Override
    protected int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initView(){
        if (projectId.equals(Constant.GAME_BINARY_NUM) ||
               projectId.equals(Constant.GAME_LONG_NUM) ||
               projectId.equals(Constant.GAME_RANDOM_NUM)) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.line_setting, null);

            RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group_competition_mode);
            final int lineMode = SharepreferenceUtils.getInt(projectId + "_linemode", 0);
            switch (lineMode){
                case 0:
                    mRadioGroup.check(R.id.btn_default_mode_0);
                    break;
                case 2:
                    mRadioGroup.check(R.id.btn_default_mode);
                    break;
                case 3:
                    mRadioGroup.check(R.id.btn_default_mode_3);
                    break;
                case 4:
                    mRadioGroup.check(R.id.btn_default_mode_4);
                    break;
            }

            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Message msg = Message.obtain();
                    msg.what = MSG_LINE_FOR_NUM;
                    switch (group.getCheckedRadioButtonId()) {
                        case R.id.btn_default_mode_0:
                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 0;
                                handler.sendMessage(msg);

                            }else{
                                SharepreferenceUtils.writeint(projectId + "_linemode", 0);
                            }
                            dismiss();
                            break;
                        case R.id.btn_default_mode:
                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 2;
                                handler.sendMessage(msg);

                            }else{
                                SharepreferenceUtils.writeint(projectId + "_linemode", 2);
                            }
                            dismiss();
                            break;
                        case R.id.btn_default_mode_3:
                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 3;
                                handler.sendMessage(msg);
                            }else{
                                SharepreferenceUtils.writeint(projectId + "_linemode", 3);
                            }
                            dismiss();
                            break;
                        case R.id.btn_default_mode_4:
                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 4;
                                handler.sendMessage(msg);
                            }else{
                                SharepreferenceUtils.writeint(projectId + "_linemode", 4);
                            }
                            dismiss();
                            break;
                    }
                }
            });
            contentView.addView(view);
        }

        if(projectId.equals(Constant.GAME_LONG_POCKER)){
            View view = LayoutInflater.from(mContext).inflate(R.layout.line_setting, null);
            RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group_competition_mode);
            ((TextView) view.findViewById(R.id.txt_title_competition)).setText(mContext.getResources().getString(R.string.long_poker_remember_way));
             view.findViewById(R.id.btn_default_mode).setVisibility(View.GONE);
            ((RadioButton) view.findViewById(R.id.btn_default_mode_3)).setText("3");
            ((RadioButton) view.findViewById(R.id.btn_default_mode_4)).setText("5");

            final int lineMode = SharepreferenceUtils.getInt(projectId + "_dividemode", 0);
            switch (lineMode){
                case 0:
                    mRadioGroup.check(R.id.btn_default_mode_0);
                    break;
                case 3:
                    mRadioGroup.check(R.id.btn_default_mode_3);

                    break;
                case 5:
                    mRadioGroup.check(R.id.btn_default_mode_4);
                    break;
            }

            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Message msg = Message.obtain();
                    msg.what = MSG_GROUP_GOR_LONGPOKER;
                    switch (group.getCheckedRadioButtonId()) {
                        case R.id.btn_default_mode_0:
                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 0;
                                handler.sendMessage(msg);

                            }else{
                                SharepreferenceUtils.writeint(projectId + "_dividemode", 0);
                            }
                            dismiss();
                            break;
                        case R.id.btn_default_mode_3:

                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 3;
                                handler.sendMessage(msg);

                            }else{
                                SharepreferenceUtils.writeint(projectId + "_dividemode", 3);
                            }
                            dismiss();
                            break;
                        case R.id.btn_default_mode_4:
                            if (isNeedUpdateView) {
                                updateBefore();
                                ToastUtil.createWaitingDlg(mContext, "loading", Constant.CREATE_SETTING_WATI_DLG).show();
                                msg.obj = 5;
                                handler.sendMessage(msg);

                            }else{
                                SharepreferenceUtils.writeint(projectId + "_dividemode", 5);
                            }
                            dismiss();
                            break;
                    }
                }
            });
            contentView.addView(view);
        }

    }
    public abstract void updateBefore();
    public abstract void updateOtherView(int preSet,int nextSet);
    public abstract void updateAfter();

    public void setIsNeedUpdateView(boolean isNeedUpdateView) {
        this.isNeedUpdateView = isNeedUpdateView;
    }
}
