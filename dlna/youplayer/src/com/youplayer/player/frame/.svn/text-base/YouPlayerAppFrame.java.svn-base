package com.youplayer.player.frame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.youplayer.core.You_Core;
import com.youplayer.core.struct.You_core_push_or_pop_page_data_t;

import com.youplayer.player.YouPlayerAbsoluteLayout;
import com.youplayer.player.YouExplorer;
import com.youplayer.player.YouPlayerFullScreenPlayer;

import com.youplayer.player.R;
//import com.youplayer.player.Util;
import com.youplayer.player.frame.YouPlayerContainerView.OnHideListener;
//import com.youplayer.util.YouUtility;
import com.youplayer.util.LOG;


public class YouPlayerAppFrame implements YouPlayerActionHandler{
	private static final String TAG = "AppFrame";

	public static final int STATE_EXPLORER = 0;

	public static final int STATE_FULLPLAYER = 1;
	
	public static final int HANDLER_LEFT_MENU = 0x01;
	public static final int HANDLER_RIGHT_MENU = 0x02;
	public static final int HANDLER_UPDATE_RIGHT_MENU = 0x03;
	public static final int HANDLER_LANDSCAPE = 0x04;
	public static final int HANDLER_PORTRAIT = 0x05;
	public static final int HANDLER_HIDE_RIGHT = 0x06;
	public static final int HANDLER_HIDE_LEFT = 0x07;
	public static final int HANDLER_CHANNEL_JUMP = 0x08;
	
	/**应用上下文*/
	private Activity context;
	/**根视图*/
	private FrameLayout rootLayout;
	/**视图-浏览导航视图*/
	public static YouPlayerAbsoluteLayout youExplorer;
	/**视图容器*/
    private YouPlayerContainerView container;
    /**全屏播放控制器*/
    private YouPlayerViewControler fullPlayerControler;

    /**当前视图*/
	private View currentView;
	/**当前状态*/
	private int currentState = -1;
	
	private Handler mHandler;
	
	private AlertDialog dialog;
	
	private boolean fullPlayerHasAdd = false;
	
	public boolean isStartFromExternal = false;
	
	public boolean isStartPlayFromExternal = false;
	public boolean isPlayBackground = false;
	
	public YouPlayerAppFrame(Activity context){
		this.context = context;
		rootLayout = (FrameLayout) context.findViewById(R.id.root_layout);
		initHandler();
	}
	
	public void initYouExplorerLayout(){
		youExplorer = (YouPlayerAbsoluteLayout) LayoutInflater.from(context).inflate(R.layout.youplayer_explorer, null);
		container = (YouPlayerContainerView) youExplorer.findViewById(R.id.container_layout);
		currentView = youExplorer;
		currentState = STATE_FULLPLAYER;
		container.setOnHideListener(new ContainerHideListener());
		rootLayout.addView(currentView);
	}
	

	public View getRootLayout(){
		return this.rootLayout;
	}

	public View getCurrentView(){
		return this.currentView;
	}
	

	public int getCurrentState(){
		return this.currentState;
	}

	public YouPlayerContainerView getContainer() {
		return container;
	}
	

	public YouPlayerViewControler getFullPlayerControler() {
		return fullPlayerControler;
	}
	
