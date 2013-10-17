package com.youplayer.player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.youplayer.core.You_Core;
import com.youplayer.core.mediainfo.YouPlayerBitMap;
import com.youplayer.core.mediainfo.YouPlayerLyrics;
import com.youplayer.core.struct.You_full_screen_player_data_to_ui;
import com.youplayer.core.struct.You_full_screen_tips;
import com.youplayer.core.struct.You_page_full_player_show_type;
import com.youplayer.core.struct.You_player_media_info;
import com.youplayer.core.struct.You_player_media_info.Cls_you_media_type_e;
import com.youplayer.core.struct.You_player_media_info.Player_type;
import com.youplayer.player.frame.YouPlayerEventControler;
import com.youplayer.player.frame.YouPlayerViewControler;
import com.youplayer.player.fullplayer.YouLyricView;
import com.youplayer.player.fullplayer.YouPlayerAdapter;
import com.youplayer.player.fullplayer.YouPlayerAudioSubtitle;
import com.youplayer.player.fullplayer.YouPlayerBrightControl;
import com.youplayer.player.fullplayer.YouPlayerEvent;
import com.youplayer.player.fullplayer.YouPlayerException;
import com.youplayer.player.fullplayer.YouPlayerRelativeList;
import com.youplayer.player.fullplayer.YouPlayerSlideView;
import com.youplayer.player.fullplayer.YouPlayerSubtitleView;
import com.youplayer.player.fullplayer.YouPlayerSurfaceView;
import com.youplayer.player.fullplayer.YouPlayerVolControl;
import com.youplayer.player.local.YouPlayerInterfaceFile.MediaInfoCallBack;
import com.youplayer.player.local.YouPlayerMediaFile;
import com.youplayer.util.LOG;
import com.youplayer.util.YouOnGesture;
import com.youplayer.util.YouPhonePowerListener;
import com.youplayer.util.YouPhonePowerUtil;
import com.youplayer.util.YouPlayerSensorObserver;
import com.youplayer.util.YouUtility;


public class YouPlayerFullScreenPlayer extends YouPlayerViewControler implements YouPlayerEvent, YouPlayerSensorObserver {
    private static final String TAG = "FullScreenPlayer";
    
    public static YouPlayerFullScreenPlayer instance;
    public static SurfaceView mPlayerSysSurfaceView;
    public static SurfaceView mPlayerYouSurfaceView;
    
//    public RelativeLayout mPlayerMediaVideoLayout;
    public RelativeLayout mPlayerMediaAudioLayout;
    public RelativeLayout mPlayerMediaRadioLayout;
//    public RelativeLayout mPlayerMediaAironeLayout;   
    
    private RelativeLayout   mPlayerBackgroundLogo;
//    private TextView    mPlayerTopSysTimeText;
//    private ImageView   mPlayerTopImgvBattery;
//    private TextView    mPlayerTopDecodeTypeText;
    
//    private TextView mPlayerBtmMediaName;
//    private TextView mPlayerBtmPlayState;
//    private TextView mPlayerBtmTimeMin;
//    private TextView mPlayerBtmTimeMax;
    private SeekBar  mPlayerBtmPlaySeekbar;
//    private ImageButton mPlayerBtmImgbMore;
//    private ImageButton mPlayerBtmImgbDlna;
//    private ImageButton mPlayerBtmImgbQuality;
    private ImageButton mPlayerBtmImgbPrevious;
    private ImageButton mPlayerBtmImgbPlay;
    private ImageButton mPlayerBtmImgbNext;
//    private ImageButton mPlayerBtmImgbFullScreen;
    private TextView    mPlayerCenterTips;
    
    public  GestureOverlayView  mPlayerGestureView;
    private YouPlayerVolControl    mPlayerSeekbarVolume;
    public  YouPlayerBrightControl mPlayerSeekbarBrightness;
//    private YouPlayerRelativeList  mPlayerRelativeList;
//    private ImageButton         mPlayerRelativeBtn;
//    private View                mPlayerRelativeContainer;
    
    private YouPlayerSubtitleView        mPlayerSubtitleView;
    private YouLyricView           mPlayerLyricView;
    
    private Handler         mPlayerEventHandler;
    private YouPhonePowerUtil  mPlayerPhonePower;
    private YouPlayerMediaFile       mMediaFile;
    private InitMediaTask   mInitMediaTask = null;
    public  YouPlayerAdapter   mPlayerAdapter;
//    private PlayerAdManager mPlayerAdManager;
    private PopupWindow     mPopupWindowSpeed = null;
    private BroadcastReceiver   sdcardEjectReceiver;    
    private BroadcastReceiver   phoneStateReceiver;
    private NotificationManager mPlayerNotificationManager;
    private Notification        mPlayerNotification;
    private YouPlayerAudioSubtitle mPlayerAudioSubtitle;
    private RelativeLayout      mPlayerLockView;
    private YouPlayerSlideView         mPlayerUnlockImgBtn;
    private YouPlayerSurfaceView.OnCreateExecuted mOnSurfaceCreatedExecute;
    
    
    private int mSpeed = 1;
	
    
    private int[] BatteryDrawableArray = {
            R.drawable.youplayer_fullplayer_battery_0,R.drawable.youplayer_fullplayer_battery_1,R.drawable.youplayer_fullplayer_battery_2,R.drawable.youplayer_fullplayer_battery_3,
            R.drawable.youplayer_fullplayer_battery_4,R.drawable.youplayer_fullplayer_battery_5,R.drawable.youplayer_fullplayer_battery_6,R.drawable.youplayer_fullplayer_battery_7,
            R.drawable.youplayer_fullplayer_battery_8,R.drawable.youplayer_fullplayer_battery_9,R.drawable.youplayer_fullplayer_battery_10,
    };
    
    private int     mPlayerBatteryPercent = PLAYER_DEFAULT_UNKNOW_VALUE;
    private boolean mPlayerIsSeeking = false;
    private boolean mPlayerIsGoToBackground = false;
    private boolean playerViewHasAdded = false;
    private String  mPlayerFileName = "";
    private boolean mPlayerIsLocked = false;
    
    //弹出框的销毁
    private List<IStopExec> onDestoryExec = new ArrayList<IStopExec>(); 
    private List<IStopExec> onNewExec = new ArrayList<IStopExec>();
	
	private int mSurface_width;
	private int mSurface_height;
	public YouPlayerFullScreenPlayer(Context context, Object core_data, Object ui_data) {
		super(context, core_data, ui_data);

		instance = this;
		
		loadPlayerViews(context);
		
	}

	private void loadPlayerViews(Context context) {
		try {
            mView = LayoutInflater.from(context).inflate(R.layout.youplayer_fullplayer, null);
            initPlayerViews();
            initializeControls();
            
            LOG.v(TAG, "loadPlayerViews", "load success");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.e(TAG, "loadPlayerViews", "load failed");
            playerExitOnRequest(); 
        }
	}
	
	private void reloadPlayerViews(Context context) {
        try {
            reInitPlayerViews();
            initializeControls();
//    		AirOne.getInstance().resetChecked();
//        	mPlayerBtmImgbDlna.setSelected(false);

        	LOG.v(TAG, "reLoadPlayerViews", "reload success");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.e(TAG, "reLoadPlayerViews", "reload failed");
            playerExitOnRequest(); 
        }
    }
	
    private void setPlayerViewsInitalizeValue() {
        try {
            reInitPlayerViews();
            setBackgroundLogoIsShow(true);

            LOG.v(TAG, "setPlayerViewsInitalizeValue", "Initalize success");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.e(TAG, "setPlayerViewsInitalizeValue", "Initalize failed");
            playerExitOnRequest();  
        }
    }
    
	private void initializeControls() throws YouPlayerException{
        initPlayerHandler();
        initPlayerOnClickListener();
        initPlayerBatteryPowerListener();
        initPlayerGestureListener();
//        initializeAdManager();
        initSdcardStateReceiver();
//        initShakeListener();
        initScreenOffRegisters();
        initTelephoneReceiver();
        removeNotification();
        initAudioThumbnailPos();
        initSysTimeUpdateTimer();
        
        mView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			
//				if( ( v != mPlayerBtmMediaName && hasFocus ) )
//					mPlayerBtmMediaName.requestFocus();
//				else if( v == mPlayerBtmMediaName && !hasFocus )
//					mPlayerBtmMediaName.requestFocus();
			}
		});
	}
	
	private void initSysTimeUpdateTimer() {
		final You_full_screen_player_data_to_ui uidata = new You_full_screen_player_data_to_ui();
		uidata.type  = PLAYER_FN_UI_MSG_UPDATE_SYSTIME;
		new Timer().schedule(new TimerTask(){

			@Override
			public void run() {
				
				playerSendMessage(PLAYER_UI_MSG_ADAPTER_HANDLER_CONVERT,uidata,0);
			}
			
		},1000,1000);
	}

    private void initPlayerHandler() throws YouPlayerException{
        mPlayerEventHandler = new Handler(mContext.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                 playerMessageHandler(msg);
            }
	    };
	    
	    mPlayerAdapter = new YouPlayerAdapter(mContext){
	        @Override
	        public void adapterCallback(int page_id, int page_action, Object core_data,Object ui_data) {
	            playerSendMessage(PLAYER_UI_MSG_ADAPTER_HANDLER_CONVERT, (You_full_screen_player_data_to_ui)core_data, 0);
	        }
	    };
    }

	private void playerMessageHandler(Message msg) {
	    switch(msg.what){
        case PLAYER_UI_MSG_ADAPTER_HANDLER_CONVERT:
            doPlayerHandlerCallBackAction((You_full_screen_player_data_to_ui)msg.obj, null);
            break;

        default:
            break;
	    }
	}

	
	private  void playerSendMessage(int msgType, Object obj, long delayMillis){
	    Message msg = mPlayerEventHandler.obtainMessage();
	    msg.what = msgType;
	    msg.obj = obj;
	    mPlayerEventHandler.sendMessageDelayed(msg, delayMillis);
	}
	
    private void initPlayerViews() throws YouPlayerException{

//        mPlayerMediaVideoLayout = (RelativeLayout)mView.findViewById(R.id.fullplayer_video_layout);
        mPlayerMediaAudioLayout = (RelativeLayout)mView.findViewById(R.id.fullplayer_audio_layout);
        mPlayerMediaRadioLayout = (RelativeLayout)mView.findViewById(R.id.fullplayer_radio_layout);
        
        mPlayerSysSurfaceView = (SurfaceView)mView.findViewById(R.id.fullplayer_surface_view_system);
        mPlayerYouSurfaceView = (SurfaceView)mView.findViewById(R.id.fullplayer_surface_view_fone);
        mPlayerBackgroundLogo = (RelativeLayout)mView.findViewById(R.id.full_logo_bg);
        
//	    mPlayerTopSysTimeText = (TextView)mView.findViewById(R.id.fullplayer_top_sys_time_text);
//	    mPlayerTopImgvBattery = (ImageView)mView.findViewById(R.id.fullplayer_top_imgv_battery);
	    
   
	    
//	    mPlayerBtmMediaName = (TextView)mView.findViewById(R.id.fullplayer_btm_media_name);
//	    mPlayerBtmPlayState = (TextView)mView.findViewById(R.id.fullplayer_btm_play_state);
//	    mPlayerBtmTimeMin = (TextView)mView.findViewById(R.id.fullplayer_btm_text_time_min);
//	    mPlayerBtmTimeMax = (TextView)mView.findViewById(R.id.fullplayer_btm_text_time_max);
	    mPlayerBtmPlaySeekbar = (SeekBar)mView.findViewById(R.id.full_play_seekbar);
//	    mPlayerBtmImgbMore = (ImageButton)mView.findViewById(R.id.fullplayer_btm_imgb_more);
//	    mPlayerBtmImgbDlna = (ImageButton)mView.findViewById(R.id.fullplayer_imgb_dlna);
	    mPlayerBtmImgbPrevious = (ImageButton)mView.findViewById(R.id.fullplayer_btm_imgb_previous);
	    mPlayerBtmImgbPlay = (ImageButton)mView.findViewById(R.id.fullplayer_btm_imgb_play);
	    mPlayerBtmImgbNext = (ImageButton)mView.findViewById(R.id.fullplayer_btm_imgb_next);
//	    mPlayerBtmImgbQuality = (ImageButton)mView.findViewById(R.id.fullplayer_btm_imgb_quality);
//	    mPlayerBtmImgbFullScreen = (ImageButton)mView.findViewById(R.id.fullplayer_btm_imgb_fullscreen);
//	    mPlayerRelativeBtn = (ImageButton) mView.findViewById(R.id.fullplayer_btn_relative);
//	    mPlayerRelativeContainer = mView.findViewById(R.id.fullplayer_relative_container);
	    
//	    mPlayerTopDecodeTypeText = (TextView) mView.findViewById(R.id.fullplayer_top_sys_decode_type);
	    
	    
	    mPlayerCenterTips = (TextView)mView.findViewById(R.id.fullplayer_center_tips);
	    mPlayerGestureView = (GestureOverlayView) mView.findViewById(R.id.full_surface_gesture);
	    mPlayerSeekbarVolume = (YouPlayerVolControl) mView.findViewById(R.id.full_sound_seekto);
	    mPlayerSeekbarBrightness = (YouPlayerBrightControl) mView.findViewById(R.id.full_brightness_seekto);
//	    mPlayerRelativeList = (YouPlayerRelativeList) mView.findViewById(R.id.fullplayer_videolink);
	    
	    mPlayerSubtitleView = (YouPlayerSubtitleView) mView.findViewById(R.id.fullplayer_subtitle_view);
	    mPlayerLyricView = (YouLyricView) mView.findViewById(R.id.fullplayer_audio_lyricview);
	    mPlayerLockView = (RelativeLayout)mView.findViewById(R.id.fullplayer_lock_layout);
	    mPlayerUnlockImgBtn = (YouPlayerSlideView)mView.findViewById(R.id.fullplayer_lock_slidrview);
	    
//	    mPlayerMask = (ImageView)mView.findViewById(R.id.fullplayer_mask);
	    
	    setPlayerSeekbarIsEnable(false);
	    mPlayerSeekbarBrightness.setBrightInit();

		
		 mOnSurfaceCreatedExecute = new YouPlayerSurfaceView.OnCreateExecuted(){

			@Override
			public void onCreated(SurfaceView sv) {
				
				switch (mPlayerAdapter.mMediaInfo.is_system_player) {

					case Player_type.PLAYER_TYPE_HARD_SOFT:
						Player_UIManager.fone_media_player_set_surface_view(sv.getHolder().getSurface());
						break;
					case Player_type.PLAYER_TYPE_SYSTEM:
						YouPlayerAndroidPlayer.Uim_Set_Player_Surface(mPlayerSysSurfaceView);
						break;
					case Player_type.PLAYER_TYPE_SOFT:
						Player_UIManager.fone_media_player_set_surface_view(sv.getHolder().getSurface());
						if( mPlayerAdapter.mMediaInfo.width == 0 )
							sv.getHolder().setFixedSize(mSurface_width,mSurface_height);
						else
							sv.getHolder().setFixedSize(mPlayerAdapter.mMediaInfo.width,mPlayerAdapter.mMediaInfo.height);
						
						break;
						
				}
				
				YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_VIEW_READY, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
			}
			
		};
		
	    
	}

    private void reInitPlayerViews() throws YouPlayerException{
    	LOG.v(TAG, "reInitPlayerViews", "start");
        mPlayerBtmPlaySeekbar.setProgress(0);
        mPlayerBtmImgbPrevious.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_prev);
        mPlayerBtmImgbPlay.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_pause);
        mPlayerBtmImgbNext.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_next);
       // mPlayerBtmImgbSpeed.setImageResource(R.drawable.fullplayer_bottom_btn_speed10);
        
        mPlayerBtmImgbPrevious.setEnabled(true);
        mPlayerBtmImgbNext.setEnabled(true);       
