package com.test.pulltoscale.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * 该View继承自LinearLayout，支持根据被拖动组件（如ListView）的拖动位置对HeaderView进行缩放；<p>
 * 使用时，请手动设置被拖动组件（如ListView等）和要进行缩放的HeaderView（默认去HeaderView的初始高度为缩放的最大高度maxHeight），<p>
 * 以及要缩放到的最小高度minHeight（如果不设置最小高度，则默认为0）；<p>
 * @author zhangshuo
 */
public abstract class PullToScaleBaseView extends LinearLayout{

	private final String TAG = PullToScaleBaseView.class.getSimpleName();
	
	private Context mContext;
	
	/** header最大化时的高度*/
	protected int maxHeight = 0;
	/** header最小化时的高度*/
	protected int minHeight = 0;
	/** header当前的高度*/
	protected int currentHeight = 0;
	/** header在用户拖动结束时是否进行最大或最小化的临界点状态时的高度*/
	protected int anchorHeight = minHeight + (maxHeight - minHeight)/2;
	/** 标记是否在用户拖动的过程中*/
	private boolean mIsBeingDragged;
	
	private int mTouchSlop;
	
	private float mLastMotionY;
	private float mLastMotionX;
	
	protected View headerView;
	
	/**ContentView的最大高度*/
	protected int contentViewMaxHeight = -1;
	
	/**标记是否允许在第一次触摸时设置ContentView的MaxHeight*/
	private boolean allowResetContentViewMaxHeight = true;
	
	private ValueAnimator mValueAnimator;
	
	protected OnPullZoomListener onPullZoomListener;
	
	public PullToScaleBaseView(Context context) {
		this(context, null);
	}

