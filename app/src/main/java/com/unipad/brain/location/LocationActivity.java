package com.unipad.brain.location;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.unipad.AppContext;
import com.unipad.brain.BasicActivity;
import com.unipad.brain.R;
import com.unipad.brain.dialog.ShowDialog;
import com.unipad.brain.location.bean.CityBean;
import com.unipad.brain.location.bean.CompetitionBean;
import com.unipad.brain.location.bean.ProvinceBean;
import com.unipad.brain.location.dao.LocationService;
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
 * Created by Wbj on 2016/5/24.
 * Updata by gongjb on 2016/6/22
 */
public class LocationActivity extends BasicActivity implements IDataObserver, AdapterView.OnItemClickListener {
    // 省份list
    private ListView lv_province;
    // 城市List
    private ListView lv_city;
    // 賽事Id
    private ListView lv_com;
    // 获取定位dao
    private LocationService service;

    private List<ProvinceBean> provinceBeans;
    private List<CityBean> cityBeans;
    private List<CompetitionBean> competitionBeans;
    private TextView txt_nodata;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //网络请求的超时时间 12秒
    private final int DELAYETIMEOUT = 12000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        competitionBeans = new ArrayList<CompetitionBean>();
        setContentView(R.layout.location_aty);
    }

    @Override
    public void initData() {
        lv_province = (ListView) findViewById(R.id.lv_province);
        lv_province.setOnItemClickListener(this);
        lv_city = (ListView) findViewById(R.id.lv_city);
        lv_city.setOnItemClickListener(this);
        lv_com = (ListView) findViewById(R.id.lv_com);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_pull_refresh_listview);
        txt_nodata = (TextView) findViewById(R.id.txt_null_data);
        lv_com.setOnItemClickListener(this);
        findViewById(R.id.title_bar_left_text).setOnClickListener(this);
        findViewById(R.id.title_bar_right_text).setOnClickListener(this);
        service = (LocationService) AppContext.instance().getService(Constant.LOCATION_SERVICE);
        service.registerObserver(HttpConstant.GET_PROVINCE, this);
        service.registerObserver(HttpConstant.GET_CITY, this);
        service.registerObserver(HttpConstant.CITY_GAME, this);
        service.registerObserver(HttpConstant.LOCATION_APPLY_GAME, this);
        ToastUtil.createWaitingDlg(this,null,Constant.LOGIN_WAIT_DLG).show(15);
        service.getProvinceList();

        initEvent();
    }

    private void initEvent(){

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue2,
                R.color.red,
                R.color.stroke_color,
                R.color.black
        );
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dip2px(24));

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


         /*避免出现item太大 之后 避免冲突scroll*/
        lv_com.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

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
                competitionBeans.clear();
                service.getCompetitionList(cityBeans.get((Integer) mSwipeRefreshLayout.getTag()).cityId);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
