package com.imabaya.asatsuki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.imabaya.asatsuki.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StartActivity extends FragmentActivity implements LoaderCallbacks<BookList> {

	UILApplication mUap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("StartActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		/// キャッシュクリア
		@SuppressWarnings("unused")
		ImageLoader imageLoader = ImageLoader.getInstance();
		//imageLoader.clearMemoryCache();
		//imageLoader.clearDiscCache();

		// 設定情報関連
		mUap = (UILApplication) this.getApplication();
		///mUap.ebookconst.getImageUrls2();
		startBookListLoadData(getString(R.string.book_const_xml_url));	// ネット越しに設定情報 xml ファイルを読み込み
	}

	// BookList.xml 設定ファイル呼び出し
	public void startBookListLoadData(String url) {
		Bundle args1 = new Bundle();
		args1.putString("url", url);
		getSupportLoaderManager().initLoader(0, args1, this);    // onCreateLoaderが呼ばれる
	}

	@Override
	public Loader <BookList> onCreateLoader(int id, Bundle args) {

		// 非同期で処理を実行する Loader オブジェクトを生成
		// （id の区分けにより様々な非同期処理に対応可能）
		if (id == 0) {
			String url = args.getString("url");
			return new BookList(this, url);		// 書籍情報 BookList 呼び出しオブジェクト生成
		}
		return null;
	}


	// ロード完了時
	@Override
	public void onLoadFinished(Loader <BookList> arg0, BookList booklist) {
		Log.v("StartActivity BookList - onLoadFinished", "onLoadFinished");

		// 取得したデータを UILApplication オブジェクトに入れて永続化する
		mUap.setBooklist(booklist);
		if (booklist.mErrorStatus != null) {
			if (booklist.mErrorStatus.equals("UnknownHostException")) {
				Toast.makeText(this, "通信エラー：インターネットに繋がっているかご確認下さい。", Toast.LENGTH_LONG).show();
			} else if (booklist.mErrorStatus.equals("FileNotFoundException")) {
				Toast.makeText(this, "通信エラー：サーバ上の設定ファイルにアクセスできませんでした。", Toast.LENGTH_LONG).show();
			}
			finish();
		} else {
			// 次の表紙リスト一覧画面へ遷移
			Intent intent = new Intent(this, ImageListActivity.class);
			startActivity(intent);
			// このアクティビティは任務を終えて終了。バックキーで戻らないようにしておく。
			finish();
		}

	} //onLoadFinished


	@Override
	public void onLoaderReset(Loader <BookList> arg0) {
		Log.v("StartActivity BookList - onLoaderReset", "onLoaderReset");
	}

	@Override
	public void onStart() {
		Log.v("StartActivity:onStart", "INFO");
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}
	@Override
	public void onStop() {
		Log.v("StartActivity:onStop", "INFO");
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
}

