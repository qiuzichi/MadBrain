package com.unipad.brain.consult.view;


import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.consult.ConsultBaseFragment;
import com.unipad.brain.consult.adapter.MyInfoListAdapter;
import com.unipad.brain.consult.entity.ConsultClassBean;
import com.unipad.brain.consult.entity.ListEnum;
import com.unipad.brain.consult.widget.CircularImageView;
import com.unipad.http.HttpConstant;
import com.unipad.utils.DateUtil;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 63 on 2016/6/23.
 */
public class InfoListFragment extends ConsultBaseFragment implements AdapterView.OnItemClickListener{
    private ListView mLvInfos;
    private View mInfoListView;
    private List<ConsultClassBean> mList;
    private MyInfoListAdapter mInfoListAdapter;
    private int mCurrentSelectedPosition;
    private OnHomePageChangeListener mOnHomePageChangeListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_info_list;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        this.mInfoListView = view;
        mLvInfos = (ListView)view.findViewById(R.id.lv_info_list);
        initTitleBar();
    }

    private void  initTitleBar(){
        TextView user_name = (TextView) mInfoListView.findViewById(R.id.tv_uese_name_consult);
        if(AppContext.instance().loginUser == null) return;
        user_name.setText(AppContext.instance().loginUser.getUserName());
        user_name.setSelected(true);
        ((TextView)mInfoListView.findViewById(R.id.txt_user_group)).setText(DateUtil.getMatchGroud(getmContext()));
        ((TextView)mInfoListView.findViewById(R.id.tv_uese_level_consult)).setText(getString(R.string.person_level) + AppContext.instance().loginUser.getLevel());
        final ImageView user_photo = (ImageView)mInfoListView.findViewById(R.id.iv_header);
        ImageOptions imageOptions =new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
//                .setRadius(DensityUtil.dip2px(360))//ImageView  设置拐角弧度
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCircular(true)  //设置图片显示为圆形
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
                        // .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
                        // .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
                .build();
        if (!TextUtils.isEmpty(AppContext.instance().loginUser.getPhoto())) {
            x.image().bind(user_photo, HttpConstant.PATH_FILE_URL + AppContext.instance().loginUser.getPhoto(),imageOptions);
        }else {
            user_photo.setImageResource(R.drawable.set_headportrait);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        initMyListData();
        initAdapter();
    }

    public void initMyListData(){
        ListEnum[] datas = ListEnum.values();
        if(mList == null){
            mList = new ArrayList<ConsultClassBean>();
        }
        for(int i = 0; i < datas.length; i ++){
            ListEnum data=  datas[i];
            ConsultClassBean bean = new ConsultClassBean();
            bean.setNameResId(data.getNameResId());
            bean.setLabelResId(data.getLabelResId());
            if(data.equals(ListEnum.CONSULT_PRESENT)){
                bean.setSelected(true);
                mCurrentSelectedPosition = 0;
            }else{
                bean.setSelected(false);
            }
            mList.add(bean);
            bean = null;
        }
    }

    public void initAdapter(){
        if(mInfoListAdapter == null){
            mInfoListAdapter = new MyInfoListAdapter(getmContext(), mList, R.layout.layout_consult_info_item);
        }
        mLvInfos.setAdapter(mInfoListAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mLvInfos.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        if(position == mCurrentSelectedPosition)return;
        ConsultClassBean item = (ConsultClassBean)parent.getAdapter().getItem(position);
        mList.get(mCurrentSelectedPosition).setSelected(false);
        mCurrentSelectedPosition = position;
        item.setSelected(true);

        mInfoListAdapter.notifyDataSetChanged();
        if(mOnHomePageChangeListener != null) {
            switch (item.getNameResId()) {
                case R.string.consult_present:
                    mOnHomePageChangeListener.onNeedConsultPageShow();
                    break;
                case R.string.competition_present:
                    mOnHomePageChangeListener.onNeedCompetitionPageShow();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initTitleBar();
    }

    public interface OnHomePageChangeListener{
        void onNeedConsultPageShow();
        void onNeedCompetitionPageShow();
    }

    public void setOnHomePageChangeListener(OnHomePageChangeListener onHomePageChangeListener){
        this.mOnHomePageChangeListener = onHomePageChangeListener;
    }

    public OnHomePageChangeListener getOnHomePageChangeListener(){
        return mOnHomePageChangeListener;
    }
}
