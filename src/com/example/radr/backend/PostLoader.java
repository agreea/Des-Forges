package com.example.radr.backend;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.example.radr.AleppoPost;
import com.example.radr.backend.database.RadarDataHelper;
import com.example.radr.backend.util.ListLoader;
import com.example.radr.util.LogTags;

public class PostLoader extends ListLoader<AleppoPost> {
	// TODO: Put this in an xml resource
	private static final int DEFAULT_NUM_POSTS = 1000;
	private final int mNumPosts;
	private Context c;

	public PostLoader(Context context) {
		this(context, DEFAULT_NUM_POSTS);
	}
	
	public PostLoader(Context context, int numPosts) {
		super(context);
		mNumPosts = numPosts;
		c = context;
		Log.v(LogTags.BACKEND_LOAD, "Constructing PostLoader with mNumPosts=" + mNumPosts);
	}
	
	@Override
	protected List<AleppoPost> getData() {
		return (new RadarDataHelper(c)).getRankedFeedPosts(mNumPosts);
	}
}
