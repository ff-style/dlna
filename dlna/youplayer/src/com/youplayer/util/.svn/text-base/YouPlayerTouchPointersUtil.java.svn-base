package com.youplayer.util;

import android.util.FloatMath;
import android.view.MotionEvent;



public class YouPlayerTouchPointersUtil {
    public static final String tag = "TouchPointers";
    private TouchPointersMove mTouchPointersMove;
    private TouchPointersZoom mTouchPointersZoom;
    
    public YouPlayerTouchPointersUtil(){
        mTouchPointersMove = new TouchPointersMove();
        mTouchPointersZoom = new TouchPointersZoom();
    }
    
    public void setMoveEvent(MotionEvent event){
        if(mTouchPointersMove != null){
            mTouchPointersMove.setEvent(event);
        }
    }
    
    public void setZoomEvent(MotionEvent event){
        if(mTouchPointersZoom != null){
            mTouchPointersZoom.setEvent(event);
        }
    }
    
    public class TouchPointersMove{
        
        private float old1_X = 0;
        private float old1_Y = 0;
        
        private float old2_X = 0;
        private float old2_Y = 0;
        
        private boolean x1_left_move = false;
        private boolean x2_left_move = false;
        private boolean x1_right_move = false;
        private boolean x2_right_move = false;
        
        private boolean y1_top_move = false;
        private boolean y2_top_move = false;
        private boolean y1_bottom_move = false;
        private boolean y2_bottom_move = false;
        
        private float   POINTER_DISTANCE = 5;
        
        public TouchPointersMove(){
            
        }
        
        public void setMoveDefaultValue(){
            x1_right_move = false;
            x2_right_move = false;
            x1_left_move = false;
            x2_left_move = false;
            
            y1_top_move = false;
            y2_top_move = false;
            y1_bottom_move = false;
            y2_bottom_move = false;

        }
        
        public void setPos1DefaultValue(MotionEvent event){
            old1_X = event.getX(event.getPointerId(0));
            old1_Y = event.getY(event.getPointerId(0));
        }
        
        public void setPos2DefaultValue(MotionEvent event){
            old2_X = event.getX(event.getPointerId(1));
            old2_Y = event.getY(event.getPointerId(1));
        }
           
