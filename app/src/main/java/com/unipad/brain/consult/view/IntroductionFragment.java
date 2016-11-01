package com.unipad.brain.consult.view;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.adapter.MyRecyclerAdapter;
import com.unipad.brain.consult.entity.AdPictureBean;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.brain.consult.listener.DividerDecoration;
import com.unipad.brain.consult.listener.OnLoadMoreListener;
import com.unipad.brain.consult.listener.OnShowUpdateDialgo;
import com.unipad.brain.consult.widget.RecommendGallery;
import com.unipad.brain.consult.widget.RecommendPot;
import com.unipad.brain.dialog.BaseConfirmDialog;
import com.unipad.brain.dialog.ConfirmUpdateDialog;
import com.unipad.brain.home.MainBasicFragment;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.bean.VersionBean;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.LogUtil;
import com.unipad.utils.ToastUtil;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐 新闻
 * Created by jianglu on 2016/6/20.
 */
public class IntroductionFragment extends MainBasicFragment implements IDataObserver {

    private List<NewEntity> newsDatas;
    //默认加载第一页  的数据 标记为最后一页的页数
    private int requestPagerNum = 1;
    private int permaryDataNumber = 10;
    private NewsService service;
    private boolean isGetData;
    private MyRecyclerAdapter mRecyclerViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    //返回的服务器 apk版本信息
    private VersionBean versionBean;
    private ConfirmUpdateDialog mConfirmDialog;
    private RelativeLayout mRelativeLayoutVersion;
    private RelativeLayout emptyView;
    private Boolean isRefreshData;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsDatas = new ArrayList<NewEntity>();
        initData();
        initRecycler();
    }

    private void initData() {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.lv_introduction_recyclerview);
        TextView tv_error = (TextView) getView().findViewById(R.id.tv_load_error_show);
        emptyView = (RelativeLayout) getView().findViewById(R.id.rl_empty_view);
        tv_error.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_widget);
        mRelativeLayoutVersion = (RelativeLayout) getView().findViewById(R.id.rl_reminder_version);
        ((TextView) getView().findViewById(R.id.text_update_version)).setOnClickListener(this);

        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_OPERATE, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_NEWS, this);
        service.registerObserver(HttpConstant.NOTIFY_DOWNLOAD_APK, this);

    }
    private void initRecycler(){

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue2,
                R.color.red,
                R.color.stroke_color,
                R.color.black
        );
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dip2px(24));
        isRefreshData = false;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshData = true;
                if (newsDatas.size() > 1 && newsDatas.get(0).getTotalPager() > 1)
                    mRecyclerViewAdapter.setFooterIsFoot(false);

                newsDatas.clear();
                service.getNews(ConsultTab.INTRODUCATION.getTypeId(), null, requestPagerNum = 1, permaryDataNumber);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
//                        if(mRecyclerViewAdapter.getIsVisibility()){
//                            newsDatas.add(0, new NewEntity("header"));
//                        }
                    }
                }, DELAYETIMEOUT);
            }

        });

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerDecoration(mActivity, LinearLayoutManager.VERTICAL, R.drawable.list_divider_line));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mSwipeRefreshLayout.setRefreshing(false);

        mRecyclerViewAdapter = new MyRecyclerAdapter(mActivity, mRecyclerView, newsDatas, 0, mSwipeRefreshLayout);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (newsDatas != null && newsDatas.size() != 0 && !isRefreshData) {
                    int totalPager = newsDatas.get(0).getTotalPager();
                    if (requestPagerNum > totalPager) {
                   /* 最后一页 直接吐司 不显示下拉加载*/
                        ToastUtil.showToast(getString(R.string.loadmore_null_data));
                        return;
                    }
                    mRecyclerViewAdapter.setLoading(true);
                    newsDatas.add(null);
                    mRecyclerViewAdapter.notifyItemInserted(newsDatas.size() - 1);
                    //请求网络 获取数据
                    getNews(ConsultTab.INTRODUCATION.getTypeId(), null, requestPagerNum, permaryDataNumber);
                    loadMoreData(true, DELAYETIMEOUT);
                }
            }
        });

        mRecyclerViewAdapter.setmOnShowUpdateDialgo(new OnShowUpdateDialgo() {
            @Override
            public void showDialogUpdate() {
                showUpdateVersionDialog(mActivity);
            }
        });
    }

    private void getNews(String contentType,String title,int page,int size ){
        service.getNews(contentType, title, page, size);
//        ToastUtil.createWaitingDlg(getActivity(),null,Constant.LOGIN_WAIT_DLG).show(15);
    }

    private boolean checkVersionIsNew(){
        PackageManager pm = mActivity.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(mActivity.getPackageName(), 0);
            String versionName = pi.versionName;
            if (versionName.equals(versionBean.getVersion())) {
                return true;
            }
            ((TextView) getView().findViewById(R.id.text_update_version)).setText(getString(R.string.check_version) + versionBean.getVersion());
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return  false;
        }
