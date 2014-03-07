package com.imabaya.asatsuki;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AbsListView;

public class AbsListViewBaseActivity extends BaseActivity {

	protected AbsListView listView;

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
