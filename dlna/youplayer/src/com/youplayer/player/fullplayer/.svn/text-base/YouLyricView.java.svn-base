package com.youplayer.player.fullplayer;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;

import com.youplayer.core.mediainfo.YouPlayerLyrics;
import com.youplayer.core.mediainfo.YouPlayerLyricsLine;
import com.youplayer.util.YouUtility;
import com.youplayer.util.YouPlayerTouchPointersUtil;


public class YouLyricView extends View {
	public Paint paint;
	public LyricTouchListener listener;
	//public String path;
	public int index;
	public long time;
	public Vector<Item> items = new Vector<Item>();
	public float tx, ty;
	public static int DRAG_CLICK = 0,DRAG_Y = 1,DRAG_X=2;
	public static  int dragging = DRAG_CLICK;//0点击 1 上下拖拽  2 左右拖拽

	//public static int limited_character = 12;
	public static int item_height;
	public static int item_width=800;

	public interface LyricTouchListener {

	    public void onPoint();

	    public void onDragging(float x, float y, float nx, float ny,int vwidth,int vheight);

	    public void onDragged(long time);
	    
	    public void onDragged(boolean left);
	    
	    public void onSizeChanged(int width,int height);

	}
	
	public YouLyricView(Context context, AttributeSet attrs) {
		
		super(context, attrs, 0);
		paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setTextSize(36);
		paint.setAntiAlias(true);
		this.setBackgroundColor(0x00000000);
		this.getBackground().setAlpha(0);
		TouchPointersInit();
		// initWords();
	}

	public YouLyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public YouLyricView(Context context) {

		super(context);

	}

	public void addItem(long time, String text) {

		Item item = null;
		if (text == null){ 
			text = "";
		}
		else{
			
			if( text.indexOf("QQ") > -1 )
				text = "";
			if( text.indexOf("\r\n") >-1 )
				text = text.replaceAll("\r\n","");
		}
		
		item = new Item(time, text);
		items.add(item);
		if (items.size() > 1)
			items.elementAt(items.size() - 2).nextline = item.time;

	}

//	@SuppressLint("WrongCall")
	@SuppressLint("WrongCall")
	@Override
	public void onDraw(Canvas canvas) {
		if (items == null || items.size() == 0 || index < 0
				|| index >= items.size())
			return;
		int x = 20;
		int y = getHeight() / 2 - getFocusItemPos();
		Item item = items.elementAt(index);
		int off = 0;
		if ((item.time > 0 && item.nextline > 0) && (item.nextline != item.time)) {
			off = (int) ((time - item.time) * item.height / (item.nextline - item.time));
		}
		y -= off;
		int a = 200 - index * 20;
		int k = 1;

		for (int i = 0; i < items.size(); i++) {
			item = items.elementAt(i);
			if (index == i) {
				item.setFocus(true);
				k = -1;
			} else if (i + 1 == index) {
				item.setFocus(false);
			} else
				item.reset();
			item.setAlpha(a);

			a += 20 * k;
			if( y >= -item_height && y < getHeight()+item_height )
				item.onDraw(canvas, x, y, paint);
			y += item.height;
		}
	}

	
	private int getFocusItemPos() {
			int pos=0;
			if (index > 0) {
				for (int i = 0; i <= index; i++) {
					pos += items.elementAt(i).height;
				}
				return pos;
			}
			return 0;
	}

	public boolean dragTouchEvent(MotionEvent event){
	    if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
            tx = event.getX();
            ty = event.getY();
            dragging = 0;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
        	if( Math.abs(tx - event.getX()) < 10 || Math.abs(ty - event.getY()) < 10 )
        		return true;
			if (Math.abs(tx - event.getX()) > Math.abs(ty - event.getY())) {
				dragging = DRAG_X;
				return true;
			}
            if (Math.abs(ty - event.getY()) < 20  ){
                return true;
            }  
            dragging = DRAG_Y;
            if (listener != null)
                listener.onDragging(tx, ty, event.getX(), event.getY(),getWidth(),getHeight());
            tx = event.getX();
            ty = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {   
            if (dragging == DRAG_Y ) {
                if (index < 1) {
                    listener.onDragged(0);
                }           
            } else if( dragging == DRAG_X ){
            	listener.onDragged((tx - event.getX()) > 0);
            }
            else {
            	listener.onPoint();
            } 	  
        }
	    return true;
	}	
	public void updateItemSize(){
		if( items != null && items.size() > 0 )
			for(int i = 0; i < items.size(); i++){
				items.elementAt(i).setSize(paint,getMeasuredWidth()-20);
			}
		
	}
	
