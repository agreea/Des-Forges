package com.example.radr.backend;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.example.radr.AleppoChannel;
import com.example.radr.backend.database.RadarDataHelper;
import com.example.radr.backend.util.ListLoader;
import com.example.radr.util.LogTags;

public class ChannelLoader extends ListLoader<AleppoChannel> {
	Context c;
	public ChannelLoader(Context context) {
		super(context);
		Log.v(LogTags.BACKEND_LOAD, "Constructing EventLoader");
		c = context;
	}
	
	@Override
	protected List<AleppoChannel> getData() {
		return (new RadarDataHelper(c)).getSubscribedChannels();
	}
}