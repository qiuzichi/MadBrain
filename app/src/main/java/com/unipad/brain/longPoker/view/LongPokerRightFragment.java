package com.unipad.brain.longPoker.view;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.home.util.SharedPreferencesUtil;
import com.unipad.brain.longPoker.IProgress;
import com.unipad.brain.longPoker.adapter.HorRecycleAdapter;
import com.unipad.brain.longPoker.adapter.NormalSpinerAdapter;
import com.unipad.brain.longPoker.adapter.OnePokerRecycleAdapter;
import com.unipad.brain.longPoker.dao.LongPokerService;
import com.unipad.common.BasicCommonFragment;
import com.unipad.common.Constant;
import com.unipad.utils.LogUtil;
import com.unipad.utils.SharepreferenceUtils;

import java.util.ArrayList;

/**
 * Created by gongkan on 2016/7/13.
 */
public class LongPokerRightFragment extends BasicCommonFragment implements IProgress {
    private RecyclerView recyclerView;
    private LongPokerService service;
    private RecyclerView[] rememoryPokerRecycle;
    private View shadeView;
    private ViewPager viewpager;
   // private GridLayoutManager layoutManager;
    private LinearLayout memoryLayout;
    @Override
    public int getLayoutId() {
        return R.layout.long_poker_frg_right;
    }

    private ListView dianListView;

    private NormalSpinerAdapter listAdapter;

