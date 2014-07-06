package com.example.radr.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.androidquery.AQuery;
import com.example.radr.LocationGrabber;
import com.example.radr.R;
import com.example.radr.adapter.EventHolder.EventHolderInterface;
import com.example.radr.listitems.AddEventRow;
import com.example.radr.listitems.EventView;
import com.example.radr.listitems.PostView;

public class EventAdapter extends BaseAdapter implements EventHolderInterface{
	private List<EventView> events;
	private List voteValues;
    private List netVotes;
    private List attendenceStatus;
    private List attendeeCount;
	private final String TAG = "EVENTADAPTER";
	private LocationGrabber lg;
	private AQuery aq;	
    private Activity mActivity;
    public class AddEventHolder{
    	Button addEvent;
    }
    
	public EventAdapter(Activity activity){
		super();
		mActivity = activity;
		lg = (LocationGrabber) mActivity;
		events = new ArrayList<EventView>();
		aq = new AQuery(mActivity); 
		voteValues = new ArrayList();
		netVotes = new ArrayList();
		attendenceStatus = new ArrayList();
		attendeeCount = new ArrayList();

	}

	@Override
	public EventView getView(int position, View convertView, ViewGroup parent) {
		EventHolder eventHolder;
		EventView eventContent = (EventView) getItem(position);
		if(convertView == null){
			//inflate the view if it's null
			convertView = new EventView(mActivity);
			eventHolder = new EventHolder(convertView, position, this);
			convertView.setTag(eventHolder);
		}
		else{
			eventHolder = (EventHolder) convertView.getTag();
		}
		// set all the stuff that varies by event:
		eventHolder.caption.setText(eventContent.getCaption());
		// TODO: write methods for distance and time in EventView********************
		//eventHolder.dist.setText(eventContent.getLocationStamp());
		//eventHolder.timestamp.setText(eventContent.properTime());
		eventHolder.setForPosition(position);
		// Log.w("DISTANCE STAMP", eventContent.getLocationStamp());
        if(eventContent.getImageURL() != null){ // if there's no image url just set the default
        	aq.id(eventHolder.media).image(eventContent.getImageURL());
        }
        else{
        	aq.id(eventHolder.media).image(eventContent.getPicDrawable());
        }
        convertView.setTag(eventHolder);
		return (EventView) convertView;
	}
	
	// need to cast the return value to AddEventRow
	private View addEventRow(View convertView){
        AddEventHolder addEvent = new AddEventHolder();
		if(convertView == null){
            LayoutInflater inflater = 
            		(LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_add_event, null);
            addEvent.addEvent = (Button) convertView.findViewById(R.id.add_event_button);
            convertView.setTag(addEvent);
		}
		else{
			addEvent = (AddEventHolder) convertView.getTag();
		}
		return (AddEventRow) convertView;
	}
	
	public void addEvent(EventView ev){
		events.add(ev);
		voteValues.add(0);
		netVotes.add(0);
		attendenceStatus.add(0);
		attendeeCount.add(0);
		notifyDataSetChanged();
		Log.w("ADDEVENT", "Events list size is now: " + events.size());
	}
	
	public void addEventsList(List<EventView> newEvents){
		events.addAll(newEvents);
		for(EventView event : events){
			voteValues.add(0);
			netVotes.add(0);
			attendenceStatus.add(0);
			attendeeCount.add(0);
		}
		notifyDataSetChanged();
	}
	
	// clear the net votes, the vote values, and the attendance statuses
	// initialize them all at zero for now
	@SuppressWarnings("unchecked")
	public void setEvents(List<EventView> newEvents){
		events.clear();
		voteValues.clear();
		netVotes.clear();
		attendenceStatus.clear();
		attendeeCount.clear();
		events.addAll(newEvents);
		for(EventView event: events){
			voteValues.add(0);
			netVotes.add(0);
			attendenceStatus.add(0);
			attendeeCount.add(0);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		return events.get(position);
	}
	@Override
	public long getItemId(int position) {
		return events.get(position).getId();
	}
	
	@Override
	public int getCount() {
		return events.size();
	}

//================EventHolder Interface Methods==================
//	@Override
//	public int getVoteValue(int position) {
//		return (Integer) voteValues.get(position);
//	}
//
//	@Override
//	public int updateVoteValue(int position, int newVoteValue) {
//		voteValues.set(position, newVoteValue);
//		return (Integer) voteValues.get(position);
//	}
//
//	@Override
//	public int getNetVote(int position) {
//		return (Integer) netVotes.get(position);
//	}
//
//	@Override
//	public int updateNetVote(int position, int newVote) {
//		if(newVote < 3 && newVote > -3){
//			int newNetVote = (Integer) netVotes.get(position) + newVote;
//			netVotes.set(position, newNetVote);
//		}
//		return (Integer) netVotes.get(position);
//	}

	@Override
	public int getAttenStatus(int position) {
		return (Integer) attendenceStatus.get(position);
	}

	@Override
	public int updateAttenStatus(int position, int attenValue) {
		if(attenValue < 2 && attenValue > -1){
			attendenceStatus.set(position, attenValue);
		}
		 int attenStatus = (Integer) attendenceStatus.get(position);
		// if they said they're not attending but were before, update their status and take one
		// off the total attendees:
		if(attenValue == 0 && attenStatus == 1){
			attendeeCount.set(position, (Integer) attendeeCount.get(position)-1);
			attendenceStatus.set(position, attenValue);
		}
		// if they said they are attending and weren't before, update their status and add one
		// to the total attendees:
		else if(attenValue == 1 && attenStatus == 0){
			attendeeCount.set(position, (Integer) attendeeCount.get(position)+1);
			attendenceStatus.set(position, attenValue);
		}
		return (Integer) attendenceStatus.get(position);
	}

	@Override
	public int getAttendees(int position) {
		return (Integer) attendeeCount.get(position);
	}
}
