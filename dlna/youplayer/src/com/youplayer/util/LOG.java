package com.youplayer.util;

import android.util.Log;

public class LOG {
	
	public final static boolean mode_for_release = false;
	public final static boolean server_switch = true;
	public final static String TAG = "FP10";
	
    public static void v(String tag, String type, String msg)
    {
    	if(!mode_for_release)
        {
    		String des = String.format("[%s][%s]%s", tag, type, msg);
    		Log.v(TAG, des);
        }
    }
    
    public static void v(String tag, String type, int msg)
    {
    	if(!mode_for_release)
        {
    		String des = String.format("[%s][%s]%d", tag, type, msg);
    		Log.v(TAG, des);
        }
    }
    
    public static void v(String tag, String type, boolean msg)
    {
        if(!mode_for_release)
        {
            String des = String.format("[%s][%s]%s", tag, type, msg ? "true" : "false");
            Log.v(TAG, des);
        }
    }
    public static void i(String tag, String type, String msg)
    {
    	if(!mode_for_release)
        {
    		String des = String.format("[%s][%s]%s", tag, type, msg);
    		Log.i(TAG, des);
        }
    }
    
    public static void i(String tag, String type, int msg)
    {
    	if(!mode_for_release)
        {
    		String des = String.format("[%s][%s]%d", tag, type, msg);
    		Log.v(TAG, des);
        }
    }
    public static void i(String tag, String msg)
    {
       i(tag,"",msg);
    }
    public static void i(String tag, String type, boolean msg)
    {
        if(!mode_for_release)
        {
            String des = String.format("[%s][%s]%s", tag, type, msg ? "true" : "false");
            Log.v(TAG, des);
        }
    }
    public static void e(String tag, String type, String msg)
    {
    	if(!mode_for_release)
    	{
    		String des = String.format("[%s][%s]%s", tag, type, msg);
    		Log.e(TAG, des);
    	}
    }
    public static void e(String tag, String type, int msg)
    {
    	if(!mode_for_release)
    	{
    		String des = String.format("[%s][%s]%d", tag, type, msg);
    		Log.e(TAG, des);
    	}
    }
    public static void e(String tag, String type, boolean msg)
    {
    	if(!mode_for_release)
    	{
    		String des = String.format("[%s][%s]%d", tag, type, msg ? "true" : "false");
    		Log.e(TAG, des);
    	}
    }
}
