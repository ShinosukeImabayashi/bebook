package com.bebook;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Xml;

public class BookList extends AsyncTaskLoader <BookList> {


private String xmlurl;



public BookList(Context context, String url) {
		super(context);
		xmlurl = url;
	}





// インターネットからデータを非同期通信で取得
public void setBookListData () {

}


// 取得したデータの受け渡し
public  String getBookName (int bookid) {
	return "bookname";
}

public String[] getBookCoverImageUrl () {
	String[] imageUrls = {
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



@Override
public BookList loadInBackground() {
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

	    URLConnection connection = new URL(xmlurl).openConnection();

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

    return this;
}

@Override
protected void onStartLoading() {
    forceLoad();
}
















}
