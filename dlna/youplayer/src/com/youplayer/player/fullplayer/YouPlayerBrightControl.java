package com.youplayer.player.fullplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.youplayer.player.YouExplorer;
import com.youplayer.player.R;
import com.youplayer.util.YouUtility;

public class YouPlayerBrightControl extends View {

	Bitmap back;
	Paint paint;
	Bitmap p_back,p_focus;
	
	public YouPlayerBrightControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		back = BitmapFactory.decodeResource(context.getResources(),R.drawable.youplayer_fullplayer_light_bg);
		p_back = BitmapFactory.decodeResource(context.getResources(),R.drawable.youplayer_fullplayer_adjustment_bg);
		p_focus = BitmapFactory.decodeResource(context.getResources(),R.drawable.youplayer_fullplayer_adjustment_active);
		paint = new Paint();
		paint.setStyle(Style.FILL);
//		AudioManager volumeManager = (AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
		max = 9;
		
		if( init_value == -1 ){
			init_value = YouUtility.BrightnessGetValue(YouExplorer.instance.getWindow());
			vol = init_value;
			//Log.i("lrl","bright value"+vol);
		}
	}
	int lines = 10;
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(back,0,0, paint);
		int l = (getWidth()-p_back.getWidth())/2;
		int t = (getHeight()-p_back.getHeight())/2;
		canvas.drawBitmap(p_back, l , t , paint);
		if( vol >1 ){//最小值不能小于1  
			int off = (int) (p_focus.getHeight()*(1f-vol*1f/max));
			Rect src = new Rect(0,off,p_focus.getWidth(),p_focus.getHeight());
			Rect dst = new Rect(l,t+off,l+p_focus.getWidth(),t+p_focus.getHeight());
			canvas.drawBitmap(p_focus,src, dst, paint);
		}
	}
	
	int vol = 6;
	public void setBright(Window window,int vol){
		if( vol<1 ) vol = 1;
		if( vol <= max)
		{
			this.vol = vol;
			invalidate();
			YouUtility.BrightnessSetValue(window,this.vol);
		}
	}
	static int init_value=-1;
	
	public void reset(){
		
		if( init_value > -1 && init_value != vol){
			if( init_value == 0 )
				YouUtility.BrightnessSetValue(YouExplorer.instance.getWindow(),1);
			else
				YouUtility.BrightnessSetValue(YouExplorer.instance.getWindow(),init_value);
		}
	}
	public void init(){
		setBright(YouExplorer.instance.getWindow(),vol);
	}
	public int getBright(){
		return vol;
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
	public void setBright(int p){
		vol = p;
	}
	public void adjust(Window window,float per) {
		int v = Math.round((vol + per*max));
		if( v< 0 ) v = 0;
		else if( v > max ) v = max;
		setBright(window,v);
	}
	float last_y;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if( event.getAction() == MotionEvent.ACTION_DOWN ){
			last_y = event.getY();
		}else if( event.getAction() == MotionEvent.ACTION_MOVE ){
			if( Math.abs(event.getY() - last_y )>5 ){
				if( event.getY() - last_y > 0 )
					setBright(YouExplorer.instance.getWindow(),vol-1);
				else
					setBright(YouExplorer.instance.getWindow(),vol+1);
			}
			last_y = event.getY();
		}
		return true;
		
	}
	public void setBrightInit() {
		init_value = YouUtility.BrightnessGetValue(YouExplorer.instance.getWindow());
	}
	
}
