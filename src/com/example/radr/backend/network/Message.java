package com.example.radr.backend.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Message {
	private static final int DEFAULT_INDENT = 4;
	
	private static abstract class AbstractMessage {
		
		public abstract JSONObject toJSON() throws JSONException;
		
		public String toJSONString() throws JSONException {
			return toJSON().toString();
		}
		
		public String toJSONStringPretty() throws JSONException {
			return toJSONStringPretty(DEFAULT_INDENT);
		}
		
		private String toJSONStringPretty(int indent) throws JSONException {
			return toJSON().toString(indent);
		}
	}
	
	public static class Response extends AbstractMessage {
		public boolean invalidateAll = false;
		public String timestamp = "";
		public Post[] posts;
		public Event[] events;
		public Channel[] channels;
		public PostChannel[] postChannels;
		
		public static final String invalidateAllName = "Invalidate_all";
		public static final String timestampName = "Timestamp";
		public static final String postsName = "Posts";
		public static final String eventsName = "Events";
		public static final String channelsName = "Channels";
		public static final String postChannelsName = "Post_channels";
		
		public Response() {
			posts = new Post[0];
			events = new Event[0];
			channels = new Channel[0];
			postChannels = new PostChannel[0];
		}
		
		public boolean getInvalidateAll() { return invalidateAll; }
		public String getTimestamp() { return timestamp; }
		public Post[] getPosts() { return posts; }
		public Event[] getEvents() { return events; }
		public Channel[] getChannels() { return channels; }
		public PostChannel[] getPostChannels() { return postChannels; }
		
		
		public static Response fromJSONString(String json) throws JSONException {
			return fromJSON(new JSONObject(json));
		}
		
		public static Response fromJSON(JSONObject json) throws JSONException {
			Response r = new Response();
			r.invalidateAll = json.getBoolean(invalidateAllName);
			r.timestamp = json.getString(timestampName);
			
			JSONArray arr = json.getJSONArray(postsName);
			r.posts = new Post[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				r.posts[i] = Post.fromJSON(arr.getJSONObject(i));
			}
			
			arr = json.getJSONArray(eventsName);
			r.events = new Event[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				r.events[i] = Event.fromJSON(arr.getJSONObject(i));
			}
			
			arr = json.getJSONArray(channelsName);
			r.channels = new Channel[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				r.channels[i] = Channel.fromJSON(arr.getJSONObject(i));
			}
			
			arr = json.getJSONArray(postChannelsName);
			r.postChannels = new PostChannel[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				r.postChannels[i] = PostChannel.fromJSON(arr.getJSONObject(i));
			}
			
			return r;
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(invalidateAllName, invalidateAll);
			json.put(timestampName, timestamp);
			
			JSONArray arr = new JSONArray();
			if (this.posts != null) {
				for (Post p : this.posts) {
					arr.put(p.toJSON());
				}
			}
			json.put(postsName, arr);
			
			arr = new JSONArray();
			if (this.events != null) {
				for (Event e : this.events) {
					arr.put(e.toJSON());
				}
			}
			json.put(eventsName, arr);
			
			arr = new JSONArray();
			if (this.channels != null) {
				for (Channel c : this.channels) {
					arr.put(c.toJSON());
				}
			}
			json.put(channelsName, arr);
			
			arr = new JSONArray();
			if (this.postChannels != null) {
				for (PostChannel pc : this.postChannels) {
					arr.put(pc.toJSON());
				}
			}
			json.put(postChannelsName, arr);
			return json;
		}
	}
	
	public static class Post extends AbstractMessage {
		public int postId;
		public boolean delete = false;
		public boolean update = false;
		public long timestamp;
		public float latitude;
		public float longitude;
		public String caption;
		public String mediaId;
		
		public static final String postIdName = "Post_id";
		public static final String deleteName = "Delete";
		public static final String updateName = "Update";
		public static final String timestampName = "Timestamp";
		public static final String latitudeName = "Latitude";
		public static final String longitudeName = "Longitude";
		public static final String captionName = "Caption";
		public static final String mediaIdName = "Media_id";
		
		public int getPostId() { return postId; }
		public boolean getDelete() { return delete; }
		public boolean getUpdate() { return update; }
		public long getTimestamp() { return timestamp; }
		public float getLatitude() { return latitude; }
		public float getLongitude() { return longitude; }
		public String getCaption() { return caption; }
		public String getMediaId() { return mediaId; }
		
		public static Post fromJSONString(String json) throws JSONException {
			return fromJSON(new JSONObject(json));
		}
		
		public static Post fromJSON(JSONObject json) throws JSONException {
			Post p = new Post();
			p.postId = json.getInt(postIdName);
			p.delete = json.getBoolean(deleteName);
			p.update = json.getBoolean(updateName);
			p.timestamp = json.getLong(timestampName);
			p.latitude = (float)json.getDouble(latitudeName);
			p.longitude = (float)json.getDouble(longitudeName);
			p.caption = json.getString(captionName);
			p.mediaId = json.getString(mediaIdName);
			return p;
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(postIdName, postId);
			json.put(deleteName, delete);
			json.put(updateName, update);
			json.put(timestampName, timestamp);
			json.put(latitudeName, latitude);
			json.put(longitudeName, longitude);
			json.put(captionName, caption);
			json.put(mediaIdName, mediaId);
			return json;
		}
	}
	
	public static class Event extends AbstractMessage {
		public int eventId;
		public boolean delete = false;
		public boolean update = false;
		public long timestamp;
		public float latitude;
		public float longitude;
		public String caption;
		public String mediaId;
		public long sTime;
		public long eTime;
		
		public static final String eventIdName = "Event_id";
		public static final String deleteName = "Delete";
		public static final String updateName = "Update";
		public static final String timestampName = "Timestamp";
		public static final String latitudeName = "Latitude";
		public static final String longitudeName = "Longitude";
		public static final String captionName = "Caption";
		public static final String mediaIdName = "Media_id";
		public static final String sTimeName = "Start";
		public static final String eTimeName = "End";
		
		public int getEventId() { return eventId; }
		public boolean getDelete() { return delete; }
		public boolean getUpdate() { return update; }
		public long getTimestamp() { return timestamp; }
		public float getLatitude() { return latitude; }
		public float getLongitude() { return longitude; }
		public String getCaption() { return caption; }
		public String getMediaId() { return mediaId; }
		public long getStartTime() { return sTime; }
		public long getEndTime() { return eTime; }
		
		public static Event fromJSONString(String json) throws JSONException {
			return fromJSON(new JSONObject(json));
		}
		
		public static Event fromJSON(JSONObject json) throws JSONException {
			Event e = new Event();
			e.eventId = json.getInt(eventIdName);
			e.delete = json.getBoolean(deleteName);
			e.update = json.getBoolean(updateName);
			e.timestamp = json.getLong(timestampName);
			e.latitude = (float)json.getDouble(latitudeName);
			e.longitude = (float)json.getDouble(longitudeName);
			e.caption = json.getString(captionName);
			e.mediaId = json.getString(mediaIdName);
			e.sTime = json.getLong(sTimeName);
			e.eTime = json.getLong(eTimeName);
			return e;
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(eventIdName, eventId);
			json.put(deleteName, delete);
			json.put(updateName, update);
			json.put(timestampName, timestamp);
			json.put(latitudeName, latitude);
			json.put(longitudeName, longitude);
			json.put(captionName, caption);
			json.put(mediaIdName, mediaId);
			json.put(sTimeName, sTime);
			json.put(eTimeName, eTime);
			return json;
		}
	}
	
	public static class Channel extends AbstractMessage {
		public int channelId;
		public boolean delete = false;
		public boolean update = false;
		public String name;
		public String description;
		
		public static final String channelIdName = "Channel_id";
		public static final String deleteName = "Delete";
		public static final String updateName = "Update";
		public static final String nameName = "Name";
		public static final String descriptionName = "Description";
		
		public int getChannelId() { return channelId; }
		public boolean getDelete() { return delete; }
		public boolean getUpdate() { return update; }
		public String getName() { return name; }
		public String getDescription() { return description; }
		
		public static Channel fromJSONString(String json) throws JSONException {
			return fromJSON(new JSONObject(json));
		}
		
		public static Channel fromJSON(JSONObject json) throws JSONException {
			Channel c = new Channel();
			c.channelId = json.getInt(channelIdName);
			c.delete = json.getBoolean(deleteName);
			c.update = json.getBoolean(updateName);
			c.name = json.getString(nameName);
			c.description = json.getString(descriptionName);
			return c;
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(channelIdName, channelId);
			json.put(deleteName, delete);
			json.put(updateName, update);
			json.put(nameName, name);
			json.put(descriptionName, description);
			return json;
		}
	}
	
	public static class PostChannel extends AbstractMessage {
		public boolean delete = false;
		public boolean update = false;
		public int postId;
		public int channelId;
		public int ups;
		public int downs;
		
		public static final String deleteName = "Delete";
		public static final String updateName = "Update";
		public static final String postIdName = "Post_id";
		public static final String channelIdName = "Channel_id";
		public static final String upsName = "Ups";
		public static final String downsName = "Downs";
		
		public boolean getDelete() { return delete; }
		public boolean getUpdate() { return update; }
		public int getPostId() { return postId; }
		public int getChannelId() { return channelId; }
		public int getUps() { return ups; }
		public int getDowns() { return downs; }
		
		public static PostChannel fromJSONString(String json) throws JSONException {
			return fromJSON(new JSONObject(json));
		}
		
		public static PostChannel fromJSON(JSONObject json) throws JSONException {
			PostChannel pc = new PostChannel();
			pc.delete = json.getBoolean(deleteName);
			pc.update = json.getBoolean(updateName);
			pc.postId = json.getInt(postIdName);
			pc.channelId = json.getInt(channelIdName);
			pc.ups = json.getInt(upsName);
			pc.downs = json.getInt(downsName);
			return pc;
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(deleteName, delete);
			json.put(updateName, update);
			json.put(postIdName, postId);
			json.put(channelIdName, channelId);
			json.put(upsName, ups);
			json.put(downsName, downs);
			return json;
		}
	}
}