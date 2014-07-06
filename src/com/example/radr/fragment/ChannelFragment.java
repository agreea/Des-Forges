package com.example.radr.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.radr.R;
import com.example.radr.adapter.ChannelAdapter;
import com.example.radr.listitems.Channel;
 
public class ChannelFragment extends ListFragment {
	ChannelAdapter mCAdapter;
	static View rootView;
	Button addChan;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
      mCAdapter = new ChannelAdapter(getActivity());
      setListAdapter(mCAdapter);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_channels, container, false);
        addChan = (Button) rootView.findViewById(R.id.addchannel);
        addChan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addChannel();
			}
		});
        return rootView;
    }
    
    public void addChannel(){
    	Channel chan = new Channel(getActivity());
		mCAdapter.add(chan);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
      // do something with the data
    }
}