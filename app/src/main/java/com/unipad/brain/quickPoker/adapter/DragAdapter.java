package com.unipad.brain.quickPoker.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.unipad.brain.R;
import com.unipad.brain.quickPoker.entity.ChannelItem;
import com.unipad.brain.quickPoker.entity.PokerEntity;

import java.util.ArrayList;
import java.util.List;

public class DragAdapter extends BaseAdapter {
	/** TAG */
	private final static String TAG = "DragAdapter";
	private View dragView;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的频道列表） */
	public List<ChannelItem> channelList;

	/** 要删除的position */
	public int remove_position = -1;

	public DragAdapter(Context context, List<ChannelItem> channelList) {
		this.context = context;
		if (channelList == null) {
			this.channelList = new ArrayList<ChannelItem>();
		} else {
			this.channelList = channelList;
		}
	}

	public List<ChannelItem> getChannelList() {
		return channelList;
	}

	@Override
	public int getCount() {
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.quick_poker_v_answer_item, null);
			holder.img = (ImageView)convertView.findViewById(R.id.text_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.img.setImageDrawable(getItem(position).drawable);
		// item_text.setText(channel.getName());
		// if ((position == 0) || (position == 1)) {
		// item_text.setEnabled(false);
		// }

		return convertView;
	}
	static class ViewHolder
	{
		public ImageView img;

	}
	/** 添加频道列表 */
	public void addItem(ChannelItem channel) {
		for (int i = channelList.size()-1;i>0;i--) {
			if (channelList.get(i).resId == channel.resId){
				return;
			}
		}
		channelList.add(channel);
		notifyDataSetChanged();
	}

	/** 拖动变更频道排序 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		ChannelItem dragItem = getItem(dragPostion);
		Log.d(TAG, "startPostion=" + dragPostion + ";endPosition="
				+ dropPostion);
		if (dragPostion < dropPostion) {
			if (dropPostion == getCount()){//落点是不是最后一个位置，
				channelList.add(dragItem);
			}else {
				channelList.add(dropPostion + 1, dragItem);
			}
			channelList.remove(dragPostion);
		} else {
			channelList.add(dropPostion+1, dragItem);
			channelList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}

	/** 获取频道列表 */
	public List<ChannelItem> getChannnelLst() {
		return channelList;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		//remove_position = position;
		//notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public void remove(int position) {
		if (position > 0 || position < channelList.size()) {
			channelList.remove(position);

			notifyDataSetChanged();
		}

	}
	public void clearData(){
		if (channelList != null){
			channelList.clear();
			notifyDataSetChanged();
		}
	}
	/** 设置频道列表 */
	public void setListDate(List<ChannelItem> list) {
		channelList = list;
	}

	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}

	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	/** 显示放下的ITEM */
	public void setDropItem(View view) {
		dragView = view;
	}

	public void showDropItem() {
		if (dragView != null) {
			dragView.setVisibility(View.VISIBLE);
		}
	}

}