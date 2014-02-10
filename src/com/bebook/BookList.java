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

   	// 全書籍データを管理
	private Map bookListData= new HashMap ();

	// 書籍 id リスト
	private ArrayList <String> mBookIdList;

	// 書籍データ xml ファイル url
	private String xmlurl;


	public BookList(Context context, String url) {
			super(context);
			xmlurl = url;
		}



	// 書籍表紙画像データ取得
	public String[] getBookCoverImageUrl () {

		ArrayList <String> mCoverImageList = new ArrayList <String>();
		mBookIdList = new ArrayList <String>();

		try {
	        Iterator it = bookListData.keySet().iterator();
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
	        		Log.d("getBookCoverImageUrl", o.toString() + "---" + ((Map) bookListData.get(o)).get("coverimageurl"));
	            	mCoverImageList.add((String) ((Map) bookListData.get(o)).get("coverimageurl"));	// 表紙画像 url のリストを格納
	            	mBookIdList.add((String) ((Map) bookListData.get(o)).get("id"));	// bookid のリストを格納
	            }
	        }
	    } catch (Exception  e) {
	        Log.e("getBookCoverImageUrl", "error");
	    	e.printStackTrace();
	    }

		return mCoverImageList.toArray(new String[mCoverImageList.size()]);
	}



	// 書籍表紙タイトルデータ取得
	public String[] getBookCoverText() {
		ArrayList <String> mCoverTextList = new ArrayList <String>();

		try {
	        Iterator it = bookListData.keySet().iterator();
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
	        		Log.d("getBookCoverText", o.toString() + "---" + ((Map) bookListData.get(o)).get("coverimageurl"));

	        		// 書籍の一覧画面に表示する各書籍ごとのタイトルテキストを生成
	        		mCoverTextList.add(
	            			(String) ((Map) bookListData.get(o)).get("name")
	            			+ " - " + (String) ((Map) bookListData.get(o)).get("covertext")
	            			+ " [" + (String) ((Map) bookListData.get(o)).get("updatedate") + "]"
	            			+ " (" + (String) ((Map) bookListData.get(o)).get("page") + ")"
	            			);
	            }
	        }
	    } catch (Exception  e) {
	        Log.e("getBookCoverText", "error");
	    	e.printStackTrace();
	    }

		return mCoverTextList.toArray(new String[mCoverTextList.size()]);
	}



	// 書籍データ取得
	public String[] getBookImageUrl (int listnum) {

		ArrayList <String> getBookImageUrl  = new ArrayList <String>();
		ArrayList <String> getBookImageUrl2  = new ArrayList <String>();

		String bookid = mBookIdList.get(listnum);
		try {
			getBookImageUrl = (ArrayList<String>) ((Map) bookListData.get(bookid)).get("contentsUrl");	// 書籍の内容画像データの url リストを取得
			getBookImageUrl2  = new ArrayList <String>(getBookImageUrl) ;	// 内容画像データは追記修正するので、コピーを作ってそちらを利用する
	    } catch (Exception  e) {
	        Log.e("getBookCoverImageUrl", "error");
	    	e.printStackTrace();
	    }

		return getBookImageUrl2.toArray(new String[getBookImageUrl2.size()]);
	}



	// インターネットからデータを非同期通信で取得
	@Override
	public BookList loadInBackground() {

		// １書籍データを管理
		Map bookData = new HashMap ();
		// １書籍内の画像データを管理
		List <String> contentsUrl = new ArrayList <String>();
		List <String> contentsText = new ArrayList <String>();


		// URLConnection による xml の読み込み
		try{
		    XmlPullParser xmlPullParser = Xml.newPullParser();

		    URLConnection connection = new URL(xmlurl).openConnection();	//指定した url から設定 xml ファイルを取得

		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");

		    int eventType;
		    while ( (eventType = xmlPullParser.getEventType()) != XmlPullParser.END_DOCUMENT) {

		    		// 読み込んだ xml データに対して、開きタグ・閉じタグ・内容のそれぞれに応じてデータ再まとめ処理を実施
		    		if (eventType == XmlPullParser.START_DOCUMENT) {	// 一番最初のみ
		    			Log.d("0 XmlPullParserSampleUrl", "start");
		    		} else if (eventType == XmlPullParser.START_TAG) {
		            	String tag = xmlPullParser.getName();
			            if (tag != null) {
			            	xmlPullParser.next();
			            	eventType = xmlPullParser.getEventType();
			            	//Log.d("21 XmlPullParserSampleUrl", "-" + eventType + "-" + tag + "-"  +  xmlPullParser.getText() + "-");


			            	if (tag.equals("book")) {	// book 単位での大処理
			            		// 書籍データを bookList に挿入
				            	bookData.put("contentsUrl", contentsUrl);
				            	bookData.put("contentsText", contentsText);
			            		bookListData.put(bookData.get("id"),  bookData);

			            		// 次の書籍データに移る準備
			            		bookData = new HashMap();

			            	}	else if (tag.equals("contents")) {	// book 内の contents 処理のスタート
				            	contentsUrl = new ArrayList <String>();
				            	contentsText = new ArrayList <String>();
			            	}	else if (tag.equals("iurl")) {
			            		contentsUrl.add(xmlPullParser.getText());
			            	}	else if (tag.equals("itext")) {
			            		contentsText.add(xmlPullParser.getText());
			            	} else {
			            		bookData.put(tag,  xmlPullParser.getText());	// 特殊処理必要なタグ以外のデータはここで拾っておく
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


		// 書籍データ取得（プログラム参考用）
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