//    	mPlayerBtmImgbFullScreen.setEnabled(true);       	
//    	mPlayerBtmImgbMore.setEnabled(true);
        
        ((ImageView) mView.findViewById(R.id.full_audio_thumbnail)).setImageResource(R.drawable.youplayer_player_audio_thumbnail);
        ((TextView)mView.findViewById(R.id.bar_text_audio_file_value)).setText("");
        ((TextView)mView.findViewById(R.id.bar_text_audio_album_value)).setText("");
        ((TextView)mView.findViewById(R.id.bar_text_audio_singer_value)).setText("");
        
        mPlayerCenterTips.setText("");
//        mPlayerTopSysTimeText.setText("");
//        mPlayerBtmMediaName.setText("");
//        mPlayerBtmPlayState.setText("");
//        mPlayerBtmTimeMin.setText("");
//        mPlayerBtmTimeMax.setText("");
//        mPlayerTopDecodeTypeText.setText("");
        mPlayerSysSurfaceView.setVisibility(View.INVISIBLE);
        mPlayerYouSurfaceView.setVisibility(View.INVISIBLE);
        
        YouPlayerSubtitleView.titleStr = null;
        mPlayerAdapter.destoryPlayerAdapter();
       
        setPlayerSeekBarVolumeOrBrightVisibleChanged(PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE);
        setPlayerLyricViewVisibleChanged(false);
        setPlayerMediaLayoutDefault();
        setPlayerSeekbarIsEnable(false);
        dismissSpeedPopupwindow();
        dismissSubtitlePopupwindow();
        mPlayerIsSeeking = false;
//        mPlayerRelativeList.onDestoryed();

        if(getPlayerIsLock()){
            setPlayerUnlockAciton();
            setPlayerIsLock(true);
        }

        
        mPlayerLyricView.setVisibility(View.INVISIBLE);
        mPlayerGestureView.getLayoutParams().width = RelativeLayout.LayoutParams.FILL_PARENT;
        
    }
    
	private void initPlayerOnClickListener()throws YouPlayerException{
	    
	    mPlayerBtmPlaySeekbar.setOnSeekBarChangeListener(playerOnSeekBarChangeListener);
//	    mPlayerBtmImgbMore.setOnClickListener(playerOnClickListener);
//	    mPlayerBtmImgbDlna.setOnClickListener(playerOnClickListener);
	    mPlayerBtmImgbPrevious.setOnClickListener(playerOnClickListener);
	    mPlayerBtmImgbPlay.setOnClickListener(playerOnClickListener);
	    mPlayerBtmImgbNext.setOnClickListener(playerOnClickListener);
//	    mPlayerBtmImgbFullScreen.setOnClickListener(playerOnClickListener);
	    mPlayerLyricView.setLyricTouchListener(playerLyricTouchListener);
	    mView.setOnTouchListener(mViewOnTouchListener);
	    mPlayerLockView.setOnTouchListener(mPlayerLockListener);
	    mPlayerUnlockImgBtn.setOnSlideListener(mPlayerUnlockBtnListener);
//	    mPlayerRelativeBtn.setOnClickListener(playerOnClickListener);
//	    mPlayerBtmImgbQuality.setOnClickListener(playerOnClickListener);
//	    mPlayerMask.setOnClickListener(playerOnClickListener);
	}
	
	private void initAudioThumbnailPos() {
        int nScreenWidth = extScreenGetWidth();
        int nScreenHeight = extScreenGetHeight();
        float x_scale = ((float) nScreenWidth) / 800.0f;
        float y_scale = ((float) nScreenHeight) / 480.0f;
        LayoutParams params;
        params = (LayoutParams) ((ImageView)mView.findViewById(R.id.full_audio_thumbnail_frame)).getLayoutParams();
        params.width = (int) (178 * y_scale + 1);
        params.height = (int) (160 * y_scale + 1);
        params.leftMargin = (int) (56 * y_scale);
        params.topMargin = (int) (50 * y_scale);
    
        ((ImageView)mView.findViewById(R.id.full_audio_thumbnail_frame)).setLayoutParams(params);
        ((ImageView)mView.findViewById(R.id.full_audio_thumbnail_frame)).setScaleType(ImageView.ScaleType.FIT_XY);
        
        params = (LayoutParams) ((ImageView)mView.findViewById(R.id.full_audio_thumbnail)).getLayoutParams();
        params.width = (int) (138 * y_scale + 1);
        params.height = (int) (138 * y_scale + 1);
        params.leftMargin = (int) (86 * y_scale);
        params.topMargin = (int) (62 * y_scale);
    
        ((ImageView)mView.findViewById(R.id.full_audio_thumbnail)).setLayoutParams(params);
        ((ImageView)mView.findViewById(R.id.full_audio_thumbnail)).setScaleType(ImageView.ScaleType.FIT_XY);
        
        params = (LayoutParams) ((ImageView)mView.findViewById(R.id.full_audio_thumbnail_reflection)).getLayoutParams();
        params.width = (int) (178 * y_scale + 1);
        params.height = (int) (16 * y_scale + 1);
        params.leftMargin = (int) ((56) * y_scale);
    
        ((ImageView)mView.findViewById(R.id.full_audio_thumbnail_reflection)).setLayoutParams(params);
        ((ImageView)mView.findViewById(R.id.full_audio_thumbnail_reflection)).setScaleType(ImageView.ScaleType.FIT_XY);
        
        params = (LayoutParams) ((RelativeLayout)mView.findViewById(R.id.full_audio_view_container)).getLayoutParams();
        params.leftMargin = (int) ((56) * y_scale);
        ((RelativeLayout)mView.findViewById(R.id.full_audio_view_container)).setLayoutParams(params);
    }
	
	private void initPlayerGestureListener() {
		
		LOG.v(TAG, "initPlayerGestureListener", "start");
		YouOnGesture youOnGesture = new YouOnGesture(mContext);
		youOnGesture.setYouOnGestureListener(new YouOnGesture.YouOnGestureListener() {
            
            @Override
            public void YouOnGestureStart() {
                if(getGestureIsEnable() && getPlayerIsReadyToPlay()){
                	LOG.v(TAG, "initPlayerGestureListener", "YouOnGestureStart");
                    onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_START, 0, null, null);
                }
            }
            int width;
            @Override
            public void YouOnGestureMoveUPOrDown(boolean isEnableSeek, float distance,float start_x,boolean two_pointer) {
                if(getGestureIsEnable()){
                	LOG.v(TAG, "initPlayerGestureListener", "YouOnGestureMoveUPOrDown");
                    if( width == 0 )
                    	width =  extScreenGetWidth();
                    onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_UP_DOWN, 0, (int)distance,( (( width /2 < start_x )? 1:0)<<1 ) | (two_pointer?1:0));
                }
            }
            
            @Override
            public void YouOnGestureMovePrevious() {
                if(getGestureIsEnable()){
                	LOG.v(TAG, "initPlayerGestureListener", "YouOnGestureMovePrevious");
                    onPlayerPreviousAction();
                }
            }
            
            @Override
            public void YouOnGestureMoveNext() {
                if(getGestureIsEnable()){
                	LOG.v(TAG, "initPlayerGestureListener", "YouOnGestureMoveNext");
                    onPlayerNextAction();
                }
            }
            
            @Override
            public void YouOnGestureMoveLeftOrRight(boolean isEnableSeek, float distance) {
                if(getGestureIsEnable() && getSeekIsEnable() ){
                    try {
                        setPlayerIsSeeking(true);
                        int seek_to = getGestureSeekPos(distance);
//                        L.v(TAG, "gestureListener", "YouOnGestureMoveLeftOrRight move distance:" + distance+"---"+ seek_to);
                        onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_LEFT_RIGHT, 0, seek_to, null);
                        setSeekPreviewCallback();
                    } catch (YouPlayerException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            @Override
            public void YouOnGestureEnd(boolean isEnableSeek, boolean isLeftOrRight) {
            	LOG.v(TAG, "initPlayerGestureListener", "YouOnGestureEnd isEnableSeek: " + isEnableSeek + " ,isLeftOrRight:" + isLeftOrRight);
                if(getGestureIsEnable() && getPlayerIsReadyToPlay()){
                    removeSeekPreviewCallback();
                    onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_END, 0, isEnableSeek, null);
                    if(getSeekIsEnable() && isEnableSeek && isLeftOrRight){
                        onRequestCoreServiceForSeek();
                    }
                }
            }
            
            @Override
            public void YouOnGesture() {
                if(getGestureIsEnable() && getSeekIsEnable()){
                	LOG.v(TAG, "initPlayerGestureListener", "YouOnGesture");
                    onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_SEEKING, 0, null, null);
                }
            }
        });
	    
	    mPlayerGestureView.addOnGestureListener(youOnGesture);
	}
	
	private void setPlayerGestureView(){
		LOG.v(TAG, "setPlayerGestureView IsEnable:", getGestureIsEnable());
	    if(getGestureIsEnable()){
	        setPlayerGestureViewIsVisible(true);
        }else{
            setPlayerGestureViewIsVisible(false);
        }
	 }
	 
	private void setPlayerUsedSurface() throws YouPlayerException{
		LOG.v(TAG, "setPlayerUsedSurface IsSystemMediaPlayer:", getPlayerIsSystemMediaPlayer());
		setCurrentSurfaceViewVisible(mPlayerAdapter.mMediaInfo.is_system_player);
    }
	
	private boolean getSeekIsEnable(){
	    if(mPlayerAdapter.getMediaInfoIsLive() || getPlayerIsOnlineAudio() || !getPlayerIsReadyToPlay()){
	          return false;
	    }
	    return true;
	}
	
    private void setPlayerMediaLayout(int mediaType)throws YouPlayerException{
    	LOG.v(TAG, "setPlayerMediaLayout mediaType: ", mediaType);
        if(mPlayerAdapter.mMediaInfo != null && mPlayerAdapter.mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_AIRONE) {
        	mediaType = You_player_media_info.Cls_you_media_type_e.YOU_AIRONE_VIDEO_MEDIA;
        	
//        	mPlayerBtmImgbFullScreen.setEnabled(false);
//        	mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_size_gray);        	
//        	mPlayerBtmImgbMore.setEnabled(false);
    		
    		setBackgroundLogoIsShow(false);

        } else {
//        	mPlayerBtmImgbFullScreen.setEnabled(true);
//        	mPlayerBtmImgbMore.setEnabled(true);
        }
        
//		this.showStartGuide();
		
        switch(mediaType){
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA:
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA:
//            mPlayerMediaVideoLayout.setVisibility(View.VISIBLE);
            mPlayerMediaAudioLayout.setVisibility(View.INVISIBLE);
            mPlayerMediaRadioLayout.setVisibility(View.INVISIBLE);
//            mPlayerMediaAironeLayout.setVisibility(View.INVISIBLE);
//            mPlayerBtmImgbDlna.setEnabled(true);
//            mFlingAirControl.registerListener(this);
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_LOCAL_AUDIO_MEDIA:
//            mPlayerMediaVideoLayout.setVisibility(View.INVISIBLE);
            mPlayerMediaAudioLayout.setVisibility(View.VISIBLE);
            mPlayerMediaRadioLayout.setVisibility(View.INVISIBLE);
//            mPlayerMediaAironeLayout.setVisibility(View.INVISIBLE);
//            mPlayerBtmImgbDlna.setEnabled(false);
//            mFlingAirControl.unregisterListener();
            break;
            
        case You_player_media_info.Cls_you_media_type_e.YOU_ONLINE_AUDIO_MEDIA: 
//            mPlayerMediaVideoLayout.setVisibility(View.INVISIBLE);
            mPlayerMediaAudioLayout.setVisibility(View.INVISIBLE);
            mPlayerMediaRadioLayout.setVisibility(View.VISIBLE);
//            mPlayerMediaAironeLayout.setVisibility(View.INVISIBLE);
//            mPlayerBtmImgbDlna.setEnabled(true);
//            mFlingAirControl.registerListener(this);
            break;

        case You_player_media_info.Cls_you_media_type_e.YOU_AIRONE_VIDEO_MEDIA: 
//            mPlayerMediaVideoLayout.setVisibility(View.INVISIBLE);
            mPlayerMediaAudioLayout.setVisibility(View.INVISIBLE);
            mPlayerMediaRadioLayout.setVisibility(View.INVISIBLE);
//            mPlayerMediaAironeLayout.setVisibility(View.VISIBLE);
//            mPlayerBtmImgbDlna.setEnabled(true);
//            mFlingAirControl.registerListener(this);
            break;            
        default:
            break;
        }
    }
    
    private void setPlayerMediaLayoutDefault(){
    	LOG.v(TAG, "setPlayerMediaLayoutDefault", "start");
//        mPlayerMediaVideoLayout.setVisibility(View.INVISIBLE);
        mPlayerMediaAudioLayout.setVisibility(View.INVISIBLE);
        mPlayerMediaRadioLayout.setVisibility(View.INVISIBLE);  
        if(mPlayerYouSurfaceView != null)
        	mPlayerYouSurfaceView.setVisibility(View.INVISIBLE);
        if(mPlayerSysSurfaceView != null)
        mPlayerSysSurfaceView.setVisibility(View.INVISIBLE);
//        mPlayerRelativeContainer.setVisibility(View.INVISIBLE);
//        (mView.findViewById(R.id.fullplayer_topbar)).setVisibility(View.INVISIBLE);
        if(mView != null)
        {
        	View view = mView.findViewById(R.id.fullplayer_bottombar);
        	if(view != null)
        	{
        		view.setVisibility(View.INVISIBLE);
        	}
        }
//        (mView.findViewById(R.id.fullplayer_bottombar)).setVisibility(View.INVISIBLE);
      
    }
    
    private void setRelativeListVisible(boolean isVisible){
    	LOG.v(TAG, "setRelativeListVisible isVisible:", isVisible);
//        if(isVisible){
//        	if( mPlayerRelativeContainer.getVisibility() != View.VISIBLE ){
//        		mPlayerRelativeContainer.setVisibility(View.VISIBLE);
//	            mPlayerRelativeList.setVisibility(View.GONE);
//        	}
//        }else{
//        	if(mPlayerRelativeList.getVisibility() == View.VISIBLE){
//        		mPlayerRelativeContainer.setVisibility(View.INVISIBLE);
//        		mPlayerRelativeContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_out));
//        	}
//        	else
//        		mPlayerRelativeContainer.setVisibility(View.INVISIBLE);
//
//        }
    }
    
    private void setPlayerGestureViewIsVisible(boolean isVisible){
    	LOG.v(TAG, "setPlayerGestureViewIsVisible isVisible:", isVisible);
        if(isVisible){
            mPlayerGestureView.setVisibility(View.VISIBLE);
        }else{
            mPlayerGestureView.setVisibility(View.INVISIBLE);
        }
    }
    
	private void setPlayerSeekBarProgress(int pos ){
	    mPlayerBtmPlaySeekbar.setProgress(pos);
	}
	
	private int getPlayerSeekBarProgress(){
        return mPlayerBtmPlaySeekbar.getProgress();
    }

	public int[] extScreenGetContentRect() {
        int[] pos = new int[4];
        if(getObjectIsNull(mContext)){
            return pos;
        }
        int width = extScreenGetWidth();
        int height = extScreenGetHeight();
        if (width < height) {
            int temp = width;
            width = height;
            height = temp;
        }
        if(height >= 720)
        {
            pos[2] = 970 < width ? 970 : width;
            pos[3] = 405;
        }
        else
        {
            pos[2] = mContext.getResources().getDimensionPixelSize(R.dimen.fullplayer_full_ad_pos_width);
            pos[3] = mContext.getResources().getDimensionPixelSize(R.dimen.fullplayer_full_ad_pos_height);
        }
        
        pos[0] = (int)((width - pos[2])/2.0f + 0.5) ;
        pos[1] = height - pos[3]- mContext.getResources().getDimensionPixelSize(R.dimen.fullplayer_full_bottom_button_group_layoutheight)-3;
        
        return pos;
    }
	
	public int[] extScreenGetContentRect(int ad_width, int ad_height) {
        int[] pos = new int[4];
        if(getObjectIsNull(mContext)){
            return pos;
        }
        
        try {
            int top_height = 0;//((LinearLayout)mView.findViewById(R.id.fullplayer_topbar)).getHeight(); 
            int bottom_height    = ((RelativeLayout)mView.findViewById(R.id.fullplayer_bottombar)).getHeight();
            
            int width = extScreenGetWidth();
            int height = extScreenGetHeight();
            if (width < height) {
                int temp = width;
                width = height;
                height = temp;
            }
            
            if(ad_width > width){
                ad_width = width;
                LOG.e(TAG, "extScreenGetContentRect width", "error default value");
            }
            
            if(ad_height > (height - bottom_height - top_height)){
                ad_height = height - bottom_height - top_height;
                LOG.e(TAG, "extScreenGetContentRect height", "error default value");
            }
            
            pos[0] = (int)((width - ad_width)/2.0f + 0.5) ;
            pos[1] = height - ad_height - bottom_height;
            pos[2] = ad_width;
            pos[3] = ad_height;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return pos;
    }
	
	private int getGestureSeekPos(float move_x) throws YouPlayerException{
	    int temp_seek = (int) (move_x / ((float) extScreenGetWidth() / (float) mPlayerBtmPlaySeekbar.getMax()));
        int cur_pos = mPlayerBtmPlaySeekbar.getProgress();
        cur_pos -= temp_seek / 10;

        if (cur_pos >= mPlayerBtmPlaySeekbar.getMax()){
            cur_pos = mPlayerBtmPlaySeekbar.getMax() - 100;
        }
        if (cur_pos < 0){
            cur_pos = 0;
        }
        LOG.v(TAG, "getGestureSeekPos", "cur_pos:" + cur_pos);
        return cur_pos;
	}
	
	private View.OnClickListener playerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                playerOnClick(v);
            } catch (YouPlayerException e) {
            	LOG.v(TAG, "playerOnClickListener", "view:" + v);
                e.printStackTrace();
            }
        }
    };
    
    private View.OnTouchListener mViewOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
        	LOG.v(TAG, "mViewOnTouchListener Action:", event.getAction() + "getGestureIsEnable:" + getGestureIsEnable());
            if(event.getAction() == MotionEvent.ACTION_DOWN ){
                onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_END, You_Core.FN_UI_EVT_TOUCH_UP, false ,null);
                return true;
            }
            return false;
        }
    };
    
    
    private SeekBar.OnSeekBarChangeListener playerOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        	LOG.v(TAG, "OnSeekBarChangeListener", "onStopTrackingTouch");
            onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_END, 0, true, null);
            if(getSeekIsEnable()){
                int pos = getPlayerSeekBarProgress();
                LOG.v(TAG, "OnSeekBarChangeListener", "onStopTrackingTouch pos:" + pos);
                onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_SEEK,You_Core.FN_UI_EVT_TOUCH_UP, null, pos);
            }
        }
        
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        	LOG.v(TAG, "OnSeekBarChangeListener", "onStartTrackingTouch");
            setPlayerIsSeeking(true);
            onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_START, 0, null, null);
        }
        
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                if(getPlayerIsReadyToPlay()){
//                    L.v(TAG, "OnSeekBarChangeListener", "onProgressChanged:" + progress);
                    playerRefreshUIMaxAndMinTime(getCurrentTaskTotalDuration(), progress);      
                    playerRefreshUILyricView(progress);
                }
            } catch (YouPlayerException e) {
                e.printStackTrace();
            }
        }
    };
    
    private YouLyricView.LyricTouchListener playerLyricTouchListener = new YouLyricView.LyricTouchListener(){

        @Override
        public void onDragged(long time) {
        	mPlayerBtmPlaySeekbar.setProgress((int)time);
        	onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_SEEK, You_Core.FN_UI_EVT_TOUCH_UP, null, (int)time);
        }

        @Override
        public void onDragging(float x, float y, float nx, float ny,int width,int height) {
        	
        	int temp_seek = (int) ((y-ny) / ((float) height / (float) mPlayerBtmPlaySeekbar.getMax()));
			int cur_pos = mPlayerBtmPlaySeekbar.getProgress();
			cur_pos += temp_seek / 3;

			if (cur_pos >= mPlayerBtmPlaySeekbar.getMax())
				cur_pos = mPlayerBtmPlaySeekbar.getMax() - 100;
			if (cur_pos < 1)
				cur_pos = 0;
			LOG.v(TAG, "onDragging mViewOnTouchListener seeking .... ", cur_pos);
			mPlayerBtmPlaySeekbar.setProgress(cur_pos);
			
        	onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_SEEK, You_Core.FN_UI_EVT_TOUCH_UP, null, (int)cur_pos);
        }

        @Override
        public void onPoint() {
        	onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_END, You_Core.FN_UI_EVT_TOUCH_UP, false ,null);
        }
        
        
        
		@Override
		public void onDragged(boolean left) {
			LOG.v(TAG, "onDragged","draw x xol : left :"+left);
			View view = mView.findViewById(R.id.full_audio_text_container);
			if( left && view.getVisibility() == View.VISIBLE ){
				view.setVisibility(View.GONE);
			}else if( !left && view.getVisibility() == View.GONE ){
				view.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onSizeChanged(int width, int height) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mPlayerGestureView.getLayoutParams();
			lp.width = extScreenGetWidth() - width;
			lp.leftMargin = 0;
		}
    };
    
    
    private View.OnTouchListener mPlayerLockListener = new View.OnTouchListener() {
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
            case MotionEvent.ACTION_UP:
            	LOG.v(TAG, "mPlayerLockListener", "isLock: " + getPlayerIsLock());
                if(getPlayerIsLock()){
                    setPlayerLockBtnVisibleChanged(true);
                    setPlayerLockRunnable();
                }
                return true;
                
            default:
                break;
            }
            return false;
        }
    };
    
    private YouPlayerSlideView.onSlideFinishedListener  mPlayerUnlockBtnListener = new YouPlayerSlideView.onSlideFinishedListener() {

        @Override
        public void OnSlideStart() {
            setPlayerLockRunnableRemove();
        }

        @Override
        public void OnSlideEnd(boolean isFinished) {
            if(isFinished){
                if(getPlayerIsLock()){
                    setPlayerUnlockAciton();
                }
            }else{
                setPlayerLockRunnable();
            }
        }
        
    };
    
    private void playerOnClick(View v) throws YouPlayerException{
    	LOG.v(TAG, "playerOnClick", "start");
        if((!getPlayerIsReadyToPlay() || !isAllControllPanelIsVisible())&& v.getId()!= R.id.fullplayer_mask ){
            return;
        }
        switch(v.getId()){
    	
        
        case R.id.btn_quality1:{
        	onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_DEFINITION, You_Core.FN_UI_EVT_TOUCH_UP, null, 1);
        }
        break;
        case R.id.btn_quality2:{
        	onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_DEFINITION, You_Core.FN_UI_EVT_TOUCH_UP, null, 2);
        }
        break;
        case R.id.btn_quality3:{
        	onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_DEFINITION, You_Core.FN_UI_EVT_TOUCH_UP, null, 3);
        }
        break;
        
        case R.id.fullplayer_imgb_download:
            onPlayerDownloadAction();
            break;
        case R.id.fullplayer_imgb_share:
            onPlayerShareAction();
            break;
        case R.id.fullplayer_imgb_3d:
        	on3DAction();
        	break;
        case R.id.fullplayer_imgb_track:
        	onPlayerTrackAction();
        	break; 
        case R.id.fullplayer_imgb_speed:
        	onPlayerSpeedAction();
        	break;
