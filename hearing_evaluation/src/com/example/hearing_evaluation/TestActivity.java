package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import android.widget.ProgressBar;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
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
    
  //pulsed envelope
    public static int numSamplesTest = RepeatTask.duration * RepeatTask.sampleRate;
    public static float env[] = new float[numSamplesTest*2];
    public int transition = (RepeatTask.sampleRate/1000)*60;
    public float attack = 0;
    public float release = 1;
    public int pulseLength = RepeatTask.sampleRate/2;
    public int envCount;
	
    public static int upperThresholdCount = 0;
    
    public static long initTime;

	public static String parseDataObjectId;
	
	RepeatTask repeatTask = new RepeatTask();
	
	public static int dBLevelIndex;
	
	public static ProgressBar progress;
	
	public static float[][] dBhLArray = new float[][]{
		  {0f, 19f, 35f, 61f, 105f, 189f, 336f, 600f, 1070f, 1940f, 3450f, 6200f, 1100f, 19500f, 32000f},
		  {0f, 9.05f, 11f, 20.5f, 38f, 68f, 120f, 215f, 380f, 680f, 1220f, 2200f, 3900f, 7000f, 12500f},
		  {0f, 2.1f, 9.05f, 11.1f, 22f, 39f, 72.1f, 126f, 224f, 396f, 705f, 1265f, 2275f, 4070f, 7300f},
		  {0f, 0f, 2.01f, 9.04f, 11.3f, 21.5f, 38.7f, 72.1f, 125f, 222f, 395f, 700f, 1258f, 2265f, 4055f},
		  {0f, 0f, 2.01072f, 9.12f, 12.8f, 26.1f, 45.05f, 80f, 140f, 249f, 441f, 792f, 1410f, 2535f, 4530f},
		  {0f, 0f, 3.2f, 10.2f, 18.1f, 32.02f, 56f, 103f, 179f, 321f, 568f, 1020f, 1835f, 3280f, 5850f},
		  {0f, 0f, 0f, 2.01f, 10.1f, 11.5f, 30.3f, 50.9f, 85.5f, 155f, 273.5f, 485f, 875f, 1555f, 2800f}
	};
	
//	public static int[][] expectedFemale= new int[][]{
//		{0,0,0,0,0,0,0},// 20-29
//		{0,0,0,0,1,2,3},// 30-39
//		{0,0,0,0,1,2,3},// 40-49
//		{0,0,0,0,1,2,4},// 50-59
//		{0,0,0,1,2,4,5},// 60-69
//		{0,0,1,2,4,6,9},// 70-79
//		{0,0,1,3,5,9,14}// 80-
//	};
//	public static int[][] expectedMale= new int[][]{
//		{0,0,0,0,0,0,0},// 20-29
//		{0,0,0,0,1,2,3},// 30-39
//		{0,0,0,0,1,2,4},// 40-49
//		{0,0,0,0,1,3,4},// 50-59
//		{0,0,0,1,3,5,7},// 60-69
//		{0,0,1,4,6,9,11},// 70-79
//		{0,0,2,5,8,11,14}// 80-
//	};
	
	public static int[][] expectedFemale= new int[][]{
		{0,0,0,0,0,0,0},// 20-29
		{0,0,0,0,0,1,1},// 30-39
		{0,1,1,1,1,2,2},// 40-49
		{0,1,1,1,2,3,4},// 50-59
		{0,2,2,2,3,5,6},// 60-69
		{0,3,3,3,5,7,10},// 70-79
		{0,4,4,5,6,10,14}// 80-
	};
	public static int[][] expectedMale= new int[][]{
		{0,0,0,0,0,0,0},// 20-29
		{0,0,0,0,0,1,1},// 30-39
		{0,1,1,1,1,2,3},// 40-49
		{0,1,1,1,2,5,6},// 50-59
		{0,2,2,2,4,7,9},// 60-69
		{0,3,3,4,6,10,12},// 70-79
		{0,4,5,5,8,12,14}// 80-
	};
	
	public static String testGender;
	public static int testAge;
	public static int answerAll;
	public static int[] offset = {0, 0, 1, 1, 2, 2};

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
		
		
    	for(int i = 0; i<testDbResultLeft.length; i++){
    		testDbResultLeft[i] = 0f;
    		testDbResultRight[i] = 0f;
    	}
    	
		//Parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				answerAll = Integer.parseInt((String) object.get("q_1")) + Integer.parseInt((String) object.get("q_2")) +
						Integer.parseInt((String) object.get("q_3")) + Integer.parseInt((String) object.get("q_4")) + 
						Integer.parseInt((String) object.get("q_5"));
				testGender = (String) object.get("Gender");
				//Log.d("testGender",testGender);
				testAge = Integer.parseInt((String) object.get("Age"));
				Log.d("testAge", Integer.toString(testAge));
				
				if(testAge < 20)
					testAge = 20;
				if(testAge > 89)
					testAge = 80;
				
		    	if(testGender.equals("F")){
		    		dBLevelIndex = offset[answerAll] + expectedFemale[((int)(testAge/10))-2][3] + 6; //offset + 40 dB (8) - 1
		    	}
		    	else if(testGender.equals("M")){
		    		dBLevelIndex = offset[answerAll] + expectedMale[((int)(testAge/10))-2][3] + 6; //offset + 40 dB (8) - 1
		    	}	
	    	
		    	toneLevel = (dBLevelIndex+1)*5;
		    }
		});
		
		
		
