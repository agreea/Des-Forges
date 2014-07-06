package com.example.radr.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.radr.backend.AsyncRefresher.Interface;
import com.example.radr.backend.network.ServerPoller;
import com.example.radr.util.LogTags;

public class AsyncRefresher extends AsyncTask<Interface, Void, Void> {
	private Interface mInterface;
	
	public interface Interface {
		public Context getApplicationContext();
		public void asyncRefresherDone();
	}
	
	public AsyncRefresher(Interface i){
		mInterface = i;
	}
	
	@Override
	protected Void doInBackground(Interface... params) {
//		mInterface = params[0];
//		refresh(params[0]);
		refresh(mInterface);
		return null;
	}
	
	private static void refresh(Interface i) {
		Log.v(LogTags.BACKEND_NETWORK_POLL, "AsyncRefresher.refresh: explicit refresh invoked");
		Context c = i.getApplicationContext();
		ServerPoller.stopPolling(c);
		new ServerPoller().onReceive(c, null);
		ServerPoller.startPolling(c);
	}
	
	@Override
	public void onPostExecute(Void result) {
		mInterface.asyncRefresherDone();
	}
}