//        int versioncode = pi.versionCode;
//        String versionName = getString(R.string.versionName);
    }

    private BaseConfirmDialog.OnActionClickListener mDialogListener = new BaseConfirmDialog.OnActionClickListener() {
        @Override
         public void onActionRight(BaseConfirmDialog dialog) {
            mConfirmDialog.dismiss();
            //从网络下载文件
            downloadApkFile(versionBean.getPath());
            //点击确认更新 隐藏提示栏 删除header item
            mRelativeLayoutVersion.setVisibility(View.GONE);
//            mRecyclerViewAdapter.setHeadVisibility(false);
        }

        @Override
        public void onActionLeft(BaseConfirmDialog dialog) {
            mConfirmDialog.dismiss();
        }
    };

    private void downloadApkFile(String path){
        Intent server = new Intent("com.loaddown.application");
        server.putExtra("loadPath", path);
        server.setPackage(mActivity.getPackageName());//这里你需要设置你应用的包名
        mActivity.startService(server);
//      LoadService.handler.obtainMessage(1,path).sendToTarget();

    }

    //显示dialog  以及 通知栏 有新版本
    private void showUpdateVersionDialog(Context context) {
        mConfirmDialog = new ConfirmUpdateDialog(context, getString(R.string.update_version_title) + versionBean.getVersion(),
                versionBean.getInfoDescription(), getString(R.string.cancel),  getString(R.string.confirm_update), mDialogListener);
        mConfirmDialog.show();
    }

    private void showNotification(){

        NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(mActivity.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mActivity);

        String title = getString(R.string.update_version_title) + versionBean.getVersion();
        mBuilder
                .setContentTitle(title)//设置通知栏标题
                .setContentText(versionBean.getInfoDescription())
                .setContentIntent(getDefalutIntent(versionBean.getPath())) //设置通知栏点击意图
//                .setNumber(number) //设置通知集合的数量
                .setTicker(getString(R.string.update_version_title)) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON

        Notification mNotification  = mBuilder.build();
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(11, mNotification);
    }

    private PendingIntent getDefalutIntent(String path){
        Intent server = new Intent("com.loaddown.application");
        server.putExtra("loadPath", path);
        server.setPackage(mActivity.getPackageName());//这里你需要设置你应用的包名
        PendingIntent pendingIntent= PendingIntent.getService(mActivity, 1, server, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }


   /* 对于用户不可见 与 不可见  会被调用；*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {

            if (!isGetData) {
                service.getNews(ConsultTab.INTRODUCATION.getTypeId(), null, requestPagerNum, permaryDataNumber);
                service.getApkVersion();

                //下拉动画生效
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                //网络超时自动取消下拉刷新；
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, DELAYETIMEOUT);
                Log.d("introduction visit ", "获取消息 界面可见");
                isGetData = true;
            }

        } else if (!isVisibleToUser) {
            super.onPause();
        }
    }




   private void clear() {
       service.unRegisterObserve(HttpConstant.NOTIFY_GET_NEWS, this);
       service.unRegisterObserve(HttpConstant.NOTIFY_GET_OPERATE, this);
       service.unRegisterObserve(HttpConstant.NOTIFY_DOWNLOAD_APK, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //第一个节目  当节目可见 获取数据
        setUserVisibleHint(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(getClass().getSimpleName(), "onDestroy");
        clear();
    }


    @Override
    public int getLayoutId() {
        return  R.layout.fragment_introduction;
    }
    //回调数据到搜索栏
    @Override
    public List<String> getNewsDatas() {
        if(newsDatas.size() != 0){
            List<String> mTipList= new ArrayList<String>();
            for(int i=0; i<newsDatas.size(); i++){
                String title =  newsDatas.get(i).getTitle();
                if(TextUtils.isEmpty(title)){
                    continue;
                }
                mTipList.add(title);
            }
            return  mTipList;
        }
        return null;
    }

    private void loadMoreData(final Boolean isLoading, int time){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isLoading) {
                        removeFooterView();

                    }
                }
            }, time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_update_version:
                showUpdateVersionDialog(mActivity);
                break;
            case R.id.tv_load_error_show:
                getNews(ConsultTab.INTRODUCATION.getTypeId(), null, 1, permaryDataNumber);
                break;
            default:
                break;
        }
    }

    //用于网络请求数据 key 是网页的id   o是解析后的list数据
    @Override
    public void update(int key, Object o) {
//        HIDDialog.dismissAll();
            switch (key) {
                case HttpConstant.NOTIFY_GET_NEWS:
                    requestPagerNum++;
                    isRefreshData = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    removeFooterView();
                    mRecyclerViewAdapter.setIsRefresh();
                    if(null == o ){
                        //网络访问错误 刷新数据
                        if(newsDatas.size() == 0){
                            emptyView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                        }
                        ToastUtil.showToast(getString(R.string.net_error_refrush_data));
                        return;
                    }

                    //获取新闻页面数据
                    List<NewEntity> databean = (List<NewEntity>) o;
                    if(databean.size() == 0){
                        //数据为空 显示默认 刷新数据
                        if(newsDatas.size() == 0){
                            emptyView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                        }
                        return;
                    }
                    emptyView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    newsDatas.addAll(databean);

                    if(requestPagerNum > newsDatas.get(0).getTotalPager()){
                        newsDatas.add(null);
                        mRecyclerViewAdapter.notifyItemInserted(newsDatas.size() - 1);
                        mRecyclerViewAdapter.setFooterIsFoot(true);
                    }
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    break;

                case HttpConstant.NOTIFY_GET_OPERATE:
                    //获取喜欢 点赞 评论 信息
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case HttpConstant.NOTIFY_DOWNLOAD_APK:
                    versionBean = (VersionBean) o;
                    if (versionBean == null ||TextUtils.isEmpty(versionBean.getVersion()) ) {
                    /*没有网络 网络异常的时候 直接返回什么都不做*/
                        return;
                    }

                    if (mRecyclerViewAdapter != null && !checkVersionIsNew()) {
                        /*版本不一致的时候 显示textview*/
                        mRelativeLayoutVersion.setVisibility(View.VISIBLE);
//                    mRecyclerViewAdapter.setHeadVisibility(true);
                        showNotification();
                    }
                    break;
                default:
                    break;
            }


    }

    private void removeFooterView() {

        if (newsDatas.size() > 1 && 1 == mRecyclerViewAdapter.getItemViewType(newsDatas.size() - 1)){
            if(mRecyclerViewAdapter.getLoading()){
                mRecyclerViewAdapter.setLoading(false);
                newsDatas.remove(newsDatas.size() - 1);
                mRecyclerViewAdapter.notifyItemRemoved(newsDatas.size());
            }
        }
    }
}
