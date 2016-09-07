package com.unipad.brain.consult.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.unipad.brain.R;
import com.unipad.brain.consult.ConsultBaseFragment;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.brain.consult.widget.CustomViewPager;
import com.unipad.brain.home.MainBasicFragment;
import com.unipad.utils.DensityUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 63 on 2016/6/20.
 */
public class ConsultMainFragment extends ConsultBaseFragment{
    private TabWidget mTabWidget;
    private CustomViewPager mViewPager;
    private int mCurrentIndex;
    private ConsultTab[] mConsultTabs;
    private SearchView mSearchView;
    private PopupWindow mPopupWindows;
    private List<String> result;
    private ArrayAdapter adapter;


    @Override
    public  int getLayoutId(){
        return R.layout.fragment_consult_main;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mTabWidget = (TabWidget) view.findViewById(R.id.tabwidget_consult_main);
        mViewPager = (CustomViewPager) view.findViewById(R.id.viewPager_consult);
        mSearchView = (SearchView) view.findViewById(R.id.searchview_search_bar);
        result = new ArrayList<String>();

        initMyTabWidget();
        initViewPager();
        initSearchView();
    }

    private void initMyTabWidget(){
           /*去掉分割线*/
        mTabWidget.setDividerDrawable(null);
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
                tvTabItem.setText(enumTab.getType());
                if(i == 0){
                    tabWidgetItem.setBackgroundColor(Color.RED);
                }else{
                    tabWidgetItem.setBackgroundResource(R.color.main_1);
                }

                tabWidgetItem.setTag(i);
                mTabWidget.addView(tabWidgetItem);
                tabWidgetItem.setOnClickListener(mTabClickListener);

            }
        }
        mTabWidget.setCurrentTab(0);

    }

    private void initSearchView(){
        //初始化搜索栏；
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.clearFocus();
        mSearchView.setQueryHint(getString(R.string.search_conment));
        try {
            Field field = mSearchView.getClass().getDeclaredField("mSubmitButton");
            field.setAccessible(true);
            ImageView mSearchButton = (ImageView) field.get(mSearchView);
            //设置搜索的 button 背景图片
            mSearchButton.setImageResource(R.drawable.search_button_blue);
            mSearchButton.setPadding(0,0,0,3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
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

    private void setTabSelectedChanged(int index, boolean selected){
        View itemView = mTabWidget.getChildTabViewAt(index);
        itemView.setBackgroundResource(selected ? R.color.red : R.color.main_1);
    }

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener(){
        //提交按钮之后  调用该方法
        @Override
        public boolean onQueryTextSubmit(String query) {
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


    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
            com.unipad.brain.consult.Manager.FragmentManager.maps.clear();

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
