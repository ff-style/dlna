package com.youplayer.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.youplayer.core.You_Core;
import com.youplayer.core.adapter.YouPlayerJNIUtil;
import com.youplayer.core.mediainfo.YouPlayerBitMap;
import com.youplayer.core.struct.You_full_screen_player_data_to_ui;
import com.youplayer.player.YouPlayerConstant;
import com.youplayer.player.YouApplication;
import com.youplayer.player.YouExplorer;
import com.youplayer.player.Player_UIManager;
import com.youplayer.player.R;



abstract interface SensorCommander {
    public void addObserver(final Context context, YouPlayerSensorObserver ob);

    public void removeObserver(YouPlayerSensorObserver ob);

    public void notifyObservers(HashMap<String, Object> event);
}

public class YouUtility implements SensorCommander{
	public static final String TAG = "YouUtility";
	public static final String VIDEO_SCALE_MODE = "video_scale_mode";
	public static final String AUDIO_LOOP_MODE = "audio_loop_mode";
	public static final String MUTE_STATE = "Mute_state";
	public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode"; // Settings.System.SCREEN_BRIGHTNESS_MODE
	public static int g_brightness_value = -2;
	public static final String PREFS_NAME = "AUTO_UPDATE_DICT";
	public static final String CURRENT_VOLUME = "Current_volume";
	public static final String RECOMMEND_RIGHT = "recommend_right";
	public static final String RECOMMEND_FIRST = "recommend_first";

	public static final String APP_IS_FIRST = "app_is_first_run";
	
    public static final String SUB_TITLE_FONTSIZE = "SUB_TITLE_FONTSIZE";
    
    public static final String PLAY_QUALITY = "play_quality";
    
    public static final String CLEAR_AUTO_FLAG = "clear_auto_flag";
	public static final String AUTO_STATE_FLAG = "auto_state_flag";
	
	private static List<YouPlayerSensorObserver> g_sensor_observer_list = null;
	private static SensorManager g_sensorManager = null;
	private static Sensor sensor;
    private static Sensor sensor_orient;
    private static SensorEventListener g_sensorListener = null;    
//    private static final int SHAKE_THRESHOLD = 3000;
    private static long g_acce_old_time = 0;
//    private static long g_acce_last_notify_time = 0;
    private static float old_x, old_y, old_z;
    private static boolean g_b_portrait = true;
    private static YouUtility g_utility = null;
    
    public static int webview_width, webview_height;
    
    public static final int SD_NO_AVAIL_SIZE = 1;
	public static final int FLASH_NO_AVAIL_SIZE = 2;
	public static final int NO_STORE = 0;
	public static final int FULL_SPACE = 3;

	public static String g_ver_string = null;
	public static String g_id_string = null;
	public static final String g_id = "user_id";
	public static final String g_ver = "soft_ver";
	public static final String SKIN_SWITCH = "skin_flag";
    
	private static String formatUrl = null;
	private static String  aes  = null;
	
	public static final String ROOT_FOLDER = Environment
			.getExternalStorageDirectory() + "/100tv";
	
	public static final String CACHE_DOWNLOAD = "video";
	public static final String ONLINE_PICTURE = "picture";
	public static final String DOWNLOAD_APK = "apk";
	
	public static final int CACHE_DOWNLOAD_TYPE = 1;
	public static final int ONLINE_PICTURE_TYPE = 2;
	public static final int DOWNLOAD_APK_TYPE = 3;
	
	
	public static int v(String tag, String msg) {
		LOG.v(YouUtility.TAG, tag, msg);
		return 0;
	}

