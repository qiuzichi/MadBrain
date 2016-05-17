package com.unipad.brain.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.main.MainActivity;

public class RegisterActivity extends BasicActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_aty);
    }

    @Override
    public void initData() {
        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                this.openMainActivity();
                break;
            default:
                break;
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
