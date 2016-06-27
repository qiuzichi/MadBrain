package com.unipad.brain.consult.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import com.unipad.brain.R;
import com.unipad.brain.consult.entity.ConsultClassBean;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;

import java.util.List;

/**
 * Created by 63 on 2016/6/27.
 */
public class MyInfoListAdapter extends CommonAdapter<ConsultClassBean>{

    public MyInfoListAdapter(Context context, List<ConsultClassBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, ConsultClassBean consultClassBean) {
        holder.setImageResource(R.id.iv_info_label, consultClassBean.getLabelResId());
        holder.setTextResource(R.id.tv_info_name, consultClassBean.getNameResId());
        holder.setVisible(R.id.iv_item_selected_label, consultClassBean.isSelected());
    }
}


