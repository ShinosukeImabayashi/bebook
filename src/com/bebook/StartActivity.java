package com.bebook;

import java.util.Map;

import com.bebook.Constants.Extra;
import com.nostra13.universalimageloader.core.ImageLoader;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class StartActivity extends FragmentActivity implements OnClickListener, LoaderCallbacks<BookList> {

	UILApplication uap;

	Button startbutton;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			Log.d("StartActivity", "onCreate");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.start);

			// キャッシュクリア
			ImageLoader imageLoader = ImageLoader.getInstance();
			//imageLoader.clearMemoryCache();
			//imageLoader.clearDiscCache();


			// 設定情報関連
			uap = (UILApplication) this.getApplication();
			///uap.ebookconst.getImageUrls2();
			startBookListLoadData("http://www.imabaya.com/ebooktest/ebookconst.xml");


			startbutton = (Button) findViewById(R.id.button1);
			startbutton.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(this, ImageListActivity.class);
			startActivity(intent);

		}



		// Loaderの初期化から起動までを行います
		public void startBookListLoadData(String url) {

			// BookList.xml 設定ファイル呼び出し
			Bundle args1 = new Bundle();
			args1.putString("url", url);
			getSupportLoaderManager().initLoader(0, args1, this);    // onCreateLoaderが呼ばれます



	    }





	    @Override
	    public Loader <BookList> onCreateLoader(int id, Bundle args) {

	        // 非同期で処理を実行するLoaderを生成します.
	        // ここを切り替えてあげるだけで様々な非同期処理に対応できます.
	        if (id == 0) {
	            String url = args.getString("url");
	            return new BookList(this, url);
	        }
            return null;
	    }







	    @Override
	    public void onLoadFinished(Loader <BookList> arg0, BookList booklist) {

	    	uap.setBooklist(booklist);

	        // 非同期処理終了
	        Log.d("StartActivity BookList - onLoaderReset", "onLoaderReset");

			Intent intent = new Intent(this, ImageListActivity.class);
			startActivity(intent);
	    }

	    @Override
	    public void onLoaderReset(Loader <BookList> arg0) {
	        Log.d("StartActivity BookList - onLoaderReset", "onLoaderReset");
	    }


}

