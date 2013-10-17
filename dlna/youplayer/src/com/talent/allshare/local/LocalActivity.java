package com.talent.allshare.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.talent.allshare.ContentActivity;
import com.talent.allshare.util.FileFilterUtil;
import com.talent.allshare.util.IOHelper;
import com.talent.allshare.widget.PullToUpdataListView;
import com.talent.allshare.widget.PullToUpdataListView.OnRefreshListener;
import com.youplayer.StartMp3Activity;
import com.youplayer.player.R;
import com.youplayer.player.YouExplorer;

public class LocalActivity extends Activity implements OnClickListener {

	protected static final int UPDATE_VIDEO = 0x10001;
	protected static final int UPDATE_MUISC = 0x10002;
	protected static final int UPDATE_PIC = 0x10003;
	protected static final int UPDATE_FILE = 0x10004;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private ImageView t1, t2, t3, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private Button back;
	private FileAdapter videoAdapter;
	private FileAdapter musicAdapter;
	private FileAdapter picAdapter;
	private FileAdapter fileAdapter;

	private Handler handler;
	private PullToUpdataListView pul_video_list;
	private PullToUpdataListView pul_music_list;
	private PullToUpdataListView pul_pic_list;
	private PullToUpdataListView pul_file_list;

	private boolean isLoading;
	private LinearLayout ll_progress;
	private ImageView iv_loading_frame;
	private AnimationDrawable loadingFrameAnimDrawable;
	private List<File> videos;
	private List<File> musics;
	private List<File> pics;
	private List<File> files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local);

		videoAdapter = new FileAdapter(this);
		musicAdapter = new FileAdapter(this);
		picAdapter = new FileAdapter(this);
		fileAdapter = new FileAdapter(this);
		handler = new Handler() {
			// private List<File> videos;
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				switch (msg.what) {
				case UPDATE_VIDEO:
					isLoading = false;
					ll_progress.setVisibility(View.GONE);
					pul_video_list.onRefreshComplete();
					videos = (List<File>) msg.obj;
					videoAdapter.setDataList(videos, 0);
					pul_video_list.setAdapter(videoAdapter);
					videoAdapter.notifyDataSetChanged();
					break;
				case UPDATE_MUISC:
					isLoading = false;
					ll_progress.setVisibility(View.GONE);
					pul_music_list.onRefreshComplete();
					musics = (List<File>) msg.obj;
					musicAdapter.setDataList(musics, 1);
					pul_music_list.setAdapter(musicAdapter);
					musicAdapter.notifyDataSetChanged();
					break;
				case UPDATE_PIC:
					isLoading = false;
					ll_progress.setVisibility(View.GONE);
					pul_pic_list.onRefreshComplete();
					pics = (List<File>) msg.obj;
					picAdapter.setDataList(pics, 2);
					pul_pic_list.setAdapter(picAdapter);
					picAdapter.notifyDataSetChanged();
					break;
				case UPDATE_FILE:
					isLoading = false;
					ll_progress.setVisibility(View.GONE);
					pul_file_list.onRefreshComplete();
					files = (List<File>) msg.obj;
					fileAdapter.setDataList(files, -1);
					pul_file_list.setAdapter(fileAdapter);
					fileAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}

			}
		};
		
		iv_loading_frame = (ImageView) this.findViewById(R.id.iv_loading_frame);
		ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
		InitImageView();
		InitTextView();
		InitViewPager();

		pul_video_list.setMoreEnable(false);
		pul_music_list.setMoreEnable(false);
		pul_pic_list.setMoreEnable(false);
		pul_file_list.setMoreEnable(false);

		pul_video_list.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (isLoading) {
					pul_video_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}
				searchVideo();
			}
		});

		pul_video_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (isLoading) {
					pul_video_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}

				File data = videos.get(position - 1);
				String url = data.getPath();
				if (url == null || "".equals(url)) {
					return;
				}

				// IOHelper.openFile(LocalActivity.this, data);
				Intent intent = new Intent(LocalActivity.this,
						YouExplorer.class);
				intent.setAction("android.intent.action.VIEW");
				intent.setData(Uri.parse(url));
				LocalActivity.this.startActivity(intent);
				// VideoBean vb = new VideoBean();
				// vb.playUrl = url;
				// vb.title = "视频文件";
				// vb.itemId =-2;
				//
				// softApplication.saveHistory(vb.title,vb.playUrl);
			}
		});

		pul_music_list.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (isLoading) {
					pul_music_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}

				searchMusic();
			}
		});

		pul_music_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (isLoading) {
					pul_music_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}

				File data = musics.get(position - 1);
				String url = data.getPath();
				if (url == null || "".equals(url)) {
					return;
				}
