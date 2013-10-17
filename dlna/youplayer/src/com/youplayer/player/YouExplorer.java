package com.youplayer.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.youplayer.core.You_Core;

import com.youplayer.core.struct.You_data_original_web_t;
import com.youplayer.core.struct.You_full_screen_player_item;

import com.youplayer.player.frame.YouPlayerAppFrame;
import com.youplayer.player.frame.YouPlayerContainerView;
import com.youplayer.player.frame.YouPlayerEventControler;

import com.youplayer.util.YouUtility;
import com.youplayer.util.LOG;


public class YouExplorer extends Activity {

	private static final String TAG = "YouExplorer";

	public static YouExplorer instance;

	public static YouPlayerAppFrame appFrame;
	public static boolean notificationFlag = false;

    public boolean appFirstRunFlag = false;
	private boolean hasOnResume = false;
	
	public Intent intent = null;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.youplayer_root_layout);
		hasOnResume = false;
		
		android.os.Process.setThreadPriority(android.os.Process.myTid(), android.os.Process.THREAD_PRIORITY_AUDIO);
		if (appFrame != null) {
			LOG.v(TAG, "appFrame 1 : ", appFrame.toString());
		}
		appFrame = new YouPlayerAppFrame(this);
		LOG.v(TAG, "appFrame 2 : ", appFrame.toString());
		instance = this;
		if (!YouApplication.judgeByCpu(this)) {
			return;
		}
		
		intent = getIntent();

		handlerIntent(intent, true);

		if (android.os.Build.VERSION.SDK_INT >= 14) {
			try {
				int flag = (Integer) getStaticProperty(
						"android.view.WindowManager$LayoutParams",
						"FLAG_HARDWARE_ACCELERATED");
				getWindow().setFlags(flag, flag);
				// Log.v("MainActivity", "flag  " + flag);
			} catch (Exception e) {
				// Log.v("MainActivity", "flag  " + " Exception");
				e.printStackTrace();
			}
		}
	}

	
	private Object getStaticProperty(String className, String fieldName)
			throws Exception {
		Class<?> ownerClass = Class.forName(className);
		Field field = ownerClass.getField(fieldName);
		Object property = field.get(ownerClass);
		return property;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!YouApplication.cpu_is_ok) {
			return;
		}
		LOG.v(TAG, "onStart()", " onStart-----> ");
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			appFrame.getContainer().getCurrentViewControler().onStart();
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.getFullPlayerControler().onStart();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (!YouApplication.cpu_is_ok) {
			return;
		}
		LOG.v(TAG, "onRestart()", " onRestart-----> ");
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			appFrame.getContainer().getCurrentViewControler().onRestart();
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.getFullPlayerControler().onRestart();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!YouApplication.cpu_is_ok) {
			return;
		}
		LOG.v(TAG, "onResume()", " onResume-----> ");
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			appFrame.getContainer().getCurrentViewControler().onResume();
	
			hasOnResume = true;
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.isPlayBackground = false;
			appFrame.getFullPlayerControler().onResume();
			((YouPlayerFullScreenPlayer) appFrame.getFullPlayerControler()).playerGoForeground();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!YouApplication.cpu_is_ok) {
			return;
		}
		LOG.v(TAG, "onPause()", " onPause-----> ");
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			appFrame.getContainer().getCurrentViewControler().onPause();
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.isPlayBackground = true;
			appFrame.getFullPlayerControler().onPause();
			((YouPlayerFullScreenPlayer) appFrame.getFullPlayerControler()).playerGoBackground();
			break;
		default:
			break;
		}
		appFrame.cancelDialog();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!YouApplication.cpu_is_ok) {
			return;
		}
		LOG.v(TAG, "onStop()", " onStop-----> ");
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			appFrame.getContainer().getCurrentViewControler().onStop();
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.getFullPlayerControler().onStop();
			break;
		default:
			break;
		}
		
		for(Runnable runable:execs)
			new Thread(runable).start();
		execs.removeAllElements();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LOG.v(TAG, "onDestroy()", " onDestroy-----> ");
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			appFrame.getContainer().getCurrentViewControler().onDestroy();
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.getFullPlayerControler().onDestroy();
			break;

		default:
			break;
		}

	}

	@Override
	public void finish() {
		super.finish();
		switch (appFrame.getCurrentState()) {
		case YouPlayerAppFrame.STATE_EXPLORER:
			appFrame.getContainer().getCurrentViewControler().finish();
			break;

		case YouPlayerAppFrame.STATE_FULLPLAYER:
			appFrame.getFullPlayerControler().finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		LOG.v(TAG, "onNewIntent", "------");
		if (!hasOnResume) {
			LOG.v(TAG, "onNewIntent", " postDelayed-----> ");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					handlerIntent(intent, false);
					LOG.v(TAG, "onNewIntent", " postDelayed<----- ");
				}
			}, 50);
		} else {
			handlerIntent(intent, false);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// 检测屏幕的方向：纵向或横向
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 当前为横屏， 在此处添加额外的处理代码
			LOG.e(TAG, "onConfigurationChanged", "ORIENTATION_LANDSCAPE");
			appFrame.addFullScreenPlayer();
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 当前为竖屏， 在此处添加额外的处理代码
			LOG.e(TAG, "onConfigurationChanged", "ORIENTATION_PORTRAIT");
			appFrame.addYouExplorer();
		}
	}

	private boolean isTouchSelf = false;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			isTouchSelf = true;
		}
		if (!isTouchSelf)
			return false;
		boolean result = super.dispatchTouchEvent(ev);
		if (action == MotionEvent.ACTION_UP) {
			isTouchSelf = false;
		}
		return result;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			LOG.v(TAG, "onKeyDown()", " KeyEvent.KEYCODE_BACK ");
			if (appFrame.getCurrentState() == YouPlayerAppFrame.STATE_EXPLORER) {
				if (appFrame.getContainer().currentHideType == YouPlayerContainerView.HIDE_LEFT) {
					appFrame.getContainer().hide(YouPlayerContainerView.HIDE_BOTH);
				} else if (appFrame.getContainer().currentHideType == YouPlayerContainerView.HIDE_BOTH) {
					boolean flag = appFrame.getContainer().getCurrentViewControler().onkeyDown(keyCode, event);
					LOG.v(TAG, "flag : ", flag + "");
					if (!flag) {
						if (appFrame.getContainer().viewLevel == 1) {
							YouPlayerEventControler.fn_core_service_request(
									You_Core.FN_COMMON_BTN_MAIN_MENU,
									You_Core.FN_UI_EVT_TOUCH_UP, null, null);
						} else {
							YouPlayerEventControler.fn_core_service_request(
									You_Core.FN_COMMON_BTN_BACK,
									You_Core.FN_UI_EVT_TOUCH_UP, null, null);
						}
					}
				} else {
					YouPlayerEventControler.fn_core_service_request(
							You_Core.FN_COMMON_BTN_BACK,
							You_Core.FN_UI_EVT_TOUCH_UP, null, null);
				}
			} else if (appFrame.getCurrentState() == YouPlayerAppFrame.STATE_FULLPLAYER) {
				appFrame.getFullPlayerControler().onkeyDown(keyCode, event);
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (appFrame.getCurrentState() == YouPlayerAppFrame.STATE_FULLPLAYER) {
				appFrame.getFullPlayerControler().onkeyDown(keyCode, event);
			} else if ((appFrame.getCurrentState() == YouPlayerAppFrame.STATE_EXPLORER)) {
				appFrame.getContainer().getCurrentViewControler().onkeyDown(keyCode, event);
			}
			break;
		default:
			break;
		}
		return true;
	}

	public static String getSdcardPath() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if(path != null && path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		return path;
	}
	public  String FormatPath(String path) {
		LOG.v("FormatPath","", path);
		if(null == path){
			return null;
		}
		else{
			path = path.trim();
		}
		String temp = path;
		
		try {
			File file = new File(temp);
			if (file.exists()) {
				LOG.v("FormatPath","","file.exists()");
				return temp;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (path.startsWith("/mnt") || path.startsWith("/storage") || path.startsWith("content://")) {
			try{
				temp = URLDecoder.decode(temp);
			}catch(Exception e)
			{
				e.printStackTrace();
				return path;
			}
			return temp;
		}
		
		if (!path.startsWith("file://") && !path.startsWith("/") ) {
			return path;
		}
		String SDCARD_PATH = getSdcardPath();
		if (null == SDCARD_PATH) {
			return null;
		}
		
		int index1 = temp.indexOf("%");
		if(index1 >= 0){
			String dirPath = temp.substring(7, index1);
			temp = temp.substring(index1 , temp.length());
			temp = dirPath + temp;
		}else{
			temp = temp.substring(7 , temp.length());
		}
		
		
		try{
			temp = URLDecoder.decode(temp);
		}catch(Exception e)
		{
			e.printStackTrace();
			return path;
		}
		LOG.v(TAG,"", temp);
		return temp;
	}
	public String getRealPath(String path) {
		if(null == path || null == YouExplorer.instance){
			return null;
		}
		if (path.indexOf("video/") > 0) {
			Cursor c = YouExplorer.instance.managedQuery(Uri.parse(path),
					new String[] { MediaStore.Video.Media.DATA }, null, null,
					null);
			if (c != null) {
				c.moveToFirst();
				int cid = c.getColumnIndex(MediaStore.Video.Media.DATA);

				path = c.getString(cid).trim();
			}
		} else if (path.indexOf("audio/") > 0) {
			Cursor c = YouExplorer.instance.managedQuery(Uri.parse(path), new String[] {
					MediaStore.Audio.Playlists._ID,
					MediaStore.Audio.Playlists.DATA }, null, null,
					MediaStore.Audio.Playlists._ID);
			if (c != null) {
				c.moveToFirst();
				int cid = c.getColumnIndex(MediaStore.Audio.Playlists.DATA);
				path = c.getString(cid).trim();
			}
		} else if (path.startsWith("content://mms/")) {
			String cachePath = null;
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					cachePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
				} else {
					cachePath = YouExplorer.instance.getCacheDir().toString();
				}
				String fileSubfix = null;
				
				if (null != path && !TextUtils.isEmpty(path)) {
					int index = path.lastIndexOf("/");
					if (index >= 0) {
						fileSubfix = path.substring(index + 1);
					}
				} else {
					fileSubfix = "mp4";
				}

				cachePath = cachePath + "/"
						+ String.valueOf(System.currentTimeMillis()) + "."
						+ fileSubfix;

				byte[] buffer = new byte[4096];
				int length = -1;
				is = YouExplorer.instance.getContentResolver().openInputStream(Uri.parse(path));
				fos = new FileOutputStream(new File(cachePath));
				while ((length = is.read(buffer)) != -1) {
					fos.write(buffer, 0, length);
				}
				fos.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				cachePath = null;
			} catch (IOException e) {
				e.printStackTrace();
				cachePath = null;
			} catch (Exception e) {
				e.printStackTrace();
				cachePath = null;
			} finally {
				try {
					if(null != is){
						is.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if(null != fos){
						fos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			LOG.v("getRealPath", "","cachePath:"+cachePath);
			if((null != cachePath) && (!TextUtils.isEmpty(cachePath))){
				path = cachePath.trim();
			}
		}

		return path;
	}
	private  You_full_screen_player_item getLocalMediaFromOthers(String path,boolean can_direct_play){
		LOG.v("AptLoacalMedia", "getLocalMediaFromOthers", "path:"+path);
		if(null == path || TextUtils.isEmpty(path)){
			LOG.i("AptLoacalMedia", "getLocalMediaFromOthers", "err parms");
			return null;
		}
		String realPath;
		
		realPath = getRealPath(path);
		if(null == realPath || realPath.length() == 0){
			return null;
		}
		LOG.v("AptLoacalMedia", "getLocalMediaFromOthers", "realPath1:"+realPath);
		realPath = FormatPath(realPath);	
		LOG.v("AptLoacalMedia", "getLocalMediaFromOthers", "realPath2:"+realPath);
		
		You_full_screen_player_item item = new You_full_screen_player_item();
		
		item.url = realPath;
		item.name = realPath.substring(realPath.lastIndexOf("/")+1, realPath.length());
		item.play_time = 0;
		item.fraglist_cnt = 1;

		item.can_direct_play = can_direct_play;
		LOG.v("AptLoacalMedia", "getLocalMediaFromOthers", "url name:"+item.name+",item.url:"+item.url+",can_direct_play:"+can_direct_play);
		
		realPath = null;
		return item;
	}

	public void handlerIntent(Intent intent, boolean isFirst) {

		int ctrl = -1;
		Object ui_data = You_Core.FN_PAGE_ONLINE;
		String action = intent.getAction();
		LOG.v(TAG, "handlerIntent", "action : " + action + " isFirst : "+ isFirst);
		int reportType = 2;
		if (null != action) {
			if (YouPlayerConstant.YOU_ACTION_FULLPLAY.equals(action)) {
				if (isFirst) {
					ctrl = You_Core.FN_COREPAGE_BTN_START_FROM_EXTERNAL;
				} else {
					ctrl = You_Core.FN_COMMON_BTN_PLAY_FROM_EXTERNAL;
				}
				You_full_screen_player_item data = new You_full_screen_player_item();
				data.url = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_URL);
				ui_data = data;
			} else if (YouPlayerConstant.YOU_ACTION_NOTIFICATION.equals(action)){
				if (isFirst) {
					ctrl = You_Core.FN_COREPAGE_BTN_START_FROM_NOTIFICATION;
				} else {
					ctrl = You_Core.FN_COMMON_BTN_PUSH_PAGE_FROM_EXTERNAL;
				}
				ui_data = intent.getIntExtra(YouPlayerConstant.YOU_ACTION_PAGE,You_Core.FN_PAGE_ONLINE);
				String name = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_NAME);;
				String url = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_URL);
				String ourl = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_OURL);
				String weibourl = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_WEIBOURL);
				boolean showPlayButton = intent.getBooleanExtra(YouPlayerConstant.YOU_ACTION_BTNPLY, true);
				String definition = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_DEFINITION);
				String pic = intent.getStringExtra(YouPlayerConstant.YOU_ACTION_PIC);
				int liveBroadcastFlag = 0;
				LOG.v(TAG, "handlerIntent", "name : " + name + " url : " + url
						+ " ourl : " + ourl + " weibourl : " + weibourl
						+ " definition : " + definition + " pic : " + pic
						+ " showPlayButton : " + "" + showPlayButton);
				You_data_original_web_t original_web = new You_data_original_web_t(
						name, url, ourl, weibourl, definition, pic, "",
						liveBroadcastFlag, false,showPlayButton,false);
				YouPlayerEventControler.fn_core_service_request(ctrl,You_Core.FN_UI_EVT_TOUCH_UP, original_web , ui_data);
				LOG.v(TAG, "handleIntent :", "reportType :11" + ",isFirst : "+isFirst);
				if (isFirst) {
					reportType = 11;
					YouPlayerEventControler.fn_core_service_request(You_Core.FN_REPORT_PLAYER_INFO_START, You_Core.FN_UI_EVT_TOUCH_UP, null, reportType);
				}
				return;
			} else if (YouPlayerConstant.YOU_ACTION_EXPLORER.equals(action)) {
				if (isFirst) {
					ctrl = You_Core.FN_COREPAGE_BTN_START;
					appFrame.isStartFromExternal = true;
				} else {
					ctrl = You_Core.FN_COMMON_BTN_PUSH_PAGE_FROM_EXTERNAL;
				}
				ui_data = intent.getIntExtra(YouPlayerConstant.YOU_ACTION_PAGE,You_Core.FN_PAGE_ONLINE);
			} else if ("android.intent.action.VIEW".equals(action)) {
				if (isFirst) {
					ctrl = You_Core.FN_COREPAGE_BTN_START_FROM_EXTERNAL;
					appFrame.isStartPlayFromExternal = true;
				} else {
					ctrl = You_Core.FN_COMMON_BTN_PLAY_FROM_EXTERNAL;
				}
				LOG.v(TAG, "intent.getDataString()", intent.getDataString());
				ui_data = getLocalMediaFromOthers(intent.getDataString(), true);
				if (isFirst) {
					if(((You_full_screen_player_item)ui_data).url.contains("http:") 
							|| ((You_full_screen_player_item)ui_data).url.contains("rtsp:")){
						reportType = 5;
					}else{
						reportType = 4;
					}
				}	
			} 
		} else if (isFirst) {
			ctrl = You_Core.FN_COREPAGE_BTN_START;
			ui_data = You_Core.FN_PAGE_ONLINE;
			reportType = 2;
		}
		LOG.i(TAG, "handleIntent :", "ctrl :" + ctrl + "" + " ui_data :" + ui_data);
		
		if (ctrl > -1) {
			YouPlayerEventControler.fn_core_service_request(ctrl,You_Core.FN_UI_EVT_TOUCH_UP, null, ui_data);
		}
		
		LOG.e(TAG, "handleIntent :", "reportType :" + reportType+",isFirst :"+isFirst);
		if (isFirst) {
			YouPlayerEventControler.fn_core_service_request(You_Core.FN_REPORT_PLAYER_INFO_START, You_Core.FN_UI_EVT_TOUCH_UP, null, reportType);
		}
	}

	public static void showToast(String msg, int duration) {
		LOG.v(TAG, " ", "showToast msg : " + msg);
		if (null != instance) {
			instance.sendShowToastMsg(msg, duration);
		}
	}

	public static void showToast(int resId, int duration) {
		LOG.v(TAG, " ", "showToast resId : " + resId);
		if (null != instance) {
			Toast.makeText(instance, resId, duration).show();
		}
	}

	public void sendShowToastMsg(final String msg, final int duration) {
		new Handler(getMainLooper()).post(new Runnable() {
			public void run() {
				Toast.makeText(instance, msg, duration).show();
			}
		});
	}

	static Vector<Runnable> execs = new Vector<Runnable>();
	public static void addOnStopExec(Runnable runable){
		execs.add(runable);
	}
}
