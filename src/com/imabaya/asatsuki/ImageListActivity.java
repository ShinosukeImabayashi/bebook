package com.imabaya.asatsuki;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.imabaya.asatsuki.R;
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
	BookList booklist;
	String[] mCoverImageUrls;
	String[] mCoverText;
	int ｍSelectedListItemPosition;	// リストの何番目が選択されたか

	AdRequest mAdRequestInterstitial;	//admob インタースティシャル広告配信
	InterstitialAd mInterstitialAdView;
	boolean mIsBookInterstitialAdBeforeRead = false;	// 1 = 広告配信（読書前）

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);

		// 設定情報関連
		mUap = (UILApplication) this.getApplication();

		// 書籍情報オブジェクトの取得
		booklist = mUap.getBooklist();

		// 表紙画像 URL リストの取得
		try {
			mCoverImageUrls = booklist.getBookCoverImageUrl("printorder", "asc");
		} catch (NullPointerException e) {	// アプリを起動状態のまましばらく放置して book 関連のリソースが強制開放された後に復帰するとここでエラーとなるので最初からやり直す
			Intent intent = new Intent(this, StartActivity.class);
			startActivity(intent);
		}

		// 表紙タイトルリストの取得
		mCoverText = booklist.getBookCoverText();

		// リスト形式での画像表示に伴う設定項目
		mDisplayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loader)
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
				prepareStartImagePagerActivity(position);
			}
		});



	}

	// ■admob インタースティシャル広告表示準備
	private void loadInterstitialAd () {
		Log.v("mInterstitialAdView - loadInterstitialAd", 	"INFO");
		String adUnitInterstitialId = booklist.getAdmobAdvertisingUnitId("interstitial");
		mAdRequestInterstitial = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
			.addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
			.build();

		// 広告配信オブジェクトの作成
		mInterstitialAdView = new InterstitialAd(this);
		mInterstitialAdView.setAdUnitId(adUnitInterstitialId);

		// 読み込み開始
		mInterstitialAdView.loadAd(mAdRequestInterstitial);
		mInterstitialAdView.setAdListener(new AdListener(){	    // リスナー設定
			public void onAdClosed() {	// 広告表示がクローズされた場合
				Log.w("mInterstitialAdView - onAdClosed", 	"ｍSelectedListItemPosition=" + ｍSelectedListItemPosition);
				startImagePagerActivity(ｍSelectedListItemPosition);
			}
		});
	}



	// 書籍閲覧画面への遷移準備
    // インタースティシャル広告配信設定が有ったら、表示し、広告クローズ後に書籍閲覧アクティビティへ遷移する
	private void prepareStartImagePagerActivity(int position) {

		ｍSelectedListItemPosition = position;

		// 書籍情報オブジェクトの取得
		mIsBookInterstitialAdBeforeRead = booklist.getIsBookAdvertising(position, 1);	// 1 = 広告配信（読書前）
		Log.v("startImagePagerActivity", 	" isBookInterstitialAdBeforeRead=" + mIsBookInterstitialAdBeforeRead);
		if (mIsBookInterstitialAdBeforeRead == true) { // 広告表示設定有り
			try {
				if (mInterstitialAdView.isLoaded()) {
					mInterstitialAdView.show();
					Log.v("mInterstitialAdView - isLoaded", 	"show");
				} else {
					startImagePagerActivity(ｍSelectedListItemPosition);
				}
			} catch (NullPointerException e) {
				Log.w("mInterstitialAdView - isLoaded", 	"NullPointerException");
				startImagePagerActivity(ｍSelectedListItemPosition);
			}
		} else { // 広告表示設定無し
			startImagePagerActivity(ｍSelectedListItemPosition);
		}

	}



	// 書籍閲覧画面への遷移
	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		//intent.putExtra(Extra.IMAGES, mCoverImageUrls);
		intent.putExtra("IMAGE_POSITION", position);		// 何番目の書籍が選ばれたかを指定
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		// 広告表示準備（ onResume は初回起動時にも呼ばれる）
		loadInterstitialAd ();
	}


	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

    @Override
    protected void onDestroy() {
		Log.v("ImageListActivity - onDestroy", "INFO");
		listView = null;

		// imageLoader.destroy();  ２回目の起動時にエラーが発生する

		finish();
		System.gc();

		super.onDestroy();
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
				holder.text = (TextView) view.findViewById(R.id.listimage_text);
				holder.image = (ImageView) view.findViewById(R.id.listimage);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 表紙タイトルの表示
			holder.text.setText(Html.fromHtml(mCoverText[position]));

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
