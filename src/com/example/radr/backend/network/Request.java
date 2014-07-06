package com.example.radr.backend.network;

import java.io.IOException;

import org.json.JSONException;

import android.util.Log;

import com.example.radr.backend.network.Message.Response;
import com.example.radr.util.LogTags;

public abstract class Request {
	// TODO: Keep these up to date
	private static String API_KEY = "kjjKucj0GXD26D75B2zwBeJsgcH5xIDSjbuIgksApsPLrM6vqDh7N27LHIJ6Zhfy";
	private static String BASE_URL = "http://private.joshlf.com:14483/?k=" + API_KEY;
	private static String MEDIA_URL = "http://private.joshlf.com:14483/media/?k=" + API_KEY;
	
	// TODO: How should we handle exceptions? Internally?
	public static Response getChangesSince(String timestamp) throws JSONException, IOException {
		String url = BASE_URL + "&t=" + timestamp;
		return Response.fromJSONString(HTTP.get(url));
	}
	
	// TODO: How should we handle exceptions? Internally?
	public static String pushChanges(Response r) throws IOException, JSONException {
		return HTTP.post(BASE_URL, r.toJSONStringPretty());
	}
	
	public static byte[] getMedia(String mediaId) throws IOException {
		return HTTP.getBytes(getMediaUrl(mediaId));
	}
	
	public static String getMediaUrl(String mediaId) {
		return MEDIA_URL + "&bk=" + mediaId;
	}
	
	// TODO: Implement
	// Returns media id
	public static String putMedia(byte[] bytes) throws IOException {
		
		String value = HTTP.post(MEDIA_URL, bytes);
		Log.w(LogTags.BACKEND_WRITE, "IN PUT MEDIA, media id: " + value);
		return value;
	}
}
