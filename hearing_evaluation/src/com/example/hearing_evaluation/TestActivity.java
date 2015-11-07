package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioTrack;
import android.os.Bundle;
import android.provider.ContactsContract;

public class TestActivity extends ActionBarActivity implements OnTouchListener {
	
	private static final int MIN_SAMPLE_RATE = 44100;
	
	public static ImageButton yesButton;
	
	public int tmpCount = 0; 
	public static float[] testDbResultLeft = new float[RepeatTask.freqValues.length];
	public static float[] testDbResultRight = new float[RepeatTask.freqValues.length];
	public static int toneLevel;
	
	public static int tmpToneLevel = 1000;
	public final int startTestDelay = 3000;
	
	public static boolean yesBtnClicked = false;
	public static int[] hearingThreshold = new int[6]; // history of hearing thresholds
	public static int currentFreq;
	public static final int testFlowEnd = RepeatTask.freqValues.length;
	public static boolean isLeftChannel = true;
    private static Context testActivityContext;
    
    public long initTime;

	public static String parseDataObjectId;
	
	RepeatTask repeatTask = new RepeatTask();
	
	public static int dBLevelIndex;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TestActivity.testActivityContext = getApplicationContext();
				
		setContentView(R.layout.activity_test);
		
        initGui();
                       
        //Parse
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectId  = (String) b.get("parseDataObjectId");
        }
       
        Log.d("d", parseDataObjectId);
        
		
		/// TEST FLOW
		
		initTime = System.currentTimeMillis();
		
		currentFreq = 0;
		
    	for(int i = 0; i <= 5; i++){
    		hearingThreshold[i] = 0;
    	}
		toneLevel = 25;
		dBLevelIndex = 3; // index + 1 of 25 dB
		
    	for(int i = 0; i<testDbResultLeft.length; i++){
    		testDbResultLeft[i] = 0f;
    		testDbResultRight[i] = 0f;
    	}
		
		RepeatTask.timer = new Timer();
		RepeatTask.timer.schedule(new RepeatTask(), startTestDelay);
		System.out.println(Integer.toString(currentFreq));

	}
	
	
	private void initGui() {
		yesButton = (ImageButton) findViewById(R.id.btnYes);
		yesButton.setOnTouchListener(this);
	}
	
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnYes:
			
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				yesButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				yesButton.setColorFilter(Color.argb(0, 0, 0, 0));
			
			RepeatTask.timer.cancel();
			RepeatTask.timer = new Timer(); 
			//repeatTask.timer.purge();
			//tmpCount += 1;
			//Log.d("BUTTOOOON CLICKED", "");
			
			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
				RepeatTask.audioTrack.stop();
			
			repeatTask = new RepeatTask();
						
			yesBtnClicked = true;
			RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
			
						        	       	        		 
			//if(tmpCount>=100/*9*/){// number of tested frequencies
//			if(currentFreq > 2){ //Testing 1000 Hz two times
//				currentFreq = 2;
////				
//				goToResults();
////				
//			 }
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
	    	
			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState()){
				RepeatTask.audioTrack.stop();
				RepeatTask.timer.cancel();
	    	
			AlertDialog alertDialog = new AlertDialog.Builder(TestActivity.this)
	        .setTitle("Leave test?")
	        .setMessage("Are you sure you want to leave the current hearing test? The incomplete test result will be discarded.")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) { 
	    			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
	    				RepeatTask.audioTrack.stop();
	    				RepeatTask.timer.cancel();
	    				
		            	ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		            	query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		            	try {
							Log.d("PARSE", parseDataObjectId);
		            	    query.get(parseDataObjectId).deleteInBackground();
		            	    query.get(parseDataObjectId).saveInBackground();
						} 
		            	catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
		            	Intent mainA = new Intent(TestActivity.getTestActivityContext(), MainActivity.class);
		            	mainA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            	TestActivity.getTestActivityContext().startActivity(mainA);		            	
		            	
	            }
	         })
	        .setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	    			RepeatTask.timer = new Timer();
	    			toneLevel -= 5;
	    			dBLevelIndex--;
	    			RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
	            }
	         })
	         .setIcon(android.R.drawable.ic_dialog_alert)
	         .show();
	         
			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {         
			    @Override
			    public void onCancel(DialogInterface dialog) {
	    			RepeatTask.timer = new Timer(); 
	    			toneLevel -= 5;
	    			dBLevelIndex--;
	    			RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
			    }
	         });
			}
	    	return true;
	    }
		else
			return true;
	}
	
    protected void onResume() {
    super.onResume();
	RepeatTask.timer = new Timer();
	toneLevel -= 5;
	RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
    }
	
    @Override
    protected void onPause() {
    super.onPause();
	if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
		RepeatTask.audioTrack.stop();
		RepeatTask.timer.cancel();
    
    }
    
    @Override
    protected void onStop() {
        Log.d("", "Application stopped");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
    	
        Log.w("", "Application destroyed");

        super.onDestroy();
        
		if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
			RepeatTask.audioTrack.stop();
			RepeatTask.timer.cancel();
			
        	ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
        	query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
        	try {
				Log.d("PARSE", parseDataObjectId);
        	    query.get(parseDataObjectId).deleteInBackground();
        	    query.get(parseDataObjectId).saveInBackground();
			} 
        	catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
	
	public static void goToResults(){
			
		
		//Parse
	        ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
	        query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
	        query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
	          public void done(ParseObject userDataObject, ParseException e) {
	            if (e == null) {
	            	userDataObject.put("HearingDataLeft", Arrays.toString(testDbResultLeft));
	            	userDataObject.put("HearingDataRight", Arrays.toString(testDbResultRight));
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
		 
		 Intent ChannelSA = new Intent(TestActivity.getTestActivityContext(), ChannelShifterActivity.class);
		 ChannelSA.putExtra("parseDataObjectId", parseDataObjectId);
		 ChannelSA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 TestActivity.getTestActivityContext().startActivity(ChannelSA);
		
	}
	
	public static Context getTestActivityContext() {
        return TestActivity.testActivityContext;
    }

}
