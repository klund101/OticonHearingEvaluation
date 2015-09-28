package com.example.hearing_evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import com.parse.Parse;
import com.parse.ParseObject;

//import com.xxmassdeveloper.mpchartexample.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnClickListener {
	
	Button toneButton;
	Button proceedButton;
	
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
		proceedButton = (Button) findViewById(R.id.btnProceed);
		proceedButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnProceed:
			Intent optA = new Intent(MainActivity.this, OptionsActivity.class);
			optA.putExtra("parseDataObjectId", parseDataObjectId);
	        startActivity(optA);
		break;
		}
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
//    PdAudio.startAudio(this);
//    
//		Log.d("PdAudio.isRunning()", String.valueOf(PdAudio.isRunning()));
//		if(!PdAudio.isRunning()){
//			try {
//				initAudio();
//				Log.d("PdAudio.isRunning()", String.valueOf(PdAudio.isRunning()));
//			} catch (IOException e) {
//				// 
//				e.printStackTrace();
//			}
//		}	
    }
    

}
