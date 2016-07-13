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
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.unipad.utils.PicUtil;
import com.unipad.utils.ToastUtil;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianglu on 2016/7/8.
 */
public class SearchResultActivity  extends BasicActivity implements IDataObserver{

    private List<NewEntity> mSearchDatas = new ArrayList<NewEntity>();
    private NewsService service;
    private SearchAdapter mSearchAdapter;
    private ListView mListView;
    private View mPopupView;

    private PopupWindow mPopupWindows;
    private ScaleAnimation sa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_NEWS, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_OPERATE, this);
        service.getNews(getIntent().getStringExtra("contentId"), getIntent().getStringExtra("queryContent"), 1, 10);
    }

    @Override
    public void initData() {
        mListView = (ListView) findViewById(R.id.listview_search_result);
        //返回键点击事件
        ((TextView)findViewById(R.id.title_back_text_search)).setOnClickListener(this);


        mSearchAdapter = new SearchAdapter(this, mSearchDatas, R.layout.item_listview_introduction );
        mListView.setAdapter(mSearchAdapter);
    }

    private class SearchAdapter extends CommonAdapter<NewEntity>{

        public SearchAdapter(Context context, List<NewEntity> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final NewEntity newEntity) {
            //设置  缩略图
            ImageView iv_picture = (ImageView) holder.getView(R.id.iv_item_introduction_icon);
            iv_picture.setImageBitmap(PicUtil.getbitmap(newEntity.getThumbUrl()));
            //设置标题
             ((TextView) holder.getView(R.id.tv_item_introduction_news_title)).setText(newEntity.getTitle());
            //设置更新时间
            ((TextView) holder.getView(R.id.tv_item_introduction_updatetime)).setText(newEntity.getPublishDate());
            //分割线
            View view_line_split = (View) holder.getView(R.id.view_line_item_introduction);

            //点赞的imagebutton
            final ImageView iv_pager_zan = (ImageView) holder.getView(R.id.iv_item_introduction_zan);
            //评论
            final ImageView iv_pager_comment = (ImageView) holder.getView(R.id.iv_item_introduction_comment);

//            TextView tv_checkDetail  = (TextView) holder.getView(R.id.tv_item_introduction_detail);
            //查看详情的 relative
            RelativeLayout rl_checkDetail = holder.getView(R.id.rl_item_introduction_detail);

            if (newEntity.getIsLike()) {
                iv_pager_zan.setImageResource(R.drawable.favorite_introduction_check);
            } else {
                //默认情况下
                iv_pager_zan.setImageResource(R.drawable.favorite_introduction_normal);
            }


            //点赞  点击事件
            iv_pager_zan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.e("", "dianzao kai shi !!!!");
                    service.getNewsOperate(newEntity.getId(), "1", String.valueOf(!newEntity.getIsLike()), "0", 0,
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


            //评论的点击事件
            iv_pager_comment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    initPopupWindows(newEntity);
                    showPopupWindows(iv_pager_comment);
                }
            });
            //查看详情点击
            rl_checkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看详情的界面
                    Intent intent = new Intent(getApplicationContext(), PagerDetailActivity.class);
                    intent.putExtra("pagerId", newEntity.getId());
                    startActivity(intent);

                }
            });


        }

    }
    private void initPopupWindows(final NewEntity newEntity ){
        mPopupView = View.inflate(this, R.layout.comment_commit_popup, null);
        //评论内容
        final EditText et_commment = (EditText) mPopupView.findViewById(R.id.et_popup_comment_input);
        //提交评论按钮
        Button btn_commit = (Button) mPopupView.findViewById(R.id.btn_comment_commit);

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击提交关闭窗体  用户评论内容
                final String user_comment = et_commment.getText().toString().trim();
                if(TextUtils.isEmpty(user_comment)){
                    Toast.makeText(getApplicationContext(), "内容为空 请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                //提交评论内容到服务器
                service.getNewsOperate(newEntity.getId(), "2", null, user_comment, 0, new Callback.CommonCallback<String>(){

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(getApplicationContext(), "网络原因 提交失败", Toast.LENGTH_SHORT).show();
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

        mPopupWindows = new PopupWindow(mPopupView, -1, 100, true);
        mPopupWindows.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindows.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindows.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //动画效果;
        sa = new ScaleAnimation(0f, 1f, 0.5f, 1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);

    }

    private void showPopupWindows(View parent){
        closePopup();
        popupInputMethodWindow();
        mPopupView.startAnimation(sa);
        mPopupWindows.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 0);
    }


    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.NOTIFY_GET_NEWS:

                mSearchDatas.clear();
                //获取新闻页面数据
                mSearchDatas.addAll((List<NewEntity>) o);

                if(mSearchDatas.size() == 0){
                    //说明搜索不到数据；
                    mListView.setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.tv_listview_empty)).setVisibility(View.VISIBLE);
                    return;
                }
                mSearchAdapter.notifyDataSetChanged();
                break;
            case HttpConstant.NOTIFY_GET_OPERATE:
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
        clear();
    }
    private void clear(){
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_NEWS,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_OPERATE,this);
    }
}