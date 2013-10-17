package com.youplayer.player.fullplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.youplayer.core.adapter.YouPlayerJNICallBack;
import com.youplayer.core.adapter.YouPlayerJNIUtil;
import com.youplayer.core.struct.You_full_screen_player_data_to_ui.Cls_fn_related_content_t;
import com.youplayer.player.R;


public class YouPlayerRelativeList extends ListView{

	OnSelectedListener selectedListener;
	LinkImageAdapter adapter;

	public static boolean wifi_yes =true;
	public static boolean user_hide=true;
	public static boolean hasData = false;

	public static boolean isShouldShow(){
		return wifi_yes&&!user_hide&&hasData;
	}
	

	private static Bitmap default_img,default_shadow_img; 
	
	public static int getRelativeWidth(){
	    if(default_img != null){
	        return (int)(default_img.getWidth()/105f * 180);
	    }
	    return 180;
	}
	public YouPlayerRelativeList(Context context, AttributeSet attrs) {
		super(context, attrs);
		if( default_img == null ){//列表大小字体按图片 大小进行缩放
			default_img =  BitmapFactory.decodeResource(getResources(), R.drawable.youplayer_link_icon_video);
			default_shadow_img = BitmapFactory.decodeResource(getResources(), R.drawable.youplayer_fullplayer_img_up_shadow);
			float p = default_img.getWidth()/105f;
			RelativeItem.WIDTH = (int) (180*p);
			RelativeItem.DIS_H = (int) (8*p); 
			RelativeItem.TEXTSIZE = (int) (24*p);//设置关联内容文字大小
			RelativeItem.HEIGHT =RelativeItem.DIS_H*3+default_shadow_img.getHeight()+RelativeItem.TEXTSIZE ;
		}
		setVisibility( isShouldShow() ? View.VISIBLE : View.INVISIBLE );
		
	}
	public  static Bitmap getDefaultBitmap(){
		return default_img;
	}
	public  static Bitmap getDefaultBoxBitmap(){
		return default_shadow_img;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

			super.onMeasure(
					MeasureSpec.makeMeasureSpec(RelativeItem.WIDTH,MeasureSpec.EXACTLY),
					heightMeasureSpec);
			
	}
	class LinkImageAdapter extends BaseAdapter{

		Cls_fn_related_content_t[] list;
		public void setDatas(Cls_fn_related_content_t[]  list){
			this.list = list;
		}
		public void clear(){
			list=null;
		}
		@Override
		public int getCount() {
			return list == null ? 0 : list.length;
		}

		@Override
		public Object getItem(int position) {
			return list[position];
		}

		@Override
		public long getItemId(int p) {
			return p;
		}

		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			final Cls_fn_related_content_t media = list != null ? list[position] : null ;
			if( media == null )
				return new RelativeItem(getContext(),"");
			OnClickListener listener = new OnClickListener(){
				@Override
				public void onClick(View v) {
						if( selectedListener != null && !seledted ){
							//Log.i("lrl","selected index:"+position);
							seledted = selectedListener.onSelected(position);
						}	
				}
			};
			
			RelativeItem item;
			
//			Bitmap cache_bit = cache.getBitmap(media.pic);
			
			if( convertView == null ){
				item = new RelativeItem(getContext(),media.name);
				item.setLayoutParams(new AbsListView.LayoutParams(RelativeItem.WIDTH,RelativeItem.HEIGHT));
			}	
			else{
				item = (RelativeItem) convertView;
			}

			
			item.setText(media.name);
			item.setOnClickListener(listener);
			
