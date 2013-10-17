/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talent.allshare.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

import com.youplayer.player.R;


public class IOSSwitch extends CompoundButton{
    private static final boolean DBG = false;
    private static final String TAG = "IOSSwitch";

    private static final int MSG_ANIMATE = 1000;
    private static final int MSG_ANIMATE_REVEAL = 1001;
    private static final int MSG_ANIMATE_END = 1002;

    private int mSwitcherStyle = 0;
    private BitmapDrawable mDrawable;
    private Bitmap mBitmap;
    private float mOffset;
    //54(Handle left width) + 40(handle width) + 54(handle right width) = 148(bitmap width) * 27(bitmap height)
    private int mDisplayWidth = 0; //116
    private int mExtra = 0;
    private int mWidth;
    private int mHeight;
    
    private boolean mOn = false;
    
    private Paint mPaint = new Paint();

    private H mHandler = new H();
    
    private OnSwitchChangeListener mOnSwitchChangeListener;

    static final int ANIM_FRAME_DURATION = (1000/60);
    
    static final int CLICK_FAULT_TOLERANCE = 2;

	private int mTouchSlop;
	private boolean mDragging = false;
	private boolean mDraggStatue = false;

    boolean mAnimating = false;
    long mCurAnimationTime;
    long mAnimLastTime;
    float mAnimVel = 200.0f;
    float mAnimAccel = 1000.0f;

	private float[] mTouchDown = {0.0f,0.0f};
	private float[] mDragTrack = {0.0f,0.0f};
	
    public IOSSwitch(Context context) {
        this(context, null);
    }

    public IOSSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IOSSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

		ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
		
