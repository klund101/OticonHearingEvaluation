package com.example.hearing_evaluation;

import android.app.Activity;
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

public class InstructionsTwoActivity extends Activity implements OnTouchListener{

	ImageButton instructionTwoProceedButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions_two);
		
		initGui();
	}
	
	private void initGui() {
		instructionTwoProceedButton = (ImageButton) findViewById(R.id.btnInstructionsTwoProceed);
		instructionTwoProceedButton.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnInstructionsTwoProceed:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				instructionTwoProceedButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				instructionTwoProceedButton.setColorFilter(Color.argb(0, 0, 0, 0));
				startActivity(new Intent(InstructionsTwoActivity.this, InstructionsThreeActivity.class)); //
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
	    else if (keyCode == KeyEvent.KEYCODE_BACK){
	    	super.onBackPressed();
	    	return true;
	    }
		else
			return true;
	}

	@SuppressWarnings("deprecation")
	@Override
    protected void onStart() {
		super.onStart();
	    Log.d("onStart","onStart");	
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onRestart() {
		super.onRestart();
	    Log.d("onRestart","onRestart");	
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onStop() {
	    super.onStop();
	    Log.d("onStop","onStop");
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
		super.onPause();
	    Log.d("onPause","onPause");
	    
        if(MainActivity.initialRingVolume == 0){
        	MainActivity.audioManager.setRingerMode(0);
        }
        else{
        	MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.initialMusicVolume, 0);		
	    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_RING, MainActivity.initialRingVolume, 0);
		MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, MainActivity.initialVibNote);
		MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, MainActivity.initialVibRing);
        }
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
	    super.onResume();
	    Log.d("onResume","onResume");
	    MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
		MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
		MainActivity.audioManager.setRingerMode(0);
		MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.maxVolume, 0);
		
		Log.d("musicVolume", Integer.toString(MainActivity.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	    Log.d("onDestroy","onDestroy");
    }
}
