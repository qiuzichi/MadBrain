package com.unipad.brain.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.unipad.AppContext;
import com.unipad.AuthEntity;
import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.dialog.ShowDialog;
import com.unipad.brain.personal.bean.UploadFileBean;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.brain.view.WheelMainView;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.Identity;
import com.unipad.utils.PicUtil;
import com.unipad.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;

/**
 * 个人中心之实名认证
 * Created by Wbj on 2016/4/26.
 */
public class PersonalAuthenticationFragment extends PersonalCommonFragment implements IDataObserver, WheelMainView.OnChangingListener {
    private static final String PHOTO_POSTFIX = ".jpg".trim();
    private static final int MAX_STEP = 4;
    private View mTextSelectedSex;
    private View mTextSelectedRole;
    private RelativeLayout mLayoutStep123Parent;
    private ViewGroup mLayoutStep1, mLayoutStep2, mLayoutStep3, mLayoutStep3Up, mLayoutStep3Down;
    private ViewStub mViewStub;
    private ImageView mImageIdentityPositive, mImageIdentityNegative;
    private ImageView mImageCertificate1, mImageCertificate2;
    private TextView mLayoutStep3UpTitle, mLayoutStep3DownTitle;
    private Button mBtnNextStep;

    // 将用户所填信息 封装到AuthBean 中。
    private AuthEntity authBean;
    /**
     * 当前处于第几步，共四步，默认从第一步开始
     */
    private int mCurrentStep = 1;
    private SparseArray<String> mPhotoFileList = new SparseArray<>();
    private PersonCenterService service;

    private EditText ed_name;

    private EditText ed_id;

    private Button ed_data;
    // 上传图片
    private int indexUpLoadFile;
    // 自定义dialog
    private ShowDialog showDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitleBarRightText = mActivity.getString(R.string.next_step);

        mLayoutStep123Parent = (RelativeLayout) mActivity.findViewById(R.id.layout_step123_parent);
        // 这一部分  获取后台交互服务（个人中心）
        service = (PersonCenterService) AppContext.instance().getService(Constant.PERSONCENTER);
        // 实名认证
        service.registerObserver(HttpConstant.USER_AUTH, this);
        // 上传文件
        service.registerObserver(HttpConstant.UOLOAD_AUTH_FILE,this);
        // 显示实名认证信息
        service.registerObserver(HttpConstant.USER_AUTH_INFO, this);

        mLayoutStep1 = (ViewGroup) mLayoutStep123Parent.findViewById(R.id.layout_step1);
        View view = mLayoutStep1.findViewById(R.id.radio_men);
        view.setOnClickListener(this);
        view.setSelected(true);
        mTextSelectedSex = view;
        mLayoutStep1.findViewById(R.id.radio_women).setOnClickListener(this);
        view = mLayoutStep1.findViewById(R.id.radio_player);
        view.setOnClickListener(this);
        view.setSelected(true);
        mTextSelectedRole = view;
        mLayoutStep1.findViewById(R.id.radio_coach).setOnClickListener(this);
        mLayoutStep1.findViewById(R.id.radio_judge).setOnClickListener(this);

        mBtnNextStep = (Button) mActivity.findViewById(R.id.next_step);
        mBtnNextStep.setOnClickListener(this);

        mViewStub = (ViewStub) mActivity.findViewById(R.id.view_shade_step2);
        mLayoutStep2 = (ViewGroup) mViewStub.inflate();
        mLayoutStep2.setVisibility(View.GONE);