	private void initHandler() {
		mHandler = new Handler(context.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HANDLER_LANDSCAPE:
					setFullScreen(context);
					break;
				case HANDLER_PORTRAIT:
					setPortraitScreen(context);
					break;
				case HANDLER_HIDE_LEFT:
					YouPlayerEventControler.fn_core_service_request(You_Core.FN_COMMON_BTN_CONTEXT_MENU, You_Core.FN_UI_EVT_TOUCH_UP, null, null);	
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}
	
	@Override
	public boolean action_callback(int page_id, int page_action,
			Object core_data, Object ui_data) {
		switch (page_id) {
		case You_Core.FN_PAGE_ROOTPAGE:
			LOG.i(TAG, " ","handlerRootCallBack : " + page_id + " page_action : " + page_action );
			return  handlerRootCallBack(page_id, page_action, core_data, ui_data);
		default:
			LOG.i(TAG," ","handlerPageCallBack : " + page_id + " page_action : " + page_action );
			return false;
		}
		
	}
	
	private boolean handlerRootCallBack(int page_id, int page_action,
			Object core_data, Object ui_data) {
		int page_type = 0;
		switch (page_action) {
		case You_Core.FN_PAGE_EVT_CREATE:
			if (String.valueOf(You_Core.FN_PAGE_EXPLORER).equals(core_data.toString())) {
				initYouExplorerLayout();
				LOG.i(TAG, " ", "initYouExplorerLayout : ");
			}
			return true;
		case You_Core.FN_PAGE_EVT_PUSH:
			page_type = ((You_core_push_or_pop_page_data_t) core_data).page_type;
			switch (page_type) {
			case You_Core.FN_PAGE_FULL_SCREEN_PLAYER:
				if (currentState == STATE_EXPLORER) {
					if (null != getContainer().getCurrentViewControler()) {
						getContainer().getCurrentViewControler().onPause();
						getContainer().getCurrentViewControler().onStop();
					}
				}
				rootLayout.removeAllViews();
				
				fullPlayerControler = YouPlayerViewControlerFactory.createViewByPageType(context.getApplicationContext(), page_type, core_data, ui_data);
				
				currentView = fullPlayerControler.getView();
				
				currentState = STATE_FULLPLAYER;
			
				mHandler.sendEmptyMessageDelayed(HANDLER_LANDSCAPE, 50);
				return true;
			}
		case You_Core.FN_PAGE_EVT_POP:
			
			if (isStartPlayFromExternal && isPlayBackground) {
				LOG.e(TAG, " FN_PAGE_EVT_POP ", " killProcess ");
				exitApp();
				return true;
			}
			isStartPlayFromExternal = false;
			fullPlayerHasAdd = false;
			fullPlayerControler.finish();
			rootLayout.removeAllViews();
			currentView = youExplorer;
			// 设置屏幕为竖屏
			mHandler.sendEmptyMessageDelayed(HANDLER_PORTRAIT, 50);
			return true;
		case You_Core.FN_PAGE_EVT_EXIT_APP:
			YouPlayerEventControler.fn_core_service_request(You_Core.FN_REPORT_PLAYER_INFO_END, You_Core.FN_UI_EVT_TOUCH_UP,null /*LocalMediaUtil.getMediaFileData()*/, null);	
			//showExitDialog();
			exitApp();
			return true;
		}
		return false;
	}
	
	public void showExitDialog() {
		dialog = new AlertDialog.Builder(context)
		.setTitle(R.string.dialog_title)
		.setMessage(R.string.alert_dialog_message)
		.setNegativeButton(R.string.dialog_cancel, null)
		.setPositiveButton(R.string.dialog_ok,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitApp();
			}
		}).show();
	}
	
	public void cancelDialog() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
	
	public void exitApp() {
		YouPlayerEventControler.fn_core_service_request(You_Core.FN_COREPAGE_BTN_STOP, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
		youExplorer = null;
		YouExplorer.instance.finish();
		if(fullPlayerControler!= null){
			((YouPlayerFullScreenPlayer)fullPlayerControler).exitFullPlayerApp();
		}
  	}
	  
	private class ContainerHideListener implements OnHideListener{
		@Override
		public void goHide(int hideType) {
			switch (hideType) {
			case YouPlayerContainerView.HIDE_LEFT:
				YouPlayerEventControler.fn_core_service_request(You_Core.FN_COMMON_VIEW, You_Core.FN_UI_EVT_DRAG_LEFT, null, null);
				LOG.i(TAG, "ContainerHideListener ", "ContainerView.HIDE_LEFT ");
				break;
			case YouPlayerContainerView.HIDE_RIGHT:
				YouPlayerEventControler.fn_core_service_request(You_Core.FN_COMMON_VIEW, You_Core.FN_UI_EVT_DRAG_RIGHT, null, null);
				break;
			default:
				break;
               	}
			}
		}
	
	public void setFullScreen(Activity context){
        if(context != null){
            try {
            	fullPlayerControler.getView().setKeepScreenOn(true);
                context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setPortraitScreen(Activity context) {
        if(context != null){
            try {
            	if(fullPlayerControler != null)
            	{
//            		fullPlayerControler.getView().setKeepScreenOn(false);
            		View view = fullPlayerControler.getView();
            		if(view != null)
            		view.setKeepScreenOn(false);
            	}
                final WindowManager.LayoutParams attrs = context.getWindow().getAttributes();
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                context.getWindow().setAttributes(attrs);
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public void addFullScreenPlayer() {
		// 显示全屏播放视图控制器
		if(!fullPlayerHasAdd && currentState == STATE_FULLPLAYER){
			rootLayout.addView(currentView);
			((YouPlayerFullScreenPlayer)fullPlayerControler).viewHasAdded();
			fullPlayerHasAdd = true;
			fullPlayerControler.onStart();
			fullPlayerControler.onResume();
		}
	}
	
	public void addYouExplorer() {
		if (currentState == STATE_EXPLORER ) {
			if (null != rootLayout && rootLayout.getChildCount() == 0) {
					rootLayout.addView(currentView);
				if (null != getContainer().getCurrentViewControler()) {
					LOG.i(TAG, "CurrentViewControler :", getContainer().getCurrentViewControler().getClass().toString());
					getContainer().getCurrentViewControler().onStart();
					getContainer().getCurrentViewControler().onResume();
				}
			}
		}
	}
}
