package com.youplayer.core.struct;

public class You_data_original_web_t {
	public You_data_original_web_t(String name,String url,String ourl,String weibourl,String definition,String pic,String from,int liveBroadcastFlag
				,boolean toply,boolean showPlayButton,boolean showDownButton){
		this.name = name;
		this.url = url;
		this.ourl = ourl;
		this.weibourl = weibourl;
		this.definition = definition;
		this.pic = pic;
		this.from = from;
		this.liveBroadcastFlag = liveBroadcastFlag;
		this.toply = toply;
		this.showPlayButton = showPlayButton;
		this.showDownButton = showDownButton;
	}

	public String name;
	public String url;
	public String ourl;
	public String weibourl;
	public String definition;  //1-->标清，2-->高清，3-->超清
	public String pic;  //分享图片地址
	public String from;
	public int liveBroadcastFlag;  //直播标识:0 非直播\1 视频直播\2 音频直播
	public boolean toply; 
	public boolean showPlayButton;
	public boolean showDownButton;
}
