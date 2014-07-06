package com.example.radr.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.radr.R;
import com.example.radr.listitems.Channel;

public class ChannelAdapter extends BaseAdapter{
	private LayoutInflater inflater;
    private List<Channel> channels;
    
    static class ChanHolder{
    	TextView channelName;
    	TextView followerCount;
    	CheckBox togglePush;
    }
    
    public ChannelAdapter(Context context){
    	inflater = LayoutInflater.from(context);
    	channels = new ArrayList<Channel>();
    }
	@Override
	public int getCount() {
		return channels.size();
	}
	
	public void add(Channel channel){
		channels.add(channel);
		notifyDataSetChanged();
	}
	
	public void add(List<Channel> channels){
		this.channels.addAll(channels);
		notifyDataSetChanged();
	}

	@Override
	public Channel getItem(int position) {
		return channels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return channels.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChanHolder cH;
		if(convertView == null){
			convertView = new Channel(inflater.getContext());
			cH = new ChanHolder();
			cH.channelName = (TextView) convertView.findViewById(R.id.channel_name);
			cH.followerCount = (TextView) convertView.findViewById(R.id.follower_count);
			cH.togglePush = (CheckBox) convertView.findViewById(R.id.check_notifications);
			convertView.setTag(cH);
		}
		else{
			//retrieve the viewholder if it isn't null.
			cH = (ChanHolder) convertView.getTag();
		}
		return (Channel) convertView;
	}
}