        init();        
    }

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
				
		float[] touchPonit={0.0f, 0.0f};
		float distanceX = 0.0f;
		float distanceY = 0.0f;
		final int action = ev.getActionMasked();
		Log.d(TAG, "onTouchEvent():: action = "+action +" mAnimating="+mAnimating +" isEnabled="+IOSSwitch.this.isEnabled());
		if(mAnimating){
			return true;
    	}
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mDragging = false;
			mDraggStatue = false;
			mTouchDown[0] = ev.getX();
			mTouchDown[1] = ev.getY();

			return true;
		}

		case MotionEvent.ACTION_MOVE: {
			
			touchPonit[0] = ev.getX();
			touchPonit[1] = ev.getY();
			if(mDragging){
				distanceX = touchPonit[0]-mDragTrack[0];
				distanceY = touchPonit[1]-mDragTrack[1];
			}else{
				distanceX = touchPonit[0]-mTouchDown[0];
				distanceY = touchPonit[1]-mTouchDown[1];
				if(Math.abs(distanceX)>mTouchSlop){
					mDraggStatue = true;
				}
				mDragTrack[0] = ev.getX();
				mDragTrack[1] = ev.getY();
			}
			if(Math.abs(distanceX)>mTouchSlop){
				mDragging = true;
				distanceX = touchPonit[0]-mTouchDown[0];
			}
			if(mDragging && !this.mOn && 0<distanceX){
				mOffset = mExtra - distanceX + 0.5f; 
			}else if(mDragging && this.mOn && 0>distanceX){
				mOffset = 0.5f - distanceX;
			}
			mDragging = false;
			invalidate();
			return true;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			
			touchPonit[0] = ev.getX();
			touchPonit[1] = ev.getY();
			distanceX = touchPonit[0]-mTouchDown[0];
			distanceY = touchPonit[1]-mTouchDown[1];
			if(!mDraggStatue){
				performFling(!this.mOn);
			}else{
				if(mOffset >= mExtra/2 && mOffset<mExtra){
					performFling(false);
				}else if(mOffset<mExtra/2 && mOffset>0){
					performFling(true);
				}else{
					mOn = (mOffset == 0);
	                if (mOnSwitchChangeListener != null) {
	                	mOnSwitchChangeListener.onSwitchChanged(IOSSwitch.this, mOn);
	                }                
	                IOSSwitch.this.setChecked(mOn);
				}				
			}
	
			return true;
			}
		}

		return false;
	}
	 
    
    public void setOn(boolean on) {
        if (on) {
            mOffset = 0;
            Log.d(TAG, "setOn::mOffset = 0");
        } else {
            mOffset = mExtra;
        }
        invalidate();
        mOn = on;
    }
   
	public void setSwitchStyle(int iStyle){
    	mSwitcherStyle = iStyle;
    	setDrawable();
    }
    
    private void setDrawable(){
    	switch(mSwitcherStyle){
        case 1:
        	mDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.switch_handler_normal);
        	break;
        case 2:
        	mDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.switch_handler_warning);
        	break;
        default:
        	mDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.switch_handler_normal);
        	break;
        }
		mBitmap = mDrawable.getBitmap();
        mDrawable = null;
    }
    
    private void init() {
        setDrawable();
        mWidth = mBitmap.getWidth();
        mHeight = mBitmap.getHeight();
        
        mDisplayWidth= (mWidth+mHeight)/2; //(SWITCHER_DISPLAYWIDTH*mWidth)/SWITCHER_WIDTH ;
        
        mExtra = mWidth - mDisplayWidth;  //148 - 94 = 54
        
        mOffset = mExtra;
      
        
        Log.d(TAG, "init::mOffset = " +mOffset);
    }
    
    
    @Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		super.setChecked(checked);
		if(IOSSwitch.this.isEnabled() && !mAnimating){
			setOn(isChecked());
		}
		
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (DBG) Log.d(TAG, "onMeasure mode:"+widthSpecMode+" w:"+width+" h:"+height);

        setMeasuredDimension(mDisplayWidth, mHeight);
    }

    //two conditions: one is touch switch ok, the other is animation switch ok
    boolean isSwitchOk(){
    	if(this.mAnimating){
    		if((mOffset == mExtra)||(mOffset == 0)){
    			return true;
    		}
    		/*
	    	if (mOn && (mOffset == mExtra)) {
	            return true;
	        }
	    	if ((!mOn) && (mOffset == 0)) {
	            return true;
	        }*/
    	}
		return false;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	Log.d(TAG, "init::mOffset = " +mOffset);
        if (mOffset < 0){
        	mOffset = 0;
        }else if (mOffset > mExtra){
        	mOffset = mExtra;
        }else if(!mAnimating && !mDraggStatue){
        	mOffset = mOffset>=mExtra?mExtra:0;
        }
        
        Rect srcRect = new Rect((int)mOffset, 0, (int)mOffset+mDisplayWidth, mHeight);
        Rect dstRect = new Rect(0, 0, mDisplayWidth, mHeight);
        //Log.e("AppleSwitcher", "bitmap drawn");
       // canvas.drawBitmap(mBitmap, srcRect, dstRect, mPaint);


	//get rounder bitmap start
	Bitmap Roundedoutput = Bitmap.createBitmap(mDisplayWidth,mHeight, Config.ARGB_8888);
	Canvas canvas1 = new Canvas(Roundedoutput);
       int color = 0xff424242;
       Paint paint = new Paint();
       RectF rectF = new RectF(dstRect);
       float roundPx = mHeight/2;//(ROUND_RADIUS*mWidth)/SWITCHER_WIDTH;  //21
       paint.setAntiAlias(true);
       canvas1.drawARGB(0, 0, 0, 0);
       paint.setColor(color);
       canvas1.drawRoundRect(rectF, roundPx, roundPx, paint);
       paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
       canvas1.drawBitmap(mBitmap, srcRect, dstRect, paint);
	//get rounder bitmap end
       canvas.drawBitmap(Roundedoutput, dstRect, dstRect, mPaint);

        
        if (isSwitchOk()) {
            Log.d(TAG, "onDraw() mOn=" +mOn +" mOffset=" +mOffset);
            mHandler.sendEmptyMessage(MSG_ANIMATE_END);          
        }

    }


    private void performFling(boolean on) {
    	Log.d(TAG, "mAnimating = " +mAnimating);
    	if( true == mAnimating){
    		//Log.e(TAG, "mAnimating is true, not performfling");
    		return;
    	}
        long now = SystemClock.uptimeMillis();
        mCurAnimationTime = now + ANIM_FRAME_DURATION;
        mAnimLastTime = now;
        mAnimating = true;
        mAnimVel = 200.0f;

        mHandler.removeMessages(MSG_ANIMATE);
        mHandler.removeMessages(MSG_ANIMATE_REVEAL);
        Log.d(TAG, "sendMessageAtTime on = " +on);
        mHandler.sendMessageAtTime(mHandler.obtainMessage(on ? MSG_ANIMATE : MSG_ANIMATE_REVEAL), mCurAnimationTime);
    }

    private void decrementAnim() {
        long now = SystemClock.uptimeMillis();
        float t = ((float)(now - mAnimLastTime)) / 1000;            // ms -> s
        final float x = mOffset;
        final float v = mAnimVel;                                   // px/s
        final float a = mAnimAccel;                                 // px/s/s
        mOffset = x - (v*t) - (0.5f*a*t*t);                          // px
        mAnimVel = v + (a*t);                                       // px/s
        mAnimLastTime = now;                                        // ms
    }

    private void incrementAnim() {
        long now = SystemClock.uptimeMillis();
        float t = ((float)(now - mAnimLastTime)) / 1000;            // ms -> s
        final float x = mOffset;
        final float v = mAnimVel;                                   // px/s
        final float a = mAnimAccel;                                 // px/s/s
        mOffset = x + (v*t) + (0.5f*a*t*t);                          // px
        mAnimVel = v + (a*t);                                       // px/s
        mAnimLastTime = now;                                        // ms
    }

    private void doAnimation(boolean on) {
        if (on) incrementAnim();
        else decrementAnim();

        if (mAnimating) {
            if (DBG) Log.d(TAG, "doAnimation mOffset:"+mOffset+" mExtra:"+mExtra);

            if((mOffset < mExtra) && (mOffset > 0)) {
                mCurAnimationTime += ANIM_FRAME_DURATION;
                mHandler.sendMessageAtTime(mHandler.obtainMessage(on ? MSG_ANIMATE_REVEAL : MSG_ANIMATE), mCurAnimationTime);
            }

            invalidate();
        }
    }

    private class H extends Handler {
        public void handleMessage(Message m) {
            if (m.what == MSG_ANIMATE_REVEAL) {
                doAnimation(true);
                return;
            }
            if (m.what == MSG_ANIMATE) {
                doAnimation(false);
                return;
            }
            if(m.what == MSG_ANIMATE_END){    
            	mAnimating = false;
                mOn = (mOffset == 0);
                if (mOnSwitchChangeListener != null) {
                	mOnSwitchChangeListener.onSwitchChanged(IOSSwitch.this, mOn);
                }                
                IOSSwitch.this.setChecked(mOn);
            	return;
            }
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnSwitchChangeListener(OnSwitchChangeListener listener) {
        mOnSwitchChangeListener = listener;
        
    }
    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnSwitchChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param switcher The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onSwitchChanged(IOSSwitch switcher, boolean isChecked);
    }
}

