package com.example.radr.listitems;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class AddEventRow extends Button{

	public AddEventRow(Context context) {
		super(context);
		setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// Launch create event fragment
			}
			
		});
	}

}
