package com.youplayer.core.struct;


public class You_player_media_info  {
	public static class Cls_you_media_type_e
	{
        public static final int YOU_ONLINE_VIDEO_MEDIA = 1;   //online video
        public static final int YOU_ONLINE_AUDIO_MEDIA = 2;  //online broadcast
        public static final int YOU_LOCAL_VIDEO_MEDIA  = 3;  
        public static final int YOU_LOCAL_AUDIO_MEDIA  = 5;
        public static final int YOU_AIRONE_VIDEO_MEDIA  = 6;
	}
	
    public static class Cls_fn_music_circle_mode
    {
        public static final int CIRCLE_ENTIRE_MODE  = 0;
        public static final int CIRCLE_SINGLE_MODE  = 1;
        public static final int CIRCLE_RANDOM_MODE  = 2;
        public static final int CIRCLE_MODE_MAX     = 3;
    }
    
    public static class Cls_you_player_render_mode
    {
        public static final int VIDEO_ASPECT_FULL_SCR     = 0;
        public static final int VIDEO_FULL_SCR            = 1;
        public static final int VIDEO_ORIGINAL_SCR        = 2;
        public static final int VIDEO_ASPECT_MODE_MAX     = 3;  
    }
    
    public static class Player_type
    {
        public static final int PLAYER_TYPE_HARD_SOFT     = 1;
        public static final int PLAYER_TYPE_SYSTEM        = 2;
        public static final int PLAYER_TYPE_SOFT          = 4;
        public static final int PLAYER_TYPE_AIRONE        = 8;  
    } 
    
    
    //ENGINER EVENT
    public int mediatype;   
    public int ds;          //直播  
    public int audio_mode;  
    public int video_mode;  
    public int is_system_player;   
    public boolean  can_fav;
    public boolean can_cache;
    
    //MEDIAINFO EVENT
    public int width; 
    public int height;  
    public int start_play_time;
    public long duration;   
    public String url;  
    public int isEpisode;   //剧集  
    public int is_have_next_online_media;  
    public int is_have_pre_online_media;   
    public boolean is_3D;
    public String definition;
    public int current_dfnt;
    public String cache_xyz_url;
    public String airone_code;
    
    public String toString(){
    	
    	return "mediatype:"+mediatype+";is_system_player:"+is_system_player
    			+";definition:"+definition+";current_dfnt"+current_dfnt+";video_mode:"+video_mode;
    }
    public void fixQuality(){
    	if( definition != null && definition.indexOf(current_dfnt+"")<0 && definition.length()==1){
    		current_dfnt = Integer.parseInt(definition);
    	}
    }
}