//		dBLevelIndex = 3; // index + 1 of 25 dB
//    	toneLevel = 25;
		
    	
    	/// Envelope for pulsed tone ----------------------------------------------------
    	for(int i = 1; i < numSamplesTest; ++i){
    		
    		env[i] = (i/pulseLength)%2;
    		//Log.d("env[i]", Float.toString(env[i]));
    		if(i >= numSamplesTest-transition){
    			env[i] = env[i-1]-(1.0f/transition);
    			//Log.d("fade", Float.toString(env[i]));
    		}
    		else {
    			
	    		if(env[i] - env[i-1] == 1){
	    			for(int j = i; j < i+transition; j++){
		    			attack = attack+(1.0f/transition);
		    			env[j] = attack;
		    			//Log.d("attack", Float.toString(env[j]));
	    			}
	    			i = i+transition-1;
	    			attack = 0;
	    		}
	    		else if(env[i] - env[i-1] == -1){
	    			for(int j = i; j < i+transition; j++){
		    			release = release-(1.0f/transition);
		    			env[j] = release;
		    			//Log.d("release", Float.toString(env[j]));
	    			}
	    			i = i+transition-1;
	    			release = 1;
	    		}
    		}
    	}
		///----------------------------------------------------------------------------
    	
		RepeatTask.timer = new Timer();
		RepeatTask.timer.schedule(new RepeatTask(), startTestDelay);
		System.out.println(Integer.toString(currentFreq));

	}
	
	
	private void initGui() {
		yesButton = (ImageButton) findViewById(R.id.btnYes);
		yesButton.setOnTouchListener(this);
		
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		progress.setOnTouchListener(this);
		progress.getProgressDrawable().setColorFilter(Color.argb(255, 197, 23, 139), Mode.SRC_IN);
		progress.setScaleY(3f);
	}
	
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnYes:
			
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				yesButton.setColorFilter(Color.argb(150, 0, 0, 0));
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
	    Log.d("onPause","onPause");
	    
	    super.onPause();
	    
        if(MainActivity.initialRingVolume == 0){
        	MainActivity.audioManager.setRingerMode(0);
			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState()){
				RepeatTask.audioTrack.stop();
				RepeatTask.timer.cancel();
			}
        }
        else{
        	MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.initialMusicVolume, 0);		
		    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_RING, MainActivity.initialRingVolume, 0);
		    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_DTMF, MainActivity.initialDtmfVolume, 0);
		    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_ALARM, MainActivity.initialAlarmVolume, 0);
		    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, MainActivity.initialNoteVolume, 0);
		    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, MainActivity.initialSystemVolume, 0);
		    
		    MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, MainActivity.initialVibNote);
			MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, MainActivity.initialVibRing);
			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState()){
				RepeatTask.audioTrack.stop();
				RepeatTask.timer.cancel();
	        }
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
		
		RepeatTask.timer = new Timer();
		toneLevel -= 5;
		dBLevelIndex--;
		RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
		
		Log.d("musicVolume", Integer.toString(MainActivity.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	    Log.d("onDestroy","onDestroy");
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

}
