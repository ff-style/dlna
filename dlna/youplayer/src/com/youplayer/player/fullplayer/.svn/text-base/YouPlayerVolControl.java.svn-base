package com.youplayer.player.fullplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.youplayer.player.R;
import com.youplayer.util.YouUtility;

public class YouPlayerVolControl extends View {

	Bitmap back;
	Paint paint;
	Bitmap p_back,p_focus;
	AudioManager volumeManager;
	public YouPlayerVolControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		back = BitmapFactory.decodeResource(context.getResources(),R.drawable.youplayer_fullplayer_sound_bg);
		p_back = BitmapFactory.decodeResource(context.getResources(),R.drawable.youplayer_fullplayer_adjustment_bg);
		p_focus = BitmapFactory.decodeResource(context.getResources(),R.drawable.youplayer_fullplayer_adjustment_active);
		
		paint = new Paint();
		paint.setStyle(Style.FILL);
		volumeManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		max = volumeManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		vol = volumeManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		vol = volumeManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if( vol > max ) vol = max;
		canvas.drawBitmap(back,0,0, paint);
		int l = (getWidth()-p_back.getWidth())/2;
		int t = (getHeight()-p_back.getHeight())/2;
		canvas.drawBitmap(p_back, l , t , paint);
		if( !mute && vol > 0 ){
			int off = (int) (p_focus.getHeight()*(1f-vol*1f/max));
			if( off == p_focus.getHeight() ) return;
			Rect src = new Rect(0,off,p_focus.getWidth(),p_focus.getHeight());
			Rect dst = new Rect(l,t+off,l+p_focus.getWidth(),t+p_focus.getHeight());
			canvas.drawBitmap(p_focus,src, dst, paint);
		}
	}
	
	int vol = 6;
	public void setVol(int v){
		vol = v;
		if( vol < max/(lines+1) ) 
			vol = 0; 
		
		if(vol == 0 ){
			setMute();
			invalidate();
		}else{
			if( mute ){
				mute = false;
				YouUtility.setMuteState(getContext(),mute);
			}
			setDevVol(vol);
			invalidate();
			YouUtility.setCurrentVolume(getContext(), vol);
		}
	}
	public int getVol(){
		return vol;
	}
	boolean mute = false;
	public void setMute(){
		setDevVol(0);
		mute = true;
		YouUtility.setMuteState(getContext(),mute);
		invalidate();
	}
	public boolean isMute(){
		return YouUtility.getMuteState(getContext())
				&& ((AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC)==0;
	}
	public void reset(){
		if( vol < max/(lines+1) )
			vol = max/2;
		mute = false;
		YouUtility.setMuteState(getContext(), mute );
		setDevVol(vol);
		invalidate();
	}
	public void adjust(float per){
		int v = Math.round((vol + per*max));
		if( v< 0 ) v = 0;
		else if( v > max ) v = max;
		setVol(v);
	}
	private void setDevVol(int vol){
		AudioManager volumeManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
		volumeManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol,isViewSystemSeek);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(back.getWidth(),back.getHeight());
	}
	@Override
	protected void onDetachedFromWindow() {
//		back.recycle();
//		back = null;
//		p_back.recycle();
//		p_back = null;
//		p_focus.recycle();
//		p_focus = null;
		super.onDetachedFromWindow();
	}
	int max=10;

	int lines=10;//精度个数
	int isViewSystemSeek = 0;
	public void up(){
		isViewSystemSeek = 1;
		//setVol(vol+max/lines);
		setVol(vol+1);
		isViewSystemSeek = 0;
	}
	public void down(){
		isViewSystemSeek = 1;
		//setVol(vol-max/lines);
		setVol(vol-1);
		isViewSystemSeek = 0;
	}
	
	float last_y;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if( event.getAction() == MotionEvent.ACTION_DOWN ){
			last_y = event.getY();
		}else if( event.getAction() == MotionEvent.ACTION_MOVE ){
			if( Math.abs(event.getY() - last_y )>5 ){
				if( event.getY() - last_y > 0 )
					down();
				else
					up();
			}
			last_y = event.getY();
		}
		return true;
		
	}

}
