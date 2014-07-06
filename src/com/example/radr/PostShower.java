package com.example.radr;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PostShower extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_shower);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_post_shower);		
		PostParcel pp = getIntent().getParcelableExtra("post");
		byte[] picBytes = pp.getPic();
		//PostContent pc = new PostContent(this, pp);
		//pc.getId();
		RelativeLayout.LayoutParams params = 
				new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT);
		params.getClass();
		rl.getId();
		//rl.addView(pc, params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_shower, menu);
		return true;
	}

}
