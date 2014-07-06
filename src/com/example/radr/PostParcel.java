package com.example.radr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class PostParcel implements Parcelable{
	 
	private byte[] pic;
	private String caption;
	private String[] channels;
	private int voteCount;
	private long timestamp;
	private String postID;
	private String imageURL;
	private double latitude;
	private double longitude;
	private int upvote;
	private int downvote;
	
	// TODO: Implement a way to check how the user has voted on this content, which
	// the server can deliver efficiently. 
	
	PostParcel(byte[] pic, 
			   String caption, 
			   String[] channels, 
			   int upvote, int downvote,
			   long timestamp){
		this.pic = pic;
		this.caption = caption;
		this.channels = channels;
		this.upvote = upvote;
		this.downvote = downvote;
		this.timestamp = timestamp;
		this.latitude = 0;
		this.longitude = 0;
	}
	
	PostParcel(String url, 
			   String caption, 
			   String[] channels, 
			   int voteCount, 
			   boolean isEvent,
			   long timestamp,
			   double latitude,
			   double longitude){
		this.pic = null;
		this.caption = caption;
		this.channels = channels;
		this.voteCount = voteCount;
		this.timestamp = timestamp;
		this.imageURL = url;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	
	public PostParcel(){
		this.pic = null;
		this.caption = null;
		this.channels = null;
		this.voteCount = 0;
		this.timestamp = 0;
		this.postID = null;
		this.imageURL = null;
		this.latitude = 0;
		this.longitude = 0;
	}
	
	private PostParcel(Parcel source) {
		//source.readByteArray(pic);
		pic = source.createByteArray();
		caption = source.readString();
		channels = source.createStringArray();
		voteCount = source.readInt();
	}
	
	public void setPic(byte[] pic){
		this.pic = pic;
	}
	
	public void setCaption(String caption){
		this.caption=caption;
	}
	
	public void setChannels(String[] channels){
		this.channels = channels;
	}
	
	public void setVoteCount(int voteCount){
		this.voteCount = voteCount;
	}
	
	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}
	
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	
	public void setLocation(Location location){
		this.longitude = location.getLongitude();
		this.latitude = location.getLatitude();
	}
	
	public String[] getChannels(){
		return channels;
	}
	
	public String getCaption(){
		return caption;
	}
	public int getVoteCount(){
		return voteCount;
	}
	
	public byte[] getPic(){
		return pic;
	}
		
	public long timeStamp(){
		return timestamp;
	}
	
	public int upvote(){
		return upvote;
	}
	
	public int downvote(){
		return downvote;
	}
	
	public int netvote(){
		return upvote - downvote;
	}
	
	public String postID(){
		return postID;
	}
	
	public String imageUrl(){
		return imageURL;
	}
	
	public double lat(){
		return latitude;
	}
	
	public double lon(){
		return longitude;
	}
	
	public static final Parcelable.Creator<PostParcel> CREATOR = new Creator<PostParcel>() {
	      	//Turns a parcel into a postParcel
		  public PostParcel createFromParcel(Parcel source) {
	            return new PostParcel(source);
	      }
		  
	      public PostParcel[] newArray(int size) {
	            return new PostParcel[size];
	      }
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByteArray(pic);
		dest.writeString(caption);
		dest.writeStringArray(channels);
		dest.writeInt(voteCount);
		dest.writeString(imageURL);
	}
	
	public AleppoPost toAleppoPost() {
		return new AleppoPost(0, timestamp, new AleppoLocation(latitude, longitude), caption, "", upvote, downvote);
	}
}