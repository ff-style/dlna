package com.talent.allshare.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class IOHelper {

	public static void DownloadTheFile(final String strPath) {

		try {

			Runnable r = new Runnable() {
				public void run() {
					try {
						URL myURL = new URL(strPath);
						URLConnection conn = myURL.openConnection();
						conn.connect();
						InputStream is = conn.getInputStream();
						if (is == null) {
							throw new RuntimeException("stream is null");
						}
						String fileEx = strPath.substring(
								strPath.lastIndexOf("."), strPath.length())
								.toLowerCase();
						String fileNa = strPath
								.substring(strPath.lastIndexOf("/") + 1,
										strPath.lastIndexOf("."));
						File myTempFile = File.createTempFile(fileNa, fileEx);
								
						FileOutputStream fos = new FileOutputStream(myTempFile);
						byte buf[] = new byte[128];
						do {
							int numread = is.read(buf);
							if (numread <= 0) {
								break;
							}
							fos.write(buf, 0, numread);
						} while (true);

					} catch (Exception e) {

					}
				}
			};
			new Thread(r).start();
		} catch (Exception e) {
			
		}
	}

	public static  void openFile(Context context, File file) {
		try{

		File myFile = file;
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
//		String type = getMIMEType(myFile.getName());
		String type = getMIMEType(myFile);
		intent.setDataAndType(Uri.fromFile(myFile), type);
		context.startActivity(intent);
		}
		catch(Exception e)
		{
			System.out.print("e=" + e);
		}
	}

	//str ��ʽ�磺aa=cc&bb=dd, key ָ����aa��bb
	public static String getData(String str,String key){
		if(str.length()==0)return "";
		String val="";
		if(str.contains(key)){
			String[] keyVals=str.split("&");
			for(int i=0;i<keyVals.length;i++){
				String[] keyVal=keyVals[i].split("=");
				if(keyVal[0].equals(key)){
					val= keyVal[1];}
				
			}
		}
		return val;
	}
	
	
	public static void delFile(String fileName) {

		File myFile = new File(fileName);
		if (myFile.exists()) {
			myFile.delete();
		}
	}

	public static String getMIMEType(String fileName) {
		File myFile = new File(fileName);
		String type = "";
		String fName = myFile.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		}  else if(end.equals("xls")||end.equals("xlsx")){
			type = "application/vnd.ms-excel";
		}else if(end.equals("doc") || end.equals("docx") ){
			type = "application/msword";
		}else if(end.equals("txt")){
			type = "text/plain";
		}else if(end.equals("pdf")){
			type = "application/pdf";
		}
		else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}
	
	/** 
	 * 根据文件后缀名获得对应的MIME类型。 
	 * @param file 
	 */  
	private static String getMIMEType(File file)  
	{  
	    String type="*/*";  
	    String fName=file.getName();  
	    //获取后缀名前的分隔符"."在fName中的位置。  
	    int dotIndex = fName.lastIndexOf(".");  
	    if(dotIndex < 0){  
	        return type;  
	    }  
	    /* 获取文件的后缀名 */  
	    String end=fName.substring(dotIndex,fName.length()).toLowerCase();  
	    if(end=="")return type;  
	    //在MIME和文件类型的匹配表中找到对应的MIME类型。  
	    for(int i=0;i<MIME_MapTable.length;i++){  
	        if(end.equals(MIME_MapTable[i][0]))  
	            type = MIME_MapTable[i][1];  
	    }  
	    return type;  
	}
	

private final static  String[][] MIME_MapTable={  
    //{后缀名，    MIME类型}  
    {".3gp",    "video/3gpp"},  
    {".apk",    "application/vnd.android.package-archive"},  
    {".asf",    "video/x-ms-asf"},  
    {".avi",    "video/x-msvideo"},  
    {".bin",    "application/octet-stream"},  
    {".bmp",      "image/bmp"},  
    {".c",        "text/plain"},  
    {".class",    "application/octet-stream"},  
    {".conf",    "text/plain"},  
    {".cpp",    "text/plain"},  
    {".doc",    "application/msword"},  
    {".exe",    "application/octet-stream"},  
    {".gif",    "image/gif"},  
    {".gtar",    "application/x-gtar"},  
    {".gz",        "application/x-gzip"},  
    {".h",        "text/plain"},  
    {".htm",    "text/html"},  
    {".html",    "text/html"},  
    {".jar",    "application/java-archive"},  
    {".java",    "text/plain"},  
    {".jpeg",    "image/jpeg"},  
    {".jpg",    "image/jpeg"},  
    {".js",        "application/x-javascript"},  
    {".log",    "text/plain"},  
    {".m3u",    "audio/x-mpegurl"},  
    {".m4a",    "audio/mp4a-latm"},  
    {".m4b",    "audio/mp4a-latm"},  
    {".m4p",    "audio/mp4a-latm"},  
    {".m4u",    "video/vnd.mpegurl"},  
    {".m4v",    "video/x-m4v"},      
    {".mov",    "video/quicktime"},  
    {".mp2",    "audio/x-mpeg"},  
    {".mp3",    "audio/x-mpeg"},  
    {".mp4",    "video/mp4"},  
    {".mpc",    "application/vnd.mpohun.certificate"},          
    {".mpe",    "video/mpeg"},      
    {".mpeg",    "video/mpeg"},      
    {".mpg",    "video/mpeg"},      
    {".mpg4",    "video/mp4"},      
    {".mpga",    "audio/mpeg"},  
    {".msg",    "application/vnd.ms-outlook"},  
    {".ogg",    "audio/ogg"},  
    {".pdf",    "application/pdf"},  
    {".png",    "image/png"},  
    {".pps",    "application/vnd.ms-powerpoint"},  
    {".ppt",    "application/vnd.ms-powerpoint"},  
    {".prop",    "text/plain"},  
    {".rar",    "application/x-rar-compressed"},  
    {".rc",        "text/plain"},  
    {".rmvb",    "audio/x-pn-realaudio"},  
    {".rtf",    "application/rtf"},  
    {".sh",        "text/plain"},  
    {".tar",    "application/x-tar"},      
    {".tgz",    "application/x-compressed"},   
    {".txt",    "text/plain"},  
    {".wav",    "audio/x-wav"},  
    {".wma",    "audio/x-ms-wma"},  
    {".wmv",    "audio/x-ms-wmv"},  
    {".wps",    "application/vnd.ms-works"},  
    //{".xml",    "text/xml"},  
    {".xml",    "text/plain"},  
    {".z",        "application/x-compress"},  
    {".zip",    "application/zip"},
    {".xls",    "application/vnd.ms-excel"}, 
    {".xlsx",    "application/vnd.ms-excel"}, 
    {".doc",    "application/msword"}, 
    {".docx",    "application/msword"}, 
    {"",        "*/*"}      
};
}
