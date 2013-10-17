package com.youplayer.core.struct;

public class You_full_screen_player_data_to_ui 
{
	public static class Cls_you_player_status
	{
        public final static int NoneStatus      = 0;        
        public final static int OpenningStatus  = 1;
        public final static int PlayingStatus   = 2;
        public final static int PauseStatus     = 3;
        public final static int BufferingStatus = 4;
	}
	
	public static class Cls_you_player_rate
    {
        public static final int YOU_PLAYER_LOW     = 0;
        public static final int YOU_PLAYER_NORMAL  = 1;
        public static final int YOU_PLAYER_HIGH    = 2;
        public static final int YOU_PLAYER_EXTREME = 3;
        public static final int YOU_PLAYER_TWO     = 4;
    }
	
    public static class Cls_you_player_render_mode
    {
        public static final int ASPECT_FULL_SCR     = 0;
        public static final int FULL_SCR            = 1;
        public static final int ORIGINAL_SCR        = 2;
        public static final int ASPECT_MODE_MAX     = 3;
    }
    
    public static class Cls_PlayerAdType
    {
        public static final int AD_TYPE_ALL         = 0;
        public static final int AD_TYPE_FRONT       = 1;
        public static final int AD_TYPE_BEHIND      = 2;
        public static final int AD_TYPE_PAUSE       = 3;
        public static final int AD_TYPE_BUFFERING   = 4;
        public static final int AD_TYPE_SUBTITLE    = 5;
        public static final int AD_TYPE_LOGO        = 6;
        public static final int AD_TYPE_UNDEFINE    = 7;
    }
    
    public static class Cls_fn_full_screen_audio_and_subtitle
    {
        public int audio_cnt;
        public int cur_audio;
        public String[] audio_cell;
        public int sub_cnt;
        public int cur_sub;
        public String[] sub_cell;
    }	
    
    /** 广告具体内容类 */
    public static class Cls_fn_ad_content_t
    {
        public String youcode;
        public String adlcode;
        public String uid;
        public String url;
        public int    w;
        public int    h;
        public String imei;
        public String imsi;
        public int adtype;
        public int location;
        public int leftMargin;
        public int rightMargin;
        public int upMargin;
        public int downMargin;
        public int textEffectType;
        public int textEffectDirection;
        public int effectSpeed;
        public String bottomColor;
        public int subtitleFont;
        public int subtitleFontSize;
        public int subtitleColor;
    }
	
    /** 广告总条目类 */
    public static class Cls_fn_ad_data_t
    {
        public int ad_cnt;
        public Cls_fn_ad_content_t[] items;
    }
    
    /** 关联视频 */
    public static class Cls_fn_related_content_t
    {
    	public String url;
    	public String name;
    	public String pic;
    	
    }
    
    public static class Cls_fn_data_related_t
    {
    	public Cls_fn_related_content_t[] related_content;
    }
    
    public int                           type;	//Cls_fn_page_full_player_show_type
    public Object       				 value;
}
