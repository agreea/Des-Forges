package com.example.radr.listitems;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.radr.AleppoEvent;
import com.example.radr.PostParcel;
import com.example.radr.R;
import com.google.android.gms.maps.model.LatLng;

public class EventView extends RelativeLayout{
	LayoutInflater inflater;
	Context mContext;
	ImageView content;
	
	TextView voteCountTV;
	
	TextView captionTV;
	TextView distanceTV;
	TextView timeUntilTV;
	Button moreOptions;
	ImageButton addToCalendar;
	
	private String imageURL;
	String caption;
	String distance;
	int voteCount;
	long timestamp;
	private long sTime;
	private long eTime;

	LatLng mLatLng;
	
	private final int UPVOTE = 1;
	private final int DOWNVOTE = -1;
	private final int NEUTRAL = 0;
	
	public EventView(Context context) {
		super(context);
		setUp(null,
			  null,
			  0,
			  null,
			  0,
			  0,0,
			  0,0);
	}
	
	public EventView(Context context, Bitmap bitmap, String caption,
			int voteCount, String imageURL, long timestamp){
		super(context);
		setUp(bitmap, 
			  caption, 
			  voteCount, 
			  imageURL,
			  timestamp,
			  0,0,
			  0,0);
	}
	
	public EventView(Context context, AleppoEvent ae){
		super(context);		
			setUp(null,
				  ae.caption(),
				  0,
				  ae.getMediaUrl(),
				  ae.getTimestamp(),
				  ae.getLat(),
				  ae.getLon(),
				  ae.getStartTime(),
				  ae.getEndTime());
	}
	
	private void setUp(Bitmap bitmap, String captionString, 
			int voteCount, String imageURL, long timestamp, double lat, double lon, long startTime, long endTime){
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.voteCount = voteCount;
		this.imageURL = imageURL;
		this.timestamp = timestamp;
		// Set the layout
		inflater.inflate(R.layout.event_as_polaroid, this, true);
		captionTV = (TextView) findViewById(R.id.caption);
		timeUntilTV = (TextView) findViewById(R.id.timestamp);
		content = (ImageView) findViewById(R.id.picture);			
		content.setScaleType(ScaleType.CENTER_CROP);
		bindButtons();
		mLatLng = new LatLng(lat, lon);
		// Set image
		if(bitmap != null){
			content.setImageBitmap(bitmap);
		}
		
		// Set caption
		if(captionString != null){
			captionTV.setText(captionString);
			this.caption = captionString;
		}
		else {
			captionTV.setText("EVENT EVENT EVENT EVENT!!!!!");
		}
		sTime = startTime;
		eTime = endTime;
	}
	
	// These methods set up the specific layouts needed for events and posts respectively.
	// Namely, events get a star.
	
	@SuppressWarnings("static-access")
	public void setLocation(Location location){
		float[] distanceFromPost = new float[1];
		Location currentLocation = location;
		currentLocation
			.distanceBetween(currentLocation.getLatitude(), 
							 currentLocation.getLongitude(), 
							 mLatLng.latitude, 
							 mLatLng.longitude, 
							 distanceFromPost);
		DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.0");
		double distanceInMiles = distanceFromPost[0]/1609.3;
		double dd2dec = Double.valueOf(df2.format(distanceInMiles));
		BigDecimal bd = new BigDecimal(distanceInMiles);
		bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		distanceInMiles = bd.doubleValue();
		distanceTV.setText(Double.toString((dd2dec)) + " miles");
	}

	
	public void setCaption(String caption){
		this.captionTV.setText(caption);
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
	
	
	private void bindButtons(){
		addToCalendar = (ImageButton) findViewById(R.id.add_to_calendar);
		addToCalendar.setTag(R.drawable.attendevent);
		moreOptions = (Button) findViewById(R.id.more_options);
		setListeners();
	}
	
	private void setListeners(){
		addToCalendar.setClickable(true);
		addToCalendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			// attend event is pre-click 0
			// attending event is after-click 0
				if(addToCalendar.getTag().equals(R.drawable.attendevent)){
					addToCalendar.setImageResource(R.drawable.attendingevent);
					addToCalendar.setTag(R.drawable.attendingevent);
				}
				else{
					addToCalendar.setImageResource(R.drawable.attendingevent);
					addToCalendar.setTag(R.drawable.attendingevent);
				}
				invalidate();
		}
	});
	}
}
