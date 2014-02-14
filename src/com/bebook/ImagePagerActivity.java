
package com.bebook;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;


import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.util.Log;

import com.bebook.Constants.Extra;
import com.bebook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;




public class ImagePagerActivity extends BaseActivity  {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;

	ViewPager pager;

	UILApplication uap;
	String[] imageUrls;



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ImagePagerActivity - onCreate", "INFO");
		// ウインドウ全画面化＆タイトルバー非表示化
		//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//       requestWindowFeature(Window.FEATURE_NO_TITLE);

        // layout xml を当該アクティビティのビューに結びつける
        setContentView(R.layout.ac_image_pager);
        //Toast.makeText(ImagePagerActivity.this, "start", Toast.LENGTH_SHORT).show();

        // 画像リソースの下準備
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		//String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		int mSelectListPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		// 設定情報関連
		uap = (UILApplication) this.getApplication();
		///imageUrls = uap.ebookconst.getImageUrls();

		// 書籍情報オブジェクトの取得
		BookList booklist = uap.getBooklist();

		// 書籍画像 URL リストの取得
		imageUrls = booklist.getBookImageUrl(mSelectListPosition);

		// リストの先頭に空文字列を２アイテム挿入しておく
		String[] templist = new String[imageUrls.length + 2];
		templist[0] = "";
		templist[1] = "";
		for (int i=0; i <imageUrls.length;  i++ ) {
			templist[i + 2] = imageUrls[i].toString();
		}
		imageUrls = templist;


		// 初期ページ数設定
		int pagerPosition = 2;	// ★position 0 と 1 は初期化処理がおかしいので使わないようにする

		// 端末回転による縦横変換を行った際の同一ページ保持（これが無いと端末回転した際に最初のページに戻る）
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		// universalimageloader の DisplayImageOptions 設定値初期設定
		options = new DisplayImageOptions.Builder()
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

		// 表示領域と 画像とをアダプターで関連付ける
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);

	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i("ImagePagerActivity - onSaveInstanceState", "INFO");
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}


	// 画像の表示管理全般
	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;
	    private PhotoViewAttacher mAttacher;
	    public boolean onLoadingStartedduplicateCheck = false;


	    @Override
	    // 各ページ生成時のタイミングで呼び出される
		public Object instantiateItem(ViewGroup view, int position) {
			Log.i("ImagePagerAdapter - instantiateItem p=" +position, "INFO");

	    	//レイアウトファイル item_pager_image をインスタンス化する
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.pageimage);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);		// 読み込み中にアニメーション表示する
			imageView.setBackgroundColor(Color.BLACK);

			final ImageButton startReadButton = (ImageButton) imageLayout.findViewById(R.id.startReadButton);
			final ImageButton pageRightSwipeGuideImage = (ImageButton) imageLayout.findViewById(R.id.page_right_swipe_guide_image);

			if (pager.getCurrentItem() == 0) {
				//startReadButton.setVisibility(View.VISIBLE);
			}

			// シークバー
			// どこまで読み込み中かを表示する
			final SeekBar pageSeekBar = (SeekBar) imageLayout.findViewById(R.id.pageSeekBar);
			pageSeekBar.setMax(this.getCount());
			pageSeekBar.setProgress(position +1);
			pageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	            // トラッキング開始時に呼び出されます
	            @Override
	            public void onStartTrackingTouch(SeekBar pageSeekBar) {
	                Log.v("onStartTrackingTouch()",
	                    String.valueOf(pageSeekBar.getProgress()));
	            }
	            // トラッキング中に呼び出されます
	            @Override
	            public void onProgressChanged(SeekBar pageSeekBar, int progress, boolean fromTouch) {
	                Log.v("onProgressChanged()",
	                    String.valueOf(progress) + ", " + String.valueOf(fromTouch));
	            }
	            // トラッキング終了時に呼び出されます
	            @Override
	            public void onStopTrackingTouch(SeekBar pageSeekBar) {
	                Log.v("onStopTrackingTouch()",
	                    String.valueOf(pageSeekBar.getProgress()));
	                 pager.setCurrentItem(pageSeekBar.getProgress()); 

	            }
	        });


