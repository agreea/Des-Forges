package com.example.radr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.radr.fragment.CaptureFragment;
import com.example.radr.fragment.ChannelFragment;
import com.example.radr.fragment.ExploreFragment;
import com.example.radr.fragment.FriendsFragment;
 
public class TabsPageAdapter extends FragmentPagerAdapter {
	ChannelFragment mChanFrag;
	FriendsFragment mFriendFrag;
	ExploreFragment mExploreFrag;
	CaptureFragment mCaptureFrag;
    public TabsPageAdapter(FragmentManager fm) {
        super(fm);
        mChanFrag = new ChannelFragment();
        mCaptureFrag = new CaptureFragment();
        mExploreFrag = new ExploreFragment();
    }
 
    @Override
    public Fragment getItem(int index) {
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new ChannelFragment();
        case 1:
            // Games fragment activity
            return new ExploreFragment();
        case 2:
            // Movies fragment activity
            return new FriendsFragment();
        }
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}
