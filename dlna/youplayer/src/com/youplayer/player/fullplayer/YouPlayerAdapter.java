package com.youplayer.player.fullplayer;

import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Rect;
import android.view.WindowManager;

import com.youplayer.core.You_Core;
import com.youplayer.core.struct.You_full_screen_player_data_to_ui;
import com.youplayer.core.struct.You_page_full_player_show_type;
import com.youplayer.core.struct.You_player_media_info;
import com.youplayer.core.struct.You_player_media_info.Player_type;
import com.youplayer.player.YouPlayerFullScreenPlayer;
import com.youplayer.player.R;
//import com.youplayer.player.Util;
import com.youplayer.player.frame.YouPlayerEventControler;
import com.youplayer.util.LOG;


public abstract class YouPlayerAdapter implements YouPlayerEvent{

    private static final String TAG = "PlayerAdapter";
    private Context mContext;
    
    private int     mPlayerPlayStatus = You_full_screen_player_data_to_ui.Cls_you_player_status.NoneStatus;
    private boolean mPlayerIsBuffering = false;
    private Rect    mPlayerSurfaceRect = new Rect(0, 0, 800, 480);
    private HashMap<String, Object> mPlayerAdapterData = new HashMap<String, Object>();
    private YouPlayerEventFilter             mEventFilter;
    public You_player_media_info mMediaInfo;
    
    
    //ControllPanel Timer Event
    private int[] ContrllPanelEventFilter = new int[]{
    		You_Core.FN_FULL_SCREEN_BTN_PLAY,
    		You_Core.FN_FULL_SCREEN_BTN_MODE_CHANGE,
            You_Core.FN_FULL_SCREEN_BTN_NEXT,
            You_Core.FN_FULL_SCREEN_BTN_PREVIOUS,
            You_Core.FN_FULL_SCREEN_BTN_RATE,
            You_Core.FN_FULL_SCREEN_BTN_FAV,
            You_Core.FN_FULL_SCREEN_BTN_AUDIO_CHANNEL,
            You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_READY_TO_PLAY,
            PLAYER_FN_UI_EVT_PAGE_SEEK_END,
            PLAYER_FN_UI_MSG_PLAY_SHARE,
            PLAYER_FN_UI_MSG_PLAY_DOWNLOAD,
            PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT,
            PLAYER_FN_UI_MSG_PLAY_RELATION,
            PLAYER_FN_UI_MSG_RELATIVE_TOUCH_UP,
            PLAYER_FN_UI_MSG_BTN_RELATIVE,
            PLAYER_FN_UI_AIRONE_DEVS_DIALOG_INVISIBLE
            
    };
    //Tips Timer Event
    private int[] TipsEventFilters = new int[]{
    		You_Core.FN_FULL_SCREEN_BTN_MODE_CHANGE,
    		You_Core.FN_FULL_SCREEN_BTN_FAV,
    		You_Core.FN_FULL_SCREEN_BTN_NEXT,
    		You_Core.FN_FULL_SCREEN_BTN_PREVIOUS,
            PLAYER_FN_UI_EVT_PAGE_SEEK_END,
            PLAYER_FN_UI_EVT_PAGE_SEEK_LEFT_RIGHT,
            PLAYER_FN_UI_MSG_TRACK_TIPS_NOSUPPORT,
            PLAYER_FN_UI_MSG_SPEED_TIPS_NOSUPPORT,
            PLAYER_FN_UI_MSG_PREVIOUS_TIPS_NOSUPPORT,
            PLAYER_FN_UI_MSG_SHARE_TIPS_NOSUPPORT,
            PLAYER_FN_UI_MSG_TIPS_NOSUPPORT,
            PLAYER_FN_UI_MSG_TIPS_3D_NOSUPPORT,
            You_Core.FN_FULL_SCREEN_BTN_3D,
            PLAYER_FN_UI_MSG_NEXT_TIPS_NOSUPPORT,
            PLAYER_FN_UI_EVT_PAGE_CHANGE_BRIGHT,
            PLAYER_FN_UI_MSG_FAV_TIPS_NOSUPPORT,
            PLAYER_FN_UI_MSG_TIPS_DOWNLOAD_INVISIBLITY,
            PLAYER_FN_UI_MSG_SHARETIP,
            
    };
    
    public static class Tips{
    	public Tips(String text, float size){
    		this.text = text;
    		this.size = size;
    	}
        public String  text;
        public float   size;
    }
    
