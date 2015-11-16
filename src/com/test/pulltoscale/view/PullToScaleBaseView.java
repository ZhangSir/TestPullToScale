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
 * ��View�̳���LinearLayout��֧�ָ��ݱ��϶��������ListView�����϶�λ�ö�HeaderView�������ţ�<p>
 * ʹ��ʱ�����ֶ����ñ��϶��������ListView�ȣ���Ҫ�������ŵ�HeaderView��Ĭ��ȥHeaderView�ĳ�ʼ�߶�Ϊ���ŵ����߶�maxHeight����<p>
 * �Լ�Ҫ���ŵ�����С�߶�minHeight�������������С�߶ȣ���Ĭ��Ϊ0����<p>
 * @author zhangshuo
 */
public abstract class PullToScaleBaseView extends LinearLayout{

	private final String TAG = PullToScaleBaseView.class.getSimpleName();
	
	private Context mContext;
	
	/** header���ʱ�ĸ߶�*/
	protected int maxHeight = 0;
	/** header��С��ʱ�ĸ߶�*/
	protected int minHeight = 0;
	/** header��ǰ�ĸ߶�*/
	protected int currentHeight = 0;
	/** header���û��϶�����ʱ�Ƿ����������С�����ٽ��״̬ʱ�ĸ߶�*/
	protected int anchorHeight = minHeight + (maxHeight - minHeight)/2;
	/** ����Ƿ����û��϶��Ĺ�����*/
	private boolean mIsBeingDragged;
	
	private int mTouchSlop;
	
	private float mLastMotionY;
	private float mLastMotionX;
	
	protected View headerView;
	
	/**ContentView�����߶�*/
	protected int contentViewMaxHeight = -1;
	
	/**����Ƿ������ڵ�һ�δ���ʱ����ContentView��MaxHeight*/
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
	 * �жϵ�ǰheaderview�Ƿ�������ק��<p>
	 * �ж���������HeaderView��top���ڵ��ڱ��϶��������listView�ȣ���top������£�<p>
	 * 			1���û�������ʱ��headerview��heightΪ��С��ʱ�ĸ߶�minHeightʱ��������ק��<p>
	 * 			2���û�������ʱ��headerview��heightΪ���ʱ�ĸ߶�maxHeightʱ��������ק��<p>
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
		
		/*��һ�δ���ʱ��ΪContentView�������߶�*/
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
	 * �����϶��¼�
	 * @param lastDiffY �����϶���λ�����ϴ��϶���λ�õľ����
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
	 * �ָ�����󻯻���С��
	 */
	@SuppressLint("NewApi")
	private void smoothScrollBack(){
		if(currentHeight != maxHeight && currentHeight != minHeight){
			if(currentHeight > anchorHeight){
				//�����ٽ�㣬�ָ������
				mValueAnimator.setIntValues(currentHeight, maxHeight);
				mValueAnimator.start();
			}else if(currentHeight <= anchorHeight){
				//С�ڵ����ٽ�㣬�ָ�����С��
				mValueAnimator.setIntValues(currentHeight, minHeight);
				mValueAnimator.start();
			}
		}
	}

	 /**
	  * ��ȡHeaderView���ʱ�ĸ߶�
	  * @return
	  */
	 public int getMaxHeight() {
		return maxHeight;
	}

