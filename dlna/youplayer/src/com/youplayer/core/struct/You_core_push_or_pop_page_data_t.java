package com.youplayer.core.struct;

public class You_core_push_or_pop_page_data_t {
	
	public You_core_push_or_pop_page_data_t(int page_type,boolean animation,boolean isRoot,int from_which_page){
		this.page_type = page_type;
		this.animation = animation;
		this.isRoot = isRoot;
		this.from_which_page = from_which_page;
	}
	
	public You_core_push_or_pop_page_data_t(){
		
	}
	public int page_type;
	public boolean animation;
	public boolean isRoot;
	public int from_which_page;
	
}
