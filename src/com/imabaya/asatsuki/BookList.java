package com.imabaya.asatsuki;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class BookList extends AsyncTaskLoader <BookList> {

	// 全書籍データを管理
	@SuppressWarnings("rawtypes")
	private TreeMap <String, HashMap> mBookListData = new TreeMap<String, HashMap> ();

	// 著作者情報を管理
	private HashMap <String, String> mAuthorData = new HashMap<String, String> ();

	// 出版社情報を管理
	private HashMap <String, String> mPublisherData = new HashMap<String, String> ();

	// 広告情報を管理
	private HashMap <String, String> mAdvertisingData = new HashMap<String, String> ();

	// 書籍 id リスト
	private ArrayList <String> mBookIdList;

	// 書籍カバータイトルリスト
	private ArrayList <String> mCoverTextList;

	// 書籍データ xml ファイル url
	private String mXmlUrl;


	public BookList(Context context, String url) {
		super(context);
		mXmlUrl = url;
	}


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
	        Iterator it = mBookListData.keySet().iterator();
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
	        		Log.v("getBookCoverImageUrl", o.toString() + "---" + ((Map) mBookListData.get(o)).get("coverimageurl"));
	            	mCoverImageList.add((String) ((Map) mBookListData.get(o)).get("coverimageurl"));	// 表紙画像 url のリストを格納
	            	mBookIdList.add((String) ((Map) mBookListData.get(o)).get("id"));	// bookid のリストを格納
	            }
	        }
	    } catch (Exception  e) {
	        Log.e("getBookCoverImageUrl", "error");
	    	e.printStackTrace();
	    }
		 */


		// printorder sort
		// key 要素で自動ソートする TreeMap で、ソートしたい値を key とした連想配列を作る
		TreeMap <String, String> SortBookListData= new TreeMap<String, String> ();
		try {
			Iterator<String> it = mBookListData.keySet().iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (o != null) {
					// ソートしたい値を key に、オブジェクト本体の元 key を value に入れ直す（key は重複回避のために bookid を付与して一意性を保つ）
					String bookid = (String)mBookListData.get(o).get("id");
					SortBookListData.put(mBookListData.get(o).get(sortKey) + bookid, bookid);
				}
			}
		} catch (Exception  e) {
			Log.e("getBookCoverImageUrl", "error");
			e.printStackTrace();
		}

		// 実際の並び替え
		try {
			Iterator<String> it;
			if (sortOrder.equals("desc")) {
				it = SortBookListData.descendingKeySet().iterator();
			} else {
				it = SortBookListData.keySet().iterator();
			}
			while (it.hasNext()) {
				Object o = it.next();
				if (o != null) {
					Log.v("SortBookListData", o.toString() + "---" + mBookListData.get(SortBookListData.get(o).toString()).get("name")       );
					mCoverImageList.add( (String) mBookListData.get(SortBookListData.get(o).toString()).get("coverimageurl")  );	// 表紙画像 url のリストを格納
					mBookIdList.add( (String) mBookListData.get(SortBookListData.get(o).toString()).get("id")  );	// bookid のリストを格納

					// 書籍の一覧画面に表示する各書籍ごとのタイトルテキストを生成
					mCoverTextList.add(
							(String) mBookListData.get(SortBookListData.get(o).toString()).get("name")
							+ "\n" + (String) mBookListData.get(SortBookListData.get(o).toString()).get("covertext")
							+ "\n [" + (String) mBookListData.get(SortBookListData.get(o).toString()).get("updatedate") + "]"
							+ "    (page : " + (String) mBookListData.get(SortBookListData.get(o).toString()).get("page") + ")"
							+ "\n"
							);

				}
			}
		} catch (Exception  e) {
			Log.e("getBookCoverImageUrl", "error");
			e.printStackTrace();
		}

		return mCoverImageList.toArray(new String[mCoverImageList.size()]);
	}

	/**
	 *  書籍タイトルデータ取得
	 *  @param listnum リストの何番目が選択されたか
	 */
	public String getBookTitleNameText(int listnum) {
		String bookid = mBookIdList.get(listnum);	// bookid に変換
		String bookTitleName = (String) mBookListData.get(bookid).get("name");
		if (bookTitleName == null) {
			bookTitleName = "";
		}
		return bookTitleName;
	}


	/**
	 *  奥付情報（著作者情報・出版社情報）データ取得
	 *  @param listnum リストの何番目が選択されたか
	 */
	public String getBookPublicationText(int listnum) {
		String bookid = mBookIdList.get(listnum);	// bookid に変換


		String bookInfoText =
				"<BR><BR><BR>" +
						"■書籍情報<BR>" +
						"name : " + (String) mBookListData.get(bookid).get("name") + "<BR>" +
						"page : " +  (String) mBookListData.get(bookid).get("page") + "<BR>" +
						"explanation : " +  (String) mBookListData.get(bookid).get("explanation") + "<BR>" +
						"appeal : " +  (String) mBookListData.get(bookid).get("appeal") + "<BR>" +
						"makedate : " +  (String) mBookListData.get(bookid).get("makedate") + "<BR>" +
						"version : " +  (String) mBookListData.get(bookid).get("version") + "<BR>" +
						"updatedate : " +  (String) mBookListData.get(bookid).get("updatedate") + "<BR>";

		String publicationText =
				"<BR>■著作者情報<BR>" +
						"name : " + mAuthorData.get("name") + "<BR>" +
						"url : " + mAuthorData.get("url") + "<BR>" +
						"mail : " + mAuthorData.get("mail") + "<BR>" +
						"twitter : " + mAuthorData.get("twitter") + "<BR>" +
						"skype : " + mAuthorData.get("skype") + "<BR>" +
						"<BR>■出版者情報<BR>" +
						"name : " + mPublisherData.get("name") + "<BR>" +
						"url : " + mPublisherData.get("url") + "<BR>" +
						"mail :" + mPublisherData.get("twitter") + "<BR>" +
						"twitter : " + mPublisherData.get("skype") + "<BR>" +
						"skype : " + mPublisherData.get("mail") + "<BR>";

		return bookInfoText + "\n" + publicationText;
	}



	// 書籍表紙タイトルデータ取得
	public String[] getBookCoverText() {


		/*
		 * 	// 著作者情報を管理
	private HashMap <String, String> mAuthorData = new HashMap<String, String> ();

	// 出版社情報を管理
	private HashMap <String, String> mPublisherData = new HashMap<String, String> ();
		try {
	        Iterator it = mBookListData.keySet().iterator();
	        while (it.hasNext()) {
	            Object o = it.next();
	            if (o != null) {
	        		Log.v("getBookCoverText", o.toString() + "---" + ((Map) mBookListData.get(o)).get("coverimageurl"));

	        		// 書籍の一覧画面に表示する各書籍ごとのタイトルテキストを生成
	        		mCoverTextList.add(
	            			(String) ((Map) mBookListData.get(o)).get("name")
	            			+ " - " + (String) ((Map) mBookListData.get(o)).get("covertext")
	            			+ " [" + (String) ((Map) mBookListData.get(o)).get("updatedate") + "]"
	            			+ " (" + (String) ((Map) mBookListData.get(o)).get("page") + ")"
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

	/**
	 *  書籍が左開きか右開きかの指定取得（任意の１冊分）
	 *  @param listnum リストの何番目が選択されたか
	 */
	public String getBookOpeningtype (int listnum) {

		String bookid = mBookIdList.get(listnum);	// bookid に変換

		// 左開きか右開きかの指定取得
		String opeingType;
		try {
			opeingType = ((String) (mBookListData.get(bookid).get("openingtype"))).toLowerCase();
		} catch (NullPointerException  e) {
			opeingType = "LEFT_OPENING";	// 指定が無かった場合のデフォルトは左開き
		}
		if (opeingType.equals("right"))  {
			opeingType = "RIGHT_OPENING";
		} else {
			opeingType = "LEFT_OPENING";
		}
		//Log.v("getBookImageUrl", "opeingType=" + opeingType);
		return opeingType;
	}

	/**
	 *  書籍画像データ取得（任意の１冊分）
	 *  @param listnum リストの何番目が選択されたか
	 */
	public String[] getBookImageUrl (int listnum) {

		ArrayList <String> bookImageUrlList  = new ArrayList <String>();

		String bookid = mBookIdList.get(listnum);	// bookid に変換

		// 左開きか右開きかの指定取得
		String opeingType = getBookOpeningtype(listnum);

		// 書籍 URLリストの取得
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> urlList = (ArrayList<String>) mBookListData.get(bookid).get("contentsUrl"); // 書籍の内容画像データの url リストを取得
			bookImageUrlList  = new ArrayList <String>(urlList) ;	// 内容画像データは追記修正するので、コピーを作ってそちらを利用する
		} catch (Exception  e) {
			Log.e("getBookCoverImageUrl", "error");
			e.printStackTrace();
		}

		// RIGHT_OPENING モードの場合はそのまま、LEFT_OPENING モードの場合はリストを逆順に並び替える
		if (opeingType.equals("RIGHT_OPENING"))  {
			Collections.reverse(bookImageUrlList);
		}

		return bookImageUrlList.toArray(new String[bookImageUrlList.size()]);
	}


	/**
	 *  書籍画像説明データ取得（任意の１冊分）
	 *  @param listnum リストの何番目が選択されたか
	 */
	public String[] getBookImageExplanationText (int listnum) {

		ArrayList <String> bookImageTextList  = new ArrayList <String>();

		String bookid = mBookIdList.get(listnum);	// bookid に変換

		// 左開きか右開きかの指定取得
		String opeingType = getBookOpeningtype(listnum);

		// 書籍 URLリストの取得
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> urlList = (ArrayList<String>) mBookListData.get(bookid).get("contentsText"); // 書籍の内容画像データの説明文リストを取得
			bookImageTextList  = new ArrayList <String>(urlList) ;	// コピーを作ってそちらを利用する
		} catch (Exception  e) {
			Log.e("getBookCoverImageUrl", "error");
			e.printStackTrace();
		}

		// RIGHT_OPENING モードの場合はそのまま、LEFT_OPENING モードの場合はリストを逆順に並び替える
		if (opeingType.equals("RIGHT_OPENING"))  {
			Collections.reverse(bookImageTextList);
		}

		return bookImageTextList.toArray(new String[bookImageTextList.size()]);
	}



	@Override
	public BookList loadInBackground() {

		// １書籍データを管理
		Map <String, Object> bookData = new HashMap<String, Object> ();
		// １書籍内の画像データを管理
		List <String> contentsUrl = new ArrayList <String>();
		List <String> contentsText = new ArrayList <String>();

		// URLConnection による xml の読み込み
		try{
			XmlPullParser xmlPullParser = Xml.newPullParser();

			URLConnection connection = new URL(mXmlUrl).openConnection();	//指定した url から設定 xml ファイルを取得

			xmlPullParser.setInput(connection.getInputStream(), "UTF-8");

			int eventType;
			String nowField = "";
			int bookPageImageCount = 0;
			int bookPageTextCount = 0;
			while ( (eventType = xmlPullParser.getEventType()) != XmlPullParser.END_DOCUMENT) {

				///Log.v("21 XmlPullParserSampleUrl", "-" + eventType + "-" + xmlPullParser.getName() + "-"  +  xmlPullParser.getText() + "-");
				// 読み込んだ xml データに対して、開きタグ・閉じタグ・内容のそれぞれに応じてデータ再まとめ処理を実施
				if (eventType == XmlPullParser.START_DOCUMENT) {	// 一番最初のみ
					Log.v("0 XmlPullParserSampleUrl", "start");
				} else if (eventType == XmlPullParser.START_TAG) {
					String tag = xmlPullParser.getName();
					if (tag != null) {
						xmlPullParser.next();	// タグ内の本文テキスト取得
						eventType = xmlPullParser.getEventType();
						//Log.v("21 XmlPullParserSampleUrl", "-" + eventType + "-" + tag + "-"  +  xmlPullParser.getText() + "-");

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
								bookPageImageCount = 0;
								bookPageTextCount = 0;
								if (bookData.get("id") != null) {
									mBookListData.put((String) bookData.get("id"),  (HashMap<String, Object>) bookData);
								}
								// 次の書籍データに移る準備
								bookData = new HashMap<String, Object>();
							}	else if (tag.equals("contents")) {	// book 内の contents 処理のスタート
								contentsUrl = new ArrayList <String>();
								contentsText = new ArrayList <String>();
							}	else if (tag.equals("iurl")) { // 書籍ページイメージ URL
								if ( bookPageImageCount != bookPageTextCount ) {	// <iurl> タグに対応する <itext> が無い場合、ダミーを入れておく。
									contentsText.add("");
									bookPageTextCount++;
								}
								if (xmlPullParser.getText() != null) { // 書籍ページ説明文
									contentsUrl.add(xmlPullParser.getText().trim());
								} else {
									contentsUrl.add("");
								}
								bookPageImageCount++;
							}	else if (tag.equals("itext")) {
								if (xmlPullParser.getText() != null) {
									contentsText.add(xmlPullParser.getText().trim());
								} else {
									contentsText.add("");
								}
								bookPageTextCount++;
							} else {	// 特殊処理必要なタグ以外のデータはここで拾っておく
								if (xmlPullParser.getText() != null) {
									bookData.put(tag,  xmlPullParser.getText().trim());
								} else {
									bookData.put(tag,  "");
								}
							}
						}

						// 著作者フィールドの処理
						if (nowField.equals("author")) {
							if (tag.equals("author")) {
								;
							} else {	// 特殊処理必要なタグ以外のデータはここで拾っておく
								if (xmlPullParser.getText() != null) {
									mAuthorData.put(tag,  xmlPullParser.getText().trim());
								} else {
									mAuthorData.put(tag,  "");
								}
							}
						}

						// 出版社情報フィールドの処理
						if (nowField.equals("publisher")) {
							if (tag.equals("publisher")) {
								;
							} else {	// 特殊処理必要なタグ以外のデータはここで拾っておく
								if (xmlPullParser.getText() != null) {
									mPublisherData.put(tag,  xmlPullParser.getText().trim());
								} else {
									mPublisherData.put(tag,  "");
								}
							}
						}

						// 広告フィールドの処理
						if (nowField.equals("advertising")) {
							if (tag.equals("advertising")) {
								;
							} else {	// 特殊処理必要なタグ以外のデータはここで拾っておく
								if (xmlPullParser.getText() != null) {
									mAdvertisingData.put(tag,  xmlPullParser.getText().trim());
								} else {
									mAdvertisingData.put(tag,  "");
								}
							}
						}

						//Log.v("22 XmlPullParserSampleUrl", "" + bookData.toString());

					}
				} else if(eventType == XmlPullParser.END_TAG) {	// 閉じタグ確認時の処理
					//Log.v("3 XmlPullParserSampleUrl", eventType + " " + xmlPullParser.getName() + " " + xmlPullParser.getText());
					String tag = xmlPullParser.getName();
					if (tag.equals("book")) {	// 書籍情報フィールド
						nowField = "";
					}	else if (tag.equals("publisher")) {	// 出版社フィールド
						nowField = "";
					}	else if (tag.equals("author")) {	// 著作者フィールド
						nowField = "";
					}	else if (tag.equals("advertising")) {	// 広告フィールド
						nowField = "";
					}
				} else if(eventType == XmlPullParser.TEXT) {
					//Log.v("4 XmlPullParserSampleUrl", eventType + " " + xmlPullParser.getName() + " " + xmlPullParser.getText());
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
				mBookListData.put((String)bookData.get("id"),  (HashMap<String, Object>) bookData);
			}
		} catch (Exception  e) {
			e.printStackTrace();
		}


		// 書籍データ取得（プログラム参考用）
		/*
			try {
		        Iterator it = mBookListData.keySet().iterator();
		        while (it.hasNext()) {
		            Object o = it.next();
		            if (o != null) {
		            		Log.v("bookData", o.toString() + "---" + ((Map) mBookListData.get(o)).get("id"));
		            		Log.v("bookData", o.toString() + "---" + ((Map) mBookListData.get(o)).get("name"));
		            		ArrayList list = (ArrayList)  ((Map) mBookListData.get(o)).get("contentsText");
		            		for (int i = 0, n = list.size(); i < n; i++) {
		            		    String lo = list.get(i).toString();
			            		Log.v("bookData", o.toString() + "---" + lo.toString());
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
