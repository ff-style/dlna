package com.youplayer.player.fullplayer;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.youplayer.player.R;
public class YouPlayerMoreDialog {
	public static PopupWindow show(final View view,int speed,final OnClickListener listener ){	
		final PopupWindow pw = new PopupWindow(view.getContext());
		BitmapDrawable drawable = (BitmapDrawable) view.getResources().getDrawable(R.drawable.youplayer_fullplayer_more_bg);	
		pw.setBackgroundDrawable(drawable);
		int width = 800;
		int height = 400;
        try {
            WindowManager wm = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
		pw.setWidth(width*3/8);
		pw.setHeight(height*3/8);
		ViewGroup contentView = (ViewGroup) LayoutInflater.from(view.getContext()).inflate(R.layout.youplayer_fullplayer_more_view, null);
		OnClickListener clistener = new OnClickListener() {	
			@Override
			public void onClick(View v) {
				listener.onClick(v);	
				pw.dismiss();	
			}
		};
		
		int[] speeds = new int[]{
			R.drawable.youplayer_fullplayer_bottom_btn_speed08,
			R.drawable.youplayer_fullplayer_bottom_btn_speed10,
			R.drawable.youplayer_fullplayer_bottom_btn_speed15,
			R.drawable.youplayer_fullplayer_bottom_btn_speed20,
		} ;
		
		
		contentView.findViewById(R.id.fullplayer_imgb_download).setOnClickListener(clistener);
		contentView.findViewById(R.id.fullplayer_imgb_share).setOnClickListener(clistener);
		
		ImageButton img_speed = (ImageButton) contentView.findViewById(R.id.fullplayer_imgb_speed);
		img_speed.setOnClickListener(clistener);
		img_speed.setImageResource(speeds[speed]);
		
		contentView.findViewById(R.id.fullplayer_imgb_track).setOnClickListener(clistener);
		
		ImageButton img_3d = (ImageButton) contentView.findViewById(R.id.fullplayer_imgb_3d);
		img_3d.setOnClickListener(clistener);
		
		contentView.findViewById(R.id.fullplayer_btm_imgb_lock).setOnClickListener(clistener);
		
		
		pw.setContentView(contentView);
	
		pw.setOutsideTouchable(false);
		pw.setTouchable(true);
		pw.setFocusable(true);
		pw.showAsDropDown(view,30,0);
		
		return pw;	
	}
	public static interface IOnSelectedListener{
		public void onSelected(int index);
	}
}