//				IOHelper.openFile(LocalActivity.this, data);
//				Intent intent = new Intent(LocalActivity.this,StartMp3Activity.class);
//				intent.putExtra("URL", data.getPath());
//				startActivity(intent);
				
				Intent intent = new Intent(LocalActivity.this, YouExplorer.class);
				intent.setAction("android.intent.action.VIEW");
				intent.setData(Uri.parse(url));
				startActivity(intent);			
				// File data = musics.get(position-1);
				// String url = data.getPath();
				// if(url == null || "".equals(url)){
				// return;
				// }
				// Intent intent = new Intent(LocalActivity.this,
				// MusicExplorer.class);
				// intent.setAction("android.intent.action.VIEW");
				// intent.setData(Uri.parse(url));
				// LocalActivity.this.startActivity(intent);

				// MediaManager.getInstance().setMusicList(mCurItems);
				// Intent intent = new Intent();
				// intent.setClass(LocalActivity.this,
				// MusicPlayerActivity.class);
				// intent.putExtra(MusicPlayerActivity.PLAY_INDEX, 1);
				// Item item = new Item();
				// List<Item> lists = new ArrayList<Item>();
				// lists.add(item);
				// MediaManager.getInstance().setMusicList(lists);
				// item.setRes(Uri.parse(url)+"");
				// ItemFactory.putItemToIntent(item, intent);
				// LocalActivity.this.startActivity(intent);

				// VideoBean vb = new VideoBean();
				// vb.playUrl = url;
				// vb.title = "视频文件";
				// vb.itemId =-2;
				//
				// softApplication.saveHistory(vb.title,vb.playUrl);
			}
		});

		pul_pic_list.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (isLoading) {
					pul_pic_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}
				searchPic();
			}
		});

		pul_pic_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (isLoading) {
					pul_file_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}

				File data = pics.get(position - 1);
				String url = data.getPath();
				if (url == null || "".equals(url)) {
					return;
				}
				// Intent intent = new Intent(LocalActivity.this,
				// YouExplorer.class);
				// intent.setAction("android.intent.action.VIEW");
				// intent.setData(Uri.parse(url));
				// LocalActivity.this.startActivity(intent);
				IOHelper.openFile(LocalActivity.this, data);
				// VideoBean vb = new VideoBean();
				// vb.playUrl = url;
				// vb.title = "视频文件";
				// vb.itemId =-2;
				//
				// softApplication.saveHistory(vb.title,vb.playUrl);
			}
		});

		pul_file_list.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (isLoading) {
					pul_pic_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}
				searchFile();
			}
		});

		pul_file_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (isLoading) {
					pul_file_list.onRefreshComplete();
					Toast.makeText(LocalActivity.this,
							R.string.is_loading_last_task, 10000).show();
					return;
				}

				File data = files.get(position - 1);
				String url = data.getPath();
				if (url == null || "".equals(url)) {
					return;
				}
				IOHelper.openFile(LocalActivity.this, data);

				// Intent intent = new Intent(LocalActivity.this,
				// YouExplorer.class);
				// intent.setAction("android.intent.action.VIEW");
				// intent.setData(Uri.parse(url));
				// LocalActivity.this.startActivity(intent);
				// VideoBean vb = new VideoBean();
				// vb.playUrl = url;
				// vb.title = "视频文件";
				// vb.itemId =-2;
				//
				// softApplication.saveHistory(vb.title,vb.playUrl);
			}
		});

		searchVideo();

	}

	private void searchVideo() {
		ll_progress.setVisibility(View.VISIBLE);
		startLoadingFrameAnim(iv_loading_frame);
		isLoading = true;

		new Thread() {
			public void run() {
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(), new String[] {
						"flv", "mkv", "mp4", "3gp", "wmv" });
				Message msg = new Message();
				msg.what = UPDATE_VIDEO;
				msg.obj = files;
				handler.sendMessage(msg);
			}
		}.start();
		// handler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// List<File> files = FileFilterUtil.getFiles(Environment
		// .getExternalStorageDirectory().getPath(),
		// new String[] { "mkv","rmvb","avi","rar"});
		// Message msg = new Message();
		// msg.what = UPDATE_VIDEO;
		// msg.obj = files;
		// handler.sendMessage(msg);
		// }
		// });
	}

	private void searchMusic() {
		ll_progress.setVisibility(View.VISIBLE);
		startLoadingFrameAnim(iv_loading_frame);
		isLoading = true;
		new Thread() {
			public void run() {
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "mp3" });
				Message msg = new Message();
				msg.what = UPDATE_MUISC;
				msg.obj = files;
				handler.sendMessage(msg);

			}
		}.start();
	}

	private void searchPic() {
		ll_progress.setVisibility(View.VISIBLE);
		startLoadingFrameAnim(iv_loading_frame);
		isLoading = true;
		new Thread() {
			public void run() {
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "jpg" });
				Message msg = new Message();
				msg.what = UPDATE_PIC;
				msg.obj = files;
				handler.sendMessage(msg);

			}
		}.start();
	}

	private void searchFile() {
		ll_progress.setVisibility(View.VISIBLE);
		startLoadingFrameAnim(iv_loading_frame);
		isLoading = true;
		new Thread() {
			public void run() {
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "txt" ,"pdf","doc","xls","xlsx","pdf"});
				Message msg = new Message();
				msg.what = UPDATE_FILE;
				msg.obj = files;
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 开始"..."的帧动画
	 * 
	 * @param imageView
	 */
	public void startLoadingFrameAnim(ImageView imageView) {
		imageView.setBackgroundResource(R.anim.frame_animation);
		loadingFrameAnimDrawable = (AnimationDrawable) imageView
				.getBackground();
		imageView.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						loadingFrameAnimDrawable.start();
						return true;
					}
				});
	}

	/**
	 * 停止载入中的帧动画"..."
	 */
	public void stopLoadingFrameAnim() {
		if (loadingFrameAnimDrawable != null) {
			loadingFrameAnimDrawable.stop();
		}
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		pul_video_list = (PullToUpdataListView) mInflater.inflate(
				R.layout.pul_list, null);
		pul_music_list = (PullToUpdataListView) mInflater.inflate(
				R.layout.pul_list, null);
		pul_pic_list = (PullToUpdataListView) mInflater.inflate(
				R.layout.pul_list, null);
		pul_file_list = (PullToUpdataListView) mInflater.inflate(
				R.layout.pul_list, null);
		listViews.add(pul_video_list);
		listViews.add(pul_music_list);
		listViews.add(pul_pic_list);
		listViews.add(pul_file_list);
		mPager.setAdapter(new MyPagerAdapter(listViews));
		setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (ImageView) findViewById(R.id.text1);
		t2 = (ImageView) findViewById(R.id.text2);
		t3 = (ImageView) findViewById(R.id.text3);
		t4 = (ImageView) findViewById(R.id.text4);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth() / 2;// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 4;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	// @Override
	// public void onClickEvent(View view) {
	// switch (view.getId()) {
	// case R.id.text1:
	// break;
	//
	// default:
	// break;
	// }
	// }

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {

			setCurrentItem(index);
		};

	}

	private void setCurrentItem(int i) {
		mPager.setCurrentItem(i);
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		int three = one * 3;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);

			t1.setImageResource(R.drawable.tab_video_s);
			t2.setImageResource(R.drawable.tab_music_s);
			t3.setImageResource(R.drawable.tab_pic_s);
			t4.setImageResource(R.drawable.tab_file_s);
			
			
			if (arg0 == 0) {
				if (videos == null || videos.size() == 0) {
					searchVideo();
					
				}
				t1.setImageResource(R.drawable.tab_video);
				
			} else if (arg0 == 1) {
				if (musics == null || musics.size() == 0) {
					searchMusic();
				}
				t2.setImageResource(R.drawable.tab_music);
			} else if (arg0 == 2) {
				if (pics == null || pics.size() == 0) {
					searchPic();
				}
				t3.setImageResource(R.drawable.tab_pic);
			} else if (arg0 == 3) {
				System.out.println(files);
				if (files == null || files.size() == 0) {
					searchFile();
				}
				t4.setImageResource(R.drawable.tab_file);
			}
			
			

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(LocalActivity.this)
				.setTitle("确定退出？")
				.setNegativeButton("确定",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								LocalActivity.this.finish();
							}
						})
				.setNeutralButton("取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}

}
