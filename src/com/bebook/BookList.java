package com.bebook;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Xml;

public class BookList extends AsyncTaskLoader <BookList> {

   	// 全書籍データを管理
	private TreeMap bookListData= new TreeMap ();

	// 著作者情報を管理
	private HashMap authorData = new HashMap ();

	// 出版社情報を管理
	private HashMap publisherData = new HashMap ();

	// 広告情報を管理
	private HashMap advertisingData = new HashMap ();

	// 書籍 id リスト
	private ArrayList <String> mBookIdList;

	// 書籍カバータイトルリスト
	private ArrayList <String> mCoverTextList;


	// 書籍データ xml ファイル url
	private String xmlurl;


	public BookList(Context context, String url) {
			super(context);
			xmlurl = url;
		}



	//
	/**
	 *  書籍表紙画像データ取得
	 *  @param sortKey "id" / "printorder" / "name" / "makedate" / "updatedate"
	 *  @param sortOrder "asc" / "desc"
	 */
	public String[] getBookCoverImageUrl (String sortKey, String sortOrder) {

		ArrayList <String> mCoverImageList = new ArrayList <String>();
		mBookIdList = new ArrayList <String>();
		mCoverTextList = new ArrayList <String>();
/*
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
*/


		// printorder sort
		// key 要素で自動ソートする TreeMap で、ソートしたい値を key とした連想配列を作る
		TreeMap SortBookListData= new TreeMap ();
		try {
	        Iterator it = bookListData.keySet().iterator();
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
	            	 // ソートしたい値を key に、オブジェクト本体の元 key を value に入れ直す（key は重複回避のために bookid を付与して一意性を保つ）
	            	String bookid = (String)((Map) bookListData.get(o)).get("id");
	            	SortBookListData.put(((Map) bookListData.get(o)).get(sortKey) + bookid, bookid);
	            }
	        }
	    } catch (Exception  e) {
	        Log.e("getBookCoverImageUrl", "error");
	    	e.printStackTrace();
	    }

		 //
		try {
			Iterator it;
			if (sortOrder.equals("desc")) {
		        it = SortBookListData.descendingKeySet().iterator();
			} else {
		        it = SortBookListData.keySet().iterator();
			}
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
            		Log.d("SortBookListData", o.toString() + "---" + ((Map)bookListData.get(SortBookListData.get(o).toString())).get("name")       );
	            	mCoverImageList.add( (String) ((Map)bookListData.get(SortBookListData.get(o).toString())).get("coverimageurl")  );	// 表紙画像 url のリストを格納
	            	mBookIdList.add( (String) ((Map)bookListData.get(SortBookListData.get(o).toString())).get("id")  );	// bookid のリストを格納

	        		// 書籍の一覧画面に表示する各書籍ごとのタイトルテキストを生成
	        		mCoverTextList.add(
	        				(String) ((Map)bookListData.get(SortBookListData.get(o).toString())).get("name")
	            			+ " - " + (String) ((Map)bookListData.get(SortBookListData.get(o).toString())).get("covertext")
	            			+ " [" + (String) ((Map)bookListData.get(SortBookListData.get(o).toString())).get("updatedate") + "]"
	            			+ " page : (" + (String) ((Map)bookListData.get(SortBookListData.get(o).toString())).get("page") + ")"
	            			);

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

/*
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
*/
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
        	String nowField = "";
		    while ( (eventType = xmlPullParser.getEventType()) != XmlPullParser.END_DOCUMENT) {

		    	Log.d("21 XmlPullParserSampleUrl", "-" + eventType + "-" + xmlPullParser.getName() + "-"  +  xmlPullParser.getText() + "-");
		    		// 読み込んだ xml データに対して、開きタグ・閉じタグ・内容のそれぞれに応じてデータ再まとめ処理を実施
		    		if (eventType == XmlPullParser.START_DOCUMENT) {	// 一番最初のみ
		    			Log.d("0 XmlPullParserSampleUrl", "start");
		    		} else if (eventType == XmlPullParser.START_TAG) {
		            	String tag = xmlPullParser.getName();
			            if (tag != null) {
			            	xmlPullParser.next();	// タグ内の本文テキスト取得
			            	eventType = xmlPullParser.getEventType();
			            	//Log.d("21 XmlPullParserSampleUrl", "-" + eventType + "-" + tag + "-"  +  xmlPullParser.getText() + "-");

			            	// xml の大カテゴリでの場合分け
			            	if (tag.equals("book")) {	// 書籍情報フィールド
			            		nowField = "book";
			            	}	else if (tag.equals("publisher")) {	// 出版社フィールド
			            		nowField = "publisher";
			            	}	else if (tag.equals("author")) {	// 著作者フィールド
			            		nowField = "author";
			            	}	else if (tag.equals("advertising")) {	// 広告フィールド
			            		nowField = "advertising";
			            	}

			            	// 書籍情報フィールドの処理（複数アイテム存在することを想定）
			            	if (nowField.equals("book")) {
				            	if (tag.equals("book")) {	// book 単位での大処理
				            		// 書籍データを bookList に挿入
					            	bookData.put("contentsUrl", contentsUrl);
					            	bookData.put("contentsText", contentsText);
					            	if (bookData.get("id") != null) {
					            		bookListData.put(bookData.get("id"),  bookData);
					            	}
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
			            	}

			            	// 著作者フィールドの処理
			            	if (nowField.equals("author")) {
				            	if (tag.equals("author")) {
				            		;
				            	} else {
				            		authorData.put(tag,  xmlPullParser.getText());	// 特殊処理必要なタグ以外のデータはここで拾っておく
				            	}
			            	}

			            	// 出版社情報フィールドの処理
			            	if (nowField.equals("publisher")) {
				            	if (tag.equals("publisher")) {
				            		;
				            	} else {
				            		publisherData.put(tag,  xmlPullParser.getText());	// 特殊処理必要なタグ以外のデータはここで拾っておく
				            	}
			            	}

			            	// 広告フィールドの処理
			            	if (nowField.equals("advertising")) {
				            	if (tag.equals("advertising")) {
				            		;
				            	} else {
				            		advertisingData.put(tag,  xmlPullParser.getText());	// 特殊処理必要なタグ以外のデータはここで拾っておく
				            	}
			            	}

			            	//Log.d("22 XmlPullParserSampleUrl", "" + bookData.toString());

			            }
		            } else if(eventType == XmlPullParser.END_TAG) {
			            //Log.d("3 XmlPullParserSampleUrl", eventType + " " + xmlPullParser.getName() + " " + xmlPullParser.getText());
		            	String tag = xmlPullParser.getName();
		            	if (tag.equals("book")) {	// 書籍情報フィールド
		            		nowField = "";
		            	}	else if (tag.equals("publisher")) {	// 出版社フィールド
		            		nowField = "";
		            	}	else if (tag.equals("author")) {	// 著作者フィールド
		            		nowField = "";
		            	}	else if (tag.equals("advertising")) {	// 広告フィールド
		            		nowField = "author";
		            	}
		            } else if(eventType == XmlPullParser.TEXT) {
			            //Log.d("4 XmlPullParserSampleUrl", eventType + " " + xmlPullParser.getName() + " " + xmlPullParser.getText());
		            }

		            xmlPullParser.next();

		    }

	    } catch (Exception  e) {
	        Log.e("getImageUrls2", "getImageUrls2");
	    	e.printStackTrace();
	    }

		try {
			// 最後の書籍データを bookList に挿入
			bookData.put("contentsUrl", contentsUrl);
			bookData.put("contentsText", contentsText);
        	if (bookData.get("id") != null) {
        		bookListData.put(bookData.get("id"),  bookData);
        	}
	    } catch (Exception  e) {
	    	e.printStackTrace();
	    }

		// 書籍データ取得（プログラム参考用）
/*
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
*/
	    return this;
	}

	@Override
	protected void onStartLoading() {
	    forceLoad();
	}







}
