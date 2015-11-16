package com.test.pulltoscale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class PullToScaleListView extends PullToScaleBaseView {

	private ListView listView;

	public PullToScaleListView(Context context) {
		super(context);
	}

	public PullToScaleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToScaleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 判断当前listView的headerview是否允许拖拽；
	 * <p>
	 * 判断条件：在listView的HeaderView的top大于等于listView的top的情况下返回true
	 * <p>
	 * 
	 * @return
	 */
	protected boolean isReadyForDrag() {
		if (null == listView || null == headerView)
			return false;
		if (listView.getFirstVisiblePosition() == 0) {
			return (headerView.getTop() + headerView.getHeight()) >= getTop();
		}
		return false;
	}

	/**
	 * 是否允许向下拖拽
	 * 
	 * @return
	 */
	protected boolean isReadyForDragDown() {
		if (null == listView || null == headerView)
			return false;
		if (listView.getFirstVisiblePosition() == 0) {
			if ((headerView.getTop() + headerView.getHeight()) >= getTop()) {
				return headerView.getHeight() == minHeight;
			}
		}
		return false;
	}

	/**
	 * 是否允许向上拖拽
	 * 
	 * @return
	 */
	protected boolean isReadyForDragUp() {
		if (null == listView || null == headerView)
			return false;
		if (listView.getFirstVisiblePosition() == 0) {
			if ((headerView.getTop() + headerView.getHeight()) >= getTop()) {
				return headerView.getHeight() == maxHeight;
			}
		}
		return false;
	}

	/**
	 * 获取操作对象ListView， 如果未手动设置，则自动去当前PullToZoomView的第0个child为操作对象，并强制转化为listView
	 * 
	 * @return
	 */
	public ListView getListView() {
		return listView;
	}

	/**
	 * 设置操作对象ListView，如果不手动设置，则自动去当前PullToZoomView的第0个child为操作对象，并强制转化为listView
	 * 
	 * @param mListView
	 */
	public void setListView(ListView listView) {
		this.listView = listView;
	}

	@Override
	protected void setContentViewMaxHeight() {
		if(contentViewMaxHeight < 0){
			contentViewMaxHeight = listView.getHeight() + (maxHeight - minHeight);
		}
		ViewGroup.LayoutParams lp = listView.getLayoutParams();
		lp.height = contentViewMaxHeight;
		listView.setLayoutParams(lp);
	}
}
