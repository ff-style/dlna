package com.youplayer.player.frame;

import java.util.Stack;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.youplayer.core.You_Core;
import com.youplayer.core.struct.You_core_push_or_pop_page_data_t;
import com.youplayer.player.YouPlayerAbsoluteLayout;
import com.youplayer.player.R;

import com.youplayer.util.LOG;

public class YouPlayerContainerView extends FrameLayout implements YouPlayerActionHandler {
	private static final String TAG = "ContainerView";

	public static final int HIDE_BOTH = 0;
	public static final int HIDE_LEFT = 1;
	public static final int HIDE_RIGHT = 2;


	public static boolean canSlidingAround = false;

	public int viewLevel = 0;

	public YouPlayerViewControler currentViewControler;

	public Stack<YouPlayerViewControler> stack = new Stack<YouPlayerViewControler>();

	public int currentHideType = HIDE_BOTH;

	private GestureDetector mGestureDetector;
	private FlingGestureDetectorListener mGestureDetectorListener;
	private GestureDetector mInterruptDetector;

	private int criticalValues;
	private boolean mIsTouched = false;
	private int mAnimationDuration = 250;
	private int mGalleryWidth = 0;
	public int hideWidth;
	private long mScrollTimestamp = 0;
	private int mDetectScrollX = 50;

	private int fling_width;

	private YouPlayerAbsoluteLayout.LayoutParams layoutParams;

	private boolean mIsDragging = false;
	private float mCurrentOffset = 0.0f;
	private boolean isIntercept;
	private boolean isMoveable = false;
	private OnHideListener hideListener;
	
	private Scroller mScroller;

	public static YouPlayerContainerView instance;
	

	public YouPlayerContainerView(Context context) {
		this(context, null);
	}

