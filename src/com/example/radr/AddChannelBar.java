package com.example.radr;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class AddChannelBar extends MultiAutoCompleteTextView {
	
	private TextWatcher textWatcher;
	Context context;
	int currentLength;
	int oldSLength; //stores the value of the previous spaceCharacter
	ArrayList<String> channels;
	private static class ChannelPosTracker{
		String name;
		int startIndex;
		int endIndex;
	}
	Vector<ChannelPosTracker> channelPositions;
	Vector<ImageSpan> imageSpans;
	
	public AddChannelBar(Context context) {
		super(context);
		this.context = context;
		init();		
	}
	
	public AddChannelBar(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();	
	}
	
	public AddChannelBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	public void init(){
		oldSLength = 0;
		imageSpans = new Vector<ImageSpan>();
		channels = new ArrayList<String>();
		textWatcher = new TextWatcher(){//if text changes...
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				removeTextChangedListener(this);
//				System.out.printf("onTextChanged(\"%s\", %d, %d, %d)\n", s, start, before, count);

				SpannableStringBuilder sb = new SpannableStringBuilder();
				// NOTE: You HAVE to use s.toString(), not just s.
				// Passing the CharSequence (s) itself remembers old
				// spanners. Using s.toString() removes that information,
				// and passes only the actual string.
				sb.append(s.toString());
				// begin lets you know the index at which a channel name starts. 
				//-1 if no channel name currently being processed
				for (int idx = 0, begin = -1; idx < s.length(); idx++) {
					if (s.charAt(idx) == ' ') { // if the current character is a space...
						if (begin == -1) { // ...and you aren't in the middle of processing a channel name...
							// Do nothing
						} else { // but if you ARE in the middle of processing a channel name...
							// You have completed processing a channel!
							// create a new channel name text of the substring: 
							// beginning character of channel name
							// and the final character. 
							String newChanText = s.toString().substring(begin, idx);
							setChannel(sb, newChanText, begin, idx);
							begin = -1; //set begin to -1 to signify 
							//you're not processing a channel any more
						}
					} else { // if the current character ISN'T a space
						if (begin == -1) { // and begin says you weren't previous processing a channel name
							begin = idx; // you are now because you've hit the first character
						} else { // if we see a normal character and we're in the middle of processing, 
							//just keep going through (until you hit the end)
							// [Do nothing]
						}
					}
				}
				setMovementMethod(LinkMovementMethod.getInstance());
				getText().clear();
				setText(sb.subSequence(0, sb.length()));
				setSelection(sb.length());
				addTextChangedListener(this);
			}
			
			public void setChannel(SpannableStringBuilder sb, String newChanText, int start, int end) {
				TextView tv = createBubbledChannel(newChanText); // TODO: Trim?
				Bitmap bmp = convertViewToBitmap(tv);
				BitmapDrawable bd = new BitmapDrawable(bmp);
				bd.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
				sb.setSpan(new ImageSpan(bd), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			
//			public void setChannel(String newChanText, CharSequence s, int start, int end){
//				final SpannableStringBuilder sb = new SpannableStringBuilder();
//				TextView tv = createBubbledChannel(newChanText.trim());
//				System.out.println("Tv: " + tv.getText().toString() + 
//						", START = " + Integer.toString(start) +
//						", END = " + Integer.toString(end));
//				Bitmap bmp= convertViewToBitmap(tv);
//				BitmapDrawable bd = new BitmapDrawable(bmp);
//				bd.setBounds(0,0,bmp.getWidth(), bmp.getHeight());
//				imageSpans.add(new ImageSpan(bd));
//				// add the channel position of the new channeltext to the oldSLength 
//				// to the list of chanpositions
//				 sb.append(newChanText);
//				sb.setSpan(new ImageSpan(bd), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////				sb.setSpan(imageSpans, sb.length()-(newChanText.length()+1), sb.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//				setMovementMethod(LinkMovementMethod.getInstance());
//				setText(sb);
//				//add it to the editText via a spannable
//				setText(sb);
//				oldSLength = s.length();
//				setSelection(sb.length());
//			}
			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(count > 0){
//					if(s.charAt(start) == ' ' && (start - before) > 1){
//						//if the user typed a space char after another character...
//						String currentEditText = s.toString();
//						//convert the input into a bmp drawable
//						String newChanText = ("#" + currentEditText.substring(oldSLength));
//						System.out.println(newChanText);
//						if(!newChanText.isEmpty())	
//							setChannel(newChanText, s, start);
//						}
//				}
//			}
		
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		};
		
//		setOnKeyListener(new OnKeyListener() {                 
//	        @Override
//	        public boolean onKey(View v, int keyCode, KeyEvent event) {
//	            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
//	             if(keyCode == KeyEvent.KEYCODE_DEL){  
//	                 }
//	        return false;       
//	            }
//	    });
		
		addTextChangedListener(textWatcher);
	}
	
	public String[] getChannels(){
		String allChannelText = getText().toString();
		String currentChanText = "";
		Vector<String> myChannels = new Vector<String>();
		// Go through the whole text of the channel bar, every time you see a space
		// take the current contents of the holder string and add them to the vector of channels
		// Then erase the holder string's contents and keep going.
		for(int i = 0; i < allChannelText.length(); i++){
			currentChanText += allChannelText.charAt(i);
			// If you hit a space character, it's the end of the channel name. Add it to the list
			// clear the currentChannelText.
			if(allChannelText.charAt(i) == ' '){
				myChannels.add(currentChanText);
				currentChanText = "";
			}
		}
		String[] chans = new String[myChannels.size()];
		myChannels.toArray(chans);
		return chans;
	}
	
//	public void setChannel(String newChanText, CharSequence s, int start, int end){
//		final SpannableStringBuilder sb = new SpannableStringBuilder();
//		TextView tv = createBubbledChannel(newChanText.trim());
//		System.out.println("Tv: " + tv.getText().toString() + 
//				", START = " + Integer.toString(start) +
//				", END = " + Integer.toString(end));
//		Bitmap bmp= convertViewToBitmap(tv);
//		BitmapDrawable bd = new BitmapDrawable(bmp);
//		bd.setBounds(0,0,bmp.getWidth(), bmp.getHeight());
//		imageSpans.add(new ImageSpan(bd));
//		// add the channel position of the new channeltext to the oldSLength 
//		// to the list of chanpositions
//		 sb.append(newChanText);
//		sb.setSpan(new ImageSpan(bd), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////		sb.setSpan(imageSpans, sb.length()-(newChanText.length()+1), sb.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		setMovementMethod(LinkMovementMethod.getInstance());
//		setText(sb);
//		//add it to the editText via a spannable
//		setText(sb);
//		oldSLength = s.length();
//		setSelection(sb.length());
//	}
//	
//	public void setChannel(String newChanText, CharSequence s, int start){
//		final SpannableStringBuilder sb = new SpannableStringBuilder();
//		TextView tv = createBubbledChannel(newChanText.trim());
//		System.out.println("Tv: " + tv.getText().toString() + 
//				", START = " + Integer.toString(start));
//		Bitmap bmp= convertViewToBitmap(tv);
//		BitmapDrawable bd = new BitmapDrawable(bmp);
//		bd.setBounds(0,0,bmp.getWidth(), bmp.getHeight());
//		imageSpans.add(new ImageSpan(bd));
//		//add the channel position of the new channeltext to the oldSLength 
//		//to the list of chanpositions
//		sb.append(newChanText + ",");
//		sb.setSpan(imageSpans, sb.length()-(newChanText.length()+1), sb.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		setMovementMethod(LinkMovementMethod.getInstance());
//		setText(sb);
//		//add it to the editText via a spannable
//		setText(sb);
//		oldSLength = s.length();
//		setSelection(sb.length());
//	}
	
	public static Bitmap convertViewToBitmap(View view) {
	  int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	  view.measure(spec, spec);
	  view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	  Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
	        Bitmap.Config.ARGB_8888);
	  Canvas c = new Canvas(b);
	  c.translate(-view.getScrollX(), -view.getScrollY());
	  view.draw(c);
	  view.setDrawingCacheEnabled(true);
	  Bitmap cacheBmp = view.getDrawingCache();
	  Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
	  view.destroyDrawingCache();
	  @SuppressWarnings("deprecation")
	  BitmapDrawable bd = new BitmapDrawable(viewBmp);
	  return viewBmp; 
	}

	public TextView createBubbledChannel(String text){
	  //creating textview dynamically
	  TextView tv = new TextView(context);
	  tv.setText(text);
	  tv.setTextSize(20);
	  tv.setBackgroundResource(R.drawable.channel_bubble);
	  tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	  return tv;
	}
}