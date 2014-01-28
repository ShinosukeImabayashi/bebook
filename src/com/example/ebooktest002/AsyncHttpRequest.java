package com.example.ebooktest002;

import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
 
		// dom4j による xml の読み込み
		String url2 = "http://www.imabaya.com/ebooktest/ebookconst.xml";
		SAXReader reader = new SAXReader();
		
		Document document = null;
		try {
			document = reader.read(url2);
			Element root = document.getRootElement();
			for(Object element : root.elements()) {
				Log.d("xml reading ", ((Element)element).getName());
				Log.d("xml reading ", ((Element)element).getTextTrim());
			}
		} catch (DocumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    	
    	
    	
    	
    	// URLConnection による xml の読み込み
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
