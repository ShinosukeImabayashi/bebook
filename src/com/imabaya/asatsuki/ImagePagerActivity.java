package com.imabaya.asatsuki;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.imabaya.asatsuki.R;


public class ImagePagerActivity extends BaseActivity  {

	protected ImageLoader mImageLoader = ImageLoader.getInstance();

	private static final String STATE_POSITION = "STATE_POSITION";

	private UILApplication mUap;

	private DisplayImageOptions mDisplayImageOptions;
	private asaViewPager mPager;

	private String[] mBookImageUrls;	// 書籍の画像 URL リスト
	private String[] mBookExplanationTexts;	// 書籍の画像説明文リスト
	private String mBookTitleNameText;	// 書籍のタイトルテキスト
	private String mBookPublicationText;	// 書籍の奥付けテキスト
	private String mOpeingType;	// 書籍が左開きか右開きか
	private int  mStartPagerPosition;  // 書籍表示開始ページ

	private AdView mBunnerAdView;	// admob バナー広告配信
	AdRequest mAdRequestBanner;
	private InterstitialAd mInterstitialAdView;	//admob インタースティシャル広告配信
	AdRequest mAdRequestInterstitial01;
	AdRequest mAdRequestInterstitial;
	boolean mIsBookInterstitialAdBeforeRead = false;	// 1 = 広告配信（読書前）
	boolean mIsBookInterstitialAdAfterRead = false;	// 2 = 広告配信（読書後）
	boolean mIsBookBannerAd = false;	// 3 = バナー広告配信
	

	private static final int PAGE_START_NUM = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("ImagePagerActivity - onCreate", "INFO");
		// ウインドウ全画面化＆タイトルバー非表示化
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// layout xml を当該アクティビティのビューに結びつける
		setContentView(R.layout.ac_image_pager);
		//Toast.makeText(ImagePagerActivity.this, "start", Toast.LENGTH_SHORT).show();

		// 画像リソースの下準備
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		//String[] mBookImageUrls = bundle.getStringArray(Extra.IMAGES);
		int mSelectListPosition = bundle.getInt("IMAGE_POSITION", 0);

		// 設定情報関連
		mUap = (UILApplication) this.getApplication();

		// 書籍情報オブジェクトの取得
		BookList booklist = mUap.getBooklist();

		// 書籍画像 URL リスト・説明文リストの取得
		mBookImageUrls = booklist.getBookImageUrl(mSelectListPosition);
		mBookExplanationTexts = booklist.getBookImageExplanationText(mSelectListPosition);

		// 書籍のタイトルや奥付けテキストの取得
		mBookTitleNameText  = booklist.getBookTitleNameText(mSelectListPosition);
		mBookPublicationText  = booklist.getBookPublicationText(mSelectListPosition);

		// アクセス解析
		EasyTracker.getInstance(getApplicationContext()).send(MapBuilder.createEvent("OpenBook", getPackageName(), mBookTitleNameText, null).build());

		// 左開きか右開きかの指定取得
		mOpeingType = booklist.getBookOpeningtype(mSelectListPosition);
		// 初期ページ数設定
		if (mOpeingType.equals("LEFT_OPENING"))  {	// 左開き
			mStartPagerPosition = PAGE_START_NUM;	// ★position 0 と 1 は初期化処理がおかしいので使わないようにする
		} else { // 右開き
			mStartPagerPosition = mBookImageUrls.length + 1;	// 最後のページから開く
		}

		// 端末回転による縦横変換を行った際の同一ページ保持（これが無いと端末回転した際に最初のページに戻る）
		if (savedInstanceState != null) {
			mStartPagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		// 表示・奥付けページ追加の準備と、表示開始位置の指定（右開きだったら末尾ページから始める）
		String[] templist;
		// リストの先頭に空文字列を２アイテム、末尾に２アイテム挿入しておく
		// 先頭の２アイテムは、操作案内＆書籍奥付け情報
		// 末尾の２アイテムは、書籍奥付け情報＆書籍情報レビューお願い
		templist = new String[mBookImageUrls.length + 4];
		templist[0] = "";
		templist[1] = "";
		for (int i=0; i <mBookImageUrls.length;  i++ ) {
			templist[i + PAGE_START_NUM] = mBookImageUrls[i].toString();
		}
		templist[mBookImageUrls.length + 2] = "";
		templist[mBookImageUrls.length + 3] = "";
		mBookImageUrls = templist;

		// universalimageloader の DisplayImageOptions 設定値初期設定
		mDisplayImageOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.resetViewBeforeLoading(true)
		.cacheInMemory(false)
		.cacheOnDisc(true)
		///.imageScaleType(ImageScaleType.NONE) // 画面サイズに合わせた画像の拡大縮小処理は PhotoView 側に任せる
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // Open DL の最大 Bitmap サイズが 2048*2048。元画像が範囲内でも、ここで画像自体を拡大縮小処理すると、上限値オーバーになることが有る。（極端に縦長 or 横長の場合）
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(3000))	// ３秒掛けてゆっくりフェードインアニメーション表示を行うと雰囲気良い
		.build();

