package com.example.radr.backend.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.example.radr.util.LogTags;

import android.util.Log;

public class HTTP {
	public static String get(String url) throws IOException {
		return new String(getBytes(url));
	}
	
	public static byte[] getBytes(String url) throws IOException {
		HttpURLConnection conn = connFromUrlString(url);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		
		byte[] out = IOUtils.toByteArray(conn.getInputStream());
		conn.disconnect();
		return out;
	}
	
	public static String post(String url, String body) throws IOException {
		return post(url, body.getBytes());
	}
	
	public static String post(String url, byte[] body) throws IOException {
		HttpURLConnection conn = connFromUrlString(url);
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		Log.w(LogTags.BACKEND_WRITE, "connection " + conn.toString());
		
		OutputStream os = conn.getOutputStream();
		Log.w(LogTags.BACKEND_WRITE, "output stream " + os.toString());
		os.write(body);
		os.flush();
		os.close();
		
		String out = IOUtils.toString(conn.getInputStream(), "UTF-8");
		Log.w(LogTags.BACKEND_WRITE, "out " + out);
		conn.disconnect();
		return out;
	}
	
	private static URL urlFromString(String url) throws MalformedURLException {
		return new URL(url);
	}
	
	private static HttpURLConnection connFromUrlString(String url) throws IOException {
		return (HttpURLConnection)urlFromString(url).openConnection();
	}
}
