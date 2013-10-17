package com.youplayer.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class YouPhonePowerUtil {
	public static final int POWER_SCALE = 100;
	private YouPhonePowerListener phonePowerListener;

    private static YouPhonePowerUtil instance;
    private YouPhonePowerUtil() {

    }
    
    public static YouPhonePowerUtil getInstance() {
        if(null == instance){
            instance = new YouPhonePowerUtil();
        }
        return instance;
    }

	public void registerPeceiver(Context context) {
		if (null != context && null != mBatteryInfoReceiver)
			context.registerReceiver(mBatteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	public void unregisterPeceiver(Context context) {
		try{
			if (null != context && null != mBatteryInfoReceiver){
				context.unregisterReceiver(mBatteryInfoReceiver);
				mBatteryInfoReceiver = null;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setPhonePowerListener(YouPhonePowerListener phonePowerListener) {
		if(this != null && phonePowerListener != null)
			this.phonePowerListener = phonePowerListener;
	}

	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {

				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", POWER_SCALE);
				if(phonePowerListener != null)
					phonePowerListener.powerChange(level, scale);
			}

		}
	};

}
