package com.test.pulltoscale;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import com.test.pulltoscale.view.PullToScaleScrollView;

public class PullToScaleScrollViewActivity extends Activity {
	private final String TAG = PullToScaleScrollViewActivity.class.getSimpleName();

	private PullToScaleScrollView ptzView;
	private ScrollView mScrollView;
	
	/** ListView的HeaderView*/
	private View mHeaderView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_to_scale_scroll_view);
		
		ptzView = (PullToScaleScrollView) this.findViewById(R.id.ptz_pull_to_scale_scroll_view);
		mScrollView = (ScrollView) this.findViewById(R.id.sv_pull_to_scale_scroll_view);
		mHeaderView = this.findViewById(R.id.layout_pull_to_scale_scroll_view_header);
		
		ptzView.setScrollView(mScrollView);
		ptzView.setHeaderView(mHeaderView);
		
		//允许设置ContentView的最大高度
		ptzView.setAllowResetContentViewMaxHeight(true);
//		ptzView.setContentViewMaxHeight(height);//高度不指定，由组件自己计算
		
		//设置HeaderView的最小化高度
		ptzView.setMinHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
				getResources().getDisplayMetrics()));
		
	}


}
