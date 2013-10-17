package com.youplayer.player.fullplayer;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youplayer.player.R;

public class YouPlayerSlideView extends RelativeLayout {

	private Context mContext;

	private Bitmap mSlideBitmap;
	private Paint mPaint = new Paint();
	private Rect mSlideViewPos = new Rect();
	private Rect src = new Rect();
	private Rect dst = new Rect();
	private boolean isSliding = false;
	private int lastX = 0;
	private int mSlideBitmapWidth = 59;
	private int mSlideBitmapHeight = 59;
//    private int mSlideBgWidth = 271;
    private int mSlideBgHeight = 59;
    private onSlideFinishedListener mOnSlideFinishedListener;
	
	public YouPlayerSlideView(Context context) {
		this(context, null);
	}

	public YouPlayerSlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	public void initView(){
		try {
			setBackgroundResource(R.drawable.youplayer_fullscreen_icon_unlock_bg);
			initBitmap();
			
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getText(){
	    String str = mContext.getString(R.string.full_player_unlock_message);
	    return (str != null ? str : "");
	}
	
	public void initBitmap(){
        try {
            mSlideBitmap = ((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.youplayer_fullscreen_icon_unlock_slide)).getBitmap();
            if(mSlideBitmap != null){
                mSlideBitmapWidth = mSlideBitmap.getWidth();
                mSlideBitmapHeight = mSlideBitmap.getHeight();
            }
            
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bgBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.youplayer_fullscreen_icon_unlock_bg, options);
            if(bgBitmap != null){
//                mSlideBgWidth = bgBitmap.getWidth();
                mSlideBgHeight = bgBitmap.getHeight();
            }
            
            if(bgBitmap != null && !bgBitmap.isRecycled()){
                bgBitmap.recycle();
                bgBitmap = null;
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mSlideViewPos.set(l, t, r, b);
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		drawSlideBtn(canvas);
	}

	public void drawSlideBtn(Canvas canvas){
		try {
			src.left = 0;
			src.top = 0;
			src.right = mSlideBitmapWidth;
			src.bottom = mSlideBitmapHeight;
			
			dst.left = lastX;
			dst.top = (mSlideBgHeight - mSlideBitmapHeight) / 2;
			dst.right = lastX  + mSlideBitmapWidth;
			dst.bottom = ((mSlideBgHeight - mSlideBitmapHeight) / 2) + mSlideBitmapHeight;
			canvas.drawBitmap(mSlideBitmap, src, dst, mPaint);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(isSlideStart(event)){
				isSliding = true;
				isSlideStart();
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(isSliding){
				int tmp  = (int)event.getX();
				if(tmp > mSlideViewPos.right - mSlideViewPos.left  - mSlideBitmapWidth){
					tmp = mSlideViewPos.right - mSlideViewPos.left - mSlideBitmapWidth + mSlideBitmapWidth / 5;
				}
				if(tmp < 0){
					tmp = 0;
				}
				lastX = tmp;
				invalidate();
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if(isSlideFinished(event)){
				isSlideEnd(true);
			}else{
			    isSlideEnd(false);    
			}
			
			isSliding = false;
			lastX = 0;
			invalidate();
			break;
			
		default:
			break;
		}
		return true;
	}
	
	public void isSlideEnd(boolean isFinished){
	    if(mOnSlideFinishedListener != null){
            mOnSlideFinishedListener.OnSlideEnd(isFinished);
        }
	}
	
	public void isSlideStart(){
	    if(mOnSlideFinishedListener != null){
            mOnSlideFinishedListener.OnSlideStart();
        }
	}
	
	public boolean isSlideStart(MotionEvent event){
		if((event.getRawX() >= mSlideViewPos.left) && (event.getRawX() <= (mSlideViewPos.left + mSlideBitmapWidth))){
			return true;
		}
		return false;
	}
	
	
	public boolean isSlideFinished(MotionEvent event){
		if((event.getRawX() >= (mSlideViewPos.left + (mSlideViewPos.right - mSlideViewPos.left) / 2))&& isSliding){
			return true;
		}
		return false;
	}
	
	public void setOnSlideListener(onSlideFinishedListener l){
		mOnSlideFinishedListener = l;
	}
	
	public interface onSlideFinishedListener{
		public void OnSlideStart();
		public void OnSlideEnd(boolean isFinished);
	}
}
