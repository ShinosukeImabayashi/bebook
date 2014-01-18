package com.example.ebooktest002;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Menu;



public class aacc002 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TextView textview = new TextView(this);
        textview.setText("AnotherActivity");
        setContentView(textview);
    }

	public boolean onCreateOptionMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "1st activity");
		menu.add(Menu.NONE, 0, 0, "2nd activity");
		return super.onCreateOptionsMenu(menu);

	}





}
