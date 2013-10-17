package com.talent.allshare.more;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wireme.activity.Wifi_Setting;
import com.youplayer.player.R;




public class MoreActivity extends Activity implements OnCheckedChangeListener{
	
	private static Context mContext;
	private static WifiManager wifiManager;
	private static DhcpInfo dhcpInfo;
	private List<String> files ;
	static private ListView more_list;
	private WebView webview;
	static private RelativeLayout webview_rl;
	private LinearLayout player_setting_ll;
	static private LinearLayout wifi_setting_ll;
	private Button back_button;
	private Button back_button_;
	private Button back_button_wifi_setting;
	private Button wifi_setting_btn_btn;
	static private EditText username;
	private EditText password;
	private RelativeLayout back_button_player;
	private CheckBox c_box1;
	private CheckBox c_box2;
	private CheckBox c_box3;
	private RelativeLayout wv_rl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.more);
		
		initView();
		initDate();
		
		
		
	}
	@SuppressLint("NewApi")
	private static void hideSoftInputFromWindow()
	{
		InputMethodManager inputMethodManager =(InputMethodManager)mContext.getApplicationContext().
				getSystemService(Context.INPUT_METHOD_SERVICE); 
			
	
	inputMethodManager.hideSoftInputFromWindow(username.getWindowToken(), 0); //隐藏
	}
	private void initView() {
		
		c_box1 = (CheckBox)findViewById(R.id.c_box_1);
		c_box2 = (CheckBox)findViewById(R.id.c_box_2);
		c_box3 = (CheckBox)findViewById(R.id.c_box_3);
		c_box1.setOnCheckedChangeListener( this);
		c_box2.setOnCheckedChangeListener( this);
		c_box3.setOnCheckedChangeListener( this);
		
		
		back_button_ =(Button) findViewById(R.id.more_back_player_setting);
		
		back_button_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				webview_rl.setVisibility(View.GONE);
				player_setting_ll.setVisibility(View.GONE);
				more_list.setVisibility(View.VISIBLE);
			}
		});
		
		back_button_wifi_setting =(Button) findViewById(R.id.more_back_wifi_setting);
		
		back_button_wifi_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				webview_rl.setVisibility(View.GONE);
				wifi_setting_ll.setVisibility(View.GONE);
				more_list.setVisibility(View.VISIBLE);
				hideSoftInputFromWindow();
			}
		});
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		wifi_setting_btn_btn =(Button) findViewById(R.id.wifi_setting_btn);
		wifi_setting_btn_btn.setText("确定");
		wifi_setting_btn_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Wifi_Setting.set_username_password(getGateWay(MoreActivity.this),
						username.getText().toString(),
						password.getText().toString());
						hideSoftInputFromWindow();
