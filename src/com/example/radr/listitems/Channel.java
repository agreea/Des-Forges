package com.example.radr.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.radr.R;

public class Channel extends RelativeLayout {
	TextView channelName;
	TextView followerCount;
	CheckBox togglePush;
	LayoutInflater inflater;
	Boolean isPush;
	
	public Channel(Context context, int followerCount, String channelName) {
		super(context);
		initChannel();
		setChanName(channelName);
		setFollowerCount(followerCount);
	}
	
	public Channel (Context context){
		super(context);
		initChannel();
	}
	
	public void initChannel(){
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.channel_row, this, true);
		channelName = (TextView) findViewById(R.id.channel_name);
		followerCount = (TextView) findViewById(R.id.follower_count);
		togglePush = (CheckBox) findViewById(R.id.check_notifications);
		isPush = false;
		togglePush.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isPush){
					isPush = true;
				}
				else{
					isPush = false;
				}
			}
		});
	}
	
	public void setChanName(String chanName){
		channelName.setText(chanName);
	}
	
	public void setFollowerCount(int fc){
		followerCount.setText(Integer.toString(fc) + " Subscribed ");
	}
	
}
