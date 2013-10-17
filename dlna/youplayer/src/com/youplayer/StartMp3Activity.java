package com.youplayer;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.youplayer.player.R;

public class StartMp3Activity extends Activity {
	/** Called when the activity is first created. */
	private ImageButton mStart, mPause, mNext, mBefore, mStop;
	private TextView mTextView;
//	// private ImageView mImageView;
	private boolean bIsPaused = false;
	private boolean bIsReleased = false;
	private MediaPlayer mMediaPlayer;

	private OnClickListener mStartOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			
			if(bIsPaused == true){
				mMediaPlayer.start();
				bIsPaused = false;
				mStart.setVisibility(View.GONE);
				mPause.setVisibility(View.VISIBLE);
				return ;
			}
			// mStart.setImageResource(R.drawable.play_start);

			// mImageView.setImageResource(R.drawable.play_started);
			// mPause.setImageResource(R.drawable.play_pause);
			try {
				if (mMediaPlayer.isPlaying() == true) {
					// ��mMediaPlayer����
					mMediaPlayer.reset();
				}
				mMediaPlayer.setDataSource(url);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mPause.setVisibility(View.VISIBLE);
				v.setVisibility(View.GONE);
//				mTextView.setText("kai s h");
			} catch (IllegalArgumentException e) {
//				mTextView.setText(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
//				mTextView.setText(e.toString());
				e.printStackTrace();
			} catch (Exception e) {
//				mTextView.setText(e.toString());
				e.printStackTrace();
			}
		}
	};
	private OnClickListener mPauseOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (mMediaPlayer != null) {
				if (bIsReleased == false) {
					if (bIsPaused == false) {
						// ����mMediaPlayer��ͣ����
						mMediaPlayer.pause();
						bIsPaused = true;
						mStart.setVisibility(View.VISIBLE);
						mPause.setVisibility(View.GONE);
//						mTextView.setText(R.string.str_pause);
						// mImageView.setImageResource(R.drawable.play_paused);
						// mStart.setImageResource(R.drawable.play_start);
//						mPause.setText("start");
					} else if (bIsPaused == true) {
						mMediaPlayer.start();
						bIsPaused = false;
						mStart.setVisibility(View.GONE);
						mPause.setVisibility(View.VISIBLE);
//						mTextView.setText(R.string.str_start);
						// mImageView.setImageResource(R.drawable.play_started);
						// mStart.setImageResource(R.drawable.play_start);
//						mPause.setText("pause");
					}
				}
			}
		}
	};
	private OnClickListener mNextOnClickListener = new OnClickListener() {
		public void onClick(View v) {
//			mStart.setText("start");
			// mImageView.setImageResource(R.drawable.play_before_one);

			try {
				if (mMediaPlayer.isPlaying() == true) {
					// ��mMediaPlayer����
					mMediaPlayer.reset();
				}
				// ����mMediaPlayer��ȡSDcard���ļ�
				mMediaPlayer.setDataSource("/sdcard/zhangliangying.mp3");
				mMediaPlayer.prepare();
				// ��mMediaPlayer����
				mMediaPlayer.start();
				mTextView.setText(R.string.str_start);
				// mImageView.setImageResource(R.drawable.play_started);
			} catch (IllegalArgumentException e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			}
		}
	};
	private OnClickListener mBeforeOnClickListener = new OnClickListener() {
		public void onClick(View v) {
//			mStart.setImageResource(R.drawable.play_start);
//			mStart.setText("start");
			// mImageView.setImageResource(R.drawable.play_next_one);
			if (mMediaPlayer.isPlaying() == true) {
				// ��mMediaPlayer����
				mMediaPlayer.reset();
			}
			try {
				// ����mMediaPlayer��ȡSDcard���ļ�
				mMediaPlayer.setDataSource("/sdcard/chuanqi.mp3");
				mMediaPlayer.prepare();
				// ��mMediaPlayer����
				mMediaPlayer.start();
				mTextView.setText(R.string.str_start);
				// mImageView.setImageResource(R.drawable.play_started);
			} catch (IllegalArgumentException e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			}

		}
	};
	private OnClickListener mStopOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (mMediaPlayer.isPlaying() == true) {
				// ��mMediaPlayer����
				mMediaPlayer.reset();
				mTextView.setText(R.string.str_stop);
				// mStart.setImageResource(R.drawable.play_start);
				// mPause.setImageResource(R.drawable.play_pause);
				// mImageView.setImageResource(R.drawable.play_stoped);
			}
		}
	};
	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			mMediaPlayer.reset();
			mTextView.setText(R.string.str_finished);

