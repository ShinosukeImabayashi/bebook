package com.example.ebooktest002;

import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class AsyncHttpRequest extends AsyncTask<Uri.Builder, Void, String> {
    private Activity mainActivity;
    private String url;
    
    public AsyncHttpRequest(String getUrl) {
    	url = getUrl;
    }


    // 非同期処理部分
    @Override
    protected String doInBackground(Uri.Builder... builder) {
        try{
		    XmlPullParser xmlPullParser = Xml.newPullParser();

		    URLConnection connection = new URL(url).openConnection();

		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");

		    int eventType;
		    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
		        if (eventType == XmlPullParser.START_TAG && "title".equals(xmlPullParser.getName())) {
		            Log.d("XmlPullParserSampleUrl", xmlPullParser.nextText());
		        }
		    }

        } catch (Exception  e) {
            Log.e("getImageUrls2", "getImageUrls2");
        	e.printStackTrace();
        }
        
        return null;
 }


    // 非同期処理終了時処理
    @Override
    protected void onPostExecute(String result) {
        // 取得した結果をテキストビューに入れちゃったり
        Log.e("AsyncHttpRequest - onPostExecute", "onPostExecute");
    	
    }
}
