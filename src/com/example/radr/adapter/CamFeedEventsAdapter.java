package com.example.radr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.radr.R;
import com.example.radr.fragment.CaptureEventPicFragment;
import com.example.radr.fragment.CaptureFragment;
import com.example.radr.fragment.EventFragment;
import com.example.radr.fragment.FeedFragment;
 
public class CamFeedEventsAdapter extends FragmentPagerAdapter {
	
	FragmentManager mFragmentManager;
	
    public CamFeedEventsAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }
    
    @Override
    public Fragment getItem(int position) {
//    	if(position == 1)
//    		return new CaptureFragment();
        // Check if the fragment exists.
        String name = makeFragmentName(R.id.pager, position);
        Fragment f = mFragmentManager.findFragmentByTag(name);
        // Create a new one if it doesn't.
        if(f == null){
        	switch (position) {
            case 0:
            	return new EventFragment();
            case 1:
                return new CaptureFragment();
            case 2:
                return new FeedFragment();
            case 3:
                return new FeedFragment();
            }
        }
        return f;
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
}
