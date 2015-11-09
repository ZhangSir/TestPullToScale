package com.test.pulltoscale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PullToScaleScrollView extends PullToScaleBaseView{

	private ScrollView scrollView;
	
	public PullToScaleScrollView(Context context) {
		super(context);
	}

	public PullToScaleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToScaleScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean isReadyForDrag() {
		if (null == scrollView || null == headerView)
			return false;
		if (scrollView.getScrollY() == 0) {
			return (headerView.getTop() + headerView.getHeight()) >= getTop();
		}
		return false;
	}

	@Override
	protected boolean isReadyForDragDown() {
		if (null == scrollView || null == headerView)
			return false;
		if (scrollView.getScrollY() == 0) {
			if ((headerView.getTop() + headerView.getHeight()) >= getTop()) {
				return headerView.getHeight() == minHeight;
			}
		}
		return false;
	}

	@Override
	protected boolean isReadyForDragUp() {
		if (null == scrollView || null == headerView)
			return false;
		if (scrollView.getScrollY() == 0) {
			if ((headerView.getTop() + headerView.getHeight()) >= getTop()) {
				return headerView.getHeight() == maxHeight;
			}
		}
		return false;
	}

	public ScrollView getScrollView() {
		return scrollView;
	}

	public void setScrollView(ScrollView scrollView) {
		this.scrollView = scrollView;
	}

}
