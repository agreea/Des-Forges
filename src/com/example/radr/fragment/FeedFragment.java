package com.example.radr.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import br.com.kots.mob.complex.preferences.ComplexPreferences;

import com.example.radr.AleppoPost;
import com.example.radr.LocationGrabber;
import com.example.radr.PostParcel;
import com.example.radr.R;
import com.example.radr.adapter.FeedAdapter;
import com.example.radr.backend.AsyncRefresher;
import com.example.radr.backend.PostLoader;
import com.example.radr.listitems.PostView;
import com.example.radr.util.LogTags;
import com.example.radr.backend.AsyncRefresher.Interface;
import com.example.radr.backend.database.RadarDataHelper;
import com.example.radr.backend.network.Request;
import com.example.radr.backend.network.Message.Response;


public class FeedFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<AleppoPost>>, 
com.example.radr.backend.AsyncRefresher.Interface {
	FeedAdapter mFAdapter;
	Activity mActivity;
	View rootView;
	String[] projection;
	Button refresh;
	Button addPost;
	int LOADER_ID = 0;
	List<PostView> initialPosts = new ArrayList<PostView>();
	private final String TAG = "FFNL";
	LoaderManager lm;
	LocationGrabber lg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  mActivity = getActivity();
      mFAdapter = new FeedAdapter(mActivity);
      setListAdapter(mFAdapter);
      lg = (LocationGrabber) mActivity;
      //Declare 3 new posts with distinct pictures and captions. Add them to the list. //
	  ComplexPreferences complexPrefs = 
			  ComplexPreferences.getComplexPreferences(mActivity, "myPosts", Context.MODE_PRIVATE);
	  lm = getLoaderManager();
	  lm.initLoader(LOADER_ID, null, this);

      PostView alabama, alaska, california, colorado;
      if(complexPrefs.getObject("alabama", PostParcel.class) == null){
    	  PostParcel alabamaPP = new PostParcel();
    	  alabamaPP.setImageURL("http://s4.evcdn.com/images/edpborder500/I0-001/013/897/651-7.jpeg_/montgomery-alabamas-hiphop-christian-rap-group-lif-51.jpeg");
    	  alabamaPP.setCaption("Sweet home Alabama!");
		  complexPrefs.putObject("alabama", alabamaPP);
		  complexPrefs.commit();
		 //alabama = new PostContent(mActivity, alabamaPP);
      }
      else{
    	  //alabama = new PostView(getActivity());//, complexPrefs.getObject("alabama", PostParcel.class));
		  Log.w(TAG, "Loaded from ComplexPrefs");
      }
      
      if(complexPrefs.getObject("alaska", PostParcel.class) == null){
    	  PostParcel alaskaPP = new PostParcel();
    	  alaskaPP.setImageURL("http://25.media.tumblr.com/46058eaf8c72b5d21966b84f342c620f/tumblr_mpaazfYnZG1rubnoao1_500.jpg");
    	  alaskaPP.setCaption("North to the future");
    	  //alaska = new PostContent(mActivity, alaskaPP);
		  complexPrefs.putObject("alaska", alaskaPP);
		  complexPrefs.commit();
		  //alaska = new PostContent(mActivity, alaskaPP);
      }
      else{
    	  //alaska = new PostView(mActivity);//, complexPrefs.getObject("alaska", PostParcel.class));
		  Log.w(TAG, "Loaded alaska from ComplexPrefs");
      }
      
      if(complexPrefs.getObject("california", PostParcel.class) == null){
    	  PostParcel caliPP = new PostParcel();
    	  caliPP.setImageURL("http://solitarywatch.com/wp-content/uploads/2014/02/ca-hearing-pete.jpg");
    	  caliPP.setCaption("_____ girls, they're unforgettable");
    	//  california = new PostContent(mActivity, caliPP);
		  complexPrefs.putObject("california", caliPP);
		  complexPrefs.commit();
      }
      else{
    	  //california = new PostView(mActivity);//, complexPrefs.getObject("california", PostParcel.class));
		  Log.w(TAG, "Loaded california from ComplexPrefs");
      }
      
      if(complexPrefs.getObject("colorado", PostParcel.class) == null){
    	  PostParcel coPP = new PostParcel();
    	  coPP.setImageURL("http://catchcarri.com/wp-content/uploads/2013/08/winter-park-resort-denver-colorado.jpg");
    	  coPP.setCaption("We get high like St Elmo's fire");
    	  //colorado = new PostContent(mActivity, coPP);
		  complexPrefs.putObject("colorado", coPP);  
		  complexPrefs.commit();
      }
      else{
    	  //colorado = new PostView(mActivity);//, complexPrefs.getObject("colorado", PostParcel.class));
		  Log.w(TAG, "Loaded colorado from ComplexPrefs");
      }
//      initialPosts.add(colorado);
//      initialPosts.add(california);
//      initialPosts.add(alaska);
//      initialPosts.add(alabama);
//      
//      mFAdapter.addPostList(initialPosts);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_showfeed, container, false);
        refresh = (Button) rootView.findViewById(R.id.refresh_feed);
        refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refresh();
			}
		});
        addPost = (Button) rootView.findViewById(R.id.addPost_toFeed);
        addPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addPost();
			}
		});
        return rootView;
    }
	
	public void addPost(){
		mFAdapter.addPost(new PostView(getActivity()));
	}
	
	// Called only when the fragment is out of view. The class receives data 
	// for a post that will be created
	// and added to the feed once onResume() is called.
	public void addPost(AleppoPost ap){
		Log.w("--ADDPOST", "addPost called in FFNL");
		mFAdapter.addPost(ap);
	}
	
	public void refresh(){
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1)
			new AsyncRefresher(this).execute();
		else
			new AsyncRefresher(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		//lm.initLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<List<AleppoPost>> onCreateLoader(int arg0, Bundle arg1) {
		return new PostLoader(mActivity);
	}
	
	@Override
	public void onLoadFinished(Loader<List<AleppoPost>> aleppoPostLoader,
			List<AleppoPost> posts) {
		List<PostView> pv = new ArrayList<PostView>();
		Log.w("ONLOADFINISHED", "=======Load has been finished!");
		// Add the results to the adapter.
		for(AleppoPost post : posts){
			PostView postToAdd = new PostView(mActivity, post);
			postToAdd.setLocation(lg.getLocation());
			pv.add(postToAdd);
		}
		mFAdapter.setPostList(pv);
	}

	@Override
	public void onLoaderReset(Loader<List<AleppoPost>> arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public Context getApplicationContext() {
		return mActivity;
	}

	@Override
	public void asyncRefresherDone() {
		Log.w("ASYNCREFRESHER", "finished refreshing!");
		lm.initLoader(LOADER_ID, null, this);
	}
}
