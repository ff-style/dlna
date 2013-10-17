package com.youplayer.player;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.youplayer.util.LOG;
import com.youplayer.util.YouPlayerTouchPointersUtil;

public class YouGestureOverlayView extends GestureOverlayView {

	private static String tag = Gesture.class.getName();

    private float startX = Float.NaN;
    private float startY = Float.NaN;
    private float endX = Float.NaN;
    private float endY = Float.NaN;
    private static float MAX_WIDTH = 12.0f;
    private static final int alpha = 220;
    private Paint innerPaint = new Paint();
    private Paint outerPaint = new Paint();
    private List<PointXY> list = new ArrayList<PointXY>();
    
    private List<PointXY> list_inner_1 = new ArrayList<PointXY>();
    private List<PointXY> list_inner_2 = new ArrayList<PointXY>();
    
    private List<PointXY> list_outer_1 = new ArrayList<PointXY>();
    private List<PointXY> list_outer_2 = new ArrayList<PointXY>();
    
    private List<PointXY> list_inner = new ArrayList<PointXY>();    
    private List<PointXY> list_outer = new ArrayList<PointXY>();
    
    
    private YouPlayerTouchPointersUtil mTouchPointersUtil;
    class PointXY{
    	public PointXY(float x, float y){
    		this.x = x;
    		this.y = y;
    	}
    	float x;
    	float y;
    }
    
