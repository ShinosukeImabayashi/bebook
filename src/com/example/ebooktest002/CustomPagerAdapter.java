package com.example.ebooktest002;

import com.example.ebooktest002.R;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomPagerAdapter extends PagerAdapter {
	public final static int N = 5;
	private LayoutInflater _inflater = null;

	public CustomPagerAdapter(Context c) {
		super();
		_inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LinearLayout layout = (LinearLayout) _inflater.inflate(R.layout.main_book_viewer_page, null);
		int brt = 255*position/N;
		layout.setBackgroundColor(Color.rgb(brt,brt,brt));
		ImageView img = (ImageView) layout.findViewById(R.id.img_scroll);
		int rsrc[] = { R.drawable.img00, R.drawable.img01, R.drawable.img02, R.drawable.img03, R.drawable.img04 };
		img.setImageResource(rsrc[position]);
		container.addView(layout);
		return layout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public int getCount() {
		return N;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
}
