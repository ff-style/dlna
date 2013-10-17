package com.talent.allshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.youplayer.player.R;


public class MainSelectActivity extends Activity implements OnClickListener{
	
	private View btn_1;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_select);
		btn_1 = findViewById(R.id.main_select_btn_1);
		
		btn_1.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.main_select_btn_1:
			intent = new Intent(MainSelectActivity.this,CheckNetworkActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}

	
	
}