package com.example.ebooktest002;


import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;

import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class EbookConst {

	public EbookConst() {
		Log.i("EbookConst - EbookConst ", "INFO");
	}

	public boolean getBookXmlData() {
		Log.i("EbookConst - getBookXmlData ", "INFO");
		return true;
	}

	public  boolean getImageUrls2() {


		Uri.Builder builder = new Uri.Builder();
		AsyncHttpRequest task = new AsyncHttpRequest("http://techbooster.org/?feed=rss2");
		task.execute(builder);



		return true;
	}




	public String[] getImageUrls () {
		Log.i("EbookConst - getImageUrls ", "INFO");
		String[] imageUrls = {
				"http://www.imabaya.com/testimage/niji/1380943878613.gif",
				"http://www.imabaya.com/testimage/niji/tumblr_mzamkp9o601qzlc8oo1_500.jpg",
				"http://www.imabaya.com/testimage/niji/1386118585036.jpg",


				//"http://www.imabaya.com/testimage/water/SDIM9409.jpg",
				//"http://www.imabaya.com/testimage/water/IMGP9381.jpg",

				"http://www.imabaya.com/testimage/noimage.jpg",
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
		return imageUrls;
	}



}