    public YouPlayerAdapter(Context context){
        this.mContext = context;
        Arrays.sort(ContrllPanelEventFilter);
        Arrays.sort(TipsEventFilters);
        
        mEventFilter = new YouPlayerEventFilter(){
			@Override
			public void sendEvent(TimerData td) {
				LOG.v(TAG, "EventFilter send timer event:", td.event);
				sendTimerCBEvent(td);
			}
			
			public void sendTimerCBEvent(TimerData td){
			    switch(td.event){
			    case PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY:
			    	LOG.v(TAG, "sendTimerCBEvent IsPauseStatus:", getPlayerIsPauseStatus() + " ,mPlayerIsBuffering" + mPlayerIsBuffering);
                    if(!getPlayerIsPauseStatus() && !mPlayerIsBuffering && getPlayerIsReadyToPlay()){
                        setPlayerControllPanelVisibleChangedCB(false);
                    }
                    break;
                    
			    case PLAYER_FN_UI_MSG_TIP_INVISIBILITY:  
			        setPlayerTipsInVisibleCB();
			        break;
			        
			    case PLAYER_FN_UI_MSG_NET_BUFFERING_SHORT_VISIBLITY:
			        if(mPlayerIsBuffering){
			            setPlayerBufferPercentCB(playerGetResource(R.string.fullplayer_media_buffer_timeout_1));
			        }
			        break;

			    case PLAYER_FN_UI_MSG_NET_BUFFERING_LONG_VISIBLITY:
			        if(mPlayerIsBuffering){
			            setPlayerBufferPercentCB(playerGetResource(R.string.fullplayer_media_buffer_timeout_2));
			        }
			        break;

			    case PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION:
			        setPlayerSelfTimerOutCB();
			        break;
			        
		        default:
		            break;
			    }
			}
        };
        
        mEventFilter.addPolicy(PLAYER_FN_UI_EVT_PAGE_SEEK_START,You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PROGRESS_BAR,PLAYER_FN_UI_EVT_PAGE_SEEK_END);

        mEventFilter.addTimerPolicy(PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY_TIMER, PLAYER_PANEL_TIMER_SHOW_TIME, 
                                    PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY, false, 
                                    new int[]{PLAYER_FN_UI_EVT_PAGE_SEEK_START, 
                                              PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY_TIMER,
                                              PLAYER_FN_UI_MSG_RELATIVE_TOUCH_MOVE,
                                              You_Core.FN_FULL_SCREEN_BTN_RELATED_CELL,
                                              You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_PERCENT,
                                              You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_END});
        
        mEventFilter.addTimerPolicy(PLAYER_FN_UI_MSG_CONTROLL_TIPS_VISIBLITY_TIMER, PLAYER_TIPS_SHOW_TIME, 
                                    PLAYER_FN_UI_MSG_TIP_INVISIBILITY, false, 
                                    new int[]{PLAYER_FN_UI_MSG_CONTROLL_TIPS_VISIBLITY_TIMER});
        
        mEventFilter.addTimerPolicy(PLAYER_FN_UI_MSG_NET_BUFFERING_SHORT_TIMER, PLAYER_PLAY_BUFFERING_TIMEOUT_1, 
                                    PLAYER_FN_UI_MSG_NET_BUFFERING_SHORT_VISIBLITY, false, 
                                    new int[]{You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_PERCENT,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_END});
        
        mEventFilter.addTimerPolicy(PLAYER_FN_UI_MSG_NET_BUFFERING_LONG_TIMER, PLAYER_PLAY_BUFFERING_TIMEOUT_2, 
                                    PLAYER_FN_UI_MSG_NET_BUFFERING_LONG_VISIBLITY, false, 
                                    new int[]{You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_PERCENT,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_END});  
        
        mEventFilter.addTimerPolicy(PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT, PLAYER_PLAY_TIMEOUT_TIME, 
                                    PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION, false, 
                                    new int[]{PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENSUCCESS,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPEN_FAILED,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_NET_FAILED,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_TIME_OUT,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_LIVE_MEDIA_DISCONNECT,
        		You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_DATA_FAILED});   
        
        
        mEventFilter.addPolicy(PLAYER_FN_UI_MSG_PANEL_VIEW, PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY,PLAYER_FN_UI_MSG_PANEL_VIEW_CANCEL);
        mEventFilter.addPolicy(PLAYER_FN_UI_AIRONE_DEVS_DIALOG_VISIBLE, PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY,PLAYER_FN_UI_AIRONE_DEVS_DIALOG_INVISIBLE);
       
       
    }
    
    public void playerAdapterOnRequestCoreService(int ctrl, int evt, Object core_data, Object ui_data){
    	LOG.v(TAG, "playerAdapterOnRequestCoreService *****ctrl: ", ctrl);
        
        if( Arrays.binarySearch(ContrllPanelEventFilter,ctrl) >= 0 ){
            mEventFilter.filter(PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY_TIMER);
        } 
        
        if( Arrays.binarySearch(TipsEventFilters, ctrl) >= 0 ){
            mEventFilter.filter(PLAYER_FN_UI_MSG_CONTROLL_TIPS_VISIBLITY_TIMER);
        }
        
        mEventFilter.filter(ctrl);
        adapterOnRequestService(ctrl, evt, core_data, ui_data);
    }
    
