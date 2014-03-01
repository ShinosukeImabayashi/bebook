
package com.bebook;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.bebook.Constants.Extra;
import com.bebook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


public class ImagePagerActivity extends BaseActivity  {

	protected ImageLoader mImageLoader = ImageLoader.getInstance();

	private static final String STATE_POSITION = "STATE_POSITION";

	private UILApplication mUap;

	private DisplayImageOptions mDisplayImageOptions;
	private asaViewPager mPager;

	private String[] mBookImageUrls;	// 書籍の画像 URL リスト
	private String mBookTitleNameText;	// 書籍のタイトルテキスト
	private String mBookPublicationText;	// 書籍の奥付けテキスト



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
		int mSelectListPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		// 設定情報関連
		mUap = (UILApplication) this.getApplication();

		// 書籍情報オブジェクトの取得
		BookList booklist = mUap.getBooklist();

		// 書籍画像 URL リストの取得
		mBookImageUrls = booklist.getBookImageUrl(mSelectListPosition);

		// 書籍のタイトルや奥付けテキストの取得
		mBookTitleNameText  = booklist.getBookTitleNameText(mSelectListPosition);
		mBookPublicationText  = booklist.getBookPublicationText(mSelectListPosition);

		// リストの先頭に空文字列を２アイテム、末尾に２アイテム挿入しておく
		String[] templist = new String[mBookImageUrls.length + 4];
		templist[0] = "";
		templist[1] = "";
		for (int i=0; i <mBookImageUrls.length;  i++ ) {
			templist[i + 2] = mBookImageUrls[i].toString();
		}
		templist[mBookImageUrls.length + 2] = "";
		templist[mBookImageUrls.length + 3] = "";
		mBookImageUrls = templist;


		// 初期ページ数設定
		int pagerPosition = 2;	// ★position 0 と 1 は初期化処理がおかしいので使わないようにする

		// 端末回転による縦横変換を行った際の同一ページ保持（これが無いと端末回転した際に最初のページに戻る）
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		// universalimageloader の DisplayImageOptions 設定値初期設定
		mDisplayImageOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
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
		mPager.setCurrentItem(pagerPosition);
		//mPager.setRotationX(33);	// 表示領域を傾けてスターウォーズ風になる。ちょっとおもしろい。


