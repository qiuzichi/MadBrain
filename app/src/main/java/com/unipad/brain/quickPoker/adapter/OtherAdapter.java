package com.unipad.brain.quickPoker.adapter;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.unipad.brain.R;
import com.unipad.brain.quickPoker.entity.ChannelItem;
import com.unipad.brain.quickPoker.entity.PokerEntity;
import com.unipad.common.ViewHolder;
import com.unipad.common.adapter.CommonAdapter;

public class OtherAdapter extends CommonAdapter<ChannelItem> {




	public OtherAdapter(Context context, List<ChannelItem> datas, int layoutId) {
		super(context, datas, layoutId);
	}

	public void setData(List<ChannelItem> datas){
		if (datas != null){
			if (mDatas == null){
				mDatas = datas;
			}else {
				mDatas.clear();
				mDatas.addAll(datas);
			}
			notifyDataSetChanged();
		}

	}
	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		if (mDatas != null && mDatas.size() != 0) {
			return mDatas.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void convert(ViewHolder holder, ChannelItem channelItem) {
		ImageView item_text = (ImageView)holder.getView(R.id.text_item);
		item_text.setImageDrawable(channelItem.drawable);
	}


	/** 获取频道列表 */
	public List<ChannelItem> getChannnelLst() {
		return mDatas;
	}

	/** 添加频道列表 */
	public void addItem(ChannelItem channel) {
		boolean  isAddsuccess = false;
		for (int i = 0; i < mDatas.size(); i++) {
			if (channel.id < mDatas.get(i).id) {
				mDatas.add(i,channel);
				isAddsuccess = true;
				break;
			}
		}
		if (!isAddsuccess) {
			mDatas.add(channel);
		}
		notifyDataSetChanged();
	}

	/** 设置删除的position */


	/** 删除频道列表 */
	public void remove(int position) {
		if (position >= 0 &&  position < mDatas.size()) {
			mDatas.remove(position);

			notifyDataSetChanged();
		}
	}


}