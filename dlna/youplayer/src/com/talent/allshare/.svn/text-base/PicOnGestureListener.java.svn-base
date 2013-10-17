package com.talent.allshare;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class PicOnGestureListener extends SimpleOnGestureListener{

    private Context mContext; 
    private PicturePlayerActivity picActivity;

    PicOnGestureListener(Context context ,Activity activity) { 
        mContext = context; 
        picActivity = (PicturePlayerActivity) activity;
    } 
    @Override
    public boolean onDown(MotionEvent e) {
    	Log.e("gzf","ondown "+ e.getX()+"||||"+e.getY());
    	return super.onDown(e);
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
    		float distanceY) {
    	Log.e("gzf","onScroll e1 "+ e1.getX()+"||||"+e1.getY());
    	Log.e("gzf","onScroll e2 "+ e2.getX()+"||||"+e2.getY());
    	
    	return super.onScroll(e1, e2, distanceX, distanceY);
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
    		float velocityY) {
    	Log.e("gzf","onFling e1"+ e1.getX()+"||||"+e1.getY());
    	Log.e("gzf","onFling e2 "+ e2.getX()+"||||"+e2.getY());
    	
    	if(e2.getX()-e1.getX()>100){
    		
    		picActivity.pre();
    	}else{
    		picActivity.next();
    	}
    	return super.onFling(e1, e2, velocityX, velocityY);
    }
	
}
