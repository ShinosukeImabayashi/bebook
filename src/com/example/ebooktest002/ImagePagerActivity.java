/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.ebooktest002;
/*
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.sample.R;
*/
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.util.Log;

import com.example.ebooktest002.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends BaseActivity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;

	ViewPager pager;

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
		//int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		String[] imageUrls = {
				"http://www.imabaya.com/testimage/noimage.jpg",
				"http://www.imabaya.com/testimage/noimage.jpg",
				"http://www.imabaya.com/testimage/niji/tumblr_mzamkp9o601qzlc8oo1_500.jpg",
				"http://www.imabaya.com/testimage/niji/1380943878613.gif",
				"http://www.imabaya.com/testimage/niji/capture-20131203-155301.png",
				"http://www.imabaya.com/testimage/niji/32777694.jpg",
				"http://www.imabaya.com/testimage/niji/tumblr_mzcbmpiPO71sb8y8jo1_500.jpg",
				"http://www.imabaya.com/testimage/niji/33687665_p3-horz.jpg",
				"http://www.imabaya.com/testimage/niji/1387682467421.jpg",
				"http://www.imabaya.com/testimage/niji/1386472153102.png",
				"http://www.imabaya.com/testimage/niji/1386118585036.jpg",
				"http://www.imabaya.com/testimage/niji/1386063445429.jpg",
				"http://www.imabaya.com/testimage/niji/1385438040214.jpg",
				"http://www.imabaya.com/testimage/niji/1385430383037.jpg",
				"http://www.imabaya.com/testimage/niji/1385418265951.jpg",
				"http://www.imabaya.com/testimage/niji/1310131s.jpg",
		};
		int pagerPosition = 0;

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
			.imageScaleType(ImageScaleType.NONE) // 画面サイズに合わせた画像の拡大縮小処理は PhotoView 側に任せる
			//.imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // Open DL の最大 Bitmap サイズが 2048*2048。元画像が範囲内でも、ここで画像自体を拡大縮小処理すると、上限値オーバーになることが有る。（極端に縦長 or 横長の場合）
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

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

	    private PhotoViewAttacher mAttacher;

	    public boolean onLoadingStartedduplicateCheck = false;

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



	    private class ViewTapListener implements OnViewTapListener {
	        @Override
	        public void onViewTap(View view, float x, float y) {
	            float xPercentage = x * 100f;
	            float yPercentage = y * 100f;
	            //Toast.makeText(ImagePagerActivity.this, "tap! " + xPercentage + " " + yPercentage, Toast.LENGTH_SHORT).show();
	            pager.arrowScroll(View.FOCUS_RIGHT);
				Log.i("onPhotoTap：" + xPercentage + " " + yPercentage + " page:" + pager.getCurrentItem(), "INFO");
	        }
	    }

	    private class MatrixChangeListener implements OnMatrixChangedListener {
	        @Override
	        public void onMatrixChanged(RectF rect) {
	            //mCurrMatrixTv.setText(rect.toString());
	        	Toast.makeText(ImagePagerActivity.this, "onMatrixChanged" , Toast.LENGTH_SHORT).show();
	        }
	    }


	    @Override
	    // 各ページ生成時のタイミングで呼び出される
		public Object instantiateItem(ViewGroup view, int position) {
			Log.i("ImagePagerAdapter - instantiateItem p=" +position, "INFO");
	    	//レイアウトファイル item_pager_image をインスタンス化する
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.pageimage);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			imageView.setBackgroundColor(Color.BLACK);

			//■ 得られた imageView は PhotoViewAttacher を通して表示させるようにする
			mAttacher = new PhotoViewAttacher(imageView);
			//mAttacher.setScaleType(ScaleType.CENTER_CROP);




			// position は 0 から、pager.getCurrentItem() は 0 からカウントスタート
			setTitle("ページ：" + position+ "-" + pager.getCurrentItem());
			Log.i("ページ：" + position + "-" + pager.getCurrentItem(), "INFO");

			///Toast.makeText(ImagePagerActivity.this, "ページ： " + position , Toast.LENGTH_SHORT).show();

			// imageLoader で画像の読み込み処理と表示処理を一括して行う
			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);

					if (pager.getCurrentItem() ==0) {
						if (onLoadingStartedduplicateCheck == false) {
							onLoadingStartedduplicateCheck = true;

						} else {

						}
					}
					Log.i("imageLoader - onLoadingStarted p=" +pager.getCurrentItem() + " " + onLoadingStartedduplicateCheck, "INFO");
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

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					Log.i("imageLoader - onLoadingComplete p=" +pager.getCurrentItem() , "INFO");
					spinner.setVisibility(View.GONE);

					if (pager.getCurrentItem() == 0) {

					} else {

						///Toast.makeText(ImagePagerActivity.this, "ページ： " + pager.getCurrentItem() , Toast.LENGTH_SHORT).show();

				        // PhotoViewAttacher にお願いして表示画像を表示領域にフィットさせる
						// 本当は instantiateItem 呼び出し直下で指定したいのだけど、そこで指定しても上手く反映がされなかったため苦肉の策でここに
						// 画像サイズが大きすぎる（1MByte 以上～）と、処理が追いつかなくて上手く行ったりいかなかったりする
						ImageView imageView = (ImageView)findViewById(R.id.pageimage);
						if (imageView != null) {	// imageView の取得がたまに失敗することがある
							mAttacher = new PhotoViewAttacher(imageView);

							if(pager.getCurrentItem() == 0) {
								//setTitle("最初！");
							}

							//mAttacher.setScaleType(ScaleType.FIT_CENTER);
							mAttacher.setOnViewTapListener(new ViewTapListener());	// タップイベントのリスナー設定
						}

					}
				}

			});



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
	}


	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK){
	    	this.onDestroy();
	        return super.onKeyDown(keyCode, event);
	    }
	    return super.onKeyDown(keyCode, event);
	  }

	  public synchronized void sleep(long msec)	// 指定ミリ秒の間、完全スリープ  
	    {
	    	try
	    	{
	    		wait(msec);
	    	}catch(InterruptedException e){}
	    }
}




