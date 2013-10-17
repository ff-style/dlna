package com.youplayer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.youplayer.player.YouExplorer;

import android.app.Activity;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.util.Log;
import android.view.MotionEvent;

public class YouOnGesture implements GestureOverlayView.OnGestureListener{
    Context mContext;
    // gesture
    private float old_x, old_y, new_x, new_y, move_x, move_y;
    private boolean is_gesture_flag, is_left, is_top, is_right, is_bottom;
    private GestureLibrary gestureLib;
    private Gesture gesture;
    
    private YouOnGestureListener mListener;
    
    public void setYouOnGestureListener(YouOnGestureListener listener){
        this.mListener = listener;
    }
    
    public void YouOnGesture(){
        if(mListener != null){
            mListener.YouOnGesture();
        }
    }
    
    public void YouOnGestureStart(){
        if(mListener != null){
            mListener.YouOnGestureStart();
        } 
    }
    
    public void YouOnGestureEnd(boolean isEnableSeek, boolean isLeftOrRight){
        if(mListener != null){
            mListener.YouOnGestureEnd(isEnableSeek, isLeftOrRight);
        }
    }
    
    public void YouOnGestureMovePrevious(){
        if(mListener != null){
            mListener.YouOnGestureMovePrevious();
        }
    }
    
    public void YouOnGestureMoveNext(){
        if(mListener != null){
            mListener.YouOnGestureMoveNext();
        }
    }
    
    public void YouOnGestureMoveUPOrDown(boolean isEnableSeek, float distance,boolean two_pointer){
        if(mListener != null){
            mListener.YouOnGestureMoveUPOrDown(isEnableSeek, distance,start_x,two_pointer);
        }
    }

    public void YouOnGestureMoveLeftOrRight(boolean isEnableSeek, float distance){
        if(mListener != null){
            mListener.YouOnGestureMoveLeftOrRight(isEnableSeek, distance);
        }
    }
    
    public static interface YouOnGestureListener{
        public void YouOnGesture();
        public void YouOnGestureStart();
        public void YouOnGestureEnd(boolean isEnableSeek,boolean isLeftOrRight);
        public void YouOnGestureMovePrevious();
        public void YouOnGestureMoveNext();
        public void YouOnGestureMoveUPOrDown(boolean isEnableSeek, float distance,float start_x,boolean two_pointer);
        public void YouOnGestureMoveLeftOrRight(boolean isEnableSeek, float distance);
        
    }
    
    public YouOnGesture(Context context){
        mContext = context;
        loadGestureLib();
    }
    
    public void loadGestureLib(){
        gestureLib = GestureLibraries.fromFile(getGeturePath());
        gestureLib.load();
    }
    
    @Override
    public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        if (!is_top
                && !is_bottom
                && Math.abs(new_x - event.getX()) > 20
                && Math.abs(new_x - event.getX()) > Math.abs(new_y - event.getY())) {
            if (new_x > event.getX()) {
//                L.v("fullplayer","YouOnGestureMoveLeftOrRight is is_left:",""+is_left);
                if (!is_left) {
                    is_gesture_flag = true;
                    is_left = true;
                    is_right = false;
                }
            } else {
//                L.v("fullplayer","YouOnGestureMoveLeftOrRight is is_right:",""+is_right);
                if (!is_right) {
                    is_gesture_flag = true;
                    is_left = false;
                    is_right = true;
                }
            }
            
            if (is_gesture_flag) {
                is_gesture_flag = false;
                move_x = new_x - event.getX();
            } else {
                move_x = move_x + new_x - event.getX();
            }
//            L.v("fullplayer","YouOnGestureMoveLeftOrRight is gesture:",""+is_gesture_flag);
            YouOnGestureMoveLeftOrRight(true, move_x);
            new_x = event.getX();
            new_y = event.getY();
        } else if (!is_left
                && !is_right
                && Math.abs(new_y - event.getY()) > 20
                && Math.abs(new_x - event.getX()) < Math.abs(new_y - event.getY())) {
            if (new_y < event.getY()) {
                if (!is_top) {
                    is_gesture_flag = true;
                    is_top = true;
                    is_bottom = false;
                }
            } else {
                if (!is_bottom) {
                    is_gesture_flag = true;
                    is_top = false;
                    is_bottom = true;
                }
            }

            if (is_gesture_flag) {
                is_gesture_flag = false;
                
                move_y = event.getY() - new_y;
            } else {
                move_y = move_y + event.getY() - new_y;
            }
            YouOnGestureMoveUPOrDown(true, move_y,event.getPointerCount()==2);
            new_x = event.getX();
            new_y = event.getY();
        }
    }

    @Override
    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
        if (Math.abs(old_x - event.getX()) < 20 && Math.abs(old_y - event.getY()) < 20) {
            is_left = false;
            is_top = false;
            is_right = false;
            is_bottom = false;
            is_gesture_flag = false;
            YouOnGestureEnd(false, false);
            return;
        } 

        gesture = overlay.getGesture();
        String gesture_name = findGesture(gesture);
        if (gesture_name.equals("previous")) {
            YouOnGestureMovePrevious();
        } else if (gesture_name.equals("next")) {
            YouOnGestureMoveNext();
        }
        
        if(is_left || is_right){
            YouOnGestureEnd(true, true);
        }else{
            YouOnGestureEnd(true, false);
        }

        is_left = false;
        is_top = false;
        is_right = false;
        is_bottom = false;
        is_gesture_flag = false;
        return;
    }

    float start_x;
    @Override
    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
        start_x = new_x = old_x = event.getX();
        new_y = old_y = event.getY();
        is_gesture_flag = false;
        YouOnGestureStart();
//        L.v("fullplayer","onGestureStarted is gesture:",""+is_gesture_flag);
    }
    
    @Override
    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        
    }
    
    private String findGesture(Gesture gesture) {
        List<Prediction> predictions = gestureLib.recognize(gesture);
        if (!predictions.isEmpty()) {
            Prediction prediction = predictions.get(0);
            if (prediction.score >= 1) {
                return prediction.name;
            }
        }
        return "";
    }
    
    public File getGeturePath(){
        String path = mContext.getFilesDir() + File.separator
        + ((YouExplorer.instance).getApplication().getPackageName()) + File.separator
        + "gesture";
        InputStream is = null;
        FileOutputStream os = null;
        File filepath = new File(path);
        File file = new File(path + File.separator + "gestures");
        
        try {
            if (!filepath.isDirectory() && filepath.mkdirs())
                ;
            if (!file.isFile()) {
                if (file.createNewFile()) {
                    if (file.canWrite()) {
                        is = mContext.getAssets().open("gestures");
                        byte[] b = new byte[is.available()];
                        os = new FileOutputStream(file);
                        while (is.read(b) != -1) {
                            os.write(b);
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            }
        }
        return file;
    }
}
