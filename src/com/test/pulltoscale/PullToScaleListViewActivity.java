package com.test.pulltoscale;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.test.pulltoscale.view.PullToScaleBaseView.OnPullZoomListener;
import com.test.pulltoscale.view.PullToScaleListView;

public class PullToScaleListViewActivity extends Activity {
	private final String TAG = PullToScaleListViewActivity.class.getSimpleName();

	private PullToScaleListView ptzView;
	private ListView mListView;
	
	/** ListView的HeaderView*/
	private View mHeaderView;
	private TextView tvHeader;
	
	private int offsetWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_to_scale_list_view);

		ptzView = (PullToScaleListView) this.findViewById(R.id.ptz_pull_to_scale_list_view);
		mListView = (ListView) this.findViewById(R.id.lv_pull_to_scale_list_view);
		
		mHeaderView = this.findViewById(R.id.layout_pull_to_scale_list_view_header);
		tvHeader = (TextView) mHeaderView.findViewById(R.id.tv_pull_to_scale_view_header);
		//初始化header的大小
		ViewGroup.LayoutParams lp = mHeaderView.getLayoutParams();
		lp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300,
				getResources().getDisplayMetrics());
		lp.width = LayoutParams.MATCH_PARENT;
		mHeaderView.setLayoutParams(lp);
		
		ptzView.setListView(mListView);
		ptzView.setHeaderView(mHeaderView);
		
		//设置HeaderView的最小化高度
		ptzView.setMinHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
				getResources().getDisplayMetrics()));
		
		
		
		ptzView.setOnPullZoomListener(new OnPullZoomListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onPullZooming(int currenHeaderHeight) {
				if(offsetWidth == 0) offsetWidth = tvHeader.getLeft();
				float franch = (currenHeaderHeight - ptzView.getMinHeight())/(float)(ptzView.getMaxHeight() - ptzView.getMinHeight());
				
				float franch1 = (currenHeaderHeight - ptzView.getAnchorHeight())/(float)(ptzView.getMaxHeight() - ptzView.getAnchorHeight());
				
				tvHeader.setTranslationX(offsetWidth * (franch - 1));
				
			}
			
			@Override
			public void onPullZoomEnd(int currenHeaderHeight) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		 String[] adapterData = new String[]{"Activity", "Service", "Content Provider", "Intent", "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient",
	                "DDMS", "Android Studio", "Fragment", "Loader", "Activity", "Service", "Content Provider", "Intent",
	                "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient", "Activity", "Service", "Content Provider", "Intent",
	                "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient"};

		 mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adapterData));

		 mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                Log.e(TAG, "position = " + position);
	            }
	        });
		 
		 
		 
	}
	
}