//        case R.id.fullplayer_imgb_dlna:
////        	m_airone_action = 0;
//        	//onDlnaAction();
//        	break;
//        case R.id.fullplayer_btm_imgb_more:
//        {
//        	// 如果为DLNA投放模式，则操作不可用  
//        	if(mPlayerAdapter.mMediaInfo != null && mPlayerAdapter.mMediaInfo.is_system_player != Player_type.PLAYER_TYPE_AIRONE) {
//        		//0 1 2:1.5 3:2.0
//	        	int speed  = mSpeed;
//	        	if( getPlayerIsSystemMediaPlayer() ){
//	        		speed = 1;
//	        	}
//	        	final PopupWindow pw = YouPlayerMoreDialog.show(v,speed,playerOnClickListener);
//	        	onNewExec.add(new IStopExec(){
//	
//					@Override
//					public void onStop() {
//						if( pw != null && pw.isShowing() )
//							pw.dismiss();
//						
//					}
//	        	});
//	        	
//        	}
//        	break;
//        }
        case R.id.fullplayer_btm_imgb_lock:
        	 onPlayerLockAction();
            break;
            
        case R.id.fullplayer_btm_imgb_previous:
        	mPlayerBtmImgbPrevious.setEnabled(false);
            onPlayerPreviousAction();
            break;
            
        case R.id.fullplayer_btm_imgb_play:
            onPlayerPlayAction();
            break;
            
        case R.id.fullplayer_btm_imgb_next:
        	
        	mPlayerBtmImgbNext.setEnabled(false);
            onPlayerNextAction();
            break;
            
