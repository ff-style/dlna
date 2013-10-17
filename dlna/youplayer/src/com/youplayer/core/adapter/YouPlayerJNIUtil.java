package com.youplayer.core.adapter;

import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;


import com.youplayer.core.You_Core;
import com.youplayer.core.struct.You_full_screen_cached_player_item;


import com.youplayer.player.YouApplication;
import com.youplayer.player.YouExplorer;

import com.youplayer.util.YouUtility;
import com.youplayer.util.LOG;

public class YouPlayerJNIUtil {
	private static final String TAG = "JNIUtil";
	private static HashMap<String, YouPlayerJNICallBack> map = new HashMap<String, YouPlayerJNICallBack>(); 
	
	public static void getBitmapWithUrl(String url,YouPlayerJNICallBack call){		
		map.put(url, call);		
		You_Core.fn_get_image_with_url(url);
	}
	
	public static void callback_proxy(Object url,Object path){
		YouPlayerJNICallBack cb = map.get(url);
		if(cb != null)
		{
			cb.callback(url, path);
			map.remove(url);
		}
	}
	
	public static int getScreenWidth()
	{
		Context context = YouApplication.GetGlobalContext();
		if(context != null)
		{
			return YouUtility.ConfigureGetScreenWidth(context, true);
		}
		else
		{
			LOG.v(TAG, "getScreenWidth","getCipher context is null. who did it?");
			return 480;
		}
	}
	
	public static int getScreenHeight()
	{
		Context context = YouApplication.GetGlobalContext();
		if(context != null)
		{
			return YouUtility.ConfigureGetScreenHeight(context, true);
		}
		else
		{
			LOG.v("JNIUtil","getScreenHeight", "getCipher context is null. who did it?");
			return 800;
		}
	}
	
	public static void show_msg_box_str(String tips)
	{
		YouExplorer.showToast(tips, Toast.LENGTH_LONG);
	}
	
	public static int getNetType(){
		Context context = YouApplication.GetGlobalContext();
		if (null == context) {
			return 0;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);//获取系统的连接服务  
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情况  
		if(null != activeNetInfo) {
			LOG.v(TAG, "getNetType : " , activeNetInfo.toString());
			if(activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI){  
				//判断WIFI网  
				return 1;
			}else if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {  
				TelephonyManager mTelephonyManager = (TelephonyManager) YouApplication.GetGlobalContext().getSystemService(Context.TELEPHONY_SERVICE);
				int type = mTelephonyManager.getNetworkType();
				if(type == TelephonyManager.NETWORK_TYPE_UNKNOWN || type == TelephonyManager.NETWORK_TYPE_GPRS || type == TelephonyManager.NETWORK_TYPE_EDGE) {
					//判断gprs网
					return 3;
				} else {
					//判断3g网
					return 2;
				}
			}
		}
		return 0;
	}
		
	public static String getMessageByFlag(int flag){
		LOG.v(TAG, "getMessageByFlag", "flag:"+flag);
		StringBuffer sb = new StringBuffer();
		if (flag == 0){
			sb.append(YouUtility.getLocalIpAddress(YouApplication.GetGlobalContext()));
			sb.append(",");
			sb.append(YouUtility.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
			return sb.toString();
		} else {
			sb.append(YouUtility.getModel());
			sb.append(",");
			sb.append(YouUtility.ConfigureGetVersion(YouApplication.GetGlobalContext()));
			sb.append(",");
			sb.append(YouUtility.ConfigureGetID(YouApplication.GetGlobalContext()));
			return sb.toString();
		}
	}
	

	public static You_full_screen_cached_player_item you_player_history_get_item_by_local_cache_data(String url) {
		LOG.v(TAG, "you_player_history_get_item_by_local_cache_data", "url="+url);
				return null;
	}
	

	public static You_full_screen_cached_player_item you_player_get_item_by_local_cache_data(
			int index) {
				return null;
	}
	

	public static void you_player_update_local_cache_data(int index,
			int play_time, int isCachePlay) {

	}
	
	public static int you_player_get_play_time_by_url(String url){
		LOG.v(TAG, "you_player_get_play_time_by_url", " URL>> : " + url);

		return 0;
	}
	
	public static String getProxyInfo() {
		ConnectivityManager conMan = (ConnectivityManager) YouApplication.GetGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conMan.getActiveNetworkInfo();
		if(info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			String host = android.net.Proxy.getDefaultHost();
			if(host != null) {
				StringBuffer proxy = new StringBuffer(host);
				proxy.append(":").append(android.net.Proxy.getDefaultPort());
				LOG.v("JNIUtil", "getProxyInfo", proxy.toString());
				return proxy.toString();
			}
		}
		return null;
	}

	public static int android_support_phone_model()
	{
		int value = 1;
		LOG.v("JNIUtil", "android_support_phone_model", android.os.Build.MODEL);
		if(android.os.Build.MODEL.equals("K-Touch U86") 
				|| android.os.Build.MODEL.equals("Lenovo A60") 
			//	|| ( android.os.Build.MODEL.equals("MI-ONE Plus") && android.os.Build.VERSION.SDK.equals("10") )
			//&&	android.os.Build.VERSION.SDK.equals("16")
			//&& android.os.Build.VERSION.RELEASE.equals("4.1.2")
			)
		{
			value = 0;
		}
		return value;
	}
	public static int jni_support_harddecord(){
		int v = android.os.Build.VERSION.SDK_INT;
		return v;//((v>=9 && v<=10)|| (v>=14))?1:0;
	}

	public static void exePTL(byte[] ptl_data) {
	}
	
	public static String formatURL(String url) {
		return YouUtility.ConfigureFormatFeeURL(YouExplorer.instance, url);
	}
}
