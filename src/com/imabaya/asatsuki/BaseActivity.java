
package com.imabaya.asatsuki;

import android.app.Activity;
import android.content.Intent;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.imabaya.asatsuki.R;
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
		case R.id.item_help:
			// ヘルプ画面へ遷移
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			return true;
		case R.id.item_clear_memory_cache:
			Log.v("BaseActivity:item_clear_memory_cache", "INFO");
			imageLoader.clearMemoryCache();
			return true;
		case R.id.item_clear_disc_cache:
			Log.v("BaseActivity:item_clear_disc_cache", "INFO");
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
