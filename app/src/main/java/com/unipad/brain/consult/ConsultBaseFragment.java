package com.unipad.brain.consult;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unipad.brain.home.MainBasicFragment;
import com.unipad.common.AppGlobalManager;
import com.unipad.utils.LogUtil;

/**
 * Created by liuxiang on 2016/6/22.
        */
public abstract class ConsultBaseFragment extends MainBasicFragment{
    private String TAG;
    private Context mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.d(tag(), "onAttach");
        mActivity = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(tag(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(tag(), "onCreateView");
        View view = inflater.inflate(this.getLayoutId(), container, false);
        initView(view);
        initListener();
        initData();

        return view;
    }

    protected void initView(View view){

    }

    protected void initListener(){

    }

    protected void initData(){

    }

    public abstract int getLayoutId();

    @Override
    public final void onClick(View v) {
        if(AppGlobalManager.isFastDoubleClick(v.getId())){
            return;
        }
        handleClickEvent(v);
    }

    protected void handleClickEvent(View v){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       LogUtil.d(tag(), "onDestory");
    }

    @Override
    public void onDestroyView() {
        LogUtil.d(tag(), "onDestoryView");
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(tag(), "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(tag(), "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(tag(), "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(tag(), "onStop");
    }

    private String tag(){
        if(TAG == null){
            TAG = this.getClass().getSimpleName();
        }
        return TAG;
    }

    protected Context getmContext(){
    return mActivity;
}
}
