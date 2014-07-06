package com.example.radr.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.radr.AleppoLocation;
import com.example.radr.AleppoPost;
import com.example.radr.CamFeedEvent;
import com.example.radr.CameraPreview;
import com.example.radr.ChannelManager;
import com.example.radr.R;
import com.example.radr.backend.AsyncBackendWriter;

public class CaptureFragment extends Fragment {
	Activity mActivity;
	
	// Camera interface
	ImageView splash;
	Camera mCamera;
	CameraPreview mPreview;
	View rootView;
	ImageButton eventBack;
	Button capture;
	ImageButton toFeed;
	SurfaceHolder surfaceHolder;
	FrameLayout preview;
	Bitmap pic;
	ScrollView scrollWrapper;
	byte[] mPicData;
	int screenHeight;
	int screenWidth;
	int captionBarPixelCount;
	int captureBarHeightPixels;
	int statusBarHeightPixels;
	int topBarPixelCount; // this is status bar + captionbar
	
	boolean captureMode = true;
	
	// Caption image interface parts
	Button xOut;
	MultiAutoCompleteTextView addCaptions;
	String captionContents;
	Button postButton;
	LinearLayout captionBanner;
	String ACTIVITY_NAME = "CaptionImage";
	FrameLayout captionPreview;
	PostManager postManager;
	LinearLayout captureBar;

	
	private final String TAG = "CaptFrag";
	
	public interface PostManager {
        public void addPost(AleppoPost ap);
        public void switchToFragment(int position);
        public File getPostIdFile();
        public Location getLocation();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		rootView = inflater.inflate(R.layout.fragment_capture_content, container, false);
		splash = (ImageView) rootView.findViewById(R.id.capture_content_splash);
		eventBack = (ImageButton) rootView.findViewById(R.id.to_manager);
		captionBanner = (LinearLayout) rootView.findViewById(R.id.caption_image_dropdown);
		addCaptions = (MultiAutoCompleteTextView) rootView.findViewById(R.id.add_caption_bar);
		captureBar = (LinearLayout) rootView.findViewById(R.id.capture_row);
		capture = (Button) rootView.findViewById(R.id.capture);
		toFeed = (ImageButton) rootView.findViewById(R.id.to_feed);
		captionPreview = (FrameLayout) rootView.findViewById(R.id.caption_picture_preview);
		preview = (FrameLayout) rootView.findViewById(R.id.picture_preview);
		//scrollWrapper = (ScrollView) rootView.findViewById(R.id.previewScrollWrapper);
		Log.w("CAPTURE FRAG", "Attached the buttons");
		LayoutParams params = captureBar.getLayoutParams();
		params.height = captureBarHeight();
        try {
            postManager = (PostManager) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
		setListeners();
		return rootView;
    }
	
