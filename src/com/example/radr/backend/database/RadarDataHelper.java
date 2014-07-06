package com.example.radr.backend.database;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.example.radr.AleppoChannel;
import com.example.radr.AleppoEvent;
import com.example.radr.AleppoPost;
import com.example.radr.backend.network.Message;
import com.example.radr.util.LogTags;

public class RadarDataHelper {

	private final RadarDatabase db;

	public RadarDataHelper(Context c){
		db = new RadarDatabase(c);
	}

	public List<AleppoEvent> getRankedEvents(int numEvents){
		return db.getRankedEvents(numEvents);
	}

	public List<AleppoPost> getRankedFeedPosts(int numPosts){
		return db.getRankedPosts(numPosts);
		// return getRankedPostsByChannel(numPosts, getSubscribedChannels());
	}

	public List<AleppoPost> getRankedPostsByChannel(int numPosts, AleppoChannel channel){
		return db.getPostsByChannel(channel.getID(), numPosts);
	}

	public List<AleppoPost> getRankedPostsByChannel(int numPosts, int channelId){
		return db.getPostsByChannel(channelId, numPosts);
	}

	public List<AleppoPost> getRankedPostsByChannel(int numPosts, List<AleppoChannel> channels){
		if(channels.isEmpty()){
			return new LinkedList<AleppoPost>();
		}
		List<Integer> channel_ids = new LinkedList<Integer>();
		for(AleppoChannel channel : channels){
			channel_ids.add(Integer.valueOf(channel.getID()));
		}
		return db.getPostsByChannels(channel_ids, numPosts);
	}
	
	public List<AleppoPost> getRankedPostsByChannel(List<Integer> channels, int numPosts){
		return db.getPostsByChannels(channels, numPosts);
	}

	public List<AleppoChannel> getSubscribedChannels(){
		//TODO where does the subscribed channel list live?
		return new LinkedList<AleppoChannel>();
	}

	public byte[] getMediaByID(String id){
		//TODO where are we keeping media these days?
		return new byte[0];
	}

	//TODO will take a callback of some kind
	public void refresh(){
		//TODO need to link this up with the real deal...
	}

	public void update(Message.Response response){
		db.update(response);
	}

	public String getWorldTime(){
		synchronized (RadarDatabase.TABLE_WORLD_TIME){
			return db.getCurrentWorldTime();
		}
	}
}
