package com.youplayer.player;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.youplayer.player.YouExplorer;
import com.youplayer.player.R;


public class TestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        final EditText text = (EditText) this.findViewById(R.id.editText1);
        
        Button button = (Button) this.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//act=android.intent.action.VIEW dat=file:///mnt/sdcard/1.mp4 typ=video/mp4 flg=0x13800000
		        Intent intent = new Intent(TestActivity.this, YouExplorer.class);
		        intent.setAction("android.intent.action.VIEW");
		        Log.w("zhao", "length="+text.getText().length());
		        if(text.getText().length() <= 0)
		        	intent.setData(Uri.parse("http://mov.bn.netease.com/movieMP4/2012/3/P/J/S7QLI3MPJ.mp4"));
		        else
		        	intent.setData(Uri.parse(text.getText().toString()));

		        // intent.setData("file:///mnt/sdcard/1.mp4");
		        TestActivity.this.startActivity(intent);
		        }
		});
        
        }
}