        public void setEvent(MotionEvent event){
            
            try {
                if(event.getPointerCount() != 2){
                    return;
                }
                switch(event.getAction()){
                
                case MotionEvent.ACTION_POINTER_1_DOWN:
                    v("TouchPointers", "ACTION_POINTER_1_DOWN");
                    setPos1DefaultValue(event);
                    setPos2DefaultValue(event);
                    break;
                    
                case MotionEvent.ACTION_POINTER_1_UP:
                    v("TouchPointers", "ACTION_POINTER_1_UP");
                    setMoveDefaultValue();
                    break;
                    
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    v("TouchPointers", "ACTION_POINTER_2_DOWN");
                    setPos1DefaultValue(event);
                    setPos2DefaultValue(event);
                    break;
                    
                case MotionEvent.ACTION_POINTER_2_UP:
                    v("TouchPointers", "ACTION_POINTER_2_UP");
                    setMoveDefaultValue();
                    break;            
                    
                case MotionEvent.ACTION_MOVE:
//                    v("TouchPointers", "ACTION_MOVE");
                    touchMove(event);
                    break;   
                    
                default:
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public void touchMove(MotionEvent event){
            float x1 = event.getX(event.getPointerId(0));
            float y1 = event.getY(event.getPointerId(0));

            float x2 = event.getX(event.getPointerId(1));
            float y2 = event.getY(event.getPointerId(1));

            boolean is_left_move = isLeftMove(old1_X, old1_Y, old2_X, old2_Y, x1, y1, x2, y2);
            if(is_left_move){
                leftMoveNotify();
            }
            
            boolean is_right_move = isRightMove(old1_X, old1_Y, old2_X, old2_Y, x1, y1, x2, y2);
            if(is_right_move){
                rightMoveNotify();
            }
            
            boolean is_top_move = isTopMove(old1_X, old1_Y, old2_X, old2_Y, x1, y1, x2, y2);
            if(is_top_move){
                topMoveNotify();
            }
            
            boolean is_bottom_move = isBottomMove(old1_X, old1_Y, old2_X, old2_Y, x1, y1, x2, y2);
            if(is_bottom_move){
                bottomMoveNotify();
            }
            
            old1_X = x1;
            old1_Y = y1;
            old2_X = x2;
            old2_Y = y2;
        }
        
        public float distanceMoveX(float x0, float x1){
            return (x1 - x0);
        }
        
        public float distanceMoveY(float y0, float y1){
            return (y1 - y0);
        }
        
        public boolean isLeftMove(float oldx1, float oldy1, float oldx2, float oldy2, float x1, float y1, float x2, float y2){
            boolean result = false;
            float x1_distance = distanceMoveX(oldx1, x1); 
            float x2_distance = distanceMoveX(oldx2, x2);

            float y1_distance = distanceMoveY(oldy1, y1);
            float y2_distance = distanceMoveY(oldy2, y2);
            
            
            if(!x1_left_move || !x2_left_move){
                if( (x1_distance < 0 && Math.abs(x1_distance) > POINTER_DISTANCE) && (Math.abs(x1_distance) > Math.abs(y1_distance)) ){
                    x1_left_move = true;
                }
                
                if( (x2_distance < 0 && Math.abs(x2_distance) > POINTER_DISTANCE) && (Math.abs(x2_distance) > Math.abs(y2_distance)) ){
                    x2_left_move = true;
                }
                
                if(x1_left_move && x2_left_move ){
//                    e("isLeftMove", "left x move");
                    x1_left_move = false;
                    x2_left_move = false;
                    result = true;
                }
            }
            return result;
        }
        
        
        public boolean isRightMove(float oldx1, float oldy1, float oldx2, float oldy2, float x1, float y1, float x2, float y2){
            boolean result = false;
            float x1_distance = distanceMoveX(oldx1, x1); 
            float x2_distance = distanceMoveX(oldx2, x2);

            float y1_distance = distanceMoveY(oldy1, y1);
            float y2_distance = distanceMoveY(oldy2, y2);
            
            
            if(!x1_right_move || !x2_right_move){
                if( (x1_distance > 0 && Math.abs(x1_distance) > POINTER_DISTANCE) && (Math.abs(x1_distance) > Math.abs(y1_distance)) ){
                    x1_right_move = true;
                }
                
                if( (x2_distance > 0 && Math.abs(x2_distance) > POINTER_DISTANCE) && (Math.abs(x2_distance) > Math.abs(y2_distance)) ){
                    x2_right_move = true;
                }
                
                if(x1_right_move && x2_right_move){
//                    e("isRightMove", "right x move");
                    x1_right_move = false;
                    x2_right_move = false;
                    result = true;
                }
            }
            return result;
        }
        
        public boolean isTopMove(float oldx1, float oldy1, float oldx2, float oldy2, float x1, float y1, float x2, float y2){
            boolean result = false;
            float x1_distance = distanceMoveX(oldx1, x1); 
            float x2_distance = distanceMoveX(oldx2, x2);

            float y1_distance = distanceMoveY(oldy1, y1);
            float y2_distance = distanceMoveY(oldy2, y2);
            
            
            if(!y1_top_move || !y2_top_move){
                if( (y1_distance < 0 && Math.abs(y1_distance) > POINTER_DISTANCE) && (Math.abs(y1_distance) > Math.abs(x1_distance)) ){
                    y1_top_move = true;
                }
                
                if( (y2_distance < 0 && Math.abs(y2_distance) > POINTER_DISTANCE) && (Math.abs(y2_distance) > Math.abs(x2_distance)) ){
                    y2_top_move = true;
                }
                
                if(y1_top_move && y2_top_move ){
//                    e("isTopMove", "top y move");
                    y1_top_move = false;
                    y2_top_move = false;
                    result = true;
                }
            }
            return result;
        }
        
        public boolean isBottomMove(float oldx1, float oldy1, float oldx2, float oldy2, float x1, float y1, float x2, float y2){
            boolean result = false;
            float x1_distance = distanceMoveX(oldx1, x1); 
            float x2_distance = distanceMoveX(oldx2, x2);

            float y1_distance = distanceMoveY(oldy1, y1);
            float y2_distance = distanceMoveY(oldy2, y2);
            
            
            if(!y1_bottom_move || !y2_bottom_move){
                if( (y1_distance > 0 && Math.abs(y1_distance) > POINTER_DISTANCE) && (Math.abs(y1_distance) > Math.abs(x1_distance)) ){
                    y1_bottom_move = true;
                }
                
                if( (y2_distance > 0 && Math.abs(y2_distance) > POINTER_DISTANCE) && (Math.abs(y2_distance) > Math.abs(x2_distance)) ){
                    y2_bottom_move = true;
                }
                
                if(y1_bottom_move && y2_bottom_move ){
//                    e("isBottomMove", "bottom y move");
                    y1_bottom_move = false;
                    y2_bottom_move = false;
                    result = true;
                }
            }
            return result;
        }
    }
    
    
    public class TouchPointersZoom{
          
        private float oldDiff = 10.0f;
        private float POINTER_DIFF = 50.0f;
        
        public TouchPointersZoom(){
            
        }
        
        public void setPos1DefaultValue(MotionEvent event){
            event.getX(event.getPointerId(0));
            event.getY(event.getPointerId(0));
        }
        
        public void setPos2DefaultValue(MotionEvent event){
            event.getX(event.getPointerId(1));
            event.getY(event.getPointerId(1));
        }
            
        public void setZoomDefaultValue(){
            is_out_zoom_notify = false;
            is_in_zoom_notify = false;
        }
        public void setEvent(MotionEvent event){
            try {
                if(event.getPointerCount() != 2){
                    return;
                }
                switch(event.getAction()){
                
                case MotionEvent.ACTION_POINTER_1_DOWN:
//                    v("TouchPointersZoom", "ACTION_POINTER_1_DOWN");
                    setPos1DefaultValue(event);
                    setPos2DefaultValue(event);
                    oldDiff = spacing(event);
//                    v("oldDiff", "oldDiff:" + oldDiff);
                    break;
                    
                case MotionEvent.ACTION_POINTER_1_UP:
//                    v("TouchPointersZoom", "ACTION_POINTER_1_UP");
                    setZoomDefaultValue();
                    break;
                    
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    v("TouchPointersZoom", "ACTION_POINTER_2_DOWN");
                    setPos1DefaultValue(event);
                    setPos2DefaultValue(event);
                    oldDiff = spacing(event);
//                    v("oldDiff", "oldDiff:" + oldDiff);
                    break;
                    
                case MotionEvent.ACTION_POINTER_2_UP:
                    v("TouchPointersZoom", "ACTION_POINTER_2_UP");
                    setZoomDefaultValue();
                    break;            
                    
                case MotionEvent.ACTION_MOVE:
                    v("TouchPointersZoom", "ACTION_MOVE");
                    touchZoom(event);
                    break;   
                    
                default:
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public void touchZoom(MotionEvent event){
            if(isInZoom(event)){
                inZoomNotify();
            }
            
            if(isOutZoom(event)){
                outZoomNotify();
            }
        }
        
        public float spacing(MotionEvent event) {
            float x = event.getX(event.getPointerId(0)) - event.getX(event.getPointerId(1));
            float y = event.getY(event.getPointerId(0)) - event.getY(event.getPointerId(1));
            return Math.abs(FloatMath.sqrt(x * x + y * y));
        }
        
        public boolean isInZoom(MotionEvent event){
            boolean result = false; 
            float newDiff = spacing(event);
            
            if(newDiff + POINTER_DIFF < oldDiff){
               result = true;
            }
            return result;
        }
        
        public boolean isOutZoom(MotionEvent event){
            boolean result = false; 
            float newDiff = spacing(event);
            
            if(newDiff - POINTER_DIFF > oldDiff){
               result = true;
            }
            return result;
        }
    }
    
    
    boolean isZoom = true;
    public void setIsZoom(boolean isZoom){
        this.isZoom  = isZoom;
    }
    
    public boolean getIsZoom(){
        return isZoom;
    }
    
    boolean isMove = true;
    public void setIsMove(boolean isMove){
        this.isMove  = isMove;
    }
    
    public boolean getIsMove(){
        return isMove;
    }
    
    public TouchPointersMoveCallback mTouchPointersMoveCallback;    
    public void setUtilMoveCallback(YouPlayerTouchPointersUtil.TouchPointersMoveCallback callback){
        mTouchPointersMoveCallback = callback;
    }
    
    public void leftMoveNotify(){
        if(mTouchPointersMoveCallback != null){
            mTouchPointersMoveCallback.onLeftMoveCallback();
//            v("leftMoveNotify", "leftMoveNotify");
        }
    }
    
   public void rightMoveNotify(){
        if(mTouchPointersMoveCallback != null){
            mTouchPointersMoveCallback.onRightMoveCallback();
//            v("rightMoveNotify", "rightMoveNotify");
        }
    }
   
    public void topMoveNotify(){
        if(mTouchPointersMoveCallback != null){
            mTouchPointersMoveCallback.onTopMoveCallback();
//            v("topMoveNotify", "topMoveNotify");
        }
    }
    
    public void bottomMoveNotify(){
        if(mTouchPointersMoveCallback != null){
            mTouchPointersMoveCallback.onBottomMoveCallback();
//            v("bottomMoveNotify", "bottomMoveNotify");
        }
    }       
    
    private TouchPointersZoomCallback mTouchPointersZoomCallback;
    private boolean is_out_zoom_notify = false;
    private boolean is_in_zoom_notify = false;
    public void setUtilZoomCallback(TouchPointersZoomCallback callback){
        mTouchPointersZoomCallback = callback;
    }
    
    public void inZoomNotify(){
        if(mTouchPointersZoomCallback != null){
            if(!is_in_zoom_notify){
                mTouchPointersZoomCallback.onInZoomCallback();
                is_in_zoom_notify = true;
//                v("inZoomNotify", "inZoomNotify");
            }
        }
    }
    
    public void outZoomNotify(){
        if(mTouchPointersZoomCallback != null){
            if(!is_out_zoom_notify){
                mTouchPointersZoomCallback.onOutZoomCallback();
                is_out_zoom_notify = true;
//                v("outZoomNotify", "outZoomNotify");
            }
        }
    }
    
    public interface TouchPointersMoveCallback{
        public void onLeftMoveCallback();
        public void onRightMoveCallback();
        public void onTopMoveCallback();
        public void onBottomMoveCallback();
    }
    
    public interface TouchPointersZoomCallback{
        public void onInZoomCallback();
        public void onOutZoomCallback();
    }
    

    public static void v(String type, String msg){
//        String des = String.format("[%10.10s][%16.16s]%s", tag, type, msg);
    	LOG.v("TOUCHpOINTERuTIL",type, msg);
    }
    
    public static void e(String type, String msg){
//        String des = String.format("[%10.10s][%16.16s]%s", tag, type, msg);
//        Log.e("TP", des);
    	LOG.e("TOUCHpOINTERuTIL",type, msg);
    }
}
