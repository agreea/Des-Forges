package com.example.radr.adapter;

import java.util.List;
import java.util.TreeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.radr.listitems.MyEventsItem;

public class MyEventsAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
    private List<MyEventsItem> myEvents;
    private final int TYPE_EVENT = 0;
    private final int TYPE_POPEVENT = 1;
    private final int MAX_TYPECOUNT = TYPE_POPEVENT + 1;
    private TreeSet mPopEventSet = new TreeSet();
    static class EventHolder{
    	ImageView thumbnail;
    	ImageButton arrowHead;
    	TextView eventName;
    	TextView time;
    	RelativeLayout geoTime;
    }
    
    static class PopularEventHolder{
    	ImageView thumbnail;
    	ImageButton person;
    	TextView attendeeCount;
    	TextView eventName;
    	TextView time;
    }
    
    static class GreenBarHolder{
    	ImageView greenBar;
    	TextView title;
    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
