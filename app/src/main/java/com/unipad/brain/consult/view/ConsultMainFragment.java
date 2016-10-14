package com.unipad.brain.consult.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.ConsultBaseFragment;
import com.unipad.brain.consult.entity.AdPictureBean;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.brain.consult.widget.CustomViewPager;
import com.unipad.brain.consult.widget.RecommendGallery;
import com.unipad.brain.consult.widget.RecommendPot;
import com.unipad.brain.home.MainBasicFragment;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.DensityUtil;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 63 on 2016/6/20.
 */
public class ConsultMainFragment extends ConsultBaseFragment implements IDataObserver {
    private TabWidget mTabWidget;
    private CustomViewPager mViewPager;
    private int mCurrentIndex;
    private ConsultTab[] mConsultTabs;
    private Button mSearchView;
    private PopupWindow mPopupWindows;
    private List<String> result;
    private ArrayAdapter adapter;
    private List<AdPictureBean> newsAdvertDatas;
    private ImageOptions imageOptions;
    private NewsService service;
    private Boolean isNoAdvertData;
    private AdViewPagerAdapter adAdapter;
//    private ImageView mBtnSearch;

    @Override
    public  int getLayoutId(){
        return R.layout.fragment_consult_main;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mTabWidget = (TabWidget) view.findViewById(R.id.tabwidget_consult_main);
        mViewPager = (CustomViewPager) view.findViewById(R.id.viewPager_consult);
        mSearchView = (Button) view.findViewById(R.id.searchview_search_bar);
//        mLunBoPic = (RelativeLayout) view.findViewById(R.id.rl_advert_view);
//        mBtnSearch = (ImageView) view.findViewById(R.id.btn_start_search);
        result = new ArrayList<String>();
        newsAdvertDatas = new ArrayList<AdPictureBean>();
        isNoAdvertData = false;
        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_ADVERT, this);
//        service.registerObserver(HttpConstant.NOTIFY_GET_HOTADVERT, this);
        //初始化轮播图
        initLunPic(view);
        startLunPic();