	private void setListeners(){
		// Clicking channels button sends you to the Channel Manager
		eventBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				  if(!captureMode){ // go from being the back button to the to-events button
						startCamera();
						// Hide the keyboard, if it's open.
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
							      Context.INPUT_METHOD_SERVICE);
						if(addCaptions.getWindowToken() != null)
							imm.hideSoftInputFromWindow(addCaptions.getWindowToken(), 0);
						//if(addChannels.getWindowToken() != null)
							//imm.hideSoftInputFromWindow(addChannels.getWindowToken(), 0);
						preview.setVisibility(View.VISIBLE);
						captionPreview.setVisibility(View.INVISIBLE);
						addCaptions.setText("");
						capture.setText("");
						eventBack.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.channel_icon));
						captureMode = true;
				  }
				  else{
  					  postManager.switchToFragment(3);
					  preview.removeView(mPreview);
					  if(mCamera != null){
						  mCamera.release();
					      mCamera = null;
					  }
				  }
				  
			}
		});
		
		// Clicking the "toFeed" button sends you to your feed
		toFeed.setOnClickListener(new View.OnClickListener() {
			@Override
			  public void onClick(View v) {//when you click more info it switches DDM's visibility
				  ((CamFeedEvent) getActivity()).switchToFragment(2);
				  preview.removeView(mPreview);
				  mCamera.release();
			      mCamera = null;
			      //take you to your gosh dang Feed
			  }
		});
		
		// Clicking capture takes the picture and opens up the caption image interface
		// and sets the capture image interface to invisible
		capture.setOnClickListener(
        	    new View.OnClickListener() {
        	        @Override
        	        public void onClick(View v) {
        	        	if(captureMode){ // if it's capture mode it's taking a picture
            	        	captureMode = false;
            	            mCamera.takePicture(null, null, mPicture);
            	            Parameters params = mCamera.getParameters();
            	            Camera.Size size = params.getPreviewSize();
            				Log.w("PREVIEWSIZE", 
            						"Preview is H: " + (size.height)
            						+ " and W: '" + (size.width));
        	        	}
    	        		// otherwise it's going to be posting a picture
        	        	else if(addCaptions.getText().toString() != null 
        	        			&& !addCaptions.getText().toString().trim().matches("")){ 
        	        		//make sure there's something to say
        	        		String caption = addCaptions.getText().toString();
        					//Intent intent = new Intent(getActivity(), PostShower.class);
        					// Convert the scaled bitmap into a byte array 
        					// and set that as the pic data for the Post Parcel
        					ByteArrayOutputStream stream = new ByteArrayOutputStream();
        					pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        					
        					byte[] picData = stream.toByteArray();
        					// we're storing timestamps in seconds
        					long timestamp = System.currentTimeMillis()/1000L;
        					AleppoPost ap = new AleppoPost(0, 
        												   timestamp, 
        												   new AleppoLocation(postManager.getLocation()),
        												   caption,
        												   "", 0,0);
        					//get timestamp
        					// Add the post to my feed.
        					//postManager.addPost(ap);
        					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
        						      Context.INPUT_METHOD_SERVICE);
        					if(addCaptions.getWindowToken() != null){
        						imm.hideSoftInputFromWindow(addCaptions.getWindowToken(), 0);
        					}
        					//postManager.switchToFragment(1);
        					// Get the layout back to captureMode
        					preview.setVisibility(View.VISIBLE);
        					captionPreview.setVisibility(View.INVISIBLE);
        					captureMode = true;
    						addCaptions.setText("");
    						capture.setText("");
    						eventBack.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.channel_icon));
    						Log.w("CAPTURE", "Just wrote post with timestamp " + timestamp + 
    								" and location: " + ap.location().toString() + 
    								" and caption " + ap.caption());
        					AsyncBackendWriter.write(new AsyncBackendWriter.PostQuery(ap, picData));
        					pic.recycle();
        					startCamera();
        				}
        	        	else{
        	        		final Toast toast = 
        	        				Toast.makeText(mActivity, "Caption before you post!", Toast.LENGTH_SHORT);
        	        			toast.show();
        	        		Handler handler = new Handler();
        	                handler.postDelayed(new Runnable() {
        	                   @Override
        	                   public void run() {
        	                       toast.cancel(); 
        	                   }
        	            }, 1200);
        	        	}
         	        }
        	    }
        	);
		Log.w("CAPTUREFRAG", "Set the listeners, too");
	}
	
	public int getStatusBarHeight() { 
	      int height = 0;
	      int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	      if (resourceId > 0) {
	          height = getResources().getDimensionPixelSize(resourceId);
	      } 
	      return height;
	} 
	
	
	// Returns the padding value necessary for the top and the bottom padding of the capture bar
	// Based on the padding necessary to create a square preview window for the camera.
	@SuppressLint("NewApi")
	private int captureBarHeight(){
		Resources r = getResources();
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
		captionBarPixelCount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
		statusBarHeightPixels = getStatusBarHeight();
		topBarPixelCount = statusBarHeightPixels + captionBarPixelCount;
		int totalHeight = screenHeight - topBarPixelCount - screenWidth;
		captureBarHeightPixels = totalHeight;
		return totalHeight;
	}
	// Only start up the camera if we're in capture picture mode
	@Override
    public void onResume(){
		super.onResume();
		startCamera();
		splash.setVisibility(View.GONE);
	}
	
	private void startCamera(){
		if(mCamera == null){
			mCamera = getCameraInstance();// Create an instance of Camera if there is one
		}
		// Create our Preview view and set it as the content of our activity.
		if(mPreview == null){
			mPreview = new CameraPreview(getActivity(), mCamera);
			preview.addView(mPreview);
			// the scrollView's dimensions will be the camera's supported size dimensions.
			// they're just a little bigger than the actual dimensions of the screen usually.
			// This shows the user the natural dimensions of the preview rather than the squished version
			// that they get by default.
//			Log.w("PREVIEWSIZE", "Preview height: " + mPreview.previewHeight() + " preview width: " + mPreview.previewWidth());
//			scrollWrapper
//				.setLayoutParams(new RelativeLayout.LayoutParams(mPreview.previewWidth(), mPreview.previewHeight()));
		}
	}
	
	public Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera unavailable (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}

    @Override
	public void onStop(){
    	super.onStop();
    	stopCamera();
    }
    
    private void stopCamera(){
    	if(mPreview != null){
        	mPreview.getHolder().removeCallback(mPreview);
    		mPreview = null;
    	}
    	if(mCamera != null){
    		mCamera.release();
    		mCamera = null;
    	}
    	if(captureMode){
    		addCaptions.setText("");
    		capture.setText("");
    	}
		preview.removeAllViews();
    }
    @Override
	public void onPause(){
    	super.onPause();
    	stopCamera();
    	captionPreview.removeAllViews();
    }
    
    private PictureCallback mPicture = new PictureCallback() {
	    @SuppressLint("NewApi")
		@Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	captionPreview.removeAllViews();
	    	// Turn the byte array into a bitmap,
	    	// and then scale it down to the ideal dimensions for the screen
	    	
	    	//Get the dimensions from the picture captured
	    	BitmapFactory.Options options = new BitmapFactory.Options();
	    	options.inJustDecodeBounds = true;
	    	BitmapFactory.decodeByteArray(data, 0, data.length, options);
	    	int imageHeight = options.outHeight;
	    	int imageWidth = options.outWidth;
	    	
	    	// Get the screen dimensions
	    	Display display = getActivity().getWindowManager().getDefaultDisplay();
	    	Point size = new Point();
	    	display.getSize(size);
	    	int screenWidth = size.x;
	    	int screenHeight = size.y;
	    	int inSampleSize = 1;
	    	// Resize the picture by the largest factor of 2 that will still fill the screen.
	    	// Comparing imageHeight with imageWidth because the image is taken in landscape,
	    	// and we want to display it in portrait.
	        if (imageHeight > screenWidth || imageWidth > screenHeight) {
	            final int halfHeight = imageHeight / 2;
	            final int halfWidth = imageWidth / 2;
	            //inSampleSize = 8;
	            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	            // height and width larger than the requested height and width.
	            while ((halfHeight / inSampleSize) > screenWidth
	                    && (halfWidth / inSampleSize) > screenHeight) {
	                inSampleSize *= 2;
	            }
	        }
	        options.inSampleSize = inSampleSize;
//	        Log.w("CAMERA", "inSampleSize: " + inSampleSize + " ScW " + screenWidth + ". imgW " + imageWidth);
//	        Log.w("CAMERA", "ScH " + screenHeight + " imgH " + imageWidth);
	        options.inJustDecodeBounds = false;
            pic = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//	        Log.w("CAMERA", "imgW postDownsample" + options.outWidth);
//	        Log.w("CAMERA", "imgH postDownsample" + options.outHeight);

            Matrix matrix = new Matrix();
			matrix.postRotate(90);
			pic = Bitmap.createBitmap(pic, topBarPixelCount, 0, 
									  pic.getHeight(), pic.getHeight(),
									  matrix, true);
	    	ImageView imgView = new ImageView(getActivity());
	    	imgView.setImageBitmap(pic);
	    	// padding to make sure that picture preview fits perfectly within bounds.
	    	imgView.setPadding(0, topBarPixelCount, 0, captureBarHeightPixels);
	    	captionPreview.addView(imgView);
	    	imgView.setScaleType(ScaleType.CENTER_CROP);
	    	// Make the camera preview invisible, reveal the caption picture interface.
        	preview.setVisibility(View.INVISIBLE);
        	//get the buttons ready for the pre-post interface
        	eventBack.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.undo));
        	capture.setTextColor(getResources().getColor(R.color.white));
        	capture.setText("Post");
        	captionPreview.setVisibility(View.VISIBLE);
        	stopCamera();
	    	//preview.removeAllViews();
	    	//intent.putExtra("photo", data);
	    	//startActivity(intent);
	    	//camera.release();
	    	//camera = null;
	    }
	};
	
//	@Override
//	public void onPictureTaken(byte[] data, Camera camera) {
//        mPicData = new byte[data.length];
//        mPicData = data;
//        camera.release();
//	}
	
	public void othersWantCamera(){
		if(mCamera != null){
			mCamera.release();
			mCamera = null;
		}
	}
	
	public void othersDoneWithCamera(){
		startCamera();
	}
}
