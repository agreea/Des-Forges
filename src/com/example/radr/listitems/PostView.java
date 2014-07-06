package com.example.radr.listitems;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.radr.AleppoLocation;
import com.example.radr.AleppoPost;
import com.example.radr.LocationGrabber;
import com.example.radr.PostParcel;
import com.example.radr.R;

public class PostView extends RelativeLayout{
	LayoutInflater inflater;
	Activity mActivity;
	ImageView content;
	
	LinearLayout voteButtons;
	ImageButton upVote;
	ImageButton downVote;
	TextView voteCountTV;
	
	TextView captionTV;
	TextView distanceTV;
	TextView timestampTV;
	Button moreOptions;
	OnClickListener downvoteListener;
	OnClickListener upvoteListener;
	
	AleppoLocation postLocation;
	
	private final int ID;
	private final String mediaID;
	private String imageURL;
	String caption;
	String distance;
	int upvoteCount;
	int downvoteCount;
	int voteValue;
	long timestamp; // Time since epoch, in seconds, when the post was captured
	LocationGrabber locationGrabber;
	
	private final int UPVOTE = 1;
	private final int DOWNVOTE = -1;
	private final int NEUTRAL = 0;
	
	public PostView(Activity activity) {
		super(activity);
		mActivity = activity;
		ID = 0;
		mediaID = "";
		setUp(null, null, null, 0, 0, 0, null);
        try {
            locationGrabber = (LocationGrabber) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	} 
	
	public PostView(Context context, Bitmap bitmap, String caption, String[] channels,
			boolean isEvent, String imageURL, long timestamp, int upvote, int downvote, long lat, long lon){
		super(context);
		ID = 0;
		mediaID = "";
		setUp(bitmap, 
			  caption, 
			  imageURL,
			  timestamp,
			  upvote,
			  downvote, 
			  new AleppoLocation(lat, lon));
	}
	
	public PostView(Context context, AleppoPost ap){
		super(context);
		ID = ap.id();
		mediaID = ap.mediaId();
		setUp(null, // no bitmap
				  ap.caption(),
				  ap.mediaUrl(),
				  ap.timestamp(),
				  ap.upvote(),
				  ap.downvote(),
				  ap.location());
	}
	
	private void setUp(Bitmap bitmap, 
			String captionString, String imageURL, 
			long timestamp, 
			int upvote, int downvote, AleppoLocation loc){
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.upvoteCount = upvote;
		this.downvoteCount = downvote;
		this.imageURL = imageURL;
		this.timestamp = timestamp;
		this.postLocation = loc;
		Log.w("PostView", "SETUP CALLED!!");

		this.voteValue = 0;
		// Bind the views and set up the layout
		inflater.inflate(R.layout.post_as_polaroid, this, true);
		captionTV = (TextView) findViewById(R.id.caption);
		timestampTV = (TextView) findViewById(R.id.timestamp);
		voteCountTV = (TextView) findViewById(R.id.voteCount);
		distanceTV = (TextView) findViewById(R.id.distance);
		content = (ImageView) findViewById(R.id.picture);
		content.setScaleType(ScaleType.CENTER_CROP);
		
		// Dividing by 1000L to get time in seconds
//		Log.w("PV SETUP","tstmp: " + Double.toString(timestamp));
//		double timeSince = (System.currentTimeMillis()/1000L - timestamp);
//		Log.w("PV SETUP", "timeSINCE: " + Double.toString(timeSince));
		timestampTV.setText(properTime());
		bindButtons();
		
		voteCountTV.setText(Integer.toString(upvote - downvote));
		// Set image
		if(bitmap != null){
			content.setImageBitmap(bitmap);
		}
		// Set caption
		if(captionString != null && captionString != ""){
			setCaption(captionString);
		}
		else{
			setCaption("POOOOOOOOOOST POOOOOOOOST POOOOOOST");
		}
	}
	
	public void setLocation(Location location){
		float[] distanceFromPost = new float[1];
		Location currentLocation = location;
		currentLocation
			.distanceBetween(currentLocation.getLatitude(), 
							 currentLocation.getLongitude(), 
							 postLocation.getLat(), 
							 postLocation.getLon(), 
							 distanceFromPost);
		DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.0");
		double distanceInMiles = distanceFromPost[0]/1609.3;
		double dd2dec = Double.valueOf(df2.format(distanceInMiles));
		BigDecimal bd = new BigDecimal(distanceInMiles);
		bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		distanceInMiles = bd.doubleValue();
		distanceTV.setText(Double.toString((dd2dec)) + " miles");
	}
	
	public String getLocationStamp(){
		return (String) distanceTV.getText();
	}
	
	// takes the time (in milliseconds) difference between the current time and the timestamp of the post
	// returns the appropriate timestamp string--___minutes ago, ___ hours ago, ____ days ago...
	public String properTime(){
		double timeSince = System.currentTimeMillis()/1000L - timestamp;
		double minuteInSeconds = 60; // 60 seconds
		double hourInSeconds = 60 * minuteInSeconds;
		double dayInSeconds = 24 * hourInSeconds;
		// just say a minute ago if it was posted less than 60 seconds ago
		if(timeSince < minuteInSeconds){
//			Log.w("PROPER TIME", "Time is in seconds");
			return "a minute ago";
		}
		
		// if it's been less than an hour, return the timestamp in minutes
		if(timeSince < hourInSeconds/2){
//			Log.w("PROPER TIME", "Time is in minutes" );
			double timeInMins = timeSince/minuteInSeconds;
			BigDecimal bd = new BigDecimal(timeInMins);
			bd.setScale(0, BigDecimal.ROUND_UP); // round up to the next whole number.
			timeInMins = bd.doubleValue();
			if(timeInMins > 1)
				return Integer.toString((int) timeInMins) + " minutes ago";
			else
				return "a minute ago";
		}
		
		// if it's been less than a day return the timestamp in hours
		if(timeSince < dayInSeconds*.75){
//			Log.w("PROPER TIME", "Time is in hours" );

			double timeInHours = timeSince/hourInSeconds;
			BigDecimal bd = new BigDecimal(timeInHours);
			bd.setScale(0, BigDecimal.ROUND_UP);
			timeInHours = bd.doubleValue();
			if(timeInHours > 1)
				return Integer.toString((int) timeInHours) + " hours ago";
			else
				return Integer.toString((int) timeInHours) + " hour ago";
		}
		else{ // the time since is more than a day, get the timestamp in days
//			Log.w("PROPER TIME", "Time is in days");
			double timeInDays = timeSince/dayInSeconds;
//			Log.w("PROPER TIME", "TimeSINCE " + Double.toString(timeInDays));
			BigDecimal bd = new BigDecimal(timeInDays);
			// but round down because people don't round up 1 day and 12 hours to 2 days
			bd.setScale(0, BigDecimal.ROUND_UP); 
			timeInDays = bd.doubleValue();
			if (timeInDays < 1)
				timeInDays = 1;
			if(timeInDays > 1)
				return Integer.toString((int) timeInDays) + " days ago";
			else
				return Integer.toString((int) timeInDays) + " day ago";
		}
	}
	
	public void setCaption(String caption){
		this.captionTV.setText(caption);
		this.caption = caption;
	}
	
	public String getCaption(){
		return (String) captionTV.getText();
	}
	
	public void setPicture(Bitmap bm){
		content.setImageBitmap(bm);
		invalidate();
	}
	
	public void setPicture(Drawable d){
		content.setImageDrawable(d);
		invalidate();
	}
	
	public void setPicture(int drawableId){
		content.setImageDrawable((getResources().getDrawable(drawableId)));
	}
	
	public void setImageURL(String imageurl){
		this.imageURL = imageurl;
	}
	public Drawable getPicDrawable(){
		return content.getDrawable();
	}
	
	public String getImageURL(){
		return imageURL;
	}
	
	public PostParcel getPostParcel(){
		PostParcel pp = new PostParcel();
		return null;
	}
	
	public ImageView getImageView(){
		return content;
	}
	// returns: 
	// 1 if the user has upvoted the post
	// -1 if the user has downvoted the post
	// 0 if the user has neither upvoted or downvoted
	public int getVoteValue(){
		Log.w("POSTVIEW", "Votevalue is " + voteValue);
		return voteValue;
	}
	// returns the total number of upvotes minus the total number of downvotes for the content.
	public int getNetVote(){
		return upvoteCount - downvoteCount;
	}
	private void bindButtons(){
		upVote = (ImageButton) findViewById(R.id.upvote);
		upVote.setImageResource(R.drawable.arrohead_up);
		upVote.setTag(R.drawable.arrohead_up);
		downVote = (ImageButton) findViewById(R.id.downvote);
		downVote.setImageResource(R.drawable.arrohead_down);
		downVote.setTag(R.drawable.arrohead_down);
		moreOptions = (Button) findViewById(R.id.more_options);
		setListeners();
	}
	
	private void setListeners(){
		//General pattern for quick button listeners: upVote, downVote
		//Check the current tag--switch the image resource from plain to red/green/yellow. Or RGY to plain
		//Change the tag to match the image resource id
		//invalidate to force a redraw.
		upVote.setClickable(true);
		upVote.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Log.w("PostView", "UPVOTE CLICKED!!");
				// If the upVote button is currently white, make it green. Otherwise it's green so make it white
				if(upVote.getTag().equals(R.drawable.arrohead_up)){
					voteValue = UPVOTE;
					upVote.setImageResource(R.drawable.arrohead_up_green);
					upVote.setTag(R.drawable.arrohead_up_green);
					upvoteCount += 1;
					// TODO: Update the total vote (+1) and tell the server what just happened.
					if(downVote.getTag().equals(R.drawable.arrohead_down_red)){//if the downvote is red when you upvote...
						downVote.setImageResource(R.drawable.arrohead_down);//also set the red back to white
						downVote.setTag(R.drawable.arrohead_down);
						// you're taking away a downvote now and adding an upvote
						downvoteCount -=1;
						// TODO: Update total vote (+1 more) and tell server.
					}
				}
				else{//If it's anything else, then quick up must be green, so make it white
					// TODO: Update total vote (-1) and tell server user isn't on vote list any more.
					voteValue = NEUTRAL;
					upVote.setImageResource(R.drawable.arrohead_up);
					upVote.setTag(R.drawable.arrohead_up);
					upvoteCount -= 1;
				}
				//then force a redraw
				voteCountTV.setText(Integer.toString(upvoteCount - downvoteCount));
				getVoteValue();
				invalidate();
			}
		});
		
		downvoteListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w("PostView", "DOWNVOTE CLICKED!!");
				//If the upVote button is currently white, make it green. Otherwise it's green so make it white
				if(downVote.getTag().equals(R.drawable.arrohead_down)){
					voteValue = DOWNVOTE;
					downVote.setImageResource(R.drawable.arrohead_down_red);
					downVote.setTag(R.drawable.arrohead_down_red);
					// you added a downvote
					downvoteCount += 1;
					// if upvote is green...
					if(upVote.getTag().equals(R.drawable.arrohead_up_green)){
						upVote.setImageResource(R.drawable.arrohead_up);
						upVote.setTag(R.drawable.arrohead_up);
						// you took away an upvote
						upvoteCount -= 1;
					}
				}
				else{ // the downvote button must be red
					// take away a downvote
					voteValue = NEUTRAL;
					downvoteCount -= 1;
					downVote.setImageResource(R.drawable.arrohead_down);
					downVote.setTag(R.drawable.arrohead_down);
				}
				voteCountTV.setText(Integer.toString(upvoteCount - downvoteCount));
				getVoteValue();
				// TODO: Tell the server your updated vote status for this post
				invalidate();
				
			}
		};
		downVote.setClickable(true);
		downVote.setOnClickListener(downvoteListener);
	}
	public void pushUpVote(){
		if(voteValue == DOWNVOTE){
			downvoteCount -=1;
		}
		voteValue = UPVOTE;
		upvoteCount +=1;
	}
	
	public void pushDownVote(){
		if(voteValue == UPVOTE){
			upvoteCount -=1;
		}
		downvoteCount +=1;
		voteValue = DOWNVOTE;
	}
	public void pushNeutral(){
		// if the vote was non-neutral before, change the vote count in the appropriate direction
		if(voteValue == DOWNVOTE){
			downvoteCount -=1;
		}
		else if(voteValue == UPVOTE){
			upvoteCount -=1;
		}
		voteValue = NEUTRAL;
	}
	
	// turns the data associated with this view into an aleppoPost
	public AleppoPost toAleppoPost(){
		return new AleppoPost(ID, timestamp, postLocation, caption, mediaID, upvoteCount, downvoteCount);
	}
}
