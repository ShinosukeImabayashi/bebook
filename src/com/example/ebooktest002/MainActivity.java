package com.example.ebooktest002;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "1st activity");
		menu.add(Menu.NONE, 1, 1, "2nd activity");
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(this, "1st ", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(MainActivity.this, com.example.ebooktest002.MainBookViewerPagerActivity.class);
		    startActivity(intent);
			break;
		case 1:
			Toast.makeText(this, "2nd ", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent(MainActivity.this, com.example.ebooktest002.ImagePagerActivity.class);
		    startActivity(intent2);
			break;
		}
		return true;
	}









}
