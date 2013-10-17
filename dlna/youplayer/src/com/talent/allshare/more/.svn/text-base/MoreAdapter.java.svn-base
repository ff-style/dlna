package com.talent.allshare.more;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youplayer.player.R;




public class MoreAdapter extends BaseAdapter {
	private List<String> files;
	private Context context;
	
	public MoreAdapter(Context context) {
		this.context = context;
		
	}
	
	public void setData(List<String> files){
		this.files = files;
	}
	
	
	@Override
	public int getCount() {
		if(files != null)
		return files.size();
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int i, View subview, ViewGroup arg2) {
		 subview = View.inflate(context, R.layout.more_list_item, null);
		TextView tv =(TextView) subview.findViewById(R.id.tv_content);
		ImageView image = (ImageView)subview.findViewById(R.id.more_image);
		if(i == 0)
		{
			image.setImageResource(R.drawable.wifi_setting);
		}
		else if(i == 1)
		{
			image.setImageResource(R.drawable.route_setting);
		}
		else if(i == 2)
		{
			image.setImageResource(R.drawable.player_setting);
		}
		tv.setText(files.get(i));
		return subview;
	}

	
	
	
}