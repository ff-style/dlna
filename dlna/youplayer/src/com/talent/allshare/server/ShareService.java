package com.talent.allshare.server;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.TextItem;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.talent.allshare.util.FileFilterUtil;
import com.talent.allshare.util.IOHelper;
import com.wireme.activity.WireUpnpService;
import com.wireme.mediaserver.ContentNode;
import com.wireme.mediaserver.ContentTree;
import com.wireme.mediaserver.MediaServer;


public class ShareService extends Service {

	private boolean mbstarted;
	private boolean mbsharevideo=true;
	private boolean mbsharemusic=true;
	private boolean mbsharepicture=true;
	private boolean mbsharedoc=true;
	private String mdevicename="MacroDisk";

	private final static String TAG = "ShareService";  


	private AndroidUpnpService upnpService=null;

	private MediaServer mediaServer;

	private static boolean serverPrepared = false;
	

	protected static final int UPDATE_VIDEO = 0x10001;
	protected static final int UPDATE_MUISC = 0x10002;
	protected static final int UPDATE_PIC = 0x10003;
	protected static final int UPDATE_FILE = 0x10004;

	protected static final int LOADSTEP_IDLE = 0;
	protected static final int LOADSTEP_STOPPENDING = 1;
	protected static final int LOADSTEP_BEGIN = 2;
	protected static final int LOADSTEP_VIDEO = 3;
	protected static final int LOADSTEP_MUISC = 4;
	protected static final int LOADSTEP_PIC = 5;
	protected static final int LOADSTEP_FILE = 6;



    private int mloadstep=LOADSTEP_IDLE;


	private Handler handler;
	
	@Override
	public IBinder onBind(Intent intent) {
	
	Log.i(TAG, "onBind");
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG,"ShareService oncreat");

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					
				case UPDATE_VIDEO:
					
