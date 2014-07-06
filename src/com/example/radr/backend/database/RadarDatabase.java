package com.example.radr.backend.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.radr.AleppoEvent;
import com.example.radr.AleppoLocation;
import com.example.radr.AleppoPost;
import com.example.radr.backend.network.Message;
import com.example.radr.util.LogTags;

public class RadarDatabase extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "radar_content";

	public static final String TABLE_POSTS = "posts";
	public static final String TABLE_EVENTS = "events";
	public static final String TABLE_CHANNELS = "channels";
	public static final String TABLE_POST_CHANNELS = "post_channels";
	public static final String TABLE_WORLD_TIME = "world_time";

	public static final String POSTS_KEY_POST_ID = "post_id";
	public static final String POSTS_KEY_TIMESTAMP = "ts";
	public static final String POSTS_KEY_LAT = "lat";
	public static final String POSTS_KEY_LON = "lon";
	public static final String POSTS_KEY_CAPTION = "caption";
	public static final String POSTS_KEY_VOTES_UP = "ups";
	public static final String POSTS_KEY_VOTES_DOWN = "downs";
	public static final String POSTS_KEY_MEDIA = "media_id";

	public static final String EVENTS_KEY_EVENT_ID = "event_id";
	public static final String EVENTS_KEY_TIMESTAMP = "ts";
	public static final String EVENTS_KEY_LAT = "lat";
	public static final String EVENTS_KEY_LON = "lon";
	public static final String EVENTS_KEY_CAPTION = "caption";
	public static final String EVENTS_KEY_MEDIA = "media_id";
	public static final String EVENTS_KEY_TIME_START = "stime";
	public static final String EVENTS_KEY_TIME_END = "etime";

	public static final String CHANNELS_KEY_CHANNEL_ID = "channel_id";
	public static final String CHANNELS_KEY_NAME = "name";
	public static final String CHANNELS_KEY_DESCRIPTION = "description";
	public static final String CHANNELS_KEY_SUBSCRIBER_COUNT = "subscribers";

	public static final String POST_CHANNELS_KEY_POST_ID = "post_id";
	public static final String POST_CHANNELS_KEY_CHANNEL_ID = "channel_id";
	public static final String POST_CHANNELS_KEY_VOTES_UP = "ups";
	public static final String POST_CHANNELS_KEY_VOTES_DOWN = "downs";

	public static final String WORLD_TIME_KEY_TIME = "time";

	public static final String ACTION_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

	public static final String QUERY_POSTS_JOIN = "SELECT " + POSTS_KEY_POST_ID + ", " + POSTS_KEY_TIMESTAMP + ", " + POSTS_KEY_LAT + ", " + POSTS_KEY_LON + ", " + POSTS_KEY_CAPTION + ", " + POSTS_KEY_MEDIA
		+ ", " + TABLE_POSTS + "." + POSTS_KEY_VOTES_UP + ", " + TABLE_POSTS + "." + POSTS_KEY_VOTES_DOWN + ", (" + TABLE_POSTS + "." + POSTS_KEY_VOTES_UP + " - " + TABLE_POSTS + "." + POSTS_KEY_VOTES_DOWN
		+ ") AS score " + " FROM " + TABLE_POST_CHANNELS + " INNER JOIN " + TABLE_POSTS + " USING (" + POSTS_KEY_POST_ID + ") WHERE " + POST_CHANNELS_KEY_CHANNEL_ID + "=?";
	public static final String[] ARGS_POSTS_JOIN = new String[] { };
	public static final String[] EVENTS_COLUMNS = new String[] { EVENTS_KEY_EVENT_ID, EVENTS_KEY_TIMESTAMP, EVENTS_KEY_LAT, EVENTS_KEY_LON, EVENTS_KEY_CAPTION, EVENTS_KEY_MEDIA, EVENTS_KEY_TIME_START, EVENTS_KEY_TIME_END };

	public static final String UNION = " UNION ";

	private Context context;

	public RadarDatabase(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = c;
	}

