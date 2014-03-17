package com.imabaya.asatsuki;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.imabaya.asatsuki.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey="",
	formUri="http://www.imabaya.com/asatsuki/crashreport/receivereport.php",
	mode = ReportingInteractionMode.DIALOG,
	resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
	resDialogText = R.string.crash_dialog_text,
	resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
	resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
	resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
	resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class UILApplication extends Application {

	// 書籍情報の統括管理オブジェクト
	private BookList  booklist;

	// 書籍データ管理オブジェクトのセット
	public void setBooklist(BookList sbooklist) {
		booklist = sbooklist;
	}

	// 書籍データ管理オブジェクトの取得
	public BookList getBooklist() {
		return booklist;
	}

	public static final boolean DEVELOPER_MODE = false;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		if (DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		super.onCreate();

		ACRA.init(this);	// エラーレポート

		// UnivasalImageLoader の初期化処理
		initImageLoader(getApplicationContext());
	}



	// Univasal Image Loader の初期設定
	public static void initImageLoader(Context context) {
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);	// 最大で搭載メモリの 1/8 まで使用する
		Log.v("memoryCacheSize", " " + memoryCacheSize);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		//.threadPoolSize(1)
		.threadPriority(Thread.MAX_PRIORITY)
		//.memoryCacheSize(memoryCacheSize)
		//.denyCacheImageMultipleSizesInMemory()
		//.discCacheFileCount(100)
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		///.writeDebugLogs() // Remove for release app
		.build();

		ImageLoader.getInstance().init(config);
	}
}