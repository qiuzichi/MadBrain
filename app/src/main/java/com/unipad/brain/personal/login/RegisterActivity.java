package com.unipad.brain.personal.login;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.unipad.AppContext;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.dialog.ShowDialog;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.brain.view.WheelMainView;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HitopRegist;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.MD5Utils;
import com.unipad.utils.ToastUtil;

public class RegisterActivity extends BasicActivity implements View.OnClickListener ,
        WheelMainView.OnChangingListener,IDataObserver {
    private EditText registName;
    private View mTextSelectedSex;
    private TextView radio_men;
    private TextView radio_women;
    private TextView registBirthday;
    private Spinner registNation;
    private EditText registTel;
    private EditText registPwd;
    private WheelMainView wheelMainView;
    private ShowDialog showDialog;
    private Button register;
    private int intSex;
    private PersonCenterService service;
    private String name;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_aty);
    }

    @Override
    public void initData() {
        registNation = (Spinner) findViewById(R.id.register_nation);
        (radio_men = (TextView) findViewById(R.id.radio_men)).setOnClickListener(this);
        (radio_women = (TextView) findViewById(R.id.radio_women)).setOnClickListener(this);
        (register = (Button) findViewById(R.id.btn_register)).setOnClickListener(this);
        (registBirthday = (TextView) findViewById(R.id.register_day)).setOnClickListener(this);
        (registName = (EditText) findViewById(R.id.register_name)).addTextChangedListener(mTextWatcher);
        (registPwd = (EditText) findViewById(R.id.register_pwd)).addTextChangedListener(mTextWatcher);
        registTel = (EditText) findViewById(R.id.register_phone);

        showDialog = new ShowDialog(this);
        if (showDialog.getDialog() != null) {
            showDialog.getDialog().setCanceledOnTouchOutside(true);
        }
        service = (PersonCenterService) AppContext.instance().getService(Constant.PERSONCENTER);
        service.registerObserver(HttpConstant.REGIST_FILED, this);
        service.registerObserver(HttpConstant.REGIST_OK, this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                regist();
                break;
            case R.id.register_day:
                wheelMainView = new WheelMainView(this);
                wheelMainView.setChangingListener(this);
                showDialog.showDialog(wheelMainView, ShowDialog.
                        TYPE_CENTER, getWindowManager(), 0.3f, 0.6f);
                break;
            case R.id.radio_men:
            case R.id.radio_women:
                if (mTextSelectedSex != null) {
                    mTextSelectedSex.setSelected(false);
                }
                v.setSelected(true);
                mTextSelectedSex = v;
                if (v.getId() == R.id.radio_men) {
                    intSex = 0;
                } else {
                    intSex = 1;
                }
                break;
            default:
                break;
        }
    }


    private void regist() {
        name = registName.getText().toString().trim();
        pwd = registPwd.getText().toString().trim();
        String sex = String.valueOf(intSex).trim();
        String nation = registNation.getSelectedItem().toString().trim();
        String birthday = registBirthday.getText().toString().trim();
        String tel = registTel.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(sex)) {
            ToastUtil.showToast("性别不能为空");
            return;
        }
        if (TextUtils.isEmpty(birthday)) {
            ToastUtil.showToast("出身日期不能为空");
            return;
        }
        if (TextUtils.isEmpty(nation)) {
            ToastUtil.showToast("国籍不能为空");
            return;
        }
        if (TextUtils.isEmpty(tel)) {
            ToastUtil.showToast("联系方式不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showToast("密码不能为空");
            return;
        }
        HitopRegist httpRegist = new HitopRegist();
        httpRegist.buildRequestParams("user_name", name);
        httpRegist.buildRequestParams("user_country", nation);
        httpRegist.buildRequestParams("user_sex", sex);
        httpRegist.buildRequestParams("user_contact", tel);
        httpRegist.buildRequestParams("user_born", birthday);
        httpRegist.buildRequestParams("user_password", MD5Utils.MD5_two(pwd));
        httpRegist.post();
        ToastUtil.createWaitingDlg(RegisterActivity.this,null,Constant.LOGIN_WAIT_DLG).show(15);
    }

    @Override
    public void onChanging(String changStr) {
        registBirthday.setText(changStr);
    }

    @Override
    protected void onDestroy() {
        service.unregistDataChangeListenerObj(this);
        super.onDestroy();
    }

    @Override
    public void update(int key, Object o) {
        HIDDialog.dismissAll();
        switch (key) {
            case HttpConstant.REGIST_OK:
                Intent intent = new Intent();
                intent.putExtra("user_name", name);
                intent.putExtra("user_pwd", pwd);
                this.setResult(1001, intent);
                this.finish();
                break;
            case HttpConstant.REGIST_FILED:
            default:
                break;
        }

    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    }

