package com.example.radr.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.radr.R;

class PostHolder{
	ImageView media;
	ImageButton up;
	ImageButton down;
	TextView caption;
	TextView voteCount;
	TextView dist;
	TextView timestamp;
	Button moreOptions;
	int position;
	int voteValue;
	int netVote;
	AQuery aq;
	PostHolderInterface mInterface;

	interface PostHolderInterface{
		// returns the vote value (1 || 0 || -1) associated with the post at "position" in the adapter
		int getVoteValue(int position);
		// returns the net votes (total upvotes - total downvotes) associated with the post at "position" in the adapter
		int getNetVote(int position);
		// update the vote value (1 || 0 || -1) at position with the new vote value
		// returns the new vote value
		int updateVoteValue(int position, int voteValue);
		// update the net votes at position to account for the new vote (2||1 || 0 || -1||2)
		// returns the new net vote count
		int updateNetVote(int position, int newVote);
	}
	public PostHolder(View convertView, final int position, FeedAdapter fa){
		aq = new AQuery(convertView);
		mInterface = (PostHolderInterface) fa;
		this.position = position;
		// get the vote data--netvote goes in the voteCount textview
		voteValue = mInterface.getVoteValue(position);
		netVote = mInterface.getNetVote(position);
		// inflate buttons
		caption = (TextView) convertView.findViewById(R.id.caption);
		media = (ImageView) convertView.findViewById(R.id.picture);
		voteCount = (TextView) convertView.findViewById(R.id.voteCount);
		up = (ImageButton) convertView.findViewById(R.id.upvote);
		down = (ImageButton) convertView.findViewById(R.id.downvote);
		timestamp = (TextView) convertView.findViewById(R.id.timestamp);
		dist = (TextView) convertView.findViewById(R.id.distance);
		moreOptions = (Button) convertView.findViewById(R.id.more_options);		
		setButtonInterface();
		setListeners();
	}
	// sets the postHolder's vote data according to the data for the post at the given position
	// this prevents recycling data from other vote layouts
	public void setForPosition(int position){
		this.position = position;
		voteValue = mInterface.getVoteValue(position);
		netVote = mInterface.getNetVote(this.position);
		setButtonInterface();
	}
	private void setButtonInterface(){
		// render the proper state of the button interface based on
		// the voteValue and the netVote
		if(voteValue == 1){
			aq.id(up).image(R.drawable.arrohead_up_green);
			aq.id(down).image(R.drawable.arrohead_down);
		}
		if(voteValue == -1){//
			aq.id(down).image(R.drawable.arrohead_down_red);
			aq.id(up).image(R.drawable.arrohead_up);
		}
		if(voteValue == 0){
			aq.id(up).image(R.drawable.arrohead_up);
			aq.id(down).image(R.drawable.arrohead_down);
		}
		voteCount.setText(Integer.toString(netVote));
	}
	
	private void setListeners(){
		down.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				// Update the drawable, the netVote and the vote value according to
				// what the current state of the vote interface is
				if(voteValue==0){ // if it's neutral...
					// set to upvote
					aq.id(down).image(R.drawable.arrohead_down_red);
					aq.id(up).image(R.drawable.arrohead_up);
					voteValue = mInterface.updateVoteValue(position, -1);
					netVote = mInterface.updateNetVote(position, -1);
				}
				else if(voteValue==-1){ // if it's already downvoted...
					// set to neutral
					aq.id(down).image(R.drawable.arrohead_down);
					aq.id(up).image(R.drawable.arrohead_up);
					voteValue = mInterface.updateVoteValue(position, 0);
					// you just took a downvote away, so add net 1 to the pile
					netVote = mInterface.updateNetVote(position, 1);
				}
				else if(voteValue==1){ // if it's at upvote, make the down arrow red and the up arrow gray
					aq.id(down).image(R.drawable.arrohead_down_red);
					aq.id(up).image(R.drawable.arrohead_up);
					voteValue = mInterface.updateVoteValue(position, -1); // take away an up vote and add a downvote
					netVote = mInterface.updateNetVote(position, -1-1);
				}
				// set the voteText to the new vote count
				voteCount.setText(Integer.toString(netVote));
			}
		});
		up.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// Update the drawable, the netVote and the vote value according to
				// what the current state of the vote interface is
				if(voteValue==(0)){ // if it's neutral...
					// set to upvote
					aq.id(up).image(R.drawable.arrohead_up_green);
					voteValue = mInterface.updateVoteValue(position, 1);
					netVote = mInterface.updateNetVote(position, 1);
				}
				else if(voteValue==1){ // if it's already upvoted...
					// set to neutral
					aq.id(up).image(R.drawable.arrohead_up);
					netVote = mInterface.updateVoteValue(position, 0);
					voteValue = mInterface.updateNetVote(position, -1); // take away one upvote
				}
				else if(voteValue==-1){ // if it's a downvote, make the down arrow gray and up one green
					aq.id(up).image(R.drawable.arrohead_up_green);
					aq.id(down).image(R.drawable.arrohead_down);
					netVote = mInterface.updateNetVote(position, 1+1);// take away a downvote and add an upvote
					voteValue = mInterface.updateVoteValue(position, 1);
				}
				// set the voteText to the new vote count
				voteCount.setText(Integer.toString(netVote));
			}
		});
	}
}