		// 表示領域と 画像読み込みオブジェクト（ImagePagerAdapter）とをアダプターで関連付ける
		mPager = (asaViewPager) findViewById(R.id.pager);
		mPager.setAdapter(new ImagePagerAdapter(mBookImageUrls));
		mPager.setCurrentItem(mStartPagerPosition);
		//mPager.setRotationX(33);	// 表示領域を傾けてスターウォーズ風などになる。




		// ■admob スマートバナー広告配信オブジェクトの作成
		mIsBookBannerAd = booklist.getIsBookAdvertising(mSelectListPosition, 3);	// 3 = バナー広告配信
		String adUnitBannerId = booklist.getAdmobAdvertisingUnitId("banner");
		Log.v("getAdmobAdvertisingUnitId", 	"adUnitBannerId=" + adUnitBannerId + " isBookBannerAd=" + mIsBookBannerAd);
		if (mIsBookBannerAd == true && adUnitBannerId != null) {  // 広告を表示するかしないか

			// 広告リクエスト準備
			mAdRequestBanner = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
			.addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
			.build();

			// 広告配信オブジェクトの作成
			mBunnerAdView = new AdView(this);
			mBunnerAdView.setAdUnitId(adUnitBannerId);
			mBunnerAdView.setAdSize(AdSize.SMART_BANNER);

			// 読み込み開始
			LinearLayout baseLayout = (LinearLayout) findViewById(R.id.ac_image_pager_base);
			baseLayout.addView(mBunnerAdView, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			mBunnerAdView.loadAd(mAdRequestBanner);
		} // printBunnerAd



		// ■admob インタースティシャル広告表示
		mIsBookInterstitialAdAfterRead = booklist.getIsBookAdvertising(mSelectListPosition, 2);	// 2 = 広告配信（読書後）
		String adUnitInterstitialId = booklist.getAdmobAdvertisingUnitId("interstitial");
		Log.v("getAdmobAdvertisingUnitId", 	"adUnitInterstitialId=" + adUnitInterstitialId + " isBookInterstitialAdAfterRead=" + mIsBookInterstitialAdAfterRead);
		if ( mIsBookInterstitialAdAfterRead == true && adUnitInterstitialId != null) {
			// 広告リクエスト準備
			mAdRequestInterstitial = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
			.addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
			.build();

			// 広告配信オブジェクトの作成
			mInterstitialAdView = new InterstitialAd(this);
			mInterstitialAdView.setAdUnitId(adUnitInterstitialId);

			// 読み込み開始
			mInterstitialAdView.loadAd(mAdRequestInterstitial);
		} // printInterstitialAd

	} // onCreate





	// 画像の表示管理全般
	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;
		private PhotoViewAttacher mAttacher;

		ImagePagerAdapter(String[] images) {
			Log.v("ImagePagerAdapter - ImagePagerAdapter", "INFO");
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		// 各ページ生成時のタイミングで呼び出される
		public Object instantiateItem(ViewGroup view, int position) {

			Log.v("ImagePagerAdapter - instantiateItem p=" +position, "INFO");

			//レイアウトファイル item_pager_image をインスタンス化する
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.pageimage);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);		// 読み込み中にアニメーション表示する
			//	imageView.setBackgroundColor(Color.BLACK); // admob 広告表示に支障をきたす

			// アナウンス系
			final ImageButton pageRightSwipeGuideImage = (ImageButton) imageLayout.findViewById(R.id.page_right_swipe_guide_image);
			final ImageButton pageLeftSwipeGuideImage = (ImageButton) imageLayout.findViewById(R.id.page_left_swipe_guide_image);

