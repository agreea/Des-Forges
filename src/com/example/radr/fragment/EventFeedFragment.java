package com.example.radr.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.radr.AleppoEvent;
import com.example.radr.AleppoPost;
import com.example.radr.CamFeedEvent;
import com.example.radr.LocationGrabber;
import com.example.radr.R;
import com.example.radr.adapter.EventAdapter;
import com.example.radr.backend.EventLoader;
import com.example.radr.backend.PostLoader;
import com.example.radr.listitems.EventView;
import com.example.radr.listitems.PostView;

public class EventFeedFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<AleppoEvent>>{
	Button addEvent;
	ImageButton createEvent;
	TextView title;

	View rootView;
	Button refresh;
	LocationGrabber lg;
	Activity mActivity;
	EventAdapter mEAdapter;
	
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		mActivity = getActivity();
	    lg = (LocationGrabber) mActivity;
	    mEAdapter = new EventAdapter(mActivity);
	    setListAdapter(mEAdapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_show_events, container, false);
		createEvent = (ImageButton) rootView.findViewById(R.id.toCreateEvent);
		title = (TextView) rootView.findViewById(R.id.event_fragment_title);

        refresh = (Button) rootView.findViewById(R.id.refresh_events);
        refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//refresh();
			}
		});
        addEvent = (Button) rootView.findViewById(R.id.add_event_to_feed);
        addEvent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addEvent();
				Log.w("ADDEVENT", "I got called!");
			}
		});
		createEvent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				CamFeedEvent cfe = (CamFeedEvent) getActivity();
				cfe.createEventLaunched();
				//createEvent.setImageResource(R.drawable.addevent_pressed);
				createEvent.setVisibility(View.INVISIBLE);
			}
		});
        return rootView;
    }
	
	public void addEvent(){
		mEAdapter.addEvent(new EventView(mActivity));
	}
	@Override
	public Loader<List<AleppoEvent>> onCreateLoader(int arg0, Bundle arg1) {
		return new EventLoader(getActivity());
	}
	
	@Override
	public void onLoadFinished(Loader<List<AleppoEvent>> aleppoEventLoader,
			List<AleppoEvent> events) {
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");
		Log.w("EVENTLOAD FINISHED", "=======EVENT LOAD has been finished!");

		// Add the results to the adapter.
		List<EventView> ev = new ArrayList<EventView>();
		for(AleppoEvent event : events){
			EventView eventToAdd = new EventView(mActivity, event);
			eventToAdd.setLocation(lg.getLocation());
			ev.add(eventToAdd);
		}
		mEAdapter.setEvents(ev);

	}
	@Override
	public void onLoaderReset(Loader<List<AleppoEvent>> arg0) {
		// TODO Auto-generated method stub
		
	}
}
