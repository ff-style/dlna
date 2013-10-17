package com.talent.allshare.local;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youplayer.player.R;


public class FileAdapter extends BaseAdapter {
	private ViewHolder holder;
	private Context context;
	private List<File> vList;
	int type ; //0 1 2 3

	public FileAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setDataList(List<File> videoBeans,int type) {
		this.vList = videoBeans;
		this.type = type;
	}

	@Override
	public int getCount() {
		return vList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.file_list_item, null);
			holder.video_img = (ImageView) convertView.findViewById(R.id.list_item_image);
			holder.video_url = (TextView) convertView.findViewById(R.id.list_item_url);
			switch (type) {
			case 0:
				holder.video_img.setBackgroundResource(R.drawable.local_video_);
				break;
			case 1:
				holder.video_img.setBackgroundResource(R.drawable.local_music_);
				break;
			case 2:
				holder.video_img.setBackgroundResource(R.drawable.local_pic_);
				break;
			default:
				holder.video_img.setBackgroundResource(R.drawable.local_file_);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (vList != null && vList.get(position) != null) {
			File f = vList.get(position);
			holder.video_url.setText(f.getName()/*.getPath()*/+"");
		}

		return convertView;
	}

	class ViewHolder {
		ImageView video_img;
		TextView video_url;
	}

}
