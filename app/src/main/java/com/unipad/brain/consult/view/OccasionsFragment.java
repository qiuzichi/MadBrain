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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lidroid.xutils.BitmapUtils;
import com.unipad.brain.R;
import com.unipad.brain.consult.adapter.MyRecyclerAdapter;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.brain.consult.listener.DividerDecoration;
import com.unipad.brain.consult.listener.OnLoadMoreListener;
import com.unipad.brain.home.MainBasicFragment;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.entity.AdPictureBean;
import com.unipad.brain.consult.widget.RecommendGallery;
import com.unipad.brain.consult.widget.RecommendPot;
import com.unipad.brain.home.MainBasicFragment;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
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

public class OccasionsFragment extends MainBasicFragment implements IDataObserver {

    private NewsService service;
    private List<NewEntity> newsDatas ;
    private int requestPagerNum = 1;
    private final int perPageDataNumber = 10;
    //总页面大小
    private int totalPager = 1;
    private boolean isGetData;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyRecyclerAdapter mRecyclerViewAdapter;
    private TextView tv_error;


    @Override
    public void update(int key, Object o) {

        switch (key) {
            case HttpConstant.NOTIFY_GET_COMPETITION:
                if(null == o){
                    //网络访问错误 刷新数据
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setClickable(true);
                    tv_error.setText(getString(R.string.net_error_refrush_data));
                    tv_error.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            tv_error.setClickable(false);
                        }
                    });
                    return;
                }
                //获取新闻页面数据
                List<NewEntity> databean = (List<NewEntity>) o;
                if(databean.size() == 0){
                    //数据为空 显示默认 刷新数据
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(getString(R.string.not_news_data));
                    return;
                }

                tv_error.setVisibility(View.GONE);

                if (requestPagerNum == 1 && databean.size() != 0) {
                    totalPager = databean.get(0).getTotalPager();
                }

                if(!(requestPagerNum == totalPager)){
                    //始终记录是最后一页的  页数
                    requestPagerNum++;
                }
                newsDatas.addAll(databean);
                mRecyclerViewAdapter.notifyDataSetChanged();
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
        //隐藏轮播图的view
        RelativeLayout mLunBoPic = (RelativeLayout) getView().findViewById(R.id.rl_advert_view);
        mLunBoPic.setVisibility(View.GONE);

        tv_error = (TextView) getView().findViewById(R.id.tv_load_error_show);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.lv_introduction_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_widget);

        initData();
        initRecycler();

    }



    private void initData(){
        //注册服务；
        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_COMPETITION, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_OPERATE, this);

    }

    private void initRecycler(){

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
                        requestPagerNum = 1;
                        service.getNews(ConsultTab.OCCASIONS.getTypeId(), null, requestPagerNum, perPageDataNumber);
                    }
                }, 1000);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerDecoration(mActivity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSwipeRefreshLayout.setRefreshing(false);

        mRecyclerViewAdapter = new MyRecyclerAdapter(mActivity, mRecyclerView, newsDatas,1);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(requestPagerNum == totalPager){
                   /* 最后一页 直接吐司 不显示下拉加载*/
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

    //对于用户不可见 与 不可见  会被调用；
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {

            if(!isGetData){
                service.getNews(ConsultTab.OCCASIONS.getTypeId(), null, requestPagerNum, perPageDataNumber);
                Log.d("occasion visit ", "获取消息 界面可见");
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
                        getNews(ConsultTab.OCCASIONS.getTypeId(), null, requestPagerNum, perPageDataNumber);
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

    private void getNews(String contentType,String title,int page,int size ){
        service.getNews(contentType, title, page, size);
    }


    private void clear(){
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_COMPETITION, this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_OPERATE, this);
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

    }

}
