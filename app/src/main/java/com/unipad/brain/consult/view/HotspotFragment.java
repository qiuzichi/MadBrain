package com.unipad.brain.consult.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.adapter.MyRecyclerAdapter;
import com.unipad.brain.consult.entity.AdPictureBean;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.brain.consult.listener.DividerDecoration;
import com.unipad.brain.consult.listener.OnLoadMoreListener;
import com.unipad.brain.consult.widget.RecommendGallery;
import com.unipad.brain.consult.widget.RecommendPot;
import com.unipad.brain.home.MainBasicFragment;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.ToastUtil;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * 热点
 * Created by jiangLu on 2016/6/20.
 */

public class HotspotFragment extends MainBasicFragment implements IDataObserver {

    private NewsService service;
    private List<NewEntity> newsDatas ;
    private List<AdPictureBean> newsAdvertDatas ;
    private ImageOptions imageOptions;
    private int requestPagerNum = 1;
    private final int perPageDataNumber = 10;
    private boolean isGetData;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyRecyclerAdapter mRecyclerViewAdapter;
    //广告轮播图adapter
    private AdViewPagerAdapter adAdapter;
    //总页面大小
    private int totalPager = 1;
    private TextView tv_error;
    private Boolean isNoAdvertData = false;

    @Override
    public void update(int key, Object o) {
        HIDDialog.dismissAll();
            switch (key) {
                case HttpConstant.NOTIFY_GET_HOTSPOT:
                    if(null == o){
                        //网络访问错误 刷新数据
                        if(newsDatas.size() == 0){
                            tv_error.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            tv_error.setText(getString(R.string.net_error_refrush_data));
                        }else {
                            ToastUtil.showToast(getString(R.string.net_error_refrush_data));
                        }
                        return;
                    }
                    //获取新闻页面数据
                    List<NewEntity> databean = (List<NewEntity>) o;
                    if(databean.size() == 0){
                        //数据为空 显示默认 刷新数据
                        if(newsDatas.size() == 0){
                            tv_error.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            tv_error.setText(getString(R.string.not_news_data));
                        }
                        return;
                    }

                    tv_error.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                    if(requestPagerNum == 1 && databean.size() != 0){
                        totalPager = databean.get(0).getTotalPager();
                    }

                    if(!(totalPager == requestPagerNum)){
                        //始终记录是最后一页的  页数
                        requestPagerNum++;
                    }
//                    if (newsDatas.size() != 0) {
//                        for (int i = databean.size()-1; i >= 0; i--) {
//                            for (int j = 0; j < newsDatas.size(); j++) {
//                                if (databean.get(i).equals(newsDatas.get(j))) {
//                                    break;
//                                } else {
//                                    if (j == newsDatas.size() - 1) {
//                                        //不同 则是新数据
//                                        newsDatas.add(0, databean.get(i));
//                                        break;
//                                    }
//                                    continue;
//                                }
//                            }
//                        }
//                    } else {
                    newsDatas.addAll(databean);
//                    }
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    break;

                case HttpConstant.NOTIFY_GET_HOTADVERT:
                    if(null == o ){
                        //网络访问错误；可以刷新 重新请求数据
                        startLunPic();
                        return;
                    }

                    if(((List<AdPictureBean>)o).size() == 0){
                        //服务器数据为null 没有数据
                        isNoAdvertData = true;
                        startLunPic();
                        return;
                    }

                    //获取轮播图数据
                    newsAdvertDatas.clear();
                    newsAdvertDatas.addAll((List<AdPictureBean>) o);

                    adAdapter .notifyDataSetChanged();
                    break;
                case HttpConstant.NOTIFY_GET_OPERATE:
                    //获取喜欢 点赞 评论 信息
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_introduction;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsDatas = new ArrayList<NewEntity>();
        newsAdvertDatas = new ArrayList<AdPictureBean>();
        //初始化轮播图
        initLunPic();
        initData();
        initRecycler();
        //播放轮播广告
        startLunPic();
    }



    private void initData(){
        //注册服务；
        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_HOTSPOT, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_HOTADVERT, this);
    }

