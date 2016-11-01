package com.unipad.brain.personal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.home.bean.RuleGame;
import com.unipad.brain.personal.bean.CompetitionBean;
import com.unipad.brain.personal.dao.PersonCenterService;
import com.unipad.common.CommonActivity;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.common.widget.HIDDialog;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;
import com.unipad.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心之我的报名
 * Created by Wbj on 2016/4/27.
 */
public class PersonalMsgFragment extends PersonalCommonFragment implements IDataObserver {

    private ListView lv_apple;
    private List<CompetitionBean> competitionBeans;

    // 个人中心模块 服务器交互 服务
    private PersonCenterService service;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // 获取规则
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitleBarRightText = mActivity.getString(R.string.clear);
        lv_apple =(ListView) mActivity.findViewById(R.id.lv_competition);

        mSwipeRefreshLayout = (SwipeRefreshLayout)mActivity.findViewById(R.id.swipe_refresh_widget_msg);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue2,
                R.color.red,
                R.color.stroke_color,
                R.color.black
        );
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dip2px(24));
//        mSwipeRefreshLayout.setRefreshing(false);

        competitionBeans = new ArrayList<CompetitionBean>();
        service = (PersonCenterService) AppContext.instance().getService(Constant.PERSONCENTER);
        service.registerObserver(HttpConstant.USER_APPLYED,this);
        service.registerObserver(HttpConstant.USER_IN_GAEM, this);
        service.registerObserver(HttpConstant.GET_RULE_NOTIFY, this);
        service.getApplyList(AppContext.instance().loginUser.getUserId());

        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        thisShowView = 6;
    }

    private void initEvent(){
        //下拉动画生效
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

        lv_apple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != competitionBeans)
                    service.getRule(competitionBeans.get(position).getComId());
            }
        });

         /*避免出现item太大 之后 避免冲突scroll*/
        lv_apple.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    /*第一项可见 的时候 才可以响应swipe的滑动刷新事件*/
                    mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 ? true : false);

            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                competitionBeans.clear();
                service.getApplyList(AppContext.instance().loginUser.getUserId());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, DELAYETIMEOUT);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        service.unregistDataChangeListenerObj(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.personal_frg_msg;
    }

    @Override
    public void clickTitleBarRightText() {
        this.clearMsgList();
    }

    @Override
    public String getTitleBarRightText() {
        return mTitleBarRightText;
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 清空消息列表
     */
    private void clearMsgList() {
        ToastUtil.showToast(mTitleBarRightText);
    }

    @Override
    public void update(int key, Object o) {
         // 此方法用于更新UI
        switch (key){
            case HttpConstant.USER_APPLYED:
                mSwipeRefreshLayout.setRefreshing(false);
                if(o instanceof String){
                    ToastUtil.showToast((String)o);
                } else {
                    competitionBeans.addAll((List<CompetitionBean>)o);
                    lv_apple.setAdapter(new CommonAdapter<CompetitionBean>(mActivity, competitionBeans, R.layout.personal_msg_item_layout) {
                        @Override
                        public void convert(ViewHolder holder, CompetitionBean competitionBean) {
                            holder.setText(R.id.txt_year, competitionBean.getCompetitionDate());
                            holder.setText(R.id.txt_name, competitionBean.getName() + "/" + competitionBean.getProjecNname());
                            holder.setText(R.id.txt_addr,competitionBean.getAddr());
                            holder.setText(R.id.txt_cost, competitionBean.getCost());
                           // holder.getView(R.id.in_game).setVisibility(competitionBean.getApplyState() == 0 ? View.VISIBLE : View.GONE);

                            //holder.setImageResource(R.id.img_photo, homeBean.isSelect ? homeBean.selImgId : homeBean.norImgId);
                            //holder.setTextColor(R.id.txt_name, homeBean.isSelect ? iHome.getContext().getResources().getColor(R.color.main_1) : iHome.getContext().getResources().getColor(R.color.black));
                            /////////----- 以下两行代码表示 设置某个控件的点击事件-----////
                             holder.getView(R.id.in_game).setTag(competitionBean);
                             holder.getView(R.id.in_game).setOnClickListener(new OnClickApply());
                        }
                    });
                }
                break;
            case HttpConstant.USER_IN_GAEM:
                // 判断是否可以进入比赛
                HIDDialog.dismissAll();
                try {
                    JSONObject jsonObject = new JSONObject((String)o);
                    if(jsonObject == null){
                        //13973752686
                        ToastUtil.showToast((String)o);
                        return;
                    }
                    int code = jsonObject.optInt("ret_code");
                    if(code == 0){
                        JSONObject data = jsonObject.optJSONObject("data");
                       // String allow = data.optString("allow");
                       // if("0".equals(allow)){
                            Intent intent = new Intent(mActivity, CommonActivity.class);
                            intent.putExtra("projectId",data.optString("projectId"));
                            intent.putExtra("matchId",data.optString("matchId"));
                            this.startActivity(intent);
                       // } else if("-1".equals(allow)){
                        //    ToastUtil.showToast(getString(R.string.not_game));
                       // } else {
                       //     ToastUtil.showToast(getString(R.string.gameed));
                       // }
                    } else {
                        //ret_code":"-1","ret_msg":"比赛已经结束
                        ToastUtil.showToast("" + jsonObject.optString("ret_msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HttpConstant.GET_RULE_NOTIFY:
                HIDDialog dialog = ToastUtil.createRuleDialog(mActivity, "1001", (RuleGame) o);
                //dialog.show();

                dialog.show();
                break;
        }
    }


    /**
     *  进入比赛
     */
    class OnClickApply implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CompetitionBean competitionBean = (CompetitionBean)v.getTag();
            service.checkMatchStart(competitionBean.getComId(),competitionBean.getProjectId());
            ToastUtil.createWaitingDlg(mActivity,null,Constant.LOGIN_WAIT_DLG).show();
        }
    }

}
