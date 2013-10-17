package com.youplayer.player.frame;

import android.content.Context;

import com.youplayer.core.You_Core;
import com.youplayer.player.YouPlayerFullScreenPlayer;
import com.youplayer.util.LOG;

public class YouPlayerViewControlerFactory {
	private static final String TAG = "ViewControlerFactory";

	public static YouPlayerViewControler createViewByPageType(Context context,
			int page_type, Object core_data, Object ui_data) {
		YouPlayerViewControler view = null;
		LOG.v(TAG, " ", "createViewByPageType : " + page_type);
		if( You_Core.FN_PAGE_FULL_SCREEN_PLAYER == page_type) 
		{
			view = YouPlayerFullScreenPlayer.getInstance(context, core_data, ui_data);
		}
		view.setTag(page_type);
		return view;
	}

}
