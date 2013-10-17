package com.youplayer.player.local;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.youplayer.player.R;

import com.youplayer.util.LOG;

public class YouPlayerMediaFile extends YouPlayerInterfaceFile implements Externalizable {
	
	private static final long serialVersionUID = 1L;

	private static final String TAG = "MediaFile";
	
	private String ID = "";

	private boolean isFrag = false;
	private int[] fragtimeArray;
	private int duration;
	private String[] fragPathArray;
	private int currFrag;

	private int playedTime;
	
	private String songName;
	private String albumName;
	private String singerName;
	private String KBPSName;
	
	private String HzName;
	
	private String picPath;
	
	private String WeiboURL;
	
	private String parentPath;
	
	private int position; // 文件夹所在list 位置
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public YouPlayerMediaFile() {
		
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}


	public boolean getIsFrag() {
		return isFrag;
	}

	private void setIsFrag(boolean isFrag) {
		this.isFrag = isFrag;
	}

	public int getTotalDuration() {
		return duration;
	}

	public void setTotalDuration(int length) {
		this.duration = length;
	}

	public void setFrag(int[] frag) {
		if(frag != null && frag.length > 0) {
			setIsFrag(true);
			for (int i = 0; i < frag.length; i++) {
				duration += frag[i];
			}
			this.fragtimeArray = frag;
		}
	}

	public void setFragPath(String[] fragPath) {
		this.fragPathArray = fragPath;
	}

	public String getPlayPath() {
		String path = null;
		switch (sourceType) {
		case SOURCE_TYPE_LOACAL:
			path = getPath();
			break;
			
		case SOURCE_TYPE_DOWNLOAD:
		case SOURCE_TYPE_SERVER:
			if(null != fragPathArray){
				path = fragPathArray[currFrag];
			} else path = "";
			LOG.v(TAG, "getPlayPath currFrag", currFrag);
			LOG.v(TAG, "getPlayPath path", path);
			break;
			
		case SOURCE_TYPE_ONLINE:
			path = getPath();
			break;

		default:
			break;
		}
		if(path != null)
		{
			path = path.replaceAll("&amp;", "&");
		}
		LOG.v(TAG, "getPlayPath path", path);
		return path;
	}

	public int getPlayedFragsDuration() {
		int time = 0;
		for (int i = 0; i < currFrag; i++) {
			time += fragtimeArray[i];
		}
		return time;
	}

	public void setPlayedTotalDuration(int playedTime) {
		this.playedTime = playedTime;
	}

	public int getPlayedTotalDuration() {
		return playedTime;
	}

	public int getFragOffsetFromBeg(int time) {
		LOG.v(TAG, "getFragOffsetFromBeg time", time);
		if(fragtimeArray.length > 1) {
			int num = 0;
			while ((time -= fragtimeArray[num]) > 0) {
				num++;
			}
			if (currFrag != num) {
				currFrag = num;
				LOG.v(TAG, "getFragOffsetFromBeg currFrag", currFrag);
				return time += fragtimeArray[num];
			}
		}
		LOG.v(TAG, "getFragOffsetFromBeg currFrag", currFrag);
		return -1;
	}
	
	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getPlayingAudioSongName()
	{
		return songName;
	}
	
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	public String getPlayingAudioAlbumName()
	{
		return albumName;
	}
	
	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}
	
	public String getPlayingAudioSingerName()
	{
		return singerName;
	}
	
	public void setKBPSName(String KBPSName) {
		this.KBPSName = KBPSName;
	}
	
	public String getPlayingAudioKBPSName()
	{
		return KBPSName;
	}
	
	public void setHzName(String HzName) {
		this.HzName = HzName;
	}
	
	public String getPlayingRadioHzName()
	{
		return HzName;
	}
	
	public Bitmap getPlayingRadioPicture()
	{
		return null;
	}
	
	public Bitmap getPlayingSingerPicture()
	{
		return null;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getWeiboURL() {
		return WeiboURL;
	}

	public void setWeiboURL(String weiboURL) {
		WeiboURL = weiboURL;
	}
	
	public String getTimeString() {
		return null;//MediaResolver.getTimeString(mediaType,duration);
	}
	
	public String getSizeString() {
		return null;//MediaResolver.getSizeString(size, 2);
	}
	
	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	@Override
	public void readExternal(ObjectInput input) throws IOException,
			ClassNotFoundException {
		path = (String) input.readObject();
		name = (String) input.readObject();
		modified = input.readLong();
		mediaType = input.readInt();
		size = input.readLong();
		parent = (String)input.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeObject(path);
		output.writeObject(name);
		output.writeLong(modified);
		output.writeInt(mediaType);
		output.writeLong(size);
		output.writeObject(parent);
	}

	@Override
	public Bitmap getDefaultIcon(Context context) {
		Bitmap temp = null;
		try {
			switch (mediaType) {
			case MEDIA_TYPE_VIDEO:
				temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.youplayer_local_file_default);
				break;
			case MEDIA_TYPE_MYAUDIO:
				temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.youplayer_local_file_default);
				break;
			case MEDIA_TYPE_AUDIO:
			case MEDIA_TYPE_RADIO:
				temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.youplayer_local_folder_music);
				break;

			default:
				temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.youplayer_local_file_default);
				break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public static void v(String type, String msg) {
		LOG.v(TAG, type, msg);
	}

	public static void err(String type, String msg) {
		LOG.e(TAG, type, msg);
	}

	@Override
	public void createFileIcon(Context context, MediaInfoCallBack callback) {
		// TODO Auto-generated method stub
		
	}
 }
