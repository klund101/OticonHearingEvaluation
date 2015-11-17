package com.example.hearing_evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

public class ChannelShifterActivity extends Activity implements OnTouchListener {
	
	ImageButton channelShifterLeftButton;
	ImageButton channelShifterRightButton;
	
	public static String parseDataObjectIdChannelShifter;
	
	public long oneEarTestToneInitTime;
	public final int startOneEarTestTestDelay = 1000;
	
	public String leftEarHearingData;
	public String rightEarHearingData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_shifter);
		
		initGui();
		
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectIdChannelShifter  = (String) b.get("parseDataObjectId");
        }
        
    	//Parse
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
    	query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
    	query.getInBackground(TestActivity.parseDataObjectId, new GetCallback<ParseObject>() {
    		@Override
    		public void done(ParseObject object, ParseException e) {
    			leftEarHearingData = (String) object.get("HearingDataLeft");
    			rightEarHearingData = (String) object.get("HearingDataRight");
    		}
    	});
        
        oneEarTestToneInitTime = System.currentTimeMillis();
        try{
        	OneEarTestTone.oneEarTestToneTimer.schedule(new OneEarTestTone(), startOneEarTestTestDelay);
        }
        catch(Exception e){
        	
        }
	}

	private void initGui() {
		channelShifterLeftButton = (ImageButton) findViewById(R.id.btnChannelShifterLeft);
		channelShifterLeftButton.setOnTouchListener(this);
		channelShifterRightButton = (ImageButton) findViewById(R.id.btnChannelShifterRight);
		channelShifterRightButton.setOnTouchListener(this);
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnChannelShifterLeft:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				channelShifterLeftButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				channelShifterLeftButton.setColorFilter(Color.argb(0, 0, 0, 0));
			
				if(System.currentTimeMillis() > oneEarTestToneInitTime + startOneEarTestTestDelay && AudioTrack.PLAYSTATE_PLAYING == OneEarTestTone.oneEarTestToneAudioTrack.getPlayState()){
					OneEarTestTone.oneEarTestToneAudioTrack.stop();
					OneEarTestTone.oneEarTestToneTimer.cancel();
				}
				
				Intent resultALeft = new Intent(ChannelShifterActivity.this, ResultActivity.class);
				resultALeft.putExtra("parseDataObjectId", parseDataObjectIdChannelShifter);
				startActivity(resultALeft); //
				
			}
		break;
		case R.id.btnChannelShifterRight:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				channelShifterRightButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				channelShifterRightButton.setColorFilter(Color.argb(0, 0, 0, 0));

				if(System.currentTimeMillis() > oneEarTestToneInitTime + startOneEarTestTestDelay && AudioTrack.PLAYSTATE_PLAYING == OneEarTestTone.oneEarTestToneAudioTrack.getPlayState()){
					OneEarTestTone.oneEarTestToneAudioTrack.stop();
					OneEarTestTone.oneEarTestToneTimer.cancel();
				}
				
	    		//Parse
		        ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		        query.getInBackground(TestActivity.parseDataObjectId, new GetCallback<ParseObject>() {
		          public void done(ParseObject userDataObject, ParseException e) {
		            if (e == null) {
		            			userDataObject.put("HearingDataLeft", rightEarHearingData);
		            			userDataObject.put("HearingDataRight", leftEarHearingData);
		            	try {
		            		userDataObject.save();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            } else {
		              // something went wrong
		            }
		          }
		        });
				
				Intent resultARight = new Intent(ChannelShifterActivity.this, ResultActivity.class);
				resultARight.putExtra("parseDataObjectId", parseDataObjectIdChannelShifter);
				startActivity(resultARight);
		}
		break;
		}
		
		return false;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP 
	    		|| keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	    	return true;
	    else if (keyCode == KeyEvent.KEYCODE_BACK){
	    	
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
    		if(System.currentTimeMillis() > oneEarTestToneInitTime + startOneEarTestTestDelay && AudioTrack.PLAYSTATE_PLAYING == OneEarTestTone.oneEarTestToneAudioTrack.getPlayState())
    			OneEarTestTone.oneEarTestToneAudioTrack.stop();
    			OneEarTestTone.oneEarTestToneTimer.cancel();
        }
        else{
        	MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.initialMusicVolume, 0);		
		    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_RING, MainActivity.initialRingVolume, 0);
			MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, MainActivity.initialVibNote);
			MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, MainActivity.initialVibRing);
		if(System.currentTimeMillis() > oneEarTestToneInitTime + startOneEarTestTestDelay && AudioTrack.PLAYSTATE_PLAYING == OneEarTestTone.oneEarTestToneAudioTrack.getPlayState())
			OneEarTestTone.oneEarTestToneAudioTrack.stop();
			OneEarTestTone.oneEarTestToneTimer.cancel();
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
		
	    OneEarTestTone.oneEarTestToneTimer = new Timer();
	    OneEarTestTone.oneEarTestToneTimer.schedule(new OneEarTestTone(), 500 + (int)(Math.random()*500));

    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	    Log.d("onDestroy","onDestroy");
    }
}
