package com.example.radr.fragment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.radr.AleppoEvent;
import com.example.radr.AleppoLocation;
import com.example.radr.CamFeedEvent;
import com.example.radr.LocationGrabber;
import com.example.radr.R;
import com.example.radr.backend.AsyncBackendWriter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// Adapted from: http://stackoverflow.com/questions/15433820/mapfragment-in-fragment-alternatives
public class CreateEventFragment extends SupportMapFragment {
	// member variables for the map interface
	LinearLayout mapInterface;
	EditText eventAddress;
	ImageButton backToCreate;
	LocationGrabber lg;
	SupportMapFragment mSupportMapFragment;
	GoogleMap map;
	LatLng myLatLng;
	Marker eventMarker;
	
	// Member variables for the create event interface
	ScrollView createEventInterface;
	EditText eventTitle;
	EditText eventDetails;
	TextView eventDate;
	TextView eventStart;
	TextView eventEnd;
	TextView eventLocation;
	Button createEvent;
	Button cancel;
	LinearLayout chooseImageInterface;
	RelativeLayout imageInterface;
	Button takePicture;
	Button chooseExisting;
	ImageView eventPicture;
	ImageButton backToEventFeed;
	
    DatePickerDialog mDatePicker;                
	TimePickerDialog endTimePicker;
    TimePickerDialog startTimePicker;
	
	FrameLayout eventPicContainer;
	CaptureEventPicFragment eventPic;
	AQuery aq;
	
