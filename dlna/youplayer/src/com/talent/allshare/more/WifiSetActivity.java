package com.talent.allshare.more;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.text.format.Formatter; 
import com.youplayer.player.R;




public class WifiSetActivity extends Activity {
	
	private static WifiManager wifiManager;
	private static DhcpInfo dhcpInfo;
	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifiset);
		//实例化WebView对象 
        webview = (WebView) findViewById(R.id.web_view);
        webview.setInitialScale(60);
        //设置WebView属性，能够执行Javascript脚本 
        webview.getSettings().setJavaScriptEnabled(true); 
        //加载需要显示的网页 
        //不跳转
        webview.getSettings().setLoadWithOverviewMode(false);
        String str = getGateWay(WifiSetActivity.this);
        System.out.println("gzf"+str);
        webview.loadUrl("http://192.168.89.1"); 
       
        webview.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                        view.loadUrl(url);
                        return false;
                        
                }
        });
//        webview.getSettings().setBuiltInZoomControls(true);
        
        //设置Web视图 
//        setContentView(webview); 
		
	}
	
	
//	public static String getGateWay() {
//		 if (Netgear_WifiManager.wifiManager != null) {
//		 DhcpInfo dhcpInfo=Netgear_WifiManager.wifiManager.getDhcpInfo();
//		 Log.e("gateway is", Netgear_IpAddressTranfer.long2ip(dhcpInfo.gateway));
//		 }
//		 return null;
//		 }
	  //网关获取  
    public static String getGateWay(Context context){ 
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        dhcpInfo = wifiManager.getDhcpInfo(); 
         
     //dhcpInfo获取的是最后一次成功的相关信息，包括网关、ip等  
     return FormatIP(dhcpInfo.gateway);      
    } 
 // IP地址转化为字符串格式
    @SuppressLint("NewApi") public static String FormatIP(int IpAddress) {
     return Formatter.formatIpAddress(IpAddress);
     }
//		由于DhcpInfo类提供的网关ip是个整数，因此还得将整／数转为ip格式才可以

		//===================================================
//		android获取wifi网络信息android获取wifi信息源码：
//		public class Main extends Activity {
//		 private TextView tv;
//		 private WifiManager wifiManager;
//		 private DhcpInfo dhcpInfo;
//		 private WifiInfo wifiInfo;
//		 @Override
//		 public void onCreate(Bundle savedInstanceState) {
//		  super.onCreate(savedInstanceState);
//		  setContentView(R.layout.main);
//		  
//		  wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//		  dhcpInfo = wifiManager.getDhcpInfo();
//		  wifiInfo = wifiManager.getConnectionInfo();
//		  int ip = wifiInfo.getIpAddress();
//		  int speed = wifiInfo.getLinkSpeed();
//		  int networkId = wifiInfo.getNetworkId();
//		  int getRssi = wifiInfo.getRssi();
//		  String macAddr = wifiInfo.getMacAddress();
//		  String getSSID = wifiInfo.getSSID();
//		  String detail = wifiInfo.toString();
//		  String bssid = wifiInfo.getBSSID();
//		  
//		  tv = (TextView) this.findViewById(R.id.wifiInfo);
//		  
//		  tv.append("ip :" + ip + "n");
//		  tv.append("speed :" + speed + "n");
//		  tv.append("macAddr :" + macAddr +"n");
//		  tv.append("networkId :" + networkId+ "n");
//		  tv.append("getRssi :" + getRssi +"n");
//		  tv.append("getSSID :" + getSSID +"n");
//		  tv.append("detail :" + detail + "n");
//		  tv.append("bssid :" + bssid + "n");
//		  tv.append("dhcpInfo geteway is :"+ dhcpInfo.gateway + "n");
//		  tv.append("dhcpInfo mask is :" + dhcpInfo.netmask + "n");
//		  tv.append("dhcpInfo ip is :" + dhcpInfo.ipAddress + "n");
//		  tv.append("ip is :" + FormatIP(ip) +"n");
//		  tv.append("geteway is :" + FormatIP(dhcpInfo.gateway) + "n");
//		  tv.append("mask is :" + FormatIP(dhcpInfo.netmask) +"n");
//		  }
//		// IP地址转化为字符串格式
//		 public String FormatIP(int IpAddress) {
//		  return Formatter.formatIpAddress(IpAddress);
//		  }
//		}

	
	
	private void initView() {
		WebView view = (WebView) findViewById(R.id.web_view);
		
		
	}

	private void initDate() {
		
	}
	
	
	
}