	public PullToScaleBaseView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public PullToScaleBaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context, attrs);
	}

	@SuppressLint("NewApi")
	private void init(Context context, AttributeSet attrs){
		this.mContext = context;
		
		ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        
        mValueAnimator = new ValueAnimator();
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(200);
        mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if(null == headerView) return;
				
				currentHeight = (int) animation.getAnimatedValue();
				
				ViewGroup.LayoutParams localLayoutParams = headerView.getLayoutParams();
		        localLayoutParams.height = currentHeight;
		        headerView.setLayoutParams(localLayoutParams);
		        
		        if (onPullZoomListener != null) {
		            onPullZoomListener.onPullZooming(currentHeight);
		        }
			}
		});
        
        mValueAnimator.addListener(new AnimatorListenerAdapter() {

        	@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
			}
        	
        	
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				mIsBeingDragged = false;
				if (onPullZoomListener != null) {
                    onPullZoomListener.onPullZoomEnd(currentHeight);
                }
			}

		});
	}
	
	/**
	 * 判断当前headerview是否允许拖拽；<p>
	 * 判断条件：在HeaderView的top大于等于被拖动组件（如listView等）的top的情况下：<p>
	 * 			1、用户向下拉时，headerview的height为最小化时的高度minHeight时，允许拖拽；<p>
	 * 			2、用户向上推时，headerview的height为最大化时的高度maxHeight时，允许拖拽；<p>
	 * @return
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		
		if(null == headerView) return super.onInterceptTouchEvent(event);
		if(this.maxHeight == 0) {
			this.maxHeight = headerView.getHeight();
			this.currentHeight = this.maxHeight;
		}
		if(this.anchorHeight == 0) this.anchorHeight = this.minHeight + (this.maxHeight - this.minHeight)/2;
		
		/*第一次触摸时，为ContentView设置最大高度*/
		if(allowResetContentViewMaxHeight){
			setContentViewMaxHeight();
			allowResetContentViewMaxHeight = false;
		}
		
		final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true;
        }
        switch (action) {
        case MotionEvent.ACTION_DOWN: {
	            if (isReadyForDrag()) {
	                mLastMotionY = event.getY();
	                mLastMotionX = event.getX();
	                mIsBeingDragged = false;
	            }
	            break;
        	}
            case MotionEvent.ACTION_MOVE: {
                if (isReadyForDrag()) {
                    final float y = event.getY(), x = event.getX();
                    final float diffY, absDiffX;

                    absDiffX = Math.abs(x - mLastMotionX);
                    diffY = y - mLastMotionY;

                    if((diffY > 0 && isReadyForDragDown())
                    		|| (diffY < 0 && isReadyForDragUp())){
                    	if (Math.abs(diffY) > mTouchSlop && Math.abs(diffY) > absDiffX) {
                        	mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                        }
                    }else{
                    	return super.onInterceptTouchEvent(event);
                    }
                }
                break;
            }
        }

        return mIsBeingDragged;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
		 if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
	            return false;
	        }
	        switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN: {
	                if (isReadyForDrag()) {
	                    mLastMotionY = event.getY();
	                    mLastMotionX = event.getX();
	                    return true;
	                }
	                break;
	            }
	            case MotionEvent.ACTION_MOVE: {
	                if (mIsBeingDragged) {
	                	int lastDiffY = (int) (event.getY() - mLastMotionY);
	                	pullEvent(lastDiffY);
	                    mLastMotionY = event.getY();
	                    mLastMotionX = event.getX();
	                    
	                    return true;
	                }
	                break;
	            }
	            case MotionEvent.ACTION_CANCEL:
	            case MotionEvent.ACTION_UP: {
	                if (mIsBeingDragged) {
	                    smoothScrollBack();
	                    return true;
	                }
	                
	                break;
	            }
	        }
	        return false;
	}
	
	/**
	 * 处理拖动事件
	 * @param lastDiffY 本次拖动的位置与上次拖动的位置的距离差
	 */
	private void pullEvent(int lastDiffY) {
		if(null == headerView) return;
        
		currentHeight = currentHeight + lastDiffY;
        
        if(currentHeight > maxHeight) currentHeight = maxHeight;
        if(currentHeight < minHeight) currentHeight = minHeight;
        
        ViewGroup.LayoutParams localLayoutParams = headerView.getLayoutParams();
        localLayoutParams.height = currentHeight;
        headerView.setLayoutParams(localLayoutParams);
        
        if (onPullZoomListener != null) {
            onPullZoomListener.onPullZooming(currentHeight);
        }
    }
	
	/**
	 * 恢复到最大化或最小化
	 */
	@SuppressLint("NewApi")
	private void smoothScrollBack(){
		if(currentHeight != maxHeight && currentHeight != minHeight){
			if(currentHeight > anchorHeight){
				//大于临界点，恢复到最大化
				mValueAnimator.setIntValues(currentHeight, maxHeight);
				mValueAnimator.start();
			}else if(currentHeight <= anchorHeight){
				//小于等于临界点，恢复到最小化
				mValueAnimator.setIntValues(currentHeight, minHeight);
				mValueAnimator.start();
			}
		}
	}

	 /**
	  * 获取HeaderView最大化时的高度
	  * @return
	  */
	 public int getMaxHeight() {
		return maxHeight;
	}

	 /**
	  * 设置HeaderView最大化时的高度
	  * @param maxHeight
	  */
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		this.currentHeight = this.maxHeight;
	}

	/**
	 * 获取HeaderView最小化时的高度
	 * @return
	 */
	public int getMinHeight() {
		return minHeight;
	}

	/**
	 * 设置HeaderView最小化时的高度
	 * @param minHeight
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}
	
	/**
	 * HeaderView在用户拖动结束时是否进行最大或最小化的临界点状态时的高度
	 * @return
	 */
	public int getAnchorHeight() {
		return anchorHeight;
	}

	/**
	 * 获取需要动态变化大小的HeaderView
	 * @return
	 */
	public View getHeaderView() {
		return headerView;
	}

	/**
	 * 设置需要动态变化大小的HeaderView
	 * @param mHeaderView
	 */
	public void setHeaderView(View headerView) {
		this.headerView = headerView;
	}

	/**
	 * 为contentView设置一个显示时的最大高度，这样可以避免在contentView跟随headerView的大小改变上下移动时，
	 * contentView的高度也进行改变，进而引发contentView重绘，最终造成滑动效果严重卡顿的问题；
	 * （这种情况，已在ListView下验证过，为Listview设置过最大高度后，ListView再动画过程中就不需要重绘子View，动画流畅度大大提升）
	 * @param height 大于等于0有效
	 */
	public void setContentViewMaxHeight(int height){
		this.contentViewMaxHeight = height;
	}
	
	/**
	 * 为contentView设置一个显示时的最大高度，这样可以避免在contentView跟随headerView的大小改变上下移动时，
	 * contentView的高度也进行改变，进而引发contentView重绘，最终造成滑动效果严重卡顿的问题；
	 * （这种情况，已在ListView下验证过，为Listview设置过最大高度后，ListView再动画过程中就不需要重绘子View，动画流畅度大大提升）
	 */
	public int getContentViewMaxHeight(){
		return this.contentViewMaxHeight;
	}
	
	/**
	 * 标记是否允许在第一次触摸时设置ContentView的MaxHeight，默认为true，允许设置
	 * 设置为true为允许，此时请设置setContentViewMaxHeight(int height)，指定ContentView的最大高度，
	 * 如果不指定，将自动已当前ContentView的高度 + headerView的最大高度 - headerView的最小高度为ContentView的最大高度；
	 * @return
	 */
	public boolean isAllowResetContentViewMaxHeight() {
		return allowResetContentViewMaxHeight;
	}

	/**
	 * 标记是否允许在第一次触摸时设置ContentView的MaxHeight;
	 * 设置为true为允许，此时请设置setContentViewMaxHeight(int height)，指定ContentView的最大高度，
	 * 如果不指定，将自动已当前ContentView的高度 + headerView的最大高度 - headerView的最小高度为ContentView的最大高度；
	 * @param allowResetContentViewMaxHeight，默认为true，允许设置
	 */
	public void setAllowResetContentViewMaxHeight(
			boolean allowResetContentViewMaxHeight) {
		this.allowResetContentViewMaxHeight = allowResetContentViewMaxHeight;
	}

	/**
	 * 判断当前headerview是否允许拖拽；<p>
	 * @return
	 */
	protected abstract boolean isReadyForDrag();
	 
	 /**
	  * 是否允许向下拖拽
	  * @return
	  */
	 protected abstract boolean isReadyForDragDown();
	 
	 /**
	  * 是否允许向上拖拽
	  * @return
	  */
	 protected abstract boolean isReadyForDragUp();
	 
	 /**
	  *为contentView设置一个显示时的最大高度，这样可以避免在contentView跟随headerView的大小改变上下移动时，
	  * contentView的高度也进行改变，进而引发contentView重绘，最终造成滑动效果严重卡顿的问题；
	  * 如果用户未指定contentViewMaxHeight，则将自动已当前ContentView的高度 + headerView的最大高度 - headerView的最小高度为ContentView的最大高度；
	  * （这种情况，已在ListView下验证过，为Listview设置过最大高度后，ListView再动画过程中就不需要重绘子View，动画流畅度大大提升）
	  */
	 protected abstract void setContentViewMaxHeight();
	
	/**
	 * 获取HeaderView缩放过程的监听器
	 * @return
	 */
	public OnPullZoomListener getOnPullZoomListener() {
		return onPullZoomListener;
	}

	/**
	 * 设置HeaderView缩放过程的监听器
	 * @param onPullZoomListener
	 */
	public void setOnPullZoomListener(OnPullZoomListener onPullZoomListener) {
		this.onPullZoomListener = onPullZoomListener;
	}

	public interface OnPullZoomListener {
	    	
		public void onPullZooming(int currenHeaderHeight);

        public void onPullZoomEnd(int currenHeaderHeight);
	        
	}
}