        initMyTabWidget();
        initViewPager();
        initSearchView();
    }

    private void initMyTabWidget(){
           /*去掉分割线*/
//        mTabWidget.setDividerDrawable(R.drawable.line_location);
        mConsultTabs = ConsultTab.values();
        LayoutInflater layoutInflater = LayoutInflater.from(getmContext());
        if(mConsultTabs != null){
            for (int i = 0; i < mConsultTabs.length; i++) {
                ConsultTab enumTab = mConsultTabs[i];
                if(enumTab == null){
                    continue;
                }
                View tabWidgetItem = layoutInflater.inflate(R.layout.layout_tabwidget_item, null);
                TextView tvTabItem = (TextView) tabWidgetItem.findViewById(R.id.tv_tab_item);
                View tabSelectView = tabWidgetItem.findViewById(R.id.tab_select_view_bg);
                tvTabItem.setText(enumTab.getType());

                if(i == 0){
                    tabWidgetItem.setBackgroundResource(R.color.consult_tab_select_bg);
                    tabSelectView.setVisibility(View.VISIBLE);
                    service.getAdverts(ConsultTab.INTRODUCATION.getTypeId());
                }else{
                    tabWidgetItem.setBackgroundResource(R.color.consult_tab_normal_bg);
                    tabSelectView.setVisibility(View.GONE);
                }

                tabWidgetItem.setTag(i);
                mTabWidget.addView(tabWidgetItem);
                tabWidgetItem.setOnClickListener(mTabClickListener);

            }
        }
        mTabWidget.setCurrentTab(0);

    }
    private void startLunPic(){
        //加载图片中。。。
        imageOptions = new ImageOptions.Builder()
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                        //设置加载过程中的图片
                .setLoadingDrawableId(R.drawable.default_advert_pic)
                        //设置加载失败后的图片
                .setFailureDrawableId(R.drawable.default_advert_pic)
                        //设置使用缓存
                .build();
    }

    private void initLunPic(View view){
        //广告轮播图;
        RecommendGallery mAdvertLuobo = (RecommendGallery) view.findViewById(R.id.point_gallery);
        //轮播图的点的视图;
        RecommendPot adPotView = (RecommendPot) view.findViewById(R.id.ad_pot);
        newsAdvertDatas.add(new AdPictureBean
                ("http://221.5.109.34/crazybrain-mng/image/getFile?filePath=/api/20160907/983884F02F1D.jpg"));
        adPotView.setIndicatorChildCount(newsAdvertDatas.size());
        mAdvertLuobo.initSelectePoint(adPotView);
        mAdvertLuobo.setOnItemClickListener(mOnItemClickListener);
        adAdapter = new AdViewPagerAdapter(getActivity(),newsAdvertDatas, R.layout.ad_gallery_item);
        mAdvertLuobo.setAdapter(adAdapter);

    }

    @Override
    public void update(int key, Object o) {
        switch (key){
            case HttpConstant.NOTIFY_GET_ADVERT :
                if(null == o ) return;
                if(((List<AdPictureBean>)o).size() == 0){
                    //服务器数据为null 没有数据 打开的页面 公司页面
                    isNoAdvertData = true;
                    return;
                }
                //获取轮播图数据
                newsAdvertDatas.clear();
                newsAdvertDatas.addAll((List<AdPictureBean>) o);
                adAdapter.notifyDataSetChanged();
                break;
        }
    }

    /*广告轮播图的 adapter*/
    class AdViewPagerAdapter extends CommonAdapter<AdPictureBean> {
        public AdViewPagerAdapter(Context context, List<AdPictureBean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final AdPictureBean adPictureBean) {
            ImageView imageView = holder.getView(R.id.ad_gallery_item);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if(TextUtils.isEmpty(adPictureBean.getJumpType())){
                imageView.setImageResource(R.drawable.default_advert_pic);
            } else {
                x.image().bind(imageView, adPictureBean.getAdvertPath(), imageOptions);
            }

        }
    }
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AdPictureBean bean = newsAdvertDatas.get(position);
            if (bean.getJumpType() != null) {
                if (bean.getJumpType().equals("0")) {
                    //本页面打开 发送意图
                    Intent intent = new Intent(getmContext(), PagerDetailActivity.class);
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
            } else {
                if(isNoAdvertData){  //没有广告数据  打开公司网页
                    Intent intent = new Intent(getmContext(), PagerDetailActivity.class);
                    //公司网址
                    intent.putExtra("pagerId", "https://www.sogou.com/");
                    intent.putExtra("isAdvert", true);
                    startActivity(intent);
                } else { //刷新广告数据
                    service.getAdverts(ConsultTab.INTRODUCATION.getTypeId());
                }
            }
        }
    };


    private void initSearchView(){
//        //初始化搜索栏；图标在搜索栏的编辑框外
//        mSearchView.setIconifiedByDefault(false);
//        //显示搜索按钮
//        mSearchView.setSubmitButtonEnabled(false);
//        mSearchView.onActionViewExpanded();
//        mSearchView.clearFocus();
//        mSearchView.setQueryHint(getString(R.string.search_conment));
//        try {
//            Field field = mSearchView.getClass().getDeclaredField("mSearchPlate");
//            field.setAccessible(true);
//            View mSearchButton = (View) field.get(mSearchView);
//            //设置搜索的 button 背景图片  去掉下划线
//            mSearchButton.setBackgroundColor(Color.TRANSPARENT);
////            mSearchButton.setPadding(0,0,0,3);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        /*去掉放大镜icon*/
//        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
//        ImageView search_icon = (ImageView) mSearchView.findViewById(magId);
//        search_icon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
//        search_icon.setVisibility(View.GONE);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.clearFocus();
                //发送意图到activity
                Intent intent = new Intent(getmContext(), SearchHistoryActivity.class);
                int contentId = mCurrentIndex + 1;
                intent.putExtra("contentId", "0000" + contentId);
                startActivity(intent);
            }
        });