		// アクセス解析
		EasyTracker.getInstance(this).send(MapBuilder.createEvent("uition", "butpress", "play_button", null).build());
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.v("ImagePagerActivity - onSaveInstanceState", "INFO");
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}


	// 画像の表示管理全般
	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;
		private PhotoViewAttacher mAttacher;

		@Override
		// 各ページ生成時のタイミングで呼び出される
		public Object instantiateItem(ViewGroup view, int position) {
			Log.v("ImagePagerAdapter - instantiateItem p=" +position, "INFO");

			//レイアウトファイル item_pager_image をインスタンス化する
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.pageimage);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);		// 読み込み中にアニメーション表示する
			imageView.setBackgroundColor(Color.BLACK);

			// アナウンス系
			final ImageButton pageRightSwipeGuideImage = (ImageButton) imageLayout.findViewById(R.id.page_right_swipe_guide_image);

			// 書籍奥付け表示領域
			final TextView  bookSummaryInfo = (TextView)  imageLayout.findViewById(R.id.book_summary_info);
			final ScrollView bookSummaryInfoScrollParent =  (ScrollView)  imageLayout.findViewById(R.id.book_summary_info_scrollparent);

			// 書籍情報レビューお願い領域
			final ScrollView introduceScrollParent =  (ScrollView)  imageLayout.findViewById(R.id.introduce);
			final TextView  introduceWishText = (TextView)  imageLayout.findViewById(R.id.introduce_wish_text);
			final Button  introduce_by_email = (Button)  imageLayout.findViewById(R.id.introduce_by_email);
			final Button  introduce_by_googleplay = (Button)  imageLayout.findViewById(R.id.introduce_by_googleplay);
			final Button  introduce_by_twitter = (Button)  imageLayout.findViewById(R.id.introduce_by_twitter);
			final Button  introduce_by_facebook = (Button)  imageLayout.findViewById(R.id.introduce_by_facebook);
			final Button  introduce_by_Line = (Button)  imageLayout.findViewById(R.id.introduce_by_Line);
			final Button  introduce_by_googleplus = (Button)  imageLayout.findViewById(R.id.introduce_by_googleplus);
			introduceWishText.setText(Html.fromHtml(getString(R.string.introduce_wish_text)));
			introduce_by_email.setText(getString(R.string.introduce_by_email));
			introduce_by_googleplay.setText(getString(R.string.introduce_by_googleplay));
			introduce_by_twitter.setText(getString(R.string.introduce_by_twitter));
			introduce_by_facebook.setText(getString(R.string.introduce_by_facebook));
			introduce_by_Line.setText(getString(R.string.introduce_by_Line));
			introduce_by_googleplus.setText(getString(R.string.introduce_by_googleplus));

			// シークバー
			// どこまで読み込み中かを表示する
			final SeekBar pageSeekBar;
			pageSeekBar = (SeekBar) imageLayout.findViewById(R.id.pageSeekBar);
			pageSeekBar.setMax(this.getCount()-2);

			pageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				// シークバー トラッキング開始時
				@Override
				public void onStartTrackingTouch(SeekBar pageSeekBar) {
					Log.v("onStartTrackingTouch()",
							String.valueOf(pageSeekBar.getProgress()));
				}
				// シークバー トラッキング中
				@Override
				public void onProgressChanged(SeekBar pageSeekBar, int progress, boolean fromTouch) {
					Log.v("onProgressChanged()",
							String.valueOf(progress) + ", " + String.valueOf(fromTouch));
				}
				// シークバー トラッキング終了時
				@Override
				public void onStopTrackingTouch(SeekBar pageSeekBar) {
					Log.v("onStopTrackingTouch()",
							String.valueOf(pageSeekBar.getProgress()));
					mPager.setCurrentItem(pageSeekBar.getProgress());
				}
			});

			// position は 0 から、mPager.getCurrentItem() は 0 からカウントスタート
			//（※現在表示ページの確認に position の値を使うのは不適当。非同期処理ゆえ）
			// setTitle("instantiateItem position：" + position+ "-mPager.currentitem:" + mPager.getCurrentItem());
			///Toast.makeText(ImagePagerActivity.this, "ページ： " + position , Toast.LENGTH_SHORT).show();
			Log.v("instantiateItem position：" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
			if (position == 0) {	// -2 ページ目
				Log.v("pageRightSwipeGuideImage position：" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
				pageRightSwipeGuideImage.setVisibility(View.VISIBLE);
				pageRightSwipeGuideImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("pageRightSwipeGuideImage:onClick" + mPager.getCurrentItem(), "INFO");
						mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
					}
				});

			} else if (position == 1) {	// -1 ページ目
				introduceScrollParent.setVisibility(View.VISIBLE);




/*
				// 書籍奥付け情報の表示
				bookSummaryInfo.setText(Html.fromHtml(mBookPublicationText));	// 簡易 HTML 文法が使用可能
				bookSummaryInfoScrollParent.setVisibility(View.VISIBLE);
				Log.v("bookSummaryInfo" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
				bookSummaryInfo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("bookSummaryInfo:onClick" + mPager.getCurrentItem(), "INFO");
						mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
					}
				});
*/
			} else if (position == this.getCount()-1) {	// 最終ページの後
				// 書籍奥付け情報の表示
				bookSummaryInfo.setText(Html.fromHtml(mBookPublicationText));
				bookSummaryInfoScrollParent.setVisibility(View.VISIBLE);
				Log.v("bookSummaryInfo position：" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
			} else if (position == this.getCount()-2) {	// 最終ページの後
				// 書籍奥付け情報の表示
				bookSummaryInfo.setText(Html.fromHtml(mBookPublicationText));
				bookSummaryInfoScrollParent.setVisibility(View.VISIBLE);
				Log.v("bookSummaryInfo position：" + position + "-mPager.currentitem:" + mPager.getCurrentItem(), "INFO");
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
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					// 画像読み込み完了時
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						Log.v("imageLoader - onLoadingComplete p=" +mPager.getCurrentItem() , "INFO");
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

				});
			}

			view.addView(imageLayout, 0);	// imageLayout をビューに紐付け

			return imageLayout;
		} // instantiateItem @Override 終わり

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

		ImagePagerAdapter(String[] images) {
			Log.v("ImagePagerAdapter - ImagePagerAdapter", "INFO");
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.v("ImagePagerAdapter - destroyItem p=" +position, "INFO");
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		// 画面タップ時のアクション
		private class ViewTapListener implements OnViewTapListener {
			int onCenterTouchFlag = 0;

			@Override
			public void onViewTap(View view, float x, float y) {
				Log.v("ImagePagerAdapter - onViewTap", "x=" + x);
				// Viewサイズを取得する
				float viewWidth = view.getWidth();
				float viewHeight = view.getHeight();
				int xPercentage = (int) ((x / viewWidth) * 100);
				int yPercentage = (int) ((y / viewHeight) * 100);
				//Toast.makeText(ImagePagerActivity.this, "tap! " + xPercentage + " - " + yPercentage, Toast.LENGTH_SHORT).show();
				ViewGroup parent = (ViewGroup) view.getParent();	// 引数で得られるのは PhotoView の view なので、その親の Activity の view を取得
				View imageLayout = inflater.inflate(R.layout.item_pager_image, parent, true);	// 第三パラメータの attachToRoot は、ここでは true 必須
				SeekBar pageSeekBar = (SeekBar) imageLayout.findViewById(R.id.pageSeekBar);
				TextView headerInfo = (TextView) imageLayout.findViewById(R.id.header_info);

				// 画面縦に 1/3 ずつでクリック範囲を区切る
				if (xPercentage <33) {
					mPager.arrowScroll(View.FOCUS_LEFT);	// 前のページへ遷移させる
				} else if (xPercentage <66) {
					// 領域をタップすると、ウインドウマネージャーとページヘッダーとシークバーを表示する。
					// 再度タップすると、表示したそれらを再び非表示にする。
					if (onCenterTouchFlag == 0) {
						onCenterTouchFlag = 1;
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						pageSeekBar.setProgress(mPager.getCurrentItem());
						pageSeekBar.setVisibility(View.VISIBLE);
						int nowPage = mPager.getCurrentItem() - 1;
						int totalPage = getCount() - 4;
						headerInfo.setText(mBookTitleNameText + " page :  " + nowPage + " / " + totalPage);
						headerInfo.setVisibility(View.VISIBLE);
					} else {
						onCenterTouchFlag = 0;
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						pageSeekBar.setVisibility(View.GONE);
						headerInfo.setVisibility(View.GONE);
					}
					//pageSeekBar.setVisibility(View.VISIBLE);

				} else {
					mPager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
				}
				Log.v("onPhotoTap：" + xPercentage + " " + yPercentage + " page:" + mPager.getCurrentItem(), "INFO");
			}
		}

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
				// asaViewPager のスワイプ機能を一旦停止させる
				mPager.setCanSwipeMode(mPager.CAN_SWIPE_NONE);

				// 端末解像度の取得
		        Display display = getWindowManager().getDefaultDisplay();
		        Point p = new Point();
		        display.getSize(p);
		        float displaysizeWidth = p.x;
		        float displaysizeHeight = p.y;
				//Log.v("ImagePagerAdapter - display.getSize", "x=" + displaysizeWidth + " y=" + mAttacher.getDisplayMatrix());

				// RectF(left, top, right, bottom)
				float imageHeight = rect.height(); // 画像サイズ縦幅（ズーミング後）
				float imageWidth = rect.width(); // 画像サイズ横幅（ズーミング後）

				//Log.v("MatrixChangeListener ", " imageWidth=" + imageWidth);

				// 右端、左端に近かったら、asaViewPager のスワイプを可にする
				// 左端の時は rect.left が 0 、右端の時は rect.right が端末の解像度（x dot)で判定できる
				// （画像を画面サイズ以上に拡大していなかったら、条件は常に成り立つ）
				if (rect.left > -1) { // 左端
					if (imageWidth > displaysizeWidth) {
							mPager.setCanSwipeMode(mPager.CAN_SWIPE_RIGHT_ONLY);	// 右スワイプで左のページにのみ遷移許可
					} else {
						mPager.setCanSwipeMode(mPager.CAN_SWIPE_ALL);
					}
				} else if (rect.right < displaysizeWidth + 1) { // 右端
					if (imageWidth > displaysizeWidth) {
							mPager.setCanSwipeMode(mPager.CAN_SWIPE_LEFT_ONLY);	// 左スワイプで左のページにのみ遷移許可
					} else {
						mPager.setCanSwipeMode(mPager.CAN_SWIPE_ALL);
					}
				}

			}

		}


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

	public synchronized void sleep(long msec) {	// 指定ミリ秒の間、完全スリープ
		try
		{
			wait(msec);
		}catch(InterruptedException e){}
	}



}




