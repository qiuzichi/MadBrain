package com.unipad.brain.home;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.view.CompititionMainFragment;
import com.unipad.brain.consult.view.ConsultMainFragment;
import com.unipad.brain.consult.view.InfoListFragment;
import com.unipad.brain.dialog.BaseConfirmDialog;
import com.unipad.brain.dialog.ConfirmDialog;
import com.unipad.brain.dialog.ConfirmUpdateDialog;
import com.unipad.brain.home.bean.VersionBean;
import com.unipad.brain.home.dao.LoadService;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.brain.main.MainActivity;
import com.unipad.common.Constant;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.ToastUtil;

import java.util.Observer;

public class MainHomeFragment extends MainBasicFragment implements InfoListFragment.OnHomePageChangeListener{

    private FrameLayout mLayoutHome;
    private InfoListFragment mLeftFragment;
    private MainBasicFragment mRightFragment;
    private CompititionMainFragment mCompititionMainFragment;
    private ConsultMainFragment mConsultMainFragment;
//    private ConfirmUpdateDialog mConfirmDialog;
//    private NewsService service;
//    private VersionBean versionBean;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化界面;
        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mActivity= (MainActivity)getActivity();
        if(mActivity.getFragmentView() != null && !mActivity.getFragmentView().isHidden() )
            getTransaction().hide(mActivity.getFragmentView());
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main_home;
    }

    //界面的初始化
    private void initView() {

        initLeftFragment();

        initHomeFragment();
    }

    @Override
    public void onClick(View v) {

    }

    public void initLeftFragment() {
        if (mLeftFragment == null) {
            mLeftFragment = new InfoListFragment();
        }
        FragmentTransaction transaction = getTransaction();
        transaction.replace(R.id.fl_mainpager_left, mLeftFragment);

        transaction.commit();
        //监听事件
        mLeftFragment.setOnHomePageChangeListener(this);
    }

    // 1.获取FragmentManager对象
    public FragmentTransaction getTransaction() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        return transaction;
    }

    public void initHomeFragment() {
        mLayoutHome = (FrameLayout) mActivity.findViewById(R.id.fl_mainpager_info);
        FragmentTransaction transaction = getTransaction();
        if(mConsultMainFragment == null) {
            mConsultMainFragment = new ConsultMainFragment();
            mRightFragment = mConsultMainFragment;
        }

//
        transaction.add(R.id.fl_mainpager_info, mRightFragment);
        //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
        transaction.commit();
    }

    public void onNeedConsultPageShow(){
        if(mRightFragment instanceof ConsultMainFragment)return;
//        mRightFragment = new ConsultMainFragment();
//        FragmentTransaction transaction = getTransaction();
//        transaction.add(R.id.fl_mainpager_info, mRightFragment);
        FragmentTransaction transaction = getTransaction();
        transaction.hide(mRightFragment);
        mRightFragment = mConsultMainFragment;
        if(mConsultMainFragment == null){
            mConsultMainFragment = new ConsultMainFragment();
            mRightFragment = mConsultMainFragment;
            transaction.add(R.id.fl_mainpager_info, mRightFragment);
        }else {
            transaction.show(mRightFragment);
        }

        //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
        transaction.commit();
    }

    @Override
    public void onNeedCompetitionPageShow() {
        if(mRightFragment instanceof CompititionMainFragment)return;
        FragmentTransaction transaction = getTransaction();
        transaction.hide(mRightFragment);

        if(mCompititionMainFragment == null){
            mCompititionMainFragment = new CompititionMainFragment();
            mRightFragment = mCompititionMainFragment;
            transaction.add(R.id.fl_mainpager_info, mRightFragment);
        }else {
            mRightFragment = mCompititionMainFragment;
            transaction.show(mRightFragment);
        }
        //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
        transaction.commit();
    }

    //获取本地应用的版本名称
//    private String  getApplicationVersion(){
//        PackageInfo mPackageInfo = null;
//        String currentVersionName = null;
//        PackageManager mPackageManager = mActivity.getPackageManager();
//        try {
//            mPackageInfo = mPackageManager.getPackageInfo(mActivity.getPackageName(), PackageManager.GET_CONFIGURATIONS);
//            int currentVersionCode = mPackageInfo.versionCode;
//            currentVersionName = mPackageInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return currentVersionName;
//    }
    //访问网络 获取服务器 上的版本信息
//    private void showUpdateVersionDialog(Context context) {
//        showNotification();
//        mConfirmDialog = new ConfirmUpdateDialog(context, getString(R.string.update_version_title) + versionBean.getVersion(),
//                versionBean.getInfoDescription(), getString(R.string.cancel),  getString(R.string.confirm_update), mDialogListener);
//        mConfirmDialog.show();
//    }
//
//    private void getVersionFromNet() {
//        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
//        service.registerObserver(HttpConstant.NOTIFY_DOWNLOAD_APK, this);
//        //访问网络 获取数据
//        service.getApkVersion();
//    }
//
//    private boolean checkVersionIsNew(){
//        String versionName = getString(R.string.versionName);
//
//        if(versionName.equals(versionBean.getVersion())){
//            return true;
//        }
//        return  false;
//    }
//    private BaseConfirmDialog.OnActionClickListener mDialogListener = new BaseConfirmDialog.OnActionClickListener() {
//        @Override
//        public void onActionRight(BaseConfirmDialog dialog) {
//            mConfirmDialog.dismiss();
//            //从网络下载文件
//           downloadApkFile(versionBean.getPath());
//        }
//
//        @Override
//        public void onActionLeft(BaseConfirmDialog dialog) {
//            mConfirmDialog.dismiss();
//        }
//    };

//    private void downloadApkFile(String path){
//        Intent server = new Intent("com.loaddown.application");
//        server.putExtra("loadPath", path);
//        mActivity.startService(server);
////      LoadService.handler.obtainMessage(1,path).sendToTarget();
//    }

//    private void showNotification(){
//
//        NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(mActivity.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mActivity);
//
//        String title = getString(R.string.update_version_title) + versionBean.getVersion();
//        mBuilder
//                .setContentTitle(title)//设置通知栏标题
//                .setContentText(versionBean.getInfoDescription())
//                .setContentIntent(getDefalutIntent(versionBean.getPath())) //设置通知栏点击意图
////                .setNumber(number) //设置通知集合的数量
//                .setTicker(getString(R.string.update_version_title)) //通知首次出现在通知栏，带上升动画效果的
//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
////                .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
//                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
//
//        Notification mNotification  = mBuilder.build();
//        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
//        mNotificationManager.notify(11, mNotification);
//    }
//
//    private PendingIntent getDefalutIntent(String path){
////        Intent intent = new Intent(mActivity, MainHomeFragment.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        Intent server = new Intent("com.loaddown.application");
//        server.putExtra("loadPath", path);
//        PendingIntent pendingIntent= PendingIntent.getService(mActivity, 1, server, PendingIntent.FLAG_ONE_SHOT);
//        return pendingIntent;
//    }

//    @Override
//    public void update(int key, Object o) {
//        switch(key){
//            case HttpConstant.NOTIFY_DOWNLOAD_APK:
//                versionBean = (VersionBean) o;
//                if(versionBean == null){
//                    //没有网络 网络异常的时候 直接返回什么都不做
//                    return;
//                }
//
//                if(!checkVersionIsNew()){
//                    //不一致 提示notification 以及dialog
//                    showUpdateVersionDialog(mActivity);
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        service.unRegisterObserve(HttpConstant.NOTIFY_DOWNLOAD_APK, this);
    }
}
