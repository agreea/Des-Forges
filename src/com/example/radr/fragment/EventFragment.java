package com.example.radr.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.radr.R;

public class EventFragment extends Fragment{
	FragmentManager fragmentManager;
	EventFeedFragment eFF;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_event, null, false);
		fragmentManager = getActivity().getSupportFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	    eFF = new EventFeedFragment();
	    fragmentTransaction.add(R.id.fragmentcontainer, eFF);
	    fragmentTransaction.commit();
		return rootView;
	}
		
	public void launchCreateEvent(){
	    FragmentTransaction transaction = fragmentManager.beginTransaction();
	    transaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
	    CreateEventFragment createEvent = new CreateEventFragment();
	    transaction.replace(R.id.fragmentcontainer, createEvent);
	    transaction.commit();
	}
	public void onEventCreated(){
//	    createEvent.setImageResource(R.drawable.addevent);
//	    createEvent.setVisibility(View.VISIBLE);
//		title.setText("events");
	}
	public EventFeedFragment getEventFeedFragment(){
		return eFF;
	}
}
