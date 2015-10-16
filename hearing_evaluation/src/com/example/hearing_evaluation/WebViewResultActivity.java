package com.example.hearing_evaluation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebViewResultActivity extends Activity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view_result);
		
		//WebView myWebView = (WebView) findViewById(R.id.webViewResult);
		
		
		//myWebView.loadUrl("http://www.oticon.dk/");
//		WebView browser = (WebView) findViewById(R.id.webViewResult); //if you gave the id as browser
//		browser.getSettings().setJavaScriptEnabled(true); //Yes you have to do it
//		browser.loadData("file:///android_asset/audiogram/javascript/audiogramcharter.js", "text/html", "utf-8"); //If you put the HTML file in asset folder of android
		
		 WebView webView = (WebView) findViewById(R.id.webViewResult);
	     webView.getSettings().setJavaScriptEnabled(true);
//	     webView.setWebChromeClient(new WebChromeClient());
//	     webView.getSettings().setDomStorageEnabled(true);
	     //webView.loadDataWithBaseURL("file:///android_asset/resultsOticonChart.html","", "text/html", "UTF-8",null);
	     webView.loadUrl("file:///android_asset/resultsOticonChart.html");
	     //webView.loadData("file:///android_asset/audiogram/resultOticonChart.html", "text/html", "UTF-8");
		
		//String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
		//myWebView.loadData(customHtml, "text/html", "UTF-8");
	}
	
}
