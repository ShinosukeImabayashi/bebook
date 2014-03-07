package com.imabaya.asatsuki;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("Help", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		Button  helpBackButton1 = (Button)  this.findViewById(R.id.help_back1);
		helpBackButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				///throw new IllegalArgumentException("がっ");
	        	finish();
			}
		});

		Button  helpBackButton2 = (Button)  this.findViewById(R.id.help_back2);
		helpBackButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	        	finish();
			}
		});


	}





}
