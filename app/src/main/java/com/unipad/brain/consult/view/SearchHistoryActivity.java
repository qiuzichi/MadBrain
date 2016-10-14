package com.unipad.brain.consult.view;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unipad.AppContext;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.consult.widget.CustomSearchView;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.SharepreferenceUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jianglu on 2016/7/8.
 */
public class SearchHistoryActivity extends BasicActivity implements IDataObserver {


    private ListView mListView;
    private RelativeLayout mShowHistory;
    private ArrayAdapter<String> autoCompleteAdapter;
    private String contentId;
    private List<String> autoCompleteData;
    private List<String> dbData;
    private HistoryAdapter mHistoryAdapter;
    private LinkedList<String> historyDatas;
    private CustomSearchView mSearchView;
    private NewsService service;
    private int requestPager;
    //限制显示历史数据的个数；
    private final  int showNumHistoryItem = 3;
    //是否完全显示所有的历史记录数据
   // private boolean isFullyShow;
    //记录是否已经添加了footerview
    /////private Boolean isAlreadyAddFooter;
    private View mShowMoreView;
    private TextView mTextViewMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_SEARCH_RUSULT, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_SEARCH_OCCSION, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_SEARCH_HOTSPOT, this);
        service.registerObserver(HttpConstant.NOTIFY_GET_OPERATE, this);
        contentId = getIntent().getStringExtra("contentId");
        //isFullyShow = false;
        //isAlreadyAddFooter = false;

    }

    @Override
    public void initData() {
        historyDatas = new LinkedList<String>();
        //返回键点击事件
        ((TextView) findViewById(R.id.title_back_text_search)).setOnClickListener(this);
        ((TextView) findViewById(R.id.txt_clear_history)).setOnClickListener(this);
        /*1、 读取本地搜索历史记录；  2、 如果有  显示历史 如没有 隐藏  3、 获取焦点*/
        mListView = (ListView) findViewById(R.id.listview_search_history);
        mShowHistory = (RelativeLayout) findViewById(R.id.rl_history_visit);
        mSearchView = (CustomSearchView) findViewById(R.id.custom_search_view);
//        CustomSearchView mSearchView = new CustomSearchView(this);
       // frameLayout.addView(mSearchView);
        //获取焦点
        mSearchView.setSearchViewListener(mSearchViewListener);
        //加载本地的历史搜索数据
        mHistoryAdapter = new HistoryAdapter(this, historyDatas, R.layout.list_item_searchlist);
        showHistoryDatas();
        if(historyDatas.size() != 0 ){
            mSearchView.setFouceEditText(historyDatas.getFirst());
        }

        //获取自动引导完成的数据；从fragment获取 如果fragment 数据为空
        requestPager = 1;
        getDbData();
        //初始化自动补全数据
        getAutoCompleteData(null);
        getShowMoreView();
        mSearchView.setAutoCompleteAdapter(autoCompleteAdapter);
        mListView.setAdapter(mHistoryAdapter);
        mListView.setOnItemClickListener(mHistoryItemClickListener);

    }

    private void getShowMoreView(){
        if(historyDatas == null || historyDatas.size() <= showNumHistoryItem) return;

        mShowMoreView = View.inflate(this, R.layout.textview_show_full_more, null);
        mTextViewMore = (TextView) mShowMoreView.findViewById(R.id.txt_show_more_history);
        mShowMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isFullyShow = !isFullyShow;
                mListView.setAdapter(mHistoryAdapter);
                Drawable drawable = null;
                if (mTextViewMore.getText().toString().trim().equals(getString(R.string.show_more_search_history))) {
//                    mListView.removeFooterView(mShowMoreView);
                    mTextViewMore.setText(getString(R.string.show_limit_search_history));
                    drawable = getResources().getDrawable(R.drawable.line_location_left);
                   // isAlreadyAddFooter = true;
//                    mListView.addFooterView(mShowMoreView);
                } else {
                    mTextViewMore.setText(getString(R.string.show_more_search_history));
                    drawable = getResources().getDrawable(R.drawable.arrow_right);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                mTextViewMore.setCompoundDrawables(null, null, drawable, null);
                mHistoryAdapter.notifyDataSetChanged();
            }
        });

            mListView.addFooterView(mShowMoreView);

    }

    private void showHistoryDatas() {
        String searchHistory = SharepreferenceUtils.getString(AppContext.instance().loginUser.getUserName() + "history", null);
        if(TextUtils.isEmpty(searchHistory)){
            mShowHistory.setVisibility(View.GONE);
        }else {
            mShowHistory.setVisibility(View.VISIBLE);
            String[] history = searchHistory.split("_");
            for (int i = 0; i < history.length; i++){
                historyDatas.addFirst(history[i]);
            }
        }
            mHistoryAdapter.notifyDataSetChanged();
    }

    private CustomSearchView.SearchViewListener mSearchViewListener = new CustomSearchView.SearchViewListener() {
        @Override
        public void onRefreshAutoComplete(String text) {
            mShowHistory.setVisibility(View.GONE);
            getAutoCompleteData(text);
        }

        @Override
        public void onSearch(String text) {
            //保存数据 到本地；
            if(TextUtils.isEmpty(text)) return;{
                Toast.makeText(SearchHistoryActivity.this, getString(R.string.search_conment), Toast.LENGTH_SHORT).show();
            }
            String data = SharepreferenceUtils.getString(AppContext.instance().loginUser.getUserName() + "history", null);
            if(TextUtils.isEmpty(data)){
                SharepreferenceUtils.writeString(AppContext.instance().loginUser.getUserName() + "history", text);
                historyDatas.add(text);
            } else {
                //判断是否包含该数据
                if(historyDatas.contains(text)){
                    if(historyDatas.size() != 1){
                        if(historyDatas.indexOf(text) != 0){
                            data = data.replaceAll(text + "_", "");
                            SharepreferenceUtils.writeString(AppContext.instance().loginUser.getUserName() + "history", data + "_" + text);
                        }
                        //重复数据 放在最前面
                        historyDatas.remove(text);
                        historyDatas.addFirst(text);
                    }

                } else {
                    historyDatas.addFirst(text);
                    SharepreferenceUtils.writeString(AppContext.instance().loginUser.getUserName() + "history", data + "_" + text);
                }
            }
            Log.d("history activity", "保存本地数据    ==   " + SharepreferenceUtils.getString(AppContext.instance().loginUser.getUserName() + "history", null));
            Log.d("history activity", "保存数据    ==   " + historyDatas.toString());
            //发送意图到activity
            Intent intent = new Intent(SearchHistoryActivity.this, SearchResultActivity.class);
            intent.putExtra("queryContent", text);
            intent.putExtra("contentId", contentId);
            startActivity(intent);
        }

        @Override
        public void onShowHistoryDatas() {
            //用于显示历史搜索界面
            if(!TextUtils.isEmpty(SharepreferenceUtils.getString(AppContext.instance().loginUser.getUserName() + "history", null)))
                mShowHistory.setVisibility(View.VISIBLE);
        }

        @Override
        public void onChangeCurrent(int position) {
            contentId = "0000" + (position + 1);
            if(dbData != null)
                dbData.clear();
            requestPager = 1;
            getDbData();
        }
    };
    /**
     * 获取服务器新闻的标题数据的 数据
     */
    private void getDbData() {
        service.getSearchNews(contentId, contentId, null, requestPager, 20);
    }

    /**
     * 获取自动补全data 和adapter
     */
    private void getAutoCompleteData(String text) {
        if (autoCompleteData == null) {
            //初始化
            autoCompleteData = new ArrayList<>();
        } else {
            // 根据text 获取auto data
            autoCompleteData.clear();
            if(dbData != null){
                for (int i = 0; i < dbData.size(); i++) {
                    if (dbData.get(i).contains(text.trim())) {
                        autoCompleteData.add(dbData.get(i));
                    }
                }
            }
        }
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_autocomplete, autoCompleteData );
        } else {
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }


    private class HistoryAdapter extends CommonAdapter<String>{

        public HistoryAdapter(Context context, List<String> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public int getCount() {
            if(historyDatas != null) {
                if(mShowMoreView == null || mTextViewMore == null){
                    return super.getCount();
                }
                if (mTextViewMore.getText().toString().trim().equals(getString(R.string.show_more_search_history))) {
                    return showNumHistoryItem;
                }
            }
            return super.getCount();
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            TextView itemView = (TextView) holder.getView(R.id.txt_history_item);
            itemView.setText(s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(historyDatas.size() > 0 )
            mShowHistory.setVisibility(View.VISIBLE);
        //同时隐藏自动补齐的导航；
        mSearchView.setTipListGone();
        if(historyDatas != null && historyDatas.size() >showNumHistoryItem) {
            if(mShowMoreView == null){
                getShowMoreView();
            }
        }

        if(mHistoryAdapter != null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    private ListView.OnItemClickListener mHistoryItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSearchView.setEditInputContent(historyDatas.get(position).toString().trim());
        }
    };


    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.NOTIFY_GET_SEARCH_RUSULT:
            case HttpConstant.NOTIFY_GET_SEARCH_OCCSION:
            case HttpConstant.NOTIFY_GET_SEARCH_HOTSPOT:
                if(getActivityisShow()) {
                    List<NewEntity> database = (List<NewEntity>) o;
                    if(database == null || database.size() == 0) return;
                    if(dbData == null){
                        dbData= new ArrayList<String>();
                    }
                    int TotalPager = 1;
                    for (int i = 0; i < database.size(); i++) {
                        if (i == 0) {
                            TotalPager = database.get(0).getTotalPager();
                        }
                        String title = database.get(i).getTitle();
                        dbData.add(title);
                    }
                    // 是否是最后一页的数据
                    if(requestPager != TotalPager){
                        requestPager ++;
                        //获取下一页的数据；
                        getDbData();
                    }
                }

                break;
            default:
                break;
        }
    }

    /**
     * @return 当前的activity 是否在当前界面显示
     */
    private Boolean getActivityisShow(){
        boolean result = false;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn != null) {
            if (getClass().getName().equals(cn.getClassName())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back_text_search:
                finish();
                break;

            case R.id.txt_clear_history:
                //清空搜索的记录
                SharepreferenceUtils.writeString(AppContext.instance().loginUser.getUserName() + "history", null);
                if(historyDatas.size() > showNumHistoryItem)
                    mListView.removeFooterView(mShowMoreView);
                historyDatas.clear();
;               mShowHistory.setVisibility(View.GONE);
                mShowMoreView = null;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }
    private void clear(){
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_SEARCH_RUSULT,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_SEARCH_OCCSION,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_SEARCH_HOTSPOT,this);
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_OPERATE, this);
    }
}