//                        competitionBeans.clear();
                    }
                }, DELAYETIMEOUT);
            }
        });
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_bar_left_text:
                finish();
                break;
            case R.id.title_bar_right_text:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        service.unregistDataChangeListenerObj(this);
        super.onDestroy();
    }

    @Override
    public void update(int key, Object o) {
        HIDDialog.dismissAll();
        switch (key) {
            case HttpConstant.GET_CITY:
                if (o instanceof String) {
                    ToastUtil.showToast((String) o);
                } else {
                    cityBeans = (List<CityBean>) o;
                        lv_city.setAdapter(new CommonAdapter<CityBean>(this, cityBeans, R.layout.location_item) {
                            @Override
                            public void convert(ViewHolder holder, CityBean provinceBean) {
                                holder.setText(R.id.txt_name, provinceBean.cityName);
                                ((LinearLayout)holder.getView(R.id.ll_item_city_bg)).setBackgroundResource(R.color.light_blue_tran);
                                TextView txtName = (TextView) holder.getView(R.id.txt_name);
                                txtName.setTextColor(LocationActivity.this.getResources().getColor(R.color.black));
                                ImageView img_bian = (ImageView)holder.getView(R.id.img_bian);
                                ImageView img_bian_left = (ImageView)holder.getView(R.id.img_bian_left);
                                if(provinceBean.isSel){
//                                    img_bian.setImageResource(R.drawable.line_two_location);
                                    img_bian.setVisibility(View.VISIBLE);
                                    img_bian_left.setVisibility(View.VISIBLE);
                                    img_bian.setImageResource(R.drawable.line_location_right);
                                    img_bian_left.setImageResource(R.drawable.line_location_left);
                                } else {
//                                    img_bian.setImageResource(R.drawable.line_location);
                                    img_bian.setVisibility(View.GONE);
                                    img_bian_left.setVisibility(View.GONE);
                                }
                            }
                    });
                      /*获取第一个省 然后马上点击item 1 获取 item 1 数据    */
                   // lv_city.setSelection(0);

                    onItemClick(lv_city,null,0,1);
                }
                break;
            case HttpConstant.GET_PROVINCE:
                if (o instanceof String) {
                    ToastUtil.showToast((String) o);
                } else {
                provinceBeans = (List<ProvinceBean>) o;
                lv_province.setAdapter(new CommonAdapter<ProvinceBean>(this, provinceBeans, R.layout.location_item) {
                    @Override
                    public void convert(ViewHolder holder, ProvinceBean provinceBean) {
                        holder.setText(R.id.txt_name, provinceBean.provinceName);
                        LinearLayout ll_city = holder.getView(R.id.ll_item_city_bg);
                        TextView txtName = (TextView) holder.getView(R.id.txt_name);
                        ImageView img_bian = (ImageView)holder.getView(R.id.img_bian);
                        img_bian.setVisibility(View.GONE);
                        ImageView img_bian_left = (ImageView)holder.getView(R.id.img_bian_left);
                        img_bian_left.setVisibility(View.GONE);
                        txtName.setTextColor(LocationActivity.this.getResources().getColor(R.color.black));
                        if(provinceBean.isSel){
                            ll_city.setBackgroundResource(R.color.light_blue_item);
//                                img_bian.setImageResource(R.drawable.line_location_left);
                        } else {
                            ll_city.setBackgroundResource(R.color.white);
//                                img_bian.setImageResource(R.drawable.line_location);
                        }
                    }
                });
                /*获取第一个省 然后马上点击item 1 获取 item 1 数据    */
                   // lv_province.setSelection(0);
                    onItemClick(lv_province,null,0,1);
            }
                break;
            case HttpConstant.CITY_GAME:
                // 赛事
                mSwipeRefreshLayout.setRefreshing(false);
                if (o instanceof String) {
                    ToastUtil.showToast((String) o);
                } else {
                    List<CompetitionBean> databean = (List<CompetitionBean>) o;
                    if(databean.size() == 0){ //没有比赛
                        if(competitionBeans.size() == 0){
                            txt_nodata.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                        }

                    }else {
                        txt_nodata.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    }


                    lv_com.setAdapter(new CommonAdapter<CompetitionBean>(this, competitionBeans, R.layout.location_msg_item_layout) {
                        @Override
                        public void convert(ViewHolder holder, final CompetitionBean competitionBean) {
                            holder.setText(R.id.txt_year, competitionBean.getCompetitionDate());
                            holder.setText(R.id.txt_name, competitionBean.getName() + "/" + competitionBean.getProjecNname());
                            holder.setText(R.id.txt_addr, competitionBean.getAddr());
                            holder.setText(R.id.txt_cost, competitionBean.getCost());
                            TextView in_game = (TextView) holder.getView(R.id.in_game);

//                            in_game.setText(competitionBean.getIsApply() == 0 ? getString(R.string.my_apply) : getString(R.string.applied) );
                            // holder.getView(R.id.in_game).setVisibility(competitionBean.getApplyState() == 0 ? View.VISIBLE : View.GONE);
                            //holder.setImageResource(R.id.img_photo, homeBean.isSelect ? homeBean.selImgId : homeBean.norImgId);
                            //holder.setTextColor(R.id.txt_name, homeBean.isSelect ? iHome.getContext().getResources().getColor(R.color.main_1) : iHome.getContext().getResources().getColor(R.color.black));
                            /////////----- 以下两行代码表示 设置某个控件的点击事件-----////
//                            holder.getView(R.id.in_game).setTag(competitionBean);
//                            holder.getView(R.id.in_game).setOnClickListener(new OnClickApply());

                            if (0 == competitionBean.getIsApply()) {  //选手报名
//                                in_game.setBackgroundResource(R.drawable.button_apply_competition);
                                in_game.setClickable(true);
                                in_game.setBackgroundResource(R.drawable.entry_btn_shape);
                                in_game.setText(getString(R.string.person_entry_fee));
                                in_game.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        service.getLoactionApplyCompetition(AppContext.instance().loginUser.getUserId(), HttpConstant.LOCATION_APPLY_GAME,
                                                competitionBean.getComId(), competitionBean.getProjectId(), null, 0);
                                    }
                                });
                            } else {  //已报名
                                in_game.setClickable(false);
                                in_game.setBackgroundResource(R.drawable.entryed_btn_shape);
                                in_game.setText(getString(R.string.applied));
                                in_game.setOnClickListener(null);
                            }
                        }
                    });

