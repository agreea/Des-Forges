package com.example.radr;

import com.example.radr.backend.network.Message.Post;
import com.example.radr.backend.network.Request;

public class AleppoPost {
	private final int ID;
	private final long timestamp;
	private final AleppoLocation location;
	private final String caption;
	private final String mediaId;
	private int upvote;
	private int downvote;

	public AleppoPost(int id, 
					  long timestamp, 
					  AleppoLocation location, 
					  String caption, 
					  String mediaId, 
					  int upvote, int downvote){
		this.ID = id;
		this.timestamp = timestamp;
		this.location = location;
		this.caption = caption;
		this.mediaId = mediaId;
		this.upvote = upvote;
		this.downvote = downvote;
	}

	public int id(){
		return ID;
	}

	public long timestamp(){
		return timestamp;
	}
	
	public int upvote(){
		return upvote;
	}
	
	public int downvote(){
		return downvote;
	}

	public AleppoLocation location(){
		return location;
	}

	public double lat(){
		return location.getLat();
	}

	public double lon(){
		return location.getLon();
	}

	public String caption(){
		return caption;
	}

	public String mediaId(){
		return mediaId;
	}
	
	public String mediaUrl() {
		return Request.getMediaUrl(mediaId);
	}
		
	public Post toPost() {
		return toPost(false, false);
	}
	
	public Post toPost(boolean delete, boolean update) {
		Post p = new Post();
		p.postId = ID;
		p.delete = delete;
		p.update = update;
		p.latitude = (float) location.getLat();
		p.longitude = (float) location.getLon();
		p.timestamp = timestamp;
		p.caption = caption;
		p.mediaId = mediaId;
		return p;
	}

	@Override
	public String toString(){
		return "(" + String.valueOf(ID) + " @ " + String.valueOf(timestamp) + " : " + location.toString() + " " + mediaId + " '" + caption + "')";
	}
}
