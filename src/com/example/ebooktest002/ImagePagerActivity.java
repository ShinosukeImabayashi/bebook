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
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;


import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

		// ウインドウ全画面化＆タイトルバー非表示化
		//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//       requestWindowFeature(Window.FEATURE_NO_TITLE);

        // layout xml を当該アクティビティのビューに結びつける
        setContentView(R.layout.ac_image_pager);

        // 画像リソースの下準備
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		//String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		//int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		String[] imageUrls = {
				"http://www.imabaya.com/testimage/niji/tumblr_mzamkp9o601qzlc8oo1_500.jpg",
				"http://www.imabaya.com/testimage/water/IMGP0583.jpg",
				"http://www.imabaya.com/testimage/niji/1380943878613.gif",
				"http://www.imabaya.com/testimage/water/IMGP1508.jpg",
				"http://www.imabaya.com/testimage/niji/32777694.jpg",
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
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

		// 表示領域と 画像とをアダプターで関連付ける
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}


	    private PhotoViewAttacher mAttacher;
	    private Matrix mCurrentDisplayMatrix = null;
	    private class PhotoTapListener implements OnPhotoTapListener {

	        @Override
	        public void onPhotoTap(View view, float x, float y) {
	            float xPercentage = x * 100f;
	            float yPercentage = y * 100f;

	            //showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage));
	        }
	    }

	    private void showToast(CharSequence text) {
	        //if (null != mCurrentToast) {
	        //    mCurrentToast.cancel();
	        //}

	        //mCurrentToast = Toast.makeText(SimpleSampleActivity.this, text, Toast.LENGTH_SHORT);
	        //mCurrentToast.show();
	    }

	    private class MatrixChangeListener implements OnMatrixChangedListener {

	        @Override
	        public void onMatrixChanged(RectF rect) {
	            //mCurrMatrixTv.setText(rect.toString());
	        }
	    }







	    @Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);




		    // imageLoader で画像の読み込み処理を行う
			imageLoader.loadImage(images[position], options, new SimpleImageLoadingListener() {
		        @Override
		        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		        	ImageView imageView = (ImageView)findViewById(R.id.image);
		        	imageView.setImageBitmap(loadedImage);

			        // The MAGIC happens here!
			        mAttacher = new PhotoViewAttacher(imageView);

			        // Lets attach some listeners, not required though!
			     //// mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
			     // mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		        }
		    });


/*
			// imageLoader で画像の読み込み処理と表示処理を一括して行う
			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
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
					spinner.setVisibility(View.GONE);
				}
			});
*/



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
}























