package com.unipad.brain.quickPoker.view.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.quickPoker.entity.ChannelItem;
import com.unipad.brain.quickPoker.entity.PokerEntity;
import com.unipad.common.Constant;
import com.unipad.utils.DataTools;
import com.unipad.utils.LogUtil;

import org.xutils.image.ImageOptions;
import org.xutils.x;


public class QuickPokerBrowseHorizontalView extends HorizontalScrollView
		implements App.HandlerCallback {
	private static final String TAG = QuickPokerBrowseHorizontalView.class
			.getSimpleName();
	private static final int MSG_LAYOUT_POKER = 101;
	private Context mContext;
	private App.AppHandler mHandler = new App.AppHandler(this);
	/** 扑克牌所在的父布局 */
	private RelativeLayout mPokerLayout;
	/** 临时的View，用于计算扑克牌的宽度 */
	private float mScaleFactor = 1.0f;
	private ScaleGestureDetector mScaleGestureDetector;
	private boolean isFirst = true;

	public QuickPokerBrowseHorizontalView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mScaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener());
	}

	public QuickPokerBrowseHorizontalView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mScaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener());
		this.initView(context);

	}

	private void initView(Context context) {
		mContext = context;
		mPokerLayout = new RelativeLayout(context);
		addView(mPokerLayout);

		Bitmap bimap = BitmapFactory.decodeResource(this.getResources(), R.drawable.poker_fangkuai_01);
		LogUtil.e("",""+bimap.getWidth());
		PokerEntity.getInstance().setPokerWith(bimap.getWidth());
		PokerEntity.getInstance().setPokerHeigth(bimap.getHeight());
		layoutPokerView();
		/**post(new Runnable() {
			@Override
			public void run() {

				Message msg = mHandler.obtainMessage();
				msg.arg1 = PokerEntity.getInstance().getPokerWith();
				mHandler.sendEmptyMessage(MSG_LAYOUT_POKER);
			}
		});
		 */

	}




	@Override
	public void dispatchMessage(Message msg) {
		switch (msg.what) {
		case MSG_LAYOUT_POKER:
			this.layoutPokerView();
			break;
		default:
			break;
		}
	}

	private void layoutPokerView() {
// 用于排布扑克后，保存扑克的顺序

		int availableWidth = (int) (App.screenWidth * Constant.RIGHT_RATIO)
				- getPaddingLeft() - getPaddingRight();
		int basicMargin = (availableWidth - PokerEntity.getInstance().getPokerWith()) / 20;

		int margin = 0;
		ImageView pokerImage = null;
		RelativeLayout.LayoutParams pokerLayoutParams = null;
		for (int i = 0; i < PokerEntity.pairNums * PokerEntity.pairs; i++) {

			pokerImage = new ImageView(mContext);
			pokerLayoutParams = new RelativeLayout.LayoutParams(
					PokerEntity.getInstance().getPokerWith(), PokerEntity.getInstance().getPokerHeigth());
			pokerImage.setLayoutParams(pokerLayoutParams);
			pokerLayoutParams.leftMargin = margin;
			margin += basicMargin;
			//pokerImage.setImageResource(R.drawable.bg_poker);
			mPokerLayout.addView(pokerImage);
		}


	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (isFirst) {
			super.onLayout(changed, l, t, r, b);
			return;
		}
		RelativeLayout view = (RelativeLayout) getChildAt(0);
		LayoutParams firstChildParams = (LayoutParams) view
				.getLayoutParams();
		view.layout((int) (firstChildParams.leftMargin * mScaleFactor),
				(int) (firstChildParams.topMargin * mScaleFactor),
				(int) ((firstChildParams.leftMargin + view.getMeasuredWidth()
						* mScaleFactor)),
				(int) ((firstChildParams.topMargin + view.getMeasuredHeight()
						* mScaleFactor)));
		int count = view.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = view.getChildAt(i);
			if (child.getVisibility() != GONE) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child
						.getLayoutParams();
				child.layout(
						(int) (params.leftMargin * mScaleFactor),
						(int) (params.topMargin * mScaleFactor),
						(int) ((params.leftMargin + child.getMeasuredWidth()) * mScaleFactor),
						(int) ((params.topMargin + child.getMeasuredHeight()) * mScaleFactor));
			}
		}

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			// Apply limits to the zoom scale factor:
			mScaleFactor = Math.max(0.6f, Math.min(mScaleFactor, 1.5f));
			invalidate();
			requestLayout();
			// scrollTo(x, y)
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		isFirst = false;
		mScaleGestureDetector.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

	/**
	 * 展示扑克牌面
	 */
	public void showPokerFace() {
		try {
			ImageView imageView = null;
			ArrayList<ChannelItem> pokerSortArray = PokerEntity.getInstance()
					.getPokerSortArray();
			if (pokerSortArray != null
					&& pokerSortArray.size() == PokerEntity.pairNums) {
				for (int index = 0; index < mPokerLayout.getChildCount(); index++) {
					imageView = (ImageView) mPokerLayout.getChildAt(index);
					//imageView.setImageDrawable(null);
					/**x.image().bind(imageView, "drawable://" + pokerSortArray.get(index).resId, new ImageOptions.Builder()
							.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
							.setRadius(5)
							.build());
					 */
				imageView.setImageDrawable(pokerSortArray.get(index).drawable);

				}
			}
			scrollTo(0,0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
