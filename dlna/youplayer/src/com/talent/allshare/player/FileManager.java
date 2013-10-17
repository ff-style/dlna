package com.talent.allshare.player;

import java.io.File;

import android.os.Environment;

import com.talent.allshare.util.CommonUtil;


public class FileManager {

	public static String getSaveIconPath() {
		return CommonUtil.getRootFilePath() + "com.geniuseoe2012/allshare/icons/";
	}
	
	public static String mkSaveIconPath(String uri) {
		return getSaveIconPath() + getFormatUri(uri);
	}
	
	public static String mkSaveFilePath(String uri){
		File file = new File(Environment.getExternalStorageDirectory()+"/MacroDisk/"+ getFormatUri(uri));
		if(!file.exists()){
			file.mkdirs();
		}else{
			if(file.isFile()){
				file.delete();
				file.mkdirs();
			}
		}
		
		return file.getPath();
	}

	public static String getFormatUri(String uri)
	{
		uri  = uri.replace("/", "_");
		uri  = uri.replace(":", "");	
		uri  = uri.replace("?", "_");
		uri  = uri.replace("%", "_");	
		
		int length = uri.length();
		if (length > 150)
		{
			uri = uri.substring(length - 150);
		}
		
		
		return uri;
	}
	
}
