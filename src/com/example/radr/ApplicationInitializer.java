package com.example.radr;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.radr.backend.database.RadarDataHelper;
import com.example.radr.backend.database.RadarDatabase;
import com.example.radr.backend.network.ServerPoller;

public class ApplicationInitializer extends Application {
	private static Context appContext = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		//init(getApplicationContext());
	}
	
	public static void init(Context context){
		appContext = context;
		//RadarDatabase.setContext(context);
		ServerPoller.startPolling(context);
	}
	
	public static Context getAppContext() {
		return appContext;
	}
}