			// 書籍奥付け表示領域
			final TextView  bookSummaryInfo = (TextView)  imageLayout.findViewById(R.id.book_summary_info);
			final ScrollView bookSummaryInfoScrollParent =  (ScrollView)  imageLayout.findViewById(R.id.book_summary_info_scrollparent);

			// 書籍情報レビューお願い領域
			final ScrollView introduceScrollParent =  (ScrollView)  imageLayout.findViewById(R.id.introduce);
			final TextView  introduceWishText = (TextView)  imageLayout.findViewById(R.id.introduce_wish_text);
			final Button  introduceByEmail = (Button)  imageLayout.findViewById(R.id.introduce_by_email);
			final Button  introduceByGoogleplay = (Button)  imageLayout.findViewById(R.id.introduce_by_googleplay);
			final Button  introduceByTwitter = (Button)  imageLayout.findViewById(R.id.introduce_by_twitter);
			final Button  introduceByFacebook = (Button)  imageLayout.findViewById(R.id.introduce_by_facebook);
			final Button  introduceByLine = (Button)  imageLayout.findViewById(R.id.introduce_by_Line);
			final Button  introduceByGoogleplus = (Button)  imageLayout.findViewById(R.id.introduce_by_googleplus);
			final Button  introduceViewToFirstpage = (Button)  imageLayout.findViewById(R.id.introduce_view_to_firstpage);
			final Button  introduceViewToTop = (Button)  imageLayout.findViewById(R.id.introduce_view_to_top);

			introduceWishText.setText(Html.fromHtml(getString(R.string.introduce_wish_text)));
			introduceByEmail.setText(getString(R.string.introduce_by_email));
			introduceByGoogleplay.setText(getString(R.string.introduce_by_googleplay));
			introduceByTwitter.setText(getString(R.string.introduce_by_twitter));
			introduceByFacebook.setText(getString(R.string.introduce_by_facebook));
			introduceByLine.setText(getString(R.string.introduce_by_Line));
			introduceByGoogleplus.setText(getString(R.string.introduce_by_googleplus));
			introduceViewToFirstpage.setText(getString(R.string.introduce_view_to_firstpage));
			introduceViewToTop.setText(getString(R.string.introduce_view_to_top));

			IntroduceWish IWListner = new IntroduceWish();
			introduceByEmail.setOnClickListener(IWListner);
			introduceByGoogleplay.setOnClickListener(IWListner);
			introduceByTwitter.setOnClickListener(IWListner);
			introduceByFacebook.setOnClickListener(IWListner);
			introduceByLine.setOnClickListener(IWListner);
			introduceByGoogleplus.setOnClickListener(IWListner);
			introduceViewToFirstpage.setOnClickListener(IWListner);
			introduceViewToTop.setOnClickListener(IWListner);

			// シークバー
			final SeekBar pageSeekBar;
			pageSeekBar = (SeekBar) imageLayout.findViewById(R.id.page_seek_bar);
			pageSeekBar.setMax(this.getCount() - PAGE_START_NUM + 1);

			pageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				// シークバー トラッキング開始時
				@Override
				public void onStartTrackingTouch(SeekBar pageSeekBar) {
					Log.v("onStartTrackingTouch()", 	String.valueOf(pageSeekBar.getProgress()));
				}
				// シークバー トラッキング中
				@Override
				public void onProgressChanged(SeekBar pageSeekBar, int progress, boolean fromTouch) {
					Log.v("onProgressChanged()", String.valueOf(progress) + ", " + String.valueOf(fromTouch));

					// シークバーで現在トラック中のページ数を表示したい
					// 下記の実装を試したが、ViewPager の配下でやると、画面に実際にデータが反映表示されるのがページング後になるので意味が無い
					// 要技術検討項目
					//ViewGroup parent = (ViewGroup) mPager.getParent();	// 引数で得られるのは PhotoView の view なので、その親の Activity の view を取得
					//View imageLayout = inflater.inflate(R.layout.item_pager_image, mPager, true);	// 第三パラメータの attachToRoot は、ここでは true 必須
					//TextView SeekInfo = (TextView) imageLayout.findViewById(R.id.seek_bar_info);
					//SeekInfo.setText(String.valueOf(progress));

				}
				// シークバー トラッキング終了時
				@Override
				public void onStopTrackingTouch(SeekBar pageSeekBar) {
					Log.v("onStopTrackingTouch()", 	String.valueOf(pageSeekBar.getProgress()));
					mPager.setCurrentItem(pageSeekBar.getProgress());
				}
			}); // pageSeekBar.setOnSeekBarChangeListener

			// 各ページの表示
			// position は 0 から、mPager.getCurrentItem() は 0 からカウントスタート
			//（※現在表示ページの確認に position の値を使うのは不適当。非同期処理ゆえ）
			///Log.v("instantiateItem position：" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
			if ( (mOpeingType.equals("LEFT_OPENING") && position == 0 ) ||
					(mOpeingType.equals("RIGHT_OPENING") && position == this.getCount() - 1 )  ) {	// -2 ページ目
				if (mOpeingType.equals("LEFT_OPENING") ) {
					pageRightSwipeGuideImage.setVisibility(View.VISIBLE);
				} else {
					pageLeftSwipeGuideImage.setVisibility(View.VISIBLE);
				}
				pageRightSwipeGuideImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
					}
				});
				pageLeftSwipeGuideImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mPager.arrowScroll(View.FOCUS_LEFT);	// 次のページへ遷移させる
					}
				});

			} else if ( (mOpeingType.equals("LEFT_OPENING") && position == 1 ) ||
					(mOpeingType.equals("RIGHT_OPENING") && position == this.getCount() - 2 )  ) {	// -1 ページ目
				// 書籍奥付け情報の表示
				bookSummaryInfo.setText(Html.fromHtml(mBookPublicationText));	// 簡易 HTML 文法が使用可能
				bookSummaryInfoScrollParent.setVisibility(View.VISIBLE);
				Log.v("bookSummaryInfo" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
				bookSummaryInfo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("bookSummaryInfo:onClick" + mPager.getCurrentItem(), "INFO");
						if (mOpeingType.equals("LEFT_OPENING") ) {
							mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
						} else {
							mPager.arrowScroll(View.FOCUS_LEFT);	// 次のページへ遷移させる
						}
					}
				});

			} else if ( (mOpeingType.equals("LEFT_OPENING") && position == this.getCount() - 2 ) ||
					(mOpeingType.equals("RIGHT_OPENING") && position == 1 )  ) {	// 最終ページ + 2
				// アクセス解析
				EasyTracker.getInstance(getApplicationContext()).send(MapBuilder.createEvent("ReadEnd", getPackageName(), mBookTitleNameText, null).build());

				// 書籍奥付け情報の表示
				bookSummaryInfo.setText(Html.fromHtml(mBookPublicationText));	// 簡易 HTML 文法が使用可能
				bookSummaryInfoScrollParent.setVisibility(View.VISIBLE);
				Log.v("bookSummaryInfo" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
				bookSummaryInfo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("bookSummaryInfo:onClick" + mPager.getCurrentItem(), "INFO");
						if (mOpeingType.equals("LEFT_OPENING") ) {
							mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
						} else {
							mPager.arrowScroll(View.FOCUS_LEFT);	// 次のページへ遷移させる
						}
					}
				});

			} else if ( (mOpeingType.equals("LEFT_OPENING") && position == this.getCount() - 1 ) ||
					(mOpeingType.equals("RIGHT_OPENING") && position == 0 )  ) {	// 最終ページ + 2
				// 書籍情報レビューお願い領域の表示
				introduceScrollParent.setVisibility(View.VISIBLE);
				introduceScrollParent.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("bookSummaryInfo:onClick" + mPager.getCurrentItem(), "INFO");
						if (mOpeingType.equals("LEFT_OPENING") ) {
							mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
						} else {
							mPager.arrowScroll(View.FOCUS_LEFT);	// 次のページへ遷移させる
						}
					}
				});

			} else {

				// imageLoader で画像の読み込み処理を行う
				mImageLoader.loadImage(images[position], mDisplayImageOptions, new ImageLoadingListener() {

					// 画像ロード中は読み込み中表示を出す
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
						pageSeekBar.setVisibility(View.GONE);
						Log.v("imageLoader - onLoadingStarted mPager=" +mPager.getCurrentItem() , "INFO");
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = getString(R.string.loadimage_error_io_error);
							break;
						case DECODING_ERROR:
							message = getString(R.string.loadimage_error_decoding_error);
							break;
						case NETWORK_DENIED:
							message = getString(R.string.loadimage_error_network_denied);
							break;
						case OUT_OF_MEMORY:
							message = getString(R.string.loadimage_error_out_of_memory);
							break;
						case UNKNOWN:
							message = getString(R.string.loadimage_error_unknown);
							break;
						}
						Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					// 画像読み込み完了時
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						///Log.v("imageLoader - onLoadingComplete p=" +mPager.getCurrentItem() , "INFO");
						spinner.setVisibility(View.GONE);

						// PhotoViewAttacher にお願いして表示画像を表示領域にフィットさせる
						// instantiateItem 呼び出し直下で指定しても上手く反映がされない
						// 画像サイズが大きすぎる（1MByte 以上～）と、処理が追いつかなくて上手く行ったりいかなかったりする

						if (imageView != null) {	// imageView の取得がたまに失敗することがある
							imageView.setImageBitmap(loadedImage);
							mAttacher = new PhotoViewAttacher(imageView);
							mAttacher.setScaleType(ScaleType.FIT_CENTER);	// 画像をセンターでフィットさせる
							mAttacher.setOnViewTapListener(new ViewTapListener());	// タップイベントのリスナー設定
							mAttacher.setOnLongClickListener(new LongClickListener());	// ロングクリックイベントのリスナー設定
							mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
						}
					} 

					// 読み込みキャンセル時
					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				}); // mImageLoader.loadImage

			} // ページごとの処理

			view.addView(imageLayout, 0);	// imageLayout をビューに紐付け

			return imageLayout;
		} // instantiateItem



		// 画面タップ時のアクション
		private class ViewTapListener implements OnViewTapListener {
			int onCenterTouchFlag = 0;

			@Override
			public void onViewTap(View view, float x, float y) {

				// Viewサイズを取得する
				float viewWidth = view.getWidth();
				//float viewHeight = view.getHeight();
				int xPercentage = (int) ((x / viewWidth) * 100);
				//int yPercentage = (int) ((y / viewHeight) * 100);
				//Toast.makeText(ImagePagerActivity.this, "tap! " + xPercentage + " - " + yPercentage, Toast.LENGTH_SHORT).show();
				ViewGroup parent = (ViewGroup) view.getParent();	// 引数で得られるのは PhotoView の view なので、その親の Activity の view を取得
				View imageLayout = inflater.inflate(R.layout.item_pager_image, parent, true);	// 第三パラメータの attachToRoot は、ここでは true 必須
				SeekBar pageSeekBar = (SeekBar) imageLayout.findViewById(R.id.page_seek_bar);	// シークバー
				TextView headerInfo = (TextView) imageLayout.findViewById(R.id.header_info); //ヘッダー
				TextView imageExplanation = (TextView) imageLayout.findViewById(R.id.image_explanation); //画像説明文

				// 画面縦に 1/3 ずつでクリック範囲を区切る
				if (xPercentage <33) {
					mPager.arrowScroll(View.FOCUS_LEFT);	// 前のページへ遷移させる
				} else if (xPercentage <66) {
					// 領域をタップすると、ウインドウマネージャーとページヘッダーと画像説明文とシークバーを表示する。
					// 再度タップすると、表示したそれらを再び非表示にする。
					if (onCenterTouchFlag == 0) {
						onCenterTouchFlag = 1;
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						pageSeekBar.setProgress(mPager.getCurrentItem());
						pageSeekBar.setVisibility(View.VISIBLE);
						int nowPage;	// 現在ページ数
						if (mOpeingType.equals("LEFT_OPENING"))  {	// 左開き
							nowPage = mPager.getCurrentItem() - 1;
						} else { // 右開き
							nowPage = (getCount() - 4)  - (mPager.getCurrentItem() - PAGE_START_NUM);
						}
						int totalPage = getCount() - 4;

						// ページヘッダー表示
						headerInfo.setText(mBookTitleNameText + " - No. " + nowPage + " / " + totalPage);
						headerInfo.setVisibility(View.VISIBLE);

						// 画像説明文表示
						boolean isImageExplanation = true;
						try {
							String explanationText = mBookExplanationTexts[mPager.getCurrentItem() - PAGE_START_NUM];
							explanationText = explanationText.replaceAll("\n", "<br>");
							
							if (explanationText.equals("")) {
								isImageExplanation = false;
							}

							// <a> タグ利用の準備
							MovementMethod movementmethod = LinkMovementMethod.getInstance(); // LinkMovementMethod のインスタンスを取得
							imageExplanation.setMovementMethod(movementmethod);// TextView に LinkMovementMethod を登録
							CharSequence spanned = Html.fromHtml(explanationText);// URLSpan をテキストにを組み込みます
							// 画像説明文をビューにセット
							imageExplanation.setText(spanned);
						} catch (ArrayIndexOutOfBoundsException e) {
							// 画像説明文が無い場合
							isImageExplanation = false;
						}
						if (isImageExplanation == true) {
							imageExplanation.setVisibility(View.VISIBLE);
						}

					} else {
						onCenterTouchFlag = 0;
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						pageSeekBar.setVisibility(View.GONE);
						headerInfo.setVisibility(View.GONE);
						imageExplanation.setVisibility(View.GONE);
					}
					//pageSeekBar.setVisibility(View.VISIBLE);

				} else {
					mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
				}
				///Log.v("onPhotoTap：" + xPercentage + " " + yPercentage + " page:" + mPager.getCurrentItem(), "INFO");
			}
		} //ViewTapListener

		// 画面ロングタップ時のアクション
		private class LongClickListener implements OnLongClickListener {

			@Override
			public boolean onLongClick(View view) {
				mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
				Log.v("onLongClick page:" + mPager.getCurrentItem(), "INFO");
				return true;
			}
		}

		// 画面スワイプ時のアクション
		// PhotoView で画像を拡大して移動操作をしてる状況下において、ViewPager の機能で次の画像に移ってしまうことを避ける
		private class MatrixChangeListener implements OnMatrixChangedListener {

			@Override
			public void onMatrixChanged(RectF rect) {
				try {
					// asaViewPager のスワイプ機能を一旦停止させる
					mPager.setCanSwipeMode(asaViewPager.CAN_SWIPE_NONE);

					// ビューウィンドウの横幅サイズ (dot) を取得
					float displaysizeWidth = mPager.getWidth();

					// RectF(left, top, right, bottom)
					//float imageHeight = rect.height(); // 画像サイズ縦幅（ズーミング後）
					float imageWidth = rect.width(); // 画像サイズ横幅（ズーミング後）

					// 右端、左端に近かったら、asaViewPager のスワイプを可にする
					// 左端の時は rect.left が 0 、右端の時は rect.right が端末の解像度（x dot)で判定できる
					// （画像を画面サイズ以上に拡大していなかったら、条件は常に成り立つ）
					if (rect.left > -1) { // 左端
						if (imageWidth > displaysizeWidth) {
							mPager.setCanSwipeMode(asaViewPager.CAN_SWIPE_RIGHT_ONLY);	// 右スワイプで左のページにのみ遷移許可
						} else {
							mPager.setCanSwipeMode(asaViewPager.CAN_SWIPE_ALL);
						}
					} else if (rect.right < displaysizeWidth + 1) { // 右端
						if (imageWidth > displaysizeWidth) {
							mPager.setCanSwipeMode(asaViewPager.CAN_SWIPE_LEFT_ONLY);	// 左スワイプで左のページにのみ遷移許可
						} else {
							mPager.setCanSwipeMode(asaViewPager.CAN_SWIPE_ALL);
						}
					}
				} catch (NullPointerException e) {
					Log.v("MatrixChangeListener - onMatrixChanged", "NullPointerException");
				}
			} //onMatrixChanged

		} //MatrixChangeListener




		public class IntroduceWish implements OnClickListener {

			@Override
			public void onClick(View v) {
				String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();

				// 書籍名を URL エンコード化
				String bookTitleName = mBookTitleNameText;
				try {
					bookTitleName = URLEncoder.encode(bookTitleName, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					bookTitleName = "";
					e.printStackTrace();
				}

				Intent intent;
				String caseType = "";
				switch(v.getId()){
				case R.id.introduce_by_email:
					caseType = "introduce_by_email";
					Uri uri=Uri.parse("mailto:");
					intent=new Intent(Intent.ACTION_SENDTO,uri);
					intent.putExtra(Intent.EXTRA_SUBJECT, mBookTitleNameText);
					intent.putExtra(Intent.EXTRA_TEXT, "『" + mBookTitleNameText + "』" + appUrl + "");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case R.id.introduce_by_googleplay:
					caseType = "introduce_by_googleplay";
					Uri uri2=Uri.parse(appUrl);
					intent=new Intent(Intent.ACTION_VIEW,uri2);
					startActivity(intent);
					break;
				case R.id.introduce_by_twitter:
					caseType = "introduce_by_twitter";
					Uri uri3=Uri.parse("http://twitter.com/share?url=" + appUrl + "&text=『" + bookTitleName + "』");
					intent=new Intent(Intent.ACTION_VIEW,uri3);
					startActivity(intent);
					break;
				case R.id.introduce_by_facebook:
					caseType = "introduce_by_facebook";
					Uri uri4=Uri.parse("http://www.facebook.com/sharer.php?u=" + appUrl + "&t=『" + bookTitleName + "』");
					intent=new Intent(Intent.ACTION_VIEW,uri4);
					startActivity(intent);
					break;
				case R.id.introduce_by_Line:
					caseType = "introduce_by_Line";
					Uri uri5=Uri.parse("http://line.naver.jp/msg/text/『" + bookTitleName + "』 " + appUrl + "");
					intent=new Intent(Intent.ACTION_VIEW,uri5);
					startActivity(intent);
					break;
				case R.id.introduce_by_googleplus:
					caseType = "introduce_by_googleplus";
					Uri uri6=Uri.parse("https://plus.google.com/share?url=" + appUrl + "");
					intent=new Intent(Intent.ACTION_VIEW,uri6);
					startActivity(intent);
					break;
				case R.id.introduce_view_to_firstpage:
					if (mOpeingType.equals("LEFT_OPENING"))  {	// 左開き
						mPager.setCurrentItem(PAGE_START_NUM);
					} else { // 右開き
						mPager.setCurrentItem( (getCount() - 2) - PAGE_START_NUM + 1);
					}
					break;
				case R.id.introduce_view_to_top:
					Log.v("ImagePagerActivity - introduce_view_to_top", "mIsBookInterstitialAdAfterRead=" + mIsBookInterstitialAdAfterRead);
					if (mIsBookInterstitialAdAfterRead == true) {
							try {
								if (mInterstitialAdView.isLoaded()) {
									mInterstitialAdView.show();
									Log.v("mInterstitialAdView - isLoaded", 	"show");
								}
							} catch (NullPointerException e) {
								Log.w("mInterstitialAdView - isLoaded", 	"NullPointerException");
							}
					}
					finish();
					break;
				}

				// アクセス解析
				if (caseType != "") {
					EasyTracker.getInstance(getApplicationContext()).send(MapBuilder.createEvent("IntroduceWish", getPackageName(), caseType, null).build());
				}

			}

		} //IntroduceWish

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.v("ImagePagerAdapter - destroyItem p=" +position, "INFO");
			container.removeView((View) object);	// コンテナの廃棄

		}

		@Override
		public int getCount() {
			return images.length;
		}

	} // ImagePagerAdapter



	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.v("ImagePagerActivity - onSaveInstanceState", "INFO");
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// バックキーが押されたときの挙動
		if(keyCode==KeyEvent.KEYCODE_BACK){
			this.onDestroy();
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		Log.v("ImagePagerActivity - onDestroy", "INFO");

		// 広告関連オブジェクトの開放
		if (mBunnerAdView != null) {
			mBunnerAdView.destroy();
		}
		mBunnerAdView = null;
		mInterstitialAdView = null;
		mAdRequestBanner = null;
		mAdRequestInterstitial01 = null;
		mAdRequestInterstitial = null;

		// viewPager オブジェクトの開放
		if (mPager != null) {
			mPager.setAdapter(null);
		}
		mPager = null;

		if (mImageLoader != null) {
			// mImageLoader.destroy();  // ここで ImageLoader を終わらせると２回目以降の閲覧がエラーになる
		}
		mImageLoader = null;

		// おまじない
		finish();
		System.gc();

		super.onDestroy();
	}



} //ImagePagerActivity




