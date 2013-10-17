package com.talent.allshare.downloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.talent.allshare.downloader.DownlaodStateListener.OnDownloadFinishedListener;
import com.talent.allshare.downloader.DownlaodStateListener.OnDownloadStartedListener;
import com.talent.allshare.downloader.DownlaodStateListener.OnProgressUpdateListener;

public class DownloadFileAsync extends AsyncTask<String, String, String> {
	private final static String TAG = "Downloader";
	private String SDCARD = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	private String MYDOWNLOAD = "MacroDisk";
	private String mDownloadUrl = null;
	private String mFileName = null;
	private String mFilePath;
	private boolean mDownloading = true;
	private DownloadDB mDownLoadDB;
	private int mCompleteSize = 0;
	private int msizeperupdate= 100*1024;
	private long mTempSize = 0;
	private long mtimeMillis=0;
	private int mspeed=3*1024; //kb/s
	private Context mContext;

	private OnDownloadStartedListener mOnDownloadStartedListener = null;
	private OnProgressUpdateListener mOnProgressUpdateListener = null;
	private OnDownloadFinishedListener mOnDownloadFinishedListener = null;
	private DownloadNotification notification = null;

	public DownloadFileAsync(Context context, String fileName,
			String downloadUrl,DownloadNotification notification) {
		mFileName = fileName;
		mContext = context;
		File file = new File(SDCARD + "/" + MYDOWNLOAD);
		if (!file.exists()) {
			file.mkdirs();
		}
		mFilePath = SDCARD + "/" + MYDOWNLOAD + "/" + mFileName;
		mDownloadUrl = downloadUrl;
		mDownLoadDB = new DownloadDB(context);
		this.notification = notification;
	}

	public void setOnDownloadStartedListener(OnDownloadStartedListener l) {
		mOnDownloadStartedListener = l;
	}

	public void setOnProgressUpdateListener(OnProgressUpdateListener l) {
		mOnProgressUpdateListener = l;

	}

