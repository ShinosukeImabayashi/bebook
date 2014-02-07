package com.bebook;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.xmlpull.v1.XmlPullParser;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;



public class AsyncHttpRequest extends AsyncTask<Uri.Builder, Void, String> {

    private String url;

    public AsyncHttpRequest(String getUrl) {
    	url = getUrl;
    }


    // 非同期処理部分
    @Override
    protected String doInBackground(Uri.Builder... builder) {



    	// 全書籍データを管理
    	Map bookListData= new HashMap ();
    	// １書籍データを管理
    	Map bookData = new HashMap ();
    	// １書籍内の画像データを管理
    	List <String> contentsUrl = new ArrayList <String>();
    	List <String> contentsText = new ArrayList <String>();


    	// URLConnection による xml の読み込み
    	try{
		    XmlPullParser xmlPullParser = Xml.newPullParser();

		    URLConnection connection = new URL(url).openConnection();

		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");

		    int eventType;
		    while ( (eventType = xmlPullParser.getEventType()) != XmlPullParser.END_DOCUMENT) {


		            if(eventType == XmlPullParser.START_DOCUMENT) {
			            Log.d("0 XmlPullParserSampleUrl", "start");
		            } else if(eventType == XmlPullParser.START_TAG) {
		            	String tag = xmlPullParser.getName();
			            if (tag != null) {
			            	xmlPullParser.next();
			            	eventType = xmlPullParser.getEventType();
			            	//Log.d("21 XmlPullParserSampleUrl", "-" + eventType + "-" + tag + "-"  +  xmlPullParser.getText() + "-");


			            	if (tag.equals("book")) {
			            		// 書籍データを bookList に挿入
				            	bookData.put("contentsUrl", contentsUrl);
				            	bookData.put("contentsText", contentsText);
			            		bookListData.put(bookData.get("id"),  bookData);

			            		// 次の書籍データに移る準備
			            		bookData = new HashMap();

			            	} else if (tag.equals("id")) {
			            		bookData.put(tag,  xmlPullParser.getText());
			            	} else if (tag.equals("name")) {
			            		bookData.put(tag,  xmlPullParser.getText());
			            	} else if (tag.equals("page")) {
			            		bookData.put(tag,  xmlPullParser.getText());
			            	}	else if (tag.equals("contents")) {
				            	contentsUrl = new ArrayList <String>();
				            	contentsText = new ArrayList <String>();
			            	}	else if (tag.equals("iurl")) {
			            		contentsUrl.add(xmlPullParser.getText());
			            	}	else if (tag.equals("text")) {
			            		contentsText.add(xmlPullParser.getText());
			            	}

			            	//Log.d("22 XmlPullParserSampleUrl", "" + bookData.toString());

			            }
		            } else if(eventType == XmlPullParser.END_TAG) {
			            //Log.d("3 XmlPullParserSampleUrl", eventType + " " + xmlPullParser.getName() + " " + xmlPullParser.getText());
		            } else if(eventType == XmlPullParser.TEXT) {
			            //Log.d("4 XmlPullParserSampleUrl", eventType + " " + xmlPullParser.getName() + " " + xmlPullParser.getText());
		            }

		            xmlPullParser.next();

		    }

        } catch (Exception  e) {
            Log.e("getImageUrls2", "getImageUrls2");
        	e.printStackTrace();
        }

    	// 最後の書籍データを bookList に挿入
    	bookData.put("contentsUrl", contentsUrl);
    	bookData.put("contentsText", contentsText);
		bookListData.put(bookData.get("id"),  bookData);


		// 書籍データ取得
		try {
	        Iterator it = bookListData.keySet().iterator();
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
	            		Log.d("bookData", o.toString() + "---" + ((Map) bookListData.get(o)).get("id"));
	            		Log.d("bookData", o.toString() + "---" + ((Map) bookListData.get(o)).get("name"));
	            		ArrayList list = (ArrayList)  ((Map) bookListData.get(o)).get("contentsText");
	            		for (int i = 0, n = list.size(); i < n; i++) {
	            		    String lo = list.get(i).toString();
		            		Log.d("bookData", o.toString() + "---" + lo.toString());
	            		}
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

        Log.d("AsyncHttpRequest - onPostExecute", "onPostExecute");

    }
}
