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

	private ImageView item_text;
	/** 是否可见 */
	boolean isVisible = true;
	/** 要删除的position */
	public int remove_position = -1;

	public OtherAdapter(Context context, List<ChannelItem> datas, int layoutId) {
		super(context, datas, layoutId);
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
		item_text = (ImageView)holder.getView(R.id.text_item);
		item_text.setImageBitmap(PokerEntity.getInstance().getBitmap(channelItem.resId));
	}


	/** 获取频道列表 */
	public List<ChannelItem> getChannnelLst() {
		return mDatas;
	}

	/** 添加频道列表 */
	public void addItem(ChannelItem channel) {
		boolean  isAddsuccess = false;
		for (int i = 0; i < mDatas.size(); i++) {
			if (channel.id < mDatas.get(i).resId) {
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
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public void remove() {
		if (remove_position != -1) {
			mDatas.remove(remove_position);
			remove_position = -1;
			notifyDataSetChanged();
		}
	}

	/** 设置频道列表 */
	public void setListDate(List<ChannelItem> list) {
		mDatas = list;
	}

	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}

	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}

}