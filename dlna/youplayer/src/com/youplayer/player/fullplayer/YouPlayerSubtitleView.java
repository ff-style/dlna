package com.youplayer.player.fullplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.youplayer.player.Player_UIManager;
import com.youplayer.util.YouUtility;

public class YouPlayerSubtitleView extends View {
	
	public static String titleStr /*, strLastSubTitle */;
	private int fontsize=0;
	Paint paint = new Paint();
	
	public String getTitleStr() {
		return titleStr;
	}

	public YouPlayerSubtitleView(Context context) {
		super(context);
		// strLastSubTitle = null;
		titleStr = null;
	}

	public YouPlayerSubtitleView(Context context, AttributeSet attr) {
		super(context, attr);
		paint.setTextSize(fontsize);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		// strLastSubTitle = null;
		titleStr = null;
	
	}

	int OFF_W = 20;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int height = getHeight();	// rotated when playing fully 
		int width  = getWidth();
		
		if( fontsize == 0 ) return;
		paint.setTextSize(fontsize);
		int RowGap = 8;

		if (getTitleStr() != null && getTitleStr().length() > 0) {
			String[] strList = getTitleStr().split("\n");
			int lines = 0;
			for(String str:strList)
				lines += measureLineCount(paint,str,width);
			int y = height - (fontsize + RowGap) * (lines ) - 10;
			
			for (int i = 0; i<strList.length; i++) {
				int strWidth = (int) paint.measureText(strList[i]);
				if( strWidth > width ){
					int center_i = strList[i].length()/2;
					for(int z=center_i;z<center_i+10&&z<strList[i].length();z++){
						if( strList[i].charAt(z) == ' ' )
						{
							center_i = z;
							break;
						}
					}
					String sub0 = strList[i].substring(0,center_i);
					String sub1 = strList[i].substring(center_i);
					int x =OFF_W;
					onDraw(canvas,sub0,x,y,paint,Color.WHITE,Color.BLACK);
					y += fontsize + RowGap;
					x = width - (int)paint.measureText(sub1)-OFF_W;
					onDraw(canvas,sub1,x,y,paint,Color.WHITE,Color.BLACK);
					y += fontsize + RowGap;
					sub0 = null;sub1 = null;
				}else{
					int x = (width - strWidth)/2;
					onDraw(canvas,strList[i],x,y,paint,Color.WHITE,Color.BLACK);
					y += fontsize + RowGap;
				}
			}
		}
	}
	private void onDraw(Canvas canvas,String str,int x,int y,Paint paint,int tcolor,int ocolor){
	    Player_UIManager.DrawOneLineStr(canvas,
				str,
				x,
				y,
				paint,
				Color.WHITE, /* text color */
				Color.BLACK /* outline color: BLACK */
		);
	}
	
	private int measureLineCount(Paint paint,String str,int w){
		int strw = (int) paint.measureText(str);
		if( strw < w )
			return 1;
		else
			return 2;//strw/w+1;
	}

	private void updateyouSize(){
//		int index = youUtility.getSubtitleyouSize(getContext());
	    int index = getFontSizeIndex();
		if( index == 0 )
			fontsize = getHeight()/10;
		else if( index == 1 )
			fontsize = getHeight()/15;
		else
			fontsize = getHeight()/18;
	}
	
	
	
	public static final int FONT_SIZE_INDEX_MAX = 0;
    public static final int FONT_SIZE_INDEX_NORMAL = 1;
    public static final int FONT_SIZE_INDEX_MIN = 2;    
    
    public int getFontSizeIndex() {
        return YouUtility.getSubtitileyouSize(getContext());
    }

    public void setFontSizeIndex(int index) {
    	YouUtility.setSubtitleFontSize(getContext(), index);
        
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
    		int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    	updateyouSize();
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
}
