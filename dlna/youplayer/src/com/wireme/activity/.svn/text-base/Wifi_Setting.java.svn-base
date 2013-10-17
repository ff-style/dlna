package com.wireme.activity;

import com.talent.allshare.MainTabActivity;
import com.talent.allshare.more.MoreActivity;


public class Wifi_Setting {
	public native static boolean mac_crypto(String server_ip,String mac);
	public native static boolean reboot(String flag);
	public native static boolean set_username_password(String server_ip,String username,String password);
	static int jni_callback(String str,int type)
	{
		System.out.println("str=:" + str + ",type=:" + type);
		switch(type)
		{
			case 1:
				MainTabActivity.WifiNotFindDlna();
				break;
			case 2:
				MainTabActivity.gotoSearchActivity();
				break;
			case 3:
				MoreActivity.TipRestart();
				break;
		}
        return 1;
	}
}
