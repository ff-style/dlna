package com.talent.allshare.server;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youplayer.player.R;




public class ShareAdapter extends BaseAdapter {
	private List<String> files;
	private Context context;
	
	public ShareAdapter(Context context) {
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
		tv.setText(files.get(i));
		return subview;
	}

	
	
	
}