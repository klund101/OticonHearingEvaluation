package com.example.hearing_evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;

import org.puredata.android.io.PdAudio;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChannelShifterActivity extends Activity implements OnClickListener {
	
	Button channelShifterLeftButton;
	Button channelShifterRightButton;
	
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
		channelShifterLeftButton = (Button) findViewById(R.id.btnChannelShifterLeft);
		channelShifterLeftButton.setOnClickListener(this);
		channelShifterRightButton = (Button) findViewById(R.id.btnChannelShifterRight);
		channelShifterRightButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnChannelShifterLeft:
			
			if(System.currentTimeMillis() > oneEarTestToneInitTime + startOneEarTestTestDelay && AudioTrack.PLAYSTATE_PLAYING == OneEarTestTone.oneEarTestToneAudioTrack.getPlayState()){
				OneEarTestTone.oneEarTestToneAudioTrack.stop();
				OneEarTestTone.oneEarTestToneTimer.cancel();
			}
			
			Intent resultALeft = new Intent(ChannelShifterActivity.this, ResultActivity.class);
			resultALeft.putExtra("parseDataObjectId", parseDataObjectIdChannelShifter);
			startActivity(resultALeft); //
		break;
		case R.id.btnChannelShifterRight:
			
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
		break;
		}
		
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
