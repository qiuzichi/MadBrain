package com.unipad.brain.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.home.bean.CompetitionBean;
import com.unipad.brain.home.bean.ProjectBean;
import com.unipad.brain.home.dao.HomeGameHandService;
import com.unipad.brain.dialog.ShowDialog;

import com.unipad.brain.home.iview.ICompetition;
import com.unipad.brain.home.util.ConstSettings;
import com.unipad.common.CommonActivity;
import com.unipad.common.Constant;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.http.HitopApplyGame;


/**
 * @author gongjiebin
 * @描述： 赛事的中间件
 */
public class CompetitionPersenter {

    private ICompetition iCompetition;

    private List<CompetitionBean> competitionBeans;

    private BaseAdapter competitionAdapter;

    private OnApplyOrAtClickListenter applyOrAtClickListenter;
    // 所报名的 比赛项目
    private ProjectBean projectBean;
    // 当前展示的赛事
    private CompetitionBean competitionBean;

    private ShowDialog pay_dialog;
    // 操作资源文件的类
    private Resources resources;

    private int level;

    public CompetitionPersenter(ICompetition iCompetition, ProjectBean projectBean) {
        super();
        this.iCompetition = iCompetition;
        competitionBeans = new ArrayList<>();
        resources = iCompetition.getContext().getResources();
        level = CityCompetitionFragment.TAG.equals(this.iCompetition.getCompetitionIndex()) ? 0 : ChinaCompetitionFragment.TAG.equals(this.iCompetition.getCompetitionIndex()) ? 1 : 2;
        setAdapter();
        this.projectBean = projectBean;
        applyOrAtClickListenter = new OnApplyOrAtClickListenter();
    }

    /**
     * 虚拟加载数据
     */

    public void setData(List<CompetitionBean> beans) {
//        competitionBeans.clear();
        if (competitionBeans.size() != 0) {
            for (int i = beans.size() - 1; i >= 0; i--) {
                for (int j = 0; j < competitionBeans.size(); j++) {
                    if (beans.get(i).equals(competitionBeans.get(j))) {
                        break;
                    } else {
                        if (j == competitionBeans.size() - 1) {
                            //添加最前面 新数据
                            competitionBeans.add(0, beans.get(i));
                            break;
                        }
                        continue;
                    }
                }
            }
        } else {
            competitionBeans.addAll(beans);
        }
        competitionAdapter.notifyDataSetChanged();
    }


