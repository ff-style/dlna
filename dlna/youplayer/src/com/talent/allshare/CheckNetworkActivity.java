package com.talent.allshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.youplayer.player.R;

public class CheckNetworkActivity extends Activity implements OnClickListener {

	private View btn_1;
	private Intent intent;
	private Button check_network_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_network);
		check_network_btn = (Button) findViewById(R.id.check_network_btn);
		check_network_btn.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (checkNetWork()) {
			intent = new Intent(CheckNetworkActivity.this,
					AllShareActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(CheckNetworkActivity.this,
					getResources().getString(R.string.reconnect_wifi), Toast.LENGTH_SHORT).show();
			check_network_btn.setVisibility(View.VISIBLE);
		}

	}

	private boolean checkNetWork() {
		  ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	        //wifi Network
	        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
	        
	        //如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
	        if(wifi==State.CONNECTED||wifi==State.CONNECTING){
	        	return true;
	        }
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.check_network_btn:
			startActivity(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS));
			break;

		default:
			break;
		}

	}

}