	public static void setRecommendRightFlag(Context context, boolean flag) {
		if (context != null) {
			SharedPreferences settings = context.getSharedPreferences(
					RECOMMEND_RIGHT, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(RECOMMEND_FIRST, flag);
			editor.commit();
		}
	}

	public static boolean getRecommendRightFlag(Context context){
		boolean res = false;
		if (context != null) {
			SharedPreferences settings = context.getSharedPreferences(
					RECOMMEND_RIGHT, 0);
			res = settings.getBoolean(RECOMMEND_FIRST, false);
		}
		
		return res;
	}
	
	public static int BrightnessGetValue(Window window) {
		int value = 1;
		
		WindowManager.LayoutParams lp = window.getAttributes();
		float window_brightness = lp.screenBrightness;
		if (window_brightness < 0.0f) {
			int screen_bright = 0;
			try {
				screen_bright = Settings.System.getInt(window.getContext()
						.getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS);

			} catch (SettingNotFoundException e) {
				e.printStackTrace();
				YouUtility.v(TAG, "Can't find brightness mode");
			}
			float bright = 10.0f * screen_bright / 255.0f;
			value = (int) bright;
		} else
			value = (int) (window_brightness * 10);

		if (value < 0)
			value = 1;
		return value;
	}

	public static void BrightnessSetValue(Window window, int value) {
		WindowManager.LayoutParams lp = window.getAttributes();
		if( Build.MODEL != null && Build.MODEL.indexOf("MEIZU MX") > -1 ){
			lp.screenBrightness = 0.6f + value*3/100f;
			window.setAttributes(lp);
		}else
		{
			if (value > 10)
				value = 10;
			if (value < 0)
				value = 0;
			float bright = value;
			bright /= 10.0f;
			lp.screenBrightness = bright;
			window.setAttributes(lp);
		}
	}
    
	public static void BrightnessRestore(Window window) {
		if (g_brightness_value != -2) // mode changed. should be back
		{
			Settings.System.putInt(window.getContext().getContentResolver(),
					SCREEN_BRIGHTNESS_MODE, 1);
			YouUtility.v(TAG, "BrightnessRestore to system auto");
			g_brightness_value = -2;
		}
	}

	public static boolean getMuteState(Context context) {
		if (null == context) {
			return false;
		}

		Context app = context.getApplicationContext();
		SharedPreferences settings = app.getSharedPreferences(PREFS_NAME, 0);
		boolean ret = settings.getBoolean(MUTE_STATE, false);
		return ret;
	}
	
	public static void setCipher(Context context, String aes) {
		if (null == context) {
			return;
		}
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("cipher", aes);
		editor.commit();
	}
	
	public static String getCipher(Context context){
		String aes = null;
		if(context != null){
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			aes = settings.getString("cipher","");
		}
		return aes;
	}
	
	public static void setDeclaretion(Context context, String key, String value) {
		if (null == context) {
			return;
		}
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getDeclaretion(Context context,String key){
		String dec = null;
		if(context != null){
			SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
			dec = spf.getString(key,"");
		}
		return dec;
	}
	
	public static void setCancelNotificationTime(Context context){
		if(null == context){
			return;
		}
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("cancel_time", System.currentTimeMillis());
		editor.commit();
	}
	
	public static long getCanelNotificationTime(Context context){
		long time = 0;
		if(context != null){
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			time = settings.getLong("cancel_time", System.currentTimeMillis());
		}
		return time;
	}
	
	public static void setMuteState(Context context, boolean is_Mute) {
		if (context != null) {
			SharedPreferences settings = context.getSharedPreferences(
					PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(MUTE_STATE, is_Mute);
			editor.commit();
		}
	}

	public static void setCurrentVolume(Context context, int currentVolume) {
		if (null == context) {
			return;
		}
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(CURRENT_VOLUME, currentVolume);
		editor.commit();
	}

	public static boolean getNotificationSwitch(Context context, String modeName) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean ret = settings.getBoolean(modeName, true);
		return ret;
	}
	
	public static boolean getDownloadOFFSwitch(Context context, String modeName){
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean ret = settings.getBoolean(modeName, true);
		return ret;
	}

	public static void setSettingsMode(Context context,String modeName,boolean modeValue) {
		if (null == context) {
			return;
		}
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(modeName, modeValue);
		editor.commit();
		LOG.v(TAG, "setSettingsMode "+modeName, modeValue);
	}
	public static int getSubtitileyouSize(Context context){
		if(null == context)return 1;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		int ret = settings.getInt(SUB_TITLE_FONTSIZE, 1);
		LOG.v(TAG, "getSubtitileyouSize", ret);
		return ret;
	}
	public static void setSubtitleFontSize(Context context,int sizeindex){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(SUB_TITLE_FONTSIZE,sizeindex);
		editor.commit();
	}
	public static int getPlayQuality(Context context){
		if(null == context)return 0;
		int ret = You_Core.fn_setting_get_value(You_Core.FN_SETTING_DEFAULT_CLARITY);
		LOG.e(TAG, "getPlayQuality", ret);
		return ret;
	}
	
	public static void setPlayQuality(Context context,int value){
		You_Core.fn_setting_set_value(You_Core.FN_SETTING_DEFAULT_CLARITY,value);
		LOG.v(TAG, "setPlayQuality", value);
	}
	
	
	public static String ConfigureFormatURL(Context context, String url) {// 加密
		if (url == null)
			return "";
		if (null == context) {
			return "";
		}
		StringBuffer output = new StringBuffer(url);
		if (!url.endsWith("&") && !url.endsWith("?")) {
			if (url.contains("?")) {
				output.append("&");
			} else {
				output.append("?");
			}
		}
		output.append("cipher=");
		output.append(YouApplication.cipher);
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		webview_height = displayMetrics.heightPixels;
		webview_width = displayMetrics.widthPixels;
		output.append("&w=" + webview_width);
		output.append("&h=" + webview_height);
		String nt = "";
		int netType = YouPlayerJNIUtil.getNetType();
		switch (netType) {
		case 0:
			nt = "";
			break;
		case 1:
			nt = "wifi";
			break;
		case 2:
			nt = "3g";
			break;
		case 3:
			nt = "gprs";
			break;
		default:
			nt = "unknown";
			break;
		}
		output.append("&nt=" + nt);
		output.append("&fmt=xml");
		formatUrl = output.toString();
		LOG.v(TAG, "formatUrl = ", formatUrl);
		return formatUrl;
	}
	
	public static StringBuffer initRawCipher(Context context) {
		StringBuffer suffix = new StringBuffer();
		suffix.append("cv=" + ConfigureGetVersion(context));
		suffix.append("&imei=" + ConfigureGetIMEI(context));
		suffix.append("&imsi=" + ConfigureGetIMSI(context));
		suffix.append("&uid=" + ConfigureGetID(context));
		suffix.append("&p=" + ConfigureGetPhonenum(context));
		suffix.append("&pfv=android_" + ConfigureGetSDK());
		suffix.append("&macadd=" + ConfigureGetMac());
		return suffix;
	}
	
	public static String ConfigureGetCipher(Context context) {// 加密
		if (null == context) {
			return "";
		}
		if (aes == null) {
			StringBuffer suffix = initRawCipher (context) ;
			YouUtility.v(TAG, "rawCipher : " + suffix.toString());
			// suffix.append("cv=7.1.8.404.8.8001&imei=&imsi=460005524193830&uid=20110506065243828&p=");
			StringBuffer md5_source = new StringBuffer(suffix);
			if (LOG.server_switch) {
				md5_source.append("37297E^7&((1A4C"); // key string. should be changed when released.
			} else {
				md5_source.append("01"); // key string. should be changed when released.
			}
			String md5 = getMD5Str(md5_source.toString());
			suffix.append("&key=" + md5);
			if (LOG.server_switch) {
				aes = encrypt("23E5BBF9&9#02E5B", suffix.toString());
			} else {
				aes = encrypt("abcdefghijklmnop", suffix.toString());
			}
		}
		YouUtility.v(TAG, "cipher=" + aes);
		return aes;
	}
	
	public static String ConfigureGetIniPath(Context context){
		if(context != null)
		{
			File file = context.getFilesDir();
			return file.getAbsolutePath();
		}
		else
		{
			return "/data/data/com.youplayer.player/files";
		}
	}
	
	public static void initStrategyVersion(Context context) {
		String filterPath = ConfigureGetIniPath(context) + "/vs_filter.txt";
		LOG.v("initStrategyVersion", " filterPath ", filterPath);
		File fFilter = new File(filterPath);
		if (!fFilter.exists()) {

			LOG.v("initStrategyVersion", "copy filter", "");
			try {
				AssetManager am = context.getAssets();
				DataInputStream dis = new DataInputStream(am.open("filter_no.txt"));
				byte[] buffer = new byte[dis.available()];
				dis.readFully(buffer);
				OutputStream fos = new FileOutputStream(fFilter);
				fos.write(buffer);
				fos.flush();
				fos.close();
				dis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static StringBuffer initFeeRawCipher(Context context) {
		StringBuffer suffix = new StringBuffer();
		suffix.append("cv=" + ConfigureGetVersion(context));
		suffix.append("&imei=" + ConfigureGetIMEI(context));
		suffix.append("&imsi=" + ConfigureGetIMSI(context));
		suffix.append("&uid=" + ConfigureGetID(context));
		suffix.append("&p=" + ConfigureGetPhonenum(context));
		suffix.append("&pfv=android_" + ConfigureGetSDK());
		return suffix;
	}
	
	public static String ConfigureGetFeeCipher(Context context) {// 加密
		if (null == context) {
			return "";
		}
		String cipher = "";
		StringBuffer suffix = initFeeRawCipher(context);
		YouUtility.v(TAG, "rawFeeCipher : " + suffix.toString());
		StringBuffer md5_source = new StringBuffer(suffix);
		md5_source.append("38297E^7&((1A4C"); // key string. should be changed // when released.
		String md5 = getMD5Str(md5_source.toString());
		suffix.append("&key=" + md5);
		cipher = encrypt("24E5BBF9&9#02E5B", suffix.toString());
		return cipher;
	}
	
	public static String ConfigureFormatFeeURL(Context context, String url) {// 加密
		if (null == context) {
			return "";
		}
		StringBuffer output = new StringBuffer(url);
		output.append("&cipher=");
		output.append(ConfigureGetFeeCipher(context));
		output.append("&pt=0");
		String nt = "";
		int netType = YouPlayerJNIUtil.getNetType();
		switch (netType) {
		case 0:
			nt = "";
			break;
		case 1:
			nt = "wifi";
			break;
		case 2:
			nt = "3g";
			break;
		case 3:
			nt = "gprs";
			break;
		default:
			nt = "unknown";
			break;
		}
		output.append("&nt=" + nt);
		output.append("&fmt=xml");
		
		return output.toString();
	}
	
	public static int ConfigureGetScreenWidth(Context context, boolean portrait)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		if(portrait) {
			return (width < height ? width : height);
		} else {
			return (width > height ? width : height);
		}
	}
	
	public static int ConfigureGetScreenHeight(Context context, boolean portrait)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		if(portrait) {
			return (width > height ? width : height);
		} else {
			return (width < height ? width : height);
		}
	}
	
	public static String readInputStreamAsString(InputStream in) {
		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result;
		try {
			result = bis.read();
			while (result != -1) {
				byte b = (byte) result;
				buf.write(b);
				result = bis.read();
			}
		} catch (IOException e) {
			YouUtility.v(TAG, e.getMessage());
		}

		return buf.toString();
	}

	public static String ConfigureGetVersion(Context context) {
		String versionName = "10.6.7.1307.9000.9000001";
		if (null == context) {
			return versionName;
		}
		if (g_ver_string != null && g_ver_string.length() > 0){
			return g_ver_string;
		}
		// 拷贝文件出来后的存放路径
		String pkgPath = ((ContextWrapper) context).getPackageCodePath(); // 取到当前应用包路径
		File f = new File(pkgPath);
		if (f.isFile()) {
			if (f.canRead()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					int total = fis.available();
					// 将读取的文件写出
					byte[] bytes = new byte[100];
					// int byteed = 0;
					fis.skip(total - 100);
					fis.read(bytes);
					fis.close();
					if ((bytes[0] == 'Z' || bytes[0] == 'z')
							&& (bytes[1] == 'Z' || bytes[1] == 'z')
							&& (bytes[2] == 'Z' || bytes[2] == 'z')
							&& (bytes[3] == 'Z' || bytes[3] == 'z')) {
						if (bytes[4] != 0x00) {
							int len = 0;
							for (len = 0; len < 96; len++)
								if (bytes[4 + len] == 0x00)
									break;
							String ver = new String(bytes, 4, len);
							YouUtility.v(TAG, ver);
							g_ver_string = ver;
							return ver;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			AssetManager am = context.getAssets();
			DataInputStream dis = new DataInputStream(am.open("ver.txt"));
			byte[] buffer = new byte[dis.available()];
			dis.readFully(buffer);
			versionName = new String(buffer, "utf-8");
			dis.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		YouUtility.v("from assets ver. version = ", versionName);
		g_ver_string = versionName;
		return versionName;
	}

	public static String ConfigureGetIMEI(Context context) {
		if (null == context) {
			return "";
		}
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (imei == null)
			imei = "";
		return imei;
	}

	public static String ConfigureGetIMSI(Context context) {
		if (null == context) {
			return "";
		}
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi == null)
			imsi = "";
		return imsi;
	}

	public static String ConfigureGetID(Context context) {
		if (null == context) {
			return "";
		}
		if (g_id_string != null && g_id_string.length() > 0) {
			v("ConfigureGetID uid=", g_id_string);
			return g_id_string;
		}
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		String str_id = settings.getString(g_id, "");
		if (str_id != null && str_id.length() > 0) {
			g_id_string = str_id;
			v("ConfigureGetID uid=", g_id_string);
			try {
				String idPath = Environment.getExternalStorageDirectory()
						+ "/id_num.txt";
				File outputFile = new File(idPath);
				if (!outputFile.exists()) {
					OutputStream fos = new FileOutputStream(outputFile);
					byte[] bt = g_id_string.getBytes();
					fos.write(bt);
					fos.flush();
					fos.close();
				}
			} catch (Exception e) {
			}
			v("ConfigureGetID save uid to file =", g_id_string);
			return g_id_string;
		}
		String idPath = Environment.getExternalStorageDirectory()
				+ "/id_num.txt";
		File f = new File(idPath);
		if (f.isFile()) {
			if (f.canRead()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					byte[] bytes = new byte[100];
					int len = fis.read(bytes);
					fis.close();
					LOG.v(TAG, "uid len = ", "" + len);
					if (len == 17) {
						g_id_string = new String(bytes, 0, len);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(g_id, g_id_string);
						editor.commit();
						v("ConfigureGetID uid=", g_id_string);
						return g_id_string;
					}else{
						LOG.e(TAG, "ConfigureGetID", "incorrect userid is read from id_number.txt.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		int flag = (int) (Math.random() * 10);
		Time time = new Time();
		time.setToNow();
		// the end char of UID: 0 is 100tv; 1 is MovieNotifier
		long ms = System.currentTimeMillis();
		ms = ms % 1000;
		str_id = String.format(
				"%1$02d%2$02d%3$02d%4$02d%5$02d%6$02d%7$03d%8$01d0",
				time.year % 100, time.month + 1, time.monthDay, time.hour,
				time.minute, time.second, ms, flag % 10);

		g_id_string = str_id;
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(g_id, g_id_string);
		editor.commit();
		try {
			File outputFile = new File(idPath);
			OutputStream fos = new FileOutputStream(outputFile);
			byte[] bt = g_id_string.getBytes();
			fos.write(bt);
			fos.flush();
			fos.close();
		} catch (Exception e) {
		}
		v("new ConfigureGetID uid=", g_id_string);
		return str_id;
	}

	/*
	 * NOTE: the length of UUID is 32 bytes.
	 */
	public static String ConfigureGetUUID(Context context) {
		String defUUID = "201234567890";
		if (null == context) {
			return defUUID; // default value.
		}
		SharedPreferences settings = context.getSharedPreferences("userdict", 0);
		String str_id = settings.getString("userid", "");
		
		if(str_id != null && str_id.length() > 0)
			return str_id;
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(tm != null)
		{
			String imei = tm.getDeviceId();
			if (imei == null)
				imei = "imei1234567890";
			
			String imsi = tm.getSubscriberId();
			if(imsi == null)
				imsi = "imsi1234567890";
			
			// android ID is not safe. if ROM changed, android ID may be changed.
			String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			if(androidId == null)
				androidId = "androidid1234567890";
			
			String src = imei + imsi;
			
			MessageDigest messageDigest = null;

			try {
				messageDigest = MessageDigest.getInstance("MD5");

				messageDigest.reset();

				messageDigest.update(src.getBytes("UTF-8"));
			} catch (NoSuchAlgorithmException e) {
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			byte[] byteArray = messageDigest.digest();

			StringBuffer md5StrBuff = new StringBuffer();

			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
					md5StrBuff.append("0").append(
							Integer.toHexString(0xFF & byteArray[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
			String des = md5StrBuff.toString();
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("userid", des);
			editor.commit();

			return des;
		}
		
		return defUUID;
	}
	
	public static String ConfigureGetPhonenum(Context context) {
		if (null == context) {
			return "";
		}
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getLine1Number();
		if (imsi == null)
			imsi = "";
		return imsi;
	}
	public static int ConfigureGetSDK() {
		int verion = android.os.Build.VERSION.SDK_INT;
		return verion;
	}

	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return md5StrBuff.toString();
	}

	public static String ConfigureGetMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	public static String encrypt(String seed, String cleartext) {

		return "";
	}

	public static int getPackageVersionNum(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		int versionNum = 0;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionNum = pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionNum;
	}
	 
	public static String getPackageVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		String versionName = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	 
	public static int getDrawableIdByName(Context context, String name) {
		int id;
		if (null == name) {
			id = 0;
		} else {
			id = context.getResources().getIdentifier(name, "drawable",
					context.getPackageName());
		}
		return id;
	}

	public static String getClearDefaultFlag(Context context) {
		SharedPreferences sp = context.getSharedPreferences("you_notification", Activity.MODE_PRIVATE);
		String flag = sp.getString(CLEAR_AUTO_FLAG, "");
		if (flag == null || flag.length() == 0) {
			sp.edit().putString(CLEAR_AUTO_FLAG, context.getString(R.string.notification_clear_default_switch))
					.commit();
			flag = context.getString(R.string.notification_clear_default_switch);
		}
		return flag;
	}
	 
	 public static void resetDefaultSetting(Context context, String classname) {
			if (null == context) {
				return;
			}
			LOG.v(TAG, " resetDefaultSetting classname ", classname);
			String className = classname;// getClass().getPackage().getName();

			List<IntentFilter> intentList = new ArrayList<IntentFilter>();
			List<ComponentName> cnList = new ArrayList<ComponentName>();
			PackageManager localPackageManager = context.getPackageManager();
			localPackageManager.getPreferredActivities(intentList, cnList, null);
			IntentFilter dhIF;
			LOG.v(TAG, " resetDefaultSetting intentList.size():", intentList.size());
			for (int i = 0; i < intentList.size(); i++) {
				dhIF = intentList.get(i);
				String apptype = null;
				String appaction = null;
				String appcategory = null;
				int ifschemeCount = dhIF.countDataSchemes();
				int iftypeCount = dhIF.countDataTypes();
				String appname = cnList.get(i).getPackageName();
				LOG.v(TAG, " resetDefaultSetting cnList.get(i).getPackageName(): ", cnList.get(i).getPackageName());
				if (ifschemeCount > 0 && !appname.equals(className)) {
					String dataScheme = dhIF.getDataScheme(0);
					appaction = dhIF.getAction(0);
					appcategory = dhIF.getCategory(0);

					Intent intent = new Intent(appaction);
					intent.addCategory(appcategory);
					Uri data = null;
					if ("rtsp".equals(dataScheme))
						data = Uri.parse("rtsp://");
					else if ("http".equals(dataScheme))
						data = Uri.parse("http://");
					if (data != null)
						intent.setData(data);
					replaceSet(context, className, intent);
				}
				if (iftypeCount > 0 && null != dhIF.getDataType(0)) {
					apptype = dhIF.getDataType(0);
					appaction = dhIF.getAction(0);
					appcategory = dhIF.getCategory(0);
					Intent intent = new Intent(appaction);
					intent.addCategory(appcategory);
					LOG.v(TAG, ">>>>apptype: ", apptype);
					String apptypeSub=null;
					if(apptype.length()>=5){
					   apptypeSub = apptype.substring(0, 5);
					}else{
					   return;
					}
					LOG.v(TAG, " resetDefaultSetting apptypeSub ", apptypeSub);
					LOG.v(TAG, " resetDefaultSetting appname ", appname);
					if ((apptypeSub.equals("audio") || apptypeSub.equals("video"))
							&& !appname.equals(className)) {
						YouPlayerConstant.isClearDefaultSetting = true;
						LOG.v(TAG, " resetDefaultSetting ", " end  ");
					}
					if ((apptypeSub.equals("audio") || apptypeSub.equals("video"))
							&& !appname.equals(className)) {
						if (ifschemeCount >= 0) {
							if (apptype.equals("audio")	|| apptype.equals("video/*") || apptype.equals("")) {
								intent.setType(apptype.substring(0, 5) + "/*");
							} else {
								intent.setType(apptype);
							}
							replaceSet(context, className, intent);

						}

					}
					if ((apptypeSub.equals("audio") || apptypeSub.equals("video"))
							&& !appname.equals(className)) {
						if (ifschemeCount >= 0) {

							Uri data = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()));
							intent.setDataAndType(data, apptype);
						}
						replaceSet(context, className, intent);

					}
					if ((apptypeSub.equals("audio") || apptypeSub.equals("video"))
							&& !appname.equals(className)) {
						if (ifschemeCount >= 0) {
							intent.setDataAndType(null, "video/*");
						}
						// replaceSet(context, className, intent);

					}
				}
			}
		}
	 
	public static synchronized void replaceSet(Context context, String names, Intent intent) {
		if (null == context) {
			return;
		}
		LOG.v(TAG, "replaceSet", "replaceSet");
		PackageManager localPackageManager = context.getPackageManager();
		String strDefault = "";

		LOG.v(TAG, "strDefault : ", strDefault);
		ComponentName localComponentName = new ComponentName("com.youplayer.player",strDefault);
		localPackageManager.setComponentEnabledSetting(localComponentName, 1, 1);
		localPackageManager.resolveActivity(intent, 0);
		localPackageManager.setComponentEnabledSetting(localComponentName, 2, 1);
	}
	 
	public static String getCurrentTime(long currentTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(currentTime);
		String currentDate = formatter.format(curDate);
		return currentDate;
	}
	
	public static byte[] read(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}
	
	public static String getCurrentTime(){
		
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDate.format(new Date());
	}
	public static String getCurrentTime(String pattern){
		
		SimpleDateFormat simpleDate = new SimpleDateFormat(pattern);
		return simpleDate.format(new Date());
	}
  
   public static void setStartServiceFlag(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(
				"you_notification", Activity.MODE_PRIVATE);
		sp.edit().putString(AUTO_STATE_FLAG, flag)
				.commit();
	}
   
   public static void resetStartServiceFlag(Context context){
	   SharedPreferences sp = context.getSharedPreferences(
				"you_notification", Activity.MODE_PRIVATE);
	   sp.edit().putString(AUTO_STATE_FLAG, "1").commit();
   }
	
	public static String getStartServiceFlag(Context context){
		SharedPreferences sp = context.getSharedPreferences(
				"you_notification", Activity.MODE_PRIVATE);
		String flag = sp.getString(AUTO_STATE_FLAG, "");
		if (flag == null || flag.length() == 0) {
			 sp.edit().putString(AUTO_STATE_FLAG, context
					.getString(R.string.notification_service_switch)).commit();
			 flag = context.getString(R.string.notification_service_switch);
		}
		return flag;
	}
	
	public static void setClearDefualtFlag(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(
				"you_notification", Activity.MODE_PRIVATE);
		sp.edit().putString(CLEAR_AUTO_FLAG, flag)
				.commit();
	}
	
	public static String replaceUrlHost(Context context, String rptUrl, String host){
		StringBuffer sbUrl = new StringBuffer();
		LOG.v(TAG, "new replaceUrlHost", "rptUrl :" + rptUrl+" host :"+host);
		if (rptUrl.contains("[host]")) {
			String url = rptUrl.substring(rptUrl.lastIndexOf("]") + 1,
					rptUrl.length());
			sbUrl.append(host).append(url);
		}else{
			sbUrl.append(rptUrl);
		}
		return sbUrl.toString();
	}

	public static boolean isServiceStarted(Context context) {
		boolean isRunning = false;
		return isRunning;
	}
	
	 @SuppressWarnings("unchecked")
    public static HashMap<String, ArrayList<HashMap>> adsListToHashMap(You_full_screen_player_data_to_ui.Cls_fn_ad_data_t adsList, Context context) {
        if (null == context || adsList == null || adsList.items == null ) {
            return null;
        }
        
        HashMap<String, ArrayList<HashMap>> adsHashMap = new HashMap<String, ArrayList<HashMap>>();
        
        ArrayList arrayListPre = new ArrayList();
        ArrayList arrayListEnd = new ArrayList();
        ArrayList arrayListPause = new ArrayList();
        ArrayList arrayListBuffering = new ArrayList();
        ArrayList arrayListPlayingSub = new ArrayList();
        ArrayList arrayListPlayingLogo = new ArrayList();

        HashMap playingMap = new HashMap();
        HashMap sonHashMap = null;
        HashMap textparamHashMap = null;
        
       DisplayMetrics dm = new DisplayMetrics();
       (YouExplorer.instance).getWindowManager().getDefaultDisplay().getMetrics(dm);
       int width = dm.widthPixels;
       int height = dm.heightPixels;
       if(width  < height){
          width = dm.heightPixels;
          height = dm.widthPixels;
       }
        for(int i = 0; i < adsList.items.length; i++){
            if(adsList.items[i] == null){
                break;
            }
            sonHashMap = new HashMap();
            textparamHashMap = new HashMap();
            switch(adsList.items[i].adtype){
            default:
                break;
            }
        }
        
        return playingMap;
	 }

	 
	 public static void toShareToSystem(Context context,String imagepath,String url, String shareContent){
		 LOG.v("youUtility", "toShareToSystem", "imagepath:"+imagepath);   
         Intent intent=new Intent(Intent.ACTION_SEND);
         intent.setType("text/plain");
         if( imagepath != null ){
             
             File f = new File(imagepath);        
             if( f.exists() ){
                 intent.setType("image/png");
                 Uri u = Uri.fromFile(f);
                 intent.putExtra(Intent.EXTRA_STREAM, u);
             }
         }
          intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getText(R.string.share_blog_title));
          if( url == null ) url = "";
          String shareText = context.getResources().getText(R.string.share_text_start).toString();
          shareText += (shareContent == null ? "" : shareContent); 
          intent.putExtra(Intent.EXTRA_TEXT, shareText +"\n"+ url);
          intent.putExtra("sms_body", shareText +"\n"+ url);
          context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.share_blog_title)));
   } 
	 
	 
	 public static void saveWeiboBitmap(Bitmap bitmap,String filename)  {
	        String status = Environment.getExternalStorageState();
	        if (bitmap == null || !status.equals(Environment.MEDIA_MOUNTED)) {
	            return ;
	        }
	        File f = new File(filename);
	        FileOutputStream fOut = null;
	        boolean error = false;
	        try {
	            f.createNewFile();
	        } catch (IOException e1) {
	            e1.printStackTrace();
	            error = true;
	        }
	        
	        try {
	                fOut = new FileOutputStream(f);
	        } catch (FileNotFoundException e) {
	                e.printStackTrace();
	                error = true;
	        } 
	        
	        try {
	             bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	        } catch (Exception e) {
	            e.printStackTrace();
	            error = true;
	        }
	       
	        try {
	                fOut.flush();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	        try {
	                fOut.close();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	        
	    }
	 
	private static String PALY_PATH = "/data/data/"
			+ YouApplication.GetGlobalContext().getString(
					R.string.package_name) + "/bcache/";

	public static String getLastFrameBitmap(String url)  {
		try {
			Bitmap bitmap = null;
			bitmap = Player_UIManager.getBitmap();
			bitmap = Bitmap.createScaledBitmap(bitmap, 138, 93, false);
			File dir = new File(PALY_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String imagePath = PALY_PATH + getMD5Str(url);
			File bitmapPath = new File(imagePath);
			
			FileOutputStream fOut = null;
			bitmapPath.createNewFile();
			
			fOut = new FileOutputStream(bitmapPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 75, fOut);
			
			fOut.flush();
			fOut.close();
			LOG.v("youUtility", "getLastFrameBitmap", "imagepath:"+imagePath);   
			return imagePath;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String getBitmapFromUrl(String url, int pos)  {
		try {
			Bitmap bitmap = null;
			Player_UIManager.fone_media_thumbnail_init(138, 93);
			YouPlayerBitMap tag = (YouPlayerBitMap) Player_UIManager
						.fone_media_player_get_thumbnail_from_video(url, pos, 16, 138, 93);
			bitmap = (Bitmap) tag.m_bitmap;
			File dir = new File(PALY_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String imagePath = PALY_PATH + getMD5Str(url);
			File bitmapPath = new File(imagePath);

			FileOutputStream fOut = null;
			bitmapPath.createNewFile();

			fOut = new FileOutputStream(bitmapPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 75, fOut);

			fOut.flush();
			fOut.close();
			LOG.v("youUtility", "getBitmapFromUrl", "imagepath:"+imagePath);   
			return imagePath;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void cleanBitmapCache() {
		File dir = new File(PALY_PATH);
		RecursionDeleteFile(dir);
	}

	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param fromFilePath
	 *            源文件路径
	 * @param toFilePath
	 *            目标文件路径
	 */
	public static void copyFile(String fromFilePath, String toFilePath) {
		
		String status = Environment.getExternalStorageState();
		if(!status.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File toFile = new File(toFilePath);
		if (toFile.exists()) {
			toFile.delete();
		}
		
		if(fromFilePath == null) {
			return;
		}
		
		File fromFile = new File(fromFilePath);
		if (!fromFile.exists()) {
			return;
		}

		if (!fromFile.isFile()) {
			return;
		}

		if (!fromFile.canRead()) {
			return;
		}

		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}

		try {

			FileInputStream fosfrom = new FileInputStream(fromFile);

			FileOutputStream fosto = new FileOutputStream(toFile);

			byte bt[] = new byte[1024];

			int c;

			while ((c = fosfrom.read(bt)) > 0) {

				fosto.write(bt, 0, c); // 将内容写到新文件当中

			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {
			LOG.e(TAG,"copyFile", ex.getMessage());
		}
	}

	public static void copyFile(BitmapDrawable fromDrawable, String toFilePath) {

		String status = Environment.getExternalStorageState();
		if(!status.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File toFile = new File(toFilePath);
		if (toFile.exists()) {
			toFile.delete();
		}
		
		if(fromDrawable == null) {
			return;
		}

		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		FileOutputStream out = null;
		try {
			Bitmap bmp = fromDrawable.getBitmap();
			out = new FileOutputStream(toFile);
			
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out); 
			
		} catch (Exception ex) {
			LOG.e(TAG,"copyFile", ex.getMessage());
			ex.printStackTrace();
			
		} finally {
			try {
				if(out != null) {
					out.flush();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				LOG.e(TAG,"copyFile", e.getMessage());
			}
			try {
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				LOG.e(TAG,"copyFile", e.getMessage());
			}
			out = null;
		}
	}

	public static void switchLinkType(Context context, Intent intent,
			String weibourl, String ourl, String url, Boolean showPlayButton,
			String definition, String pic, String linkType, String name,
			String desc) {
		   switch (Integer.parseInt(linkType)) {
		    case 0: // fullplayer
		    	LOG.v(TAG,"switchLinkType","fullplayer");
		    	intent.setAction(YouPlayerConstant.YOU_ACTION_FULLPLAY);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_URL,url);
		    	//skipIntent.putExtra(Constant.ACTION_PAGE, fn_Core.FN_PAGE_FULL_SCREEN_PLAYER);
		    	break;
		    case 3:  //open webview
		    	LOG.v(TAG,"switchLinkType","open webview");
		    	 intent.setAction("android.intent.action.VIEW");
		    	 Uri content_url1 = Uri.parse(url);   
		 		 intent.setData(content_url1);
		 		 if (android.os.Build.VERSION.SDK_INT < 16) { 
		 			 intent.setClassName("com.android.browser","com.android.browser.BrowserActivity"); 
		 		 } 
		    	 break;

		    case 8: // original web
		    	LOG.v(TAG,"switchLinkType","original web");
		    	intent.setAction(YouPlayerConstant.YOU_ACTION_NOTIFICATION);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_PAGE, You_Core.FN_PAGE_ONLINE_WEB_DETAIL);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_WEIBOURL,weibourl);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_OURL,ourl);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_URL,url);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_BTNPLY,showPlayButton);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_DEFINITION,definition);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_PIC,pic);
		    	intent.putExtra(YouPlayerConstant.YOU_ACTION_NAME,name);
		    	break;	 
		   default:
			  break;
		}
	   }

	 public static boolean getPosterStatus(Context context){
		 SharedPreferences sp = context.getSharedPreferences("recommend_poster", Activity.MODE_PRIVATE);
		 boolean ret = sp.getBoolean("poster_status", true);		 
		 return ret;
	 }
	 public static void setPosterStatus(Context context,boolean status){
		 SharedPreferences sp = context.getSharedPreferences("recommend_poster", Activity.MODE_PRIVATE);
		 SharedPreferences.Editor tor = sp.edit();
		 tor.putBoolean("poster_status", status);
		 tor.commit();
	 }
	 
	 public static boolean getPosterHelpStatus(Context context){
		 SharedPreferences sp = context.getSharedPreferences("poster_help", Activity.MODE_PRIVATE);
		 boolean ret = sp.getBoolean("poster_help", false);
		 return ret;
	 }
	 
	 public static void setPosterHelpStatus(Context context,boolean status){
		 SharedPreferences sp = context.getSharedPreferences("poster_help", Activity.MODE_PRIVATE);
		 SharedPreferences.Editor tor = sp.edit();
		 tor.putBoolean("poster_help", status);
		 tor.commit();
	 }

	 public static boolean getWebviewHelpStatus(Context context){
		 SharedPreferences sp = context.getSharedPreferences("webview_help", Activity.MODE_PRIVATE);
		 boolean ret = sp.getBoolean("webview_help", false);
		 return ret;
	 }
	 
	 public static void setWebviewHelpStatus(Context context,boolean status){
		 SharedPreferences sp = context.getSharedPreferences("webview_help", Activity.MODE_PRIVATE);
		 SharedPreferences.Editor tor = sp.edit();
		 tor.putBoolean("webview_help", status);
		 tor.commit();
	 }
	 
    public void addObserver(final Context context, YouPlayerSensorObserver ob) {
    	YouUtility.v(TAG, "addObserver beg");
        if (g_sensor_observer_list == null)
            g_sensor_observer_list = new ArrayList<YouPlayerSensorObserver>();
        if (!g_sensor_observer_list.contains(ob)) {
            g_sensor_observer_list.add(ob);

            if (g_sensorManager == null) {
                g_sensorManager = (SensorManager) context
                        .getSystemService(Context.SENSOR_SERVICE);
                sensor = g_sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensor_orient = g_sensorManager
                        .getDefaultSensor(Sensor.TYPE_ORIENTATION);

                g_sensorListener = new SensorEventListener() {
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                    public void onSensorChanged(SensorEvent eventSensor) {
                        float[] values = eventSensor.values;
                        if (eventSensor.sensor == sensor) {
                            long curTime = System.currentTimeMillis();
                            {
                                long difTime = curTime - g_acce_old_time;
                                boolean bFirst = false;
                                if (curTime == g_acce_old_time)
                                    bFirst = true;
                                g_acce_old_time = curTime;
                                float x, y, z;
                                x = values[SensorManager.DATA_X];
                                y = values[SensorManager.DATA_Y];
                                z = values[SensorManager.DATA_Z];
                                float speed;
                                if (difTime < 20)
                                    difTime = 20;
                                speed = (Math.abs(x - old_x) + Math.abs(y
                                        - old_y)/* + Math.abs(z - old_z) */)
                                        / difTime * 10000;
                                if (old_x == 0.0 && old_y == 0 && old_z == 0)
                                    bFirst = true;
                                String model = YouUtility.ConfigureGetManufactory();
                                model = model.toLowerCase();
                                if(model.equals("u8800")||model.equals("u8860")){
                                    speed = speed * 2;
                                }

                                old_x = x;
                                old_y = y;
                                old_z = z;
                            }
                        }

                        else if (eventSensor.sensor == sensor_orient) {
                            float/* x, */y, z;
                            // x = values[SensorManager.DATA_X];
                            y = values[SensorManager.DATA_Y];
                            z = values[SensorManager.DATA_Z];

                            float y_value = values[1];
                            float z_value = values[2];
                            y_value = Math.abs(y);
                            z_value = Math.abs(z);

                            boolean portrait = true;
                            if (y_value > 35 && z_value > 30) {
                                portrait = false;
                            }
                            if (g_b_portrait != portrait) {
                                g_b_portrait = portrait;
                                if (!g_b_portrait) {
                                    HashMap<String, Object> event = new HashMap<String, Object>();
                                    event.put("orientation", y_value);
                                    event.put("zvalue", z_value);
                                    event.put("type", "landscape");
                                    g_utility.notifyObservers(event);
                                } else
                                	YouUtility.v(TAG,
                                            "user change device to portrait");
                            }
                        }
                    }
                };
            }
            g_sensorManager.registerListener(g_sensorListener, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
            g_sensorManager.registerListener(g_sensorListener, sensor_orient,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        YouUtility.v(TAG, "addObserver end");
    }

    public void removeObserver(YouPlayerSensorObserver ob) {
    	YouUtility.v(TAG, "removeObserver beg");
        if (g_sensor_observer_list != null && ob != null) {
            if (g_sensor_observer_list.contains(ob)) {
            	g_sensor_observer_list.remove(ob);
            }
            if (g_sensor_observer_list.isEmpty()) {
                g_sensorManager.unregisterListener(g_sensorListener, sensor);
                g_sensorManager.unregisterListener(g_sensorListener, sensor_orient);
                g_b_portrait = true;
                old_x = 0;
                old_y = 0;
                old_z = 0;
            }
        }
        YouUtility.v(TAG, "removeObserver end");
    }

    public void notifyObservers(HashMap<String, Object> event) {
    	YouUtility.v(TAG, "notifyObservers");
        if (g_sensor_observer_list != null) {
            for (int i = 0; i < g_sensor_observer_list.size(); i++) {
            	YouPlayerSensorObserver ob = g_sensor_observer_list.get(i);
                ob.xyz_updated(event);
            }
        } else {
        	YouUtility.v(TAG, "nobody here");
        }
    }
    
    public static String ConfigureGetManufactory() {
        String imei = android.os.Build.MODEL;
        if (imei == null) {
        	imei = "";
        }
        return imei;
    }
   
    public static YouUtility GetUtility() {
        if (g_utility == null) {
            try {
            	g_utility = new YouUtility();
            } catch (SQLiteException e) {
            	YouUtility.v(TAG, e.getMessage());
            }
        }
        return g_utility;
    } 
    
	public static boolean AppGetFirstRunFlag(Context context) {
		if (null == context) {
			return true;
		}

		int versionNum = getPackageVersionNum(context);
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		boolean bLater = settings.getBoolean(APP_IS_FIRST + versionNum, true);
		if (!bLater) {
			return false;
		}
		return true;
	}
	
	public static void AppSetFirstRunFlag(Context context) {
		int versionNum = getPackageVersionNum(context);
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(APP_IS_FIRST + versionNum, false);
		editor.commit();
	}
   
    static Handler mHandler = new Handler(YouApplication.GetGlobalContext().getMainLooper()){
    	public void handleMessage(android.os.Message msg) {
    	     switch (msg.what) {
			case 1:
				Toast.makeText(YouApplication.GetGlobalContext(), R.string.olc_sd_no_fullspace, Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(YouApplication.GetGlobalContext(), R.string.download_no_sd, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
    	};
    };

	public static int getStoreSurplusSpace(Context context, long AVAIL_SIZE) {
		long blockSize = 0;
		long availCount = 0;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) { // sd card
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			blockSize = sf.getBlockSize();
			availCount = sf.getAvailableBlocks();
			if (blockSize * availCount < AVAIL_SIZE) {
				Message msg = mHandler.obtainMessage(1);
				msg.sendToTarget();
				return SD_NO_AVAIL_SIZE;
			}
		} else {
			Message msg = mHandler.obtainMessage(2);
			msg.sendToTarget();
			return NO_STORE;
		}
		return FULL_SPACE;
	}
   
   public static int getStoreSurplusSpace(Context context){
	   return YouUtility.getStoreSurplusSpace(context,200*1024*1024);
   }

   public static String getTimeType(Context context){
	   ContentResolver  cr = context.getContentResolver();
	   String timeType = android.provider.Settings.System.getString(cr, android.provider.Settings.System.TIME_12_24);
	   return timeType;
   }
   
   public static boolean PosterToastFlag(Context context){
	   SharedPreferences sp = context.getSharedPreferences("poster_toast", Activity.MODE_PRIVATE);
	   return sp.getBoolean("toast_flag", false);
   }
   
   public static void setPosterToastFlag(Context context,boolean flag){
	   SharedPreferences sp = context.getSharedPreferences("poster_toast", Activity.MODE_PRIVATE);
	   SharedPreferences.Editor tor = sp.edit();
	   tor.putBoolean("toast_flag", flag);
	   tor.commit();
   }
   
   public static void presetDownloadAd(Context context, Object core_data){
       try {
           v("cache ad", "presetDownloadAd");
           if(core_data == null){
               v("cache ad", "core_data is null");
               return;
           }

           HashMap map = adsListToHashMap((You_full_screen_player_data_to_ui.Cls_fn_ad_data_t)core_data , context);
           if(map == null){
               return;
           }
           int screen_width = 0, screen_height = 0;
           if(YouExplorer.instance != null)
           {
               DisplayMetrics dm = new DisplayMetrics();
               YouExplorer.instance.getWindowManager().getDefaultDisplay().getMetrics(dm);
               screen_width = dm.heightPixels;
               screen_height =dm.widthPixels;
               
               if(screen_width < screen_height)
               {
                   int temp = screen_width;
                   screen_width = screen_height;
                   screen_height = temp;
               }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
   }

	public static String getyouCacheFolder(int type) {
		String status = Environment.getExternalStorageState();
		LOG.v(TAG, "getyouCacheFolder status:",status); 
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			String path = null;
			switch (type) {
			case CACHE_DOWNLOAD_TYPE:
				path = ROOT_FOLDER + File.separator + CACHE_DOWNLOAD;
				break;
            case ONLINE_PICTURE_TYPE:
            	path = ROOT_FOLDER + File.separator + ONLINE_PICTURE;
				break;
            case DOWNLOAD_APK_TYPE:
            	path = ROOT_FOLDER + File.separator + DOWNLOAD_APK;
            	break;
			default:
				break;
			}
			return path;
		}
		
		File file = new File(ROOT_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
        
		File childFile =  null;
		switch (type) {
		case CACHE_DOWNLOAD_TYPE:
            childFile = new File(ROOT_FOLDER + File.separator + CACHE_DOWNLOAD);
			if(!childFile.exists()){
				childFile.mkdirs();
			}
            break;
		case ONLINE_PICTURE_TYPE:
			childFile = new File(ROOT_FOLDER + File.separator + ONLINE_PICTURE);
			if(!childFile.exists()){
				childFile.mkdirs();
			}
			break;
		case DOWNLOAD_APK_TYPE:
			childFile = new File(ROOT_FOLDER + File.separator + DOWNLOAD_APK);
			if(!childFile.exists()){
				childFile.mkdirs();
			}
			break;
		default:
			break;
		}
		
		if(childFile == null){
			return null;
		}

		return childFile.getPath();
	}
   
	public static String getLocalIpAddress(Context context) {
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface intf = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = intf.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress local = ips.nextElement();
					if (!local.isLoopbackAddress() && !local.isLinkLocalAddress()) {
						String ip = local.toString();
						if (ip.startsWith("/"))
							return ip.substring(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." +
		((i >> 8) & 0xFF) + "." +
		((i >> 16) & 0xFF) + "." +
		(i >> 24 & 0xFF);
	}
	
	public static String getModel() {
           String model = android.os.Build.MODEL;;
		return model;
	}

}
