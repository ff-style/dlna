package com.youplayer.player.fullplayer;


public interface YouPlayerEvent {
    public static final int PLAYER_DEFAULT_UNKNOW_VALUE         = -1;
    public static final int PLAYER_PANEL_PAUSE_TIMER_SHOW_TIME  = 1;
    public static final int PLAYER_PANEL_TIMER_SHOW_TIME        = 5000;
    public static final int PLAYER_TIPS_SHOW_TIME               = 1000;
    public static final int PLAYER_PLAY_STATE_SHOW_TIME         = 1000;
    public static final int PLAYER_PLAY_TIMEOUT_TIME            = 60000;
    public static final int PLAYER_PLAY_TIMEOUT_TIME_SYS_SEEK   = 1000;
    public static final int PLAYER_PLAY_BUFFERING_TIMEOUT_1     = 10000;
    public static final int PLAYER_PLAY_BUFFERING_TIMEOUT_2     = 20000;
    
    public static final int PLAYER_TIPS_UI_FONT_SIZE_SMALL      = 40;
    public static final int PLAYER_TIPS_UI_FONT_SIZE_LARGE      = 64;
    public static final int PLAYER_TIPS_UI_FONT_SIZE_20         = 20;
    
    public static final int PLAYER_PLAY_SPEED_08 = 0;
    public static final int PLAYER_PLAY_SPEED_10 = 1;
    public static final int PLAYER_PLAY_SPEED_15 = 2;
    public static final int PLAYER_PLAY_SPEED_20 = 3;
    
    public static final int PLAYER_PLAY_STATE_PLAY  = 1;
    public static final int PLAYER_PLAY_STATE_PAUSE = 2;
    
    public static final String PLAYER_MEDIA_INFO_WIDTH                  = "media_info_width";
    public static final String PLAYER_MEDIA_INFO_HEIGHT                 = "media_info_height";
    public static final String PLAYER_MEDIA_INFO_DURATION               = "media_info_duration";
    public static final String PLAYER_MEDIA_INFO_MEDIA_TYPE             = "media_info_media_type";
    public static final String PLAYER_MEDIA_INFO_SYSTEM_PLAYER          = "media_info_system_player";
    public static final String PLAYER_MEDIA_INFO_PLAYER_STATUS          = "media_info_player_status";
    public static final String PLAYER_MEDIA_INFO_PLAYER_VIDEO_SCREEN    = "media_info_player_video_screen";
    public static final String PLAYER_MEDIA_INFO_PLAYER_AUDIO_LOOP      = "media_info_player_audio_loop";
    public static final String PLAYER_MEDIA_INFO_PLAYER_IS_LIVE         = "media_info_player_is_live";
    public static final String PLAYER_MEDIA_INFO_OPEN_SUCCESS           = "media_info_player_open_success";
    public static final String PLAYER_MEDIA_INFO_IS_EPISODE             = "media_info_player_is_episode";
    public static final String PLAYER_MEDIA_INFO_HAS_PREVIOUS           = "media_info_player_has_previous";
    public static final String PLAYER_MEDIA_INFO_HAS_NEXT               = "media_info_player_has_next";
    public static final String PLAYER_MEDIA_INFO_URL                    = "media_info_player_url";
    public static final String PLAYER_MEDIA_INFO_READY_TO_PLAY          = "media_info_player_ready_to_play";
    public static final String PLAYER_MEDIA_INFO_CAN_FAV                = "media_info_player_can_fav";
    public static final String PLAYER_MEDIA_INFO_CAN_CACHE              = "media_info_player_can_cache";
    public static final String PLAYER_EVENT_IS_SHOW_STOP                = "media_info_show_is_stop";
    public static final String PLAYER_EVENT_IS_3D                = "media_info_show_is_3d";
        
    
    
    public static final int PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE  = 0;
    public static final int PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE  = 1;
    public static final int PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE   = 2;
    
    public final static String PLAYER_UI_MSG_SEEKBAR            = "seekBar";
    
