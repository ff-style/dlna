package com.youplayer.player;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.talent.allshare.downloader.DownloadNotification;
import com.talent.allshare.downloader.RealSystemFacade;
import com.youplayer.util.LOG;
import com.youplayer.util.YouUtility;

public class YouApplication extends Application{

	private static final String TAG = "YouApplication";
	private HashMap<String, Bitmap> mBGImageCache = new HashMap<String, Bitmap>();
	private static Context mContext = null;
	public static boolean cpu_is_ok = true;
	public static String cipher = null;
	public static boolean mongoad_switch = false;
	static{
		System.loadLibrary("wifi_setting");
	}


	private static YouApplication app;
	public DownloadNotification notification;
	
	public static YouApplication getInstance() {
		return app;
	}

	public static Context GetGlobalContext() {
		return mContext;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		LOG.e(TAG, "onCreate", "application launched. log from here...");
		mContext = this;
		cipher = YouUtility.ConfigureGetCipher(mContext);
		YouUtility.setCipher(mContext, cipher);
		YouUtility.initStrategyVersion(mContext);
//		DownloadUtil.setCachePlayTip(mContext, true);
		RealSystemFacade facade  = new RealSystemFacade(getApplicationContext());
		notification = new DownloadNotification(getApplicationContext(), facade);


		
		super.onCreate();

		app = this;
	}


		
	@Override
	public void onTerminate(){
		LOG.e(TAG, "onTerminate", "application terminated. log end...");

		
		super.onTerminate();
	}
	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		LOG.e(TAG, "onLowMemory", "low memory. somebody may kill this app...");
		super.onLowMemory();
	}

	public Bitmap getBitmap(String key) {
		if (mBGImageCache.containsKey(key)) {
			return mBGImageCache.get(key);
		} else {
			return null;
		}
	}
	
	public Bitmap setBitmap(String key, Bitmap image) {
		return mBGImageCache.put(key, image);
	}

	@Override
	protected void finalize() throws Throwable {
		Set<String> keyset = mBGImageCache.keySet();
		for (String key : keyset) {
			Bitmap bm = mBGImageCache.get(key);
			if (null != bm && !bm.isRecycled()) {
				bm.recycle();
			}
		}
		mBGImageCache.clear();
		
		super.finalize();
	}

	@SuppressLint("NewApi") public static boolean judgeByCpu(Context context) {
		if (YouExplorer.instance != null && !is_cpu_vfp_enabled()) {
			if (context.getResources().getBoolean(R.bool.cpu_vfp_needed)) {
				cpu_is_ok = false;
				new AlertDialog.Builder(context)
						.setTitle(R.string.app_name)
						.setMessage(R.string.cpu_vfp_error_tips)
						.setPositiveButton(R.string.cpu_vfp_error_button,
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,int which) {
										YouExplorer.appFrame.exitApp();
									}
								}).setCancelable(false).show();
				return false;
			}
		}
		return true;
	}

	public static boolean is_cpu_vfp_enabled()
	{
		String cpuinfo = ReadCPUinfo();
		if(cpuinfo == null || cpuinfo.length() == 0)
			return true;
		
		if(cpuinfo.contains("vfp"))
			return true;
		
		return false;
	}
	private static String ReadCPUinfo() {
		ProcessBuilder cmd;
		String result = "";

		try {
			String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		if(result.length() > 0)
		{	
			String low = result.toLowerCase();
			int beg = low.indexOf("Features"); 
			if(beg > 0)
			{
				String ret = low.substring(beg);
				String[] cpu = ret.split("\n");
				if(cpu.length > 0)
					result = cpu[0];
			}
		}
//		L.v("","ReadCPUinfo cpu info:", result);
		return result;
	}
}
