package com.unipad.brain.consult.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.unipad.AppContext;
import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.consult.listener.OnLoadMoreListener;
import com.unipad.brain.consult.listener.OnShowUpdateDialgo;
import com.unipad.brain.consult.view.PagerDetailActivity;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.DensityUtil;
import com.unipad.utils.PicUtil;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

//RecyclerView  的 adapter
public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataObserver {
    private boolean isLoadMoreData = false;
    private boolean isShowVersion = false;
    private LayoutInflater mLayoutInflater;
    private final int TYPE_ITEM = 0;
    private final int TYPE_FOOTER = 1;
    private final int TYPE_HEADER = 2;
    private final String OPERATE_ZAN = "1";
    private final String OPERATE_COMMENT = "2";

    private int pageId = -1;
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private List<NewEntity> newsDatas;
    private PopupWindow mPopupWindows;
    private OnLoadMoreListener onLoadMoreListener;
    private OnShowUpdateDialgo mOnShowUpdateDialgo;
    private NewsService service;
    private ImageOptions imageOptions;


    public MyRecyclerAdapter(Activity mActivity, final RecyclerView mRecyclerView, List<NewEntity> datas ,int pageId, final SwipeRefreshLayout mSwipeRefreshLayout) {
        this.mActivity = mActivity;
        this.newsDatas = datas;

        this.mRecyclerView = mRecyclerView;
        this.mLayoutInflater = LayoutInflater.from(mActivity);
        this.pageId = pageId;

        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_OPERATE, this);

        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    //获取 RecycleView第一个子view
                    View childView = mRecyclerView.getChildAt(0);
                    //获取第一个子view的顶部坐标
                    if (null != childView) {
                        int top = childView.getTop();
                        //正常来说RecycleView的顶部坐标应该是0,但是严格来考虑,当RecycleView设置了paddingTop时,
                        // 所有子view的绘制将以paddingTop的位置为起始位置,所以实际的顶部应该是paddingTop的高度的数值.
                        int topEdge = mRecyclerView.getPaddingTop();
                        mSwipeRefreshLayout.setEnabled(top >= topEdge);
                    }

                    if (!isLoadMoreData && newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItem + 1  == mRecyclerView.getAdapter().getItemCount()) {
                            onLoadMoreListener.onLoadMore();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
//                    lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
//                    mSwipeRefreshLayout.setEnabled(linearLayoutManager.findFirstVisibleItemPosition() == 0);
                }
            });
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = View.inflate(mActivity,
                    R.layout.item_listview_introduction, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        } else  if(viewType == TYPE_FOOTER){
            return new FooterViewHolder(mLayoutInflater.inflate(R.layout.recycler_footer_layout, parent, false));
        }else if(viewType == TYPE_HEADER) {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.recycle_header_layout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final NewEntity bean = newsDatas.get(position);

            ((ItemViewHolder) holder).text_title.setText(bean.getTitle());
            ((ItemViewHolder) holder).txt_brief.setText(bean.getBrief());
//            ((ItemViewHolder) holder).txt_brief.setEllipsis("...更多");//...替换剩余字符串
//            ((ItemViewHolder) holder).txt_brief.setMaxLines(3);

            final ImageView iv_icon = ((ItemViewHolder) holder).iv_picture;
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

            x.image().bind(iv_icon, bean.getThumbUrl(), imageOptions);

            final ImageView iv_zan = ((ItemViewHolder) holder).iv_pager_zan;
            if (bean.getIsLike()) {
                iv_zan.setImageResource(R.drawable.favorite_introduction_check);
            } else {
                //默认情况下
                iv_zan.setImageResource(R.drawable.favorite_introduction_normal);
            }

            //点赞  点击事件
            iv_zan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    service.getNewsOperate(bean.getId(), OPERATE_ZAN, String.valueOf(!bean.getIsLike()), null, pageId,
                            new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    bean.setIsLike(!bean.getIsLike());
                                    if (bean.getIsLike()) {
                                        //点击之后 变为check
                                        iv_zan.setImageResource(R.drawable.favorite_introduction_check);
                                    } else {
                                        iv_zan.setImageResource(R.drawable.favorite_introduction_normal);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {

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
            final ImageView iv_comment = (ImageView)((ItemViewHolder) holder).iv_pager_comment;
            //评论的点击事件
            iv_comment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //初始化弹出窗体
                    initPopupWindows(bean);
                    showPopupWindows(iv_comment);
                }
            });
            final TextView txt_brief =  ((ItemViewHolder) holder).txt_brief;
            //查看详情点击
            txt_brief.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看详情的界面
                    Intent intent = new Intent(mActivity, PagerDetailActivity.class);
                    intent.putExtra("pagerId", bean.getId());
                    mActivity.startActivity(intent);
                }
            });

        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).pr_moreData.setIndeterminate(true);
        } else if (holder instanceof HeaderViewHolder) {

            ((HeaderViewHolder) holder).text_version.setText("检查到最新版本");

            ((HeaderViewHolder) holder).ll_version.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowVersion && mOnShowUpdateDialgo != null) {
                        //显示dialog 关闭自己
                        mOnShowUpdateDialgo.showDialogUpdate();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
//            if (position   == getItemCount()) {
//                return TYPE_FOOTER;
//            } else {
//                return TYPE_ITEM;
//            }
//        if(position == 0){
//            return TYPE_HEADER;
//        }
//Log.e("myadapter", position + "" + "     lastVisibleItem =" + lastVisibleItem );
//        if(position == 0 && isShowVersion){
//            return TYPE_HEADER;
//        }
        return newsDatas.get(position) != null ? super.getItemViewType(position) : TYPE_FOOTER;
    }

    public void setLoading(Boolean isLoading) {
        isLoadMoreData = isLoading;
    }

    public void setHeadVisibility(boolean isVisibility){
        isShowVersion = isVisibility;
        MyRecyclerAdapter adapter = (MyRecyclerAdapter) mRecyclerView.getAdapter();
        if(isShowVersion){
            newsDatas.add(0, new NewEntity("header"));
            adapter.notifyItemInserted(0);
        }else {
            //移除 header 的数据 如果不移除 会出现空白item页面
            newsDatas.remove(0);
//            adapter.notifyItemRemoved(firstVisibleItem);
        }
//        adapter.notifyItemChanged(firstVisibleItem);
        mRecyclerView.setAdapter(adapter);

    }

    public boolean getIsVisibility(){
        return isShowVersion;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView text_loadMore;
        ProgressBar pr_moreData;
        public FooterViewHolder(View view) {
            super(view);
            text_loadMore = (TextView) view.findViewById(R.id.tv_item_introduction_footer);
            pr_moreData = (ProgressBar) view.findViewById(R.id.progressbar_loadmore_footer);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_version;
        TextView text_version;
        public HeaderViewHolder(View view) {
            super(view);
            ll_version = (LinearLayout) view.findViewById(R.id.relative_update_version_show);
            text_version = (TextView) view.findViewById(R.id.text_update_version);
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView text_title;
        TextView txt_brief;
        ImageView iv_pager_zan;
        ImageView iv_pager_comment;
        ImageView iv_picture;
        RelativeLayout rl_checkDetail;
        public ItemViewHolder(View view) {
            super(view);
            text_title = (TextView) view.findViewById(R.id.tv_item_introduction_news_title);
            txt_brief = (TextView) view.findViewById(R.id.view_line_item_brief);
            iv_picture = (ImageView) view.findViewById(R.id.iv_item_introduction_icon);
            iv_pager_zan = (ImageView) view.findViewById(R.id.iv_item_introduction_zan);
            iv_pager_comment = (ImageView) view.findViewById(R.id.iv_item_introduction_comment);
//            rl_checkDetail = (RelativeLayout) view.findViewById(R.id.rl_item_introduction_detail);
        }
    }


    private void initPopupWindows(final NewEntity newEntity ){
        View mPopupView = View.inflate(mActivity, R.layout.comment_commit_popup, null);

        /*初始化用戶头像*/
        final ImageView user_photo = (ImageView) mPopupView.findViewById(R.id.iv_popup_comment_icon);
        if (!TextUtils.isEmpty(AppContext.instance().loginUser.getPhoto())) {
            x.image().bind(user_photo, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto(), new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap map = PicUtil.drawableToBitmap(drawable);
                    user_photo.setImageBitmap(PicUtil.getRoundedCornerBitmap(map, 360));
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    user_photo.setImageResource(R.drawable.set_headportrait);
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }else {
            user_photo.setImageResource(R.drawable.set_headportrait);
        }


        //评论内容
        final EditText et_commment = (EditText) mPopupView.findViewById(R.id.et_popup_comment_input);
        //监听返回键事件
        et_commment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(mPopupWindows !=null && mPopupWindows.isShowing()){
                        mPopupWindows.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });


        //提交评论按钮
        ((Button) mPopupView.findViewById(R.id.btn_comment_commit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击提交关闭窗体  用户评论内容
                final String user_comment = et_commment.getText().toString().trim();
                if (TextUtils.isEmpty(user_comment)) {
                    Toast.makeText(mActivity, "内容为空 请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                //提交评论内容到服务器
                service.getNewsOperate(newEntity.getId(), OPERATE_COMMENT, null, user_comment, pageId, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(mActivity, "网络原因 提交失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
                //清空输入的内容
                et_commment.setText("");
                //关闭弹出窗体
                closePopup();
            }
        });

        mPopupWindows = new PopupWindow(mPopupView, -1, DensityUtil.dip2px(mActivity, 50), true);//窗体大小
        mPopupWindows.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindows.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindows.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //动画效果;
        ScaleAnimation sa = new ScaleAnimation(0f, 1f, 0.5f, 1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);
        mPopupView.startAnimation(sa);

    }

    private void showPopupWindows(View parent){
        closePopup();
        popupInputMethodWindow();
        mPopupWindows.showAtLocation(parent, Gravity.BOTTOM,0,0);

    }

    //关闭弹出窗体
    private void closePopup(){
        if(mPopupWindows != null && mPopupWindows.isShowing()){
            mPopupWindows.dismiss();
            popupInputMethodWindow();
        }
    }

    //同时弹出 隐藏键盘 弹出框
    private void popupInputMethodWindow() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mActivity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 0);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setmOnShowUpdateDialgo(OnShowUpdateDialgo mOnShowUpdateDialgo) {
        this.mOnShowUpdateDialgo = mOnShowUpdateDialgo;
    }



    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.NOTIFY_GET_OPERATE:
                //获取喜欢 点赞 评论 信息
                mRecyclerView.getAdapter().notifyDataSetChanged();
                break;
        }
    }

}
