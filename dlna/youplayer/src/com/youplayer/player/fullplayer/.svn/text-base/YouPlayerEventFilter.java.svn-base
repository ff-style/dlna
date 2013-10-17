package com.youplayer.player.fullplayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.youplayer.util.LOG;

public abstract class YouPlayerEventFilter {

	public static final int EVENT_ALL = -1;
	List<Integer> filters = new Vector();
	HashMap<Integer,Integer> unfilters = new HashMap();

	public  void addFilter(int filter_event,int unfilter_event){
		if( !filters.contains(filter_event) ){
			filters.add(filter_event);
			unfilters.put(unfilter_event, filter_event);
		}
	}

	public void removeAll(){
		filters.clear();
		for (TimerData data :timer_policys.values()) {
			if( data.task != null ){
				data.task.cancel();
				data.task = null;
			}
		}
	}


	public final boolean filter(int eventid){
		
		if( filters.contains(EVENT_ALL)||  filters.contains(eventid) ){
			LOG.v("PlayerAdapter", "EventFilter", "filter event id:"+eventid);
			return true;
		}
		
		if( unfilters.containsKey(eventid) ){
			filters.remove(unfilters.get(eventid));
			unfilters.remove(eventid);
			//return false;
		}
		
		if( policies.containsKey(eventid) ){
				
				String filterstr = policies.get(eventid);
				
				LOG.v("PlayerAdapter", "EventFilter", "contains policy:"+eventid+"_"+filterstr);
				int fe = Integer.parseInt( filterstr.substring(0,filterstr.indexOf('_')) );
				int ufe = Integer.parseInt( filterstr.substring(filterstr.indexOf('_')+1) );
				addFilter(fe,ufe);
		}

		for (TimerData data :timer_policys.values()) {
			if( data.isCancelEvent(eventid) ){
				LOG.v("PlayerAdapter", "EventFilter", "cancel timer:" + eventid);
				cancelTimer(data.on_event);
			}
		}
		
		if( timer_policys.containsKey(eventid) ){
			LOG.v("PlayerAdapter", "EventFilter", "create timer id:" + eventid);
			final TimerData td = timer_policys.get(eventid);
			TimerTask task = new TimerTask(){
				@Override
				public void run() {
					synchronized (timer_policys) {
						if( !filter( td.event ) )
							sendEvent(td);
					}
				}
			};
			timer.schedule(task,td.delay);
			td.task = task;
		}
		
		return false;
	}
	
	
	HashMap<Integer,String> policies = new HashMap();

	public void addPolicy(int on_event,int filter_event,int unfilter_event){
		policies.put(on_event,filter_event+"_"+unfilter_event);
	}
	
	Timer timer = new Timer();
	HashMap<Integer,TimerData> timer_policys = new HashMap();
	
	public void addTimerPolicy(final int on_event,long delay,final int send_event,final Object send_arg,int[] cancel_events ){
	    Arrays.sort(cancel_events);
	    TimerData td = new TimerData(on_event,delay,send_event,send_arg,cancel_events);
		timer_policys.put(on_event,td);
	}
	
	public abstract void sendEvent(TimerData data);
	
	public void cancelTimer(int on_event){
		synchronized (timer_policys) {
			if( timer_policys.containsKey(on_event) ){
			    if(timer_policys.get(on_event).task != null){
			    	LOG.v("PlayerAdapter", "EventFilter", "cancelTimer task:" + on_event);
			        timer_policys.get(on_event).task.cancel();
			        timer_policys.get(on_event).task = null;
			    }
			}
		}
	}
	public void cancelTimer(){
		timer.cancel();
	}
}

class TimerData{
	int on_event;
	long delay;
	int event;
	Object arg;
	int[] cancel_events;
	TimerTask task;
	
	public TimerData(int on_event,long delay,int event,Object arg,int[] cancel_events){
		this.on_event = on_event;
		this.delay = delay;
		this.event = event;
		this.arg = arg;
		this.cancel_events = cancel_events;
	}
	
	public boolean isCancelEvent(int event){
		return (cancel_events != null 
		        && cancel_events.length > 0 
		        &&( cancel_events[0] == YouPlayerEventFilter.EVENT_ALL || (java.util.Arrays.binarySearch(cancel_events,event) > -1)));
	}
}



