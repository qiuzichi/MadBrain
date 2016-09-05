package com.unipad.brain.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.slidingmenu.lib.SlidingMenu;
import com.unipad.AppContext;
import com.unipad.IcoreTimeChange;
import com.unipad.brain.R;
import com.unipad.brain.dialog.ShowDialog;
import com.unipad.brain.home.bean.HomeBean;
import com.unipad.brain.home.bean.ProjectBean;
import com.unipad.brain.home.competitionpj.view.HomePresenter;
import com.unipad.common.Constant;
import com.unipad.common.PractiseGameActivity;
import com.unipad.http.HttpConstant;
import com.unipad.utils.DateUtil;
import com.unipad.utils.PicUtil;
import com.unipad.utils.SharepreferenceUtils;
import com.unipad.utils.ToastUtil;
import com.unipad.utils.Util;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * yzj----项目:虚拟事件
 */
public class MainCompeteFragment extends MainBasicFragment {
    private RelativeLayout relatlayout;
    private ListView lv_project;
    private FrameLayout fl_project;
    //    private TextView txt_title;
//    private TextView txt_attention_content;
//    private TextView txt_memory_content;
//    private TextView txt_recall_content;
//    private TextView txt_function_content;
    //比赛项目
    private TextView txt_pname, txt_target;
    //城市赛
    private TextView txt_city_memory, txt_city_recall;
    //中国赛
    private TextView txt_china_memory, txt_china_recall;
    //世界赛
    private TextView txt_world_memory, txt_world_recall;
    //时间设置
    private TextView tv_set;
    /**
     * Fragment界面父布局
     */
    //private RelativeLayout mParentLayout;
    //索引
    private int projectindex = -1;
    private HomePresenter homePresenter;
    private List<HomeBean> homeBeans;
    private String next = "";
    HomeListAdapter homeListAdapter = new HomeListAdapter();

    private ShowDialog showDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();

        lv_project = (ListView) mActivity.findViewById(R.id.lv_project);
        fl_project = (FrameLayout) mActivity.findViewById(R.id.fl_project);
//        txt_title = (TextView) mActivity.findViewById(R.id.txt_title);
//        txt_attention_content = (TextView) mActivity.findViewById(R.id.txt_attention_content);
//        txt_memory_content = (TextView) mActivity.findViewById(R.id.txt_memory_content);
//        txt_recall_content = (TextView) mActivity.findViewById(R.id.txt_recall_content);
//        txt_function_content = (TextView) mActivity.findViewById(R.id.txt_function_content);
        initUserName();

        txt_pname = (TextView) mActivity.findViewById(R.id.txt_pname);
        txt_target = (TextView) mActivity.findViewById(R.id.txt_target);
        final LinearLayout ll_item_bg = (LinearLayout) mActivity.findViewById(R.id.ll_item_bg);
        txt_city_memory = (TextView) mActivity.findViewById(R.id.txt_city_memory);
        txt_city_recall = (TextView) mActivity.findViewById(R.id.txt_city_recall);
        txt_china_memory = (TextView) mActivity.findViewById(R.id.txt_china_memory);
        txt_china_recall = (TextView) mActivity.findViewById(R.id.txt_china_recall);
        txt_world_memory = (TextView) mActivity.findViewById(R.id.txt_world_memory);
        txt_world_recall = (TextView) mActivity.findViewById(R.id.txt_world_recall);
        tv_set = (TextView) mActivity.findViewById(R.id.txt_set_competition_time);
        relatlayout = (RelativeLayout) mActivity.findViewById(R.id.relatlayout);
        mActivity.findViewById(R.id.btn_apple).setOnClickListener(this);//比赛报名
        mActivity.findViewById(R.id.btn_exercise).setOnClickListener(this);//练习模式
        tv_set.setOnClickListener(this);

