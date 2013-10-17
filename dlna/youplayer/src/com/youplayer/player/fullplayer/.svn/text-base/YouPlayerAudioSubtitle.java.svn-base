package com.youplayer.player.fullplayer;


import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.youplayer.core.You_Core;
import com.youplayer.core.struct.You_full_screen_player_data_to_ui;
import com.youplayer.player.YouPlayerFullScreenPlayer;
import com.youplayer.player.R;
import com.youplayer.player.frame.YouPlayerEventControler;


public class YouPlayerAudioSubtitle {
    public View mView;
    private int phone_width = 800;
    private int phone_height = 480;
    
    public You_full_screen_player_data_to_ui.Cls_fn_full_screen_audio_and_subtitle mTrack;
    public RadioGroup mAudioSubTrackGroup;
    public RadioGroup mAudioSubSubtitleGroup;
    public Context mContext;
    AlertDialog mDialog;
    public int mAudioSubTrackSelectIndex = 0;
    public int mAudioSubSuttitleSelectIndex = 0;
    private Button submit;
    private Button cancel;
    public static int AUDIO_SUB_TYPE_AUDIO = 0;
    public static int AUDIO_SUB_TYPE_SUB = 1;
    public YouPlayerAudioSubtitle(Context context, You_full_screen_player_data_to_ui.Cls_fn_full_screen_audio_and_subtitle data){
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.youplayer_audio_sub_layout, null);
        mTrack = data;
        initView();
    }
    

    private String getVideoSize(){
        String sizeText = "";
        if(YouPlayerFullScreenPlayer.instance != null && YouPlayerFullScreenPlayer.instance.mPlayerAdapter != null){
            sizeText = " " + YouPlayerFullScreenPlayer.instance.mPlayerAdapter.getMediaInfoWidth() + " X " + YouPlayerFullScreenPlayer.instance.mPlayerAdapter.getMediaInfoHeight();
        }
        return sizeText;
    }
    private void initView() {
       
        mAudioSubTrackGroup = (RadioGroup)mView.findViewById(R.id.audio_sub_track_group);
        mAudioSubTrackGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mAudioSubTrackSelectIndex = checkedId;
            }
        });

        mAudioSubSubtitleGroup = (RadioGroup)mView.findViewById(R.id.audio_sub_subtitle_group);
        mAudioSubSubtitleGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mAudioSubSuttitleSelectIndex = checkedId;
            }
        });
        
        TextView video = (TextView)mView.findViewById(R.id.audio_sub_video_text);
        TextPaint videoTP = video.getPaint();
        videoTP.setFakeBoldText(true);
        video.setText(video.getText().toString() + getVideoSize());
        
        TextView track = (TextView)mView.findViewById(R.id.audio_sub_track_text);
        TextPaint trackTP = track.getPaint();
        trackTP.setFakeBoldText(true);
        
        TextView subtitle = (TextView)mView.findViewById(R.id.audio_sub_subtitle_text);
        TextPaint subtitleTP = subtitle.getPaint();
        subtitleTP.setFakeBoldText(true);
        
        
        loadAudioData();
        loadSubData();
    }
    
    public void setRadioButton(RadioGroup rootGroup, int radioId, String radioText, boolean enable, int type){
        RadioButton audioNoneRB = new RadioButton(mContext);
        LinearLayout.LayoutParams subLP = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        audioNoneRB.setPadding(10, 0, 10, 0);
        subLP.rightMargin = 0;
        subLP.leftMargin =  0;
        subLP.topMargin =  1;
        subLP.bottomMargin =  1;
        subLP.gravity = Gravity.RIGHT;
//        subLP.height = 43;

        audioNoneRB.setBackgroundColor(Color.WHITE);
        audioNoneRB.setButtonDrawable(android.R.color.transparent);
        audioNoneRB.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.youplayer_audio_sub_btn_radio), null);
        
        audioNoneRB.setText(audio_sub_desc_convert(radioText, radioId, type));
        audioNoneRB.setId(radioId);
        audioNoneRB.setTextColor(Color.BLACK);
        audioNoneRB.setTextSize(18);
        audioNoneRB.setEnabled(enable);
        rootGroup.addView(audioNoneRB, subLP);
    }
    

    public void loadAudioData(){
        if(mTrack.audio_cnt == 0){
            String text = mContext.getString(R.string.audio_sub_audio_none);
            setRadioButton(mAudioSubTrackGroup, 0, text, false, AUDIO_SUB_TYPE_AUDIO);
            return;
        }
        
        for(int i = 0; i < mTrack.audio_cnt; i++){
            setRadioButton(mAudioSubTrackGroup, i, mTrack.audio_cell[i], true, AUDIO_SUB_TYPE_AUDIO);
        }
        
        if(0 <= mTrack.cur_audio && mTrack.cur_audio < mTrack.audio_cnt){
            mAudioSubTrackGroup.check(mTrack.cur_audio);
        }else{
            mAudioSubTrackGroup.check(0);
        }
    }
    
    public void loadSubData(){
        if(mTrack.sub_cnt == 0){
            String text = mContext.getString(R.string.audio_sub_audio_none);
            setRadioButton(mAudioSubSubtitleGroup, 0, text, false, AUDIO_SUB_TYPE_SUB);
            return;
        }
        
        for(int j = 0; j < mTrack.sub_cnt; j++){
            setRadioButton(mAudioSubSubtitleGroup, j, mTrack.sub_cell[j], true, AUDIO_SUB_TYPE_SUB);
        }
        
        if(0 <= mTrack.cur_sub && mTrack.cur_sub < mTrack.sub_cnt){
            mAudioSubSubtitleGroup.check(mTrack.cur_sub);
        }else{
            mAudioSubSubtitleGroup.check(0);
        }
    }
    private PopupWindow mPopupWindow;
    public void showDialog(View parent){
        if(isShowing()){
            return;
        }
        
        if (mPopupWindow == null && parent != null) {
            try {
                submit = (Button) mView.findViewById(R.id.submit);
                submit.setOnClickListener(submitListener);
                cancel = (Button) mView.findViewById(R.id.cancel);
                cancel.setOnClickListener(cancelListener);
                mPopupWindow = new PopupWindow(mView, getWindowWidth(),getWindowHeight(), true);
                
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        
        mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.youplayer_rounded_corners_pop));
        mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, parent.getHeight());
    }
    
    private void setSelectResult() {
      mTrack.cur_audio = mAudioSubTrackSelectIndex;
      mTrack.cur_sub = mAudioSubSuttitleSelectIndex;
      YouPlayerEventControler.fn_core_service_request(You_Core.FN_FULL_SCREEN_BTN_AUDIO_CHANNEL_CONFIM, You_Core.FN_UI_EVT_TOUCH_UP, null, mTrack);
    }
    
    OnClickListener submitListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setSelectResult();
            closeWindow();
        }
    };
    
    OnClickListener cancelListener=new OnClickListener(){
        @Override
        public void onClick(View v){
            closeWindow();
        }
    };
    
    public void closeWindow(){
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }
    
    public int getWindowWidth(){
        if(YouPlayerFullScreenPlayer.instance != null){
            return  (YouPlayerFullScreenPlayer.instance.mPlayerAdapter.extGetScreenWidth() - 220);
        }else{
            return phone_width - 220;
        }
    }
    
    public int getWindowHeight(){
        if(YouPlayerFullScreenPlayer.instance != null){
//            int tmp = 180;
            int height = YouPlayerFullScreenPlayer.instance.mPlayerAdapter.extGetScreenHeight();
            int tmp = height * 180 / 480;
//            height = height > 480 ? 480 : height;
//            if(height < 480){
//                tmp = 120;
//            }
            return (height - tmp);
        }else{
            return phone_height - 180;
        }
    }    
    
    public boolean isShowing(){
        if(mPopupWindow != null){
            return mPopupWindow.isShowing();
        }
        return false;
    }
    
    public String audio_sub_desc_convert(String desc, int radioId, int type){
        if(desc == null || mContext == null){
            return desc;
        }
        String dest = "";
        int len = desc.length();
        if(len >= 3){
            len = 3;
        }
        String src = desc.substring(0, len);
        if("chi".equalsIgnoreCase(src)){
            dest = mContext.getString(R.string.audio_sub_desc_chi);
        }else if("cht".equalsIgnoreCase(src)){
            dest = mContext.getString(R.string.audio_sub_desc_cht);
        }else if("eng".equalsIgnoreCase(src)){
            dest = mContext.getString(R.string.audio_sub_desc_eng);
        }else if("unk".equalsIgnoreCase(src) || "und".equalsIgnoreCase(src)){
            if(type == AUDIO_SUB_TYPE_AUDIO){
                dest = mContext.getString(R.string.audio_sub_audio_desc_unkown) +String.valueOf(radioId);
            }else if(type == AUDIO_SUB_TYPE_SUB){
                dest = mContext.getString(R.string.audio_sub_sub_desc_unkown)+ String.valueOf(radioId);
            }
        }else{
            dest = desc;
        }
        return dest;
    }
}
