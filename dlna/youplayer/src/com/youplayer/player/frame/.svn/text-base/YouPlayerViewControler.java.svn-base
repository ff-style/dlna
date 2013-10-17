package com.youplayer.player.frame;

import com.youplayer.util.LOG;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public abstract class YouPlayerViewControler implements YouPlayerActionHandler, YouPlayerViewControlerLife, OnTouchListener {

	protected Context mContext;
	protected View mView;
	protected int tag;
	protected Handler mHandler;

	
	public YouPlayerViewControler(Context context, Object core_data, Object ui_data) {
		mContext = context;
	}

	public View getView() {
		return mView;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public void onStart() {

	}

	public void onRestart() {
	}

	public void onResume() {

	}

	public void onPause() {

	}

	public void onStop() {

	}

	public void onDestroy() {
		if(mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
		mView = null;
		mContext = null;
	}

	public void finish() {
		onPause();
		onStop();
		onDestroy();
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mView != null) {
			return mView.dispatchTouchEvent(ev);
		}
		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		LOG.v("ViewControl", "onTouch", "onTouch>>>>>>>>>");
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (mView != null) {
			return mView.onTouchEvent(event);
		}
		return false;
	}

	protected Dialog onCreateDialog(int id) {
		return null;
	}

	@Override
	public boolean action_callback(int page_id, int page_action,
			Object core_data, Object ui_data) {
		return true;
	}

	public boolean onkeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
