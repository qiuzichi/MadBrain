package com.unipad.brain.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.unipad.AppContext;
import com.unipad.UserDetailEntity;
import com.unipad.brain.R;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.brain.personal.view.ChatFunctionView;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.FileUtil;
import com.unipad.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 个人中心之个人资料
 * Created by Wbj on 2016/4/26.
 */
public class PersonalInfoFragment extends PersonalCommonFragment implements IDataObserver {

    private PersonCenterService service;

    private UserDetailEntity userDetailEntity = AppContext.instance().loginUser;

    private UserDetailEntity detailEntity; // 用于提交更新之后的用户信息
    private EditText ed_mail;
    private EditText ed_phone;
    private EditText ed_nationality;
    private EditText ed_scho;
    private EditText ed_addr;
    private EditText ed_name;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.findViewById(R.id.personal_info_btn_save).setOnClickListener(this);
        mTitleBarRightText = mActivity.getString(R.string.save);
        service = (PersonCenterService) AppContext.instance().getService(Constant.PERSONCENTER);
        service.registerObserver(HttpConstant.PERSONALDATA,this);
        ed_mail = (EditText)mActivity.findViewById(R.id.ed_mail);
        ed_addr = (EditText)mActivity.findViewById(R.id.ed_addr);
        ed_name = (EditText)mActivity.findViewById(R.id.ed_name);
        ed_nationality = (EditText)mActivity.findViewById(R.id.ed_nationality);
        ed_phone = (EditText)mActivity.findViewById(R.id.ed_phone);
        ed_scho = (EditText)mActivity.findViewById(R.id.ed_scho);
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        thisShowView = 1;
    }


    /*
          初始化数据
         */
    private void initData() {
        if(null == userDetailEntity)
            return;
        ed_mail.setText(userDetailEntity.getMail());
        ed_addr.setText(userDetailEntity.getAddr());
        ed_name.setText(userDetailEntity.getUserName());
        ed_nationality.setText(userDetailEntity.getCountry());
        ed_phone.setText(userDetailEntity.getTel());
        ed_scho.setText(userDetailEntity.getSchool());
    }



    @Override
    public int getLayoutId() {
        return R.layout.personal_frg_info;
    }

    @Override
    public void clickTitleBarRightText() {
        this.saveInfoToServer();
    }

    @Override
    public String getTitleBarRightText() {
        return mTitleBarRightText;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_info_btn_save:
                detailEntity = new UserDetailEntity();
                detailEntity.setPhoto(AppContext.instance().loginUser.getPhoto());
                detailEntity.setAddr(ed_addr.getText().toString().trim());
                detailEntity.setMail(ed_mail.getText().toString().trim());
                detailEntity.setSchool(ed_scho.getText().toString().trim());
                detailEntity.setUserName(ed_name.getText().toString().trim());
                detailEntity.setCountry(ed_nationality.getText().toString().trim());
                detailEntity.setTel(ed_phone.getText().toString().trim());
                this.saveInfoToServer();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        service.unregistDataChangeListenerObj(this);
        super.onDestroyView();
    }

    @Override
    protected void saveInfoToServer() {
        super.saveInfoToServer();
        ToastUtil.createWaitingDlg(mActivity,null,Constant.LOGIN_WAIT_DLG).show(15);
        service.saveUserInfo(detailEntity);
    }

    @Override
    public void update(int key, Object o) {
        switch (key){
            case HttpConstant.PERSONALDATA:
                HIDDialog.dismissAll();
                try {
                    JSONObject jsonObject = new JSONObject((String)o);
                    int ret_code = jsonObject.optInt("ret_code");
                    if(ret_code == 0){
                        setNameText(detailEntity.getUserName());
                        mActivity.setTxtName();
                        AppContext.instance().loginUser.setTel(detailEntity.getTel());
                        AppContext.instance().loginUser.setCountry(detailEntity.getCountry());
                        AppContext.instance().loginUser.setUserName(detailEntity.getUserName());
                        AppContext.instance().loginUser.setSchool(detailEntity.getSchool());
                        AppContext.instance().loginUser.setMail(detailEntity.getMail());
                        AppContext.instance().loginUser.setAddr(detailEntity.getAddr());
                        ToastUtil.showToast(mActivity.getString(R.string.updata_userinfo));
                    } else {
                        ToastUtil.showToast((String)o);
                    }
                } catch (JSONException e) {
                    ToastUtil.showToast(mActivity.getString(R.string.string_json_error));
                    e.printStackTrace();
                }
                break;
        };
    }
}