    /**
     * 设置适配器
     */
    private void setAdapter() {
        iCompetition.setAdapter(competitionAdapter = new CommonAdapter<CompetitionBean>(iCompetition.getContext(), competitionBeans, R.layout.competition_item_layout) {
            @Override
            public void convert(final ViewHolder holder, final CompetitionBean homeBean) {
//				holder.setText(R.id.txt_name, homeBean.name);
//				holder.setImageResource(R.id.img_photo, homeBean.isSelect ? homeBean.selImgId : homeBean.norImgId);
//				holder.setTextColor(R.id.txt_name, homeBean.isSelect ? iHome.getContext().getResources().getColor(R.color.main_1) : iHome.getContext().getResources().getColor(R.color.black));
                /////////----- 以下两行代码表示 设置某个控件的点击事件-----////
                Button apply = holder.getView(R.id.game_apply);
                TextView name = holder.getView(R.id.game_name);
                TextView date = holder.getView(R.id.game_data);
                TextView addrView = holder.getView(R.id.game_addr);
                ImageView img_gz = holder.getView(R.id.img_gz);
                TextView cost = holder.getView(R.id.game_cost);
                String projecName = projectBean != null ? "/" + projectBean.getName() : "";
                name.setText(homeBean.getName() + projecName);
                date.setText(homeBean.getCompetitionDate() + " " + homeBean.getCompetitionTime());
                cost.setText("费用:￥" + homeBean.getCost());
                apply.setTag(homeBean);
                addrView.setText(homeBean.getAddress());
                //
                img_gz.setImageResource(homeBean.isAttention() ? R.drawable.personal_favorite_press : R.drawable.personal_favorite_normal);
                img_gz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HomeGameHandService) AppContext.instance().getService(Constant.HOME_GAME_HAND_SERVICE)).attention(homeBean.getId(), AppContext.instance().loginUser.getUserId(), "0", !homeBean.isAttention() + "", holder.getPosition() + "", level);
                    }
                });
                img_gz.setTag(homeBean);
                holder.getView(R.id.game_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HomeGameHandService) AppContext.instance().getService(Constant.HOME_GAME_HAND_SERVICE)).getRule(homeBean.getId());
                    }
                });
                if (homeBean.getApplyState() == 0) {//未报名

                    apply.setText(apply.getResources().getText(R.string.my_apply));
                    apply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HitopApplyGame applyGame = new HitopApplyGame(AppContext.instance().loginUser.getUserId(), homeBean.getId(), homeBean.getProjectId(), homeBean.getGradeId(), 0);
                            applyGame.post();
                        }
                    });
                } else {
                    apply.setText(apply.getResources().getText(R.string.applied));
                    apply.setOnClickListener(null);
                }
            }
        });
    }

    public void notifyData(CompetitionBean competitionBean) {
        for (int i = 0; i < competitionBeans.size(); i++) {
            CompetitionBean compet = competitionBeans.get(i);
            if (compet.getId().equals(competitionBean.getId())) {
                compet.setApplyState(competitionBean.getApplyState());
                break;
            } else {
                continue;
            }
        }
        competitionAdapter.notifyDataSetChanged();
    }

    public void notifyData(int index) {
        for (int i = 0; i < competitionBeans.size(); i++) {
            if (i == index) {
                CompetitionBean compet = competitionBeans.get(i);
                compet.setAttention(!compet.isAttention());
                break;
            }
        }
        competitionAdapter.notifyDataSetChanged();
    }



    /**
     * @author gongjiebin
     * @描述： 报名 与 关注的点击事件
     */
    class OnApplyOrAtClickListenter implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.game_apply:
                    competitionBean = (CompetitionBean) v.getTag();
                    /**
                    // 弹出报名Dialog
                    String level = competitionBean.getCompetitionLevel() == 0 ? resources.getString(R.string.message_city) : competitionBean.getCompetitionLevel() == 1 ? resources.getString(R.string.message_china) : resources.getString(R.string.message_world);
                    View view = View.inflate(iCompetition.getContext(), R.layout.competition_apply_cost_layout, null);
                    ((TextView) view.findViewById(R.id.txt_hint)).setText(resources.getString(R.string.str_apply_pay_time) + "比赛项目为“" + projectBean.getName() + "”级别为“" + level + "”的报名，请支付报名费用！");
                    ((Button) view.findViewById(R.id.btn_pay)).setOnClickListener(this);
                    // btn_not_apply
                    ((Button) view.findViewById(R.id.btn_not_apply)).setOnClickListener(this);
                    pay_dialog = new ShowDialog(iCompetition.getContext());
                    pay_dialog.showDialog(view, ShowDialog.TYPE_RIGHT, ((BaseFragment) iCompetition).getActivity().getWindowManager(), 0.0f, 0.25f);
                   */
                    break;
                case R.id.btn_pay:
                    if (null != competitionBean) {
                        // 调用支付接口 ---------- 暂时省略支付，跳入相关比赛界面
                        iCompetition.showToast("succeed");
                        selCompetitiomAt();
                    }
                    break;
                case R.id.btn_not_apply:
                    if (null != pay_dialog) {
                        pay_dialog.dismiss();
                    }
                    break;
                case R.id.img_gz:
                    // 关注 按钮
                    //ATTENTION

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 选择跳转比赛界面
     */
    private void selCompetitiomAt() {
        if (null != projectBean) {
            if (ConstSettings.getProjectValues(iCompetition.getContext()).get(0).trim().equals(projectBean.getName().trim())) {//人名头像记忆
                iCompetition.showToast("0");
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(1).equals(projectBean.getName())) {//二进制数字
                iCompetition.showToast("1");//	iCompetition.getContext().startActivity(new Intent(iCompetition.getContext(),.class));
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(2).equals(projectBean.getName())) {//马拉松数字
                iCompetition.showToast("2");
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(3).equals(projectBean.getName())) {//抽象图形
                iCompetition.showToast("3");
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(4).equals(projectBean.getName())) {//快速随机数字
                // 跳入快速随机数字 比赛项目 界面
//			iCompetition.getContext().startActivity(new Intent(iCompetition.getContext(),RapidRandomNumberActivity.class));
                iCompetition.showToast("4");
            } else if (projectBean.getName().trim().equals(ConstSettings.getProjectValues(iCompetition.getContext()).get(5).trim())) {//虚拟事件和日期
                //openCommonActivity(iCompetition.getContext(), 5);
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(6).equals(projectBean.getName())) {//马拉松扑克记忆
                iCompetition.showToast("6");
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(7).equals(projectBean.getName())) {//随机词语记忆
                iCompetition.showToast("7");
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(8).equals(projectBean.getName())) {//听记数字
                iCompetition.showToast("8");
            } else if (ConstSettings.getProjectValues(iCompetition.getContext()).get(9).equals(projectBean.getName())) {//快速扑克记忆
                iCompetition.showToast("9");
            }
        }
    }


    private void openCommonActivity(Context context) {
        Intent intent = new Intent(context, CommonActivity.class);
        context.startActivity(intent);
    }

    public List<CompetitionBean> getDatas(){
        return competitionBeans;
    }
}
