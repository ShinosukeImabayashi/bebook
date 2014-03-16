package com.imabaya.asatsuki;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class BookList extends AsyncTaskLoader <BookList> {

	// 状態異常データステータス
	public String mErrorStatus;			// "FileNotFoundException" / "UnknownHostException"

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
		mErrorStatus = null;
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
							" <b>"  + (String) mBookListData.get(SortBookListData.get(o).toString()).get("name") + "</b>"
							+ " <SMALL>  (" + (String) mBookListData.get(SortBookListData.get(o).toString()).get("page") + " page)</SMALL>"
							+ "<BR>"
							+ "<BR>" + (String) mBookListData.get(SortBookListData.get(o).toString()).get("covertext")
							+ "<BR><SMALL> [" + (String) mBookListData.get(SortBookListData.get(o).toString()).get("updatedate") + "]</SMALL>"
							+ "<BR>"
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
						"▼書籍情報<BR>" +
						"書籍名 : " + (String) mBookListData.get(bookid).get("name") + " " +
						"( " +  (String) mBookListData.get(bookid).get("page") + ") ページ<BR>" +
						"<BR>" +  (String) mBookListData.get(bookid).get("explanation") + "<BR>" +
						" " +  (String) mBookListData.get(bookid).get("appeal") + "<BR>" +
						"<BR>初版 : " +  (String) mBookListData.get(bookid).get("makedate") + "<BR>" +
						"最新版 : " +  (String) mBookListData.get(bookid).get("updatedate") + "<BR>" +
						"version : " +  (String) mBookListData.get(bookid).get("version") + "<BR>";


		String publicationText =
				"<BR>▼著作者情報<BR>" +
						"名前 : " + mAuthorData.get("name") + "<BR>" +
						"サイトURL : " + mAuthorData.get("url") + "<BR>" +
						"メールアドレス : " + mAuthorData.get("mail") + "<BR>" +
						"twitter : " + mAuthorData.get("twitter") + "<BR>" +
						"skype : " + mAuthorData.get("skype") + "<BR>" +
						"<BR>▼発行者情報<BR>" +
						"名前 : " + mPublisherData.get("name") + "<BR>" +
						"サイトURL : " + mPublisherData.get("url") + "<BR>" +
						"メールアドレス :" + mPublisherData.get("twitter") + "<BR>" +
						"twitter : " + mPublisherData.get("skype") + "<BR>" +
						"skype : " + mPublisherData.get("mail") + "<BR>";

		return bookInfoText + "\n" + publicationText + "<BR><BR><BR><BR><BR><BR><BR>" ;
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
	 *  @return "RIGHT_OPENING" or  "LEFT_OPENING"
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
	 *  @return １ページ目からの書籍画像 URL 群
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
	 *  @return １ページ目からの書籍説明テキスト群
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

	/**
	 *  広告表示／非表示設定情報取得
	 *  @param listnum リストの何番目が選択されたか
	 *  @param printfield 広告表示場所
	 *     (1:読書前広告、2:読書後広告、3:書籍表紙、4:書籍奥付、5:書籍本文)
	 *  @return 広告を出す設定であれば true, 出さない設定または指定無しの場合は false
	 */
	public boolean getIsBookAdvertising(int listnum, int printfield) {
		String bookid = mBookIdList.get(listnum);	// bookid に変換
		if (printfield < 1 || printfield > 5) {
			return false;
		}
		String sPrintfield = "ad" + String.format("%03d", printfield);	// 文字列 ad001～ad005 を作る
		String IsBookAdvertising = "";
		try {
			IsBookAdvertising = ((String) (mBookListData.get(bookid).get(sPrintfield))).toLowerCase();
		} catch (NullPointerException  e) {

		}

		if (IsBookAdvertising.equals("true")) {
			return true;
		} else {
			return false;
		}
	}



	/**
	 *  広告ユニットID取得 （AdMob）
	 *  @param adtype 広告ユニットの種類指定（"bunner", "interstitial"）
	 *  @return 広告ユニットID（例外：指定がおかしい場合や、書籍設定 xml に指定が無かった場合は、"" を返す）
	 */
	public String getAdmobAdvertisingUnitId(String adtype) {
		if (adtype.equals("bunner")) {
			String admobAdvertisingUnitId = (String) mAdvertisingData.get("admob-bunner-unit-id");
			return admobAdvertisingUnitId;
		} else  if (adtype.equals("interstitial")) {
				String admobAdvertisingUnitId = (String) mAdvertisingData.get("admob-interstitial-unit-id");
				return admobAdvertisingUnitId;
			} else {
			return "";
		}

	}



	/**
	 *  書籍設定 xml ファイルをインターネット上から取得
	 *  データはメンバ変数に入れ込む
	 */
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

			try {
				xmlPullParser.setInput(connection.getInputStream(), "UTF-8");		// 取得通信実施
			} catch (UnknownHostException e) {	// 通信エラー
				mErrorStatus = "UnknownHostException";
				e.printStackTrace();
			} catch (FileNotFoundException e) {	// xml ファイルが存在しないエラー
				mErrorStatus = "FileNotFoundException";
				e.printStackTrace();
			}

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
							}	else if (tag.equals("printorder")) {
								if (xmlPullParser.getText() != null) {
									int printorder = 9999;
									try {
										printorder = Integer.valueOf(xmlPullParser.getText());
									} catch (Exception e) {
										printorder = 9999;
									}
									bookData.put(tag,  String.format("%04d", printorder));	// 1 → 0001
								} else {
									bookData.put(tag,  "9999");
								}
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
