package com.bebook;

import java.util.Map;

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


public class StartActivity extends FragmentActivity implements LoaderCallbacks<BookList> {

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
			startBookListLoadData("http://www.imabaya.com/ebooktest/ebookconst.xml");	// ネット越しに設定情報 xml ファイルを読み込み
		}

		// Loaderの初期化
		public void startBookListLoadData(String url) {

			// BookList.xml 設定ファイル呼び出し
			Bundle args1 = new Bundle();
			args1.putString("url", url);
			getSupportLoaderManager().initLoader(0, args1, this);    // onCreateLoaderが呼ばれる
	    }

	    @Override
	    public Loader <BookList> onCreateLoader(int id, Bundle args) {

	        // 非同期で処理を実行するLoaderを生成
	        // id の区分けにより様々な非同期処理に対応可能
	        if (id == 0) {
	            String url = args.getString("url");
	            return new BookList(this, url);		// 書籍情報 BookList 呼び出しオブジェクト生成
	        }
            return null;
	    }


        // ロード完了時
	    @Override
	    public void onLoadFinished(Loader <BookList> arg0, BookList booklist) {

	    	// 取得したデータを UILApplication に入れて永続化する
	    	uap.setBooklist(booklist);

	        Log.d("StartActivity BookList - onLoaderReset", "onLoaderReset");

	        // 次の表紙リスト一覧画面へ遷移
			Intent intent = new Intent(this, ImageListActivity.class);
			startActivity(intent);
			// このアクティビティは任務を終えて終了。バックキーで戻らないようにしておく。
			finish();
	    }

	    @Override
	    public void onLoaderReset(Loader <BookList> arg0) {
	        Log.e("StartActivity BookList - onLoaderReset", "onLoaderReset");
	    }


}