	public YouPlayerContainerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public YouPlayerContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetectorListener = new FlingGestureDetectorListener();
		mGestureDetector = new GestureDetector(mGestureDetectorListener);
		mInterruptDetector = new GestureDetector(new InterruptGestureDetectorListener());
		fling_width = (int) getContext().getResources().getDimension(R.dimen.fling_width);
		mScroller = new Scroller(context);
		initHandler(context);
		instance = this;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		mGalleryWidth = right - left;
		criticalValues = mGalleryWidth / 3;
		hideWidth = mGalleryWidth * 5 / 6;
		super.onLayout(changed, left, top, right, bottom);
	}

	private Handler mHandler;
	private void initHandler(Context context) {
		mHandler = new Handler(context.getMainLooper());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result;
		if (!isIntercept && isMoveable) {
			if(ev == null)
				Log.v(TAG, "  onInterceptTouchEvent  ev == null  ");
			result = mInterruptDetector.onTouchEvent(ev);
		} else {
			result = super.onInterceptTouchEvent(ev);
		}
		return result;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean result = super.dispatchTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			isIntercept = false;
		}
		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 视图层级为1时容器才能左右移动
		if (viewLevel == 1) {
			if(event == null)
			Log.v(TAG, "  onTouchEvent  event == null  ");
			onContainerTouchEvent(event);
		}
		return true;
	}

	public boolean onContainerTouchEvent(MotionEvent event) {
		boolean consumed = mGestureDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mIsTouched || mIsDragging) {
				mIsTouched = false;
				mIsDragging = false;
				if (currentHideType == HIDE_BOTH) {
					if (layoutParams == null) {
						layoutParams = (YouPlayerAbsoluteLayout.LayoutParams) getLayoutParams();
						LOG.e(TAG, "onContainerTouchEvent", "layoutParams = " + layoutParams.toString());
					}
					int currX = layoutParams.x;
					if (Math.abs(currX) < criticalValues) {
						hide(HIDE_BOTH);
					} else if (currX >= criticalValues) {
						hide(HIDE_RIGHT);
					} else if (currX <= -criticalValues) {
							hide(HIDE_LEFT);
					}
				} else {
					hide(HIDE_BOTH);
				}
			}
		}
		return consumed;
	}

	public YouPlayerViewControler getCurrentViewControler() {
		return this.currentViewControler;
	}

	private void push( YouPlayerViewControler viewControler) {
		LOG.v(TAG, "push()", "current hideType : " + currentHideType);
		if (currentHideType != HIDE_BOTH) {
			snake(viewControler);
		} else {
			if (viewLevel > 0) {
				currentViewControler.onPause();
				currentViewControler.onStop();
				stack.add(currentViewControler);
				getChildAt(getChildCount() - 1).setVisibility(View.GONE);
			}
			addView(viewControler.getView());
		}
		currentViewControler = viewControler;
		viewLevel++;
		LOG.i(TAG, "push()", "viewLevel = " + viewLevel);
	}

	private void pop() {
		LOG.v(TAG, "pop()", "getChildCount = " + getChildCount());
		currentViewControler.finish();
		removeViewAt(getChildCount() - 1);
		if (getChildCount() > 0) {
			getChildAt(getChildCount() - 1).setVisibility(View.VISIBLE);
		}
		if (stack.size() > 0) {
			currentViewControler = null;
			currentViewControler = stack.pop();
			currentViewControler.onResume();
			LOG.v(TAG, "pop()","currentViewControler : " + currentViewControler.getClass());
		}
		viewLevel--;
		LOG.i(TAG, "pop()", "viewLevel = " + viewLevel);
	}

	private void popAll() {
		currentViewControler.finish();
		removeAllViews();
		if (stack.size() > 0) {
			stack.clear();
			LOG.v(TAG, "stack.clear()"," " );
		}
		viewLevel = 0;
		LOG.i(TAG, "popAll()", "viewLevel = " + viewLevel);
	}

	private void snake(YouPlayerViewControler view) {
		if (layoutParams == null) {
			layoutParams = (YouPlayerAbsoluteLayout.LayoutParams) getLayoutParams();
			LOG.e(TAG, "snake", "layoutParams = " + layoutParams.toString());
		}
		targetType = -1;
    	int delta = 0;
		switch (currentHideType) {
		case HIDE_RIGHT:
	    	delta = layoutParams.x - mGalleryWidth;
			break;
		case HIDE_BOTH:
			delta = layoutParams.x;
			break;
		case HIDE_LEFT:
			delta = layoutParams.x + mGalleryWidth;
			break;

		default:
			break;
		}
		addView(view.getView());
		mScroller.startScroll(-layoutParams.x, 0, delta, 0, 300);
		YouPlayerAppFrame.youExplorer.invalidate();
		LOG.v(TAG, "snake()", "current hideType : " + currentHideType);
	}

	public void onComputeScroll() {
		if (mScroller.computeScrollOffset()) {
            layoutParams.x = -mScroller.getCurrX();
            requestLayout();
            YouPlayerAppFrame.youExplorer.postInvalidate();
            if(mScroller.isFinished()) {
            	if(targetType == -1) {
            		targetType = HIDE_BOTH;
            		mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mScroller.startScroll(-layoutParams.x, 0, layoutParams.x, 0, 300);
							YouPlayerAppFrame.youExplorer.invalidate();
						}
					}, 150);
				} else {
					moveEnd();
				}
            }
			
		}
		super.computeScroll();
	}

	public void hide(int hideType) {
		if (layoutParams == null) {
			layoutParams = (YouPlayerAbsoluteLayout.LayoutParams) getLayoutParams();
		}
		LOG.v(TAG, "hide()", "hideType : " + hideType + "  mIsTouched : "+ mIsTouched);
		if (!(mIsTouched && mIsDragging)) {
			moveStart(hideType);
	    	int delta = 0;
			switch (hideType) {
			case HIDE_RIGHT:
		    	delta = layoutParams.x - hideWidth;
				break;
			case HIDE_BOTH:
				delta = layoutParams.x;
				break;
			case HIDE_LEFT:
				delta = hideWidth + layoutParams.x;
				break;

			default:
				break;
			}
			mScroller.startScroll(-layoutParams.x, 0, delta, 0, 300);
			YouPlayerAppFrame.youExplorer.invalidate();
		}
	}

	public void setInterceptTouchEvent(boolean isIntercept) {
		LOG.v(TAG, "setInterceptTouchEvent()", " isIntercept : " + isIntercept);
		this.isIntercept = isIntercept;
	}

	private void setMoveable(boolean isMoveable) {
		this.isMoveable = isMoveable;
	}

	public void setOnHideListener(OnHideListener hideListener) {
		this.hideListener = hideListener;
	}

	public interface OnHideListener {
		void goHide(int hideType);
	}

	private class FlingGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
		private float moveXPosition;
		
		public void setDownEvent(float moveXPosition, float moveYPosition) {// 复原丢失的Down触摸消息
			if (layoutParams == null) {
				layoutParams = (YouPlayerAbsoluteLayout.LayoutParams) getLayoutParams();
			}
			this.moveXPosition = moveXPosition;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// 左右滑动唤出菜单开关
			if(canSlidingAround){
			if (e2.getAction() == MotionEvent.ACTION_MOVE) {
				if (layoutParams == null) {
					layoutParams = (YouPlayerAbsoluteLayout.LayoutParams) getLayoutParams();
					LOG.e(TAG, "onScroll()", "layoutParams = " + layoutParams.toString());
				}
				if (mIsDragging == false) {
					mIsTouched = true;
					mIsDragging = true;
					mScrollTimestamp = System.currentTimeMillis();
					mCurrentOffset = layoutParams.x;
				}

				float maxVelocity = mGalleryWidth
						/ (mAnimationDuration / 1000.0f);
				long timestampDelta = System.currentTimeMillis()
						- mScrollTimestamp;
				float maxScrollDelta = maxVelocity * (timestampDelta / 1000.0f);

				float currentScrollDelta = e2.getRawX() - moveXPosition;
				if (currentScrollDelta < maxScrollDelta * -1)
					currentScrollDelta = maxScrollDelta * -1;
				if (currentScrollDelta > maxScrollDelta)
					currentScrollDelta = maxScrollDelta;
				int scrollOffset = Math.round(mCurrentOffset
						+ currentScrollDelta);
				if (scrollOffset >= fling_width)
					scrollOffset = fling_width;
				if (scrollOffset <= fling_width * -1)
					scrollOffset = fling_width * -1;

				if (scrollOffset != 0) {
					if ((layoutParams.x == 0)
							|| ((scrollOffset * layoutParams.x) < 0)) {
						if (scrollOffset < 0) {
							hideListener.goHide(HIDE_LEFT);
							moveStart(HIDE_LEFT);
						} else if (scrollOffset > 0) {
							hideListener.goHide(HIDE_RIGHT);
							moveStart(HIDE_RIGHT);
						}
					}
				}
				if(Math.abs(layoutParams.x - scrollOffset) > 2) {
					layoutParams.x = scrollOffset;
					requestLayout();
				}
			}
			LOG.v(TAG, "onScroll()", " onScroll End");
		    }
			return false;
		}
	}

	private class InterruptGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			mIsTouched = true;
			if (currentHideType != HIDE_BOTH) {
				mGestureDetectorListener.setDownEvent(e.getRawX(), e.getRawY());
				return true;
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if(e2 == null)
				Log.v(TAG, "InterruptGestureDetectorListener  onScroll  e2 == null  ");
			if(e1 == null)
				Log.v(TAG, "InterruptGestureDetectorListener  onScroll  e1 == null  " + (e1 == null));
			if (e2.getAction() == MotionEvent.ACTION_MOVE) {
				float maxVelocity = mGalleryWidth
						/ (mAnimationDuration / 1000.0f);
				long timestampDelta = System.currentTimeMillis()
						- mScrollTimestamp;
				float maxScrollDelta = maxVelocity * (timestampDelta / 1000.0f);
				float currentScrollDelta = e2.getRawX() - e1.getRawX();
				float currentScrollYDelta = e2.getRawY() - e1.getRawY();
				if (currentScrollDelta < maxScrollDelta * -1)
					currentScrollDelta = maxScrollDelta * -1;
				if (currentScrollDelta > maxScrollDelta)
					currentScrollDelta = maxScrollDelta;
				if (Math.abs(currentScrollDelta) > mDetectScrollX || Math.abs(currentScrollYDelta) > mDetectScrollX) {
					isIntercept = true;
					float dy = e2.getRawY() - e1.getRawY();
					double tan = dy / currentScrollDelta;
					if (tan > -0.58 && tan < 0.58) {
						mGestureDetectorListener.setDownEvent(e2.getRawX(), e2.getRawY());
						return true;
					}
				}
			}
			return false;
		}
	}

	@Override
	public boolean action_callback(int page_id, int page_action,
			Object core_data, Object ui_data) {
		boolean result = true;
		switch (page_action) {
		case You_Core.FN_PAGE_EVT_PUSH:
			LOG.i(TAG, "handlerCallBack()", " pushPage " + page_id + " "+ core_data);
			pushPage(core_data, ui_data);
			return true;
		// 删除视图
		case You_Core.FN_PAGE_EVT_POP:
			LOG.i(TAG, "handlerCallBack()", " popPage " + page_id + " "+ core_data);
			popPage(core_data);
			return true;
		// 显示当前视图
		case You_Core.FN_PAGE_EVT_REMAIN:
			LOG.i(TAG, "handlerCallBack()", " remainPage " + page_id + " "+ core_data);
			remainPage(page_id, core_data, ui_data);
			return true;
		case You_Core.FN_PAGE_EVT_POP_TO_PAGE:
			LOG.i(TAG, "handlerCallBack()", " pop to page " + page_id + " "+ core_data);
			popToPage(core_data);
			return true;
		default:
			result = false;
			break;
		}
		return result;
	}

	private void pushPage(Object core_data, Object ui_data) {
		int page_type =((You_core_push_or_pop_page_data_t)core_data).page_type;
		LOG.v(TAG, "pushPage()", " page_type   : " + page_type + "");
		push(YouPlayerViewControlerFactory.createViewByPageType(getContext().getApplicationContext(), page_type,core_data, ui_data));
		setMoveable(true);
	}

	// popPage 与 show 存在异步执行的问题 固在pop时加同步关键字
	private synchronized void popPage(Object core_data){
		boolean isRoot = ((You_core_push_or_pop_page_data_t) core_data).isRoot;
		if(isRoot){
			popAll();
		}else{
			pop();
		}
	}

	private synchronized void popToPage(Object core_data){
		int page_type = ((You_core_push_or_pop_page_data_t) core_data).page_type;
		removeViewAt(getChildCount()-1);
		int size = stack.size();
		for (int i = 0; i < size; i++) {
			currentViewControler = null;
			currentViewControler = stack.pop();
			viewLevel--;
			if(page_type == currentViewControler.getTag()){
				getChildAt(getChildCount()-1).setVisibility(View.VISIBLE);
				return;
			}else{
				removeViewAt(getChildCount()-1);
			}
		}
	}

	private void remainPage(int page_id, Object core_data, Object ui_data) {
		hide(HIDE_BOTH);
	}
	

	private int targetType;
	private void moveStart(int hideType) {
		targetType = hideType;
		currentViewControler.onPause();
	}

	private void moveEnd() {
		switch (targetType) {
		case HIDE_BOTH:
			currentViewControler.onResume();
			break;

		case HIDE_RIGHT:
			break;

		case HIDE_LEFT:
			break;

		default:
			break;
		}
		currentHideType = targetType;
	}

}