	public void setOnDownloadFinishedListener(OnDownloadFinishedListener l) {
		mOnDownloadFinishedListener = l;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mOnDownloadStartedListener != null) {
			mOnDownloadStartedListener.onDownloadStarted(mFileName,
					mDownloadUrl, 0);
		}
	}

	public void stopDownload() {
		mDownloading = false;
		mDownLoadDB.updataInfo(mDownloadUrl, mCompleteSize,
				getFileMd5String(new File(mFilePath)));
		mDownLoadDB.closeDb();
	}

	@Override
	protected String doInBackground(String... params) {

		DownloadInfo info = getDownloadInfo(mDownloadUrl);
		if (info == null) {
			Log.e(TAG, "error, downloadInfo is null !");
			return null;
		}
		int totalSize = info.getFileSize();
		mCompleteSize = info.getCompeleteSize();
		mFilePath = info.getFilePath();
		mspeed=3*1024;
		publishProgress(mFileName, mDownloadUrl, Integer.toString(mCompleteSize) ,Integer.toString(totalSize),mFilePath,Integer.toString(info.getId()),Integer.toString(mspeed));
		HttpURLConnection c = null;
		RandomAccessFile randomAccessFile = null;
		InputStream is = null;
		try {
			// connecting to url
			Log.e(TAG, "do in Background ...." + mDownloadUrl);
			URL u = new URL(mDownloadUrl);
			c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Range", "bytes=" + info.getCompeleteSize()
					+ "-" + info.getFileSize());
			//c.setDoOutput(true);
			c.connect();

			randomAccessFile = new RandomAccessFile(mFilePath, "rwd");
			randomAccessFile.seek(info.getCompeleteSize());
			// 閻忓繐妫滈—鍛▔鐎ｎ厽绁伴柣銊ュ閺嬪啯绂掔捄鍝勬櫢闁告帡顣︾换姘憋拷濡儤韬ǎ鍥ㄧ箓閻°劎鎹勯姘辩獮濞戞挸顑囧▓鎴﹀棘閸ワ附顐藉☉鎿勬嫹
			is = c.getInputStream();
			byte[] buffer = new byte[1024*100];
			int length = -1;

                     mtimeMillis=System.currentTimeMillis();
			
			if(totalSize>1024*1024*5) {
				msizeperupdate=1024*1024*2;
			}else if (totalSize>1024*1024){
				msizeperupdate=300*1024;
			}else if (totalSize>1024*300){
				msizeperupdate=200*1024;
			}else {
				msizeperupdate=100*1024;
			}
			while ((length = is.read(buffer)) != -1 && mDownloading) {
				randomAccessFile.write(buffer, 0, length);
				mCompleteSize += length;
				mTempSize+=length;
				//Log.e("app", "mFileName : " + mFileName + " mCompleteSize = " + mCompleteSize);
				// if(mCompleteSize>=totalSize){
				if(mTempSize>msizeperupdate || mCompleteSize>=totalSize) {
					long time=System.currentTimeMillis();
					//Log.e("app", "mspeed : " + mspeed + " kb/s"+",time="+time+",mtimeMillis="+mtimeMillis);
					mspeed=(int) (mTempSize*1000/(time-mtimeMillis))/1024;
					mtimeMillis=time;
					mTempSize=0;
					publishProgress(mFileName, mDownloadUrl, Integer.toString(mCompleteSize) ,Integer.toString(totalSize),mFilePath,Integer.toString(info.getId()),Integer.toString(mspeed));

					
				}
				//Thread.sleep(10);


			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (randomAccessFile != null) {
					randomAccessFile.close();
				}
				if (c != null) {
					c.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(mCompleteSize<totalSize) {
			mDownloading=false;
		}
		
		return null;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
//		RealSystemFacade facade  = new RealSystemFacade(mContext);
//		DownloadNotification notification = new DownloadNotification(mContext, facade);
//		DownloadInfo download = getDownloadInfo(progress[1]);
//		publishProgress(mFileName, mDownloadUrl, Integer.toString(mCompleteSize) ,Integer.toString(totalSize),mFilePath,Integer.toString(info.getId()));
		DownloadInfo download = new DownloadInfo(Integer.parseInt(progress[5]), progress[1], Integer.parseInt(progress[3]), Integer.parseInt(progress[2]), mFilePath, "",Integer.parseInt(progress[6])) ;
		notification.updateActiveNotification(download,false);
		if (mOnProgressUpdateListener != null) {

			//Log.i("download", "Complete : " + progress[2] + "  total:  = " + progress[3]+ "finsh %"+(int) ((Integer.parseInt(progress[2]) * 100) / Integer.parseInt(progress[3])));

			mOnProgressUpdateListener.onProgressUpdate(progress[0],
					progress[1], 
							(int) ((((long) Integer.parseInt(progress[2])) * 100) / ((long) Integer.parseInt(progress[3]))),Integer.parseInt(progress[6]));
		}
	}

	@Override
	protected void onPostExecute(String unused) {
		// dismiss the dialog after the file was downloaded
//		RealSystemFacade facade  = new RealSystemFacade(mContext);
//		DownloadNotification notification = new DownloadNotification(mContext, facade);
		DownloadInfo download = getDownloadInfo(mDownloadUrl);
		notification.updateActiveNotification(download,true);
		notification.updateCompletedNotification(download, !mDownloading);
		if (mOnDownloadFinishedListener != null) {
			mOnDownloadFinishedListener.onDownloadFinished(mFileName,
					mDownloadUrl,mDownloading);
		}
		delete(mDownloadUrl);
	}

	// 闁告帞濞�▍搴ㄥ极閻楀牆绁﹂幖瀛樻尫閼垫唹rlstr閻庣數鎳撶花鏌ユ儍閸曨亞鐟撻弶鐐舵濞呮帗绌遍埄鍐х礀
	public void delete(String url) {
		mDownLoadDB.delete(url);
	}

	private String checkFileName(String fileDir, String fileName) {
		File file = new File(fileDir + "/" + fileName);
		String newFileName = fileName;
		if (file.exists()) {
			for (int i = 1; i < 1024; i++) {
				newFileName = String.format("%s(%d)", fileName, i);
				file = new File(fileDir + "/" + newFileName);
				if (!file.exists()) {
					return newFileName;
				}
			}
		}
		return newFileName;
	}

	private DownloadInfo getDownloadInfo(String url) {
		DownloadInfo info = null;
		if (isFirstDownlaod(url)) {
			info = firstDownloadInit();
		} else {
			Log.e(TAG, "has download before...");
			info = mDownLoadDB.getDownloadInfo(url);
			if (info != null) {
				Log.e(TAG, "compeleted: " + info.getCompeleteSize());
			}
		}
		return info;
	}

	/**
	 * 闁告帇鍊栭弻鍥及椤栨碍鍎婇柡鍕靛灣椤戝洦绋夐敓绛嬪仹 濞戞挸顑堝ù锟�
	 */
	private boolean isFirstDownlaod(String downloadUrl) {
		return mDownLoadDB.hasInfo(downloadUrl);
	}

	/**
	 * 闁告帗绻傞‖濠囧礌閿燂拷
	 */
	private DownloadInfo firstDownloadInit() {
		Log.e(TAG, "firstDownloadInit.....");
		DownloadInfo downloadInfo = null;
		try {
			URL url = new URL(mDownloadUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			int fileSize = connection.getContentLength();
			mFileName = checkFileName(SDCARD + "/" + MYDOWNLOAD, mFileName);
			mFilePath = SDCARD + "/" + MYDOWNLOAD + "/" + mFileName;
			File file = new File(mFilePath);
			if (!file.exists()) {
				file.createNewFile();
			} else {

			}
			// 闁哄牜鍓欏﹢瀵告媼閸ф锛栭柡鍌氭矗濞嗭拷
			//RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			//accessFile.setLength(fileSize);
			//accessFile.close();
			downloadInfo = new DownloadInfo(mDownloadUrl, fileSize, 0,
					mFilePath, getFileMd5String(file),mspeed);
			mDownLoadDB.saveDwonloadInfo(downloadInfo);
			connection.disconnect();
			downloadInfo = getDownloadInfo(mDownloadUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return downloadInfo;
	}

	private String getFileMd5String(File file) {
		return "";
		/*
		 * try{ MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		 * FileInputStream in = new FileInputStream(file); FileChannel ch =
		 * in.getChannel(); MappedByteBuffer byteBuffer =
		 * ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		 * messageDigest.update(byteBuffer); return new
		 * String(messageDigest.digest()); }catch(Exception e){
		 * e.printStackTrace(); } return "";
		 */
	}
}
