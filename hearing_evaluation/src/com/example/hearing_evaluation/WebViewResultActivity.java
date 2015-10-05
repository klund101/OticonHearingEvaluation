package com.example.hearing_evaluation;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view_result);
		
		WebView myWebView = (WebView) findViewById(R.id.webViewResult);
		
		
		myWebView.loadUrl("http://www.oticon.dk/");

		//String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
		//myWebView.loadData(customHtml, "text/html", "UTF-8");
	}
	
}