	private String title = "";
	private String year = "";
	private String month = "";
	private String day = "";
	private String sHour = "";
	private String sMinute = "";
	private String eHour = "";
	private String eMinute = "";
	private byte[] mPicData;
	private final double METERS_PER_MILE = 1609.34;
	private static int RESULT_LOAD_IMAGE = 1;
		
    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
      super.onCreateView(inflater, container, savedInstanceState);
      View rootView = inflater.inflate(R.layout.fragment_create_event, null, false);
      mapInterface = (LinearLayout) rootView.findViewById(R.id.map_interface);
      createEventInterface = (ScrollView) rootView.findViewById(R.id.interface_create_event);
      chooseImageInterface = (LinearLayout) rootView.findViewById(R.id.choose_event_image_interface);
      imageInterface = (RelativeLayout) rootView.findViewById(R.id.event_image_interface);
      eventStart = (TextView) rootView.findViewById(R.id.event_start);
      eventEnd = (TextView) rootView.findViewById(R.id.event_end);
      eventDate = (TextView) rootView.findViewById(R.id.event_date);
      eventLocation = (TextView) rootView.findViewById(R.id.event_location);
      backToCreate = (ImageButton) rootView.findViewById(R.id.back_to_create);
      createEvent = (Button) rootView.findViewById(R.id.create_button);
      cancel = (Button) rootView.findViewById(R.id.cancel_button);
      takePicture = (Button) rootView.findViewById(R.id.take_picture);
      chooseExisting = (Button) rootView.findViewById(R.id.choose_existing);
      eventPicture = (ImageView) rootView.findViewById(R.id.event_image);
      eventTitle = (EditText) rootView.findViewById(R.id.event_name);
      eventDetails = (EditText) rootView.findViewById(R.id.event_details);
      backToEventFeed = (ImageButton) rootView.findViewById(R.id.toEventFeed);
      eventPicContainer = (FrameLayout) rootView.findViewById(R.id.eventpic_container);
      eventAddress = (EditText) rootView.findViewById(R.id.event_address_map);
      lg = (LocationGrabber) getActivity();
      aq = new AQuery(getActivity());
      return rootView;
     }
    
    @Override
    public void onActivityCreated(Bundle bundle){
    	super.onActivityCreated(bundle);
    	setUpAddressBar();
        mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapwhere);
        if (mSupportMapFragment == null) {
        	FragmentManager fragmentManager = getFragmentManager();
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	mSupportMapFragment = SupportMapFragment.newInstance();
        	fragmentTransaction.replace(R.id.mapwhere, mSupportMapFragment).commit();
           }
        if (mSupportMapFragment != null){
        	map = mSupportMapFragment.getMap();
        	map.clear();
            // set the map's focus on the current location
      	   	Location myLocation = lg.getLocation();
      	   	myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            map.setMyLocationEnabled(true);
         	// create bounds--1 mile in either direction of the current location.
         	// 1 mile ~= .01666 in map degrees.
         	CameraPosition cameraPosition = new CameraPosition.Builder()
             	.target(myLatLng)
             	.zoom(17)
             	.build();
         	map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
         	// draw the "mylocation marker" and set it on the map
         	int px = getResources().getDimensionPixelSize(R.dimen.mylocationmarker_dimens);
         	Bitmap myLocationMarkerBm = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
         	Canvas canvas = new Canvas(myLocationMarkerBm);
         	Drawable shape = getResources().getDrawable(R.drawable.mylocationmarker);
         	shape.setBounds(0, 0, myLocationMarkerBm.getWidth(), myLocationMarkerBm.getHeight());
         	shape.draw(canvas);
        	BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(myLocationMarkerBm);
        	map.addMarker(new MarkerOptions()
        						.position(myLatLng)
        						.icon(icon));
         	map.setOnCameraChangeListener(getCameraChangeListener());
         }
    }
    
    private void setUpAddressBar(){
    	setCreateInterfaceListeners();
    	setMapListeners();
    	setTransitions();
    	setPickers();
    }
    
    private void setPickers(){
        Calendar mCalendar = Calendar.getInstance();
        int mYear = mCalendar.get(Calendar.YEAR);
        int mMonth = mCalendar.get(Calendar.MONTH);
        int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        mDatePicker = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                // Convert the month number into a readable name
                String monthString;
                if(selectedmonth == 1)
                	monthString = "January";
                else if(selectedmonth == 2)
                	monthString = "February";
                else if(selectedmonth == 3)
                	monthString = "March";
                else if(selectedmonth == 4)
                	monthString = "April";
                else if(selectedmonth == 5)
                	monthString = "May";
                else if(selectedmonth == 6)
                	monthString = "June";
                else if(selectedmonth == 7)
                	monthString = "July";
                else if(selectedmonth == 8)
                	monthString = "August";
                else if(selectedmonth == 9)
                	monthString = "September";
                else if(selectedmonth == 10)
                	monthString = "October";
                else if(selectedmonth == 11)
                	monthString = "November";
                else
                	monthString = "December";
                eventDate.setText(monthString + " " + selectedday);
                month = monthString;
                if(selectedday < 10){
                	day = "0" + selectedday;
                }
                else{
                    day = Integer.toString(selectedday);
                }
                year = Integer.toString(selectedyear);
            }
        }, mYear, mMonth, mDay);
        try {
            Field[] fields = mDatePicker.getClass().getDeclaredFields();
            for (Field field : fields) {
        		Log.w("DatePicker", "Checking field: " + field.getName());
            	if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) { 
            		Log.w("DatePicker", "FOUND YEAR PICKER FIELD");
            	    field.setAccessible(true);
                    Object yearpicker = new Object();
                    yearpicker = field.get(mDatePicker);
                    ((View) yearpicker).setVisibility(View.GONE);
                }
            }
        }
        catch (SecurityException e) {
            Log.d("ERROR", e.getMessage());
        } 
        catch (IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        } 
        catch (IllegalAccessException e) {
            Log.d("ERROR", e.getMessage());
        }
        // get rid of the year field.
        mDatePicker.setTitle("Date");
        startTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	String startTime= "Starts ";
            	String startMinute= "";
            	String startHour = "";
            	// set the start minutes and hours (which will be used to construct the AleppoEvent)
            	if(selectedHour < 10){
            		sHour = "0" + selectedHour;
            	}
            	else{
            		sHour = Integer.toString(selectedHour);
            	}
            	if(selectedMinute < 10){ // min = 9 --> "09"
            		startMinute += "0" + selectedMinute;
            	}
            	else{
            		startMinute = Integer.toString(selectedMinute);
            	}
        		sMinute = startMinute;

            	if(selectedHour > 11){ //convert 24 hour time to 12-hour time
            		if(selectedHour==12){
            			startHour = "12 ";
            		}
            		else{ // trim the extra 12 hours for 1pm --> 11pm
            			startHour = Integer.toString(selectedHour- 12);
            		}
            		startTime += startHour + " : " + startMinute + " PM";
            	}
            	else{ // morning time
            		if(selectedHour == 0){
            			startTime += "12" + " : " + startMinute + " AM"; // 0 == 12AM
            		}
            		else{
            			startHour = Integer.toString(selectedHour);
            			startTime += startHour + " : " + startMinute + " AM";
            		}
            	} 
            	eventStart.setText(startTime);
            	// Set the end time automatically to 2 hours after the start time if it's blank.
            	// Do this to minimize total user interactions-- most events last about 2 hours...
            	int guessHour = selectedHour + 2;
            	if(guessHour < 10){
            		eHour = "0" + Integer.toString(guessHour);
            	}
            	else{
            		eHour = Integer.toString(guessHour);
            	}
            	eMinute = sMinute;
            	String guessEnd = "Ends ";
            	if(guessHour > 11){
            		if(guessHour==12){
            			guessEnd += guessHour + " : " + startMinute + " PM";
            		}
            		else{
            			guessEnd += guessHour -12 + " : " + startMinute + " PM";
            			
            		}
            	}
            	else{
            		guessEnd += guessHour + " : " + startMinute + " AM";
            	}
            	eventEnd.setText(guessEnd);
        		eventEnd.setTextColor(getResources().getColor(R.color.event_pre_text));
            }
        }, hour, minute, false);// false = NOT 24 hour time
        startTimePicker.setTitle("Starts");

        endTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	String endTime = "Ends ";
            	String endHour = "";
            	String endMinute = "";
            	if(selectedHour < 10){
            		eHour = "0" + selectedHour;
            	}
            	else{
            		eHour = Integer.toString(selectedHour);
            	}
            	if(selectedMinute < 10){
            		endMinute += "0" + selectedMinute;
            	}
            	else{
            		endMinute = Integer.toString(selectedMinute);
            	}
            	eMinute = endMinute;
            	
            	if(selectedHour > 11){ //convert 24 hour time to 12-hour time
            		if(selectedHour==12){
            			endHour = "12 ";
            		}
            		else{ // trim the extra 12 hours for 1pm --> 11pm
            			endHour = Integer.toString(selectedHour- 12);
            		}
            		endTime += endHour + " : " + endMinute + " PM";	
            	}
            	else{
            		if(selectedHour == 0) // 0 == 12AM
            			endTime += "12" + " : " + endMinute + " AM";
            		else // 
            			endTime += selectedHour + " : " + endMinute + " AM";
            	}
            	eventEnd.setText(endTime);
        		eventEnd.setTextColor(getResources().getColor(R.color.black));
            }
        }, hour, minute, false);// false = NOT 24 hour time
        endTimePicker.setTitle("Ends");
    }
    // sets the animation transitions for the two interfaces as they switch in and out.
    private void setTransitions(){
    	
    }
    
    // sets the listeners for the components of the map interface
    private void setMapListeners(){
    	backToEventFeed.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//send the user back to the event feed....
				backToEventFeed();
			}
    		
    	});
    	backToCreate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// Hide the soft keyboard if it's open
				if(eventAddress.getWindowToken() != null){
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(eventAddress.getWindowToken(), 0);
				}
				eventLocation.setText(eventAddress.getText());
				eventLocation.setTextColor(getResources().getColor(R.color.black));
				createEventInterface.setVisibility(View.VISIBLE);
				mapInterface.setVisibility(View.INVISIBLE);
			}
    	});
        eventAddress.setOnEditorActionListener(new OnEditorActionListener() {
    	    public boolean onEditorAction(TextView v, int keyCode, KeyEvent event){
    	        if(keyCode==EditorInfo.IME_ACTION_DONE){
    	        	Geocoder coder = new Geocoder(getActivity(), Locale.getDefault());
    	        	String userInput = ""; // string that will contain the street address
    	        	String cityName = "";
    	        	// Find the name of the city if it's available
    	        	cityName = getCity(coder);
    	        	Log.w("LOCATION FINDER", "CITY NAME: " + cityName);
    	        	List<Address> address;
    	        	// Ask the user to enter an address if they try to submit an empty one
    	        	if(eventAddress.getText().toString().trim().isEmpty()){
    	        		onEmptyAddress();
    	  	            return false;
    	        	}
    	        	else{ // if they have an actual address:
    	        		userInput = eventAddress.getText().toString() + ", ";
    	        		LatLng addressCoords = findAddressLocation(coder, userInput, cityName);
    	        		// if we find the address and it's nearby:
    	        		if(addressCoords != null){
    	                 	int px = getResources().getDimensionPixelSize(R.dimen.eventmarker_dimens);
    	                 	Bitmap myLocationMarkerBm = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
    	                 	Canvas canvas = new Canvas(myLocationMarkerBm);
    	                 	Drawable shape = getResources().getDrawable(R.drawable.myeventlocation);
    	                 	shape.setBounds(0, 0, myLocationMarkerBm.getWidth(), myLocationMarkerBm.getHeight());
    	                 	shape.draw(canvas);
    	                	BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(myLocationMarkerBm);

    	    	        	Log.w("LOCATION FINDER", "ADDRESS FOUND!!! " 
    	    	        						   + "CITY NAME: " + cityName
    	    	        						   + "Address Name: " + userInput+cityName
    	    	        						   + "Address coords: " + addressCoords.latitude
    	    	        						   + " " + addressCoords.longitude);
    	    	        	if(eventMarker != null){
    	    	        		eventMarker.remove();
    	    	        	}
    	        			eventMarker = 
    	        					map.addMarker(new MarkerOptions()
    	        									.position(addressCoords)
    	        									.icon(icon));
    	        			setBounds(addressCoords);
    	        		}
    	     	        return false;
    	        	}
    	        }
    	        return false;
    	    }
      });
    }
    
    private void setCreateInterfaceListeners(){
    	takePicture.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchTakePictureFragment();
			}
    	});
    	chooseExisting.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent i = new Intent(
    						Intent.ACTION_PICK,
    						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivityForResult(i, RESULT_LOAD_IMAGE);
    		}
    	});
    	eventDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // To show current date in the datepicker
                mDatePicker.show();
            }
        });

    	eventStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                startTimePicker.show();
			}
        });
    	
    	eventEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                endTimePicker.show();
			}
        }
    	);
    	eventLocation.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// when the user clicks the "where" field in the create event interface
				// take them to the map interface to figure out the proper location
				mapInterface.setVisibility(View.VISIBLE);
				createEventInterface.setVisibility(View.INVISIBLE);
			}
    		
    	});
    	createEvent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(eventFieldsCheckOut()){
					// tell the activity to send the user back to the event feed
					onEventCreated();
				}
				Log.w("CURRENT TIME", Long.toString(System.currentTimeMillis()));
				Log.w("START TIME", Long.toString(epochFromTime(startTime())));
				Log.w("END TIME", Long.toString(epochFromTime(endTime())));

			}
    	});
    	cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				backToEventFeed();
			}
    	});
    }
    
    private void launchTakePictureFragment(){
    	takePicture.setClickable(false);
    	// launch the take picture fragment
    	CamFeedEvent cfe = (CamFeedEvent) getActivity();
    	// tell the parent activity to lock the view pager and also 
    	cfe.launchEventPic();
    	eventPic = new CaptureEventPicFragment();
    	FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
    	transaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
    	transaction.add(R.id.eventpic_container, eventPic).commit();
    	eventPicContainer.setVisibility(View.VISIBLE);
    	createEventInterface.setVisibility(View.INVISIBLE);
    }
    
    public void onPicTaken(byte[] picData){
    	takePicture.setClickable(true);
    	createEventInterface.setVisibility(View.VISIBLE);
    	eventPicContainer.setVisibility(View.INVISIBLE);
    	FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
    	transaction.setCustomAnimations(R.anim.slide_right_to_left, R.anim.slide_left_to_right);
    	transaction.remove(eventPic).commit();
    	CamFeedEvent cfe = (CamFeedEvent) getActivity();
    	// tell the parent activity to unlock the view pager and hand back camera control 
    	cfe.doneEventPic();
    	mPicData = picData;
    	Bitmap bmap = BitmapFactory.decodeByteArray(mPicData , 0, mPicData.length);
    	aq.id(eventPicture).image(bmap);
    	imageInterface.setVisibility(View.VISIBLE);
    	imageInterface.toString();
    }
    
    private void onEventCreated(){
    	createEvent();
    	backToEventFeed();
    }
    
    // returns start time of event in MMM dd YYYY HH:mm format
    private String startTime(){
    	return month + " " + day + " " + year + " " + sHour + ":" + sMinute;
    }
    
    // returns end time of event in MMM dd YYYY HH:mm format
    private String endTime(){
    	return month + " " + day + " " + year + " " + eHour + ":" + eMinute;
    }
    
    private long epochFromTime(String time){
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd yyyy HH:mm");
		try {
	        Date date = df.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
    }
    
    private void createEvent(){
    	title = eventTitle.getText().toString();
    	AleppoEvent ae = new AleppoEvent(0, 
				System.currentTimeMillis(), 
				new AleppoLocation(myLatLng.latitude, myLatLng.longitude), 
				title, 
				"0", 
				epochFromTime(startTime()), 
				epochFromTime(endTime()));
    	// Convert date format into epoch
		AsyncBackendWriter.write(new AsyncBackendWriter.EventQuery(ae, mPicData));
    }
    
    private void backToEventFeed(){
    	FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
	    CamFeedEvent cfe = (CamFeedEvent) getActivity();
	    EventFeedFragment eFF = cfe.getEventFeedFragment();
	    // animation should have eventfeedfragment emerge from the bottom of the screen
	    ft.setCustomAnimations(R.anim.stay_put, R.anim.abc_slide_out_bottom);
	    ft.replace(R.id.fragmentcontainer, eFF).commit();
	    InputMethodManager imm = (InputMethodManager) cfe.getSystemService(
	    	      Context.INPUT_METHOD_SERVICE);
	    if(eventTitle.getWindowToken() !=null)
	    	imm.hideSoftInputFromWindow(eventTitle.getWindowToken(), 0);
	    if(eventDetails.getWindowToken() != null)
	    	imm.hideSoftInputFromWindow(eventDetails.getWindowToken(), 0);
	    if(eventAddress.getWindowToken() != null)
	    	imm.hideSoftInputFromWindow(eventAddress.getWindowToken(), 0);
	    if(eventLocation.getWindowToken() != null)
	    	imm.hideSoftInputFromWindow(eventLocation.getWindowToken(), 0);
	    mDatePicker.dismiss();
	    startTimePicker.dismiss();
	    endTimePicker.dismiss();
	    // change the title bar back to its original state
	    // and destroy the map fragment so that we can re-inflate if necessary
	    cfe.backToEventFeed();
    }
    
    private boolean eventFieldsCheckOut(){
    	// TODO: check that start time and end time are after current time
    	if(eventTitle.getText().toString().trim().isEmpty()){
    		showToast("What's the event called?");
    		return false;
    	}
    	if(eventDetails.getText().toString().trim().isEmpty()){
    		showToast("Give us some deets in the detail field");
    		return false;
    	}
    	if(eventLocation.getText().toString().trim().isEmpty()){
    		showToast("Add a nearby location!");
    		return false;
    	}
    	if(eventDate.getText().toString().trim().isEmpty()){
    		showToast("Set a day!");
    		return false;
    	}
    	if(eventStart.getText().toString().trim().isEmpty()){
    		showToast("When does it start?");
    		return false;
    	}
    	if(eventEnd.getText().toString().trim().isEmpty()){
    		showToast("When does it end?");
    		return false;
    	} // if both start and end times have data but the event ends before it starts: 
    	else if(epochFromTime(startTime()) > epochFromTime(endTime())){
    		showToast("An event can't end before it starts!");
    		return false;
    	}
    	else if(epochFromTime(startTime()) < System.currentTimeMillis() 
    			|| epochFromTime(endTime()) < System.currentTimeMillis()){
    		showToast("Events must start and end in the future!");
    		showToast("Starts: " + epochFromTime(startTime()));
    		Log.w("CRE-8 EVENT", "starts: " + epochFromTime(startTime()));
    		Log.w("CREATE EVENT", "ends: " + epochFromTime(endTime()));
    		return false;
    	} // else if the event starts more than 15 days from now:
    	else if(epochFromTime(startTime()) - System.currentTimeMillis() > 1296000000){
    		showToast("Sorry, events must occur within the next two weeks");
    		return false;
    	}
    	if(mPicData == null){
    		showToast("Event needs a picture!");
    		return false;
    	}
		return true;
    }
    
    private OnCameraChangeListener getCameraChangeListener(){
    	return new OnCameraChangeListener(){
			@Override
			public void onCameraChange(CameraPosition camera) {
				if(camera.zoom < 11){
		         	CameraPosition cameraPosition = new CameraPosition.Builder()
	             	.target(myLatLng)
	             	.zoom(11)
	             	.build();
					map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			}
    	};
    }
    
    private void onEmptyAddress(){
    	showToast("Enter an address");
    }
    
    private void showToast(String message){
		final Toast emptyAddress = 
  			  Toast.makeText(getActivity(), 
  					  message, Toast.LENGTH_SHORT);
  			emptyAddress.show();
  		Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 emptyAddress.cancel(); 
             }
          }, 1200);
    }
    private String getCity(Geocoder coder){
    	try {
    		String cityName = "";
    		List<Address> localityAddress = 
    				coder.getFromLocation(myLatLng.latitude, myLatLng.longitude, 1);
    		if (localityAddress.size() > 0){
    			cityName = localityAddress.get(0).getLocality();
    		}
    		return cityName;
		} catch (IOException e1) {
			e1.printStackTrace();
			return "";
		}
    }
    
    // TODO: Dialog for address too far does not work properly.
    private LatLng findAddressLocation(Geocoder coder, String userInput, String cityName){
    	// try to get the address based on the user input and the city name if it was available
    	try {
    		List<Address> addresses;
			addresses = coder.getFromLocationName(userInput, 5);
    	    // tell the user to enter a valid address if the current address is null :
    	    if (addresses == null) {
    	    	showAddressNotFoundDialog();
    	    	return null;
    	    }
    	    else{
        	    for(int i = 0; i < addresses.size(); i++){
        	    	Address address = addresses.get(i);
        	    	address.getLongitude();
        	    	float[] distFromMe = new float[1];
        	    	Location.distanceBetween(myLatLng.latitude, 
        	    							 myLatLng.longitude, 
        	    							 address.getLatitude(), 
        	    							 address.getLongitude(), 
        	    							 distFromMe);
        	    	if(distFromMe[0] > 4.00 * METERS_PER_MILE){
        	    		addresses.remove(i);
        	    	}
        	    }
        	    // There were addresses but none were nearby
        	    if(addresses.isEmpty()){
        	    	showAddressTooFarDialog();
        	    	return null;
        	    }
        	    else{ // there is an address
        	    	return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        	    }
    	    }
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

    }
    
    private void showAddressTooFarDialog(){
    	AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    	alertDialog.setTitle("Whoa");
    	alertDialog.setMessage("That's too far away. Think closer");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog,int which) {
    			dialog.dismiss();
    		}
        });
		alertDialog.show();
    }
    
    private void showAddressNotFoundDialog(){
    	AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    	alertDialog.setTitle("Whoa!");
    	alertDialog.setMessage("Couldn't find that address. Maybe we've never seen it before?");
        alertDialog.setButton(1, "Go back", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog,int which) {
        	// GO BACK TO THE MAP
    		}
        });
		alertDialog.setButton(2, "Point it out", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// CREATE A DRAGGABLE MARKER
			}
		});
		alertDialog.show();
    }
    
    private void setBounds(LatLng eventLocation){
    	// create bounds--1 mile in either direction of the current location.
    	// 1 mile ~= .01666 in map degrees.
    	LatLngBounds.Builder builder = LatLngBounds.builder();
    	builder.include(myLatLng);
    	builder.include(eventLocation);
    	// markers, all 1 mile away from point
    	// 1 mile = .013671
    	LatLngBounds bounds = builder.build();
    	int paddingPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
    													   60, 
    													   getResources().getDisplayMetrics());
    	map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, paddingPixels));
    }
    // For receiving and setting the image from the gallery
    @SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    	Log.w("ONACTIVITYRESULT","Made it back!!!!!!!!");
    	Log.w("ONACTIVITYRESULT","Made it back!!!!!!!!");
    	Log.w("ONACTIVITYRESULT","Made it back!!!!!!!!");
    	Log.w("ONACTIVITYRESULT","Made it back!!!!!!!!");
    	Log.w("ONACTIVITYRESULT","Made it back!!!!!!!!");

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            // Get the pic's path
        	Uri selectedImage = data.getData();
            
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            // turn the path into a square bitmap, cropped from the top left corner
            Bitmap bm = BitmapFactory.decodeFile(picturePath, null);
            int picHeight = bm.getHeight();
            int picWidth = bm.getWidth();
            if(picHeight < picWidth){
            	bm = bm.createBitmap(bm, 0, 0, picHeight, picHeight);
            }
            else{
            	bm = bm.createBitmap(bm, 0, 0, picWidth, picWidth);
            }
            //set the event picture to that bitmap
            AQuery aq = new AQuery(getActivity());
            aq.id(eventPicture).image(bm);
            chooseImageInterface.setVisibility(View.INVISIBLE);
            imageInterface.setVisibility(View.VISIBLE);
        }
    }
    
    public void onDestroyView() {
    	   super.onDestroyView();
    	   mDatePicker.dismiss();
   	   	   startTimePicker.dismiss();
   	   	   endTimePicker.dismiss();
    	   Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapwhere));   
    	   FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
    	   ft.remove(fragment);
    	   ft.commit();
    	}
    }