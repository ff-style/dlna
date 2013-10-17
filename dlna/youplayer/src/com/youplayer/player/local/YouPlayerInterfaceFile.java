package com.youplayer.player.local;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.youplayer.util.LOG;

import android.content.Context;
import android.graphics.Bitmap;

public abstract class YouPlayerInterfaceFile {
	
	protected String parent = "";
	protected String name = "";
	protected String path = "";
	protected long modified = 0;
	protected Bitmap drawable;
	protected String iconPath;
	protected long size;
	public int fileNum = 0;
	
	protected int sourceType;
	public static final int SOURCE_TYPE_LOACAL = 0;
	public static final int SOURCE_TYPE_SERVER = 1;
	public static final int SOURCE_TYPE_ONLINE = 2;
	public static final int SOURCE_TYPE_DOWNLOAD = 3;
	
	protected int list_Type = 0;
	public static final int LIST_LOCAL = 0;
	public static final int LIST_FAVORITES = 1;
	public static final int LIST_PLAYED = 2;
	
	/** media类型 */
	public int mediaType;
	public static final int MEDIA_TYPE_VIDEO = 0;
	public static final int MEDIA_TYPE_AUDIO = 1;
	public static final int MEDIA_TYPE_RADIO = 2;
	public static final int MEDIA_TYPE_FOLDER = 3;
	public static final int MEDIA_TYPE_MYAUDIO = 4;//用户自拍视频
	
	protected int width;
	protected int height;
	
	/**
	 *  文件夹下的文件集合
	 */
	public List<YouPlayerInterfaceFile> mediaList = new ArrayList<YouPlayerInterfaceFile>();
	
	public static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();   
	
	public void resolveMedia(Context context, MediaInfoCallBack callBack) {
		// ThreadPoolManager.getInstance().addExecuteTask(new ResolveMediaInfo(context, InterfaceFile.this, callBack));
	}
	
	public Bitmap getFileIcon(Context context) {
		Bitmap temp = null;
		if ((null!= imageCache) && imageCache.containsKey(path)) {
			LOG.v("getFileIcon", "getFileIcon", "get imagecache");
            SoftReference<Bitmap> softReference = imageCache.get(path);
            temp = softReference.get();
            LOG.v("getFileIcon", "getFileIcon", "temp : "+ temp);
        }
		if(temp == null) {
			temp = getDefaultIcon(context);
		}
		return temp;
	}
	
	public abstract Bitmap getDefaultIcon(Context context);
	
	public static void destoryThreadPoll() {
//		executor = null;
//		queue = null;
	}
	
	protected boolean isHD;
	
	public void addMediaFile(YouPlayerInterfaceFile mediaFile) {
		mediaList.add(mediaFile);
	}
	
	public List<YouPlayerInterfaceFile> getMediaList() {
		return mediaList;
	}

	public void setMediaList(ArrayList<YouPlayerInterfaceFile> mediaList) {
		this.mediaList = mediaList;
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}
	
	public void setListType(int list_Type) {
		this.list_Type = list_Type;
	}
	
	public int getListType() {
		return list_Type;
	}
    
    public abstract boolean isDirectory();
    
    
    public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public long getMediaFileSize() {
		return size;
	}

	public void setMediaFileSize(long size) {
		this.size += size;
	}

	public void setMediaFileSizeToZero() {
		this.size = 0;
	}
	
    public static Bitmap getDrawable(String path){
		Bitmap temp = null;
		if ((null!= imageCache) && imageCache.containsKey(path)) {   
            SoftReference<Bitmap> softReference = imageCache.get(path);   
            temp = softReference.get();   
        }
		return temp;
    }
    
	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
    
    public static void setDrawable(String path, Bitmap drawable){
    	if(null == imageCache){
    	   imageCache = new HashMap<String, SoftReference<Bitmap>>();
    	}
		imageCache.put(path, new SoftReference<Bitmap>(drawable)); 
		drawable  = null;
    }

	public long getModified() {
        return modified;
    }

    public void setModified(long date) {
        this.modified = date;
    }
    
    public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public boolean isHD() {
		return isHD;
	}

	public void setHD(boolean isHD) {
		this.isHD = isHD;
	}

	public int getFileNum() {
		return fileNum;
	}

	public void setFileNum(int fileNum) {
		this.fileNum = fileNum;
	}

	public void addFileNum() {
		this.fileNum++;
	}
	
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	protected UpdateBitmapListener listener;
    
    public interface UpdateBitmapListener {
		public void updateVideoDrawable(String path, Bitmap drawable, boolean isHD, String durtion);
		public void updateAudioDrawable(String path, Bitmap drawable, String singger, String durtion);
		public void updateMediaDrawable(String path, Bitmap drawable, boolean isHD);
	}
    
    public abstract void createFileIcon(Context context, MediaInfoCallBack callback);
    
    public static boolean isLoadIcon;
	
	public static void releaseRes() {
		if(imageCache != null) {
			imageCache.clear();
		}
	}
	
	public static interface MediaInfoCallBack {
		//msg0 时间 msg1歌手
		public void callback(String path, Bitmap bitmap, boolean isHD, String msg0, String msg1,long playedTime);
	    //文件夹adapter 数据显示回调
		public void callBack(int position, Bitmap bitmap);
	}
   
}