//			mStart.setImageResource(R.drawable.play_start);
//			mStart.setText("start");
			// mImageView.setImageResource(R.drawable.play_started);
		}
	};
	private OnErrorListener mErrorListener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			try {
				// ���������ʱҲ�����Դ��MediaPlayer�ĸ�ֵ���ͷ���Դ
				mMediaPlayer.release();
				// �ı���ʾ��Ϣ
				mTextView.setText(R.string.str_OnErrorListener);
			} catch (Exception e) {
				mTextView.setText(e.toString());
				e.printStackTrace();
			}
			return false;
		}
	};
	private String url;
	private SeekBar seeker;
	private TextView totaltime;
	private TextView currentime;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_player_layout);
		Intent intent = getIntent();
		url = intent.getStringExtra("URL");
		
		mMediaPlayer = new MediaPlayer();
		mStart = (ImageButton) findViewById(R.id.btn_play);
		mPause = (ImageButton) findViewById(R.id.btn_pause);
//		mNext = (Button) findViewById(R.id.play_next);
//		mBefore = (Button) findViewById(R.id.play_before);
//		mStop = (Button) findViewById(R.id.play_stop);
//		// mImageView = (ImageView) findViewById(R.id.mImageView);
//		mTextView = (TextView) findViewById(R.id.mText);
		mTextView = (TextView)findViewById(R.id.title);
		
		seeker = (SeekBar)findViewById(R.id.playback_seeker);
		
		seeker.setVisibility(View.GONE);
		totaltime = (TextView)findViewById(R.id.tv_totalTime);
		currentime = (TextView)findViewById(R.id.tv_curTime);
		totaltime.setVisibility(View.GONE);
		currentime.setVisibility(View.GONE);
		
		TextView tv = (TextView) findViewById(R.id.title);
		tv.setVisibility(View.GONE);
		
		TextView content = (TextView) findViewById(R.id.content);
		content.setText(url);
		
		mStart.setOnClickListener(mStartOnClickListener);
		mPause.setOnClickListener(mPauseOnClickListener);
//		mStop.setOnClickListener(mStopOnClickListener);

		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
		mMediaPlayer.setOnErrorListener(mErrorListener);
		
		
	}
	
	Handler handler = new  Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x1233:
				int cur  = (Integer) msg.obj;
				currentime.setText(cur);
//				totaltime.setText();
				seeker.setProgress(cur*100/dur);
				break;

			default:
				break;
			}
			
		};
	};
	private boolean open = true;
	private int dur ;
	@Override
	protected void onResume() {
			open = true;
//			new 	 Thread(){
//				
//
//				private int cur;
//
//				public void run(){
////					if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
////						 dur = mMediaPlayer.getDuration();
////						
////						Message msg = new Message();
////						msg.obj = dur;
////						msg.what = 0x1233;
////						handler.sendMessage(msg);
//////						seeker.setMax(dur);
////					}
//					while(open){
//						if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
//							 cur = mMediaPlayer.getCurrentPosition();
//							Message msg = new Message();
//							msg.obj = cur;
//							msg.what = 0x1233;
//							handler.sendMessage(msg);
////							currentime.setText(cur);
////							seeker.setProgress(cur*100/dur);
//						}
//					}
//					
//				}
//			}.start();
		
		super.onResume();
	}

	/*
	 * ��д��������ͣ״̬�¼�
	 */
	protected void onPause() {
	open = false;
		try {
			// ����������ͣʱ�����Դ��MediaPlayer�ĸ�ֵ��ϵ
			mMediaPlayer.release();
		} catch (Exception e) {
			mTextView.setText(e.toString());
			e.printStackTrace();
		}
		super.onPause();
	}
}