package com.youplayer.core.jni;

class Load_Jni{
	private static String classname[] = {
		
		"com/youplayer/player/Player_UIManager",
		
		"com/youplayer/core/mediainfo/YouPlayerMediaDesc",
		
		null,
		
		null,
		
		"com/youplayer/core/mediainfo/YouPlayerBitMap",
		
		"com/youplayer/core/mediainfo/YouPlayerAudioInfo",
		
		null	
		
	};
	
	public Load_Jni()
	{
		
	}
	
	public static int get_class_num()
	{
		
		return classname.length;
		
	}
	
	public static String[] get_class()
	{
		
		return classname;
		
	}
}