    private void adapterOnRequestService(int ctrl, int evt, Object core_data, Object ui_data){
        switch(ctrl){
           
        case PLAYER_FN_UI_EVT_PAGE_SEEK_START:
        	LOG.v(TAG, "adapterOnRequestService ", "seek start");
            break;

        case PLAYER_FN_UI_EVT_PAGE_SEEK_END:
        	LOG.v(TAG, "adapterOnRequestService seek end SeekIsEnable: ", (Boolean)core_data);
            setPlayerSeekEndCB((Boolean)core_data);
            break;  
            
        case PLAYER_FN_UI_EVT_PAGE_SEEK_UP_DOWN:
        	LOG.v(TAG, "adapterOnRequestService ", "up_down");
            setPlayerSeekUpDownCB((Integer)core_data,(Integer)ui_data);
            break;
            
        case PLAYER_FN_UI_EVT_PAGE_SEEK_LEFT_RIGHT:
        	LOG.v(TAG, "adapterOnRequestService ", "left_right");
            setPlayerSeekLeftRightCB((Integer)core_data);
            break;
            
        case PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT:
            setPlayerVolumeOrBrightVisibleChangedCB();
            break;
            
        case PLAYER_FN_UI_MSG_PLAY_RELATION:
        	YouPlayerRelativeList.user_hide = !YouPlayerRelativeList.user_hide;
            setPlayerRelativeListVisibleChangedCB(YouPlayerRelativeList.isShouldShow());
            break;
            
        case PLAYER_FN_UI_MSG_PLAY_SHARE:
            setPlayerShareCB();
            break;
            
        case PLAYER_FN_UI_EVT_PAGE_CHANGE_BRIGHT:
            setPlayerBrightCB();
            break;
            
        case PLAYER_FN_UI_MSG_TRACK_TIPS_NOSUPPORT:
            setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_track_no_support),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;
        case PLAYER_FN_UI_MSG_SPEED_TIPS_NOSUPPORT:
            setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_speed_no_support),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;     
        case PLAYER_FN_UI_MSG_SHARE_TIPS_NOSUPPORT:
            setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_share_no_support),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break; 
        case PLAYER_FN_UI_MSG_TIPS_NOSUPPORT:
        	setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_no_support),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;
        case PLAYER_FN_UI_MSG_TIPS_3D_NOSUPPORT:
        	setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_3d_no_support),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;    
            
        case PLAYER_FN_UI_MSG_PREVIOUS_TIPS_NOSUPPORT:
            setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_media_no_previous),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;

        case PLAYER_FN_UI_MSG_NEXT_TIPS_NOSUPPORT:
            setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_media_no_next),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;

        case PLAYER_FN_UI_MSG_FAV_TIPS_NOSUPPORT:
            setPlayerRefreshUITipsCB(playerGetResource(R.string.favorites_add_local_media_failed),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;
        
        case PLAYER_FN_UI_MSG_SHARETIP:
            setPlayerRefreshUITipsCB(playerGetResource((Integer)ui_data),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
            break;    
        case PLAYER_FN_UI_MSG_LOCK:
            setPlayerLockCB();
            break;
        case You_Core.FN_FULL_SCREEN_BTN_3D:
        	if( ui_data != null && ui_data.equals(1) ){
        		setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_3d_opened),PLAYER_TIPS_UI_FONT_SIZE_SMALL);	
        	}else{
        		setPlayerRefreshUITipsCB(playerGetResource(R.string.fullplayer_3d_closed),PLAYER_TIPS_UI_FONT_SIZE_SMALL);
        	}
        	YouPlayerEventControler.fn_core_service_request(ctrl, evt, core_data, ui_data); 
        	break;
        case PLAYER_FN_UI_MSG_PANEL_VIEW_CANCEL:
        {
        	 mEventFilter.filter(PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY_TIMER);
        	 break;
        }
        default:
            if(ctrl< PLAYER_UI_MSG_ADAPTER_HANDLER_CONVERT){
            	LOG.v(TAG, "fn_core_service_request", "request");
            	YouPlayerEventControler.fn_core_service_request(ctrl, evt, core_data, ui_data); 
            }
            break;
        }
    }
    
    private You_full_screen_player_data_to_ui getPlayerDataToUI(int type, Object value){
    	You_full_screen_player_data_to_ui data =  new You_full_screen_player_data_to_ui();
    	data.type = type;
    	data.value = value;
    	return data;
    }
    
    
    private void setPlayerSelfTimerOutCB(){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER, You_Core.FN_PAGE_EVT_SHOW, 
                        getPlayerDataToUI(PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION, 0), 
                        null);
    }
    
    private void setPlayerBufferPercentCB(String text){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_NET_BUFFERING_VISIBLITY, text), 
        				null);
    }
    
    private void setPlayerBrightCB(){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_EVT_PAGE_CHANGE_BRIGHT, 0), 
        				null);
    }
    
    private void setPlayerShareCB(){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_PLAY_SHARE, 0),  
        				null);
    }
    
    private void setPlayerRelativeListVisibleChangedCB(boolean isVisible){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_PLAY_RELATION_VISIBLITY, isVisible), 
        				null);
    }

    private void setPlayerVolumeOrBrightVisibleChangedCB(){
    	LOG.v(TAG, "setPlayerVolumeOrBrightVisibleChangedCB visibleType:", getPlayerVolumeOrBrightIsVisible());
        if(getPlayerVolumeOrBrightIsVisible() == PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE){
            setPlayerRefreshUIVolumeOrBrightBtnCB(PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE);
            
        }else if(getPlayerVolumeOrBrightIsVisible() == PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE){
            setPlayerRefreshUIVolumeOrBrightBtnCB(PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE);
            
        }else if(getPlayerVolumeOrBrightIsVisible() == PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE){
            setPlayerRefreshUIVolumeOrBrightBtnCB(PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE);
        }
    }
    
   private void setPlayerRefreshUIVolumeOrBrightSizeCB(int size){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT_SIZE, size), 
        				null);
   }
    
   private void setPlayerRefreshUIVolumeOrBrightBtnCB(int type){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT, type), 
        				null);
   }    
    
    public void setPlayerRefreshUISurfaceCB(){
         adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        		 		getPlayerDataToUI(PLAYER_FN_UI_MSG_SURFACE_RECT, getSurfaceWidthHeight()), 
        		 		null);
    }
    
    private void setPlayerRefreshUIPlayModeCB(){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
    					getPlayerDataToUI(getMediaPlayMode(), getMediaPlayFullScreenState()), 
    					null);
    }
    
    private void setPlayerSeekEndCB(boolean seekIsEnable){
        if(!seekIsEnable){
            if(!getPlayerControllPanelIsVisible()){
                setPlayerControllPanelVisibleChangedCB(true);
            }else{
                setPlayerControllPanelVisibleChangedCB(false);
            }
        }
    }
    
    private void setPlayerSeekUpDownCB(int core_data,int value){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY, true), 
        				null);
        boolean is_twopointer = ((value & 1) == 1);
        boolean is_right = ( ((value>> 1) & 1) == 1);
       if( !is_twopointer ) {
	        if(getPlayerVolumeOrBrightIsVisible() == PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE){
	            setPlayerRefreshUIVolumeOrBrightBtnCB(!is_right?PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE:PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE);
	        }
	        else if( getPlayerVolumeOrBrightIsVisible() == PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE && is_right){
	        	setPlayerRefreshUIVolumeOrBrightBtnCB(!is_right?PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE:PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE);
	        }
	        else if( getPlayerVolumeOrBrightIsVisible() == PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE && !is_right ){
	        	setPlayerRefreshUIVolumeOrBrightBtnCB(!is_right?PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE:PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE);
	        }
	        
	        if(getPlayerVolumeOrBrightIsVisible() != PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE){
	            setPlayerRefreshUIVolumeOrBrightSizeCB(core_data);
	        }
       }else{
    	   if( mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_HARD_SOFT )
	    	   adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
	   				getPlayerDataToUI(PLAYER_FN_UI_MSG_SOFT_VOL, core_data), null);
       }
    }
    
    private void setPlayerSeekLeftRightCB(int core_data){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_EVT_PAGE_SEEK_LEFT_RIGHT, core_data), 
        				null);
        
        setPlayerRefreshUITipsCB(PLAYER_UI_MSG_SEEKBAR, 0);
        setPlayerControllPanelVisibleChangedCB(true);
    }
    
    public void setPlayerControllPanelVisibleChangedCB(boolean isVisible){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY, isVisible), 
        				null);
    }
    
    private void setPlayerTipsInVisibleCB(){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_TIP_INVISIBILITY, false),
        				null);
    }
    
    private void setPlayerRefreshUITipsCB(String text, float size){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
        				getPlayerDataToUI(PLAYER_FN_UI_MSG_TIP_VISIBILITY_REFRESH, new Tips(text, size)),
        				null);
    }
    
    
    private void setPlayerLockCB(){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER,You_Core.FN_PAGE_EVT_SHOW, 
                getPlayerDataToUI(PLAYER_FN_UI_MSG_LOCK, 0),
                null);
        setPlayerControllPanelVisibleChangedCB(false);
    }
    
    public boolean playerCallBack(int page_id, int page_action, Object core_data, Object ui_data){
        boolean result = false;
        if(page_id == You_Core.FN_PAGE_FULL_SCREEN_PLAYER ){
            switch(page_action){
            case You_Core.FN_PAGE_EVT_SHOW:
                result = playerCallBackShow(page_id, page_action, core_data, ui_data);
                break;
                
            case You_Core.FN_PAGE_DATA_FAV:
                result = playerCallBackFav(page_id, page_action, core_data, ui_data);
                break;

            case You_Core.FN_PAGE_EVT_WAITING:
                result = playerWaitingCallBack(true);
                break;
                
            case You_Core.FN_PAGE_EVT_CANCEL_WAITING:
                result = playerWaitingCallBack(false);
                break;
                
            default:
                break;
            }
        	
        }
        return result;
    }
    
    public boolean playerWaitingCallBack(boolean isWaiting){
        adapterCallback(You_Core.FN_PAGE_FULL_SCREEN_PLAYER, You_Core.FN_PAGE_EVT_SHOW,  
                        getPlayerDataToUI(PLAYER_FN_UI_MSG_WAITTING, isWaiting), null);
        return true;
    }
    
    public void CB_V(int page_id, int page_action, You_full_screen_player_data_to_ui coredata, Object ui_data){
    	if( coredata.type < showType.length && coredata.type != You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PROGRESS_BAR)
    		LOG.i( TAG, "receive msg:", " page_id: " + page_id + " | page_action: " + page_action + " | type: " + coredata.type + "->" + showType[coredata.type] + " | value: " + coredata.value);
    }
    
    public void CB_MediaInfo(You_player_media_info coreData){
    	LOG.v( TAG, "receive msg", "is_system_player: " + coreData.is_system_player);
    	LOG.v( TAG, "receive msg", "width: " + coreData.width);
    	LOG.v( TAG, "receive msg", "height: " + coreData.height);
    	LOG.v( TAG, "receive msg", "duration: " + coreData.duration);
    	LOG.v( TAG, "receive msg", "start_time: " + coreData.start_play_time);
    	LOG.v( TAG, "receive msg", "url: " + coreData.url);
    	LOG.v( TAG, "receive msg", "mediatype: " + coreData.mediatype);
    	LOG.v( TAG, "receive msg", "ds: " + coreData.ds);
    	LOG.v( TAG, "receive msg", "isEpisode: " + coreData.isEpisode);
    	LOG.v( TAG, "receive msg", "audio_mode: " + coreData.audio_mode);
    	LOG.v( TAG, "receive msg", "video_mode: " + coreData.video_mode);
    	LOG.v( TAG, "receive msg", "previous: " + coreData.is_have_pre_online_media);
    	LOG.v( TAG, "receive msg", "next: " + coreData.is_have_next_online_media);
    	LOG.v( TAG, "receive msg", "can_fav: " + coreData.can_fav);
    	LOG.v( TAG, "receive msg", "can_cache: " + coreData.can_cache);
    	LOG.v( TAG, "receive msg", "definition: " + coreData.definition);
    	LOG.v( TAG, "receive msg", "current_dfnt: " + coreData.current_dfnt);
    }
    
    public boolean playerCallBackShow(int page_id, int page_action, Object core_data, Object ui_data){
    	
    	You_full_screen_player_data_to_ui coredata = (You_full_screen_player_data_to_ui) core_data;
        if( coredata == null || mEventFilter.filter(coredata.type) ){
            return true;
        }

        if( Arrays.binarySearch(ContrllPanelEventFilter,coredata.type) >= 0 ){
            mEventFilter.filter(PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY_TIMER);
        }
        
        CB_V(page_id, page_action, coredata, ui_data);
        
        switch(coredata.type){
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SET_PLAYER_ENGINE_TYPE:
                if(coredata.value != null){
                    try {
                    	You_player_media_info media_info  = (You_player_media_info) coredata.value;
                        CB_MediaInfo(media_info);
                        // 0: YOUPALYER 1: SYSTEMPLAYER
                        setPlayerIsSystemMediaPlayer(media_info.is_system_player!=Player_type.PLAYER_TYPE_SOFT);
                        setMediaInfoMediaType(media_info.mediatype);
                        setMediaInfoIsLive(playerIntToBoolean(media_info.ds));
                        setMediaInfoCanFav(media_info.can_fav);
                        setMediaInfoCanCache(media_info.can_cache);
                        setMediaInfoHasPrevious(playerIntToBoolean(media_info.is_have_pre_online_media));
                        setMediaInfoHasNext(playerIntToBoolean(media_info.is_have_next_online_media));
                        mPlayerAdapterData.put(PLAYER_EVENT_IS_3D, media_info.is_3D);
                        
                        mMediaInfo = media_info;
                        //L.v( TAG, " fixQuality", mMediaInfo.definition+","+mMediaInfo.current_dfnt);
                        mMediaInfo.fixQuality();
                        //L.v( TAG, " fixQuality", mMediaInfo.definition+","+mMediaInfo.current_dfnt);
                        if(getPlayerIsVideoMedia() || getPlayerIsOnlineAudio()){
                            setPlayerPlayMode(media_info.video_mode);
                        }
                        
                        if(getPlayerIsLocalAudio()){
                            setPlayerPlayMode(media_info.audio_mode);
                        }
                        
                        setPlayerRefreshUIPlayModeCB();
                        setPlayerControllPanelVisibleChangedCB(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENSUCCESS:
                setPlayerIsShowStop(false);
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_READY_TO_PLAY:
                setPlayerIsReadyToPlay(true);
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_MEDIA_INFO:
                if(coredata.value != null){
                	You_player_media_info media_info  = (You_player_media_info) coredata.value;
	                mMediaInfo = media_info;
	                mMediaInfo.fixQuality();
	                try{
	                    CB_MediaInfo(media_info);
	                    setMediaInfoWidth(media_info.width);
	                    setMediaInfoHeight(media_info.height);
	                    setMediaInfoUrl(media_info.url);
	                    setMediaInfoIsEpisode(playerIntToBoolean(media_info.isEpisode));
	                    
	                    if(getPlayerIsBackground() && getPlayerIsLocalAudio()){
	                        setPlayerPlayState(You_full_screen_player_data_to_ui.Cls_you_player_status.PlayingStatus);
	                    }
	                    
	                    adapterCallback(page_id,page_action,coredata,ui_data);
	                    
	                    if( getPlayerIsVideoMedia()){
	                        extSetSurfaceScale(getVideoFullScaleState());
	                        setPlayerRefreshUISurfaceCB();
	                    }
	                }catch(Exception e){
	                    e.printStackTrace();
	                }
                }
                return true; 
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PLAY_BTN:
            	if(coredata.value != null){
            		LOG.v(TAG, "fn_page_full_player_show_play_btn:", (Integer)coredata.value);
            		setPlayerPlayState((Integer)coredata.value);
            		if(getPlayerIsPauseStatus()){
            		    setPlayerControllPanelVisibleChangedCB(true);
            		}
            	}
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PLAY_TO_END:
            	// value : 0 正常播放完毕   1  多片中的一片播放完        3 seek 多片到最后
            	int value = 0;
            	if( coredata.value != null ){
            		value = (Integer)coredata.value;
            	}
            	// 其中一片播放完不用处理 
            	if( value == 3 ){
            		coredata.value=0;
            	}
            	LOG.v(TAG,"fn_full_screen_btn_stop to end value:" , value);
                break;
                
            case  You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_CHANGE_MODE:
                if(coredata != null){
                    setPlayerPlayMode((Integer)coredata.value);
                    setPlayerRefreshUITipsCB(getPlayerTipsText(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_CHANGE_MODE), PLAYER_TIPS_UI_FONT_SIZE_SMALL);
                    setPlayerRefreshUIPlayModeCB();
                    if(getPlayerIsVideoMedia()){
                        extSetSurfaceScale(getVideoFullScaleState());
                        setPlayerRefreshUISurfaceCB();
                    }
                }
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_START:
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_PERCENT:
                mEventFilter.filter(PLAYER_FN_UI_MSG_NET_BUFFERING_SHORT_TIMER);
                mEventFilter.filter(PLAYER_FN_UI_MSG_NET_BUFFERING_LONG_TIMER);
                if(!mPlayerIsBuffering){
                    mPlayerIsBuffering = true;
                    setPlayerControllPanelVisibleChangedCB(true);
                }
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_END:
                mPlayerIsBuffering = false;
                if(!getPlayerIsPauseStatus() && getPlayerIsReadyToPlay()){
                    setPlayerControllPanelVisibleChangedCB(false);
                }
                break;  
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_STOP:
                //0:over 1:frags
                if(coredata.value != null && YouPlayerFullScreenPlayer.instance != null){
                    setPlayerIsShowStop(true);
                    int stop = (Integer)coredata.value;
                    YouPlayerFullScreenPlayer.instance.doPlayerStopAction(stop);//同频调用
                    if(stop == 0){
                        setPlayerPlayState(You_full_screen_player_data_to_ui.Cls_you_player_status.PauseStatus);
                    }
                }
                break;
                
            case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENNING:
                if(getPlayerIsOnlineVideo() || getPlayerIsOnlineAudio()){
                    mEventFilter.filter(PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT);
                }
                break;
                
            default:
                break;
        }
        adapterCallback(page_id,page_action,core_data,ui_data);
        return true;
    }
    
    public boolean playerCallBackFav(int page_id, int page_action, Object core_data, Object ui_data){
        String favText = ""; 
        setPlayerRefreshUITipsCB(favText, PLAYER_TIPS_UI_FONT_SIZE_SMALL);
        return true;
    }
    
    public void destoryPlayerAdapter(){
    	mEventFilter.removeAll();
    	mPlayerAdapterData.clear();
    }
    
    private String playerGetResource(int id){
        String res = "";
        if(mContext != null){
            res =  mContext.getResources().getString(id);
        }
        return res;
    }
    
    private String getPlayerTipsText(int ctrl){
        String tips = "";
        switch(ctrl){
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_CHANGE_MODE:
            switch(getMediaInfoMediaType()){
            case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA:    
            case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA:
                tips =  getPlayerVideoTips();
                break;
                
            case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_AUDIO_MEDIA:
                break;
                
            case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_AUDIO_MEDIA:
                tips = getPlayerAudioTips();
                break;     
                
            default:
                break;
           }
           break;
            
        case You_Core.FN_FULL_SCREEN_BTN_PREVIOUS:
            if(getMediaInfoHasPrevious()){
                tips =  playerGetResource(R.string.fullplayer_media_previous);
            }else{
                tips =  playerGetResource(R.string.fullplayer_media_no_previous);
            }
            break;
            
        case You_Core.FN_FULL_SCREEN_BTN_NEXT:
            tips =  playerGetResource(R.string.fullplayer_media_next);
            if(getMediaInfoHasNext()){
                tips =  playerGetResource(R.string.fullplayer_media_previous);
            }else{
                tips =  playerGetResource(R.string.fullplayer_media_no_previous);
            }
            break;   
         
        default:
            break;
        }
        
    	return tips;
    }
    
    private String getPlayerVideoTips(){
        switch(getVideoFullScaleState()){
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_FULL_SCR:
            return playerGetResource(R.string.fullplayer_scale_tips2);
            
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_ORIGINAL_SCR:
            return playerGetResource(R.string.fullplayer_scale_tips0);
            
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_ASPECT_FULL_SCR:
            return playerGetResource(R.string.fullplayer_scale_tips1);
       
        default:
            break;
        }
        return "";
    }
 
    private String getPlayerAudioTips(){
        
        switch(getAudioLoopState()){
        case You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_ENTIRE_MODE:
            return playerGetResource(R.string.fullplayer_audio_loop_whole);
            
        case You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_RANDOM_MODE:
            return playerGetResource(R.string.fullplayer_audio_loop_random);
            
        case You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_SINGLE_MODE:
            return playerGetResource(R.string.fullplayer_audio_loop_self);
       
        default:
            break;
        }
        return "";
    }

    public int extGetScreenWidth() {
        try {
            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            if(width > height){
                return width;
            }else{
                return height;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 800;
        }
    }

    public int extGetScreenHeight() {
        try {
            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            if(width > height){
                return height;
            }else{
                return width;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 480;
        }
    }
    
    public void extSetSurfaceScale(int mode) {

        int nMediaWidth = getMediaInfoWidth();
        int nMediaHeight = getMediaInfoHeight();
        int nScreenWidth = extGetScreenWidth();
        int nScreenHeight = extGetScreenHeight();

        if (nMediaWidth == 0 || nMediaHeight == 0) {
            nMediaWidth = nScreenWidth;
            nMediaHeight = nScreenHeight;
        }

        if (nScreenWidth < nScreenHeight) {
            int temp = nScreenWidth;
            nScreenWidth = nScreenHeight;
            nScreenHeight = temp;
        }
        switch (mode) {
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_ORIGINAL_SCR:
            if (nScreenWidth < nMediaWidth || nScreenHeight < nMediaHeight) {
                float previewScale = (float) nScreenWidth / nScreenHeight;
                float mediaScale = (float) nMediaWidth / nMediaHeight;
                if (previewScale > mediaScale) {
                    setSurfaceWidthHeight((int) (nScreenHeight * mediaScale), nScreenHeight);
                } else {
                    setSurfaceWidthHeight(nScreenWidth, (int) (nScreenWidth / mediaScale));
                }
            } else {
                setSurfaceWidthHeight(nMediaWidth, nMediaHeight);
            }
            break;
            
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_ASPECT_FULL_SCR:
            float previewScale = (float) nScreenWidth / nScreenHeight;
            float mediaScale = (float) nMediaWidth / nMediaHeight;
            if (previewScale > mediaScale) {
                setSurfaceWidthHeight((int) (nScreenHeight * mediaScale), nScreenHeight);
            } else {
                setSurfaceWidthHeight(nScreenWidth, (int) (nScreenWidth / mediaScale));
            }
            break;
            
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_FULL_SCR:
            setSurfaceWidthHeight(nScreenWidth, nScreenHeight);
            break;

        default:
            break;
        }
    }
    
    
    
    private void setSurfaceWidthHeight(int w, int h){
    	mPlayerSurfaceRect.set(0, 0, w, h);
    }
    private Rect getSurfaceWidthHeight(){
        return mPlayerSurfaceRect;
    }
    
    private int getMediaPlayFullScreenState() {
        int FullScreenState = You_player_media_info.Cls_you_player_render_mode.VIDEO_FULL_SCR;
        int mediaType = getMediaInfoMediaType();
        switch(mediaType){
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA:    
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA:
            FullScreenState = getVideoFullScaleState();
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_AUDIO_MEDIA:
            FullScreenState = getAudioLoopState();
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_AUDIO_MEDIA:
            FullScreenState = getVideoFullScaleState();
            break;     
            
        default:
            break;
        }
        LOG.v(TAG, "getMediaPlayFullScreenState FullScreenState: ", FullScreenState);
        return FullScreenState;
    }

    private void setPlayerPlayMode(int mode){
        int mediaType = getMediaInfoMediaType();
        LOG.v(TAG, "setPlayerPlayMode mediaType ", mediaType);
        switch(mediaType){
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA:    
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA:
            setVideoFullScaleState(mode);
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_AUDIO_MEDIA:
            setAudioLoopState(mode);
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_AUDIO_MEDIA:
            setVideoFullScaleState(mode);
            break;     
            
        default:
            break;
        }
    }
    
    private int getMediaPlayMode() {
        int playMode = PLAYER_FN_UI_MSG_PLAY_MODE_VIDEO;
        int mediaType = getMediaInfoMediaType();
        switch(mediaType){
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA:    
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA:
            playMode = PLAYER_FN_UI_MSG_PLAY_MODE_VIDEO;
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_AUDIO_MEDIA:
            playMode = PLAYER_FN_UI_MSG_PLAY_MODE_AUDIO;
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_AUDIO_MEDIA:
            playMode = PLAYER_FN_UI_MSG_PLAY_MODE_VIDEO;
            break;     
            
        default:
            break;
        }
        LOG.v(TAG, "getMediaPlayMode playMode: ", playMode);
        return playMode;
    }
    
    public int getVideoFullScaleState(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_PLAYER_VIDEO_SCREEN)){
            return (Integer)this.mPlayerAdapterData.get(PLAYER_MEDIA_INFO_PLAYER_VIDEO_SCREEN);
        }
        return You_player_media_info.Cls_you_player_render_mode.VIDEO_ASPECT_FULL_SCR;
    }
    
    private void setVideoFullScaleState(int scaleState){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_PLAYER_VIDEO_SCREEN, scaleState);
    }

    public int getAudioLoopState(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_PLAYER_AUDIO_LOOP)){
            return (Integer)this.mPlayerAdapterData.get(PLAYER_MEDIA_INFO_PLAYER_AUDIO_LOOP);
        }
        return You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_ENTIRE_MODE;
    }
    
    private void setAudioLoopState(int loopState){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_PLAYER_AUDIO_LOOP, loopState);
    }
    
    public boolean getMediaInfoIsLive(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_PLAYER_IS_LIVE)){
            return (Boolean)this.mPlayerAdapterData.get(PLAYER_MEDIA_INFO_PLAYER_IS_LIVE);
        }
        return false; 
    }
    
    private void setMediaInfoIsLive(boolean isLive){
    	LOG.v(TAG, "setMediaInfoIsLive isLive:", isLive);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_PLAYER_IS_LIVE, isLive);
    }
    
    private void setMediaInfoHasPrevious(boolean hasPrevious){
    	LOG.v(TAG, "setMediaInfoHasPrevious hasPrevious:", hasPrevious);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_HAS_PREVIOUS, hasPrevious);
    }
    
    public boolean getMediaInfoHasPrevious(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_HAS_PREVIOUS)){
            return (Boolean)this.mPlayerAdapterData.get(PLAYER_MEDIA_INFO_HAS_PREVIOUS);
        }
        return false;
    }
    
    private void setMediaInfoHasNext(boolean hasNext){
    	LOG.v(TAG, "setMediaInfoHasNext hasNext:", hasNext);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_HAS_NEXT, hasNext);
    }
    public void setMediaInfoIs3D(boolean flag)
    {
    	LOG.v(TAG, "setMediaInfoIs3D hasNext:", flag);
        mPlayerAdapterData.put(PLAYER_EVENT_IS_3D, flag);
    }
    public boolean getMediaInfoIs3D()
    {
    	if(mPlayerAdapterData.containsKey(PLAYER_EVENT_IS_3D)){
            return (Boolean)this.mPlayerAdapterData.get(PLAYER_EVENT_IS_3D);
        }
        return false;
    }
    public boolean getMediaInfoHasNext(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_HAS_NEXT)){
            return (Boolean)this.mPlayerAdapterData.get(PLAYER_MEDIA_INFO_HAS_NEXT);
        }
        return false;
    }
    
    private void setMediaInfoIsEpisode(boolean isEpisode){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_IS_EPISODE, isEpisode);
    }
    
    
    public boolean getMediaInfoIsEpisode(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_IS_EPISODE)){
            return (Boolean)this.mPlayerAdapterData.get(PLAYER_MEDIA_INFO_IS_EPISODE);
        }
        return false;
    }
    
    private void setMediaInfoUrl(String url){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_URL, url);
    }
    
    public String getMediaInfoUrl(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_URL)){
            return (String)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_URL);
        }
        return "";
    }
    
    private void setMediaInfoMediaType(int currentMediaType){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_MEDIA_TYPE, currentMediaType);
    }
    
    public int getMediaInfoMediaType(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_MEDIA_TYPE)){
            return (Integer)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_MEDIA_TYPE);
        }
        return -1;
    }
    
    public void setMediaInfoWidth(int width){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_WIDTH, width);
    }
    
    public int getMediaInfoWidth(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_WIDTH)){
            return (Integer)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_WIDTH);
        }
        return extGetScreenWidth();
    }
    
    public void setMediaInfoHeight(int height){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_HEIGHT, height);
    }
    
    public int getMediaInfoHeight(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_HEIGHT)){
            return (Integer)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_HEIGHT);
        }
        return extGetScreenHeight();
    }
    
    public void setMediaInfoDuration(long total){
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_DURATION, total);
    }
    
    public long getMediaInfoDuration(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_DURATION)){
            return (Long)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_DURATION);
        }
        return 0;
    }
    
    public void setMediaInfoCanFav(boolean isFav){
    	LOG.v(TAG, "setMediaInfoCanFav isFav:", isFav);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_CAN_FAV, isFav);
    }
    
    public boolean getMediaInfoCanFav(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_CAN_FAV)){
            return (Boolean)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_CAN_FAV);
        }
        return false;
    }
    
    public void setPlayerIsShowStop(boolean isStop){
    	LOG.v(TAG, "setPlayerShowIsStop isStop:", isStop);
        mPlayerAdapterData.put(PLAYER_EVENT_IS_SHOW_STOP, isStop);
    }
    
    public boolean getPlayerIsShowStop(){
        if(mPlayerAdapterData.containsKey(PLAYER_EVENT_IS_SHOW_STOP)){
            return (Boolean)mPlayerAdapterData.get(PLAYER_EVENT_IS_SHOW_STOP);
        }
        return false;
    }
    
    public void setMediaInfoCanCache(boolean isCache){
    	LOG.v(TAG, "setMediaInfoCanCache isCache:", isCache);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_CAN_CACHE, isCache);
    }
    
    public boolean getMediaInfoCanCache(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_CAN_CACHE)){
            return (Boolean)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_CAN_CACHE);
        }
        return false;
    }
    
    private int getPlayerVolumeOrBrightIsVisible(){
        if(YouPlayerFullScreenPlayer.instance != null){
            return YouPlayerFullScreenPlayer.instance.getPlayerSeekBarVolumeOrBrightIsVisible();
        }else{
            return PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE;
        }
    }
    
    private boolean getPlayerControllPanelIsVisible(){
        if(YouPlayerFullScreenPlayer.instance != null){
            return YouPlayerFullScreenPlayer.instance.isAllControllPanelIsVisible();
        }
        return false;
    }
    
    private boolean getPlayerIsBackground(){
        if(YouPlayerFullScreenPlayer.instance != null){
            return YouPlayerFullScreenPlayer.instance.getPlayerIsBackground();
        }
        return false;
    }

    public boolean getPlayerIsPauseStatus(){
        if( getPlayerPlayState() ==  You_full_screen_player_data_to_ui.Cls_you_player_status.PauseStatus){
            return true;
        }
        return false;
    }
    
    private void setPlayerPlayState(int playStatus){
    	LOG.v(TAG, "setPlayerPlayState playStatus:", playStatus);
        mPlayerPlayStatus = playStatus;
    }
    
    public int getPlayerPlayState(){
        return this.mPlayerPlayStatus;
    }
    
    private void setPlayerIsSystemMediaPlayer(boolean isSystemPlayer){
    	LOG.v(TAG, "setPlayerIsSystemMediaPlayer isSystemPlayer: ", isSystemPlayer);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_SYSTEM_PLAYER, isSystemPlayer);
    }
    
    public boolean getPlayerIsSystemMediaPlayer(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_SYSTEM_PLAYER)){
            return (Boolean)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_SYSTEM_PLAYER);
        }
        return false;
    }
    
    private void setPlayerIsReadyToPlay(boolean readyToPlay){
    	LOG.v(TAG, "setPlayerReadyToPlay readyToPlay:", readyToPlay);
        mPlayerAdapterData.put(PLAYER_MEDIA_INFO_READY_TO_PLAY, readyToPlay);
    }

    public boolean getPlayerIsReadyToPlay(){
        if(mPlayerAdapterData.containsKey(PLAYER_MEDIA_INFO_READY_TO_PLAY)){
            return (Boolean)mPlayerAdapterData.get(PLAYER_MEDIA_INFO_READY_TO_PLAY);
        }
        return false;
    }
    
    public boolean getPlayerIsOnlineAudio(){
        return (getMediaInfoMediaType() == You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_AUDIO_MEDIA);
    }
    
    public boolean getPlayerIsLocalAudio(){
        return (getMediaInfoMediaType() == You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_AUDIO_MEDIA);
    }
    
    public boolean getPlayerIsOnlineVideo(){
        return (getMediaInfoMediaType() == You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA);
    }
    
    public boolean getPlayerIsLocalVideo(){
        return (getMediaInfoMediaType() == You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA);
    }
    
    public boolean getPlayerIsVideoMedia(){
        return (getPlayerIsOnlineVideo() || getPlayerIsLocalVideo());
    }
    
    public boolean playerIntToBoolean(int value){
        return (value >= 1 ? true : false);
    }
    
    public abstract void adapterCallback(int page_id, int page_action, Object core_data, Object ui_data);
    
    public static String[] showType = {
        "FN_PAGE_FULL_PLAYER_SHOW_BASE ",
        "FN_PAGE_FULL_PLAYER_SHOW_OPENNING ", 
        "FN_PAGE_FULL_PLAYER_SHOW_OPENSUCCESS ",
        "FN_PAGE_FULL_PLAYER_SHOW_OPEN_FAILED ",
        "FN_PAGE_FULL_PLAYER_SHOW_NO_DATA_FAILED ",
        "FN_PAGE_FULL_PLAYER_SHOW_READY_TO_PLAY ",
        "FN_PAGE_FULL_PLAYER_SHOW_VIDEO_VIEW ", 
        "FN_PAGE_FULL_PLAYER_SHOW_VIDEO_RENDER ", 
        "FN_PAGE_FULL_PLAYER_SHOW_MEDIA_INFO ", 
        "FN_PAGE_FULL_PLAYER_SHOW_PLAY_BTN ", 
        "FN_PAGE_FULL_PLAYER_SHOW_PROGRESS_BAR ", 
        "FN_PAGE_FULL_PLAYER_SHOW_SUBTITLE ", 
        "FN_PAGE_FULL_PLAYER_SHOW_LYRIC ", 
        "FN_PAGE_FULL_PLAYER_SHOW_BUFFER_START ", 
        "FN_PAGE_FULL_PLAYER_SHOW_BUFFER_END ",
        "FN_PAGE_FULL_PLAYER_SHOW_BUFFER_PERCENT ", 
        "FN_PAGE_FULL_PLAYER_SHOW_SEEK_THUMBNAIL ", 
        "FN_PAGE_FULL_PLAYER_SHOW_STOP ", 
        "FN_PAGE_FULL_PLAYER_SHOW_PLAY_TO_END ", 
        "FN_PAGE_FULL_PLAYER_ALL_AD",
        "FN_PAGE_FULL_PLAYER_SHOW_AD_VIEW ",
        "FN_PAGE_FULL_PLAYER_HIDE_AD_VIEW ",
        "FN_PAGE_FULL_PLAYER_HIDE_ALL_AD_VIEWS ",
        "FN_PAGE_FULL_PLAYER_SET_PLAYER_ENGINE_TYPE ", 
        "FN_PAGE_FULL_PLAYER_SET_SUBTITLE_TYPE ",
        "FN_PAGE_FULL_PLAYER_SET_MEDIA_TYPE ",
        "FN_PAGE_FULL_PLAYER_CHANGE_MODE ",
        "FN_PAGE_FULL_PLAYER_AUDIO_SUBTITLE ",
        "FN_PAGE_FULL_PLAYER_SHOW_NO_NET_FAILED",
        "FN_PAGE_FULL_PLAYER_SHOW_CONN_TIME_OUT",
        "FN_PAGE_FULL_PLAYER_SHOW_CONN_RESET",
        "FN_PAGE_FULL_PLAYER_SHOW_NO_NEXT_PREVIOUD_FILE",
        "FN_PAGE_FULL_PLAYER_SHOW_LIVE_MEDIA_DISCONNECT",
        "FN_PAGE_FULL_PLAYER_SHOW_FILE_NOT_EXIST",
        "FN_PAGE_FULL_PLAYER_RELATED_VIDEO",
        "FN_PAGE_FULL_PLAYER_DO_SEEK_PREVIEW",
        "... ",
        "... ",
    };
}