//        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
//        mBtnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!TextUtils.isEmpty(mSearchView.getQuery())){
//                    Log.e(getTag(), mSearchView.getQuery().toString());
//                    startSearchQueryText(mSearchView.getQuery().toString());
//                }
//            }
//        });
    }

    private void initViewPager(){
        FragmentPagerAdapter adapter = new ChildsFragmentAdapter(getChildFragmentManager()
                );
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mSimpleOnPageChangeListener);
    }

    private ViewPager.SimpleOnPageChangeListener mSimpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        public void onPageSelected(int position) {
        if(position == mCurrentIndex)return;
        setTabSelectedChanged(mCurrentIndex, false);
        mCurrentIndex = position;
        setTabSelectedChanged(mCurrentIndex, true);
        }
    };

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            if (index == mCurrentIndex) {
                return;
            }
            mViewPager.setCurrentItem(index);
            setTabSelectedChanged(mCurrentIndex, false);
            mCurrentIndex = index;
            setTabSelectedChanged(mCurrentIndex, true);
        }

    };

    /**
      * @param index
     * @param selected
     */
    private void setTabSelectedChanged(int index, boolean selected){
        View itemView = mTabWidget.getChildTabViewAt(index);
        itemView.findViewById(R.id.tab_select_view_bg).setVisibility(selected ? View.VISIBLE : View.GONE);
        itemView.setBackgroundResource(selected ? R.color.consult_tab_select_bg : R.color.consult_tab_normal_bg);
    }

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener(){
        //提交按钮之后  调用该方法
        @Override
        public boolean onQueryTextSubmit(String query) {

            startSearchQueryText(query);
            return true;
        }

        //当有文字输入的时候调用该方法；
        @Override
        public boolean onQueryTextChange(String newText) {
            if(TextUtils.isEmpty(newText)){
                closePopup();
                return false;
            }
            MainBasicFragment baseFragment = (MainBasicFragment) com.unipad.brain.consult.Manager.
                    FragmentManager.getFragment(mConsultTabs[mCurrentIndex]);
            List<String> titleTip = baseFragment.getNewsDatas();

            if(null == titleTip){
                return true;
            }

            if(result == null){
                result = new ArrayList<String>();
            }
            result.clear();

            for(int i = 0; i < titleTip.size(); i++){
                String searchResult = titleTip.get(i);
                if(searchResult.contains(newText)){
                    //如果有搜索的内容出现
                    result.add(searchResult);
                }
            }

            if(result.size() > 0 ){
                adapter = new ArrayAdapter(getmContext(), R.layout.list_item_searchlist, result);
                showSelectDDialog(adapter);
            }
            return true;
        }
    } ;

    /**
     * 弹出选择
     */
    private void showSelectDDialog(BaseAdapter baseAdapter) {
        ListView lv = new ListView(getmContext());
        lv.setBackgroundResource(R.drawable.icon_spinner_listview_background);
        // 隐藏滚动条
        lv.setVerticalScrollBarEnabled(false);

        lv.setCacheColorHint(Color.parseColor("#00000000"));
        // 让listView没有分割线
        lv.setDividerHeight(0);
        lv.setDivider(null);
        //lv.setId(10001);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindows.setFocusable(true);
                mOnQueryTextListener.onQueryTextSubmit(result.get(position).toString());
            }
        });
        if(lv.getAdapter() != null){
            baseAdapter.notifyDataSetChanged();
        } else {
            lv.setAdapter(baseAdapter);
        }
        ScaleAnimation sa = new ScaleAnimation(1f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        sa.setDuration(300);
        lv.startAnimation(sa);


        mPopupWindows = new PopupWindow(lv, mSearchView.getWidth() - DensityUtil.dip2px(getmContext(),40), DensityUtil.dip2px(getmContext(),240));
        // 设置点击外部可以被关闭
        mPopupWindows.setOutsideTouchable(true);
        mPopupWindows.setBackgroundDrawable(new BitmapDrawable());

        // 设置popupWindow可以得到焦点
        mPopupWindows.setFocusable(false);
        mPopupWindows.showAsDropDown(mSearchView, DensityUtil.dip2px(getmContext(), 20), DensityUtil.dip2px(getmContext(), -5));		// 显示
        mSearchView.setFocusable(true);

    }
    private void startSearchQueryText(String query){
        //强制隐藏软键盘；
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        mSearchView.clearFocus();
        //发送意图到activity
        Intent intent = new Intent(getmContext(), SearchResultActivity.class);
        intent.putExtra("queryContent", query);
        int contentId = mCurrentIndex + 1;
        intent.putExtra("contentId", "0000" + contentId);
        startActivity(intent);
    }

    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
            com.unipad.brain.consult.Manager.FragmentManager.maps.clear();
            clear();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    class ChildsFragmentAdapter extends FragmentPagerAdapter {
        public ChildsFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            initTabs();
            return com.unipad.brain.consult.Manager.FragmentManager.getFragment(mConsultTabs[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            initTabs();
            return mConsultTabs[position].getType();
        }

        @Override
        public int getCount() {
            initTabs();
            return mConsultTabs.length;
        }
    }

    private void initTabs() {
        if (mConsultTabs == null) {
            mConsultTabs = ConsultTab.values();
        }
    }

    //关闭弹出窗体
    private void closePopup(){
        if(mPopupWindows != null && mPopupWindows.isShowing()){
            mPopupWindows.dismiss();
        }
    }
    private void clear() {
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_ADVERT, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.searchview_search_bar);
        search.collapseActionView();
        //是搜索框默认展开
         search.expandActionView();
         super.onCreateOptionsMenu(menu, inflater);
    }

}
