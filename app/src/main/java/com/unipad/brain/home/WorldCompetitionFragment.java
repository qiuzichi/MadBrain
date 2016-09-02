package com.unipad.brain.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.home.bean.CompetitionBean;
import com.unipad.brain.home.bean.ProjectBean;
import com.unipad.brain.home.dao.HomeGameHandService;
import com.unipad.brain.home.iview.ICompetition;
import com.unipad.brain.home.util.MyTools;
import com.unipad.common.BaseFragment;
import com.unipad.common.CommonActivity;
import com.unipad.common.Constant;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;

import java.util.List;

/**
 * @描述：          世界赛
 * @author gongjiebin
 *
 */
public class WorldCompetitionFragment extends BaseFragment implements ICompetition, IDataObserver {
	
	final public static String TAG = "WorldCompetitionFragment";
	
	private static WorldCompetitionFragment worldCompetitionFragment;
	
	private CompetitionPersenter competitionPersenter;
	
	@ViewInject(R.id.lv_competition)
	private ListView lv_competition;

	@ViewInject(R.id.progressbar_loading_list)
	private ProgressBar progressBar_load;

	@ViewInject(R.id.txt_loading_progress)
	private TextView txt_progressBar;

	@ViewInject(R.id.swipe_refresh_widget_apply)
	private SwipeRefreshLayout mSwipeRefreshLayout;


	//数据为空  显示的EmptyView
	@ViewInject(R.id.txt_empty_view)
	private TextView emptyView;
	
	private ProjectBean projectBean;
	private HomeGameHandService service;
	private int requestPagerNum;
	private boolean isLoadMoreData = false;
	private int totalPager;
	private CompetitionListActivity activity;

	public static WorldCompetitionFragment getWorldCompetitionFragment(){
		if( null == worldCompetitionFragment)
			return worldCompetitionFragment = new WorldCompetitionFragment();
		return worldCompetitionFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = (HomeGameHandService) AppContext.instance().getService(Constant.HOME_GAME_HAND_SERVICE);
		service.registerObserver(HttpConstant.WORD_GET_HOME_GAME_LIST, this);
		service.registerObserver(HttpConstant.WORD_APPLY_GAME, this);
		service.registerObserver(HttpConstant.WORLD_ATTENTION, this);

		activity = (CompetitionListActivity) getActivity();
		activity.getGameList(Constant.WORD_GAME, requestPagerNum = 1, 10);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View homeView = inflater.inflate(R.layout.fragment_competition_layout, container,false);
		ViewUtils.inject(this, homeView);
		showLoadProgress(View.VISIBLE);
		initView(homeView);
		initEvent();
		return homeView;
	}

	private void showLoadProgress(int visible) {
		progressBar_load.setVisibility(visible);
		txt_progressBar.setVisibility(visible);
	}

	private void initEvent() {
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.light_blue2,
				R.color.red,
				R.color.stroke_color,
				R.color.black
		);
		mSwipeRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dip2px(24));
		mSwipeRefreshLayout.setRefreshing(false);


         /*避免出现item太大 之后 避免冲突scroll*/
		lv_competition.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {
				switch (i) {
					// 当不滚动时
					case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
						// 判断滚动到底部
						if (lv_competition.getLastVisiblePosition() == (lv_competition.getCount() - 1)) {

							if (requestPagerNum == totalPager) {
								if (requestPagerNum >= 2) {
									ToastUtil.showToast(getString(R.string.loadmore_null_data));
								}
								return;
							} else {
								if (isLoadMoreData) {
									ToastUtil.showToast(getString(R.string.loadmore_data));
									return;
								}
								isLoadMoreData = true;
								activity.getGameList(Constant.WORD_GAME, requestPagerNum, 10);
								showLoadProgress(View.VISIBLE);
							}
						}
						break;
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				/*第一项可见 的时候 才可以响应swipe的滑动刷新事件*/
				mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0);

			}
		});


		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mSwipeRefreshLayout.setRefreshing(false);
						activity.getGameList(Constant.WORD_GAME, requestPagerNum = 1, 10);
					}
				}, 2000);
			}
		});

		emptyView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getGameList(Constant.WORD_GAME, requestPagerNum = 1, 10);
				showLoadProgress(View.VISIBLE);
			}
		});
	}
	@Override
	public void initView(View view) {
		super.initView(view);
		projectBean = ((CompetitionListActivity)getActivity()).getProjectBean();
		competitionPersenter = new CompetitionPersenter(this,projectBean);
	}

	@Override
	public Context getContext() {
		return getActivity();
	}


	@Override
	public void request(String jsonStr, int flag) {
		
	}


	@Override
	public void showToast(String checkResult) {
		MyTools.showToast(getContext(), checkResult);
	}


	@Override
	public void loadingDialog(long total, long current, boolean isUploading) {
		
	}


	@Override
	public void showDialog(boolean isOpen) {
	}


	@Override
	public void setAdapter(BaseAdapter baseAdapter) {
		lv_competition.setAdapter(baseAdapter);
	}


	@Override
	public String getCompetitionIndex() {
		return TAG;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		service.unRegisterObserve(HttpConstant.WORD_GET_HOME_GAME_LIST, this);
		service.unRegisterObserve(HttpConstant.WORD_APPLY_GAME, this);
		service.unRegisterObserve(HttpConstant.WORLD_ATTENTION, this);
	}

	@Override
	public void update(int key, Object o) {
		showLoadProgress(View.GONE);
		switch (key){
			case HttpConstant.WORD_GET_HOME_GAME_LIST:
				List<CompetitionBean> beans = (List<CompetitionBean>) o;
				if(beans.size() == 0){
					if(competitionPersenter.getDatas().size() == 0){
						mSwipeRefreshLayout.setVisibility(View.GONE);
						emptyView.setVisibility(View.VISIBLE);
					}
					return;
				}
				emptyView.setVisibility(View.GONE);
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);

				totalPager = beans.get(0).getTotalPage();
				if(totalPager != requestPagerNum){
					requestPagerNum ++;
				}
				competitionPersenter.setData((List<CompetitionBean>) o);
				break;
			case HttpConstant.WORD_APPLY_GAME:

				competitionPersenter.notifyData((CompetitionBean) o);

				Intent intent = new Intent(getActivity(), CommonActivity.class);
				intent.putExtra("projectId",((CompetitionBean) o).getProjectId());
				intent.putExtra("matchId",((CompetitionBean) o).getId());
				this.startActivity(intent);
				break;

			case HttpConstant.WORLD_ATTENTION :
				try {
					JSONObject jsonObject = new JSONObject((String)o);
					if (jsonObject.getInt("ret_code") == 0) {
						int index = jsonObject.optInt("dataset");
						// 刷新界面
						competitionPersenter.notifyData(index);
					}
					showToast(jsonObject.optString("ret_msg"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}
}
