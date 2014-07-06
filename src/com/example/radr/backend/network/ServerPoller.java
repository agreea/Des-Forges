package com.example.radr.backend.network;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.example.radr.backend.database.RadarDataHelper;
import com.example.radr.backend.network.Message.Response;
import com.example.radr.listitems.PostView;
import com.example.radr.util.LogTags;

// Implementation adapted from http://stackoverflow.com/a/8801990/836390

// TODO: Keep this class name (package included) up to date
// in AndroidManifest.xml as being allowed to receive broadcasts
// (use '<receiver  android:process=":remote" android:name="class-name-here" />')
public class ServerPoller extends BroadcastReceiver {
	private static final int SLEEP_TIME = 20*1000; //5 * 60 * 1000; // min * sec * ms
	private static Context mContext;
	public static void startPolling(Context c) {
		// Make sure we're using the app context.
		// (calling getApplicationContext() on the
		// app context returns itself, so this is
		// safe).
		mContext = c;
		Log.w(LogTags.BACKEND_NETWORK_POLL, "Registering with context " + c.getPackageName());
		AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		if (am == null) {
			// TODO: Come up with tag name
			Log.e(LogTags.BACKEND_NETWORK_POLL, "Failed to get system service ALARM_SERVICE from Context#getSystemService()");
		} else {
			Intent i = new Intent(mContext, ServerPoller.class);
			PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, 0);
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SLEEP_TIME,
					SLEEP_TIME, pi);
		}
	}
	
	public static void stopPolling(Context c) {
		// Make sure we're using the app context.
		// (calling getApplicationContext() on the
		// app context returns itself, so this is
		// safe).
		c = c.getApplicationContext();
		Log.v(LogTags.BACKEND_NETWORK_POLL, "Unregistering from context " + c.getPackageName());
		AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		if (am == null) {
			Log.e(LogTags.BACKEND_NETWORK_POLL, "Failed to get system service ALARM_SERVICE from Context#getSystemService()");
		} else {
			Intent i = new Intent(c, ServerPoller.class);
			PendingIntent sender = PendingIntent.getBroadcast(c, 0, i, 0);
			am.cancel(sender);
		}
	}

	// SuppressLint is for use of AsyncTask.THREAD_POOL_EXECUTOR
	// at bottom of method. It will only execute on API levels
	// that support it.
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context c, Intent i) {
		class UpdateDBTask extends AsyncTask<Void, Void, Void> {
			Context updateDBContext;
			public UpdateDBTask(Context c){
				super();
				updateDBContext = c;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
//				Log.w(LogTags.BACKEND_NETWORK_POLL, "Polling server...");
				try {
					RadarDataHelper db = new RadarDataHelper(updateDBContext);
					String t = db.getWorldTime();
					Log.w(LogTags.BACKEND_NETWORK_POLL, "...using timestamp " + t);
					Response r = Request.getChangesSince(t);
					Log.v(LogTags.BACKEND_NETWORK_POLL, "Update contents: " + r.toJSONStringPretty());
					db.update(r);
					Log.w(LogTags.BACKEND_NETWORK_POLL, "Got response from server");
				} catch (Exception e) {
					Log.e(LogTags.BACKEND_NETWORK_POLL, "Error polling server: " + e);
					Log.e(LogTags.BACKEND_NETWORK_POLL, Log.getStackTraceString(e));
				}
				return null;
			}
		}
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1)
			new UpdateDBTask(c).execute();
		else
			new UpdateDBTask(c).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
