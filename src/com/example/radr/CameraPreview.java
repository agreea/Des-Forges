
package com.example.radr;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private final String TAG = "CamPreview:";
		private final double PREVIEW_SIZE_FACTOR = 1;
	    private SurfaceHolder mHolder;
	    private Camera mCamera;
	    private Context context;
	    private int previewHeight;
	    private int previewWidth;
	    public CameraPreview(Context context, Camera camera) {
	        super(context);
	        mCamera = camera;
	        this.context = context;
	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        // deprecated setting, but required on Android versions prior to 3.0
	       	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	       	surfaceCreated(mHolder);
	    }
	    @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	        	if(mCamera == null){
	        		mCamera = Camera.open();
	        	}
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
	            Configuration config = getResources().getConfiguration();
	        	if(config.orientation==config.ORIENTATION_PORTRAIT){
	        		mCamera.stopPreview();
	        		mCamera.setDisplayOrientation(90);
	        		mCamera.startPreview();
	        	}
	        	if(config.orientation==config.ORIENTATION_LANDSCAPE){
	        		mCamera.stopPreview();
	        		mCamera.setDisplayOrientation(180);
	        		mCamera.startPreview();
	        	}
	        } catch (IOException e) {
	            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
	    }
	    
	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder) {
			System.out.println("Releasing cam from " + "CapFrag.surfaceDestroyed");
	    	mCamera.release();
	    }
	    
	    @Override
	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }

	        // set preview size and make any resize, rotate or
	        // reformatting changes here

	        // start preview with new settings
	        try {
	            Configuration config = getResources().getConfiguration();
	        	if(config.orientation==config.ORIENTATION_PORTRAIT){
	        		mCamera.setDisplayOrientation(90);
	        	}
	        	if(config.orientation==config.ORIENTATION_LANDSCAPE){
	        		mCamera.setDisplayOrientation(180);
	        	}
	        	Parameters parameters = mCamera.getParameters();
	            //List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
	            Camera.Size previewSize = getOptimalPreviewSize();
	            parameters.setPreviewSize(previewSize.width, previewSize.height);
	            //parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
	            //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	            mCamera.setParameters(parameters);
	            Log.w(TAG, "Called set parameters, just fine");
	            Display display = ((WindowManager) 
	            		context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	            if(display.getRotation() == Surface.ROTATION_0) {                        
	                mCamera.setDisplayOrientation(90);
	            } else if(display.getRotation() == Surface.ROTATION_270) {
	                mCamera.setDisplayOrientation(180);
	            }
	            //mCamera.startPreview();

	            mCamera.setPreviewDisplay(mHolder);
	            mCamera.startPreview();
	        } catch (Exception e){
	            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
	    }
	    
	    // Returns the best preview size for the screen.
	    private Size getOptimalPreviewSize() {
	        Camera.Size result = null;
	        final Camera.Parameters parameters = mCamera.getParameters();
	        Log.i(TAG, "window width: " + getWidth() + ", height: " + getHeight());
	        for (final Camera.Size size : parameters.getSupportedPreviewSizes()) {
	            if (size.width <= getWidth() * PREVIEW_SIZE_FACTOR && size.height <= getHeight() * PREVIEW_SIZE_FACTOR) {
	                if (result == null) {
	                    result = size;
	                } else {
	                    final int resultArea = result.width * result.height;
	                    final int newArea = size.width * size.height;

	                    if (newArea > resultArea) {
	                        result = size;
	                    }
	                }
	    	        Log.i(TAG, "Using a specially selected supported size");
	            }
	        }
	        if (result == null) {
	            result = parameters.getSupportedPreviewSizes().get(0);
    	        Log.i(TAG, "Went with the first one");

	        }
	        Log.i(TAG, "Using PreviewSize: " + result.width + " x " + result.height);
	        previewHeight = result.height;
	        previewWidth = result.width;
	        return result;
	    }
	    
	    public int previewHeight(){
	    	return previewHeight;
	    }
	    
	    public int previewWidth(){
	    	return previewWidth;
	    }
	}