//                    if (competitionBeans.size() != 0) {
//                        for (int i = databean.size()-1; i >= 0; i--) {
//                            for (int j = 0; j < competitionBeans.size(); j++) {
//                                if (databean.get(i).equals(competitionBeans.get(j))) {
//                                    break;
//                                } else {
//                                    if (j == competitionBeans.size() - 1) {
//                                        //不同 则是新数据
//                                        competitionBeans.add(databean.get(i));
//                                        break;
//                                    }
//                                    continue;
//                                }
//                            }
//                        }
//                    } else {
                        competitionBeans.addAll(databean);
//                    }
                    ((BaseAdapter)lv_com.getAdapter()).notifyDataSetChanged();
                }
                break;
            case HttpConstant.LOCATION_APPLY_GAME:
                if (o instanceof String) {
                    ToastUtil.showToast((String) o);
                } else {
                    if (null != o) {
                    CompetitionBean bean = (CompetitionBean) o;
                        for (int i = 0; i < competitionBeans.size(); i++) {
                            CompetitionBean compet = competitionBeans.get(i);
                            if (compet.getComId().equals(bean.getComId())) {
                                compet.setIsApply(bean.getIsApply());
                                break;
                            } else {
                                continue;
                            }
                        }
                        ((BaseAdapter)lv_com.getAdapter()).notifyDataSetChanged();
                    } else { //用户没有实名认证
                        if(AppContext.instance().loginUser.getAuth() == 0 || AppContext.instance().loginUser.getAuth() == 3) {
                            View dialogView = View.inflate(this, R.layout.first_login_dialog, null);
                            TextView txt_msg = (TextView) dialogView.findViewById(R.id.txt_msg);
                            txt_msg.setText(AppContext.instance().loginUser.getAuth() == 0 ? this.getString(R.string.auth_hint) : this.getString(R.string.auth_fail_hint));
                            final ShowDialog showDialog = new ShowDialog(this);
                            showDialog.showDialog(dialogView, ShowDialog.TYPE_CENTER, this.getWindowManager(), 0.4f, 0.5f);

                            showDialog.setOnShowDialogClick(new ShowDialog.OnShowDialogClick() {
                                @Override
                                public void dialogClick(int id) {
                                    if(null != showDialog && showDialog.isShowing()){
                                        showDialog.dismiss();
                                    }
                                }
                            });
                            showDialog.bindOnClickListener(dialogView, new int[]{R.id.img_close});
                        } else {
                            ToastUtil.showToast(getString(R.string.submit_fail));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_province:
                if(position != 0)
                    ToastUtil.createWaitingDlg(this,null,Constant.LOGIN_WAIT_DLG).show(15);
                for(int i = 0; i < provinceBeans.size() ; i ++ ){
                    if(i == position){
                        provinceBeans.get(i).isSel = true;
                    } else {
                        provinceBeans.get(i).isSel = false;
                    }
                }
                if(competitionBeans != null){
                    competitionBeans.clear();
                    if(null != (BaseAdapter)lv_com.getAdapter()){
                        ((BaseAdapter)lv_com.getAdapter()).notifyDataSetChanged();
                    }
                }
                ((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
                service.getCityList(provinceBeans.get(position).provinceId);
                break;
            case R.id.lv_city:
                // 根据城市ID 获取比赛列表
                if(position != 0)
                    ToastUtil.createWaitingDlg(this,null,Constant.LOGIN_WAIT_DLG).show(15);
                for(int i = 0; i < cityBeans.size() ; i ++ ){
                    if(i == position){
                        cityBeans.get(i).isSel = true;
                    } else {
                        cityBeans.get(i).isSel = false;
                    }
                }
                ((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
                mSwipeRefreshLayout.setTag(position);
                competitionBeans.clear();
                service.getCompetitionList(cityBeans.get(position).cityId);
                break;
        }
    }
}