    /**
     * FULLPLAYER CTRL ID  
     */
    public final static int PLAYER_UI_MSG_ADAPTER_HANDLER_CONVERT           = 888888;
//    public final static int PLAYER_UI_MSG_HANDLER_CONVERT_AD                = 888889;
    public final static int PLAYER_UI_MSG_HANDLER_CONVERT_AIRONE            = 888899;
    public final static int PLAYER_FN_UI_EVT_PAGE_SEEK_START                = 1000000;
    public final static int PLAYER_FN_UI_EVT_PAGE_SEEK_SEEKING              = 1000001;
    public final static int PLAYER_FN_UI_EVT_PAGE_SEEK_END                  = 1000002;
    public final static int PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY       = 1000003;
    public final static int PLAYER_FN_UI_MSG_TIP_INVISIBILITY               = 1000004;
    public final static int PLAYER_FN_UI_MSG_SURFACE_RECT                   = 1000005;//视频大小调节
    public final static int PLAYER_FN_UI_MSG_PLAY_MODE_VIDEO                = 1000006;//视频模式
    public final static int PLAYER_FN_UI_MSG_PLAY_MODE_AUDIO                = 1000007;
    public final static int PLAYER_FN_UI_MSG_PLAY_SHARE                     = 1000008;
    public final static int PLAYER_FN_UI_MSG_PLAY_BOOKMARK                  = 1000009;
    public final static int PLAYER_FN_UI_MSG_PLAY_DOWNLOAD                  = 10000010;
    public final static int PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT             = 10000011;
    public final static int PLAYER_FN_UI_MSG_PLAY_RELATION                  = 10000012;//关联视频
    public final static int PLAYER_FN_UI_EVT_PAGE_CHANGE_BRIGHT             = 10000013;//调节亮度
    public final static int PLAYER_FN_UI_EVT_PAGE_SEEK_UP_DOWN              = 10000014;//手势事件
    public final static int PLAYER_FN_UI_EVT_PAGE_SEEK_LEFT_RIGHT           = 10000015;
    public final static int PLAYER_FN_UI_MSG_TIP_VISIBILITY_REFRESH         = 10000016;
    public final static int PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT_SIZE        = 10000017;//调节值
    public final static int PLAYER_FN_UI_MSG_PLAY_RELATION_VISIBLITY        = 10000018;//显示关联视频
    public final static int PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY_TIMER = 10000019;//上下工具栏
    public final static int PLAYER_FN_UI_MSG_CONTROLL_TIPS_VISIBLITY_TIMER  = 10000020;//全屏提示文字
    public final static int PLAYER_FN_UI_MSG_TRACK_TIPS_NOSUPPORT           = 10000021;//不支持提示信息
    public final static int PLAYER_FN_UI_MSG_SPEED_TIPS_NOSUPPORT           = 10000022;//
    public final static int PLAYER_FN_UI_MSG_PREVIOUS_TIPS_NOSUPPORT        = 10000023;//上一条 没有提示
    public final static int PLAYER_FN_UI_MSG_NEXT_TIPS_NOSUPPORT            = 10000024;
    public final static int PLAYER_FN_UI_MSG_NET_BUFFERING_SHORT_TIMER      = 10000025;//缓冲提示
    public final static int PLAYER_FN_UI_MSG_NET_BUFFERING_LONG_TIMER       = 10000026;
    public final static int PLAYER_FN_UI_MSG_NET_BUFFERING_SHORT_VISIBLITY  = 10000027;
    public final static int PLAYER_FN_UI_MSG_NET_BUFFERING_LONG_VISIBLITY   = 10000028;    
    public final static int PLAYER_FN_UI_MSG_NET_BUFFERING_VISIBLITY        = 10000029;   
    public final static int PLAYER_FN_UI_MSG_TIPS_DOWNLOAD_INVISIBLITY      = 10000030;   
    public final static int PLAYER_FN_UI_MSG_FAV_TIPS_NOSUPPORT             = 10000031;    
    public final static int PLAYER_FN_UI_MSG_RELATIVE_TOUCH_MOVE            = 10000032;
    public final static int PLAYER_FN_UI_MSG_RELATIVE_TOUCH_UP              = 10000033;
    public final static int PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT             = 10000034;//网络超时 退出
    public final static int PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION      = 10000035;    
    public final static int PLAYER_FN_UI_MSG_WAITTING     					= 10000036;
    public final static int PLAYER_FN_UI_MSG_LOCK                           = 10000037;
    public final static int PLAYER_FN_UI_MSG_SHARETIP                       = 10000038;
    public final static int PLAYER_FN_UI_MSG_SHARE_TIPS_NOSUPPORT           = 10000039;
    public final static int PLAYER_FN_UI_MSG_TIPS_NOSUPPORT                 = 10000040;
    public final static int PLAYER_FN_UI_MSG_TIPS_3D_NOSUPPORT              = 10000041;
    public final static int PLAYER_FN_UI_MSG_TIPS_3D_SUPPORT                = 10000042;
    public final static int PLAYER_FN_UI_MSG_BTN_RELATIVE                   = 10000043;
    public final static int PLAYER_FN_UI_MSG_PANEL_VIEW                     = 10000044; 
    public final static int PLAYER_FN_UI_MSG_PANEL_VIEW_CANCEL              = 10000045;
    
    public final static int PLAYER_FN_UI_MSG_SOFT_VOL                       = 10000046;
    public final static int PLAYER_FN_UI_MSG_UPDATE_SYSTIME                 = 10000047;
    
    public final static int PLAYER_FN_UI_AIRONE_DEVS_DIALOG_VISIBLE            = 10000048;
    public final static int PLAYER_FN_UI_AIRONE_DEVS_DIALOG_INVISIBLE            = 10000049;
    
    
}
