package com.youplayer.player.frame;

import com.youplayer.core.You_Core;
import com.youplayer.player.YouExplorer;
import com.youplayer.util.LOG;


public class YouPlayerEventControler {

	private static final String TAG = "EventControler";

	public static void fn_core_service_request(int ctrl, int evt, Object arg1,Object ui_data) {
		
		LOG.i(TAG, " ", " ctrl : " + ctrl + " evt : " + evt
			    + " arg1 : " + (null == arg1 ? "null" : arg1)
			    + " ui_data : " + (null == ui_data ? "null" : ui_data));
		
		You_Core.fn_core_service_request(ctrl, evt, arg1, ui_data);
	}


	public static void action_handler_callback(int page_id, int page_action,Object core_data, Object ui_data) {
		
		LOG.i(TAG, " ", " page_id : " + page_id + " page_action : " + page_action
				    + " core_data : " + (null == core_data ? "null" : core_data)
				    + " ui_data : " + (null == ui_data ? "null" : ui_data));

		YouPlayerAppFrame appFrame = YouExplorer.appFrame;

		YouPlayerContainerView containView = appFrame.getContainer();

		YouPlayerViewControler viewControler = null;

		if (appFrame.action_callback(page_id, page_action, core_data, ui_data)) {
			
			return;
		}
		if (appFrame.getCurrentState() == YouPlayerAppFrame.STATE_EXPLORER) {
	
			if (containView.action_callback(page_id, page_action, core_data,ui_data)) {
				
				return;
			}
			
			viewControler = containView.getCurrentViewControler();
			
		} else {
			
			viewControler = appFrame.getFullPlayerControler();
		}

		if (viewControler != null) {
			
			if (page_id == viewControler.getTag()) {
				
				viewControler.action_callback(page_id, page_action, core_data,ui_data);
			}
		}
	}
}
