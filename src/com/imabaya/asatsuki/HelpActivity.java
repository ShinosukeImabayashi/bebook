package com.imabaya.asatsuki;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.*;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;


public class HelpActivity extends Activity {
	  private AdView adView;	// admob バナー広告配信
	  private InterstitialAd interstitial;	//admob インタースティシャル広告配信


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("Help", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		Button  helpBackButton1 = (Button)  this.findViewById(R.id.help_back1);
		helpBackButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				///throw new IllegalArgumentException("がっ");
			    displayInterstitial();
	        	finish();
			}
		});

		Button  helpBackButton2 = (Button)  this.findViewById(R.id.help_back2);
		helpBackButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    displayInterstitial();
	        	finish();
			}
		});

		

	    // 広告表示の追加スタイルの設定
	    Bundle bundle = new Bundle();
	    bundle.putString("color_bg", "AAAAFF");
	    bundle.putString("color_bg_top", "FFFFFF");
	    bundle.putString("color_border", "FFFFFF");
	    bundle.putString("color_link", "000080");
	    bundle.putString("color_text", "808080");
	    bundle.putString("color_url", "008000");
	    AdMobExtras extras = new AdMobExtras(bundle);
		

	    // admob スマートバナー広告配信オブジェクトの作成
	    adView = new AdView(this);
	    adView.setAdUnitId("ca-app-pub-8815528739036624/4719045392");
	    adView.setAdSize(AdSize.SMART_BANNER);
	    LinearLayout layout = (LinearLayout)findViewById(R.id.help_parent);
	    layout.addView(adView, 0);

	    // スマートバナー広告リクエスト
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
	    .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
	    .build();
	    
	    adView.loadAd(adRequest);


	    // admob インタースティシャル広告配信オブジェクトの作成
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-8815528739036624/6195778599");

	    
	    // インタースティシャル広告リクエスト
	    AdRequest adRequest2 = new AdRequest.Builder()
	    .addNetworkExtras(extras) // 追加スタイルの設定
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // エミュレータ
	    .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // Galaxy Nexus テスト用携帯電話
	    .build();
	    interstitial.loadAd(adRequest2);

	    // ボタン押下イベントなどのタイミングでインタースティシャル広告の刺し込み表示を実行
	    displayInterstitial();
	} //onCreate


	  // インタースティシャルを表示する準備ができたら、displayInterstitial() を呼び出す。
	  public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }

}
