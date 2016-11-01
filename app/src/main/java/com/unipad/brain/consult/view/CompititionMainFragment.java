package com.unipad.brain.consult.view;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.ConsultBaseFragment;
import com.unipad.brain.consult.entity.ListAreaEnum;
import com.unipad.brain.consult.entity.ListCompetitionEnum;
import com.unipad.brain.dialog.ShowDialog;
import com.unipad.brain.home.bean.CompetitionBean;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.ToastUtil;
import org.xutils.common.util.DensityUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 63 on 2016/6/28.
 */
public class CompititionMainFragment extends ConsultBaseFragment implements IDataObserver, ShowDialog.OnShowDialogClick {

    private NewsService service;
    private List<CompetitionBean> mNewCompetitionDatas = new ArrayList<CompetitionBean>();
    private NewCompetitionAdapter mNewCompetitionAdapter;
    private ShowDialog showDialog;
    private TextView tv_error;
    private int requestPagerNum;
    private int requestRefreshDatas;
    private final int permaryDataNumber = 10;
    private final int DELAYETIMEOUT = 12000;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private View mListViewFooter;
    private Boolean isLoading;
    private boolean isUpMove;
    private float downY;
    private boolean isActionDown;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_compitition_main;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mNewCompetitionDatas = new ArrayList<CompetitionBean>();

        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_NEWCOMPETITION, this);
        service.registerObserver(HttpConstant.NOTIFY_APPLY_NEWCOMPETITION, this);

        mListView = (ListView) view.findViewById(R.id.listview_compitition_main);
        tv_error = (TextView) view.findViewById(R.id.tv_load_error_show);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_newcompetition);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue2,
                R.color.red,
                R.color.stroke_color,
                R.color.black
        );
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dip2px(24));

        //默认加载第一页的数据 10条 分页加载数据
        service.getNewCompetition(AppContext.instance().loginUser.getUserId(), null, null, requestPagerNum = 1, permaryDataNumber);
        //下拉动画生效 初始化之后开始播放下拉刷新
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
//        mSwipeRefreshLayout.setRefreshing(false);

        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        mNewCompetitionAdapter = new NewCompetitionAdapter(getmContext(), mNewCompetitionDatas, R.layout.layout_compition_info_item);
        mListView.setAdapter(mNewCompetitionAdapter);
        initEvent();

    }

    private void initEvent() {
         /*避免出现item太大 之后 避免冲突scroll*/
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
                                if (canLoad()) {
                                    if (mNewCompetitionDatas.size() != 0) {
                                        int TotalPager = mNewCompetitionDatas.get(0).getTotalPage();
                                        if (requestPagerNum > TotalPager) {
                                            ToastUtil.showToast(getString(R.string.loadmore_null_data));
                                            isUpMove = false;
                                        } else if(isLoading) {
                                                ToastUtil.showToast(getString(R.string.loadmore_data));
                                                isUpMove = false;
                                        } else {
                                            setLoading(true);
                                            getNewCompetition(AppContext.instance().loginUser.getUserId(), null, null, requestPagerNum, permaryDataNumber);
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

//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//                switch (i) {
//                    // 当滚动时
//                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//                        // 判断滚动到底部
//                        if (canLoad()) {
//                            if (requestPagerNum > mNewCompetitionDatas.get(0).getTotalPage()) {
//                                ToastUtil.showToast(getString(R.string.loadmore_null_data));
//                                return;
//                            } else {
//                                if (isLoading) {
//                                    ToastUtil.showToast(getString(R.string.loadmore_data));
//                                    return;
//                                }
//                                setLoading(true);
//                                getNewCompetition(AppContext.instance().loginUser.getUserId(), null, null, requestPagerNum, permaryDataNumber);
//                            }
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0)
//                    /*第一项可见 的时候 才可以响应swipe的滑动刷新事件*/
//                    mSwipeRefreshLayout.setEnabled(true);
//                else {
//                    mSwipeRefreshLayout.setEnabled(false);
//                }
//
//            }
//        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNewCompetitionDatas.clear();
                //默认加载第一页的数据 10条 分页加载数据
                service.getNewCompetition(AppContext.instance().loginUser.getUserId(), null, null, requestRefreshDatas = 1, permaryDataNumber);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, DELAYETIMEOUT);
            }
        });

        tv_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //点击重新加载数据
                getNewCompetition(AppContext.instance().loginUser.getUserId(), null, null, requestPagerNum = 1, permaryDataNumber);
            }
        });
    }

    private void getNewCompetition(String userId, String projectId, String gradeId, int page, int size){
        service.getNewCompetition(userId, projectId, gradeId, page, size);
        ToastUtil.createWaitingDlg(getmContext(), null, Constant.LOGIN_WAIT_DLG).show(15);
    }

    private void initFooterView(){
        if(mListViewFooter == null){
            mListViewFooter = View.inflate(getmContext(), R.layout.recycler_footer_layout, null);
            mListViewFooter.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, com.unipad.utils.DensityUtil.dip2px(getmContext(),80)));
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
            mListView.setAdapter(mNewCompetitionAdapter);
        } else {
            mListView.removeFooterView(mListViewFooter);
        }

    }

    private class NewCompetitionAdapter extends CommonAdapter<CompetitionBean> {

        public NewCompetitionAdapter(Context context, List<CompetitionBean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(final ViewHolder holder, final CompetitionBean competitionBean) {
            //比赛项目名称
            TextView race_model = (TextView) holder.getView(R.id.tv_competion_info_model_item);
            race_model.setText(Constant.getProjectName(competitionBean.getProjectId()));
            ListCompetitionEnum projectEnum = null;
            if (Constant.GAME_QUICKIY_POCKER.equals(competitionBean.getProjectId())) {
                projectEnum = ListCompetitionEnum.values()[9];
            } else {
                projectEnum = ListCompetitionEnum.values()[Integer.parseInt(competitionBean.getProjectId().substring(competitionBean.getProjectId().lastIndexOf("0") + 1)) - 1];
            }
            Drawable drawable_model = getResources().getDrawable(projectEnum.getLabelResId());
            drawable_model.setBounds(0, 0, drawable_model.getMinimumWidth(), drawable_model.getMinimumHeight());
            race_model.setCompoundDrawables(null, drawable_model, null, null);


            //比赛区域
            TextView race_gradle = (TextView) holder.getView(R.id.tv_competion_info_area_item);
            ListAreaEnum areaEnum = ListAreaEnum.values()[Integer.parseInt(competitionBean.getGradeId().substring(competitionBean.getGradeId().lastIndexOf("0") + 1)) - 1];
            Drawable drawable = getResources().getDrawable(areaEnum.getLabelResId());
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            race_gradle.setText(areaEnum.getNameResId());
            race_gradle.setCompoundDrawables(null, drawable, null, null);
            //设置参赛日期  2016.07.08
            ((TextView) holder.getView(R.id.tv_competion_info_date_item)).setText(competitionBean.getCompetitionDate());
            //参赛时间
            ((TextView) holder.getView(R.id.tv_competion_info_time_item)).setText(competitionBean.getCompetitionTime());
            //比赛费用
            ((TextView) holder.getView(R.id.tv_competion_info_fee_item)).setText("￥ " + competitionBean.getCost());
            //选手报名
            final Button btn_apply = (Button) holder.getView(R.id.btn_input_competition);

            if (0 == competitionBean.getApplyState()) {  //选手报名
                btn_apply.setBackgroundResource(R.drawable.entry_btn_shape);
                btn_apply.setText(getString(R.string.person_entry_fee));

                btn_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        service.getApplyCompetition(AppContext.instance().loginUser.getUserId(), HttpConstant.NOTIFY_APPLY_NEWCOMPETITION,
                                competitionBean.getId(), competitionBean.getProjectId(), competitionBean.getGradeId(), 0);
                    }
                });
            } else {  //已报名
                btn_apply.setBackgroundResource(R.drawable.entryed_btn_shape);
                btn_apply.setText(getString(R.string.applied));
                btn_apply.setOnClickListener(null);
            }
        }
    }


    @Override
    public void update(int key, Object o) {
        switch (key) {
            case HttpConstant.NOTIFY_GET_NEWCOMPETITION:
                //获取数据  关闭dialog
                HIDDialog.dismissAll();
                isUpMove = false;
                mSwipeRefreshLayout.setRefreshing(false);
                setLoading(false);
                List<CompetitionBean> databean = (List<CompetitionBean>) o;
                if(databean.size() == 0){
                    if(mNewCompetitionDatas.size() ==0 ){
                        tv_error.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                    }
                    return;
                }


                tv_error.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
//                if (mNewCompetitionDatas.size() != 0) {
//                    for (int i = databean.size()-1; i >= 0; i--) {
//                        for (int j = 0; j < mNewCompetitionDatas.size(); j++) {
//                            if (databean.get(i).equals(mNewCompetitionDatas.get(j))) {
//                                break;
//                            } else {
//                                if (j == mNewCompetitionDatas.size() - 1) {
//                                        //不同 则是新数据
//                                    if(requestRefreshDatas  == 1 && i<mNewCompetitionDatas.size()){
//                                        mNewCompetitionDatas.add(i,databean.get(i));
//                                        requestRefreshDatas = 0;
//                                    } else {
//                                        mNewCompetitionDatas.add(databean.get(i));
//                                    }
//                                    break;
//                                }
//                                continue;
//                            }
//                        }
//                    }
//                } else {
                    mNewCompetitionDatas.addAll(databean);
//                }

                if(requestPagerNum <= mNewCompetitionDatas.get(0).getTotalPage()){
                    requestPagerNum++;
                }

                if(requestPagerNum > mNewCompetitionDatas.get(0).getTotalPage() && mListView.getFooterViewsCount() == 0){
                    View footerView = View.inflate(getmContext(), R.layout.recycler_footer_layout, null);
                    footerView.setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT, com.unipad.utils.DensityUtil.dip2px(getmContext(), 80)));
                    ((ProgressBar) footerView.findViewById(R.id.progressbar_loadmore_footer)).setVisibility(View.GONE);
                    TextView text_loadMore = (TextView) footerView.findViewById(R.id.tv_item_introduction_footer);
                    text_loadMore.setText(getString(R.string.no_available));
                    text_loadMore.setTextColor(getResources().getColor(R.color.stroke_color));
                    mListView.addFooterView(footerView);
                }
                mNewCompetitionAdapter.notifyDataSetChanged();

                break;

            case HttpConstant.NOTIFY_APPLY_NEWCOMPETITION:
                CompetitionBean bean = (CompetitionBean) o;
                if (null != bean) {
                    for (int i = 0; i < mNewCompetitionDatas.size(); i++) {
                        CompetitionBean compet = mNewCompetitionDatas.get(i);
                        if (compet.getId().equals(bean.getId())) {
                            compet.setApplyState(bean.getApplyState());
                            break;
                        } else {
                            continue;
                        }
                    }
                    mNewCompetitionAdapter.notifyDataSetChanged();
                } else { //用户没有实名认证
                    if (AppContext.instance().loginUser.getAuth() == 0 || AppContext.instance().loginUser.getAuth() == 3) {
                        View dialogView = View.inflate(getmContext(), R.layout.first_login_dialog, null);
                        TextView txt_msg = (TextView) dialogView.findViewById(R.id.txt_msg);
                        txt_msg.setText(AppContext.instance().loginUser.getAuth() == 0 ? this.getString(R.string.auth_hint) : this.getString(R.string.auth_fail_hint));
                        showDialog = new ShowDialog(getmContext());
                        showDialog.showDialog(dialogView, ShowDialog.TYPE_CENTER, getActivity().getWindowManager(), 0.4f, 0.5f);
                        showDialog.setOnShowDialogClick(this);
                        showDialog.bindOnClickListener(dialogView, new int[]{R.id.img_close});
                    } else {
                        ToastUtil.showToast(getString(R.string.submit_fail));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
    }

    @Override
    public void dialogClick(int id) {
        if (null != showDialog && showDialog.isShowing()) {
            showDialog.dismiss();
        }
    }

    private void clear() {
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_NEWCOMPETITION, this);
        service.unRegisterObserve(HttpConstant.NOTIFY_APPLY_NEWCOMPETITION, this);
    }
}
