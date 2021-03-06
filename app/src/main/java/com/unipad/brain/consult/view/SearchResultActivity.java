package com.unipad.brain.consult.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lidroid.xutils.BitmapUtils;
import com.unipad.AppContext;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.ToastUtil;
import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jianglu on 2016/7/8.
 */
public class SearchResultActivity extends BasicActivity implements IDataObserver{

    private List<NewEntity> mSearchDatas;
    private NewsService service;
    private SearchAdapter mSearchAdapter;
    private ListView mListView;
    private String contentId;
    private int pageId;
    private final int permaryDataNumber = 10;
    private int requestPager;
    private final String OPERATE_ZAN = "1";
    private boolean isLoading = false;
    //上拉加载更多；
    private View mListViewFooter;
    private ImageOptions imageOptions;
    private boolean isActionDown;
    private float downY;
    private boolean isUpMove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mSearchDatas = new ArrayList<NewEntity>();
        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_SEARCH_RUSULT, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_SEARCH_OCCSION, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_SEARCH_HOTSPOT, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_OPERATE, this);
        contentId = getIntent().getStringExtra("contentId");
        //说明的第一个页面 资讯页面；00001---00002----00003
        pageId = Integer.parseInt(contentId.substring(contentId.lastIndexOf("0") + 1)) - 1;
        service.getSearchNews(contentId, contentId, getIntent().getStringExtra("queryContent"), requestPager = 1, permaryDataNumber);

    }

    @Override
    public void initData() {
        mListView = (ListView) findViewById(R.id.listview_search_result);
        //返回键点击事件
        ((TextView)findViewById(R.id.title_back_text_search)).setOnClickListener(this);
        String title = null;
        if("00001".equals(contentId)){
            title = getString(R.string.info_result);
        }else if("00002".equals(contentId)) {
            title = getString(R.string.competetion_result);
        }else if("00003".equals(contentId)) {
            title = getString(R.string.hotspot_result);
        }
       //设置搜索结果的标题
        ((TextView)findViewById(R.id.title_detail_text_search)).setText(title);
        mSearchAdapter = new SearchAdapter(this, mSearchDatas, R.layout.item_listview_introduction );
        mListView.setAdapter(mSearchAdapter);

        isUpMove = false;
        downY = 0;
        // 拖动listview时，如果点击到的地方是item里的一些view，可能出现ACTION_DOWN触发不了的问题。
        // 利用isActionDown，当为false时就触发了ACTION_MOVE，第一个action需要当成ACTION_DOWN处理
        isActionDown = false;
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isActionDown = true;
                        downY = event.getY();
                        Log.i("onTouchListener", "downY:" + downY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isActionDown) {
                            // 当为false时就触发了ACTION_MOVE，第一个action需要当成ACTION_DOWN处理
                            isActionDown = true;
                            downY = event.getY();
                            Log.i("onTouchListener", "downY:" + downY + " no action down");
                        } else {
                            float currentY = event.getY();
                            Log.i("onTouchListener", "downY:" + downY + " currentY::"
                                    + currentY + " currentY - downY:"
                                    + (currentY - downY));
                            if (currentY - downY < -20 && !isUpMove) {
                                // 向上拉，隐藏
                                isUpMove = true;
                                Log.i("onTouchListener", "downY:" + downY + " currentY::"
                                        + currentY + " currentY - downY:"
                                        + (currentY - downY) + " hide");
                                if(canLoad()){
                                    if (mSearchDatas.size() != 0) {
                                        int TotalPager = mSearchDatas.get(0).getTotalPager();
                                        if (requestPager > TotalPager) {
                                            ToastUtil.showToast(getString(R.string.loadmore_null_data));
                                            isUpMove = false;
                                        } else {
                                            setLoading(true);
                                            service.getSearchNews(contentId, contentId, getIntent().getStringExtra("queryContent"), requestPager, permaryDataNumber);
                                        }
                                    }
                                }

                            } else if (currentY - downY > 20 && isUpMove) {
                                // 向下拉，显示
                                isUpMove = false;
                                Log.i("onTouchListener", "downY:" + downY + " currentY::"
                                        + currentY + " currentY - downY:"
                                        + (currentY - downY) + " show");
                            }

                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        isActionDown = false;// isActionDown重置
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


    }

    private void initFooterView(){
        if(mListViewFooter == null){
            mListViewFooter = View.inflate(this, R.layout.recycler_footer_layout, null);
            mListViewFooter.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, com.unipad.utils.DensityUtil.dip2px(this,80)));
        }
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading ;
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {

        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView
                    .getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * @param loading true 正在加载数据
     *                false  隐藏加载进度条；
     */
    public void setLoading(boolean loading) {
        initFooterView();
        isLoading = loading;
        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            mListView.removeFooterView(mListViewFooter);
        }
    }

    private class SearchAdapter extends CommonAdapter<NewEntity>{

        public SearchAdapter(Context context, List<NewEntity> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final NewEntity newEntity) {
            //设置  缩略图
            ImageView iv_picture = (ImageView) holder.getView(R.id.iv_item_introduction_icon);
            if(null == imageOptions){
                 imageOptions = new ImageOptions.Builder()
                        //                    .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                        //                    .setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                        .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                                //                    .setLoadingDrawableId(R.mipmap.iv_icon)//加载中默认显示图片
                        .setFailureDrawableId(R.drawable.error_remind)//加载失败后默认显示图片
                        .build();
            }

            x.image().bind(iv_picture, newEntity.getThumbUrl(), imageOptions);
            //设置标题
             ((TextView) holder.getView(R.id.tv_item_introduction_news_title)).setText(newEntity.getTitle());
            //设置更新时间
            TextView txt_brief = (TextView) holder.getView(R.id.view_line_item_brief);
            ((TextView) holder.getView(R.id.view_line_item_brief)).setText(newEntity.getBrief());
            //分割线
//            View view_line_split = (View) holder.getView(R.id.view_line_item_introduction);

            //点赞的imagebutton
            final ImageView iv_pager_zan = (ImageView) holder.getView(R.id.iv_item_introduction_zan);


            if (newEntity.getIsLike()) {
                iv_pager_zan.setImageResource(R.drawable.favorite_introduction_check);
            } else {
                //默认情况下
                iv_pager_zan.setImageResource(R.drawable.favorite_introduction_normal);
            }

            ((TextView) holder.getView(R.id.tv_item_introduction_news_title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看详情的界面
                    openDetialPager(newEntity);
                }
            });
            txt_brief.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看详情的界面
                  openDetialPager(newEntity);
                }
            });

            //点赞  点击事件
            iv_pager_zan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    service.getNewsOperate(newEntity.getId(), OPERATE_ZAN, String.valueOf(!newEntity.getIsLike()), null, pageId,
                            new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    newEntity.setIsLike(!newEntity.getIsLike());
                                    if (newEntity.getIsLike()) {
                                        //点击之后 变为check
                                        iv_pager_zan.setImageResource(R.drawable.favorite_introduction_check);
                                    } else {
                                        iv_pager_zan.setImageResource(R.drawable.favorite_introduction_normal);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    ToastUtil.showToast("网络异常  提交失败");
                                }

                                @Override
                                public void onCancelled(CancelledException e) {

                                }

                                @Override
                                public void onFinished() {

                                }

                            });
                }
            });

        }

        private void openDetialPager(NewEntity newEntity ){
            Intent intent = new Intent(SearchResultActivity.this, PagerDetailActivity.class);
            intent.putExtra("pagerId", newEntity.getId());
            SearchResultActivity.this.startActivity(intent);
        }

    }


    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.NOTIFY_GET_SEARCH_RUSULT:
            case HttpConstant.NOTIFY_GET_SEARCH_OCCSION:
            case HttpConstant.NOTIFY_GET_SEARCH_HOTSPOT:
                //加载完成；删除footerview
                isUpMove = false;
                if(mListViewFooter != null)
                    setLoading(false);
                if(o == null){
                    ToastUtil.showToast(getString(R.string.net_error_refrush_data));
                    return;
                }
                //获取新闻页面数据
                mSearchDatas.addAll((List<NewEntity>) o);
                if(mSearchDatas.size() == 0){
                    //说明搜索不到数据；
                    mListView.setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.tv_listview_empty)).setVisibility(View.VISIBLE);
                }else {
                    requestPager ++;
                    if(requestPager > mSearchDatas.get(0).getTotalPager()){
                        View footerView = View.inflate(this, R.layout.recycler_footer_layout, null);
                        footerView.setLayoutParams(new AbsListView.LayoutParams
                                (AbsListView.LayoutParams.MATCH_PARENT, com.unipad.utils.DensityUtil.dip2px(this,80)));
                        ((ProgressBar)footerView.findViewById(R.id.progressbar_loadmore_footer)).setVisibility(View.GONE);
                        TextView text_loadMore = (TextView) footerView.findViewById(R.id.tv_item_introduction_footer);
                        text_loadMore.setText(getString(R.string.no_available));
                        text_loadMore.setTextColor(getResources().getColor(R.color.stroke_color));
                        mListView.addFooterView(footerView);
                    }
                    mSearchAdapter.notifyDataSetChanged();
                }
                break;
            case HttpConstant.NOTIFY_GET_OPERATE:
                mSearchAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back_text_search:
                finish();

                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy()" + " is run");
    }
    private void clear(){
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_SEARCH_RUSULT,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_SEARCH_OCCSION,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_SEARCH_HOTSPOT,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_OPERATE, this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