        relatlayout.setVisibility(View.GONE);
        lv_project.setAdapter(homeListAdapter);//(new nvvervi());
        final int[] iconDrawable = new int[]{R.drawable.person_icon_bg, R.drawable.binary_num_bg, R.drawable.long_num_bg,
                R.drawable.abs_picture_bg, R.drawable.item_bg, R.drawable.virtual_date_bg, R.drawable.quickiy_pocker_bg, R.drawable.random_num_bg,
                R.drawable.liston_and_memory_bg, R.drawable.quickiy_pocker_bg};
        lv_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (relatlayout.getVisibility() == View.GONE) {
                    relatlayout.setVisibility(View.VISIBLE);
                }
                projectindex = position;
                ll_item_bg.setBackgroundResource(iconDrawable[projectindex]);
                homeListAdapter.notifyDataSetChanged();
                txt_pname.setText(homeBeans.get(position).projectBean.getName());
                SpannableStringBuilder bulid = new SpannableStringBuilder(homeBeans.get(position).projectBean.getTarget().trim());
                bulid.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_blue2)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_target.setText(bulid);

                txt_city_memory.setText((homeBeans.get(position).projectBean.getMemorysDate())[0]);
                txt_city_recall.setText((homeBeans.get(position).projectBean.getRecallsDate())[0]);
                txt_china_memory.setText((homeBeans.get(position).projectBean.getMemorysDate())[1]);
                txt_china_recall.setText((homeBeans.get(position).projectBean.getRecallsDate())[1]);
                txt_world_memory.setText((homeBeans.get(position).projectBean.getMemorysDate())[2]);
                txt_world_recall.setText((homeBeans.get(position).projectBean.getRecallsDate())[2]);

                if (position == 6 || position == 9) {
                    return;
                }

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.main_compete_fragment;
    }

    private void initUserName() {
        ((TextView) mActivity.findViewById(R.id.txt_uese_name)).setText(AppContext.instance().loginUser.getUserName() + DateUtil.getMatchGroud(mActivity));
        ((TextView) mActivity.findViewById(R.id.txt_uese_level)).setText(getString(R.string.person_level) + AppContext.instance().loginUser.getLevel());

        final ImageView user_photo = (ImageView) mActivity.findViewById(R.id.iv_user_pic);
        ImageOptions imageOptions =new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                .setRadius(DensityUtil.dip2px(360))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        // .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
                        // .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
                .build();
        if (!TextUtils.isEmpty(AppContext.instance().loginUser.getPhoto())) {
            x.image().bind(user_photo, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto(),imageOptions);
        } else {
            user_photo.setImageResource(R.drawable.set_headportrait);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserName();
    }

    public static int TmpSeconds = 10;

    /**
     * 打开比赛Activity
     */
    private void openCommonActivity() {
        // Intent intent = new Intent(mActivity, CommonActivity.class);
        // startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apple:
                Intent intent = new Intent(mActivity, CompetitionListActivity.class);
                intent.putExtra("projectBean", homeBeans.get(projectindex).projectBean);
                this.startActivity(intent);
                break;
            case R.id.txt_set_competition_time:
                setMenuOpen();
                //设置时间 每次都会读取本地保存的数据
                break;
            case R.id.img_close:
                menu.toggle();
                break;
            case R.id.btn_exercise:  //练习模式
                Intent praIntent = new Intent(mActivity, PractiseGameActivity.class);
                praIntent.putExtra("projectId", homeBeans.get(projectindex).projectBean.getProjectId());
                this.startActivity(praIntent);
            default:
                break;
        }
    }

    /**
     * 此方法，  初始化home页左边菜单。
     */
    private void initData() {
        homeBeans = new ArrayList<>();
        //人名头像记忆
        ProjectBean npPj = new ProjectBean(mActivity.getString(R.string.project_1), Constant.getProjectId(getString(R.string.project_1)), getString(R.string.project_1_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean npBean = new HomeBean(R.drawable.sel_np, R.drawable.nor_np, npPj, false);
        //二进制数字
        ProjectBean ejzPj = new ProjectBean(mActivity.getString(R.string.project_2), Constant.getProjectId(getString(R.string.project_2)), getString(R.string.project_2_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean ejzBean = new HomeBean(R.drawable.sel_rjz, R.drawable.nor_rjz, ejzPj, false);
        //马拉松数字项目
        ProjectBean mlsNumPj = new ProjectBean(mActivity.getString(R.string.project_3), Constant.getProjectId(getString(R.string.project_3)), getString(R.string.project_3_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean mlsNumBean = new HomeBean(R.drawable.sel_mlsnum, R.drawable.nor_mlsnum, mlsNumPj, false);
        //抽象图形
        ProjectBean cxTxPj = new ProjectBean(mActivity.getString(R.string.project_4), Constant.getProjectId(getString(R.string.project_4)), getString(R.string.project_4_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean cxTxBean = new HomeBean(R.drawable.sel_cx, R.drawable.nor_cx, cxTxPj, false);
        // 快速随机数字
        ProjectBean kssjPj = new ProjectBean(mActivity.getString(R.string.project_5), Constant.getProjectId(getString(R.string.project_5)), getString(R.string.project_5_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "CDVDVWE", "FBRRYJU", "GWGWG5", "FKBNG");
        HomeBean kssjBean = new HomeBean(R.drawable.sel_kssj, R.drawable.nor_kssj, kssjPj, false);
        // 虚拟事件和日期
        ProjectBean xnsjPj = new ProjectBean(mActivity.getString(R.string.project_6), Constant.getProjectId(getString(R.string.project_6)), getString(R.string.project_6_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean xnsjBean = new HomeBean(R.drawable.sel_xlsj, R.drawable.nor_xlsj, xnsjPj, false);
        // 马拉松扑克记忆
        ProjectBean mlspkPj = new ProjectBean(mActivity.getString(R.string.project_7), Constant.getProjectId(getString(R.string.project_7)), getString(R.string.project_7_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean mlspkBean = new HomeBean(R.drawable.sel_mlspk, R.drawable.nor_mlspk, mlspkPj, false);
        // 随机词语记忆
        ProjectBean sjcyPj = new ProjectBean(mActivity.getString(R.string.project_8), Constant.getProjectId(getString(R.string.project_8)), getString(R.string.project_8_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean sjcyBean = new HomeBean(R.drawable.sel_sjcy, R.drawable.nor_sjcy, sjcyPj, false);
        //听记数字
        ProjectBean thnumPj = new ProjectBean(mActivity.getString(R.string.project_9), Constant.getProjectId(getString(R.string.project_9)), getString(R.string.project_9_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean thnumBean = new HomeBean(R.drawable.sel_tjnum, R.drawable.nor_tjnum, thnumPj, false);
        // 快速扑克记忆
        ProjectBean kspkPj = new ProjectBean(mActivity.getString(R.string.project_10), Constant.getProjectId(getString(R.string.project_10)), getString(R.string.project_10_target), new String[]{"5min", "5min", "5min"}, new String[]{"15min", "15min", "15min"}, new String[]{"1" + next, "2" + next, "2" + next}, "", "", "", "");
        HomeBean kspkBean = new HomeBean(R.drawable.sel_kspk, R.drawable.nor_kspk, kspkPj, false);
        homeBeans.add(npBean);
        homeBeans.add(ejzBean);
        homeBeans.add(mlsNumBean);
        homeBeans.add(cxTxBean);
        homeBeans.add(kssjBean);
        homeBeans.add(xnsjBean);
        homeBeans.add(mlspkBean);
        homeBeans.add(sjcyBean);
        homeBeans.add(thnumBean);
        homeBeans.add(kspkBean);

    }

    class HomeListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return homeBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return homeBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_home_item, null);
            TextView name = (TextView) convertView.findViewById(R.id.txt_name);
            ImageView img = (ImageView) convertView.findViewById(R.id.img_photo);
            name.setText(homeBeans.get(position).projectBean.getName());
            if (projectindex == position) {
                name.setTextColor(mActivity.getResources().getColor(R.color.main_1));
                img.setImageResource(homeBeans.get(position).selImgId);
            } else {
                name.setTextColor(mActivity.getResources().getColor(R.color.black));
                img.setImageResource(homeBeans.get(position).norImgId);
            }
            return convertView;
        }
    }


    /**
     * 侧边栏是否显示
     */
    private SlidingMenu menu;

    public void setMenuOpen() {
        if (menu == null) {
            setSidebar();
        }
        if (!menu.isShown()) {
            menu.showMenu();

        } else {
            menu.toggle();
        }

    }


    /*
     * 设置侧边栏
	 */

    private void setSidebar() {
        menu = new SlidingMenu(mActivity);
        // 设置左侧边栏
        menu.setMode(SlidingMenu.RIGHT);
        // 手指滑动全屏屏幕，弹出侧边栏
        // menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置阴影图片的宽度
        menu.setShadowWidthRes(R.dimen.shadow_width);
        // 设置阴影图片的宽度
        menu.setShadowDrawable(R.drawable.shadow);
        // SlidingMenu划出时主页面显示的剩余宽度
        //menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置SlidingMenu菜单的宽度
        //menu.setBehindWidth(550);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setBehindOffset(mActivity.getWindowManager().getDefaultDisplay().getWidth() / 2);
        // SlidingMenu滑动时的渐变程度
        menu.setFadeDegree(0.55f);
        menu.attachToActivity(mActivity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.setting_parent);

        View view = (RelativeLayout) menu.getMenu();

        final LinearLayout parent = (LinearLayout) view.findViewById(R.id.parent_setting);
        final TextView set_tital_slidmenu = (TextView) view.findViewById(R.id.txt_title_project);
        set_tital_slidmenu.setText(Constant.getProjectName(homeBeans.get(projectindex).projectBean.getProjectId()));

        final TextView binaryMemoryText = (TextView) view.findViewById(R.id.binary_memory_set_show);
        final TextView binaryAnswerText = (TextView) view.findViewById(R.id.binary_answer_set_show);
        binaryMemoryText.setText(Util
                .dateFormat(SharepreferenceUtils.readLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_memoryTime", 300)));
        binaryAnswerText.setText(Util
                .dateFormat(SharepreferenceUtils.readLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_re_memoryTime", 300)));

        // 设置点击事件
        view.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
            }
        });

        view.findViewById(R.id.binary_memory_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* 设置记忆比赛时间点击事件*/
                Util.createSetting(getActivity(),
                        SharepreferenceUtils.readLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_memoryTime", 300), new IcoreTimeChange() {
                            @Override
                            public void callback(long value) {
                                SharepreferenceUtils.writeLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_memoryTime", value);
                                binaryMemoryText.setText(Util
                                        .dateFormat(value));
                            }
                        }).show();

            }
        });

        view.findViewById(R.id.binary_answer_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* 设置答题时间点击事件*/
                Util.createSetting(getActivity(),
                        SharepreferenceUtils.readLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_re_memoryTime", 300), new IcoreTimeChange() {
                            @Override
                            public void callback(long value) {
                                SharepreferenceUtils.writeLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_re_memoryTime", value);
                                binaryAnswerText.setText(Util
                                        .dateFormat(value));
                            }
                        }).show();
            }
        });
        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                set_tital_slidmenu.setText(Constant.getProjectName(homeBeans.get(projectindex).projectBean.getProjectId()));
                binaryMemoryText.setText(Util
                        .dateFormat(SharepreferenceUtils.readLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_memoryTime", 300)));
                binaryAnswerText.setText(Util
                        .dateFormat(SharepreferenceUtils.readLong(homeBeans.get(projectindex).projectBean.getProjectId() + "_re_memoryTime", 300)));
                for (int i = parent.getChildCount(); i > 5; i--) {
                    parent.removeViewAt(i-1);
                }
                if (homeBeans.get(projectindex).projectBean.getProjectId().equals(Constant.GAME_BINARY_NUM) ||
//                        homeBeans.get(projectindex).projectBean.getProjectId().equals(Constant.GAME_LONG_POCKER) ||
                        homeBeans.get(projectindex).projectBean.getProjectId().equals(Constant.GAME_LONG_NUM) ||
                        homeBeans.get(projectindex).projectBean.getProjectId().equals(Constant.GAME_RANDOM_NUM)) {

                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.line_setting, null);

                    RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group_competition_mode);
                    int lineMode = SharepreferenceUtils.getInt(homeBeans.get(projectindex).projectBean.getProjectId() + "_linemode", 0);
                    switch (lineMode){
                        case 0:
                            mRadioGroup.clearCheck();
                            break;
                        case 2:
                            mRadioGroup.check(R.id.btn_default_mode);
                            break;
                        case 3:
                            mRadioGroup.check(R.id.btn_default_mode_3);
                            break;
                        case 4:
                            mRadioGroup.check(R.id.btn_default_mode_4);
                            break;
                    }

                    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            switch (group.getCheckedRadioButtonId()) {
                                case R.id.btn_default_mode:
                                    SharepreferenceUtils.writeint(homeBeans.get(projectindex).projectBean.getProjectId() + "_linemode", 2);
                                    break;
                                case R.id.btn_default_mode_3:
                                    SharepreferenceUtils.writeint(homeBeans.get(projectindex).projectBean.getProjectId() + "_linemode", 3);
                                    break;
                                case R.id.btn_default_mode_4:
                                    SharepreferenceUtils.writeint(homeBeans.get(projectindex).projectBean.getProjectId() + "_linemode", 4);
                                    break;
                            }
                        }
                    });
                    parent.addView(view);
                }

                if(homeBeans.get(projectindex).projectBean.getProjectId().equals(Constant.GAME_LONG_POCKER)){
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.line_setting, null);
                    RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group_competition_mode);
                    ((TextView) view.findViewById(R.id.txt_title_competition)).setText(getString(R.string.remember_mode_set));
                    ((RadioButton) view.findViewById(R.id.btn_default_mode)).setVisibility(View.GONE);
                    ((RadioButton) view.findViewById(R.id.btn_default_mode_3)).setText("3");
                    ((RadioButton) view.findViewById(R.id.btn_default_mode_4)).setText("5");

                    int lineMode = SharepreferenceUtils.getInt(homeBeans.get(projectindex).projectBean.getProjectId() + "_dividemode", 3);
                    switch (lineMode){
                        case 3:
                            mRadioGroup.check(R.id.btn_default_mode_3);
                            break;
                        case 5:
                            mRadioGroup.check(R.id.btn_default_mode_4);
                            break;
                    }

                    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            switch (group.getCheckedRadioButtonId()) {

                                case R.id.btn_default_mode_3:
                                    SharepreferenceUtils.writeint(homeBeans.get(projectindex).projectBean.getProjectId() + "_dividemode", 3);
                                    break;
                                case R.id.btn_default_mode_4:
                                    SharepreferenceUtils.writeint(homeBeans.get(projectindex).projectBean.getProjectId() + "_dividemode", 5);
                                    break;
                            }
                        }
                    });
                    parent.addView(view);
                }

            }

        });
    }


}
