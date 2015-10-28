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
	
	public static boolean invertedEarPhones;
	public long oneEarTestToneInitTime;
	public final int startOneEarTestTestDelay = 1000;

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
        
        oneEarTestToneInitTime = System.currentTimeMillis();
        
        OneEarTestTone.oneEarTestToneTimer.schedule(new OneEarTestTone(), startOneEarTestTestDelay);
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
		            		userDataObject.put("invertedEarPhones", "true");
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
	
	protected void onResume() {
	    super.onResume();
	    OneEarTestTone.oneEarTestToneTimer = new Timer();
	    OneEarTestTone.oneEarTestToneTimer.schedule(new OneEarTestTone(), 500 + (int)(Math.random()*500));
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		if(System.currentTimeMillis() > oneEarTestToneInitTime + startOneEarTestTestDelay && AudioTrack.PLAYSTATE_PLAYING == OneEarTestTone.oneEarTestToneAudioTrack.getPlayState())
			OneEarTestTone.oneEarTestToneAudioTrack.stop();
			OneEarTestTone.oneEarTestToneTimer.cancel();
	    
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

}
