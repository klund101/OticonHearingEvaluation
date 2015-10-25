package com.example.hearing_evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.parse.Parse;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnTouchListener {
	
	Button webViewButton;
	
	ImageButton newTestButton;
	ImageButton archiveButton;
	
	public int initialRingVolume;	
	
	public String parseDataObjectId = "";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initGui();
        
        //Parse
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectId  = (String) b.get("parseDataObjectId");
        }
       
        Log.d("parseId_main", parseDataObjectId);
        
        
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE); // fix audio output volume
        
        initialRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);		
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);		

        //Parse
    }

	
	private void initGui() {
//		webViewButton = (Button) findViewById(R.id.webViewMainBtn);
//		webViewButton.setOnClickListener(this);
		
		newTestButton = (ImageButton) findViewById(R.id.btnNewTest);
		newTestButton.setOnTouchListener(this);
		archiveButton = (ImageButton) findViewById(R.id.btnArchive);
		archiveButton.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
//		case R.id.webViewMainBtn:
//			Intent webViewA = new Intent(MainActivity.this, WebViewResultActivity.class);
//	        startActivity(webViewA);
//		break;
		case R.id.btnNewTest:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				newTestButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				newTestButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent opA = new Intent(MainActivity.this, OptionsActivity.class);
				opA.putExtra("parseDataObjectId", parseDataObjectId);
				startActivity(opA);		
			}
		break;
		case R.id.btnArchive:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				archiveButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				archiveButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent archA = new Intent(MainActivity.this, ArchiveActivity.class);
				archA.putExtra("parseDataObjectId", parseDataObjectId);
				startActivity(archA);	
			}
		break;
		}
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
	            || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	        return true;
	    else
	        return true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP 
	    		|| keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	    	return true;
		else
			return true;
	}
	    
	
    protected void onResume() {
    super.onResume();
	
    }


	
    

}
