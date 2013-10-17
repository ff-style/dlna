package com.talent.allshare.softapplication;

import java.util.ArrayList;
import java.util.List;

import com.talent.allshare.bean.PlaylistBean;

import android.app.Application;

public class SoftApplication extends Application{
	private List<PlaylistBean> musicPlaylists = new ArrayList<PlaylistBean>();
	
	private static SoftApplication softApplication;
	public static SoftApplication getInstance(){
		if(softApplication == null){
			softApplication = new SoftApplication();
		}
		return softApplication;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public List<PlaylistBean> getMusicPlaylists() {
		return musicPlaylists;
	}

	public void setMusicPlaylists(List<PlaylistBean> musicPlaylists) {
		this.musicPlaylists = musicPlaylists;
	}
	
}