    private void initRecycler(){
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.lv_introduction_recyclerview);
        tv_error = (TextView) getView().findViewById(R.id.tv_load_error_show);
        tv_error.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue2,
                R.color.red,
                R.color.stroke_color,
                R.color.black
        );
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dip2px(24));
        mSwipeRefreshLayout.setRefreshing(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        newsDatas.clear();

                        service.getNews(ConsultTab.HOTSPOT.getTypeId(), null, 1, perPageDataNumber);
                    }
                }, 1000);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerDecoration(mActivity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSwipeRefreshLayout.setRefreshing(false);

        mRecyclerViewAdapter = new MyRecyclerAdapter(mActivity, mRecyclerView, newsDatas,2, mSwipeRefreshLayout);

//        mRecyclerViewAdapter = new MyRecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (totalPager == requestPagerNum) {
                   /* 最后一页 直接吐司 不显示下拉加载*/
                    if(requestPagerNum > 1)
                        ToastUtil.showToast(getString(R.string.loadmore_null_data));
                    return;
                }

                newsDatas.add(null);
                mRecyclerViewAdapter.notifyItemInserted(newsDatas.size() - 1);
                loadMoreData(true);
                mRecyclerViewAdapter.setLoading(true);
            }
        });

    }
    private void initLunPic(){
        //广告轮播图;
        RecommendGallery mAdvertLuobo = (RecommendGallery) getView().findViewById(R.id.point_gallery);
        //轮播图的点的视图;
        RecommendPot adPotView = (RecommendPot) getView().findViewById(R.id.ad_pot);
        newsAdvertDatas.add(new AdPictureBean());
        adPotView.setIndicatorChildCount(newsAdvertDatas.size());
        mAdvertLuobo.initSelectePoint(adPotView);
        mAdvertLuobo.setOnItemClickListener(mOnItemClickListener);

        adAdapter = new AdViewPagerAdapter(getActivity(),newsAdvertDatas, R.layout.ad_gallery_item);
        mAdvertLuobo.setAdapter(adAdapter);
    }

    //对于用户不可见 与 不可见  会被调用；
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {

            if(!isGetData){
                service.getNews(ConsultTab.HOTSPOT.getTypeId(), null, requestPagerNum, perPageDataNumber);
                //获取广告的数据
                service.getAdverts("00002");
                Log.d("hotspot visit ", "获取消息 界面可见");
                isGetData = true;
            }

        } else if (!isVisibleToUser) {
            super.onPause();
        }
    }
    private void loadMoreData(final Boolean isLoading){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoading) {
                    mRecyclerViewAdapter.setLoading(false);
                    newsDatas.remove(newsDatas.size() - 1);
                    mRecyclerViewAdapter.notifyItemRemoved(newsDatas.size());

                    if (!(totalPager == requestPagerNum)) {
                        getNews(ConsultTab.HOTSPOT.getTypeId(), null, requestPagerNum, perPageDataNumber);
                    } else {
                        mRecyclerViewAdapter.notifyItemChanged(newsDatas.size());
                        //重新加载adapter 不然不更新数据
                        mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        ToastUtil.showToast(getString(R.string.loadmore_null_data));
                    }

                }
            }
        }, 3000);
    }
    private class AdViewPagerAdapter extends CommonAdapter<AdPictureBean>{

        public AdViewPagerAdapter(Context context, List<AdPictureBean> datas, int layoutId){
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, AdPictureBean adPictureBean) {
            ImageView imageView = holder.getView(R.id.ad_gallery_item);
            x.image().bind(imageView, adPictureBean.getAdvertPath(),imageOptions);
        }
    }

    private void getNews(String contentType,String title,int page,int size ){
        service.getNews(contentType,title,page,size );
        ToastUtil.createWaitingDlg(getActivity(),null,Constant.LOGIN_WAIT_DLG).show(15);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AdPictureBean bean = newsAdvertDatas.get(position);
            if (bean.getAdvertPath() != null) {
                if (bean.getJumpType().equals("0")) {
                    //本页面打开 发送意图
                    Intent intent = new Intent(mActivity, PagerDetailActivity.class);
                    intent.putExtra("pagerId", bean.getJumpUrl());
                    intent.putExtra("isAdvert", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setData(Uri.parse(bean.getJumpUrl()));
                    startActivity(intent);
                }
            }else {
                if(isNoAdvertData){  //没有广告数据  打开公司网页
                    Intent intent = new Intent(mActivity, PagerDetailActivity.class);
                    //公司网址
                    intent.putExtra("pagerId", "http://www.baidu.com/");
                    intent.putExtra("isAdvert", true);
                    startActivity(intent);
                } else { //刷新广告数据
                    service.getAdverts("00002");
                }

            }
        }
    };

    private void clear(){
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_HOTSPOT, this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_HOTADVERT, this);
    }

    //播放轮播图；
    private void startLunPic(){

        imageOptions = new ImageOptions.Builder()
                // 加载中或错误图片的ScaleType;
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                        //设置加载过程中的图片
                .setLoadingDrawableId(R.drawable.default_advert_pic)
                        //设置加载失败后的图片
                .setFailureDrawableId(R.drawable.default_advert_pic)
                        //设置使用缓存
                .build();
    }

    //回调数据到搜索栏
    @Override
    public List<String> getNewsDatas() {
        if(newsDatas.size() != 0){
            List<String> mTipList= new ArrayList<String>();
            for(int i=0; i<newsDatas.size(); i++){
                String title =  newsDatas.get(i).getTitle();
                mTipList.add(title);
            }
            return  mTipList;
        }
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_load_error_show:
                getNews(ConsultTab.HOTSPOT.getTypeId(), null, 1, perPageDataNumber);
                break;
        }
    }

}
