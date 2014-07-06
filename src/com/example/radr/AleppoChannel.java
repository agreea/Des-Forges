package com.example.radr;

import com.example.radr.backend.network.Message.Channel;

public class AleppoChannel {
	private final int id;
	private final String name;
	private final String description;
	private final int subscribers;

	public AleppoChannel(int id, String name, String description, int subscribers){
		this.id = id;
		this.name = name;
		this.description = description;
		this.subscribers = subscribers;
	}

	public int getID(){
		return id;
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public int getSubscribers(){
		return subscribers;
	}

	public Channel toChannel() {
		return toChannel(false, false);
	}
	
	public Channel toChannel(boolean delete, boolean update) {
		Channel c = new Channel();
		c.channelId = id;
		c.delete = delete;
		c.update = update;
		c.name = name;
		c.description = description;
		return c;
	}
}
