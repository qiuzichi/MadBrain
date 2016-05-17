package com.unipad.brain.home.dialog;




import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.unipad.brain.R;


/**
 * 加载中Dialog
 * @author gongjb
 */
public class LoadingDialog extends AlertDialog {

    private TextView tips_loading_msg;

    private String message = null;

    public LoadingDialog(Context context) {
        super(context);
//        this.setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog(Context context, String message) {
        super(context);
        this.message = message;
        this.setCancelable(false);
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.message = message;
        this.setCancelable(false);
    }

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_tips_loading);
        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
        tips_loading_msg.setText(this.message);
    }

    public void setText(String message) {
        this.message = message;
        tips_loading_msg.setText(this.message);
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	//Xutils_HttpUtils.getHttpUtils(this.getContext()).getHttpClient().getConnectionManager().shutdown();
    	//Xutils_HttpUtils.setThisNull();
    }
    
}
