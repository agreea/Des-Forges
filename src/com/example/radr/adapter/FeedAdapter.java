package com.example.radr.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.radr.AleppoPost;
import com.example.radr.LocationGrabber;
import com.example.radr.R;
import com.example.radr.backend.AsyncBackendWriter;
import com.example.radr.listitems.PostView;

public class FeedAdapter extends BaseAdapter implements PostHolder.PostHolderInterface{
    private List<PostView> blips;
    // a list of integers with possible values{-1||0||1}
    // index in this list corresponds with position in the array's data set
    private static List voteValues;  
    // a list of integers which reflect the total upvotes - total downvotes
    // for each post.
    private static List netVotes;
    private Activity mActivity;
	private final String TAG = "FEEDADAPTER";
	private LocationGrabber lg;
	private static AQuery aq;	
	private final int VOTE_NEUTRAL = 0;
	private final int UP_VOTE = 1;
	private final int DOWN_VOTE = -1;
 
	public FeedAdapter(Activity activity){
		super();
		mActivity = activity;
		lg = (LocationGrabber) mActivity;
		blips = new ArrayList<PostView>();
		aq = new AQuery(mActivity); 
		voteValues = new ArrayList();
		netVotes = new ArrayList();
	}
	
	//adds a single piece of new content to the current list
	public void addPost(PostView content){
		blips.add(content);
		netVotes.add(0);
		voteValues.add(0);
		notifyDataSetChanged();
	}
	
	public void addPost(AleppoPost ap){
		blips.add(new PostView(mActivity, ap));
		notifyDataSetChanged();
	}
	
	//adds a list of newContent to the current list
	public void addPostList(List<PostView> newContent){
		blips.addAll(newContent);
		notifyDataSetChanged();
	}
		
	public void setPostList(List<PostView> newContent){
		blips.clear();
		blips.addAll(newContent);
		// initialize all the votes as 0 for now. TODO: get the vote values from storage or something
		// get the initial net-votes as well.
		voteValues.clear();
		netVotes.clear();
		for(PostView blip : blips){
			voteValues.add(blip.getVoteValue());
			netVotes.add(blip.getNetVote());
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return blips.size();
	}

	
	/* New implementation:
	 * check the vote value of the postContent at that point in the feed. 
	 * Give the content the appropriate holder pattern
	 */
	@Override
	public PostView getView(int position, View convertView, ViewGroup parent) {
		PostHolder postHolder;
		PostView postContent = (PostView) getItem(position);
		if(convertView == null){
			//inflate the view if it's null
			convertView = new PostView(mActivity);
			postHolder = new PostHolder(convertView, position, this);
			convertView.setTag(postHolder);
		}
		else{
			postHolder = (PostHolder) convertView.getTag();
		}
		// set all the stuff that varies by post:
		postHolder.caption.setText(postContent.getCaption());
		postHolder.dist.setText(postContent.getLocationStamp());
		postHolder.timestamp.setText(postContent.properTime());
		postHolder.setForPosition(position);
		// Log.w("DISTANCE STAMP", postContent.getLocationStamp());
        if(postContent.getImageURL() != null){ // if there's no image url just set the default
        	aq.id(postHolder.media).image(postContent.getImageURL());
        }
        else{
        	aq.id(postHolder.media).image(postContent.getPicDrawable());
        }
        // make sure the arrows correspond to the vote value 
//        if(voteValues.get(position).equals(1)){
//        	Log.w("FAdapter", "Upvote at position " + position);
//        	aq.id(postHolder.up).image(R.drawable.arrohead_up_green);
//        }
//        if(voteValues.get(position).equals(-1)){
//        	Log.w("FAdapter", "Downvote at position " + position);
//        	aq.id(postHolder.down).image(R.drawable.arrohead_down_red);
//        }
//        if(voteValues.get(position).equals(0)){
//        	aq.id(postHolder.down).image(R.drawable.arrohead_down);
//        	aq.id(postHolder.up).image(R.drawable.arrohead_up);
//        }
        convertView.setTag(postHolder);
		return (PostView) convertView;
	}
		
	@Override
	public Object getItem(int position) {
		return blips.get(position);
	}
	@Override
	public long getItemId(int position) {
		return blips.get(position).getId();
	}
//================PostHolder Interface Methods==================
	@Override
	public int getVoteValue(int position) {
		// TODO Auto-generated method stub
		return (Integer) voteValues.get(position);
	}

	// Called by the post holder when a change occurs to the vote value 
	// of a specific post. The vote value is updated locally in the janky-ass array of ints
	// also updated in the array of postViews. The new postView with the glistening, fresh votecount
	// is sent to the server as a pushed voteQuery.
	@Override
	public int updateVoteValue(int position, int newVoteValue) {
		voteValues.set(position, newVoteValue);
		if(newVoteValue == VOTE_NEUTRAL){
			blips.get(position).pushNeutral();
		}
		if(newVoteValue == UP_VOTE){
			blips.get(position).pushUpVote();
		}
		if(newVoteValue == DOWN_VOTE){
			blips.get(position).pushDownVote();
		}
		AsyncBackendWriter.write(new AsyncBackendWriter.VoteQuery(blips.get(position).toAleppoPost()));
		return (Integer) voteValues.get(position);
	}

	@Override
	public int getNetVote(int position) {
		return (Integer) netVotes.get(position);
	}

	@Override
	public int updateNetVote(int position, int newVote) {
		if(newVote < 3 && newVote > -3){
			int newNetVote = (Integer) netVotes.get(position) + newVote;
			netVotes.set(position, newNetVote);
		}
		return (Integer) netVotes.get(position);
	}	
}