	 /**
	  * ����HeaderView���ʱ�ĸ߶�
	  * @param maxHeight
	  */
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		this.currentHeight = this.maxHeight;
	}

	/**
	 * ��ȡHeaderView��С��ʱ�ĸ߶�
	 * @return
	 */
	public int getMinHeight() {
		return minHeight;
	}

	/**
	 * ����HeaderView��С��ʱ�ĸ߶�
	 * @param minHeight
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}
	
	/**
	 * HeaderView���û��϶�����ʱ�Ƿ����������С�����ٽ��״̬ʱ�ĸ߶�
	 * @return
	 */
	public int getAnchorHeight() {
		return anchorHeight;
	}

	/**
	 * ��ȡ��Ҫ��̬�仯��С��HeaderView
	 * @return
	 */
	public View getHeaderView() {
		return headerView;
	}

	/**
	 * ������Ҫ��̬�仯��С��HeaderView
	 * @param mHeaderView
	 */
	public void setHeaderView(View headerView) {
		this.headerView = headerView;
	}

	/**
	 * ΪcontentView����һ����ʾʱ�����߶ȣ��������Ա�����contentView����headerView�Ĵ�С�ı������ƶ�ʱ��
	 * contentView�ĸ߶�Ҳ���иı䣬��������contentView�ػ棬������ɻ���Ч�����ؿ��ٵ����⣻
	 * ���������������ListView����֤����ΪListview���ù����߶Ⱥ�ListView�ٶ��������оͲ���Ҫ�ػ���View�����������ȴ��������
	 * @param height ���ڵ���0��Ч
	 */
	public void setContentViewMaxHeight(int height){
		this.contentViewMaxHeight = height;
	}
	
	/**
	 * ΪcontentView����һ����ʾʱ�����߶ȣ��������Ա�����contentView����headerView�Ĵ�С�ı������ƶ�ʱ��
	 * contentView�ĸ߶�Ҳ���иı䣬��������contentView�ػ棬������ɻ���Ч�����ؿ��ٵ����⣻
	 * ���������������ListView����֤����ΪListview���ù����߶Ⱥ�ListView�ٶ��������оͲ���Ҫ�ػ���View�����������ȴ��������
	 */
	public int getContentViewMaxHeight(){
		return this.contentViewMaxHeight;
	}
	
	/**
	 * ����Ƿ������ڵ�һ�δ���ʱ����ContentView��MaxHeight��Ĭ��Ϊtrue����������
	 * ����ΪtrueΪ������ʱ������setContentViewMaxHeight(int height)��ָ��ContentView�����߶ȣ�
	 * �����ָ�������Զ��ѵ�ǰContentView�ĸ߶� + headerView�����߶� - headerView����С�߶�ΪContentView�����߶ȣ�
	 * @return
	 */
	public boolean isAllowResetContentViewMaxHeight() {
		return allowResetContentViewMaxHeight;
	}

	/**
	 * ����Ƿ������ڵ�һ�δ���ʱ����ContentView��MaxHeight;
	 * ����ΪtrueΪ������ʱ������setContentViewMaxHeight(int height)��ָ��ContentView�����߶ȣ�
	 * �����ָ�������Զ��ѵ�ǰContentView�ĸ߶� + headerView�����߶� - headerView����С�߶�ΪContentView�����߶ȣ�
	 * @param allowResetContentViewMaxHeight��Ĭ��Ϊtrue����������
	 */
	public void setAllowResetContentViewMaxHeight(
			boolean allowResetContentViewMaxHeight) {
		this.allowResetContentViewMaxHeight = allowResetContentViewMaxHeight;
	}

	/**
	 * �жϵ�ǰheaderview�Ƿ�������ק��<p>
	 * @return
	 */
	protected abstract boolean isReadyForDrag();
	 
	 /**
	  * �Ƿ�����������ק
	  * @return
	  */
	 protected abstract boolean isReadyForDragDown();
	 
	 /**
	  * �Ƿ�����������ק
	  * @return
	  */
	 protected abstract boolean isReadyForDragUp();
	 
	 /**
	  *ΪcontentView����һ����ʾʱ�����߶ȣ��������Ա�����contentView����headerView�Ĵ�С�ı������ƶ�ʱ��
	  * contentView�ĸ߶�Ҳ���иı䣬��������contentView�ػ棬������ɻ���Ч�����ؿ��ٵ����⣻
	  * ����û�δָ��contentViewMaxHeight�����Զ��ѵ�ǰContentView�ĸ߶� + headerView�����߶� - headerView����С�߶�ΪContentView�����߶ȣ�
	  * ���������������ListView����֤����ΪListview���ù����߶Ⱥ�ListView�ٶ��������оͲ���Ҫ�ػ���View�����������ȴ��������
	  */
	 protected abstract void setContentViewMaxHeight();
	
	/**
	 * ��ȡHeaderView���Ź��̵ļ�����
	 * @return
	 */
	public OnPullZoomListener getOnPullZoomListener() {
		return onPullZoomListener;
	}

	/**
	 * ����HeaderView���Ź��̵ļ�����
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
