package com.example.radr.backend;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.example.radr.AleppoEvent;
import com.example.radr.backend.database.RadarDataHelper;
import com.example.radr.backend.util.ListLoader;
import com.example.radr.util.LogTags;

public class EventLoader extends ListLoader<AleppoEvent> {
	// TODO: Put this in an xml resource
	private static final int DEFAULT_NUM_EVENTS = 1000;
	private final int mNumEvents;
	Context c;

	public EventLoader(Context context) {
		this(context, DEFAULT_NUM_EVENTS);
	}
	
	public EventLoader(Context context, int numEvents) {
		super(context);
		c = context;
		mNumEvents = numEvents;
		Log.v(LogTags.BACKEND_LOAD, "Constructing EventLoader with mNumEvents=" + mNumEvents);
	}
	
	@Override
	protected List<AleppoEvent> getData() {
		Log.v(LogTags.BACKEND_LOAD, "Retrieving ranked events with mNumEvents=" + mNumEvents);
		return (new RadarDataHelper(c)).getRankedEvents(mNumEvents);
	}
}