	public static final int FONT_SIZE_INDEX_MAX = 0;
	public static final int FONT_SIZE_INDEX_NORMAL = 1;
	public static final int FONT_SIZE_INDEX_MIN = 2;	
	private YouPlayerTouchPointersUtil mTouchPointersUtil;
	
	public int getFontSizeIndex() {
        return YouUtility.getSubtitileyouSize(getContext());
	}

    public void setFontSizeIndex(int index) {
    	YouUtility.setSubtitleFontSize(getContext(), index);;
    }

    public void setFontSizeOutZoom(){
        int size = getFontSizeIndex();
        switch(size){
        case FONT_SIZE_INDEX_MAX:
            setFontSizeIndex(FONT_SIZE_INDEX_MAX);
            break;
            
        case FONT_SIZE_INDEX_NORMAL:
            setFontSizeIndex(FONT_SIZE_INDEX_MAX);
            break;
            
        case FONT_SIZE_INDEX_MIN:
            setFontSizeIndex(FONT_SIZE_INDEX_NORMAL);
            break;
            
       default:
           setFontSizeIndex(FONT_SIZE_INDEX_NORMAL);
           break;
        
        }
    }
    
    public void setFontSizeInZoom(){
        int size = getFontSizeIndex();
        switch(size){
        case FONT_SIZE_INDEX_MAX:
            setFontSizeIndex(FONT_SIZE_INDEX_NORMAL);
            break;
            
        case FONT_SIZE_INDEX_NORMAL:
            setFontSizeIndex(FONT_SIZE_INDEX_MIN);
            break;
            
        case FONT_SIZE_INDEX_MIN:
            setFontSizeIndex(FONT_SIZE_INDEX_MIN);
            break;
            
       default:
           setFontSizeIndex(FONT_SIZE_INDEX_NORMAL);
           break;
        
        }
    }
    
    public void TouchPointersInit(){
        mTouchPointersUtil = new YouPlayerTouchPointersUtil();
    }
    
    public void TouchPointersRelease(){
        mTouchPointersUtil = null;
    }
    
    public void setTouchPointersEvent(MotionEvent event){
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setMoveEvent(event);
        }
    
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setZoomEvent(event);
        }
    }
    
    public void setMoveCallback(YouPlayerTouchPointersUtil.TouchPointersMoveCallback callback){
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setUtilMoveCallback(callback);
        }
    }
    
    public void setZoomCallback(YouPlayerTouchPointersUtil.TouchPointersZoomCallback callback){
        if(mTouchPointersUtil != null){
            mTouchPointersUtil.setUtilZoomCallback(callback);
        }
    }
    
    public void updateFontSize() {
    	 
    	
    	int temp_height = getMeasuredHeight() / 16;
		int index = getFontSizeIndex();
		if (index == 0) {
			temp_height = getMeasuredHeight() / 10;
		} else if (index == 1) {
			temp_height = getMeasuredHeight() / 15;
		} else {
			temp_height = getMeasuredHeight() / 18;
		}
		item_height = temp_height;
		paint.setTextSize(item_height - 8);
		new Thread(){
			public void run(){
				updateItemSize();		
			}
		}.start();
		
    	
	}
  
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//Log.i("lrl"," lyric onSizeChanged "+w+" "+h+" old:"+oldw+" "+oldh);
		super.onSizeChanged(w, h, oldw, oldh);
		updateFontSize();
		if( listener != null && getVisibility() == View.VISIBLE )
			listener.onSizeChanged(w,h);
	}

	public void setTime(long time) {
		
		//Log.i("lrl","set time:"+time);
		this.time = time;
		for (int i = 0; i < items.size(); i++) {
			if (time >= items.elementAt(i).time) {

				if (i < items.size() - 1 && time <= items.elementAt(i + 1).time) {
					index = i;
					//Log.i("lrl","index:"+index);
					invalidate();
					return;
				}
			}
		}
		invalidate();
		
	}

	String path;
	public boolean init(YouPlayerLyrics tagyouLyrics) {
		items.removeAllElements();
		index = 0;
		YouPlayerLyricsLine line;
	    if(tagyouLyrics != null){
	    	YouPlayerLyrics lyrics = tagyouLyrics;
    		
    		for (int i = 0; i < lyrics.m_Count; i++) {
    			line = lyrics.m_lyrics[i];
    			if(line == null){
    			    continue;
    			}
    			addItem(line.m_TimeStart, line.m_Title);
    			if(i == (lyrics.m_Count - 1)){
    				if((line.m_TimeStart > 0) &&line.m_Title != null && (line.m_Title.length() > 0)){
    					addItem(line.m_TimeStart + 5000, null);
    				}
    			}
    		}
    		updateFontSize();
    		invalidate();
    		return true;
	    }
	    return false;
	}

	public void setLyricTouchListener(LyricTouchListener listener) {
		//this.listener = listener;
	}
}