/*
			//■ 得られた imageView は PhotoViewAttacher を通して表示させるようにする
			mAttacher = new PhotoViewAttacher(imageView);
			//mAttacher.setScaleType(ScaleType.CENTER_CROP);
			mAttacher.setScaleType(ScaleType.FIT_CENTER);
			mAttacher.setOnViewTapListener(new ViewTapListener());	// タップイベントのリスナー設定
			mAttacher.setOnLongClickListener(new LongClickListener());
*/

			// position は 0 から、pager.getCurrentItem() は 0 からカウントスタート
			setTitle("instantiateItem position：" + position+ "-pager.currentitem:" + pager.getCurrentItem());
			Log.i("instantiateItem position：" + position + "-pager.currentitem:" + pager.getCurrentItem(), "INFO");

			///Toast.makeText(ImagePagerActivity.this, "ページ： " + position , Toast.LENGTH_SHORT).show();

			if (position == 0) {
				Log.i("no load! position：" + position + "-pager.currentitem:" + pager.getCurrentItem(), "INFO");
				pageRightSwipeGuideImage.setVisibility(View.VISIBLE);
					//position = 2;
					//pager.setCurrentItem(2);
			} else if (position == 1) {
				startReadButton.setVisibility(View.VISIBLE);
				Log.i("no load! position：" + position + "-pager.currentitem:" + pager.getCurrentItem(), "INFO");
			} else {

				// imageLoader で画像の読み込み処理を行う
				imageLoader.loadImage(images[position], options, new ImageLoadingListener() {

					// 画像ロード中は読み込み中表示を出す
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
						Log.i("imageLoader - onLoadingStarted pager=" +pager.getCurrentItem() + onLoadingStartedduplicateCheck, "INFO");
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
						Log.i("imageLoader - onLoadingComplete p=" +pager.getCurrentItem() + " " + onLoadingStartedduplicateCheck, "INFO");
						spinner.setVisibility(View.GONE);

						pageSeekBar.setProgress(pager.getCurrentItem());

						///Toast.makeText(ImagePagerActivity.this, "ページ： " + pager.getCurrentItem() , Toast.LENGTH_SHORT).show();

						if (pager.getCurrentItem() ==0 && onLoadingStartedduplicateCheck == false) {
							return;
						} else {
							onLoadingStartedduplicateCheck = true;

					        // PhotoViewAttacher にお願いして表示画像を表示領域にフィットさせる
							// 本当は instantiateItem 呼び出し直下で指定したいのだけど、そこで指定しても上手く反映がされなかったため苦肉の策でここに
							// 画像サイズが大きすぎる（1MByte 以上～）と、処理が追いつかなくて上手く行ったりいかなかったりする

							//ImageView imageView = (ImageView)findViewById(R.id.pageimage);

							if (imageView != null) {	// imageView の取得がたまに失敗することがある
								imageView.setImageBitmap(loadedImage);

								mAttacher = new PhotoViewAttacher(imageView);
								mAttacher.setScaleType(ScaleType.FIT_CENTER);	// 画像をセンターでフィットさせる
								mAttacher.setOnViewTapListener(new ViewTapListener());	// タップイベントのリスナー設定
								mAttacher.setOnLongClickListener(new LongClickListener());	// ロングクリックイベントのリスナー設定
							}

						}

					}

					// 読み込みキャンセル時
					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}

				});
			}


			view.addView(imageLayout, 0);

			return imageLayout;
		}

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
			Log.i("ImagePagerAdapter - ImagePagerAdapter", "INFO");
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.i("ImagePagerAdapter - destroyItem p=" +position, "INFO");
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		// 画面タップ時のアクション
	    private class ViewTapListener implements OnViewTapListener {
	        @Override
	        public void onViewTap(View view, float x, float y) {
	            float xPercentage = x * 100f;
	            float yPercentage = y * 100f;
	            //Toast.makeText(ImagePagerActivity.this, "tap! " + xPercentage + " " + yPercentage, Toast.LENGTH_SHORT).show();
	            pager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
				Log.i("onPhotoTap：" + xPercentage + " " + yPercentage + " page:" + pager.getCurrentItem(), "INFO");
	        }
	    }

	    // 画面ロングタップ時のアクション
	    private class LongClickListener implements OnLongClickListener {

			@Override
			public boolean onLongClick(View view) {
	            pager.arrowScroll(View.FOCUS_RIGHT);	// 次のページへ遷移させる
				Log.i("onLongClick page:" + pager.getCurrentItem(), "INFO");

	            return true;
			}
	    }


	    private class MatrixChangeListener implements OnMatrixChangedListener {
	        @Override
	        public void onMatrixChanged(RectF rect) {
	            //mCurrMatrixTv.setText(rect.toString());
	        	Toast.makeText(ImagePagerActivity.this, "onMatrixChanged" , Toast.LENGTH_SHORT).show();
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