        ed_data = (Button)mLayoutStep2.findViewById(R.id.ed_data);
        showDialog = new ShowDialog(mActivity);
        ed_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WheelMainView wheelMainView = new WheelMainView(mActivity);
                wheelMainView.setChangingListener(PersonalAuthenticationFragment.this);
                showDialog.showDialog(wheelMainView,ShowDialog.TYPE_CENTER,mActivity.getWindowManager(),0.5f,0.6f);
            }
        });
        ed_id=(EditText) mLayoutStep2.findViewById(R.id.ed_id);
        ed_name=(EditText) mLayoutStep2.findViewById(R.id.ed_name);
        if(AppContext.instance().loginUser.getAuth() != 0){
            ToastUtil.createWaitingDlg(mActivity,null,Constant.LOGIN_WAIT_DLG).show(15);
            service.getAuthInfo(AppContext.instance().loginUser.getUserId());
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        thisShowView = 2;
    }

    @Override
    public int getLayoutId() {
        return R.layout.personal_frg_authentication;
    }

    @Override
    public void clickTitleBarRightText() {
        this.setNextStep();
    }

    @Override
    public String getTitleBarRightText() {
        return mTitleBarRightText;
    }

    private int intSex; // 性别

    private String intType="00001"; // 类别
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_men:
            case R.id.radio_women:
                if (mTextSelectedSex != null) {
                    mTextSelectedSex.setSelected(false);
                }
                v.setSelected(true);
                mTextSelectedSex = v;
                if(v.getId() == R.id.radio_men){
                    intSex = 0;
                } else {
                    intSex = 1;
                }
                break;
            case R.id.radio_player:
            case R.id.radio_coach:
            case R.id.radio_judge:
                if (mTextSelectedRole != null) {
                    mTextSelectedRole.setSelected(false);
                }
                v.setSelected(true);
                mTextSelectedRole = v;
                if(v.getId() == R.id.radio_player){
                    intType = "00001";
                } else if(v.getId()==R.id.radio_coach){
                    intType = "00003";
                }else{
                    intType = "00002";
                }
                break;
            case R.id.next_step:
                this.setNextStep();
                break;
            case R.id.grade_certificate_pic1:
            case R.id.grade_certificate_pic2:
            case R.id.grade_certificate_pic3:
            case R.id.grade_certificate_pic4:
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                File file = new File(App.getContext().getTakePhotoFile(), v.getId() + PHOTO_POSTFIX);
                if (file.exists()) {
                file.delete();
            }
                mPhotoFileList.put(v.getId(), file.getPath());

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));// 设置系统相机拍摄照片完成后图片文件的存放地址
                startActivityForResult(intent, v.getId());
                break;
            case R.id.authentication_confirm_sure_btn:
                // 确认提交
                this.getLayout4Date();
                break;
            default:
                break;
        }
    }

    /*
      提交信息
     */
   private void  submit(){
       if(this.isNotNull()){
           //  提交 实名认证信息
           service.userAuth(authBean);
       }
   };

    /*
     判断用户所填是否为空！！
     */
    private boolean isNotNull(){
        if(authBean == null)
            return false;
        if(TextUtils.isEmpty(authBean.getSex())){
            return false;
        }

        if(TextUtils.isEmpty(authBean.getType())){
            return false;
        }

        if(TextUtils.isEmpty(authBean.getName())){
            ToastUtil.showToast(mActivity.getString(R.string.string_name));
            return false;
        }

        if(TextUtils.isEmpty(authBean.getIdentity())){
            ToastUtil.showToast(mActivity.getString(R.string.string_identity));
            return false;
        }

        if(TextUtils.isEmpty(authBean.getBirthDate())){
            ToastUtil.showToast(mActivity.getString(R.string.string_birthdate));
            return  false;
        }

        if(TextUtils.isEmpty(authBean.getIdFrontUrl())){
            ToastUtil.showToast(mActivity.getString(R.string.string_front));
            return  false;
        }

        if (TextUtils.isEmpty(authBean.getIdReverseUrl())){
            ToastUtil.showToast(mActivity.getString(R.string.string_reverse));
            return false;
        }

        if(TextUtils.isEmpty(authBean.getRating_certificate2()) && TextUtils.isEmpty(authBean.getRating_certificate1())){
            ToastUtil.showToast(mActivity.getString(R.string.string_qc));
            return  false;
        }
        return true;
    }

    private  RelativeLayout layout4;
    /*
       封装步骤4 的所有内容。
     */
    private void getLayout4Date(){
        if(layout4 == null)
            return;

        String data = ed_data .getText().toString().trim();
        String ed_ids =  ed_id.getText().toString().trim();
        String ed_names = ed_name.getText().toString().trim();
        if(null == authBean)
            authBean = new AuthEntity();
        authBean.setBirthDate(data);
        authBean.setId(AppContext.instance().loginUser.getUserId());
        authBean.setIdentity(ed_ids);
        authBean.setSex(intSex + "");

        authBean.setName(ed_names);
        authBean.setType(intType);

        if(TextUtils.isEmpty(authBean.getName())){
            ToastUtil.showToast(mActivity.getString(R.string.string_name));
            return;
        }

        if(TextUtils.isEmpty(authBean.getIdentity())){
            ToastUtil.showToast(mActivity.getString(R.string.string_identity));
            return;
        }

        if(TextUtils.isEmpty(authBean.getBirthDate())){
            ToastUtil.showToast(mActivity.getString(R.string.string_birthdate));
            return;
        }

        if(!Identity.checkIDCard(authBean.getIdentity())){
            ToastUtil.showToast(mActivity.getString(R.string.check_id));
            return;
        }

        uploadFile();  // 上传文件
    }

    /*
      上传文件
     */
    public void uploadFile(){
        // 上传图片文件
        if(isFourPic()) {
            // 上传第一张图片
            ToastUtil.createWaitingDlg(mActivity,null,Constant.LOGIN_WAIT_DLG).show(50);
            indexUpLoadFile = 0;
            service.uploadAuthFile(mPhotoFileList.get(R.id.grade_certificate_pic1),0);
        } else {
            ToastUtil.showToast(mActivity.getString(R.string.string_ps));
        }
    }


    public boolean isFourPic(){
        if(mPhotoFileList == null)
            return false;
        int picIndex = 0;
        String path1 = mPhotoFileList.get(R.id.grade_certificate_pic1);
        String path2 = mPhotoFileList.get(R.id.grade_certificate_pic2);
        String path3 = mPhotoFileList.get(R.id.grade_certificate_pic3);
        String path4 = mPhotoFileList.get(R.id.grade_certificate_pic4);
        if(!TextUtils.isEmpty(path1))
            picIndex ++;
        if(!TextUtils.isEmpty(path2))
            picIndex ++;
        if(!TextUtils.isEmpty(path3))
            picIndex ++;
        if(!TextUtils.isEmpty(path4))
            picIndex ++;
        Log.d(this.getClass().getSimpleName(),"picIndex" + picIndex+"");
        if(picIndex == 4){
            return true;
        }
        return false;
    }


    /*
    下一步操作
     */
    private void setNextStep() {
        if ((++mCurrentStep) > MAX_STEP) {
            mCurrentStep = MAX_STEP;
            this.saveInfoToServer();
            return;
        }

        TextView textCurrent;
        switch (mCurrentStep) {
            case 2:
                mLayoutStep123Parent.removeView(mLayoutStep1);
                textCurrent = (TextView) mActivity.findViewById(R.id.text_step2);
                textCurrent.setSelected(true);
                textCurrent.setTextColor(Color.parseColor("#853EC6"));
                mActivity.findViewById(R.id.line_step2).setBackgroundResource(R.drawable.personal_authentication_step_line2);
                mLayoutStep2.setVisibility(View.VISIBLE);
                this.setNextBtnLayoutParams(R.id.layout_step2);
                break;
            case 3:
                mLayoutStep123Parent.removeView(mLayoutStep2);

                textCurrent = (TextView) mActivity.findViewById(R.id.text_step3);
                textCurrent.setSelected(true);
                textCurrent.setTextColor(Color.parseColor("#C020B4"));
                mActivity.findViewById(R.id.line_step3).setBackgroundResource(R.drawable.personal_authentication_step_line3);
                mViewStub = (ViewStub) mActivity.findViewById(R.id.view_shade_step3);
                mLayoutStep3 = (ViewGroup) mViewStub.inflate();
                this.setNextBtnLayoutParams(R.id.layout_step3);
                mImageIdentityPositive = (ImageView) mLayoutStep3.findViewById(R.id.grade_certificate_pic1);
                mImageIdentityPositive.setOnClickListener(this);
                mImageIdentityNegative = (ImageView) mLayoutStep3.findViewById(R.id.grade_certificate_pic2);
                mImageIdentityNegative.setOnClickListener(this);
                mImageCertificate1 = (ImageView) mLayoutStep3.findViewById(R.id.grade_certificate_pic3);
                mImageCertificate1.setOnClickListener(this);
                mImageCertificate2 = (ImageView) mLayoutStep3.findViewById(R.id.grade_certificate_pic4);
                mImageCertificate2.setOnClickListener(this);

                mLayoutStep3Up = (ViewGroup) mLayoutStep3.findViewById(R.id.layout_step3_up);
                mLayoutStep3UpTitle = (TextView) mLayoutStep3.findViewById(R.id.layout_step3_up_title);
                mLayoutStep3Down = (ViewGroup) mLayoutStep3.findViewById(R.id.layout_step3_down);
                mLayoutStep3DownTitle = (TextView) mLayoutStep3.findViewById(R.id.layout_step3_down_title);
                //把几个空格放在文字前面，目的是为了让“等级证书”与“身份证照片”右对齐，因为前者比后者少一个字
                mLayoutStep3DownTitle.setText(mActivity.getString(R.string.grade_certificate, "    "));
                if(AppContext.instance().loginUser.getAuth() != 0)  {
                   // x.image().bind(mImageIdentityPositive, HttpConstant.PATH_FILE_URL + authBean.getIdFrontUrl());
                   // x.image().bind(mImageIdentityNegative, HttpConstant.PATH_FILE_URL + authBean.getIdReverseUrl());
                   // x.image().bind(mImageCertificate1, HttpConstant.PATH_FILE_URL + authBean.getRating_certificate1());
                   // x.image().bind(mImageCertificate2, HttpConstant.PATH_FILE_URL + authBean.getRating_certificate2());
                }
                break;
            case 4:
                mLayoutStep3Up.removeView(mLayoutStep3UpTitle);
                mLayoutStep3.removeView(mLayoutStep3Up);
                mLayoutStep3Down.removeView(mLayoutStep3DownTitle);
                mLayoutStep3.removeView(mLayoutStep3Down);
                mLayoutStep123Parent.removeView(mLayoutStep3);

                mLayoutStep123Parent.removeView(mBtnNextStep);
                ((ViewGroup) mActivity.findViewById(R.id.authentication_frg_layout)).removeView(mLayoutStep123Parent);

                textCurrent = (TextView) mActivity.findViewById(R.id.text_step4);
                textCurrent.setSelected(true);
                textCurrent.setTextColor(Color.parseColor("#CB4A42"));

                mTitleBarRightText = mActivity.getString(R.string.confirm_commit);
                mActivity.setRightText(mTitleBarRightText);

                mViewStub = (ViewStub) mActivity.findViewById(R.id.view_shade_step4);
                this.buildLayoutStep4((RelativeLayout) mViewStub.inflate());
                break;
            default:
                break;
        }
    }


    /**
     * 构建步骤4的布局
     */
    private void buildLayoutStep4(RelativeLayout layout) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLayoutStep1.getLayoutParams();
        layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.divide_line_frg);
        layoutParams.setMargins(mActivity.getResources().getDimensionPixelOffset(R.dimen.binary_line_layout_padding),
                0, 0, mActivity.getResources().getDimensionPixelOffset(R.dimen.common_lf_margin));
        layout.addView(mLayoutStep1);

        layoutParams = (RelativeLayout.LayoutParams) mLayoutStep2.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.layout_step1);
        layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.layout_step1);
        layout.addView(mLayoutStep2);

        ViewGroup viewGroup = (ViewGroup) layout.findViewById(R.id.authentication_step4_ste3);
        viewGroup.addView(mLayoutStep3UpTitle);
        viewGroup.addView(mLayoutStep3Up);
        mLayoutStep3DownTitle.setText(mActivity.getString(R.string.grade_certificate, ""));
        viewGroup.addView(mLayoutStep3DownTitle);
        viewGroup.addView(mLayoutStep3Down);

        layout.findViewById(R.id.authentication_confirm_sure_btn).setOnClickListener(this);

        layout4 = layout;
    }


    /**
     * 根据认证步骤的变化，确保让“下一步”按钮位于认证项的父布局的下方
     *
     * @param stepLayoutId 认证项的父布局的id
     */
    private void setNextBtnLayoutParams(int stepLayoutId) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBtnNextStep.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, stepLayoutId);
        layoutParams.topMargin = mActivity.getResources().getDimensionPixelOffset(R.dimen.login_margin_top);
        mBtnNextStep.setLayoutParams(layoutParams);
    }

    @Override
    protected void saveInfoToServer() {
        super.saveInfoToServer();
        ToastUtil.showToast(R.string.confirm_commit);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case R.id.grade_certificate_pic1:
            case R.id.grade_certificate_pic2:
            case R.id.grade_certificate_pic3:
            case R.id.grade_certificate_pic4:
                this.setTakePhotoPicture(requestCode);
                break;
            default:
                break;
        }
    }

    private void setTakePhotoPicture(int viewId) {
        // 拍摄完成四张照片时 会出现OOM.      取缩略图
        Bitmap picMap =  PicUtil.getImageThumbnail(mPhotoFileList.get(viewId),76,76);
        if (picMap == null) {
            return;
        }
        switch (viewId) {
            case R.id.grade_certificate_pic1:
                mImageIdentityPositive.setImageBitmap(picMap);
                break;
            case R.id.grade_certificate_pic2:
                mImageIdentityNegative.setImageBitmap(picMap);
                break;
            case R.id.grade_certificate_pic3:
                mImageCertificate1.setImageBitmap(picMap);
                break;
            case R.id.grade_certificate_pic4:
                mImageCertificate2.setImageBitmap(picMap);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.unregistDataChangeListenerObj(this);
    }


    @Override
    public void update(int key, Object o) {
        // 本方法用于 UI更新
        switch(key){
            case HttpConstant.USER_AUTH_INFO:
                HIDDialog.dismissAll();
                if(o instanceof AuthEntity){
                    authBean = (AuthEntity)o;
                    onClick(authBean.getSex().equals("0") ? mLayoutStep1.findViewById(R.id.radio_men) : mLayoutStep1.findViewById(R.id.radio_women));
                    onClick("00001".equals(authBean.getType()) ? mLayoutStep1.findViewById(R.id.radio_player) : "00002".equals(authBean.getType()) ? mLayoutStep1.findViewById(R.id.radio_judge) : mLayoutStep1.findViewById(R.id.radio_coach));
                    ed_data.setText(AppContext.instance().loginUser.getBirthday());
                    ed_id.setText(authBean.getIdentity());
                    ed_name.setText(authBean.getName());
                } else {
                    ToastUtil.showToast((String)o);
                }
                break;
            case HttpConstant.USER_AUTH:
                // 实名认证
                HIDDialog.dismissAll();
                String str = (String) o;
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    int code = jsonObject.optInt("ret_code");
                    if(code == 0) {
                        ToastUtil.showToast(mActivity.getString(R.string.submit_sussue));
                        AppContext.instance().loginUser.setAuth(jsonObject.optInt("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case HttpConstant.UOLOAD_AUTH_FILE:
                // 上传文件
                indexUpLoadFile ++;
                UploadFileBean  uploadFileBean = (UploadFileBean)o;
                if(uploadFileBean.getRet_code() != 0){
                    HIDDialog.dismissAll();
                    ToastUtil.showToast(mActivity.getString(R.string.string_upload_fail));
                    return;
                }

                switch (indexUpLoadFile){
                    case 1:// {"data":"\\api\\20160613\\BE46D5F8AF834C6298C65E17C4009CD3.jpg","ret_code":"0"}
                        authBean.setIdFrontUrl(uploadFileBean.getPath());
                        service.uploadAuthFile(mPhotoFileList.get(R.id.grade_certificate_pic2),0);
                        break;
                    case 2:
                        authBean.setIdReverseUrl(uploadFileBean.getPath());
                        service.uploadAuthFile(mPhotoFileList.get(R.id.grade_certificate_pic3),0);
                        break;
                    case 3:
                        authBean.setRating_certificate1(uploadFileBean.getPath());
                        service.uploadAuthFile(mPhotoFileList.get(R.id.grade_certificate_pic4),0);
                        break;
                    case 4:
                        authBean.setRating_certificate2(uploadFileBean.getPath());
                        this.submit();
                        indexUpLoadFile = -1;
                        break;
                }
                break;
        }
    }

    @Override
    public void onChanging(String changStr) {
        ed_data.setText(changStr);
    }
}
