package com.talent.allshare.adapter;

import java.io.FileNotFoundException;
import java.util.List;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.talent.allshare.DownloadProcess;
import com.talent.allshare.network.Item;
import com.talent.allshare.network.UpnpUtil;
import com.youplayer.player.R;


@SuppressLint("ResourceAsColor") public class ContentAdapter extends BaseAdapter{

	private static final CommonLog log = LogFactory.createLog();
	
	private  List<Item> contentItem;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public ContentAdapter(Context context, List<Item>  contentItem) {
		mInflater = LayoutInflater.from(context);
		this.contentItem = contentItem;
		mContext = context;
	}
	
	public void refreshData(List<Item>  contentItem)
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

	@SuppressLint("ResourceAsColor") public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.content_list_item, null);
		}
		ImageView btn = (ImageView)convertView.findViewById(R.id.download);
		btn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Item item = contentItem.get(position);
				String requestUrl = item.getRes();
				downloadbyURL(requestUrl,item.getTitle());
			}
		});
		Item dataItem = (Item) getItem(position);
		ImageView iv = (ImageView)convertView.findViewById(R.id.imageView);
		TextView tvContent = (TextView)convertView.findViewById(R.id.tv_content);
		btn.setVisibility(View.VISIBLE);
		if(UpnpUtil.isVideoItem(dataItem)){
			iv.setBackgroundResource(R.drawable.local_video_);
		}else if(UpnpUtil.isAudioItem(dataItem)){
			iv.setBackgroundResource(R.drawable.local_music_);
		}else if(UpnpUtil.isPictureItem(dataItem)){
			iv.setBackgroundResource(R.drawable.local_pic_);
		}else if(UpnpUtil.isFileItem(dataItem)){
			iv.setBackgroundResource(R.drawable.local_file_);
		}else if(UpnpUtil.isNULLItem(dataItem)){
			iv.setBackgroundResource(R.drawable.local_dir_);
			btn.setVisibility(View.INVISIBLE);
		}
		
		tvContent.setText(dataItem.getTitle());

		return convertView;
	}
	
	protected void downloadbyURL(String requestUrl,String name) {

		String dirType;
		String subPath;
		int dotIndex = requestUrl.lastIndexOf(".");  
	    if(dotIndex < 0){  
	    	return;
	    }  

	    String end=requestUrl.substring(dotIndex,requestUrl.length()).toLowerCase();  

	DownloadProcess download = new DownloadProcess(mContext);
			String decodeUrl = download.decodeUri(requestUrl);
			String fileName = name+"."+end;
			download.startDownload(fileName, requestUrl);
	}
	
/*
	@SuppressLint("NewApi") protected void downloadbyURL(String requestUrl,String name) {
		DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		  
		 Uri uri = Uri.parse(requestUrl);
		 Request request = new Request(uri);
		 //设置允许使用的网络类型，这里是移动网络和wifi都可以 
		 request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI); 
		 //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION 
		 //request.setShowRunningNotification(false); 
		 //不显示下载界面 
		 request.setVisibleInDownloadsUi(false);
//		 request.set
		String dirType;
		String subPath;
		int dotIndex = requestUrl.lastIndexOf(".");  
	    if(dotIndex < 0){  
	    	return;
	    }  

	    String end=requestUrl.substring(dotIndex,requestUrl.length()).toLowerCase();  
		request.setDestinationInExternalPublicDir("/MacroDisk", name+"."+end);
		long id = downloadManager.enqueue(request);	
		
	}*/
}