    private TextView numTextView;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) mViewParent.findViewById(R.id.recycle_view);

        service = (LongPokerService) AppContext.instance().getService(Constant.LONG_POKER_SERVICE);
        //setMemoryAdapter();
        shadeView = mViewParent.findViewById(R.id.view_shade_answer);
        memoryLayout = (LinearLayout) mViewParent.findViewById(R.id.browse_proker_hlayout);

    }
    @Override
    public void startMemory() {
        //mViewParent.addView(memoryLayout,0);
        shadeView.setVisibility(View.GONE);
    }

    @Override
    public void startRememory() {
        super.startRememory();
        setRememoryAdapter();
    }

    @Override
    public void initDataFinished() {
        super.initDataFinished();
        setMemoryAdapter();
    }

    @Override
    public void pauseGame() {
        super.pauseGame();
        shadeView.setVisibility(View.VISIBLE);
    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        shadeView.setVisibility(View.GONE);
    }

    private GridLayoutManager createGridLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 53 == 0) {
                    return 2;
                } else {
                    return 1;
                }

            }
        });
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        return layoutManager;
    }
    private void moveToPosition(int n,GridLayoutManager mLinearLayoutManager,RecyclerView recyclerView) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(n);
        }else if ( n <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = recyclerView.getChildAt(n - firstItem).getTop();
            recyclerView.scrollBy(0, top);
        }else{
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            // move = true;
        }

    }

    private void setRememoryAdapter() {
        ViewStub viewStub = (ViewStub) mViewParent.findViewById(R.id.browse_proker_muti_stub);
        View view = viewStub.inflate();
        viewpager = (ViewPager) view.findViewById(R.id.rememory_viewpager);
        dianListView = (ListView) view.findViewById(R.id.poker_dianshu_listview);
        numTextView = (TextView) view.findViewById(R.id.poker_id_tip);
        numTextView.setText(mActivity.getResources().getString(R.string.long_poker_answer_num_tip,1));
        dianListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecyclerView v = rememoryPokerRecycle[viewpager.getCurrentItem()];
                OnePokerRecycleAdapter recycleAdapter = (OnePokerRecycleAdapter) v.getAdapter();

                int current = recycleAdapter.getCurrent();
                OnePokerRecycleAdapter.APokerViewHolder viewholderCurrent = (OnePokerRecycleAdapter.APokerViewHolder) v.findViewHolderForAdapterPosition(current);
                if (viewholderCurrent != null) {
                    recycleAdapter.onItemClick(position, (TextView) viewholderCurrent.view.findViewById(R.id.tv_value));
                }
                if (recycleAdapter.getPositionItem(current).getUserAnswer() == 0){
                    return;
                }
                int next = recycleAdapter.getCurrent();
                if (next > 53) {
                    numTextView.setText("");
                }else{
                    numTextView.setText(mActivity.getResources().getString(R.string.long_poker_answer_num_tip,recycleAdapter.getCurrent()));
                }
                OnePokerRecycleAdapter.APokerViewHolder viewholderNext = (OnePokerRecycleAdapter.APokerViewHolder) v.findViewHolderForAdapterPosition(++current);
                if (null == viewholderNext) {
                    int lastVisibleItem = ((GridLayoutManager) v.getLayoutManager()).findLastVisibleItemPosition() - 2;
                    moveToPosition(lastVisibleItem,(GridLayoutManager)v.getLayoutManager(),v);
                } else {
                    viewholderNext.view.findViewById(R.id.tv_value).requestFocus();
                }

            }
        });
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.e("","pager on onPageSelected " +position);
                for(int i =0;i<rememoryPokerRecycle.length;i++){
                    RecyclerView v = rememoryPokerRecycle[i];
                    OnePokerRecycleAdapter recycleAdapter = (OnePokerRecycleAdapter) v.getAdapter();
                    if (recycleAdapter == null){
                        recycleAdapter= new OnePokerRecycleAdapter(getActivity(), service.pokersQuestion, position * 53, 53,numTextView);
                        recycleAdapter.setProgress(LongPokerRightFragment.this);
                        v.setAdapter(recycleAdapter);
                    }
                    if (position == i){
                        recycleAdapter.setIsCurrentPage(true);
                        int current = recycleAdapter.getCurrent();
                        OnePokerRecycleAdapter.APokerViewHolder viewholderCurrent = (OnePokerRecycleAdapter.APokerViewHolder) v.findViewHolderForAdapterPosition(current);
                        if (viewholderCurrent != null){
                            viewholderCurrent.view.findViewById(R.id.tv_value).requestFocus();
                        }
                        numTextView.setText(mActivity.getResources().getString(R.string.long_poker_answer_num_tip,current));
                    }else{
                        recycleAdapter.setIsCurrentPage(false);
                    }
                }




            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        listAdapter = new NormalSpinerAdapter(mActivity);
        dianListView.setAdapter(listAdapter);
        rememoryPokerRecycle = new RecyclerView[service.howMany];

        for (int i = 0; i < service.howMany; i++) {
            RecyclerView recyclerView = new RecyclerView(getActivity());
            recyclerView.setLayoutManager(createGridLayoutManager());
            recyclerView.setItemAnimator(new NoAlphaItemAnimator());
            rememoryPokerRecycle[i] = recyclerView;
        }
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return rememoryPokerRecycle.length;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(rememoryPokerRecycle[position]);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                RecyclerView recyclerView = rememoryPokerRecycle[position];
                if (recyclerView.getAdapter() == null) {
                    LogUtil.e("", "viewpager postion = " + position);
                    OnePokerRecycleAdapter onePokerRecycleAdapter = new OnePokerRecycleAdapter(getActivity(), service.pokersQuestion, position * 53, 53,numTextView);
                    onePokerRecycleAdapter.setProgress(LongPokerRightFragment.this);
                    recyclerView.setAdapter(onePokerRecycleAdapter);
                }
                container.addView(rememoryPokerRecycle[position]);


                return rememoryPokerRecycle[position];
            }

        };

        viewpager.setAdapter(pagerAdapter);
        //设置布局管理器

    }

    private void setMemoryAdapter() {
        HorRecycleAdapter recycleAdapter = new HorRecycleAdapter(getActivity(), service.pokersQuestion);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置item间隙
        recyclerView.addItemDecoration(new SpaceItemDecoration(5, -150,5, SharepreferenceUtils.getInt(mActivity.getProjectId()+"_dividemode",3)));
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        //设置Adapter
        recyclerView.setAdapter(recycleAdapter);
       // mViewParent.removeView(memoryLayout);
    }

    @Override
    public void rememoryTimeToEnd(int answerTime) {
        super.rememoryTimeToEnd(answerTime);
    }

    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
        clearMemoryRecycle();
        sendMsgToPreper();
    }

    private void clearMemoryRecycle() {
        ((HorRecycleAdapter) recyclerView.getAdapter()).clear();
        recyclerView.setAdapter(null);
        mViewParent.removeView(memoryLayout);
        memoryLayout =null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