//        case R.id.fullplayer_btm_imgb_quality:
//        {
//        	
//        	if(  mPlayerAdapter.mMediaInfo.current_dfnt == 0  ){
//        		//onRequestCoreService(PLAYER_FN_UI_MSG_TIPS_NOSUPPORT, fn_Core.FN_UI_EVT_TOUCH_UP, null, null);
//        		return;
//        	}
//        	final PopupWindow pw = YouPlayerQualityDialog.show(v,mPlayerAdapter.mMediaInfo.definition,mPlayerAdapter.mMediaInfo.current_dfnt,playerOnClickListener);
//        	if( pw == null ){
//        		//onRequestCoreService(PLAYER_FN_UI_MSG_TIPS_NOSUPPORT, fn_Core.FN_UI_EVT_TOUCH_UP, null, null);
//        		return;
//        	}	
//        	onNewExec.add(new IStopExec(){
//
//				@Override
//				public void onStop() {
//					if( pw != null && pw.isShowing() )
//						pw.dismiss();
//					
//				}
//        		
//        	});
//        	break;
//        }   
//        case R.id.fullplayer_btm_imgb_fullscreen:
//            onPlayerFullScreenAction();
//            break;
//        case R.id.fullplayer_btn_relative:
//        	onRelativeBtnAction();
//        	onRequestCoreService(PLAYER_FN_UI_MSG_BTN_RELATIVE, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        case R.id.fullplayer_mask:
//        	m_showed = true;
//        	mPlayerMask.setVisibility(View.GONE);
//        	mPlayerBtmImgbDlna.setPressed(false);
        default:
            break;
    	}
    }

	private void onRelativeBtnAction(){
//    	if( mPlayerRelativeList.getVisibility() != View.GONE ){
//    		mPlayerRelativeList.setVisibility(View.GONE);
//    	} else{
//    		mPlayerRelativeList.setVisibility(View.VISIBLE);
//    	}
    }
    
    private void playerRefreshUILyricView(int progress){
        if( getPlayerIsLocalAudio() && mPlayerLyricView.getVisibility() == View.VISIBLE ){
            mPlayerLyricView.setTime(progress);
        }
    }
    
    private void setPlayerIsBackground(boolean isBackground){
        mPlayerIsGoToBackground = isBackground;
    }

    private void setPlayerIsSeeking(boolean isSeeking){
        this.mPlayerIsSeeking = isSeeking;
    }
    
    public boolean getPlayerIsShowStop(){
        return mPlayerAdapter.getPlayerIsShowStop();
    }
    
    private boolean getPlayerIsSeeking(){
        return this.mPlayerIsSeeking;
    }
    
    public boolean getPlayerIsBackground(){
        return this.mPlayerIsGoToBackground;
    }
    
    private boolean getPlayerIsReadyToPlay(){
        return mPlayerAdapter.getPlayerIsReadyToPlay();
    }
    
    public int getPlayerPlayState(){
        return mPlayerAdapter.getPlayerPlayState();
    }
    
    public boolean getPlayerIsOnlineAudio(){
        return mPlayerAdapter.getPlayerIsOnlineAudio();
    }
    
    private boolean getPlayerIsLocalAudio(){
        return mPlayerAdapter.getPlayerIsLocalAudio();
    }

    
    private boolean getPlayerIsLocalVideo(){
        return mPlayerAdapter.getPlayerIsLocalVideo();
    }
    
    private boolean getPlayerIsVideoMedia(){
        return mPlayerAdapter.getPlayerIsVideoMedia();
    }
    
   
    
    private boolean getPlayerIsPauseStatus(){
        return mPlayerAdapter.getPlayerIsPauseStatus();
    }
    
    public boolean getPlayerIsCanFav(){
        return mPlayerAdapter.getMediaInfoCanFav();
    }
    
    public boolean getPlayerIsCanCache(){
        return mPlayerAdapter.getMediaInfoCanCache();
    }

    
    private void SpeedActionView(){
        if(getObjectIsNull(mContext)){
        	LOG.e(TAG, "SpeedActionView", "mView is null");
            return ;
        }
        
        int topbar_height = 0;//(mView.findViewById(R.id.fullplayer_topbar)).getHeight();
        int bottombar_height = (mView.findViewById(R.id.fullplayer_bottombar)).getHeight();
        int tmp_height = topbar_height + bottombar_height;
        final int item_h = (extScreenGetHeight() - tmp_height )/4;
        final int item_w = YouPlayerRelativeList.getRelativeWidth();
        mPopupWindowSpeed = new PopupWindow(mContext);
        final String[] speeds = mContext.getResources().getStringArray(R.array.player_speeds);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.youplayer_player_speedlist, null);
        ListView list = (ListView) contentView.findViewById(R.id.list_playerspeeds);
        list.setAdapter(new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
            	TextView text = new TextView(mContext);
                text.setTextColor(Color.WHITE);
                text.setText(getItem(position).toString());
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setPadding(10, 0, 0, 0);
                text.setLayoutParams(new AbsListView.LayoutParams(item_w,item_h));
                return text;
            }
            
            @Override
            public long getItemId(int position) {
                return position;
            }
            
            @Override
            public Object getItem(int position) {
                return speeds[position];
            }
            
            @Override
            public int getCount() {
                return speeds.length;
            }
        });
        list.setFocusable(false);
        list.setCacheColorHint(0x00000000);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            	LOG.v(TAG, "SpeedActionView index:", arg2);
                try {
                    mSpeed = arg2;
                    onRequestServiceSpeed(arg2);
                    dismissSpeedPopupwindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mPopupWindowSpeed.setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        mPopupWindowSpeed.setContentView(contentView);
        mPopupWindowSpeed.setFocusable(true);  
        mPopupWindowSpeed.setWidth(item_w);  
        mPopupWindowSpeed.setHeight(extScreenGetHeight() - tmp_height);  
//        mPopupWindowSpeed.showAtLocation(mPlayerBtmImgbMore, Gravity.TOP|Gravity.LEFT, 0,topbar_height );  
        mPopupWindowSpeed.setAnimationStyle(R.style.AnimationFade);
        mPopupWindowSpeed.update();
    }
    
    private void dismissSpeedPopupwindow(){
    	if(mPopupWindowSpeed != null){
            mPopupWindowSpeed.dismiss();
            mPopupWindowSpeed = null;
        }
    }
    
    private void dismissSubtitlePopupwindow(){
        if(mPlayerAudioSubtitle != null){
            mPlayerAudioSubtitle.closeWindow();
            mPlayerAudioSubtitle = null;
        }
    }

    
    private void onPlayerSpeedAction() {
    	LOG.v(TAG, "onPlayerSpeedAction", "start");
        if(!getPlayerIsSystemMediaPlayer() && (getPlayerIsLocalVideo()|| getPlayerIsLocalAudio())){
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_MENU, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
            SpeedActionView();
        }else{
            onRequestCoreService(PLAYER_FN_UI_MSG_SPEED_TIPS_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }
    }

    private void onPlayerNextAction() {
    	LOG.v(TAG, "onPlayerNextAction", "request");
        if(mPlayerAdapter.getMediaInfoHasNext()){
           // setPlayerViewsInitalizeValue();
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_NEXT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }else{
            onRequestCoreService(PLAYER_FN_UI_MSG_NEXT_TIPS_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
            mPlayerBtmImgbNext.setEnabled(true);
        }
    }

    private void onPlayerPlayAction() throws YouPlayerException{
        setPanelStatePlayOrPauseRequset(getPlayerPlayState());
    }

    private void setPanelStatePlayOrPauseRequset(int playState) {
    	LOG.v(TAG, "setPanelStatePlayOrPauseRequset playState:", playState);
    	switch(playState){
    	case You_full_screen_player_data_to_ui.Cls_you_player_status.NoneStatus:
            break;
            
        case You_full_screen_player_data_to_ui.Cls_you_player_status.OpenningStatus:
            break;
            
        case You_full_screen_player_data_to_ui.Cls_you_player_status.PlayingStatus:
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PAUSE, You_Core.FN_UI_EVT_TOUCH_UP, null, 0);
            break;
            
        case You_full_screen_player_data_to_ui.Cls_you_player_status.PauseStatus:
            if(!getPlayerIsSystemMediaPlayer() && getPlayerIsVideoMedia() 
                && mPlayerYouSurfaceView.getVisibility() != View.VISIBLE){
            	mPlayerYouSurfaceView.setVisibility(View.VISIBLE);
            }
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PLAY, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
            break;
            
        case You_full_screen_player_data_to_ui.Cls_you_player_status.BufferingStatus:
            break;    	
            
    	default:
    		break;    		
    	}
    }

    private void onPlayerPreviousAction() {
    	LOG.v(TAG, "onPlayerPreviousAction", "request");
        if(mPlayerAdapter.getMediaInfoHasPrevious()){
           // setPlayerViewsInitalizeValue();
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PREVIOUS, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }else{
            onRequestCoreService(PLAYER_FN_UI_MSG_PREVIOUS_TIPS_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
            mPlayerBtmImgbPrevious.setEnabled(true);
        }
    }

    private void onPlayerTrackAction() {
    	LOG.v(TAG, "onPlayerTrackAction", "request");
        if( mPlayerAdapter.mMediaInfo.is_system_player != Player_type.PLAYER_TYPE_SYSTEM && (getPlayerIsLocalVideo() || getPlayerIsLocalAudio())){
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_MENU, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_AUDIO_CHANNEL, You_Core.FN_UI_EVT_TOUCH_UP, null, null); 
        }else{
            onRequestCoreService(PLAYER_FN_UI_MSG_TRACK_TIPS_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }
    }


    private void onPlayerDownloadAction(){
    	LOG.v(TAG, "onPlayerDownloadAction", "request");
        try {
            if(!getPlayerIsCanCache()){
                playerRefreshUITip(playerGetResource(R.string.fullplayer_undownload_tip), PLAYER_TIPS_UI_FONT_SIZE_SMALL);
                onRequestCoreService(PLAYER_FN_UI_MSG_TIPS_DOWNLOAD_INVISIBLITY, You_Core.FN_UI_EVT_TOUCH_UP ,null, null);
            }else{
            	String message = YouUtility.getDeclaretion(YouExplorer.instance, YouPlayerConstant.YOU_DECLARETION_TYPE_DOWNLOAD);
                final AlertDialog dialog = new AlertDialog.Builder(YouExplorer.instance)
                    .setTitle(R.string.declaration_text)
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            Cls_download_request_list_t coreData = new Cls_download_request_list_t(1, new int[]{0});
//                            EventControler.fn_core_service_request(fn_Core.FN_COMMON_BTN_CACHE, fn_Core.FN_UI_EVT_TOUCH_UP, 0,coreData); 
                        }
                    })
                .setNegativeButton(R.string.dialog_cancel, null)
                .setCancelable(true).create();
                dialog.show();
                
                onDestoryExec.add(new IStopExec(){
					@Override
					public void onStop() {
						if( dialog != null && dialog.isShowing() )
						dialog.dismiss();
					}
                	
                });
            }   
        } catch (YouPlayerException e) {
            e.printStackTrace();
        }
    }


    
    private void onPlayerLockAction(){
    	LOG.v(TAG, "onPlayerLockAction", "request");
//        mPlayerRelativeList.setVisibility(View.GONE);
        onRequestCoreService(PLAYER_FN_UI_MSG_LOCK, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
    }
    
    private void setPlayerLockViewVisibleChanged(boolean isVisible){
        if(isVisible && mPlayerLockView != null && mPlayerLockView.getVisibility() != View.VISIBLE){
            mPlayerLockView.setVisibility(View.VISIBLE);
        }else if(!isVisible && mPlayerLockView != null && mPlayerLockView.getVisibility() == View.VISIBLE){
            mPlayerLockView.setVisibility(View.INVISIBLE);
        }
    }
    
    
    private void setPlayerLockRunnableRemove(){
        if(mPlayerLockHandler != null){
        	LOG.v(TAG, "setPlayerLockRunnableRemove", "start");
            mPlayerLockHandler.removeCallbacks(mPlayerLockRunnable);
        }
    }
    
    private void setPlayerLockRunnable(){
        setPlayerLockRunnableRemove();
        if(mPlayerLockHandler != null){
        	LOG.v(TAG, "setPlayerLockRunnable", "start");
            mPlayerLockHandler.postDelayed(mPlayerLockRunnable, 2000);
        }
    }
    
    private void setPlayerUnlockAciton(){
    	LOG.v(TAG, "setPlayerUnlockAciton", "isLock: " + getPlayerIsLock());
        try {
            if(getPlayerIsLock()){
                setPlayerIsLock(false);
                setPlayerLockRunnableRemove();
                setPlayerLockViewVisibleChanged(false);
                setPlayerLockBtnVisibleChanged(false);
                playerSetUITipInVisible();
                //解锁,UI PANEL 显示
                onRequestCoreService(PLAYER_FN_UI_EVT_PAGE_SEEK_END, You_Core.FN_UI_EVT_TOUCH_UP, false ,null);
            }
        } catch (YouPlayerException e) {
            e.printStackTrace();
        }
    }
    
    private Handler mPlayerLockHandler = new Handler();
    private Runnable mPlayerLockRunnable = new Runnable(){
        @Override
        public void run() {
            setPlayerLockBtnVisibleChanged(false);
        }
    };
    
    private void setPlayerLockBtnVisibleChanged(boolean isVisible){
        if(isVisible && mPlayerUnlockImgBtn != null && mPlayerUnlockImgBtn.getVisibility() != View.VISIBLE){
            mPlayerUnlockImgBtn.setVisibility(View.VISIBLE);
            
        }else if(!isVisible && mPlayerUnlockImgBtn != null && mPlayerUnlockImgBtn.getVisibility() == View.VISIBLE){
            mPlayerUnlockImgBtn.setVisibility(View.INVISIBLE);
        }
    }
    
    private void setPlayerIsLock(boolean isLock){
        this.mPlayerIsLocked = isLock;
    }
    
    public boolean getPlayerIsLock(){
        return mPlayerIsLocked;
    }
    
    private void onPlayerShareAction() {
    	LOG.v(TAG, "onPlayerShareAction", "request");
        if( !getPlayerIsCanFav() ){
        	onRequestCoreService(PLAYER_FN_UI_MSG_SHARE_TIPS_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        	return;
        }
        //onRequestCoreService(fn_Core.FN_FULL_SCREEN_BTN_MENU, fn_Core.FN_UI_EVT_TOUCH_UP, null, null);
        //onRequestCoreService(PLAYER_FN_UI_MSG_PLAY_SHARE, fn_Core.FN_UI_EVT_TOUCH_UP, null, null);
        if( !getPlayerIsPauseStatus() )
        	onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PAUSE, You_Core.FN_UI_EVT_TOUCH_UP, null, 0);
        onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_SHARE, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        
    }
    
    private void on3DAction() {
    	LOG.v(TAG, "on3DAction", "request");
        
        if( ( mPlayerAdapter.getMediaInfoMediaType() == Cls_you_media_type_e.YOU_LOCAL_VIDEO_MEDIA
        		       || mPlayerAdapter.getMediaInfoMediaType() == Cls_you_media_type_e.YOU_ONLINE_VIDEO_MEDIA) )
        {	
        
	        if( mPlayerAdapter.mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_SOFT ){
	        	int is_3d;
	        	if(mPlayerAdapter.getMediaInfoIs3D())
	            {
	            	is_3d = 0;
	            }
	            else
	            	is_3d = 1;
	            mPlayerAdapter.setMediaInfoIs3D(!mPlayerAdapter.getMediaInfoIs3D());
	            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_3D, You_Core.FN_UI_EVT_TOUCH_UP, is_3d, is_3d);
	            return;
	        }
        }
        onRequestCoreService(PLAYER_FN_UI_MSG_TIPS_3D_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        
        
    }
    private void onRequestServiceSpeed(int selectIndex){
    	LOG.v(TAG, "onPlayerShareAction selectIndex: ", selectIndex);
    	 int index = You_full_screen_player_data_to_ui.Cls_you_player_rate.YOU_PLAYER_NORMAL;
    	 switch(selectIndex){
    	 case PLAYER_PLAY_SPEED_08:
    	     index = You_full_screen_player_data_to_ui.Cls_you_player_rate.YOU_PLAYER_LOW;
    	     break;
    	     
    	 case PLAYER_PLAY_SPEED_10:
    	     index = You_full_screen_player_data_to_ui.Cls_you_player_rate.YOU_PLAYER_NORMAL;
    	     break;
    	     
    	 case PLAYER_PLAY_SPEED_15:
    	     index = You_full_screen_player_data_to_ui.Cls_you_player_rate.YOU_PLAYER_HIGH;
    	     break;
    	     
    	 case PLAYER_PLAY_SPEED_20:
    	     index = You_full_screen_player_data_to_ui.Cls_you_player_rate.YOU_PLAYER_TWO;
    	     break;
    	     
	     default:
	         break;
    	 }
         onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_RATE, You_Core.FN_UI_EVT_TOUCH_UP, (Integer)index, (Integer)index);
    }

    private void onPlayerFullScreenAction(){
    	LOG.v(TAG, "onPlayerFullScreenAction", "request");
        if(!getPlayerIsOnlineAudio()){
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_MODE_CHANGE, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }else{
        	onRequestCoreService(PLAYER_FN_UI_MSG_TIPS_NOSUPPORT, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }
    }
    
    private void setBackgroundLogoIsShow(boolean isShow){
    	LOG.v(TAG, "setBackgroundLogoIsShow isShow: ", isShow);
       if(isShow){
           mPlayerBackgroundLogo.setVisibility(View.VISIBLE);
       }else{
           mPlayerBackgroundLogo.setVisibility(View.INVISIBLE);
       }
   }
   
    private void initPlayerBatteryPowerListener() throws YouPlayerException{
        mPlayerPhonePower = YouPhonePowerUtil.getInstance();
        mPlayerPhonePower.registerPeceiver(mContext.getApplicationContext());
        mPlayerPhonePower.setPhonePowerListener(new YouPhonePowerListener() {
            @Override
            public void powerChange(int level, int scale) {
                int currentPercent = level*10 / scale;
                if(mPlayerBatteryPercent != currentPercent){
                    try {
//                        mPlayerTopImgvBattery.setImageResource(BatteryDrawableArray[currentPercent]);
                        mPlayerBatteryPercent = currentPercent;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    private void uninitPlayerBatteryPowerListener(){
        if (mPlayerPhonePower != null && mContext != null) {
            mPlayerPhonePower.unregisterPeceiver(mContext.getApplicationContext());
            mPlayerPhonePower = null;
        }
    }
    
    private String playerGetResource(int id){
        String res = "";
        if(!getObjectIsNull(mContext)){
            res =  mContext.getResources().getString(id);
        }
        return res;
    }
    
    private void setPlayerPlayStateText(int type){
        int resId = R.string.fullplayer_media_space;
        switch(type){
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENNING:
        	if(getPlayerIsSystemMediaPlayer()){
                resId = R.string.fullplayer_media_open_with_sys;
            }else{
                resId = R.string.fullplayer_media_open_with_fone;
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENSUCCESS:
            resId = R.string.fullplayer_media_open_success;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_READY_TO_PLAY:
            resId = R.string.fullplayer_media_space;
            break;

        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_TIME_OUT:
            resId = R.string.fullplayer_media_space;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_RESET:
            resId = R.string.fullplayer_media_space;
            break;
            
        default:
            break;
        }
//        mPlayerBtmPlayState.setText(playerGetResource(resId));
    }
    
    private void setPanelStatePlayOrPause(int  playStatus)throws YouPlayerException{
    	LOG.v(TAG, "setPanelStatePlayOrPause playStatus: ", playStatus);
    	switch(playStatus){
    	case You_full_screen_player_data_to_ui.Cls_you_player_status.NoneStatus:
    	    break;
    	    
    	case You_full_screen_player_data_to_ui.Cls_you_player_status.OpenningStatus:
    	    break;
    	    
        case You_full_screen_player_data_to_ui.Cls_you_player_status.PlayingStatus:
            mPlayerBtmImgbPlay.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_play);
            if(!getPlayerIsOnlineAudio()){
                setBackgroundLogoIsShow(false);
            }
            break;
            
        case You_full_screen_player_data_to_ui.Cls_you_player_status.PauseStatus:
            mPlayerBtmImgbPlay.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_pause);
            break;
            
        case You_full_screen_player_data_to_ui.Cls_you_player_status.BufferingStatus:
            break;
            
        default:
            break;
    	}
    }
    
    public int getPlayerSeekBarVolumeOrBrightIsVisible(){
    	LOG.v(TAG, "getPlayerSeekBarVolumeOrBrightIsVisible", "start");
        if(mPlayerSeekbarVolume.getVisibility() == View.VISIBLE
           && mPlayerSeekbarBrightness.getVisibility() != View.VISIBLE){
                return PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE; 
        }
        if(mPlayerSeekbarBrightness.getVisibility() == View.VISIBLE
           && mPlayerSeekbarVolume.getVisibility() != View.VISIBLE){
               return PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE; 
         }
         
        return PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE; 
    }
    
    private void setPlayerSeekBarVolumeOrBrightVisibleChanged(int type){
    	LOG.v(TAG, "setPlayerSeekBarVolumeOrBrightVisibleChanged type:", type);
        switch(type){
        case PLAYER_UI_SHOW_TYPE_VOLUME_VISIBLE:
            mPlayerSeekbarVolume.setVisibility(View.VISIBLE);
            mPlayerSeekbarBrightness.setVisibility(View.INVISIBLE);
            break;
            
        case PLAYER_UI_SHOW_TYPE_BRIGHT_VISIBLE:
            mPlayerSeekbarVolume.setVisibility(View.INVISIBLE);
            mPlayerSeekbarBrightness.setVisibility(View.VISIBLE);
            break;

        case PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE:
            mPlayerSeekbarVolume.setVisibility(View.INVISIBLE);
            mPlayerSeekbarBrightness.setVisibility(View.INVISIBLE);
            break;
            
        default:
            break;
        }
    }
    
    private void setPanelStateVolumeOrBrightSize(float size)throws YouPlayerException{
        if(mContext == null){
        	LOG.v(TAG, "setPanelStateVolumeOrBrightSize", "mContext is null");
            return;
        }
        LOG.v(TAG, "setPanelStateVolumeOrBright size: ", String.valueOf(size));
       if(mPlayerSeekbarVolume.getVisibility() == View.VISIBLE){
           mPlayerSeekbarVolume.adjust((-size / 2.0f) /mPlayerGestureView.getHeight());
           if( mPlayerAdapter.mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_AIRONE ){
        	   LOG.v(TAG, "setPlayVolume", "fullplayer set vol:"+mPlayerSeekbarVolume.getVol());
        	   YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_AIRONE_VOLUME,You_Core.FN_UI_EVT_TOUCH_UP ,null, mPlayerSeekbarVolume.getVol()*10 );
           }
           
       }else if(mPlayerSeekbarBrightness.getVisibility() == View.VISIBLE){
           mPlayerSeekbarBrightness.adjust((YouExplorer.instance).getWindow(), ((-size / 2.0f) / mPlayerGestureView.getHeight()));
       }
    }

      
    private void setPlayerAudioFullScreenState(int audioType)throws YouPlayerException{
    	LOG.v(TAG, "setPlayerVideoFullScreenState audioType:", audioType);
        switch(audioType){
        case You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_ENTIRE_MODE:
//            mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_loop_whole);
            break;
            
        case You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_RANDOM_MODE:
//            mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_loop_random);
            break;
            
        case You_player_media_info.Cls_fn_music_circle_mode.CIRCLE_SINGLE_MODE:
//            mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_loop_self);
            break;
            
        default:
            break;
        }

    }
    
    private void setPlayerVideoFullScreenState(int screenType)throws YouPlayerException{
    	LOG.v(TAG, "setPlayerVideoFullScreenState screenType:", screenType);
        switch(screenType){
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_FULL_SCR:
//            mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_fullscreen);
            break;
            
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_ORIGINAL_SCR:
//            mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_original);
            break;
            
        case You_player_media_info.Cls_you_player_render_mode.VIDEO_ASPECT_FULL_SCR:
//            mPlayerBtmImgbFullScreen.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_aspect);
            break;
            
        default:
            break;
        }
    }

    private void playerRefreshUIMaxAndMinTime(int total, int current) throws YouPlayerException{
        if(mPlayerAdapter.getMediaInfoIsLive()){
//            mPlayerBtmTimeMin.setText("");
//            mPlayerBtmTimeMax.setText("");
        }else{
            String max = extGetTimeStringFromTime(total);
            String min = extGetTimeStringFromTime(current);
//            mPlayerBtmTimeMin.setText(min);
//            mPlayerBtmTimeMax.setText(max);
        }
    }
    
    public String extGetTimeStringFromTime(int time) throws YouPlayerException{
        if (time < 0){
            time = 0;
        }
        long Hours = time / 3600000;
        long Minutes = (time / 60000) % 60;
        long Seconds = (time / 1000) % 60;
        String formatTime = "";
        if (Hours > 0) {
            formatTime = String.format("%1$02d:%2$02d:%3$02d", Hours, Minutes, Seconds);
        } else {
            formatTime = String.format("%1$02d:%2$02d", Minutes, Seconds);
        }
        return formatTime;
    }
    

    private void playerRefreshUITip(String tips, float size) throws YouPlayerException{
        mPlayerCenterTips.setVisibility(View.VISIBLE);
        mPlayerCenterTips.setTextSize(size);
        mPlayerCenterTips.setText(tips);
    }
    
    private void playerSetUITipInVisible() throws YouPlayerException{
        if(mPlayerCenterTips.getVisibility() == View.VISIBLE){
            mPlayerCenterTips.setVisibility(View.INVISIBLE);
        }
    }
    
    private void playerSetUIPanelShow() throws YouPlayerException{
        if(!getObjectIsNull(mView) && !isAllControllPanelIsVisible()){
//            mView.findViewById(R.id.fullplayer_topbar).setVisibility(View.VISIBLE);
//            mView.findViewById(R.id.fullplayer_topbar).refreshDrawableState();
//            mView.findViewById(R.id.fullplayer_topbar).invalidate();
            mView.findViewById(R.id.fullplayer_bottombar).setVisibility(View.VISIBLE);
        }
    }
    
    private void toHidePanel() throws YouPlayerException{
        if(!getObjectIsNull(mView) && isAllControllPanelIsVisible()){
//            (mView.findViewById(R.id.fullplayer_topbar)).setVisibility(View.INVISIBLE);
            (mView.findViewById(R.id.fullplayer_bottombar)).setVisibility(View.INVISIBLE);
            setPlayerSeekBarVolumeOrBrightVisibleChanged(PLAYER_UI_SHOW_TYPE_ALL_INVISIBLE);
            setRelativeListVisible(false);
            toExecuteNews();
        }
    }
    
    public boolean isAllControllPanelIsVisible() {
        if(getObjectIsNull(mView)){
        	LOG.e(TAG, "isAllControllPanelIsVisible", "mView is null");
            return false;
        }
//        if ((mView.findViewById(R.id.fullplayer_topbar)).getVisibility() != View.VISIBLE){
//            return false;
//        }
        if ((mView.findViewById(R.id.fullplayer_bottombar)).getVisibility() != View.VISIBLE){
            return false;
        }
        return true;
    }
    
    private void playerRefreshUITopPanel()throws YouPlayerException{
        playerRefreshUISystemTime();
        String mpe="";
        
        switch( mPlayerAdapter.mMediaInfo.is_system_player ){
        	case Player_type.PLAYER_TYPE_AIRONE:
        	   mpe = playerGetResource(R.string.aieonedecode);
        	   break;
        	case Player_type.PLAYER_TYPE_HARD_SOFT:
        	   mpe = playerGetResource(R.string.softharddecode);
         	   break;	
        	case Player_type.PLAYER_TYPE_SOFT:
         	   mpe = playerGetResource(R.string.softwaredecode);
          	   break;   
        	case Player_type.PLAYER_TYPE_SYSTEM:
         	   mpe = playerGetResource(R.string.hardwaredecode);
          	   break;   
        }
      
//        mPlayerTopDecodeTypeText.setText(mpe);
//        if( !getPlayerIsLocalAudio()&&!getPlayerIsLocalVideo() ){
//        	mView.findViewById(R.id.fullplayer_top_url_panel).setVisibility(View.VISIBLE);
//        }else{
//        	mView.findViewById(R.id.fullplayer_top_url_panel).setVisibility(View.GONE);
//        }
        
    }
    
    private void setPlayerControllPanelVisible(boolean isVisible){
    	LOG.v(TAG, "setPlayerControllPanelVisible isVisible : ", isVisible);
        try {
            if(isVisible){
                playerSetUIPanelShow();
                playerRefreshUITopPanel();
               
            }else {
                if(isAllControllPanelIsVisible()){
                    playerSetUIPanelHideAnimation();
                    toHidePanel();
                }
            }
            setRelativeListVisible(isVisible);
        } catch (YouPlayerException e) {
            e.printStackTrace();
        }
    }
    
    private void playerSetUIPanelHideAnimation() throws YouPlayerException{
        if(!getObjectIsNull(mView)){
//        	(mView.findViewById(R.id.fullplayer_topbar)).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
    	    (mView.findViewById(R.id.fullplayer_bottombar)).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_down_out));
        }
    }
    
    private void playerRefreshUISystemTime() {
//    	if( mPlayerTopSysTimeText.getVisibility() == View.VISIBLE ){
//	        Date date = new Date();
//	        long hour = date.getHours();
//	        long min = date.getMinutes();
//	        mPlayerTopSysTimeText.setText(String.format("%1$02d:%2$02d", hour, min));
//    	}
    }

    
	public void doPlayerErrorAction(int errorCode,int value){
		LOG.v(TAG, "doPlayerErrorAction errorCode:", errorCode);
	    int errorMessageID = R.string.fullplayer_media_unknow_error;
        switch (errorCode) {
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_NET_FAILED:
            errorMessageID = R.string.network_error;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPEN_FAILED:
            errorMessageID = R.string.fullplayer_media_open_fail;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_LIVE_MEDIA_DISCONNECT:
            errorMessageID = R.string.fullplayer_media_file_content_not_exist;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_DATA_FAILED:
            errorMessageID = R.string.fullplayer_media_file_content_not_exist;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_TIME_OUT:
            errorMessageID = R.string.fullplayer_media_file_content_not_exist;
            break;
            
        case PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION:
            errorMessageID = R.string.fullplayer_media_file_server_overtime;
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_FILE_NOT_EXIST:
        	if( value == 0 )
        		errorMessageID = R.string.fullplayer_media_file_not_get;
        	else
        		errorMessageID = R.string.fullplayer_media_file_not_exist;
            
            break;
            
        default:
            errorMessageID = R.string.fullplayer_media_unknow_error;
            break;
        }
        
//        if( false && errorMessageID == R.string.fullplayer_media_open_fail){
//        		try{
//	        		java.io.FileWriter fw = new java.io.FileWriter(new File("/sdcard/analyzeOpenFailed.log"),true);
//	        		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//	        		String dateTime = dateFm.format(new java.util.Date());
//	        		fw.append("\n\r"+dateTime+"  java show:open fail!"+"\n\r");
//	        		fw.close();
//        		}catch(Exception e){
//        			e.printStackTrace();
//        		}
//        }
//        
        
        showErrorExitDialog(errorMessageID);
	}
	
	private void playerExitOnRequest(){
		LOG.v(TAG, "playerExitOnRequest", "start");
		YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_DONE, You_Core.FN_UI_EVT_TOUCH_UP ,null, null);
	}
	
	private AlertDialog error_dialog;
	public void showErrorExitDialog(int messageResourceID){
	    setPlayerUnlockAciton();
		if( error_dialog != null ){
			LOG.v(TAG, "playerExitOnRequest", "error_dialog has exist");
	    	return;
		}
	    String message = playerGetResource(messageResourceID);
	    error_dialog = new AlertDialog.Builder(YouExplorer.instance)
        .setTitle(R.string.dialog_title)
        .setPositiveButton(R.string.dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface a0, int a1) {
                        dismissErrorExitDialog();
                    }
                }).setMessage(message).create();
	    error_dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            	LOG.v(TAG, "playerExitOnRequest", "onDismiss");
                playerExitOnRequest();
            }
	    });
	    error_dialog.setCanceledOnTouchOutside(false);
	    error_dialog.show();
	}
	
	
	public void dismissErrorExitDialog(){
		LOG.v(TAG, "dismissErrorExitDialog", "start");
	    if(error_dialog != null){
            error_dialog.dismiss();
            error_dialog = null;
        }
	}
	
	@Override
    public View getView() {
        return super.getView();
    }
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void finish() {
    	LOG.v(TAG, "finish", "finish");
        super.finish();
    }

    @Override
    public int getTag() {
        return super.getTag();
    }

    @Override
    public Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override
    public void onPause() {
    	LOG.v(TAG, "onPause", "onPause");
        super.onPause();
        
//        mFlingAirControl.unregisterListener();
    }

    @Override
    public void onRestart() {
    	LOG.v(TAG, "onRestart", "onRestart");
        super.onRestart();
    }

    public void setPlayerWaitingAction(boolean isWaiting){
    	LOG.v(TAG, "setPlayerWaitingAction", isWaiting);
        if(!getObjectIsNull(mView)){
            if(isWaiting){
                mView.findViewById(R.id.fullplayer_waitting_layout).setVisibility(View.VISIBLE);
            }else{
                mView.findViewById(R.id.fullplayer_waitting_layout).setVisibility(View.INVISIBLE);
            }
        }
    }
    
    public void playerGoBackground(){
    	LOG.v(TAG, "playerGoBackground", "start");
        dismissSpeedPopupwindow();
        dismissSubtitlePopupwindow();
//        dismissSelectAireOnePopupwindow();
        dismissErrorExitDialog();
        setPlayerIsBackground(true);

        setPlayerUnlockAciton();
        
        if(getPlayerIsVideoMedia()){
        	setBackgroundLogoIsShow(true);
        }
        if(getPlayerIsPauseStatus()){
            uninitPlayerBatteryPowerListener();
        }

        if(mPlayerAdapter.getMediaInfoIsLive() || getPlayerIsOnlineAudio() ){
        	LOG.v(TAG, "playerGoBackground", "fn_full_screen_btn_done");
            doPlayerADHideAllAction();
            YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_DONE, You_Core.FN_UI_EVT_TOUCH_UP ,null, null);
        }else{
        	LOG.v(TAG, "playerGoBackground", "fn_full_screen_btn_enter_background");
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_ENTER_BACKGROUND, You_Core.FN_UI_EVT_TOUCH_UP ,null, null);
            sendNotification();
        }
    }
    
    public void playerGoForeground(){
    	LOG.v(TAG, "playerGoForeground", "start");
        setPlayerIsBackground(false);
        onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_ENTER_FOREGROUND, You_Core.FN_UI_EVT_TOUCH_UP ,null, null);
        if(getPlayerIsVideoMedia()){
            mPlayerAdapter.setPlayerRefreshUISurfaceCB();
        }
        setPlayerControllPanelVisible(true);
        
        if (null == sdcardEjectReceiver) {
            initSdcardStateReceiver();
        }
        
        if (null == phoneStateReceiver) {
            initTelephoneReceiver();
        }
        removeNotification();
    }

    @Override
    public void onResume() {
    	LOG.v(TAG, "onResume", "onResume");
        super.onResume();
        
    }

    @Override
    public void onStart() {
    	LOG.v(TAG, "onStart", "onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
    	LOG.v(TAG, "onStop", "onStop");
        
        for(IStopExec exec : onDestoryExec)
        	exec.onStop();
        onDestoryExec.clear();
        
        toExecuteNews();

        
        super.onStop();

    }
    private void toExecuteNews(){
    	for(IStopExec exec:onNewExec)
        	exec.onStop();
        onNewExec.clear();
    }
    public boolean onTouchEvent(MotionEvent event) {
    	LOG.v(TAG, "onTouchEvent", "onTouchEvent");
        return super.onTouchEvent(event);
    }

    @Override
    public void setTag(int tag) {
        super.setTag(tag);
    }

    
    @Override
    public void onDestroy() {
    	LOG.v(TAG, "onDestroy ", "start");
        try {
            doPlayerADHideAllAction();
            setPlayerIsBackground(false);
            //setRelativeListVisible(false);
            setPlayerWaitingAction(false);
            setPlayerIsSeeking(false);
            setPlayerMediaLayoutDefault();
            setPlayerIsLock(false);
            setPlayerLockViewVisibleChanged(false);
            setPlayerLockBtnVisibleChanged(false);
            setPlayerLockRunnableRemove();
            
            
            removeNotification();
            removeNotification();
            uninitPlayerBatteryPowerListener();
            uninitSdcardReceiver();
            uninitTelephoneReceiver();
//            uninitShakeListener();
            uninitMediaTask();
            uninitScreenOffRegisters();
            dismissSpeedPopupwindow();
            dismissSubtitlePopupwindow();
//            dismissSelectAireOnePopupwindow();
            dismissErrorExitDialog();
            
//            mPlayerRelativeList.onDestoryed();
            mPlayerSeekbarBrightness.reset();
            mPlayerGestureView.removeAllOnGestureListeners();
            //mPlayerTopImgbChangeBright.setImageResource(R.drawable.fullplayer_icon_save_power);
//            mPlayerAdManager.onClearMap();
            mPlayerAdapter.destoryPlayerAdapter();
            Player_UIManager.freeBitmap();
            viewHasRemove();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.v(TAG, "onDestroy ", "end");
    }

    public void setSurfaceWidthHeight( int width, int height) {
    	LOG.v(TAG, "setSurfaceWidthHeight w:", width + " ,h" + height);
        if (width < height) {
            int temp = width;
            width = height;
            height = temp;
        }
        mpe_set_video_display(0, 0, width, height);
//        if (!getPlayerIsSystemMediaPlayer()) {
//            Rect rect = new Rect(0, 0, width, height);
//            Player_UIManager.RenderLastFrame(rect);
//       } else {
           getCurrentSurfaceView(getPlayerIsSystemMediaPlayer()).invalidate();
//       }
    }
    
    public void mpe_set_video_display(int left, int top, int right, int bottom) {
        SurfaceView surfaceView = getCurrentSurfaceView(getPlayerIsSystemMediaPlayer());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)surfaceView.getLayoutParams();
        lp.leftMargin = left;
        lp.topMargin  = top;
        lp.width = right - left;
        lp.height = bottom - top;
        surfaceView.setLayoutParams(lp);
//        if(!getPlayerIsSystemMediaPlayer()){
//            Player_UIManager.Uim_set_video_display(left, top, right, bottom);
//        }
    }
    
    
    /** 强制横屏 */
    public int extScreenGetWidth() {
        return mPlayerAdapter.extGetScreenWidth();
    }

    public int extScreenGetHeight() {
        return mPlayerAdapter.extGetScreenHeight();
    }

    private String getCurrentMediaTypeName(){
        return mPlayerFileName;
    }
    
    private void setCurrentMediaTypeName(String fileName){
    	LOG.v(TAG, "setCurrentMediaTypeName" , fileName);
//        mPlayerBtmMediaName.setText(fileName);
        mPlayerFileName = fileName;
    }
    
    private void playerRefreshUIMediaLocalAudio() throws YouPlayerException{
        if(getObjectIsNull(mView)){
        	LOG.i(TAG, "playerRefreshUIMediaLocalAudio", "mView is null");
           return; 
        }
        if(mMediaFile != null){
            String albumName = mMediaFile.getPlayingAudioAlbumName();
            String singerName  = mMediaFile.getPlayingAudioSingerName();
            if (albumName == null || albumName.length() == 0){
                albumName = playerGetResource(R.string.fullplayer_message_unknow);
            }
            if (singerName == null || singerName.length() == 0){
                singerName = playerGetResource(R.string.fullplayer_message_unknow);
            }
            
            mMediaFile.createFileIcon(mContext, new MediaInfoCallBack() {
                
                @Override
                public void callback(String path, Bitmap bitmap, boolean isHD, String msg0,String msg1, long playedTime) {
                    if(bitmap == null && mView != null){
                        ((ImageView) mView.findViewById(R.id.full_audio_thumbnail)).setImageResource(R.drawable.youplayer_player_audio_thumbnail);
                    }else if (mView != null && path != null && path.equals(mMediaFile.getPath())) {
                        ((ImageView) mView.findViewById(R.id.full_audio_thumbnail)).setImageBitmap(bitmap);
                    }
                }

				@Override
				public void callBack(int position, Bitmap bitmap) {
					// TODO Auto-generated method stub
					
				}

				
            });

            ((TextView)mView.findViewById(R.id.bar_text_audio_file_value)).setText(getCurrentMediaTypeName());
            ((TextView)mView.findViewById(R.id.bar_text_audio_album_value)).setText(albumName);
            ((TextView)mView.findViewById(R.id.bar_text_audio_singer_value)).setText(singerName);
        }
    }

    private void setPlayerLyricViewVisibleChanged(boolean isVisible){
        if(isVisible){
        	mView.findViewById(R.id.full_audio_text_container).setVisibility(View.GONE);
            mPlayerLyricView.setVisibility(View.VISIBLE);
        }else{
            mPlayerLyricView.setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.full_audio_text_container).setVisibility(View.VISIBLE);
        }
    }
    
    private void playerRefreshUIMediaAudio() throws YouPlayerException{
        if(getPlayerIsLocalAudio()){
            playerRefreshUIMediaLocalAudio();
        }
    }
    
    private void setCurrentTaskTotalDuration(long total,int current){
        mPlayerBtmPlaySeekbar.setMax((int)total);
        mPlayerBtmPlaySeekbar.setProgress(current);
    }
    
    private int getCurrentTaskTotalDuration(){
        return mPlayerBtmPlaySeekbar.getMax();
    }
    
    private void playerRefreshUIPlayPosSeekbar(int progress){
        mPlayerBtmPlaySeekbar.setProgress(progress);
    }

    public boolean getGestureIsEnable(){
        return  true;
    }
    
    public SurfaceView getCurrentSurfaceView(boolean isSysSurface){
    	LOG.v(TAG, "getCurrentSurfaceView, isSysSurface: ", isSysSurface);
        if (isSysSurface){
            return mPlayerSysSurfaceView;
        }else{
            return mPlayerYouSurfaceView;
        }
    }
    
    public void setCurrentSurfaceViewVisible(int player_type){
    	LOG.v(TAG, "setCurrentSurfaceView, player_type: ", player_type);
        
        switch(player_type){
	        case Player_type.PLAYER_TYPE_HARD_SOFT:
	        case Player_type.PLAYER_TYPE_SYSTEM:
	        	mPlayerYouSurfaceView.setVisibility(View.INVISIBLE);
	            mPlayerSysSurfaceView.setVisibility(View.VISIBLE);
	            break;
	        case Player_type.PLAYER_TYPE_SOFT:
	        	mPlayerSysSurfaceView.setVisibility(View.INVISIBLE);
	        	mPlayerYouSurfaceView.setVisibility(View.VISIBLE);
	            break;
        
        }
//        LOG.v(TAG, "setCurrentSurfaceView, mPlayerMediaVideoLayout getVisibility", mPlayerMediaVideoLayout.getVisibility());
       
        if( !getPlayerIsVideoMedia() || mPlayerAdapter.mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_AIRONE ){
        	YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_VIEW_READY, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
        }
        else if( mPlayerSysSurfaceView.getVisibility() == View.VISIBLE )
     		((YouPlayerSurfaceView)mPlayerSysSurfaceView).setOnCreateExecute(mOnSurfaceCreatedExecute);
     	else if ( mPlayerYouSurfaceView.getVisibility() == View.VISIBLE )
     		((YouPlayerSurfaceView)mPlayerYouSurfaceView).setOnCreateExecute(mOnSurfaceCreatedExecute);

        
        
        
    }
    
    private void setPlayerSeekbarIsEnable(boolean isEnable){
        mPlayerBtmPlaySeekbar.setEnabled(isEnable);
    }
    
    private boolean getPlayerIsSystemMediaPlayer(){
        return mPlayerAdapter.getPlayerIsSystemMediaPlayer();
    }
    
    private int getMediaInfoMediaType(){
        return mPlayerAdapter.getMediaInfoMediaType();
    }
    

    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	LOG.v(TAG, "onKeyUp", "onKeyUp"+keyCode );
        return false;
    }

    @Override
    public boolean onkeyDown(int keyCode, KeyEvent event) {
    	LOG.v(TAG, "onkeyDown", "start");
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_UP:
            mPlayerSeekbarVolume.up();
            break;
            
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            mPlayerSeekbarVolume.down();
            break;
            
        case KeyEvent.KEYCODE_BACK:
            if(getPlayerIsLock()){
                return true;
            }
            doPlayerADHideAllAction();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                	YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_DONE, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
                }
            }, 150);
            
            return true;
        default:
            break;
        }
        
        return false;
    }
    
    private void onRequestCoreServiceForSeek(){
    	LOG.v(TAG, "onRequestCoreServiceForSeek", "start");
        int pos = getPlayerSeekBarProgress();
        LOG.v(TAG, "onRequestCoreServiceForSeek pos:", pos);
        onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_SEEK,You_Core.FN_UI_EVT_TOUCH_UP, null, pos);
    }
    
    private Handler mSeekPreviewHandler = new Handler();
    private Runnable mSeekPreviewRunnable = new Runnable(){
        public void run(){
        	LOG.v(TAG, "mSeekPreviewRunnable", "start");
            onRequestCoreServiceForSeek();
        }
    };
    
    private void setSeekPreviewCallback(){
        removeSeekPreviewCallback();
        mSeekPreviewHandler.postDelayed(mSeekPreviewRunnable, (long)(0.5f * 1000));
    }
    
    private void removeSeekPreviewCallback(){
        mSeekPreviewHandler.removeCallbacks(mSeekPreviewRunnable);
    }
    
    private void doPlayerPlayAction(int playerStatus) throws YouPlayerException{
        setPanelStatePlayOrPause(playerStatus);
    }

    private void doPlayerRelationVideoAction(You_full_screen_player_data_to_ui.Cls_fn_data_related_t coreData) throws YouPlayerException{
//        if( coreData.related_content != null){
//            mPlayerRelativeList.initDatas(coreData.related_content, new YouPlayerRelativeList.OnSelectedListener() {
//                @Override
//                public boolean onSelected(int index) {
//                    if(getPlayerIsReadyToPlay()){
//                        onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_RELATED_CELL,You_Core.FN_UI_EVT_TOUCH_UP,0,index);
//                        setPlayerViewsInitalizeValue();
//                        return true;
//                    }
//                    return false;
//                }
//
//				@Override
//				public void onTouch(int action) {
//					if( action == MotionEvent.ACTION_MOVE ){
//						onRequestCoreService(PLAYER_FN_UI_MSG_RELATIVE_TOUCH_MOVE,You_Core.FN_UI_EVT_TOUCH_UP,null,null);
//					}else if( action == MotionEvent.ACTION_UP ){
//						onRequestCoreService(PLAYER_FN_UI_MSG_RELATIVE_TOUCH_UP,You_Core.FN_UI_EVT_TOUCH_UP,null,null);
//					}
//				}
//            });
//        }
    }
    
    private void doPlayerAllADAction(You_full_screen_player_data_to_ui.Cls_fn_ad_data_t coreData)throws YouPlayerException{
    	try{
        HashMap map = YouUtility.adsListToHashMap( coreData , mContext);
        if(map != null){
        	LOG.v(TAG, "adMap:", map.toString());
//            mPlayerAdManager.onClearMap();
//            mPlayerAdManager.setAdMap(map);
        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private synchronized void doPlayerADShowAction(You_full_screen_player_data_to_ui.Cls_fn_ad_content_t coreData){
        try {
            if(coreData == null || getPlayerIsBackground()){
            	LOG.e(TAG, "doPlayerADShowAction " , "coreData is null" + "isBackground:" + getPlayerIsBackground());
                return;
            }
            LOG.v(TAG, "doPlayerADShowAction adType:" , coreData.adtype);
            switch(coreData.adtype){
           
            default:
                break;            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    private void doPlayerADHideAction(int adType)throws YouPlayerException{
    	LOG.v(TAG, "doPlayerADHideAction adType:" , adType);

    }

    private void doPlayerADHideAllAction() {
    	LOG.v(TAG, "doPlayerADHideAllAction" , "start");
//        mPlayerAdManager.onDestoryAllAdView();
    }
    
    private void doPlayerMediaInfoAction(You_player_media_info coreData)throws YouPlayerException{
        if(getPlayerIsLocalAudio()){
            setPlayerLyricViewVisibleChanged(false);
            mInitMediaTask = new InitMediaTask();
            mInitMediaTask.execute();
            if(getPlayerIsBackground()){
                removeNotification();
                sendNotification();
            }
        }
        if(!mPlayerAdapter.getMediaInfoIsLive()){
            setPlayerSeekbarIsEnable(true);
        }
        
        setCurrentTaskTotalDuration(coreData.duration,coreData.start_play_time);
        playerRefreshUIMaxAndMinTime(getCurrentTaskTotalDuration(), coreData.start_play_time);
    }
    
    
    private void doPlayerSeekBarAction(int progress)throws YouPlayerException{
    	LOG.v(TAG, "doPlayerSeekBarAction:" , "" + progress + " ,isSeeking:" + getPlayerIsSeeking());
        if(!getPlayerIsSeeking()){
            playerRefreshUIPlayPosSeekbar(progress);  
        }
    }
    
    private void doPlayerSetSurfaceRect(Rect rect) throws YouPlayerException{
        setSurfaceWidthHeight(rect.width(), rect.height());
    }

    private void doPlayerRefreshUIAudioState(int audioPlayMode) throws YouPlayerException{
        setPlayerAudioFullScreenState(audioPlayMode);
    }

    
    private void doPlayerSetSubtitleTypeAction(You_full_screen_player_data_to_ui.Cls_fn_full_screen_audio_and_subtitle coreData) {
        mPlayerAudioSubtitle = new YouPlayerAudioSubtitle(mContext, coreData);
        mPlayerAudioSubtitle.showDialog((RelativeLayout)mView.findViewById(R.id.fullplayer_bottombar));
    }


    private void doPlayerPlayToEndAction(int v) {
    	 for(IStopExec exec:onNewExec)
         	exec.onStop();
         onNewExec.clear();
        onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_STOP, You_Core.FN_UI_EVT_TOUCH_UP, null, v);
    }

    private void doPlayerBufferPercentAction(Integer value) throws YouPlayerException{
        String msg = playerGetResource(R.string.fullplayer_is_buffering);
        if (value != null ){
            msg += value + "%";
        }
        if(getPlayerIsLock()){
            playerRefreshUITip(msg, PLAYER_TIPS_UI_FONT_SIZE_20);
        }else{
//            mPlayerBtmPlayState.setText(msg);
        }
    }


    private void doPlayerBufferEndAction() throws YouPlayerException{
        String msg = playerGetResource(R.string.fullplayer_is_buffering);
        msg += "100%";
        if(getPlayerIsLock()){
            playerRefreshUITip(msg, PLAYER_TIPS_UI_FONT_SIZE_20);
            playerSetUITipInVisible();
        }else{
//            mPlayerBtmPlayState.setText(msg);
        }
    }


    private void doPlayerBufferStartAction() throws YouPlayerException{
        String msg = playerGetResource(R.string.fullplayer_is_buffering);
        if(getPlayerIsLock()){
            playerRefreshUITip(msg, PLAYER_TIPS_UI_FONT_SIZE_20);
        }else{
//            mPlayerBtmPlayState.setText(msg);
        }
    }


    private void doPlayerShowLyricAction(YouPlayerLyrics tagyouLyrics) {
        mPlayerLyricView.init(tagyouLyrics);
        //setPlayerGestureViewIsVisible(false);
        setPlayerLyricViewVisibleChanged(true);
        
    }


    private void doPlayerShowSubtitleAction(String subtitle) {
    	LOG.v(TAG, "doPlayerShowSubtitleAction subtitle:", subtitle);
        if(getPlayerIsVideoMedia()){
            if(subtitle != null && !subtitle.equals(mPlayerSubtitleView.titleStr) && mPlayerSubtitleView != null){
                mPlayerSubtitleView.titleStr = subtitle;
                mPlayerSubtitleView.invalidate();
            }
        }
    }



    private void doPlayerOpeningAction(String fileName) {
        setPlayerPlayStateText(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENNING);
        setCurrentMediaTypeName(fileName);
    }
    

    
    private void doPlayerSetSeekingSeekBarAction(int progress) throws YouPlayerException{
        setPlayerSeekBarProgress( progress);        
    }
    
    private void doPlayerSetTipsInVisibleAction() throws YouPlayerException{
        playerSetUITipInVisible();
    }
    
    private void doPlayerReadyToPlayAction(int type) {
        setPlayerPlayStateText(type);
    }

 
    
    private void doPlayerSetTipsRefreshAction(YouPlayerAdapter.Tips tips) throws YouPlayerException{
    	LOG.v(TAG, "doPlayerSetTipsRefreshAction text:", tips.text);
        if(PLAYER_UI_MSG_SEEKBAR.equals(tips.text)){
            playerRefreshUITip(extGetTimeStringFromTime(getPlayerSeekBarProgress()), PLAYER_TIPS_UI_FONT_SIZE_LARGE);
        }else{
            playerRefreshUITip(tips.text, tips.size);
        }
    }
     
    
    private boolean getSeekBarIsEnableForShare(){
        if(mPlayerBtmPlaySeekbar.getProgress() < 1000 || mPlayerBtmPlaySeekbar.getProgress() > (mPlayerBtmPlaySeekbar.getMax() - 1000)){
            return false;
        }
        return true;
    }
    
    private String getWeiBoURL(){
        return playerGetResource(R.string.share_weibo_url);
    }
    private void doPlayerShare(String picpath ,String shareUrl){
  	
    }
    private void doPlayerSetShareAction(String videourl,int time,String shareUrl){
        if((videourl == null || getPlayerIsVideoMedia() || getPlayerIsLocalAudio()) &&  (mPlayerAdapter.getMediaInfoIsLive() || getSeekBarIsEnableForShare()) ){
            weiboAction(videourl,time,shareUrl,getPlayerIsSystemMediaPlayer());
            return;
       }
        doPlayerShare(null,shareUrl);
 
    }
    
    private void doPlayerSetNetBufferingAction(String value) {
//        mPlayerBtmPlayState.setText(value);
    }
    
    private AsyncTask image_task;
    public void weiboAction(final String filepath,final int pos,final String url,final boolean systemplayer){
        if(getObjectIsNull(mContext)){
        	LOG.e(TAG, "weiboAction", "mContext is null");
            return;
        }
        LOG.v(TAG, "weiboAction", "start");
		final String filename = YouUtility
				.getyouCacheFolder(YouUtility.ONLINE_PICTURE_TYPE)
				+ File.separator + "weibo_fone.png";
        File file = new File(filename);
        if(file != null){
            if(file.exists()){
            	LOG.v(TAG, "weiboAction exists ", "delete");
                file.delete();
            }
        }
        final ProgressDialog dialog = new ProgressDialog(YouExplorer.instance);
        dialog.setMessage(mContext.getResources().getText(R.string.share_getimage));
        dialog.setOnCancelListener(new OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                if(image_task != null){
                    image_task.cancel(true);
                    LOG.v(TAG, "image_task", "cancel");
                }
            }
        });
        
        dialog.getWindow().setGravity(Gravity.CENTER); 
        dialog.show();
        
        image_task = null;
        image_task = new AsyncTask(){
            
            @Override
            protected Object doInBackground(Object... params) {
                if(getPlayerIsVideoMedia()){
                    try{
                        if( systemplayer ){
                            Player_UIManager.fone_media_thumbnail_init(320, 240);
                            YouPlayerBitMap tag = (YouPlayerBitMap) Player_UIManager
									.fone_media_player_get_thumbnail_from_video(filepath, pos, 16, 320, 240);
							LOG.v(TAG, "fone_media_thumbnail_init", "start");
                            if (tag != null&&tag.m_bitmap != null) {
                            	YouUtility.saveWeiboBitmap((Bitmap) tag.m_bitmap,filename);
                            }
                        }else{
                            Player_UIManager.fone_media_player_seek_preview_init(320, 240);
                            Bitmap weiboBitmap = (Bitmap)  Player_UIManager.fone_media_player_seek_preview();
                            LOG.v(TAG, "fone_media_player_seek_preview_init", "start");
                            YouUtility.saveWeiboBitmap(weiboBitmap,filename);
                        }   
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        if( systemplayer ){
                            Player_UIManager.fone_media_thumbnail_uninit();
                            LOG.v(TAG, "fone_media_thumbnail_uninit", "start");
                        }else{
                            Player_UIManager.fone_media_player_seek_preview_uninit();
                            LOG.v(TAG, "fone_media_player_seek_preview_uninit", "start");
                        }
                   }
                }
                try {
                    if(getPlayerIsLocalAudio() && mMediaFile != null){
                        mMediaFile.createFileIcon(mContext, new MediaInfoCallBack() {
                            
                            @Override
                            public void callback(String path, Bitmap bitmap, boolean isHD, String msg0,String msg1, long playedTime) {
                                if(bitmap != null ){
                                	YouUtility.saveWeiboBitmap(bitmap,filename);
                                }else{
                                	Bitmap default_bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.youplayer_player_audio_thumbnail);
                                	YouUtility.saveWeiboBitmap(default_bitmap,filename);
                                	if( default_bitmap != null ){
                                	    default_bitmap.recycle();
                                	    default_bitmap = null;
                                	}
                                }
                            }

							@Override
							public void callBack(int position, Bitmap bitmap) {
								// TODO Auto-generated method stub
								
							}

							
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            protected void onPostExecute(Object result) {
                dialog.dismiss();
                //FoneUtility.toShareToSystem(mContext,filename,url, getCurrentMediaTypeName());
                doPlayerShare(filename,url);
            };
            
        };
        image_task.execute();
    }    
    
    private void doPlayerSetBrightAction() throws YouPlayerException{
        if(mContext == null){
        	LOG.e(TAG, "doPlayerSetBrightAction", "mContext is null");
            return;
        }

    }
    
    
    public void doPlayerStopAction(int isStop) {
        if(isStop == 0){
            try {
                reInitPlayerViews();
            } catch (YouPlayerException e) {
                e.printStackTrace();
            }
            setBackgroundLogoIsShow(true);
            mSurface_width = mSurface_height = 0;
        }
    }
    
    private void doPlayerEngineAction() throws YouPlayerException{
        setPlayerMediaLayout(getMediaInfoMediaType());
        setPlayerUsedSurface();
        setPlayerGestureView();
        playerRefreshUITopPanel();
        setQualityIcon();
        mSpeed = 1;
    }
    private void setQualityIcon(){
    	
//    	 if (getPlayerIsLocalAudio() || getPlayerIsLocalVideo() || getPlayerIsOnlineAudio() ){
////    		 mPlayerBtmImgbQuality.setVisibility(View.INVISIBLE);
//    		 return;
//    	 }	 
//    	 else
//    		 mPlayerBtmImgbQuality.setVisibility(View.VISIBLE);
    	
    	if(mPlayerAdapter != null && mPlayerAdapter.mMediaInfo.current_dfnt == 0 ){
//    		mPlayerBtmImgbQuality.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_quality1);
    		LOG.i(TAG, "setQualityIcon", "quality:0");
    	}
    	else{
    		LOG.i(TAG, "setQualityIcon", "quality:"+mPlayerAdapter.mMediaInfo.current_dfnt);
//    		if( mPlayerAdapter.mMediaInfo.current_dfnt == 3 ) mPlayerBtmImgbQuality.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_quality3);
//    		else if( mPlayerAdapter.mMediaInfo.current_dfnt == 2 ) mPlayerBtmImgbQuality.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_quality2);
//    		else  mPlayerBtmImgbQuality.setImageResource(R.drawable.youplayer_fullplayer_bottom_btn_quality1);
    	}	
    	
    }

    
    private void onMessageCallBack(You_full_screen_player_data_to_ui coreData)throws YouPlayerException{
        if(coreData == null){
        	LOG.e(TAG, "doPlayerCallbackAction", "coreData is null");
            return;
        }
        int showType = coreData.type;
        switch(showType){
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BASE:
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENNING:
            if(coreData.value != null){
                doPlayerOpeningAction((String)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPENSUCCESS:
        	
        	 mPlayerSeekbarBrightness.init();
        	    	 
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPEN_FAILED:
            doPlayerErrorAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_OPEN_FAILED,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            break;

        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_DATA_FAILED:
            doPlayerErrorAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_DATA_FAILED,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            break;

        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_READY_TO_PLAY:
            doPlayerReadyToPlayAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_READY_TO_PLAY);
            setPlayerIsSeeking(false);
            if(getPlayerIsLock()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPlayerLockAction();        
                    }
                }, 100);
            }
            break;
          
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_VIDEO_VIEW:
            break;

        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_VIDEO_RENDER:
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_MEDIA_INFO:
        	mPlayerBtmImgbNext.setEnabled(true);
        	mPlayerBtmImgbPrevious.setEnabled(true);
            if(coreData.value != null){
                doPlayerMediaInfoAction((You_player_media_info)coreData.value);
//  				if(!getPlayerIsLock()){
//                	setRelativeListVisible(PlayerRelativeList.isShouldShow());
//				}
  				
//                if(!getPlayerIsOnlineAudio()){
//                    setBackgroundLogoIsShow(false);
//                }
               
            }
            if( getPlayerIsVideoMedia() && mPlayerAdapter.mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_SOFT ){
            	
            	int width = mPlayerAdapter.mMediaInfo.width;
            	int height = mPlayerAdapter.mMediaInfo.height;
            	mSurface_width = width;
            	mSurface_height = height;
            	mPlayerYouSurfaceView.getHolder().setFixedSize(width, height);	
            }
            
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PLAY_BTN:
            if(coreData.value != null){
                doPlayerPlayAction((Integer)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PROGRESS_BAR:
            if(coreData.value != null){
                doPlayerSeekBarAction((Integer)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_SUBTITLE:
            if(coreData.value != null){
                doPlayerShowSubtitleAction(coreData.value.toString());
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_LYRIC:
            if(coreData.value != null){
                doPlayerShowLyricAction((YouPlayerLyrics)coreData.value);
            }
            break;  
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_START:
            doPlayerBufferStartAction();
            break; 
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_END:
            doPlayerBufferEndAction();
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_BUFFER_PERCENT:
            if(coreData.value != null){
                doPlayerBufferPercentAction((Integer)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_SEEK_THUMBNAIL:
            break;      
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_DO_SEEK_PREVIEW:
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_STOP:
            break;     
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_PLAY_TO_END:
        	if( coreData.value != null ){
        		doPlayerPlayToEndAction((Integer)coreData.value);
        	}
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_ALL_AD:
            if(coreData.value != null){
                doPlayerAllADAction((You_full_screen_player_data_to_ui.Cls_fn_ad_data_t)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_AD_VIEW:
            if(coreData.value != null){
                doPlayerADShowAction((You_full_screen_player_data_to_ui.Cls_fn_ad_content_t)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_HIDE_AD_VIEW:
            if(coreData.value != null){
                doPlayerADHideAction((Integer)coreData.value);
            }
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_HIDE_ALL_AD_VIEWS:
            doPlayerADHideAllAction();
            break; 

        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SET_PLAYER_ENGINE_TYPE:
            doPlayerEngineAction();
            break; 
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SET_SUBTITLE_TYPE:
            break; 
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SET_MEDIA_TYPE:
            break; 
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_CHANGE_MODE:
            break; 
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_AUDIO_SUBTITLE:
            if(coreData.value != null){
                doPlayerSetSubtitleTypeAction((You_full_screen_player_data_to_ui.Cls_fn_full_screen_audio_and_subtitle)coreData.value);
            }
            break; 
          
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_NET_FAILED:
            doPlayerErrorAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_NET_FAILED,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_TIME_OUT:
            doPlayerErrorAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_TIME_OUT,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            doPlayerPlayToEndAction(2);
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_RESET:
//            doPlayerShowConnResetAction(Cls_fn_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_CONN_RESET);
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_NO_NEXT_PREVIOUD_FILE:
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_LIVE_MEDIA_DISCONNECT:
            doPlayerErrorAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_LIVE_MEDIA_DISCONNECT,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            break;
            
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_RELATED_VIDEO:
        	if( coreData.value != null ){
        		doPlayerRelationVideoAction((You_full_screen_player_data_to_ui.Cls_fn_data_related_t) coreData.value);
        	}
        	break;    

        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_FILE_NOT_EXIST:
            doPlayerErrorAction(You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_FILE_NOT_EXIST,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            break; 
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_SHARE_DATA:
        	LOG.v(TAG,"SHARE_DATA",(String)coreData.value);
        	String urltime = (String)coreData.value;
        	String[] datas = urltime.split("\\^_\\^");
        	
        	if( datas.length != 3 ){
        		//Log.i("lrl","url:");
        		doPlayerSetShareAction(null,0,null);
        		
        	}	
        	else{
        		
        		//Log.i("lrl","url:"+datas[2]);
        		
        		int index = urltime.lastIndexOf(':');
        		doPlayerSetShareAction(datas[0],Integer.parseInt(datas[1]),datas[2]);
        	}
        	break;
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_ONLINE_VIDEO_PLAY_URL:
        	String url = (String)coreData.value;
        	if( url == null || url.trim().length() == 0 ) {
//        		mView.findViewById(R.id.fullplayer_top_url_panel).setVisibility(View.GONE);
        	} else {
//        		mView.findViewById(R.id.fullplayer_top_url_panel).setVisibility(View.VISIBLE);
//        		((TextView)mView.findViewById(R.id.fullplayer_top_url)).setText( mView.getResources().getString(R.string.full_player_url_src) + url);
//        		((TextView)mView.findViewById(R.id.fullplayer_top_url_button)).setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_OURL, You_Core.FN_UI_EVT_TOUCH_UP ,null, null);						
//					}
//				});
        	}
        	break;
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_AIRONE_AUTHORIZED:{
        	
        	String value = (String)coreData.value;
        	LOG.v(TAG,"FN_PAGE_FULL_PLAYER_SHOW_AIRONE_AUTHORIZED",value);
        	
        	doPlayerAironeAuthorized(value);
        	
        	
        	break;
        }
        case PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION:
            doPlayerErrorAction(PLAYER_FN_UI_MSG_SELF_NET_TIMER_OUT_ACTION,coreData.value instanceof Integer ? (Integer)coreData.value:0);
            doPlayerPlayToEndAction(2);
            break;
            
        case PLAYER_FN_UI_MSG_CONTROLL_PANEL_VISIBLITY:
            if(coreData.value != null){
//            	Toast.makeText(mContext, "mPlayerAdapter.mMediaInfo.is_system_player="+mPlayerAdapter.mMediaInfo.is_system_player, Toast.LENGTH_SHORT).show();
                if((Boolean)coreData.value && getPlayerIsLock()){
                	LOG.v(TAG, "controll_panel_visiblity", "isLock unvisible controll panel");
                // 如果为DLNA投放模式，屏幕控件不隐藏  
                } else if(!(Boolean)coreData.value && mPlayerAdapter.mMediaInfo.is_system_player == Player_type.PLAYER_TYPE_AIRONE) {
                	LOG.v(TAG, "controll_panel_visiblity", "dlna mode visible controll panel");
                }else{
                    setPlayerControllPanelVisible((Boolean)coreData.value);
                }
            }
            break; 

        case PLAYER_FN_UI_MSG_PLAY_MODE_VIDEO:
            if(coreData.value != null){
                setPlayerVideoFullScreenState((Integer)coreData.value);
            }
            break; 

        case PLAYER_FN_UI_MSG_PLAY_MODE_AUDIO:
            if(coreData.value != null){
                doPlayerRefreshUIAudioState((Integer)coreData.value);
            }
            break;
            
        case PLAYER_FN_UI_MSG_SURFACE_RECT:
            if(coreData.value != null){
                doPlayerSetSurfaceRect((Rect)coreData.value);
            }
            break; 
            
        case PLAYER_FN_UI_EVT_PAGE_SEEK_LEFT_RIGHT:
            if(coreData.value != null){
                doPlayerSetSeekingSeekBarAction((Integer)coreData.value);
            }
            break;
            
        case PLAYER_FN_UI_MSG_TIP_INVISIBILITY:
            doPlayerSetTipsInVisibleAction();
            break;
            
        case PLAYER_FN_UI_MSG_TIP_VISIBILITY_REFRESH:
            if(coreData.value != null){
                doPlayerSetTipsRefreshAction((YouPlayerAdapter.Tips)coreData.value);
            }
            break;
            
        case PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT:
            if(coreData.value != null){
            	setPlayerSeekBarVolumeOrBrightVisibleChanged((Integer)coreData.value);
            }
            break;
            
        case PLAYER_FN_UI_MSG_PLAY_VOLUME_BRIGHT_SIZE:
            if(coreData.value != null){
                setPanelStateVolumeOrBrightSize((Integer)coreData.value);
            }
            break;
        case PLAYER_FN_UI_MSG_SOFT_VOL:{
        	setSoftVolume((Integer)coreData.value);
        }
        break;
        case PLAYER_FN_UI_MSG_PLAY_RELATION_VISIBLITY:
//            if(coreData.value != null){
//                doPlayerSetRelativeListAction((Boolean)coreData.value);
//            }
            break;
            
        case PLAYER_FN_UI_MSG_PLAY_SHARE:
            //doPlayerSetShareAction();
            break;
            
        case PLAYER_FN_UI_EVT_PAGE_CHANGE_BRIGHT:
            doPlayerSetBrightAction();
            break;
            
        case PLAYER_FN_UI_MSG_NET_BUFFERING_VISIBLITY:
            if(coreData.value != null){
                doPlayerSetNetBufferingAction((String)coreData.value);
            }
            break;
            
        case PLAYER_FN_UI_MSG_WAITTING:
            if(coreData.value != null){
                setPlayerWaitingAction((Boolean)coreData.value);
            }
        	break;
        	
        case PLAYER_FN_UI_MSG_LOCK:
            doPlayerLockAction();
            break;
        case PLAYER_FN_UI_MSG_UPDATE_SYSTIME:
        	playerRefreshUISystemTime();
        	break;
        case You_page_full_player_show_type.FN_PAGE_FULL_PLAYER_SHOW_FEE_TIPS:
            onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PAUSE, You_Core.FN_UI_EVT_TOUCH_UP, null, 1);
        	setPlayerControllPanelVisible(true);
        	unlock();
        	if( !mFeeShowed )
        		doShowTopDialog((You_full_screen_tips) coreData.value);
        	break;
        default:
            break;
        }
        coreData = null;
    }
    private void doPlayerAironeAuthorized(String value) {
		final EditText txt = new EditText(YouExplorer.instance);
		int index = value.indexOf("^V^");
		if( index < 0 ) return;
		
		String name = value.substring(0,index);
		final String uuid = value.substring(index+3);
		
		
		new AlertDialog.Builder(YouExplorer.instance).setTitle("请输入\""+name+"\"密码").setIcon(  
				android.R.drawable.ic_dialog_info).setView(txt)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						LOG.i(TAG, "FN_FULL_SCREEN_BTN_AIRONE_AUTHORIZE uuid:pwd",uuid+":"+txt.getText());
						onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_AIRONE_AUTHORIZE, You_Core.FN_UI_EVT_TOUCH_UP, uuid,txt.getText().toString());
						
					}
				})  
				.setNegativeButton("取消", null).show(); 
		
	}

	boolean mFeeShowed = false;
	private void doShowTopDialog(final You_full_screen_tips tips) {
		mFeeShowed = true;
		LOG.i(TAG, "doShowTopDialog tips:title:", tips.title);
		LOG.i(TAG, "doShowTopDialog tips:durl:", tips.durl);
		LOG.i(TAG, "doShowTopDialog tips:content:", tips.content);
		
		AlertDialog.Builder builder = new Builder(YouExplorer.instance);
		builder.setMessage(tips.content);
		builder.setTitle(tips.title);
		
//		if( tips.durl != null && tips.durl.length() > 0 ){
		if(tips.btns == 1 || tips.btns == 3) {
			builder.setPositiveButton(tips.btnJumpLabel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mFeeShowed = false;
					onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_FEE_DURL, You_Core.FN_UI_EVT_TOUCH_UP, null, tips.durl);
				}
	
			});
		}
//		}
		if(tips.btns == 2 || tips.btns == 3) {
			builder.setNegativeButton(tips.btnCancelLabel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mFeeShowed = false;
					dialog.dismiss();
				}
			});
		}
		if(tips.btns > 0) {
			builder.setCancelable(false);
		}
		final Dialog dialog =  builder.create();
		dialog.setCanceledOnTouchOutside(false);
//		dialog.setOnCancelListener(new OnCancelListener() {
//			
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				mFeeShowed = false;;
//			}
//		});
		onDestoryExec.add(new IStopExec(){
			@Override
			public void onStop() {
				mFeeShowed = false;
				if( dialog != null && dialog.isShowing() )
					dialog.dismiss();
			}
			
		});
		dialog.show();

	}
	private void unlock(){
        if(getPlayerIsLock()){
        	setPlayerLockViewVisibleChanged(false);
            setPlayerLockBtnVisibleChanged(false);
            setPlayerIsLock(false);    
        }
	}
	private void setSoftVolume(float value) {
    	
    	int vol = (int)(-10f * value / mPlayerGestureView.getHeight());
    	int v = Player_UIManager.fone_media_player_get_audio_volume();
    	int set_v = v+vol;
    	if( set_v < 0 ) set_v = 1;
    	else if( set_v > 10 ) set_v = 10;
    	Player_UIManager.fone_media_player_set_audio_volume( set_v );
		
	}

	private void doPlayerLockAction() {
        setPlayerLockViewVisibleChanged(true);
        if(!getPlayerIsLock()){
            setPlayerLockBtnVisibleChanged(true);
            setPlayerLockRunnable();
        }
        setPlayerIsLock(true);
    }
    
    private void doPlayerHandlerCallBackAction(You_full_screen_player_data_to_ui coreData, Object uiData){
        try {
            onMessageCallBack(coreData);
        } catch (YouPlayerException e) {
            e.printStackTrace();
        }
    }
    
    @Override
	public boolean action_callback(int page_id, int page_action, Object core_data, Object ui_data) {
        return mPlayerAdapter.playerCallBack(page_id, page_action, core_data, ui_data);
	}
	
    public void onRequestCoreService(int ctrl, int evt, Object core_data, Object ui_data){
        mPlayerAdapter.playerAdapterOnRequestCoreService(ctrl, evt, core_data, ui_data);
    }
    
    private class InitMediaTask extends AsyncTask<Boolean, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            initializeTask();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                playerRefreshUIMediaAudio();
            } catch (YouPlayerException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
    
    private void uninitMediaTask(){
        if (null != mInitMediaTask) {
            mInitMediaTask.cancel(true);
            mInitMediaTask = null;
        }
    }
    
    protected void initializeTask() {
        String path = mPlayerAdapter.getMediaInfoUrl();
//        mMediaFile = MediaResolver.resolveMediaFile(InterfaceFile.SOURCE_TYPE_LOACAL, path, mContext, false);
    }
    
    public void onSdcardEject() {
        if (getPlayerIsLocalAudio() || getPlayerIsLocalVideo()) {
        	LOG.v(TAG, "onSdcardEject", "start");
            removeNotification();
            playerExitOnRequest();
        }
    }
    
    private void initSdcardStateReceiver() {
        if (null == sdcardEjectReceiver && mContext != null) {
            sdcardEjectReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) {
                        onSdcardEject();
                    } 
                }
            };
            
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
            intentFilter.addDataScheme("file");
            
            mContext.registerReceiver(sdcardEjectReceiver, intentFilter);
        }
    }
    
    
    public BroadcastReceiver mScreenOffReceiver;
    private void initScreenOffRegisters(){
        if (mContext != null && mScreenOffReceiver == null) {
            mScreenOffReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                	LOG.v(TAG, "onReceive", intent != null ? intent.getAction() : "null");
                    if( intent.getAction().equals("android.intent.action.SCREEN_OFF")){
                        if( mPlayerYouSurfaceView.getVisibility() == View.VISIBLE ){
                        	mPlayerYouSurfaceView.setVisibility(View.INVISIBLE);
                        }
                    }
                }       
            };
            mContext.registerReceiver(mScreenOffReceiver,new IntentFilter("android.intent.action.SCREEN_OFF"));
        }
    }
    
    private void uninitScreenOffRegisters () {
        if (mContext != null && mScreenOffReceiver != null) {
            mContext.unregisterReceiver(mScreenOffReceiver);
            mScreenOffReceiver = null;
        }
    }
    
    private void uninitSdcardReceiver () {
        if (null != sdcardEjectReceiver && mContext != null) {
            mContext.unregisterReceiver(sdcardEjectReceiver);
            sdcardEjectReceiver = null;
        }
    }
    
    private void initTelephoneReceiver() {
        if(getObjectIsNull(mContext)){
        	LOG.e(TAG, "initTelephoneListener", "mView is null");
            return;
        }
        phoneStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
            	LOG.e(TAG, "initTelephoneReceiver", "action:"+intent.getAction());
                if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL) || intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                    if((getPlayerIsLocalAudio() || getPlayerIsOnlineAudio())&& !getPlayerIsPauseStatus() ){
                        onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PAUSE, You_Core.FN_UI_EVT_TOUCH_UP, null, 0);
                    }
                    sendNotification();
                } else {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                    if(tm != null){
                        switch (tm.getCallState()) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            if((getPlayerIsLocalAudio() || getPlayerIsOnlineAudio())&& !getPlayerIsPauseStatus() ){
                                onRequestCoreService(You_Core.FN_FULL_SCREEN_BTN_PAUSE, You_Core.FN_UI_EVT_TOUCH_UP, null, null);
                            }
                            sendNotification();
                            break;
                            
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            break;
                            
                        case TelephonyManager.CALL_STATE_IDLE:
                            break;
                            
                        default:
                            break;
                        }
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        mContext.registerReceiver(phoneStateReceiver, intentFilter);
    }
    
    private void uninitTelephoneReceiver(){
        if (getPlayerIsPauseStatus() && null != phoneStateReceiver && mContext != null) {
            mContext.unregisterReceiver(phoneStateReceiver);
            phoneStateReceiver = null;
        }
    }
    
    public void sendNotification() {
        if(getObjectIsNull(mContext)){
        	LOG.e(TAG, "sendNotification", "mView is null");
            return;
        }
        removeNotification();
        String mediaName = getCurrentMediaTypeName();
        String pauseOrPlay = "";
        if (getPlayerIsLocalAudio()) {
            if (getPlayerIsPauseStatus()){
            	pauseOrPlay += playerGetResource(R.string.notification_message_audio)+ playerGetResource(R.string.notification_message_pause);
            }else{
                pauseOrPlay += playerGetResource(R.string.notification_message_audio)+ playerGetResource(R.string.notification_message_play);
            }
        }else if (getPlayerIsVideoMedia()) {
            pauseOrPlay += playerGetResource(R.string.notification_message_video)+ playerGetResource(R.string.notification_message_pause);
        }else if(getPlayerIsOnlineAudio()){
            pauseOrPlay += playerGetResource(R.string.notification_message_radio)+ playerGetResource(R.string.notification_message_pause);
        }else{
            mediaName = playerGetResource(R.string.app_name);
            pauseOrPlay += playerGetResource(R.string.fullplayer_media_opening);
        }
        
        try {
            if (mPlayerNotificationManager == null){
                mPlayerNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            Intent intent = new Intent(mContext, YouExplorer.class);
            Bundle extraData = new Bundle();
            extraData.putBoolean("FLAG_RESUME_FROM_NOTIFICATION", true);
            intent.putExtras(extraData);
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            mPlayerNotification = null;
            mPlayerNotification = new Notification();
            mPlayerNotification.icon = R.drawable.icon;
            mPlayerNotification.flags = Notification.FLAG_AUTO_CANCEL;
            mPlayerNotification.contentIntent = pi;
            mPlayerNotification.contentView = new RemoteViews(mContext.getPackageName(),R.layout.youplayer_notifyview);
            mPlayerNotification.contentView.setTextViewText(R.id.text_notify_state,pauseOrPlay);
            mPlayerNotification.contentView.setTextViewText(R.id.text_notify_name,mediaName);
            
            mPlayerNotificationManager.notify(0, mPlayerNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeNotification() {
        if (mPlayerNotificationManager == null && mContext != null){
            mPlayerNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(mPlayerNotificationManager != null)
        {
	        mPlayerNotificationManager.cancel(0);
	        mPlayerNotification = null;
	        mPlayerNotificationManager = null;
        }
    }
    
    public boolean getObjectIsNull(Object obj){
        return (obj == null);
    }
    
    public void xyz_updated(HashMap<String, Object> event) {
    	LOG.v(TAG, "xyz_updated", "start");

    }
    


	public static YouPlayerFullScreenPlayer getInstance(Context context, Object core_data, Object ui_data) {
        if( instance == null ){
        	instance = new YouPlayerFullScreenPlayer(context,core_data,ui_data);
        }else{
            instance.reloadPlayerViews(context);
        }
        instance.setBackgroundLogoIsShow(true);
        return instance;
	}
	
	public void exitFullPlayerApp(){
         onDestroy();
         mPlayerSysSurfaceView = null;
         mPlayerYouSurfaceView = null;
         instance = null;
         super.onDestroy();
	}
	
	public synchronized void viewHasAdded(){
	    playerViewHasAdded = true;
	    LOG.v(TAG, "viewHasAdded", playerViewHasAdded);
	}
	
	public void viewHasRemove(){
	    playerViewHasAdded = false;
	    LOG.v(TAG, "viewHasRemove", playerViewHasAdded);
	}

}
interface IStopExec{
	public void onStop();
}