			return item;
			
			
		}
		
	};
	

	boolean seledted = false;
	
	public void initDatas(Cls_fn_related_content_t[]  linkMedias,OnSelectedListener selectedListener) {
		seledted = false;
		this.selectedListener = selectedListener;
		if( adapter != null ){
			adapter.clear();
			adapter.notifyDataSetChanged();
			adapter = null;
		}
		//removeAllViews();
		
		
		adapter = new LinkImageAdapter();
		adapter.setDatas(linkMedias);
		setAdapter(adapter);
		if( adapter.getCount() > 0 )
			hasData = true;
		adapter.notifyDataSetChanged();
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
		    if(adapter != null){
		        adapter.notifyDataSetChanged();
		    }
		};
	};

	public void getImage(final String pic) {
		
		YouPlayerJNIUtil.getBitmapWithUrl(pic,new YouPlayerJNICallBack(){

			@Override
			public void callback(Object request, Object response) {
				Bitmap bitmap = BitmapFactory.decodeFile(response.toString());
				if( bitmap != null ){
//					cache.addBitmap(pic,bitmap);
					handler.sendEmptyMessage(0);
				}
				
			}
			
		});
	}

	public void onDestoryed(){
		//Log.i("lrl","list onDestoryed");
		//cache.recycle();
		if( adapter != null ) {
			adapter.clear();
			adapter.notifyDataSetChanged();
			adapter = null;
		}
		hasData = false;
		
	}
	
	public int getLinkCount(){
		if( adapter != null ){
			return adapter.getCount();
		}
		return 0;
	}
	

	public boolean isDataEquals(Cls_fn_related_content_t[]  linkMedias) {
		return (adapter != null && linkMedias != null  && linkMedias == adapter.list );
	}
	
	public static interface OnSelectedListener{
		public boolean onSelected(int index);
		public void onTouch(int action);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if( selectedListener == null ) 
			return super.onTouchEvent(ev);
		if( ev.getAction() == MotionEvent.ACTION_DOWN ){
		}else if( ev.getAction() == MotionEvent.ACTION_MOVE ){
			selectedListener.onTouch(MotionEvent.ACTION_MOVE);
		}else if( ev.getAction() == MotionEvent.ACTION_UP ){
			selectedListener.onTouch(MotionEvent.ACTION_UP);
		}	
		return super.onTouchEvent(ev);
	}

}

class RelativeItem extends View{

	static int DIS_H = 5;   
	static int HEIGHT = 120;
	static int WIDTH = 181;
	static int TEXTSIZE = 24;
	Bitmap bitmap;
	String text ;
	Paint paint ;
	
	public RelativeItem(Context context,String text){
		super(context);
		this.text = text;
		paint = new Paint();
		paint.setColor(0xEEFFFFFF);
		paint.setTextSize(TEXTSIZE);
	}
	

	public void setText(String text){
		this.text = text;
	}

	public void setImage(Bitmap bit){
		bitmap = bit;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		if( getWidth() == 0 ) return;
		int image_h = getHeight()-TEXTSIZE - DIS_H;
		
		
		Bitmap defbit = YouPlayerRelativeList.getDefaultBitmap();
		if( bitmap != null && !bitmap.isRecycled() ){
			//canvas.drawBitmap(bitmap,(getWidth()-bitmap.getWidth())/2,(image_h-bitmap.getHeight())/2, paint);
			Rect tar = new Rect((getWidth()-defbit.getWidth())/2,(image_h-defbit.getHeight())/2,
					(getWidth()+defbit.getWidth())/2,(image_h+defbit.getHeight())/2	);
			canvas.drawBitmap(bitmap, null ,tar, paint);
		}	
		else{//默认图片
			canvas.drawBitmap( defbit,(getWidth()-defbit.getWidth())/2,(image_h-defbit.getHeight())/2, paint);
		}
		//画边框图片
		Bitmap box = YouPlayerRelativeList.getDefaultBoxBitmap();
		canvas.drawBitmap(box,(getWidth()-box.getWidth())/2,(image_h-box.getHeight())/2, paint);
		if( text != null ){
			if( paint.measureText(text)>getWidth() ){
				text = format(text);
			}
			
			canvas.drawText(text,(getWidth()-paint.measureText(text))/2,getHeight()-DIS_H, paint);
		}
		canvas.restore();
	}

	private String format(String str){
		
		StringBuffer sb = new StringBuffer(str);
		while(true){
			sb.deleteCharAt(sb.length()/2);
			if( getWidth() > paint.measureText(sb.toString()) )
				break;
		}
		return sb.toString();
	}

}


