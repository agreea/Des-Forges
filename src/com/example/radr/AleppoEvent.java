package com.example.radr;

import com.example.radr.backend.network.Request;
import com.example.radr.backend.network.Message.Event;

public class AleppoEvent {
	private final int ID;
	private final long timestamp;
	private final AleppoLocation location;
	private final String caption;
	private final String media_id;
	private final long stime;
	private final long etime;

	public AleppoEvent(int id, long timestamp, AleppoLocation location, String caption, String media_id, long stime, long etime){
		this.ID = id;
		this.timestamp = timestamp;
		this.location = location;
		this.caption = caption;
		this.media_id = media_id;
		this.stime = stime;
		this.etime = etime;
	}

	public int getID(){
		return ID;
	}

	public long getTimestamp(){
		return timestamp;
	}

	public AleppoLocation getLocation(){
		return location;
	}

	public double getLat(){
		return location.getLat();
	}

	public double getLon(){
		return location.getLon();
	}

	public String caption(){
		return caption;
	}

	public String getMediaId(){
		return media_id;
	}

	public String getMediaUrl(){
		return Request.getMediaUrl(media_id);
	}
	
	public long getStartTime(){
		return stime;
	}

	public long getEndTime(){
		return etime;
	}
	
	public Event toEvent() {
		return toEvent(false, false);
	}
	
	public Event toEvent(boolean delete, boolean update) {
		Event e = new Event();
		e.eventId = ID;
		e.delete = delete;
		e.update = update;
		e.latitude = (float) location.getLat();
		e.longitude = (float) location.getLon();
		e.timestamp = timestamp;
		e.sTime = stime;
		e.eTime = etime;
		e.caption = caption;
		e.mediaId = media_id;
		return e;
	}
}
