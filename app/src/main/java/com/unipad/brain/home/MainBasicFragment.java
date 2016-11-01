package com.unipad.brain.home;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unipad.brain.R;
import com.unipad.brain.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class MainBasicFragment extends Fragment implements View.OnClickListener {
    protected Activity mActivity;
    //网络请求的超时时间 12秒
    protected final int DELAYETIMEOUT = 12000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(this.getLayoutId(), container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity =  getActivity();

    }

    public abstract int getLayoutId();


    //回调数据到搜索栏 以后肯定删掉
    public List<String> getNewsDatas() {
        return null;
    }

}