					if(mbstarted==true){
						List<File> files=(List<File>) msg.obj;

						ContentNode rootNode = ContentTree.getRootNode();
						// Video Container
						Container videoContainer = new Container();
						videoContainer.setClazz(new DIDLObject.Class("object.container"));
						videoContainer.setId(ContentTree.VIDEO_ID);
						videoContainer.setParentID(ContentTree.ROOT_ID);
						videoContainer.setTitle("Videos");
						videoContainer.setRestricted(true);
						videoContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
						videoContainer.setChildCount(0);
					
						rootNode.getContainer().addContainer(videoContainer);
						rootNode.getContainer().setChildCount(
								rootNode.getContainer().getChildCount() + 1);
						ContentTree.addNode(ContentTree.VIDEO_ID, new ContentNode(
								ContentTree.VIDEO_ID, videoContainer));

						for(int i=0;i<files.size();i++){
							String id = ContentTree.VIDEO_PREFIX+i;
							String title = files.get(i).getName();
							String creator ="system";
							String filePath =files.get(i).getPath()+"";
							String mimeType = IOHelper.getMIMEType(title);
							long size = 50*1024*1024;
							long duration =60*60*1000;
							String resolution =null;
							Res res = new Res(new MimeType(mimeType.substring(0,
									mimeType.indexOf('/')), mimeType.substring(mimeType
									.indexOf('/') + 1)), size, "http://"
									+ mediaServer.getAddress() + "/" + id);
							res.setDuration(duration / (1000 * 60 * 60) + ":"
									+ (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
									+ (duration % (1000 * 60)) / 1000);
							res.setResolution(resolution);
				
							VideoItem videoItem = new VideoItem(id, ContentTree.VIDEO_ID,
									title, creator, res);
							videoContainer.addItem(videoItem);
							videoContainer
									.setChildCount(videoContainer.getChildCount() + 1);
							ContentTree.addNode(id,
									new ContentNode(id, videoItem, filePath));
				
							//Log.i(TAG, "added video item " + title + "from " + filePath);
						}
					}
					
					dosearch();
					
					break;
				case UPDATE_MUISC:
					if(mbstarted==true){
						List<File> files=(List<File>) msg.obj;

						ContentNode rootNode = ContentTree.getRootNode();
						// Audio Container
						Container audioContainer = new Container(ContentTree.AUDIO_ID,
								ContentTree.ROOT_ID, "Audios", "GNaP MediaServer",
								new DIDLObject.Class("object.container"), 0);
						audioContainer.setRestricted(true);
						audioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
						rootNode.getContainer().addContainer(audioContainer);
						rootNode.getContainer().setChildCount(
								rootNode.getContainer().getChildCount() + 1);
						ContentTree.addNode(ContentTree.AUDIO_ID, new ContentNode(
								ContentTree.AUDIO_ID, audioContainer));

						

						for(int i=0;i<files.size();i++){
							String id = ContentTree.AUDIO_PREFIX+i;
							String title = files.get(i).getName();
							String creator ="system";
							String filePath =files.get(i).getPath()+"";
							String mimeType = IOHelper.getMIMEType(title);
							long size = 50*1024*1024;
							long duration =60*60*1000;
							String album ="system";
							Res res = new Res(new MimeType(mimeType.substring(0,
									mimeType.indexOf('/')), mimeType.substring(mimeType
									.indexOf('/') + 1)), size, "http://"
									+ mediaServer.getAddress() + "/" + id);
							res.setDuration(duration / (1000 * 60 * 60) + ":"
									+ (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
									+ (duration % (1000 * 60)) / 1000);
				
							// Music Track must have `artist' with role field, or
							// DIDLParser().generate(didl) will throw nullpointException
							MusicTrack musicTrack = new MusicTrack(id,
									ContentTree.AUDIO_ID, title, creator, album,
									new PersonWithRole(creator, "Performer"), res);
							audioContainer.addItem(musicTrack);
							audioContainer
									.setChildCount(audioContainer.getChildCount() + 1);
							ContentTree.addNode(id, new ContentNode(id, musicTrack,
									filePath));
				
							//Log.i(TAG, "added audio item " + title + "from " + filePath);
						}
					}
					
					dosearch();
					break;
				case UPDATE_PIC:
					if(mbstarted==true){
						List<File> files=(List<File>) msg.obj;

						ContentNode rootNode = ContentTree.getRootNode();
						// Image Container
						Container imageContainer = new Container(ContentTree.IMAGE_ID,
								ContentTree.ROOT_ID, "Images", "GNaP MediaServer",
								new DIDLObject.Class("object.container"), 0);
						imageContainer.setRestricted(true);
						imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
						rootNode.getContainer().addContainer(imageContainer);
						rootNode.getContainer().setChildCount(
								rootNode.getContainer().getChildCount() + 1);
						ContentTree.addNode(ContentTree.IMAGE_ID, new ContentNode(
								ContentTree.IMAGE_ID, imageContainer));

						for(int i=0;i<files.size();i++){
							String id = ContentTree.IMAGE_PREFIX+i;	
							String title = files.get(i).getName();
							String creator ="system";
							String filePath =files.get(i).getPath()+"";
							String mimeType = IOHelper.getMIMEType(title);
							long size = 50*1024*1024;				
							Res res = new Res(new MimeType(mimeType.substring(0,
									mimeType.indexOf('/')), mimeType.substring(mimeType
									.indexOf('/') + 1)), size, "http://"
									+ mediaServer.getAddress() + "/" + id);
				
							ImageItem imageItem = new ImageItem(id, ContentTree.IMAGE_ID,
									title, creator, res);
							imageContainer.addItem(imageItem);
							imageContainer
									.setChildCount(imageContainer.getChildCount() + 1);
							ContentTree.addNode(id,
									new ContentNode(id, imageItem, filePath));
				
							//Log.i(TAG, "added image item " + title + "from " + filePath);
						}
					}
					
					dosearch();
					break;
				case UPDATE_FILE:
					if(mbstarted==true){
						List<File> files=(List<File>) msg.obj;

						ContentNode rootNode = ContentTree.getRootNode();
						// doc Container
						Container docContainer = new Container(ContentTree.FILE_ID,
								ContentTree.ROOT_ID, "document", "GNaP MediaServer",
								new DIDLObject.Class("object.container"), 0);
						docContainer.setRestricted(true);
						docContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
						rootNode.getContainer().addContainer(docContainer);
						rootNode.getContainer().setChildCount(
								rootNode.getContainer().getChildCount() + 1);
						ContentTree.addNode(ContentTree.FILE_ID, new ContentNode(
								ContentTree.FILE_ID, docContainer));

						for(int i=0;i<files.size();i++){
							String id = ContentTree.FILE_PREFIX+i;			
							String title = files.get(i).getName();
							String creator ="system";
							String filePath =files.get(i).getPath()+"";
							String mimeType = IOHelper.getMIMEType(title);
							long size = 50*1024*1024;				
							Res res = new Res(new MimeType(mimeType.substring(0,
									mimeType.indexOf('/')), mimeType.substring(mimeType
									.indexOf('/') + 1)), size, "http://"
									+ mediaServer.getAddress() + "/" + id);
				
							TextItem textItem = new TextItem(id, ContentTree.FILE_ID,
									title, creator, res);
							docContainer.addItem(textItem);
							docContainer
									.setChildCount(docContainer.getChildCount() + 1);
							ContentTree.addNode(id,
									new ContentNode(id, textItem, filePath));
				
							//Log.i(TAG, "added doc item " + title + "from " + filePath);
						}
					}
					
					dosearch();
					break;
				default:
					break;
				}

			}
		};
		
	}
	private IShareService.Stub mBinder = new IShareService.Stub(){
		@Override
		public void start() throws RemoteException {

		    mbstarted = true;
			
			try {

				if(upnpService==null){

				
					final Intent intent = new Intent(ShareService.this,WireUpnpService.class);
					getApplicationContext().bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);

				}else {
					startupnpService();
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();

			}
			
			
			
				
			
		}

		@Override
		public void stop() throws RemoteException {
			

			cleanService();
			
			mbstarted = false;
			
		}
		
		@Override
		public void restart() throws RemoteException {
			

			stop();

			start();

			
			
		}

		@Override
		public boolean isStarted() throws RemoteException {
			return mbstarted;
		}

		@Override
		public void updateconfig(String devname,boolean bsharevideo,boolean bsharemusic,boolean bsharepicture,boolean sharedoc) throws RemoteException {

			mbsharevideo=bsharevideo;
			mbsharemusic=bsharemusic;
			mbsharepicture=bsharepicture;
			mbsharedoc=sharedoc;
			mdevicename=devname;
		}
		
		@Override
		public String getCurDevName() throws RemoteException {
			return mdevicename;
		}

		@Override
		public boolean getbsharevideo() throws RemoteException {
			return mbsharevideo;
		}

		@Override
		public boolean getbsharemusic() throws RemoteException {
			return mbsharemusic;
		}

		@Override
		public boolean getbsharepicture() throws RemoteException {
			return mbsharepicture;
		}

		@Override
		public boolean getbsharedoc() throws RemoteException {
			return mbsharedoc;
		}
	};

	@Override
	public void onDestroy() {

		mbstarted = false;

	    cleanService();
		
		if(upnpService!=null){
			
			getApplicationContext().unbindService(serviceConnection);
		}

		super.onDestroy();
	}


		private void startupnpService() {

			Log.i(TAG, "startupnpService upnpService="+upnpService);
		
			if(upnpService==null){

				
				final Intent intent = new Intent(this,WireUpnpService.class);
				getApplicationContext().bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);

			}else {
					cleanService();
					
					try {
						mediaServer = new MediaServer(getLocalIpAddress(),mdevicename);

						upnpService.getRegistry()
								.addDevice(mediaServer.getDevice());
						
						autoNotifyDevices();
						
						prepareMediaServer();
		
					} catch (Exception ex) {
		
						return;
					}
				}
			}


		private void cleanService() {
			
			
		if(upnpService!=null){
			
			if (mediaServer != null) {
					upnpService.getRegistry().removeDevice(mediaServer.getDevice());
					mediaServer.stopHttpServer();

					clearMediaServer();
					
					mediaServer=null;
				}
			}
		}
		

		private ServiceConnection serviceConnection = new ServiceConnection() {
		
			public void onServiceConnected(ComponentName className, IBinder service) {
				upnpService = (AndroidUpnpService) service;
				Log.i(TAG, "Connected to UPnP Service");
		
				startupnpService();
		
			}
		
			public void onServiceDisconnected(ComponentName className) {
				upnpService = null;
			}
		};
		
			private void prepareMediaServer() {

			if (serverPrepared)
				return;

			startsearch();

			serverPrepared = true;
			
		    
		}

		private void clearMediaServer() {
		
			if (serverPrepared==false)
				return;
		
		    // stop handle
			stopsearch();
			
			ContentTree.resetRootNode();
			serverPrepared = false;
		}
		
			// FIXME: now only can get wifi address
		private InetAddress getLocalIpAddress() throws UnknownHostException {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			return InetAddress.getByName(String.format("%d.%d.%d.%d",
					(ipAddress & 0xff), (ipAddress >> 8 & 0xff),
					(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
		}


		


		private void searchVideo() {
		
		new Thread(){
			public void run(){
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "flv","mkv","mp4","3gp","wmv"});
				Message msg = new Message();
				msg.what = UPDATE_VIDEO;
				msg.obj = files;
				handler.sendMessage(msg);
			}
		}.start();

		
	}

	private void searchMusic() {

		new Thread(){
			public void run(){
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "mp3"});
				Message msg = new Message();
				msg.what = UPDATE_MUISC;
				msg.obj = files;
				handler.sendMessage(msg);

			}
		}.start();
	}

	
	private void searchPic() {


		new Thread(){
			public void run(){
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "jpg"});
				Message msg = new Message();
				msg.what = UPDATE_PIC;
				msg.obj = files;
				handler.sendMessage(msg);

			}
		}.start();
	}
	
	
	private void searchFile() {


		new Thread(){
			public void run(){
				List<File> files = FileFilterUtil.getFiles(Environment
						.getExternalStorageDirectory().getPath(),
						new String[] { "doc","pdf","txt","xls","xlsx","docx"});
				Message msg = new Message();
				msg.what = UPDATE_FILE;
				msg.obj = files;
				handler.sendMessage(msg);

			}
		}.start();
	}

	private void dosearch() {


	    Log.i(TAG,"dosearch loadstep ="+ mloadstep+ ";bstarted="+mbstarted);
		
		if(mbstarted ==false) {
			mloadstep=LOADSTEP_IDLE;
			return;
		}

		if(mloadstep==LOADSTEP_IDLE) {
			return;
		}

		if(mloadstep==LOADSTEP_STOPPENDING) {
			mloadstep=LOADSTEP_BEGIN;
			//continue
		}

		if(mloadstep==LOADSTEP_BEGIN) {

			if(mbsharevideo==true) {
				
				mloadstep=LOADSTEP_VIDEO;
				
				searchVideo();
				
				return ;
			}else {
			   //next step continue
			   mloadstep=LOADSTEP_VIDEO;
			}
		}
		

		if(mloadstep==LOADSTEP_VIDEO) {

			if(mbsharemusic==true) {
				
				mloadstep=LOADSTEP_MUISC;
				
				searchMusic();
				
				return ;
			}else {
			   //next step continue
			   mloadstep=LOADSTEP_MUISC;
			}
		}


		if(mloadstep==LOADSTEP_MUISC) {

			if(mbsharepicture==true) {
				
				mloadstep=LOADSTEP_PIC;
				
				searchPic();
				
				return ;
			}else {
			   //next step continue
			   mloadstep=LOADSTEP_PIC;
			}
		}


		if(mloadstep==LOADSTEP_PIC) {

			if(mbsharedoc==true) {
				
				mloadstep=LOADSTEP_FILE;
				
				searchFile();
				
				return ;
			}else {
			   //next step continue
			   mloadstep=LOADSTEP_FILE;
			}
		}

		if(mloadstep==LOADSTEP_FILE) {

			mloadstep=LOADSTEP_IDLE;
			return;
		}

		return;
	}

	private void startsearch() {

	
	Log.i(TAG,"startsearch loadstep ="+ mloadstep);

		if(mloadstep==LOADSTEP_IDLE) {

			mloadstep=LOADSTEP_BEGIN;
			
		}else if (mloadstep==LOADSTEP_STOPPENDING) {

			return;
		}

		dosearch();
		
		return;
	}

	private void stopsearch() {

		Log.i(TAG,"stopsearch loadstep ="+ mloadstep);
		
		if(mloadstep!=LOADSTEP_IDLE) {

			mloadstep=LOADSTEP_STOPPENDING;
		}
		
	}

    private final Runnable mautoNotifyDevicesRunnable = new Runnable() {
		
        public void run() {
			if(mbstarted==true){
				if(upnpService!=null){
			
				if (mediaServer != null) {
						upnpService.getRegistry().removeDevice(mediaServer.getDevice());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						upnpService.getRegistry().addDevice(mediaServer.getDevice());
					}
				}
				handler.postDelayed(mautoNotifyDevicesRunnable,60*1000);
			}

        }
    };

	private void autoNotifyDevices() {

		Log.i(TAG,"autoNotifyDevices");

		handler.removeCallbacks(mautoNotifyDevicesRunnable);
		
		if(mbstarted==true){

			
			handler.postDelayed(mautoNotifyDevicesRunnable,60*1000);
		}
	}

	
}
