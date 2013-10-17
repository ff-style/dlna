package com.talent.allshare.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.talent.allshare.widget.IOSSwitch;
import com.youplayer.player.R;




public class ShareActivity extends Activity implements View.OnClickListener {
	

      private boolean mShareEnabled=false;
      private boolean mShareMusicEnabled=true;
      private boolean mShareVideoEnabled=true;
      private boolean mSharePictureEnabled=true;	  
      private boolean mShareDocEnabled=true;	  
      private String    mDeviceName="MacroDisk";	  

//      private Switch mShareSwitch;
      private CheckBox mShareVideoCheckBox;	
      private CheckBox mShareMusicCheckBox;	
      private CheckBox mSharePictureCheckBox;	
      private CheckBox mShareDocCheckBox;	
      private EditText mShareNameEditText;	

	private final static String LOGTAG = "ShareMe";  


	private IShareService mService;
	private IOSSwitch mShareSwitch_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.share);
		
		initView();
		initDate();

		startService(new Intent(this,ShareService.class));

		final Intent intent = new Intent(this,ShareService.class);
		getApplicationContext().bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
		
	}




	@Override
	protected void onDestroy() {

		if(mService!=null){
			
			getApplicationContext().unbindService(serviceConnection);
		}
		
		super.onDestroy();

		
		
	}


	private void initView() {
		//add by ff
		mShareSwitch_btn = (IOSSwitch)findViewById(R.id.switch_openshare_btn);
		mShareSwitch_btn.setChecked(false);
		mShareSwitch_btn.setFocusable(true);
		mShareSwitch_btn.requestFocus();
		
//		EditText btn =(EditText) findViewById(R.id.btn);
//		btn.requestFocus();
//		mShareSwitch = (Switch) findViewById(R.id.switch_openshare);
		mShareVideoCheckBox = (CheckBox) findViewById(R.id.checkbox_video);
		mShareMusicCheckBox = (CheckBox) findViewById(R.id.checkbox_music);
		mSharePictureCheckBox = (CheckBox) findViewById(R.id.checkbox_picture);
		mShareDocCheckBox = (CheckBox) findViewById(R.id.checkbox_doc);
		mShareNameEditText = (EditText) findViewById(R.id.edittext_devicesname);
		mShareVideoCheckBox.setOnClickListener(this);
		mShareMusicCheckBox.setOnClickListener(this);
		mSharePictureCheckBox.setOnClickListener(this);
		mShareDocCheckBox.setOnClickListener(this);
	}

	private void initDate() {
		
	
		 mShareNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name=mShareNameEditText.getText().toString();
				if(name!=null) {
					mDeviceName=name;
				}
            }
        });
		 
	}

	
	@Override
		public void onClick(View v) {

			switch (v.getId()) {
				case R.id.checkbox_video:
					mShareVideoEnabled=mShareVideoCheckBox.isChecked();
					break;
				case R.id.checkbox_music:
					mShareMusicEnabled=mShareMusicCheckBox.isChecked();
					break;
				case R.id.checkbox_picture:
					mSharePictureEnabled=mSharePictureCheckBox.isChecked();
					break;
				case R.id.checkbox_doc:
					mShareDocEnabled=mShareDocCheckBox.isChecked();
					break;
				
			}
		}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		
			public void onServiceConnected(ComponentName className, IBinder service) {
				mService =  IShareService.Stub.asInterface(service);
				Log.i(LOGTAG, "Connected to share Service");
				try {
					mDeviceName=mService.getCurDevName();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				
				if(mShareNameEditText!=null) {
					mShareNameEditText.setText(mDeviceName);
				}

				try {
					mShareVideoEnabled=mService.getbsharevideo();
					mShareVideoCheckBox.setChecked(mShareVideoEnabled);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				try {
					mShareMusicEnabled=mService.getbsharemusic();
					mShareMusicCheckBox.setChecked(mShareMusicEnabled);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}


				try {
					mSharePictureEnabled=mService.getbsharepicture();
					mSharePictureCheckBox.setChecked(mSharePictureEnabled);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				try {
					mShareDocEnabled=mService.getbsharedoc();
					mShareDocCheckBox.setChecked(mShareDocEnabled);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				if(mShareSwitch_btn!=null) { 
//					mShareSwitch.setEnabled(true);
					mShareSwitch_btn.setEnabled(true);
					try {
						if(mService.isStarted()) {
							mShareSwitch_btn.setChecked(true);
							mShareSwitch_btn.setEnabled(true);
							mShareEnabled=true;
							enablesharesettingview(false);
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
					mShareSwitch_btn.setOnSwitchChangeListener(new IOSSwitch.OnSwitchChangeListener() {
				        @Override
				        public void onSwitchChanged(IOSSwitch switcher, boolean isChecked) {  
				                // TODO Auto-generated method stub  
						 Log.i("LOGTAG", "mShareEnabled="+mShareEnabled);
						 if(isChecked) {
							if(mService!=null) {
							  mShareSwitch_btn.setChecked(isChecked);  
				              mShareEnabled=isChecked;
				              try {
							  		String name=mShareNameEditText.getText().toString();
									if(name!=null) {
										mDeviceName=name;
									}
				            	  mService.updateconfig(mDeviceName,mShareVideoEnabled,mShareMusicEnabled,mSharePictureEnabled,mShareDocEnabled);
								  mService.start();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
				              
							  
							  enablesharesettingview(false);
							}
						 }else {
						 	if(mService!=null){
						 		try {
						 			mService.stop();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
								
								enablesharesettingview(true);
						 	}
						 }
		       			}
				    });

					/*

					mShareSwitch_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
	              
				            @Override  
				            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
				                // TODO Auto-generated method stub  
						 Log.i("LOGTAG", "mShareEnabled="+mShareEnabled);
						 if(isChecked) {
							if(mService!=null) {
							  mShareSwitch_btn.setChecked(isChecked);  
				              mShareEnabled=isChecked;
				              try {
							  		String name=mShareNameEditText.getText().toString();
									if(name!=null) {
										mDeviceName=name;
									}
				            	  mService.updateconfig(mDeviceName,mShareVideoEnabled,mShareMusicEnabled,mSharePictureEnabled,mShareDocEnabled);
								  mService.start();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
				              
							  
							  enablesharesettingview(false);
							}
						 }else {
						 	if(mService!=null){
						 		try {
						 			mService.stop();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
								
								enablesharesettingview(true);
						 	}
						 }
		       			}  
	        		});  */
				}
			}
		
			public void onServiceDisconnected(ComponentName className) {
				mService = null;
			}
		};

   void enablesharesettingview(boolean enalbe){

		if(mShareVideoCheckBox!=null) {

			
			mShareVideoCheckBox.setEnabled(enalbe);
			mShareMusicCheckBox.setEnabled(enalbe);
			mSharePictureCheckBox.setEnabled(enalbe);
			mShareDocCheckBox.setEnabled(enalbe);
			mShareNameEditText.setEnabled(enalbe);
		}
   }
		@Override
		public void onBackPressed() {
			new AlertDialog.Builder(ShareActivity.this).setTitle("确定退出？")
			.setNegativeButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					ShareActivity.this.finish();
				}
			})
			.setNeutralButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
			
		}
}
