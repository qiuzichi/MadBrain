package com.unipad.brain.personal.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.UserDetailEntity;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.dialog.ShowDialog;
import com.unipad.brain.home.util.SharedPreferencesUtil;
import com.unipad.brain.main.MainActivity;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.common.Constant;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.MD5Utils;
import com.unipad.utils.ToastUtil;

/**
 * Created by LiuPeng on 2016/4/14.
 */
public class LoginActivity extends BasicActivity implements View.OnClickListener,IDataObserver,ShowDialog.OnShowDialogClick{

    private EditText userName;

    private EditText userPwd;

    private PersonCenterService service;

    //  自定义Dialog 对象
    private ShowDialog showDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_aty);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001){
            String User_name=data.getStringExtra("user_name");
            String Pwd=data.getStringExtra("user_pwd");
            userName.setText(User_name);
            userPwd.setText(Pwd);
            return;
        }else {
            if(requestCode == 1002 && resultCode == 1003) {
                String Modify_name = data.getStringExtra("modify_name");
                String Modify_pwd = data.getStringExtra("modify_pwd");
                userName.setText(Modify_name);
                userPwd.setText(Modify_pwd);
                return;
            }
        }
        }

    @Override
    public void initData() {
        showDialog = new ShowDialog(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.text_new_user).setOnClickListener(this);
        findViewById(R.id.text_forget_pwd).setOnClickListener(this);
        userName = (EditText) findViewById(R.id.edit_login_name);
        userPwd = (EditText) findViewById(R.id.edit_login_pwd);
        service = (PersonCenterService) AppContext.instance().getService(Constant.PERSONCENTER);
        service.registerObserver(HttpConstant.LOGIN_UPDATE_UI,this);
        service.registerObserver(HttpConstant.LOGIN_WRONG_MSG,this);
        if(Constant.isfirstRun(this,new SharedPreferencesUtil(this))){
            View view = View.inflate(this,R.layout.first_login_dialog,null);
            showDialog.showDialog(view,ShowDialog.TYPE_CENTER,getWindowManager(),0.4f,0.5f);
            showDialog.setOnShowDialogClick(this);
            showDialog.bindOnClickListener(view,new int[]{R.id.img_close});
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
               /** UserDetailEntity user = new UserDetailEntity();
                user.setUserName("test");
                AppContext.instance().loginUser = user;
                AppContext.instance().loginUser.setLoginPwd(MD5Utils.MD5_two(userPwd.getText().toString().trim()));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                 */
                this.login();
                break;
            case R.id.text_new_user:
                this.openRegisterActivity();
                break;
            case R.id.text_forget_pwd:
                this.openForgetPwdActivity();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.unRegisterObserve(HttpConstant.LOGIN_UPDATE_UI, this);
        service.unRegisterObserve(HttpConstant.LOGIN_WRONG_MSG,this);
        
    }

    private void login() {
        String name = userName.getText().toString().trim();
        String pwd = userPwd.getText().toString().trim();
        if ("".equals(name)) {
            ToastUtil.showToast("用户名不能为空");
            return;
        }
        if ("".equals(pwd)) {
            ToastUtil.showToast("密码不能为空");
            return;
        }
        ToastUtil.createWaitingDlg(this,null,Constant.LOGIN_WAIT_DLG).show(15);
        HIDDialog waitDialog = HIDDialog.getExistDialog(Constant.LOGIN_WAIT_DLG);
        if (waitDialog != null) {
            ((TextView) waitDialog.findViewById(R.id.dialog_text)).setVisibility(View.GONE);
        }
        service.loginIn(userName.getText().toString().trim(), userPwd.getText().toString().trim());
    }

    private void openForgetPwdActivity() {
        Intent intent = new Intent(this, ForgetPwdActivity.class);
        this.startActivityForResult(intent,1002);
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        this.startActivityForResult(intent, 1000);
    }

    @Override
    public void update(int key, Object o) {
        Log.e("", "Login update UI");
        switch (key) {
            case HttpConstant.LOGIN_UPDATE_UI:
                HIDDialog.dismissAll();

                AppContext.instance().loginUser.setLoginPwd(MD5Utils.MD5_two(userPwd.getText().toString().trim()));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case HttpConstant.LOGIN_WRONG_MSG:
                HIDDialog.dismissAll();
                userName.setText((String)o);
                break;
            default:
                break;
        }
    }

    @Override
    public void dialogClick(int id) {
        if(id != 0 && showDialog != null){
            showDialog.dismiss();
        }
    }
}
