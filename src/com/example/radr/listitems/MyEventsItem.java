package com.example.radr.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.radr.R;

public class MyEventsItem extends RelativeLayout {
	private final int TYPE_GREENBAR = 0;
	private final int TYPE_STANDARD = 1;
	private final int TYPE_POPULAR = 2;
	ImageView thumbnail;
	ImageButton moreInfo;
	TextView title;
	TextView time;
	TextView attendeeCount;
	LayoutInflater inflater;
	
	public MyEventsItem(Context context, int whichLayout) {
		super(context);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		switch(whichLayout){
			case TYPE_GREENBAR:
				setUpGreenBar();
			case TYPE_STANDARD:
				setUpStandard();
			case TYPE_POPULAR:
				setUpPopular();
		}
	}
	
	public MyEventsItem(Context context) {
		super(context);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setUpStandard();
	}
	
	private void setUpGreenBar(){
		inflater.inflate(R.layout.event_greenbar, this, true);
		title = (TextView) findViewById(R.id.greenbar_title);
	}
	
	private void setUpStandard(){
		inflater.inflate(R.layout.standard_event, this, true);
		setUpCommonButtons();
	}
	
	private void setUpPopular(){
		inflater.inflate(R.layout.popular_event, this, true);
		setUpCommonButtons();
	}
	
	private void setUpCommonButtons(){
		thumbnail = (ImageView) findViewById(R.id.thumbnail);
		moreInfo = (ImageButton) findViewById(R.id.more_info);
		title = (TextView) findViewById(R.id.event_title);
		time = (TextView) findViewById(R.id.event_time);
	}
}
