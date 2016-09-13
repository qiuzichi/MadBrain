package com.unipad.brain.quickPoker.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * ITEM的对应可序化队列属性
 * */
public class ChannelItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6465237897027410019L;
	/**
	 * 栏目对应ID
	 * */
	public Integer id;
	/**
	 * 栏目对应NAME
	 * */
	public String name;
	/**
	 * 栏目在整体中的排序顺序 rank
	 * */
	public Integer resId;

	public Drawable drawable;
	public ChannelItem() {
	}

	public ChannelItem(int id,  int orderId,String name,Drawable drawable) {
		this.id = Integer.valueOf(id);
		this.resId = Integer.valueOf(orderId);
		this.name = name;
		this.drawable = drawable;
	}



//	public void setId(int paramInt) {
//		this.id = Integer.valueOf(paramInt);
//	}






	public String toString() {
		return "CardItem [id=" + this.id + ", name=" + this.name;
	}
}