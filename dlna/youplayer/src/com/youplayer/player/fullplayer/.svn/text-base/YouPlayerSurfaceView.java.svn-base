package com.youplayer.player.fullplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.youplayer.player.R;
import com.youplayer.util.LOG;

public class YouPlayerSurfaceView extends SurfaceView implements Callback {

	boolean isSystemPlayer = true;
	boolean surfaceCreateIsReady;
	String tag = "PlayerSurfaceView";
	OnCreateExecuted mExecuted;
    public YouPlayerSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		if( getId() == R.id.fullplayer_surface_view_system ){
			isSystemPlayer = true;
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		else{
			isSystemPlayer = false;
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		}	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LOG.i(tag,"surfaceCreated");
		surfaceCreateIsReady = true;
		if(mExecuted != null  ){
		    mExecuted.onCreated(this);
		    mExecuted = null;
		}  
		
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LOG.i(tag,"surfaceDestroyed");
		surfaceCreateIsReady = false;
	}
	
	public void setOnCreateExecute( OnCreateExecuted executed){
	    if( surfaceCreateIsReady )
	        executed.onCreated(this);
	    else
	        mExecuted = executed;
	}
	
	public static interface OnCreateExecuted{
	    public void onCreated(SurfaceView sv);
	}
}