//	//NOTE: this isn't thredsafe!
//	public static void setContext(Context context){
//		if(RadarDatabase.context != null){
//			Log.w("FIXME", "Overwriting already-initialized RadarDatabase.context");//FIXME: need a real log tag
//		}
//		RadarDatabase.context = context;
//	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_POSTS_TABLE =
			ACTION_CREATE_TABLE + TABLE_POSTS + " ("
			+ POSTS_KEY_POST_ID + " INTEGER PRIMARY KEY, "
			+ POSTS_KEY_TIMESTAMP + " INTEGER NOT NULL, "
			+ POSTS_KEY_LAT + " REAL NOT NULL, "
			+ POSTS_KEY_LON + " REAL NOT NULL, "
			+ POSTS_KEY_CAPTION + " TEXT NOT NULL, "
			+ POSTS_KEY_VOTES_UP + " INTEGER NOT NULL DEFAULT 0 CHECK (" + POSTS_KEY_VOTES_UP + ">=0), "
			+ POSTS_KEY_VOTES_DOWN + " INTEGER NOT NULL DEFAULT 0 CHECK (" + POSTS_KEY_VOTES_DOWN + ">=0), "
			+ POSTS_KEY_MEDIA + " TEXT NOT NULL"
			+ ");";

		String CREATE_EVENTS_TABLE =
			ACTION_CREATE_TABLE + TABLE_EVENTS + " ("
			+ EVENTS_KEY_EVENT_ID + " INTEGER PRIMARY KEY, "
			+ EVENTS_KEY_TIMESTAMP + " INTEGER NOT NULL, "
			+ EVENTS_KEY_LAT + " REAL NOT NULL, "
			+ EVENTS_KEY_LON + " REAL NOT NULL, "
			+ EVENTS_KEY_CAPTION + " TEXT NOT NULL, "
			+ EVENTS_KEY_MEDIA + " TEXT NOT NULL, "
			+ EVENTS_KEY_TIME_START + " INTEGER NOT NULL, "
			+ EVENTS_KEY_TIME_END + " INTEGER NOT NULL"
			+ ");";

		String CREATE_CHANNELS_TABLE =
			ACTION_CREATE_TABLE + TABLE_CHANNELS + " ("
			+ CHANNELS_KEY_CHANNEL_ID + " INTEGER PRIMARY KEY, "
			+ CHANNELS_KEY_NAME + " TEXT NOT NULL, "
			+ CHANNELS_KEY_DESCRIPTION + " TEXT NOT NULL, "
			+ CHANNELS_KEY_SUBSCRIBER_COUNT + " INTEGER NOT NULL DEFAULT 0"
			+ ");";

		String CREATE_POST_CHANNELS_TABLE =
			ACTION_CREATE_TABLE + TABLE_POST_CHANNELS + " ("
			+ POST_CHANNELS_KEY_POST_ID + " INTEGER NOT NULL REFERENCES " + TABLE_POSTS + " ON UPDATE RESTRICT ON DELETE CASCADE, "
			+ POST_CHANNELS_KEY_CHANNEL_ID + " INTEGER NOT NULL REFERENCES " + TABLE_CHANNELS + " ON UPDATE RESTRICT ON DELETE CASCADE, "
			+ POST_CHANNELS_KEY_VOTES_UP + " INTEGER NOT NULL CHECK (" + POST_CHANNELS_KEY_VOTES_UP + ">=0), "
			+ POST_CHANNELS_KEY_VOTES_DOWN + " INTEGER NOT NULL CHECK (" + POST_CHANNELS_KEY_VOTES_DOWN + ">=0),"
			+ "PRIMARY KEY (" + POST_CHANNELS_KEY_POST_ID + ", " + POST_CHANNELS_KEY_CHANNEL_ID + ")"
			+ ");";

		String CREATE_WORLD_TIME_TABLE =
			ACTION_CREATE_TABLE + TABLE_WORLD_TIME + " ("
			+ WORLD_TIME_KEY_TIME + " INTEGER PRIMARY KEY"
			+ ");";

		db.execSQL(CREATE_POSTS_TABLE);
		db.execSQL(CREATE_EVENTS_TABLE);
		db.execSQL(CREATE_CHANNELS_TABLE);
		db.execSQL(CREATE_POST_CHANNELS_TABLE);
		db.execSQL(CREATE_WORLD_TIME_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// just ditch any existing information and recreate
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
		onCreate(db);
	}

	public List<AleppoPost> getPostsByChannel(int channelId, int numPosts){
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(channelId);
		return getPostsByChannels(ids, numPosts);
	}

	public List<AleppoPost> getRankedPosts(int numPosts) {
		String query = "SELECT * FROM " + TABLE_POSTS + " LIMIT " + numPosts;
		Cursor result;
		List<AleppoPost> posts = new LinkedList<AleppoPost>();
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			try {
				db.beginTransaction();
				// If this NPE's, change null to "new String[0]"
				result = db.rawQuery(query, new String[0]);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			posts = listofPosts(result);
		} finally {
			//db.close();
		}

		return posts;
	}
	
	public List<AleppoPost> getPostsByChannels(List<Integer> channels, int numPosts){
		String[] channel_ids = new String[channels.size()];
		for (int i = 0; i < channel_ids.length; i++){
			channel_ids[i] = String.valueOf(channels.get(i));
		}

		StringBuilder query = new StringBuilder();
		String queryString;
		int numChannels = channel_ids.length;
		String[] queryArgs = new String[(ARGS_POSTS_JOIN.length + 1) * numChannels];
		for (int i = 0; i < numChannels - 1; i++){
			query.append(QUERY_POSTS_JOIN);
			query.append(UNION);
			System.arraycopy(ARGS_POSTS_JOIN, 0, queryArgs, (ARGS_POSTS_JOIN.length + 1) * i, ARGS_POSTS_JOIN.length);
			queryArgs[(ARGS_POSTS_JOIN.length + 1) * i + ARGS_POSTS_JOIN.length] = channel_ids[i];
		}
		query.append(QUERY_POSTS_JOIN);
		query.append(" ORDER BY score");
		query.append(" LIMIT " + String.valueOf(numPosts));
		System.arraycopy(ARGS_POSTS_JOIN, 0, queryArgs, (queryArgs.length - 1) - ARGS_POSTS_JOIN.length, ARGS_POSTS_JOIN.length);
		queryArgs[queryArgs.length - 1] = channel_ids[channel_ids.length - 1];
		
		queryString = query.toString();

		Cursor result;
		
		List<AleppoPost> posts = new LinkedList<AleppoPost>();

		SQLiteDatabase db = this.getReadableDatabase();
		try {
			try {
				db.beginTransaction();
				result = db.rawQuery(queryString, queryArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			posts = listofPosts(result);
		} finally {
			//db.close();
		}

		return posts;
	}

	public List<AleppoEvent> getRankedEvents(int numEvents){
		Cursor result;
		List<AleppoEvent> events = new LinkedList<AleppoEvent>();
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			try {
			db.beginTransaction();
			result = db.query(TABLE_EVENTS, EVENTS_COLUMNS, null, null, null, null, EVENTS_KEY_TIMESTAMP, String.valueOf(numEvents));
			db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			events = listofEvents(result);
		} finally {
			//db.close();
		}
		return events;
	}

	public void update(Message.Response update){

		if(setWorldTime(update.getTimestamp())){
			return;
		}

		if(update.getInvalidateAll()){
			invalidateAll();
		}

		for(Message.Post post : update.getPosts()){
			upserletePost(post);
		}

		for(Message.Event event : update.getEvents()){
			upserleteEvent(event);
		}

		for(Message.Channel channel : update.getChannels()){
			upserleteChannel(channel);
		}

		for(Message.PostChannel postChannel : update.getPostChannels()){
			upserletePostChannel(postChannel);
		}
	}

	private boolean setWorldTime(String time){
		synchronized (TABLE_WORLD_TIME){
			long currentTime = Long.parseLong(getCurrentWorldTime());
			long newTime = Long.parseLong(time);
			if(newTime > currentTime){
				Log.d("FIXME", "Updating world time from " + String.valueOf(currentTime) + " to " + String.valueOf(newTime));//FIXME
				storeWorldTime(time);
				return false;
			}
			Log.w("FIXME", "Got outdated update. Message time is " + String.valueOf(newTime) + " but local time is already " + String.valueOf(currentTime));//FIXME
			return true;
		}
	}

	public String getCurrentWorldTime(){
		SQLiteDatabase db = getReadableDatabase();
		try {
			Cursor results;
			try {
				db.beginTransaction();
				results = db.query(TABLE_WORLD_TIME, new String[] { WORLD_TIME_KEY_TIME }, null, null, null, null, WORLD_TIME_KEY_TIME + " DESC", null);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			int numResults = results.getCount();

			if(numResults == 0){
				Log.w("FIXME", "No timestamp stored, defaulting to zero!");//FIXME
				return String.valueOf(0);
			}

			int timeCol = results.getColumnIndex(WORLD_TIME_KEY_TIME);
			results.moveToFirst();
			String time = results.getString(timeCol);

			if(numResults > 1){
				Log.e("FIXME", "Multiple timestamps stored, taking the latest! Expected one timestamp, but found " + String.valueOf(numResults));//FIXME
			} else {
				Log.d("FIXME", "Found timestamp normally, returning it: " + time);//FIXME
			}
			return time;
		} finally {
			//db.close();
		}
	}

	private void storeWorldTime(String time){
		ContentValues values = new ContentValues();
		values.put(WORLD_TIME_KEY_TIME, Long.parseLong(time));
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			int numUpdated;
			try {
				db.beginTransaction();
				numUpdated = db.update(TABLE_WORLD_TIME, values, null, null);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			if(numUpdated == 0){
				//no timestamp in place, need to insert one
				try {
					db.beginTransaction();
					if(db.insert(TABLE_WORLD_TIME, null, values) != -1){
						db.setTransactionSuccessful();
						Log.w("FIXME", "No timestamp stored, successfully inserted new timestamp of " + time);//FIXME
					} else {
						Log.e("FIXME", "On update, found no stored timestamp and failed to insert one!");//FIXME
					}
				} finally {
					db.endTransaction();
				}
			} else if(numUpdated == 1){
				Log.d("FIXME", "Sucessfully updated stored timestamp to " + time);//FIXME
			} else {
				Log.e("FIXME", "Multiple timestamps stored, updated them all! Expected just one but found " + String.valueOf(numUpdated));//FIXME
			}
		} finally {
			//db.close();
		}
	}

	private void invalidateAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.delete(TABLE_POSTS, null, null);
			db.delete(TABLE_EVENTS, null, null);
			db.delete(TABLE_CHANNELS, null, null);
			db.delete(TABLE_POST_CHANNELS, null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
//			//db.close();
		}
	}

	private void upserletePost(Message.Post post){
		String postId = String.valueOf(post.getPostId());
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();

			if(post.getDelete()){
				int numDeleted;
				numDeleted = db.delete(TABLE_POSTS, "?=?", new String[] { POSTS_KEY_POST_ID, postId });
				db.setTransactionSuccessful();
				if (numDeleted == 0) {
					Log.w("FIXME", "Attemped to delete post id " + postId + " but no such post was found!");//FIXME need a real log tag
				} else if (numDeleted > 1) {
					Log.w("FIXME", "Found duplicate post id " + postId + " on delete. Deleted " + String.valueOf(numDeleted) + " instead of 1!");//FIXME need a real log tag
				}
				return;
			}

			Cursor lookup = db.query(TABLE_POSTS, new String[] { POSTS_KEY_POST_ID }, "?=?", new String[] { POSTS_KEY_POST_ID, postId }, null, null, null);
			int numResults = lookup.getCount();
			ContentValues values = new ContentValues();
			values.put(POSTS_KEY_POST_ID, post.getPostId());
			values.put(POSTS_KEY_TIMESTAMP, post.getTimestamp());
			values.put(POSTS_KEY_LAT, post.getLatitude());
			values.put(POSTS_KEY_LON, post.getLongitude());
			values.put(POSTS_KEY_CAPTION, post.getCaption());
			values.put(POSTS_KEY_MEDIA, post.getMediaId());
//			values.put(POSTS_KEY_VOTES_UP, post.getUps());//TODO Message.Post doesn't have votes yet
//			values.put(POSTS_KEY_VOTES_DOWN, post.getDowns());//TODO Message.Post doesn't have votes yet
			if (numResults == 0){
				db.insert(TABLE_POSTS, null, values);
			} else if (numResults == 1){
				db.update(TABLE_POSTS, values, "?=?", new String[] { POSTS_KEY_POST_ID, postId });
			} else {
				Log.w("FIXME", "Found duplicate post id " + postId + " on update. No update performed.");//FIXME need a real log tag
				return;
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			//db.close();
		}
	}

	private void upserleteEvent(Message.Event event){
		String eventId = String.valueOf(event.getEventId());
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();

			if(event.getDelete()){
				int numDeleted;
				numDeleted = db.delete(TABLE_EVENTS, "?=?", new String[] { EVENTS_KEY_EVENT_ID, eventId });
				db.setTransactionSuccessful();
				if (numDeleted == 0) {
					Log.w("FIXME", "Attemped to delete event id " + eventId + " but no such event was found!");//FIXME need a real log tag
				} else if (numDeleted > 1) {
					Log.w("FIXME", "Found duplicate event id " + eventId + " on delete. Deleted " + String.valueOf(numDeleted) + " instead of 1!");//FIXME need a real log tag
				}
				return;
			}

			Cursor lookup = db.query(TABLE_EVENTS, new String[] { EVENTS_KEY_EVENT_ID }, "?=?", new String[] { EVENTS_KEY_EVENT_ID, eventId }, null, null, null);
			int numResults = lookup.getCount();
			ContentValues values = new ContentValues();
			values.put(EVENTS_KEY_EVENT_ID, event.getEventId());
			values.put(EVENTS_KEY_TIMESTAMP, event.getTimestamp());
			values.put(EVENTS_KEY_LAT, event.getLatitude());
			values.put(EVENTS_KEY_LON, event.getLongitude());
			values.put(EVENTS_KEY_CAPTION, event.getCaption());
			values.put(EVENTS_KEY_MEDIA, event.getMediaId());
			values.put(EVENTS_KEY_TIME_START, event.getStartTime());
			values.put(EVENTS_KEY_TIME_END, event.getEndTime());
			if (numResults == 0){
				db.insert(TABLE_EVENTS, null, values);
			} else if (numResults == 1){
				db.update(TABLE_EVENTS, values, "?=?", new String[] { EVENTS_KEY_EVENT_ID, eventId });
			} else {
				Log.w("FIXME", "Found duplicate event id " + eventId + " on update. No update performed.");//FIXME need a real log tag
				return;
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			//db.close();
		}
	}

	private void upserleteChannel(Message.Channel channel){
		String channelId = String.valueOf(channel.getChannelId());
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();

			if(channel.getDelete()){
				int numDeleted;
				numDeleted = db.delete(TABLE_CHANNELS, "?=?", new String[] { CHANNELS_KEY_CHANNEL_ID, channelId });
				db.setTransactionSuccessful();
				if (numDeleted == 0) {
					Log.w("FIXME", "Attemped to delete channel id " + channelId + " but no such channel was found!");//FIXME need a real log tag
				} else if (numDeleted > 1) {
					Log.w("FIXME", "Found duplicate channel id " + channelId + " on delete. Deleted " + String.valueOf(numDeleted) + " instead of 1!");//FIXME need a real log tag
				}
				return;
			}

			Cursor lookup = db.query(TABLE_CHANNELS, new String[] { CHANNELS_KEY_CHANNEL_ID }, "?=?", new String[] { CHANNELS_KEY_CHANNEL_ID, channelId }, null, null, null);
			int numResults = lookup.getCount();
			ContentValues values = new ContentValues();
			values.put(CHANNELS_KEY_CHANNEL_ID, channel.getChannelId());
			values.put(CHANNELS_KEY_NAME, channel.getName());
			values.put(CHANNELS_KEY_DESCRIPTION, channel.getDescription());
			if (numResults == 0){
				db.insert(TABLE_CHANNELS, null, values);
			} else if (numResults == 1){
				db.update(TABLE_CHANNELS, values, "?=?", new String[] { CHANNELS_KEY_CHANNEL_ID, channelId });
			} else {
				Log.w("FIXME", "Found duplicate channel id " + channelId + " on update. No update performed.");//FIXME need a real log tag
				return;
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			//db.close();
		}
	}

	private void upserletePostChannel(Message.PostChannel postChannel){
		String postId = String.valueOf(postChannel.getPostId());
		String channelId = String.valueOf(postChannel.getChannelId());
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();

			if(postChannel.getDelete()){
				int numDeleted;
				numDeleted = db.delete(TABLE_POST_CHANNELS, "?=? AND ?=?", new String[] { POST_CHANNELS_KEY_POST_ID, postId, POST_CHANNELS_KEY_CHANNEL_ID, channelId });
				db.setTransactionSuccessful();
				if (numDeleted == 0) {
					Log.w("FIXME", "Attemped to delete post-channel with post id " + postId + " and channel id " + channelId + " but no such post-channel was found!");//FIXME need a real log tag
				} else if (numDeleted > 1) {
					Log.w("FIXME", "Found duplicate post-channel id on delete! Post id " + postId + " and channel id " + channelId + ". Deleted " + String.valueOf(numDeleted) + " instead of 1!");//FIXME need a real log tag
				}
				return;
			}

			Cursor lookup = db.query(TABLE_POST_CHANNELS, new String[] { POST_CHANNELS_KEY_POST_ID }, "?=? AND ?=?", new String[] { POST_CHANNELS_KEY_POST_ID, postId, POST_CHANNELS_KEY_CHANNEL_ID, channelId }, null, null, null);
			int numResults = lookup.getCount();
			ContentValues values = new ContentValues();
			values.put(POST_CHANNELS_KEY_POST_ID, postId);
			values.put(POST_CHANNELS_KEY_CHANNEL_ID, channelId);
			values.put(POST_CHANNELS_KEY_VOTES_UP, String.valueOf(postChannel.getUps()));
			values.put(POST_CHANNELS_KEY_VOTES_DOWN, String.valueOf(postChannel.getDowns()));
			if (numResults == 0){
				db.insert(TABLE_POST_CHANNELS, null, values);
			} else if (numResults == 1){
				db.update(TABLE_POST_CHANNELS, values, "?=? AND ?=?", new String[] { POST_CHANNELS_KEY_POST_ID, postId, POST_CHANNELS_KEY_CHANNEL_ID, channelId });
			} else {
				Log.w("FIXME", "Found duplicate post-channel on update. No update performed. Post id " + postId + " and channel id  " + channelId);//FIXME need a real log tag
				return;
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			//db.close();
		}
	}

	private List<AleppoPost> listofPosts(Cursor results){
		int col_id = results.getColumnIndex(RadarDatabase.POSTS_KEY_POST_ID);
		int col_timestamp = results.getColumnIndex(RadarDatabase.POSTS_KEY_TIMESTAMP);
		int col_lat = results.getColumnIndex(RadarDatabase.POSTS_KEY_LAT);
		int col_lon = results.getColumnIndex(RadarDatabase.POSTS_KEY_LON);
		int col_caption = results.getColumnIndex(RadarDatabase.POSTS_KEY_CAPTION);
		int col_media = results.getColumnIndex(RadarDatabase.POSTS_KEY_MEDIA);
		int col_upvotes = results.getColumnIndex(RadarDatabase.POSTS_KEY_VOTES_UP);
		int col_downvotes= results.getColumnIndex(RadarDatabase.POSTS_KEY_VOTES_DOWN);

		int id;
		long timestamp;
		float lat, lon;
		String caption;
		String media;
		int upvote;
		int downvote;

		List<AleppoPost> posts = new LinkedList<AleppoPost>();

		results.moveToLast();

		while(!results.isBeforeFirst()){
			id = results.getInt(col_id);
			timestamp = results.getLong(col_timestamp);
			lat = results.getFloat(col_lat);
			lon = results.getFloat(col_lon);
			caption = results.getString(col_caption);
			media = results.getString(col_media);
			upvote = results.getInt(col_upvotes);
			downvote = results.getInt(col_downvotes);

			AleppoLocation loc = new AleppoLocation(lat, lon);

			AleppoPost next = new AleppoPost(id, timestamp, loc, caption, media, upvote, downvote);
			posts.add(next);

			results.moveToPrevious();
		}

		return posts;
	}

	private List<AleppoEvent> listofEvents(Cursor results){
		int col_id = results.getColumnIndex(RadarDatabase.EVENTS_KEY_EVENT_ID);
		int col_timestamp = results.getColumnIndex(RadarDatabase.EVENTS_KEY_TIMESTAMP);
		int col_lat = results.getColumnIndex(RadarDatabase.EVENTS_KEY_LAT);
		int col_lon = results.getColumnIndex(RadarDatabase.EVENTS_KEY_LON);
		int col_caption = results.getColumnIndex(RadarDatabase.EVENTS_KEY_CAPTION);
		int col_media = results.getColumnIndex(RadarDatabase.EVENTS_KEY_MEDIA);
		int col_stime = results.getColumnIndex(RadarDatabase.EVENTS_KEY_TIME_START);
		int col_etime = results.getColumnIndex(RadarDatabase.EVENTS_KEY_TIME_END);

		int id;
		long timestamp;
		float lat, lon;
		String caption;
		String media;
		long stime, etime;

		List<AleppoEvent> events = new LinkedList<AleppoEvent>();

		results.moveToLast();

		while(!results.isBeforeFirst()){
			id = results.getInt(col_id);
			timestamp = results.getLong(col_timestamp);
			lat = results.getFloat(col_lat);
			lon = results.getFloat(col_lon);
			caption = results.getString(col_caption);
			media = results.getString(col_media);
			stime = results.getLong(col_stime);
			etime = results.getLong(col_etime);

			AleppoLocation loc = new AleppoLocation(lat, lon);

			AleppoEvent next = new AleppoEvent(id, timestamp, loc, caption, media, stime, etime);
			events.add(next);

			results.moveToPrevious();
		}

		return events;
	}
}
