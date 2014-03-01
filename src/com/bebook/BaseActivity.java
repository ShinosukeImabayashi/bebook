
package com.bebook;

import android.app.Activity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bebook.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;


public abstract class BaseActivity extends Activity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_clear_memory_cache:
			imageLoader.clearMemoryCache();
			return true;
		case R.id.item_clear_disc_cache:
			imageLoader.clearDiscCache();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onStart() {
		Log.v("BaseActivity:onStart", "INFO");
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);	// google analytics

	}

	@Override
	public void onStop() {
		Log.v("BaseActivity:onStop", "INFO");
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);		// google analytics
	}
}
