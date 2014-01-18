package com.example.ebooktest002;

import java.util.ArrayList;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainBookViewerPagerActivity extends Activity implements OnClickListener {

	private final int BTN_START = 0;
	private final int BTN_PREV = 1;
	private final int BTN_NEXT = 2;
	private final int BTN_END = 3;
	private ArrayList<ImageButton> _btnList = new ArrayList<ImageButton>();
	private ViewPager _viewPager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_book_viewer);
		_btnList.add((ImageButton) findViewById(R.id.btn_start));
		_btnList.add((ImageButton) findViewById(R.id.btn_prev));
		_btnList.add((ImageButton) findViewById(R.id.btn_next));
		_btnList.add((ImageButton) findViewById(R.id.btn_end));
		for (ImageButton btn : _btnList) {
			btn.setOnClickListener(this);
		}

		_viewPager = (ViewPager) findViewById(R.id.viewpager);
		PagerAdapter mPagerAdapter = new CustomPagerAdapter(this);
		_viewPager.setAdapter(mPagerAdapter);
	}

	public void onClick(View v) {
		for (int i = 0; i < _btnList.size(); i++) {
			if (v != _btnList.get(i)) {
				continue;
			}
			switch (i) {
			case BTN_START:
				_viewPager.setCurrentItem(0);
				break;
			case BTN_PREV:
				_viewPager.arrowScroll(View.FOCUS_LEFT);
				break;
			case BTN_NEXT:
				_viewPager.arrowScroll(View.FOCUS_RIGHT);
				break;
			case BTN_END:
				_viewPager.setCurrentItem(CustomPagerAdapter.N - 1);
				break;
			}
		}
	}

}