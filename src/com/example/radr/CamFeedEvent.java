package com.example.radr;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.radr.adapter.CamFeedEventsAdapter;
import com.example.radr.backend.network.ServerPoller;
import com.example.radr.fragment.CaptureFragment;
import com.example.radr.fragment.CaptureFragment.PostManager;
import com.example.radr.fragment.EventFeedFragment;
import com.example.radr.fragment.EventFragment;
import com.example.radr.fragment.FeedFragment;
import com.example.radr.util.LocationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class CamFeedEvent extends FragmentActivity implements 
	PostManager, LocationGrabber, GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{
	
	private ViewPager viewPager;
	private CamFeedEventsAdapter mAdapter;
    private final static int
    	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    // Set to true in LocationHandlerSuccess. Set false if activity doesn't need updates. 
    //private boolean mUpdatesRequested;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    ImageView splashscreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//RadarDatabase.setContext(this);
		ServerPoller.startPolling(this);
		setContentView(R.layout.activity_cam_feed_event);
		splashscreen = (ImageView) findViewById(R.id.splashscreen);
		mAdapter = new CamFeedEventsAdapter(getSupportFragmentManager());
		
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Note that location updates are off until the user turns them on
        //mUpdatesRequested = false;
		
        mLocationClient = new LocationClient(this, this, this);
        // Open Shared Preferences
        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Get an editor
        mEditor = mPrefs.edit();
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cam_feed_event, menu);
		return true;
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
        	// TODO: show a dialog fragment that will say you need to turn on location to make this thing work
        	// If they say yes, send them to Location Settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        mLocationClient.connect();
    }
	@Override
	protected void onPause(){
		super.onPause();
		// Save current settings for updates
        //mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
       // mEditor.commit();
	}
	
	@Override
	protected void onResume(){
        super.onResume();
        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            //mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

        // Otherwise, turn off location updates until requested
        } else {
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }
	}
	
    @Override
    protected void onStop() {
    	// If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }
	
    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        // Let the user know you started getting periodic updates
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        // Maybe do something with the UI?
    }
	
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, "Found google play services!");

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }
    
	// Switches the screen's focus to the fragment at the given position.
	public void switchToFragment(int position){	
		viewPager.setCurrentItem(position);
	}
	
	// Used for both the google play services' location client
	// AND for selecting an image from the gallery (that's what the super call is for)
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
    	// 
        super.onActivityResult(requestCode, resultCode, data); 
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }
        }
     }
	
	@SuppressWarnings("deprecation")
	@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showDialog(connectionResult.getErrorCode());
        }
    }

	// Do not launch the app's screens until connected to google play services
	@Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(mAdapter);
		// start on the capture fragment
		switchToFragment(1);
		splashscreen.setVisibility(View.GONE);
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onLocationChanged(Location location) {
	      mCurrentLocation = location;		
	}

	/**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    // ==========POST MANAGER METHODS============
	// TODO: Make this method send the postParcel to the server and retrieve a new postParcel with URL
	// rather than byte[] for the image.
	public void addPost(AleppoPost ap){
		Log.w("--addPost", "addPost called in CamFeedEvent!!");
		FeedFragment ff = (FeedFragment) mAdapter.getItem(1); 
		ff.addPost(ap);
	}
	
    // Returns the most recent location if connected to Google Play services
	@Override
	public Location getLocation() {
	    mCurrentLocation = mLocationClient.getLastLocation();
//		if(servicesConnected()){
//	        Toast.makeText(this,
//	        			   "Lat: " + mCurrentLocation.getLatitude() +
//	        			   "Long: " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//		}
		return mCurrentLocation;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
	   super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState){
	   super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public File getPostIdFile() {
		// TODO Auto-generated method stub
		return null;
	}
	public void backToEventFeed(){
 	   Fragment fragment = (getSupportFragmentManager().findFragmentById(R.id.mapwhere));   
 	   FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
 	   ft.remove(fragment);
 	   ft.commit();
	   EventFragment ef = (EventFragment) mAdapter.getItem(0);
	   switchToFragment(0);
	   ef.onEventCreated();
	}
	
	// called by the Create Event Fragment when it's going to launch the "take picture" interface for events
	public void launchEventPic(){
		viewPager.setClickable(false);
		CaptureFragment captureFrag = (CaptureFragment) mAdapter.getItem(1);
		captureFrag.othersWantCamera();
		captureFrag.onPause();
	}
	
	public void doneEventPic(){
		CaptureFragment captureFrag = (CaptureFragment) mAdapter.getItem(1);
		captureFrag.othersDoneWithCamera();
		captureFrag.onResume();
		viewPager.setClickable(true);
	}
	
	// Called by create event fragment to handle transitions between it and the event feed fragment
	// returns the event feed fragment found in the event fragment
	public EventFeedFragment getEventFeedFragment(){
		EventFragment ef = (EventFragment) mAdapter.getItem(0);
		return ef.getEventFeedFragment();
	}
	public void createEventLaunched(){
		EventFragment ef = (EventFragment) mAdapter.getItem(0);
		ef.launchCreateEvent();
	}
}
