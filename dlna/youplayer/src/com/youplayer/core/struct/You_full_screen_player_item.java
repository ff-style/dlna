package com.youplayer.core.struct;

public class You_full_screen_player_item {

	public static class Cls_fn_full_player_data_frag{
        public long frag_duration;
        public String url;
	}
	
	public String pic;
	public String src;
	public String favtype;
	public String wb_url;
	
    public String next_url;
    public String priv_url;
    public Cls_fn_full_player_data_frag[] frag_list;
	
    public int hd;
    public long fraglist_duration;
    public int fraglist_cnt;
    public boolean ds;//live media type
    public  int quality;

    public boolean isEpisode;
    public int play_time;
    
	public String url;
	public String name;
	
    public String sub_url;
    public Object local_array;
    public You_local_media_click_cell_t cell;
    
    public boolean is_from_external;
	
    public int width;
    public int height;
    
    public boolean can_direct_play;
    
}
