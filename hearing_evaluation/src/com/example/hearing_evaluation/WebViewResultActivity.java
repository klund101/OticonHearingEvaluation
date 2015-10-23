package com.example.hearing_evaluation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.webkit.JavascriptInterface;


public class WebViewResultActivity extends Activity {

	@SuppressLint("JavascriptInterface")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view_result);

		 WebView webView = (WebView) findViewById(R.id.webViewResult);
//	     webView.getSettings().setJavaScriptEnabled(true);  
//	     webView.loadUrl("file:///android_asset/resultsOticonChart.html");
	     
	     final WebSettings ws = webView.getSettings();
	     ws.setJavaScriptEnabled(true);
	     ws.setPluginState(WebSettings.PluginState.ON);
	     ws.setAllowFileAccess(true);
	     ws.setDomStorageEnabled(true);
	     ws.setAllowContentAccess(true);
	     ws.setAllowFileAccessFromFileURLs(true);
	     ws.setAllowUniversalAccessFromFileURLs(true);
	     webView.setWebViewClient(new WebViewClient() {
	         @Override
	         public void onReceivedError(WebView view, int errorCode,
	                 String description, String failingUrl) {
	             Toast.makeText(WebViewResultActivity.this, description,
	                     Toast.LENGTH_SHORT).show();
	         }

	         @Override
	         public void onReceivedSslError(WebView view,
	                 SslErrorHandler handler, SslError error) {
	             // TODO Auto-generated method stub
	             // this method will proceed your url however if certification issues are there or not
	             handler.proceed();
	         }

	     });
	     
	     webView.setWebChromeClient(new WebChromeClient());
	     webView.addJavascriptInterface(new WebAppInterface( this ), "Android");
	     webView.loadUrl("file:///android_asset/resultsOticonChart.html");

	}
	
    public class WebAppInterface {
        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }
    }
	
}
