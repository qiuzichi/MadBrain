package com.unipad.brain.quickPoker.view.widget;

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.quickPoker.entity.ChannelItem;
import com.unipad.brain.quickPoker.entity.PokerEntity;
import com.unipad.common.Constant;
import com.unipad.utils.LogUtil;

public class QuickPokerBrowseVerticalView extends LinearLayout {

	private static final String TAG = QuickPokerBrowseVerticalView.class
			.getSimpleName();

	public QuickPokerBrowseVerticalView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public QuickPokerBrowseVerticalView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		try {
			this.initView(context);
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		}
	}

	private void initView(Context context) throws Exception {
		setOrientation(LinearLayout.VERTICAL);
		this.layoutPoker(context);
	}

	/**
	 * 扑克牌规则：一副扑克牌52张，分三行显示：第一行和第三行显示16张，第二行显示20张；如果是两副扑克牌，以此类推。
	 */
	private void layoutPoker(Context context) {
		int pokerNums = 0;
		List<Integer> pokerList = new ArrayList<Integer>();
		for (int pair = 0; pair < PokerEntity.pairs; pair++) {
			for (int lineNums : PokerEntity.basic_order) {
				pokerNums += lineNums;
				pokerList.add(lineNums);
			}
		}

		if (pokerNums > PokerEntity.pairNums * PokerEntity.pairs) {
			return;
		}

		int rightLayoutH = (int) context.getResources().getDimension(
				R.dimen.quick_poker_right_w);
		int availableWidth = (int)(App.screenWidth*4.6/5.8)
				- rightLayoutH - getPaddingLeft() - getPaddingRight();
		LogUtil.i(TAG, "rightLayoutH::" + rightLayoutH + ", getPaddingLeft()::"
				+ getPaddingLeft() + ", getPaddingRight()::"
				+ getPaddingRight() + ", availableWidth::" + availableWidth);
		int startIndex = 0, endIndex = 0, pokerMargin = 0, basicMargin = 0;
		int pokerWith = PokerEntity.getInstance().getPokerWith();
		int bottomMargin = (int) context.getResources().getDimension(
				R.dimen.poker_line_magin);

		RelativeLayout rLayout = null;
		LayoutParams layoutParams = null;

		ImageView pokerImage = null;
		RelativeLayout.LayoutParams pokerLayoutParams = null;
		for (int b = 0; b < pokerList.size(); b++) {
			rLayout = new RelativeLayout(context);
			layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.weight = 1;
			layoutParams.bottomMargin = bottomMargin;
			rLayout.setLayoutParams(layoutParams);
			addView(rLayout, b);

			startIndex = endIndex;
			endIndex += pokerList.get(b);
			LogUtil.i(TAG, "startIndex::" + startIndex + ", endIndex::"
					+ endIndex);

			pokerMargin = 0;
			basicMargin = (availableWidth - pokerWith) / (pokerList.get(b) - 1);

			for (int i = startIndex; i < endIndex; i++) {
				pokerImage = new ImageView(context);
				pokerLayoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				pokerImage.setLayoutParams(pokerLayoutParams);
				pokerLayoutParams.leftMargin = pokerMargin;
				pokerMargin += basicMargin;
				//pokerImage.setImageResource(R.drawable.bg_poker);
				rLayout.addView(pokerImage);
			}
		}
	}

	public void clear(){

	}
	/**
	 * 展示扑克牌面
	 */
	public void showPokerFace() {
		try {
			RelativeLayout rLayout = null;
			ImageView imageView = null;
			ArrayList<ChannelItem> pokerSortArray = PokerEntity.getInstance()
					.getPokerSortArray();
			int LayoutedTotal = 0;
			if (pokerSortArray != null
					&& pokerSortArray.size() == PokerEntity.pairNums) {
				for (int b = 0; b < PokerEntity.basic_order.length; b++) {
					rLayout = (RelativeLayout) getChildAt(b);
					int childCount = rLayout.getChildCount();

					for (int i = 0; i < childCount; i++) {
						imageView = (ImageView) rLayout.getChildAt(i);
						imageView.setImageDrawable(pokerSortArray.get(i
								+ LayoutedTotal).drawable);
					}

					LayoutedTotal += childCount;
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		}
	}
}
