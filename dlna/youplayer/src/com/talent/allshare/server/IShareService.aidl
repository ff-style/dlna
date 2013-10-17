package com.talent.allshare.server;


interface IShareService {

	 void start();
	 void stop();
	 void restart();
	 void updateconfig(String devname,boolean bsharevideo,boolean bsharemusic,boolean bsharepicture,boolean sharedoc);
	 boolean isStarted();
	 String getCurDevName();
	 boolean getbsharevideo();
	 boolean getbsharemusic();
	 boolean getbsharepicture();
	 boolean getbsharedoc();
	  
}
