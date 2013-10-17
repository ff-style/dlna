package com.youplayer.player.fullplayer;


import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.youplayer.player.R;

public class YouPlayerQualityDialog {
	
	public static PopupWindow show(final View view,String level,int hidelevel,final OnClickListener listener ){
		
		if( level ==  null || level.length() < 3 )
			return null;
		
		final PopupWindow pw = new PopupWindow(view.getContext());
		
		BitmapDrawable drawable = (BitmapDrawable) view.getResources().getDrawable(R.drawable.youplayer_fullplayer_quality_bg);
		
		pw.setBackgroundDrawable(drawable);
		pw.setWidth(drawable.getBitmap().getWidth());
		pw.setHeight(drawable.getBitmap().getHeight());
		
		ViewGroup contentView = (ViewGroup) LayoutInflater.from(view.getContext()).inflate(R.layout.youplayer_fullplayer_quality_view, null);
		
		OnClickListener clistener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				listener.onClick(v);
				pw.dismiss();
			}
		};
		
		View v1 = contentView.findViewById(R.id.btn_quality1);
		View v2 = contentView.findViewById(R.id.btn_quality2);
		View v3 = contentView.findViewById(R.id.btn_quality3);
	
		v1.setVisibility(View.GONE);
		v2.setVisibility(View.GONE);
		v3.setVisibility(View.GONE);
		if( level.indexOf('1') > -1 ) v1.setVisibility(View.VISIBLE);
		if( level.indexOf('2') > -1 ) v2.setVisibility(View.VISIBLE);
		if( level.indexOf('3') > -1 ) v3.setVisibility(View.VISIBLE);
		if( hidelevel == 1 ) v1.setVisibility(View.GONE);
		else if( hidelevel == 2 ) v2.setVisibility(View.GONE);
		else if( hidelevel == 3 ) v3.setVisibility(View.GONE);
		
		v1.setOnClickListener(clistener);
		v2.setOnClickListener(clistener);
		v3.setOnClickListener(clistener);
		
		pw.setContentView(contentView);

		    

		pw.setOutsideTouchable(false);
		pw.setTouchable(true);
		pw.setFocusable(true);
		pw.showAsDropDown(view,(view.getWidth()-pw.getWidth())/2,0);
		
		return pw;
		 
		
	}
	public static interface IOnSelectedListener{
		public void onSelected(int index);
	}
}

