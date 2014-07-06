package com.example.radr.fragment;

import java.io.ByteArrayOutputStream;

import com.example.radr.CamFeedEvent;
import com.example.radr.CameraPreview;
import com.example.radr.R;
import com.example.radr.fragment.CaptureFragment.PostManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

public class CaptureEventPicFragment extends Fragment implements Camera.PictureCallback {
	Activity mActivity;
	
	// Camera interface
	Camera mCamera;
	CameraPreview mPreview;
	View rootView;
	ImageButton retake; // allows the user to retake the picture
	ImageButton capture;
	FrameLayout preview;
	Bitmap pic;
	byte[] mPicData;
	int screenHeight;
	int screenWidth;
	int actionBarPixelCount;
	int statusBarHeightPixels;
	int topBarPixelCount; // this is status bar + "Add Event" bar
	int captureBarHeightPixels;
	
	boolean captureMode = true;
	
	// Caption image interface parts
	FrameLayout capturedPreview;
	PostManager postManager;
	LinearLayout captureBar;
	private final String TAG = "CaptFrag";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		rootView = inflater.inflate(R.layout.fragment_capture_event_pic, container, false);
		retake = (ImageButton) rootView.findViewById(R.id.to_manager);
		captureBar = (LinearLayout) rootView.findViewById(R.id.capture_row);
		capture = (ImageButton) rootView.findViewById(R.id.capture);
		capturedPreview = (FrameLayout) rootView.findViewById(R.id.caption_picture_preview);
		preview = (FrameLayout) rootView.findViewById(R.id.picture_preview);
		LayoutParams params = captureBar.getLayoutParams();
		params.height = captureBarHeight();
		setListeners();
		return rootView;
    }
	
	private void setListeners(){
		// Clicking back button when
		retake.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// This is the back button
				if(!captureMode){
					startCamera();
					preview.setVisibility(View.VISIBLE);
					capturedPreview.setVisibility(View.INVISIBLE);
					capture.setImageResource(R.drawable.circle);
					retake.setVisibility(View.INVISIBLE);
					captureMode = true;
				}
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
        	        	}
        	        	else{
        	        		mCamera.release();
        	        		mCamera = null;
        	        		if(pic != null){
            	        		pic.recycle();
        	        		}
        	        		CreateEventFragment daddy = (CreateEventFragment) getParentFragment();
        	        		daddy.onPicTaken(mPicData);
        	        	}
        					// we're storing timestamps in seconds
        					//get timestamp
        					// Add the post to my feed.
        					//postManager.addPost(ap);
        					// Get the layout back to captureMode
        	        	}
        	    });
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
		actionBarPixelCount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
		statusBarHeightPixels = getStatusBarHeight();
		topBarPixelCount = statusBarHeightPixels + actionBarPixelCount;
		int totalHeight = screenHeight - topBarPixelCount - screenWidth;
		captureBarHeightPixels = totalHeight;
		return totalHeight;
	}
	// Only start up the camera if we're in capture picture mode
	@Override
    public void onResume(){
		super.onResume();
		startCamera();
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
		}
		mCamera.startPreview();
	}
	
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	        Log.w("EVENT PIC CAMERA", c.toString());
	    }
	    catch (Exception e){
	    	Log.e("EVENT PIC CAMERA", e.toString());
	    }
	    return c; // returns null if camera is unavailable
	}

    @Override
	public void onStop(){
    	super.onStop();
    	if(mCamera != null){
    		mCamera.release();
    		mCamera = null;
    	}
    	if(mPreview != null){
    		mPreview.getHolder().removeCallback(mPreview);
    		mPreview = null;
    	}
    }
    
    @Override
	public void onPause(){
    	super.onPause();
    	if(mCamera != null){
    		mCamera.release();
    		mCamera = null;
    	}
    	mPreview.getHolder().removeCallback(mPreview);
    	if(mPreview != null){
    		mPreview = null;
    	}
    }
    
    private PictureCallback mPicture = new PictureCallback() {
	    @SuppressLint("NewApi")
		@Override
	    public void onPictureTaken(byte[] data, Camera camera) {
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
            captureMode = false;
            Matrix matrix = new Matrix();
			matrix.postRotate(90);
			pic = Bitmap.createBitmap(pic, topBarPixelCount, 0, 
									  pic.getHeight(), pic.getHeight(),
									  matrix, true);
	    	ImageView imgView = new ImageView(getActivity());
	    	imgView.setImageBitmap(pic);
	    	// padding to make sure that picture preview fits perfectly within bounds.
	    	imgView.setPadding(0, topBarPixelCount, 0, captureBarHeightPixels);
	    	capturedPreview.addView(imgView);
	    	imgView.setScaleType(ScaleType.CENTER_CROP);
	    	// Make the camera preview invisible, reveal the caption picture interface.
        	preview.setVisibility(View.INVISIBLE);
        	//get the buttons ready for the pre-post interface
        	capture.setImageResource(R.drawable.postbutton_postable);
			retake.setVisibility(View.VISIBLE);
        	capturedPreview.setVisibility(View.VISIBLE);
//            Parameters params = mCamera.getParameters();
//            Camera.Size size = params.getPreviewSize();
//			Log.w("PREVIEWSIZE", 
//					"Preview is H: " + (size.height)
//					+ " and W: '" + (size.width));
    		// otherwise it's going to be posting a picture
				// Convert the scaled bitmap into a byte array 
				// and set that as the pic data for the Post Parcel
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
			mPicData = stream.toByteArray();
	    }
	};
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
        mPicData = new byte[data.length];
        mPicData = data;
        camera.release();
	}
}