class Item {
	public String text;
	public int width;
	public int height = 30;//LyricView.item_height;
	public long time;
	public long nextline;
	public int focus;
	public int x, y;
	private int alpha;
	String[] lines;
	private static final String k = "\\[\\d\\d:\\d\\d(.*)\\](.*)";

	public Item(String text) {
		this.text = text;
		if (text.matches(k)) {
			time = Integer.parseInt(text.substring(1, 3)) * 60000;
			time += Float.parseFloat(text.substring(4, text.indexOf(']', 6))) * 1000;
		}
		this.text = text.replaceFirst("\\[(.*)\\]", "     ");
	}

	public Item(long time, String text) {
		
		this.time = time;
		if (text.length() > 0) {
			text = text.replaceAll("\n", "");
		}
		this.text = text;
		
	}

	public void setAlpha(int a) {
		this.alpha = a;
		if (a < 0)
			alpha = 0;
		else if (a > 255)
			alpha = 255;

	}

	public void setFocus(boolean b) {
		if (b) {
			if (focus > 0)
				focus++;
			else
				focus = 1;
		} else {
			if (focus < 0)
				focus--;
			else
				focus = -1;
		}
	}

	public void setFocus(int i) {
		focus = i;
	}

	public void reset() {
		focus = 0;
	}
	public void setSize(Paint paint,int width){
		if(   YouLyricView.item_height <= 0 )
			return;
		this.width = width;
		int text_width = (int) paint.measureText(text);
		int line_count;
		if( text_width == 0 )
			line_count = 1;
		else
			line_count = text_width / width + (text_width%width == 0 ? 0 : 1 );
		height =  line_count *YouLyricView.item_height;
		lines = null;
		lines = new String[line_count];
		int start = 0;
		int lines_index=0;
		if( line_count == 1 ){
			lines[0] = text;
		}
		else {
			for(int i=1;i<text.length();i++){
				if( paint.measureText(text,start,i) >= width ){
					i--;
					if( lines.length <= lines_index+1 ) continue;
					lines[lines_index++] = text.substring(start,i);
					start = i;
				}
			}
			if( start < text.length()-1 && line_count>lines_index){
				lines[lines_index++] = text.substring(start,text.length());
			}
		}
	}
	public int getHeight() {
		return height;
	}

	public void onDraw(Canvas canvas, int x, int y, Paint paint) {
		this.x = x;
		this.y = y;
		if (YouLyricView.dragging == YouLyricView.DRAG_Y ) {
			if (focus > 0)
				focus = 6;
			else if (focus < 0)
				focus = 0;
		}
		if (focus > 0) {
			paint.setColor(0xFF00B9FE);
			int a = 160 + focus * 20;
			if (a > 255)
				a = 255;
			paint.setAlpha(a);
		}
		else {
			paint.setColor(0xff777777);
		}
		if( lines != null )
			try{
				synchronized (lines) {
					if (lines != null && lines.length > 0 ) {
						for(int i=0;i<lines.length;i++){
							if( lines[i] != null )
								canvas.drawText(lines[i], x, y+(i+1)*YouLyricView.item_height, paint);
						}	
					}	
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}

	public boolean isIn(float x, float y) {
		return (this.y <= y && this.y + height >= y);
	}



}

