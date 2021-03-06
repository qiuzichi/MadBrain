package com.unipad.common;

import android.os.Bundle;
import android.view.KeyEvent;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.brain.BasicActivity;
import com.unipad.utils.LogUtil;

/**
 * Created by gongkan on 2016/8/4.
 */
public abstract class AbsMatchActivity extends BasicActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public abstract AbsBaseGameService getService();


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.e("","keycode = "+keyCode);
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public abstract String getProjectId();
}
