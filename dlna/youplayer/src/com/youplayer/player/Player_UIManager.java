package com.youplayer.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Player_UIManager {

	
	private static Rect m_desRect = new Rect(0, 0, 400, 400);
	
	private static SurfaceHolder mHolder = null;

	private static int mTotal;
	private static int mCurrent;
	private static boolean mbIsplayer;
	private static boolean ispause;
	

	
//    static Handler myHandler;
	public static native void fone_media_player_set_surface_view(Object surface);
	 Player_UIManager(){
//		 mbIsplayer = false;
	 }

	
	/**
	 * set Surface
	 */
	public static void Uim_Set_Player_Surface(SurfaceView surfaceview)
	{
		if (null != surfaceview)
		{
			mHolder = surfaceview.getHolder();
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		}else
		    mHolder = null;
	}
	
	public static void Uim_set_video_display(int a_left, int a_top, int a_right, int a_bottom)
	{
		m_desRect.set(a_left, a_top, a_right - a_left, a_bottom - a_top);
	}
	/**
	 * 从Player_Control发来的消息, 供Player_Control调用
	 */
	public static void Uim_Player_Message(int type, int percentage, int arg2){

	}

	static int count;
	public static Object Bitmapbuffer_Create(int width, int height) {
		if (width > 0 && height > 0) {
			try{			
				Object obj =  Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
				return obj;
			}
			catch(Throwable e)//Exception e
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public native static Object fone_media_player_get_description();
	public native static int fone_media_player_set_audio_volume(int a_nVolume);
	public native static int fone_media_player_get_audio_volume();

	public native static void    fone_media_player_seek_preview_init(int width, int height);
	public native static void    fone_media_player_seek_preview_uninit();
	public native static Object  fone_media_player_seek_preview();
	
	public native static void 	 fone_media_thumbnail_init(int a_width, int a_height);
	public native static void 	 fone_media_thumbnail_uninit();
	
	public native static void 	 fone_media_player_thumbnail_stop();

	public native static Object fone_media_player_get_thumbnail_from_video(String MediaPath, int pos, int bpp, int width, int height);
//		

	public native static Object  fone_media_player_get_audio_info(String MediaPath);

	/** 以上是Player_Control提供给UI调用的接口，以下是自用方法 */
	public static  void RenderVideoFrame(Object bmp, Object srcRect) 
	{	
		if (((Bitmap) bmp != null) && (srcRect != null)) 
		{
		    m_bitmap = (Bitmap) bmp;
		    m_srcRect = (Rect) srcRect;
			SurfaceHolder holder = mHolder;
			if(holder != null){
				synchronized(holder){
					Canvas canv = holder.lockCanvas();
					if (canv != null)
					{
						canv.drawBitmap((Bitmap)bmp, (Rect)srcRect, m_desRect, null);
						holder.unlockCanvasAndPost(canv);
					}
				}
			}
		}
	}
	static Bitmap m_bitmap;
	static Rect m_srcRect;

    
	public static  void RenderLastFrame(Rect des)
    {
        Handler handler = null;
        if(YouExplorer.instance != null)
            handler = new Handler(YouExplorer.instance.getMainLooper());
        if(handler == null)
            return;
        
        m_desRect = des;
        handler.postDelayed((new Runnable(){
            public void run(){
                if(mHolder != null
                    && m_bitmap != null
                    && m_srcRect != null)
                {
                	
                    SurfaceHolder holder = mHolder;
                    
                    synchronized(holder){
                    
	                    Canvas canv = null;
	                    canv = holder.lockCanvas();
	                    
	                    if (canv != null) {
	                    	
	                        canv.drawBitmap(m_bitmap, m_srcRect, m_desRect, null);
	                        holder.unlockCanvasAndPost(canv);
	                    }
                    }
                }
            }
        }), 50);
    }
	
	public static void freeBitmap(){

		if( m_bitmap != null ){
			if( !m_bitmap.isRecycled() )
				m_bitmap.recycle();
			m_bitmap = null;
		}
			
		
	}
	
	public static void DrawOneLineStr(Canvas canv
			, String strSub
			, int x
			, int y
			, Paint paint
			, int textcolor
			, int linecolor)
	{
		int left = x;
		int delta = 2;

		// draw outline
		paint.setColor(linecolor); 
		canv.drawText(strSub, left-delta, y,       paint);
		canv.drawText(strSub, left,       y+delta, paint);
		canv.drawText(strSub, left+delta, y,       paint);
		canv.drawText(strSub, left,       y-delta, paint);
		
		// draw the text
		paint.setColor(textcolor); 
		canv.drawText(strSub, left, y, paint);
	}
	
	public static Bitmap getBitmap() {
		return m_bitmap;
	}
}
