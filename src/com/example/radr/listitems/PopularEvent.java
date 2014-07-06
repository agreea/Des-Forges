package com.example.radr.listitems;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.radr.R;

public class PopularEvent extends MyEventsItem {
	ImageButton moreInfo;
	TextView starCount;
	public PopularEvent(Context context) {
		super(context, 2);
		moreInfo = (ImageButton) findViewById(R.id.more_info);
		attendeeCount = (TextView) findViewById(R.id.star_count);
	}
}