	public YouGestureOverlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
        TouchPointersInit();
		
	}

	public YouGestureOverlayView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public YouGestureOverlayView(Context context) {
		this(context, null);
	}
	
    
    private void initPaint() {
    	innerPaint.setColor(0x212121);
	   	innerPaint.setAntiAlias(true);
	   	innerPaint.setStrokeWidth(5);
	   	innerPaint.setAlpha(alpha);
	
	   	innerPaint.setMaskFilter(new EmbossMaskFilter(new float[]{0, 0, 1},0.7f, 6.0f, 3.5f));    	 
	   	innerPaint.setMaskFilter(new BlurMaskFilter(0.2f, BlurMaskFilter.Blur.NORMAL));
	   	innerPaint.setPathEffect(new CornerPathEffect(10));
	   	innerPaint.setStyle(Paint.Style.FILL);
	   	 
	   	 
	   	outerPaint.setColor(0xFFFAF0);
	   	outerPaint.setAntiAlias(true);
	   	outerPaint.setStrokeWidth(5);
	   	outerPaint.setAlpha(alpha);
	
		outerPaint.setMaskFilter(new EmbossMaskFilter(new float[]{0, 0, 1},0.7f, 6.0f, 3.5f));  
	   	outerPaint.setMaskFilter(new BlurMaskFilter(2f, BlurMaskFilter.Blur.NORMAL));
	   	outerPaint.setPathEffect(new CornerPathEffect(10));
	   	outerPaint.setStyle(Paint.Style.FILL);
	}

	public Paint getInnerPaint(){
         return innerPaint;
    }
    
    public Paint getOuterPaint(){
        return outerPaint;
    }

    public float getAngle(float x0, float y0, float x1, float y1){
    	return (float)Math.atan((y1 - y0) / (x1 - x0));
    }
    
    public void computerInnerCoors(){
	      int len = list.size() - 1;
	      int len_head = 1;
	      float h = 0;
	      
	      for(int i = 0; i < len; i++){
	    	float angle = getAngle(list.get(i).x, list.get(i).y,list.get(i + 1 ).x, list.get(i + 1).y);
	      	if(Float.isNaN(angle)){
	      		continue;
	      	}
	      	h = i * MAX_WIDTH/(len - 1); 
	      	
	    	float x2=0, y2 =0, x3 = 0, y3 = 0;
	    	
	    	if(i < len - len_head){
	      		if(list.get(i).x <= list.get(i+1).x){
	      			x2 = list.get(i).x + h * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y - h * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x - h * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y + h * (float)FloatMath.cos(angle);
	      		}else{
	      			x2 = list.get(i).x - h * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y + h * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x + h * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y - h * (float)FloatMath.cos(angle);
	      		}
	      		
	      		list_inner_1.add(new PointXY(x2, y2));
	      		list_inner_2.add(new PointXY(x3, y3));
	      	}else{
	      		
	      		if(list.get(i).x <= list.get(i+1).x){
	      			x2 = list.get(i).x + (h / len_head) * (len - i -1) * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y - (h / len_head) * (len - i -1) * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x - (h / len_head) * (len - i -1) * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y + (h / len_head) * (len - i -1) * (float)FloatMath.cos(angle);
	      		}else{
	      			x2 = list.get(i).x - (h / len_head) * (len - i -1) * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y + (h / len_head) * (len - i -1) * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x + (h / len_head) * (len - i -1) * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y - (h / len_head) * (len - i -1) * (float)FloatMath.cos(angle);
	      		}
	      		
	      		list_inner_1.add(new PointXY(x2, y2));
	      		list_inner_2.add(new PointXY(x3, y3));
	      	}
	      }
	      
	      int inner_1_len = list_inner_1.size();
	      for(int i = 0; i < inner_1_len ; i++){
	    	  list_inner.add(list_inner_1.get(i));
	      }
	       
	      int inner_2_len = list_inner_2.size();
	      for(int i = inner_2_len - 1; i >= 0 ; i--){
	    	  list_inner.add(list_inner_2.get(i));
	      }

	      list_inner_1.clear();
	      list_inner_2.clear();
    }

    public void computerOuterCoors(){
	      int len = list.size() - 1;
	      int len_head = 1;
	      float h = 0;
	      
	      for(int i = 0; i < len; i ++){
	    	float angle = getAngle(list.get(i).x, list.get(i).y,list.get(i + 1 ).x, list.get(i + 1).y);
	      	if(Float.isNaN(angle)){
	      		continue;
	      	}
	      	
	    	h = i * MAX_WIDTH/(len - 1); 
	    	
	    	float x2=0, y2 =0, x3 = 0, y3 = 0;
	    	
	    	if(i < len - len_head){
	      		if(list.get(i).x <= list.get(i+1).x){
	      			x2 = list.get(i).x + (h+2) * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y - (h+2) * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x - (h+2) * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y + (h+2) * (float)FloatMath.cos(angle);
	      		}else{
	      			x2 = list.get(i).x - (h+2) * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y + (h+2) * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x + (h+2) * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y - (h+2) * (float)FloatMath.cos(angle);
	      		}
	      		
	      		list_outer_1.add(new PointXY(x2, y2));
	      		list_outer_2.add(new PointXY(x3, y3));
	      	}else{
	      		
	      		if(list.get(i).x <= list.get(i+1).x){
	      			x2 = list.get(i).x + ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y - ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x - ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y + ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.cos(angle);
	      		}else{
	      			x2 = list.get(i).x - ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.sin(angle);
	      			y2 = list.get(i).y + ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.cos(angle);
	      			
	      			x3 = list.get(i).x + ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.sin(angle);
	      			y3 = list.get(i).y - ((h / len_head) * (len - i -1) + 2) * (float)FloatMath.cos(angle);
	      		}
	      		
	      		list_outer_1.add(new PointXY(x2, y2));
	      		list_outer_2.add(new PointXY(x3, y3));
	      	}
	      }
	      
	      int outer_1_len = list_outer_1.size();
	      for(int i = 0; i < outer_1_len ; i++){
	    	  list_outer.add(list_outer_1.get(i));
	      }
	      
	      int outer_2_len = list_outer_2.size();
	      for(int i = outer_2_len - 1; i >= 0 ; i--){
	    	  list_outer.add(list_outer_2.get(i));
	      }

	      list_outer_1.clear();
	      list_outer_2.clear();
    }


    @Override
	public void draw(Canvas canvas) {
//        if(FullScreenPlayer.instance != null && FullScreenPlayer.instance.getGestureIsEnable()){
    		if (list.size() >= 1) {
    			computerInnerCoors();
    			computerOuterCoors();
    			canvas.drawPath(drawShape(list_outer), getOuterPaint());
    			canvas.drawPath(drawShape(list_inner), getInnerPaint());
    
    			list_inner.clear();
    			list_outer.clear();
    		}
//        }
    }
    
    public static Path drawShape(List<PointXY> list){
       
    	Path backPath = new Path();
        for(int i = 0; i < list.size(); i++){
        	if(i == 0){
        		backPath.moveTo(list.get(0).x, list.get(0).y);
        	}
        	backPath.lineTo(list.get(i).x, list.get(i).y);
        }
        
        backPath.close();
        return backPath;
    }
    
    public void setGestureEvent(MotionEvent event){
        int action = event.getAction();
        switch(action){
        case MotionEvent.ACTION_DOWN:
//            v(tag, "ACTION_DOWN");
            startX = event.getX();
            startY = event.getY();
            list.add(new PointXY(startX, startY));
            break;
            
        case MotionEvent.ACTION_UP:
//            v(tag, "ACTION_UP");
            this.invalidate();
            list.clear();

            break;
            
        case MotionEvent.ACTION_MOVE:
//            v(tag, "ACTION_MOVE");
            endX = event.getX();
            endY = event.getY();
            if (list.size() > 10) {
                list.remove(0);
            }
            list.add(new PointXY(endX, endY));
            this.invalidate();
            break;
            
        default:
            break;
        }
    }
	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		if (event.getPointerCount() == 1) {
		    setGestureEvent(event);
		    
		}else if(event.getPointerCount() == 2){
		    setTouchPointersEvent(event);
		}
		return true;
	}
	
	
	public void TouchPointersInit(){
	    mTouchPointersUtil = new YouPlayerTouchPointersUtil();
	}
	
	public void TouchPointersRelease(){
	    mTouchPointersUtil = null;
	}
	
	public void setTouchPointersEvent(MotionEvent event){
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setMoveEvent(event);
        }
    
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setZoomEvent(event);
        }
    }
	
	public void setMoveCallback(YouPlayerTouchPointersUtil.TouchPointersMoveCallback callback){
	    if(mTouchPointersUtil != null){
	        mTouchPointersUtil.setUtilMoveCallback(callback);
	    }
	}
	
    public void setZoomCallback(YouPlayerTouchPointersUtil.TouchPointersZoomCallback callback){
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setUtilZoomCallback(callback);
        }
    }
    
	public static void v(String type, String msg){
	    String des = String.format("[%10.10s][%16.16s]%s", tag, type, msg);
	    LOG.v(tag, "FG", des);
	}
	
	public static void e(String type, String msg){
        String des = String.format("[%10.10s][%16.16s]%s", tag, type, msg);
        LOG.e(tag, "FG", des);
    }
}
