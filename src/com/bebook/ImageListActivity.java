package com.bebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.bebook.R;
import com.bebook.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



public class ImageListActivity extends AbsListViewBaseActivity {

	DisplayImageOptions mDisplayImageOptions;
	UILApplication mUap;
	String[] mCoverImageUrls;
	String[] mCoverText;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);

		// 設定情報関連
		mUap = (UILApplication) this.getApplication();

		// 書籍情報オブジェクトの取得
		BookList booklist = mUap.getBooklist();

		// 表紙画像 URL リストの取得
		mCoverImageUrls = booklist.getBookCoverImageUrl("id", "asc");

		// 表紙タイトルリストの取得
		mCoverText = booklist.getBookCoverText();

		// リスト形式での画像表示に伴う設定項目
		mDisplayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();

		// リストビューにアダプタを設定
		listView = (ListView) findViewById(android.R.id.list);
		((ListView) listView).setAdapter(new ItemAdapter());

		// リストのいずれかのアイテムがタップされたときの挙動
		listView.setOnItemClickListener(new OnItemClickListener() {// 書籍閲覧画面への遷移
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
	}

	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	// 書籍閲覧画面への遷移
	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		//intent.putExtra(Extra.IMAGES, mCoverImageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);		// 何番目の書籍が選ばれたかを指定
		startActivity(intent);
	}




	// 表紙リスト表示用のアダプタ
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
		}

		@Override
		public int getCount() {
			return mCoverImageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 画面表示の管理
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.listimage);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 表紙タイトルの表示
			holder.text.setText(mCoverText[position]);

			// 表紙画像の表示
			imageLoader.displayImage(mCoverImageUrls[position], holder.image, mDisplayImageOptions, animateFirstListener);

			return view;
		}
	}


	// アニメーション処理の管轄
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
