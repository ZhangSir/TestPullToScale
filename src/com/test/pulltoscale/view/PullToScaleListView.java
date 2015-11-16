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
	 * �жϵ�ǰlistView��headerview�Ƿ�������ק��
	 * <p>
	 * �ж���������listView��HeaderView��top���ڵ���listView��top������·���true
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
	 * �Ƿ�����������ק
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
	 * �Ƿ�����������ק
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
	 * ��ȡ��������ListView�� ���δ�ֶ����ã����Զ�ȥ��ǰPullToZoomView�ĵ�0��childΪ�������󣬲�ǿ��ת��ΪlistView
	 * 
	 * @return
	 */
	public ListView getListView() {
		return listView;
	}

	/**
	 * ���ò�������ListView��������ֶ����ã����Զ�ȥ��ǰPullToZoomView�ĵ�0��childΪ�������󣬲�ǿ��ת��ΪlistView
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