//				Wifi_Setting.mac_crypto(getGateWay(MoreActivity.this),getLocalMacAddress());
			}
		});
		
		more_list = (ListView)findViewById(R.id.more_list);
		webview_rl = (RelativeLayout)findViewById(R.id.web_view_rl);
		 webview = (WebView) findViewById(R.id.web_view);
		 
		 
		 player_setting_ll = (LinearLayout)findViewById(R.id.player_setting);
		 wifi_setting_ll = (LinearLayout)findViewById(R.id.wifi_setting);
		  back_button = (Button)findViewById(R.id.more_back);
		  back_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				webview_rl.setVisibility(View.GONE);
				player_setting_ll.setVisibility(View.GONE);
				more_list.setVisibility(View.VISIBLE);
			}
		});
	}

	@SuppressLint("NewApi") private void initDate() {
		files = new ArrayList<String>();
		
		files.add(getResources().getString(R.string.wifisetting));
		files.add(getResources().getString(R.string.routersetting));
		files.add(getResources().getString(R.string.playersetting));
		
		
		MoreAdapter moreAdapter = new MoreAdapter(MoreActivity.this);
		moreAdapter.setData(files);
		more_list.setAdapter(moreAdapter);
		
		more_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i,
					long l) {
				switch (i) {
				case 0:
					wifi_setting_ll.setVisibility(View.VISIBLE);
					player_setting_ll.setVisibility(View.GONE);
					webview_rl.setVisibility(View.GONE);
					more_list.setVisibility(View.GONE);
//					Wifi_Setting.wifi_setting(getGateWay(MoreActivity.this));
					break;
				case 1:
//					player_setting_ll.setVisibility(View.GONE);
//					webview_rl.setVisibility(View.VISIBLE);
//					more_list.setVisibility(View.GONE);
//					 webview.setInitialScale(100);
				        //设置WebView属性，能够执行Javascript脚本 
//				        webview.getSettings().setJavaScriptEnabled(true); 
				        //加载需要显示的网页 
				        //不跳转
//				        webview.getSettings().setLoadWithOverviewMode(false);
//				        webview.getSettings().setSupportZoom(true);
//				        webview.getSettings().setBuiltInZoomControls(true);
				        String gateway = getGateWay(MoreActivity.this);
				        gateway = "http://"+gateway;
				        System.out.println(gateway);
				        webview.loadUrl(gateway); 
//				        webview.loadUrl("http://www.hao123.com"); 
				         Uri uri = Uri.parse(gateway); 
				         Intent intent = new Intent(Intent.ACTION_VIEW , uri); 
				        startActivity(intent);
//				        Intent intent = new Intent(Action.);
				        
//				        webview.setWebViewClient(new WebViewClient(){
//				                @Override
//				                public boolean shouldOverrideUrlLoading(WebView view, String url){
//				                        view.loadUrl(url);
//				                        return false;
//				                        
//				                }
//				        });
					break;
				case 2:
					player_setting_ll.setVisibility(View.VISIBLE);
					webview_rl.setVisibility(View.GONE);
					more_list.setVisibility(View.GONE);
					break;
				default:
					break;
				}
				
//				Intent intent = new Intent(MoreActivity.this, WifiSetActivity.class);
//				startActivity(intent);
			}
		});
	}
	static private Handler handler  = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x123:
				Restart();
				break;

			default:
				break;
			}
		};
	};
    static public void TipRestart()
    {
    	handler.sendEmptyMessageDelayed(0x123, 1000);
    }
	private static void  Restart()
    {
		new AlertDialog.Builder(mContext).setTitle("是否重启路由器让设置立即生效")
		.setNegativeButton("重启路由", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Wifi_Setting.reboot("y");
				webview_rl.setVisibility(View.GONE);
				wifi_setting_ll.setVisibility(View.GONE);
				more_list.setVisibility(View.VISIBLE);
				hideSoftInputFromWindow();
//				MoreActivity.this.finish();
			}
		})
		.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				webview_rl.setVisibility(View.GONE);
				wifi_setting_ll.setVisibility(View.GONE);
				more_list.setVisibility(View.VISIBLE);
				hideSoftInputFromWindow();
			}
		}).show();
    	
    }
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		new AlertDialog.Builder(MoreActivity.this).setTitle("确定退出？")
		.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MoreActivity.this.finish();
			}
		})
		.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

    public static String getGateWay(Context context){ 
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        dhcpInfo = wifiManager.getDhcpInfo(); 
         
     //dhcpInfo获取的是最后一次成功的相关信息，包括网关、ip等  
     return FormatIP(dhcpInfo.gateway);      
    } 
	
    @SuppressLint("NewApi") 
    public static String FormatIP(int IpAddress) {
        return Formatter.formatIpAddress(IpAddress);
        }
    
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked){
					c_box1.setChecked(false);
					c_box2.setChecked(false);
					c_box3.setChecked(false);
					buttonView.setChecked(true);
				}
		
	}
	public String getLocalMacAddress() {  
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        System.out.println("info.getMacAddress()="+info.getMacAddress());
        return info.getMacAddress();  
    }  	
}