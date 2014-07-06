package com.example.radr.backend;

import android.os.AsyncTask;
import android.util.Log;

import com.example.radr.AleppoChannel;
import com.example.radr.AleppoEvent;
import com.example.radr.AleppoPost;
import com.example.radr.backend.network.Message.Channel;
import com.example.radr.backend.network.Message.Event;
import com.example.radr.backend.network.Message.Post;
import com.example.radr.backend.network.Message.Response;
import com.example.radr.backend.network.Request;
import com.example.radr.util.LogTags;

public class AsyncBackendWriter extends AsyncTask<AsyncBackendWriter.Query, Void, Void> {
	public static void write(Query q) {
		new AsyncBackendWriter().execute(q);
	}
	
	public static abstract class Query {
		protected abstract void doInBackground();
	}
	
	public static class PostQuery extends Query {
		private final AleppoPost mPost;
		private final byte[] bitmapBytes;
		
		public PostQuery(AleppoPost post, byte[] bytes) {
			mPost = post;
			bitmapBytes = bytes;
		}
		
		// TODO: Handle postChannels
		@Override
		protected void doInBackground() {
			Log.w(LogTags.BACKEND_WRITE, "Beginning PostQuery...");
			try {
				Log.w(LogTags.BACKEND_WRITE, "About to get media id! ");
				String mediaId = Request.putMedia(bitmapBytes);
				Log.w(LogTags.BACKEND_WRITE, "Got media id! " + mediaId);
				Log.w(LogTags.BACKEND_WRITE, "[PostQuery] Put media; got id: " + mediaId);
				Response r = new Response();
				Post p = mPost.toPost();
				p.mediaId = mediaId;
				r.posts = new Post[]{p};
				Request.pushChanges(r);
				Log.v(LogTags.BACKEND_WRITE, "[PostQuery] Putting json: " + r.toJSONString());
			} catch (Exception e) {
				Log.w(LogTags.BACKEND_WRITE, "Error pushing Post to server: " + e.toString());
			}
		}
	}
	
	public static class EventQuery extends Query {
		private final AleppoEvent mEvent;
		private final byte[] bitmapBytes;
		
		public EventQuery(AleppoEvent event, byte[] bytes) {
			mEvent = event;
			bitmapBytes = bytes;
		}
		
		@Override
		protected void doInBackground() {
			Log.v(LogTags.BACKEND_WRITE, "Beginning EventQuery...");
			try {
				String mediaId = Request.putMedia(bitmapBytes);
				Log.v(LogTags.BACKEND_WRITE, "[EventQuery] Put media; got id: " + mediaId);
				Response r = new Response();
				Event e = mEvent.toEvent();
				e.mediaId = mediaId;
				r.events = new Event[]{e};
				Request.pushChanges(r);
			} catch (Exception e) {
				Log.w(LogTags.BACKEND_WRITE, "Error pushing Event to server: " + e.toString());
			}
		}
	}
	
	public static class ChannelQuery extends Query {
		private final AleppoChannel mChannel;
		
		public ChannelQuery(AleppoChannel channel) {
			mChannel = channel;
		}
		
		@Override
		protected void doInBackground() {
			Log.v(LogTags.BACKEND_WRITE, "Beginning ChannelQuery...");
			try {
				Response r = new Response();
				Channel c = mChannel.toChannel();
				r.channels = new Channel[]{c};
				Request.pushChanges(r);
			} catch (Exception e) {
				Log.w(LogTags.BACKEND_WRITE, "Error pushing Channel to server: " + e.toString());
			}
		}
	}
	
	public static class VoteQuery extends Query {
		private final AleppoPost mPost;
		
		public VoteQuery(AleppoPost post) {
			mPost = post;
		}
		
		// TODO: Handle postChannels
		@Override
		protected void doInBackground() {
			Log.w(LogTags.BACKEND_WRITE, "Beginning VoteQuery...");
			try {
				Response r = new Response();
				Post p = mPost.toPost();
				p.update = true;
				r.posts = new Post[]{p};
				Request.pushChanges(r);
				Log.v(LogTags.BACKEND_WRITE, "[PostQuery] Putting json: " + r.toJSONString());
			} catch (Exception e) {
				Log.w(LogTags.BACKEND_WRITE, "Error pushing Post to server: " + e.toString());
			}
		}
	}
	
	@Override
	protected Void doInBackground(Query... params) {
		params[0].doInBackground();
		return null;
	}

}
