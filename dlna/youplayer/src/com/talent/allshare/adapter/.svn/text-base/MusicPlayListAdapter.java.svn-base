package com.talent.allshare.adapter;

import java.util.List;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.talent.allshare.bean.PlaylistBean;
import com.talent.allshare.network.Item;
import com.talent.allshare.network.UpnpUtil;
import com.youplayer.player.R;


@SuppressLint("ResourceAsColor") public class MusicPlayListAdapter extends BaseAdapter{

	private static final CommonLog log = LogFactory.createLog();
	
	private  List<PlaylistBean> contentItem;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public MusicPlayListAdapter(Context context, List<PlaylistBean>  contentItem) {
		mInflater = LayoutInflater.from(context);
		this.contentItem = contentItem;
		mContext = context;
	}
	
	public void refreshData(List<PlaylistBean>  contentItem)
	{
		this.contentItem = contentItem;
		notifyDataSetChanged();
	}

	public void clear()
	{
		if (contentItem != null){
			contentItem.clear();
			notifyDataSetChanged();
		}
	}
	/**
	 * The number of items in the list is determined by the number of
	 * speeches in our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return contentItem.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficent to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position) {
		return contentItem.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */

	@SuppressLint("ResourceAsColor") public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.music_play_list_item, null);
		}
		PlaylistBean dataItem = (PlaylistBean) getItem(position);
		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView islocal = (TextView) convertView.findViewById(R.id.islocal);
		
		title.setText(dataItem.item.getTitle());
		islocal.setText(dataItem.islocal);
		return convertView;
	}
}
