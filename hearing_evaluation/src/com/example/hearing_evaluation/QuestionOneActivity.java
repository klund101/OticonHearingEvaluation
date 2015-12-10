package com.example.hearing_evaluation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.github.mikephil.charting.data.Entry;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuestionOneActivity extends Activity implements OnTouchListener {
	
	public String parseDataObjectId;
	public List<ParseObject> answerObjectsList;
	public String testUserName;
	
	public String preObjdBValuesStringSubSLeft;
	public String preObjdBValuesStringLeft;
	float preObjdBValuesLeft = 0f;
	
	public String preObjdBValuesStringSubSRight;
	public String preObjdBValuesStringRight;
	float preObjdBValuesRight = 0f;
	
	public int q_1 = 100;
	public int q_2 = 100;
	public int q_3 = 100;
	public int q_4 = 100;
	public int q_5 = 100;
	public int q_6 = 100;
	public int q_7 = 100;
	public int q_8 = 100;
	public int q_9 = 100;
	public int q_10 = 100;
	
	public ImageButton q1ProceedButton;
	
	public RadioGroup radioTvGroup;
	public RadioButton radioTvYes;
	public RadioButton radioTvNo;
	
	public RadioGroup radioMumblingGroup;
	public RadioButton radioMumblingYes;
	public RadioButton radioMumblingNo;
	
	public RadioGroup radioRepeatGroup;
	public RadioButton radioRepeatYes;
	public RadioButton radioRepeatNo;
	
	public RadioGroup radioSoftSpeechGroup;
	public RadioButton radioSoftSpeechYes;
	public RadioButton radioSoftSpeechNo;
	
	public RadioGroup radioCrowdGroup;
	public RadioButton radioCrowdYes;
	public RadioButton radioCrowdNo;
	
	public RadioGroup radioTinnitusGroup;
	public RadioButton radioTinnitusYes;
	public RadioButton radioTinnitusNo;
	
	public RadioGroup radioHyperGroup;
	public RadioButton radioHyperYes;
	public RadioButton radioHyperNo;
	
	public RadioGroup radioSurgeryGroup;
	public RadioButton radioSurgeryYes;
	public RadioButton radioSurgeryNo;
	
	public RadioGroup radioColdGroup;
	public RadioButton radioColdYes;
	public RadioButton radioColdNo;
	
	public RadioGroup radioPainGroup;
	public RadioButton radioPainYes;
	public RadioButton radioPainNo;
	
	public MediaRecorder mRecorder = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_one);
		
		initGui();
		
		startCheckAmbientSoundLevel();
		getAmplitudeCheckAmbientSoundLevel();
		startCheckAmbientSoundLevel();
		
		//Parse
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectId  = (String) b.get("parseDataObjectId");
        }
        
        //Parse
      		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
      		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
      		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
      			@Override
      			public void done(ParseObject object, ParseException e) {
      				
      					testUserName = (String) object.get("Username");
      					//Log.d();
      					
      		    		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
      		    		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
      		    		query.whereEqualTo("Username", testUserName);
      		    		query.findInBackground(new FindCallback<ParseObject>() {	        
      		    			@Override
      		    			public void done(List<ParseObject> objects, ParseException e) {
      		    				// TODO Auto-generated method stub

      		    				answerObjectsList = objects;
      		    				
      		    				preObjdBValuesStringLeft = (String)(objects.get(objects.size()-2).get("HearingDataLeft"));
      		    				preObjdBValuesStringSubSLeft = preObjdBValuesStringLeft.substring(1);
      		    				preObjdBValuesStringSubSLeft = preObjdBValuesStringSubSLeft.replace(']', ' ');
      		    				preObjdBValuesStringSubSLeft = preObjdBValuesStringSubSLeft.trim();
      		    			    
      		    			    String[] preObjdBValuesReaderLeft = preObjdBValuesStringSubSLeft.split(",");
      		    			        for(int i=0; i<RepeatTask.freqValues.length; i++){
      		    			        	preObjdBValuesLeft  += Float.parseFloat(preObjdBValuesReaderLeft[i]);
      		    			        }
      		    			        
      	  		    			preObjdBValuesStringRight = (String)(objects.get(objects.size()-2).get("HearingDataRight"));
          		    			preObjdBValuesStringSubSRight = preObjdBValuesStringRight.substring(1);
          		    			preObjdBValuesStringSubSRight = preObjdBValuesStringSubSRight.replace(']', ' ');
          		    			preObjdBValuesStringSubSRight = preObjdBValuesStringSubSRight.trim();
          		    			   
          		    			String[] preObjdBValuesReaderRight = preObjdBValuesStringSubSRight.split(",");
          		    			    for(int i=0; i<RepeatTask.freqValues.length; i++){
          		    			       	preObjdBValuesRight += Float.parseFloat(preObjdBValuesReaderRight[i]);

          		    			    }
      		    			    if(preObjdBValuesLeft + preObjdBValuesRight == 0){
      		    			    	objects.get(objects.size()-2).deleteInBackground();
      		    			    	objects.get(objects.size()-2).saveInBackground();
      		    			    }
      		    			    else{
      		    				
	      		    				if((String)(objects.get(objects.size()-2).get("q_1")) != null){
	      		    					q_1 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_1"));
	      		    					Log.d("q_1", Integer.toString(q_1));
	      		    					switch(q_1){
		      		    					case 1:
		      		    						radioTvYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioTvNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_2")) != null){
	      		    					q_2 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_2"));
	      		    					Log.d("q_2", Integer.toString(q_2));
	      		    					switch(q_2){
		      		    					case 1:
		      		    						radioMumblingYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioMumblingNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_3")) != null){
	      		    					q_3 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_3"));
	      		    					Log.d("q_3", Integer.toString(q_3));
	      		    					switch(q_3){
		      		    					case 1:
		      		    						radioRepeatYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioRepeatNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_4")) != null){
	      		    					q_4 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_4"));
	      		    					Log.d("q_4", Integer.toString(q_4));
	      		    					switch(q_4){
		      		    					case 1:
		      		    						radioSoftSpeechYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioSoftSpeechNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_5")) != null){
	      		    					q_5 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_5"));
	      		    					Log.d("q_5", Integer.toString(q_5));
	      		    					switch(q_5){
		      		    					case 1:
		      		    						radioCrowdYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioCrowdNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_6")) != null){
	      		    					q_6 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_6"));
	      		    					Log.d("q_6", Integer.toString(q_6));
	      		    					switch(q_6){
		      		    					case 1:
		      		    						radioTinnitusYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioTinnitusNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_7")) != null){
	      		    					q_7 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_7"));
	      		    					Log.d("q_7", Integer.toString(q_7));
	      		    					switch(q_7){
		      		    					case 1:
		      		    						radioHyperYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioHyperNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_8")) != null){
	      		    					q_8 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_8"));
	      		    					Log.d("q_8", Integer.toString(q_8));
	      		    					switch(q_8){
		      		    					case 1:
		      		    						radioSurgeryYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioSurgeryNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_9")) != null){
	      		    					q_9 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_9"));
	      		    					Log.d("q_9", Integer.toString(q_9));
	      		    					switch(q_9){
		      		    					case 1:
		      		    						radioColdYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioColdNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    				if((String)(objects.get(objects.size()-2).get("q_10")) != null){
	      		    					q_10 = Integer.parseInt((String)objects.get(objects.size()-2).get("q_10"));
	      		    					Log.d("q_10", Integer.toString(q_10));
	      		    					switch(q_10){
		      		    					case 1:
		      		    						radioPainYes.setChecked(true);
		      		    					break;
		      		    					case 0:
		      		    						radioPainNo.setChecked(true);
		      		    					break;
	      		    					}
	      		    				}
	      		    			}
      		    			}
      		    		});      		    		
      		    }
      		});
    		
	}
	
	private void initGui() {
		radioTvGroup = (RadioGroup) findViewById(R.id.tvGroup);
		radioTvYes = (RadioButton) findViewById(R.id.tvRadioYes);
		radioTvYes.setOnTouchListener(this);
		radioTvNo = (RadioButton) findViewById(R.id.tvRadioNo);
		radioTvNo.setOnTouchListener(this);
		
		radioMumblingGroup = (RadioGroup) findViewById(R.id.mumblingGroup);
		radioMumblingYes = (RadioButton) findViewById(R.id.mumblingRadioYes);
		radioMumblingYes.setOnTouchListener(this);
		radioMumblingNo = (RadioButton) findViewById(R.id.mumblingRadioNo);
		radioMumblingNo.setOnTouchListener(this);
		
		radioRepeatGroup = (RadioGroup) findViewById(R.id.repeatGroup);
		radioRepeatYes = (RadioButton) findViewById(R.id.repeatRadioYes);
		radioRepeatYes.setOnTouchListener(this);
		radioRepeatNo = (RadioButton) findViewById(R.id.repeatRadioNo);
		radioRepeatNo.setOnTouchListener(this);
		
		radioSoftSpeechGroup = (RadioGroup) findViewById(R.id.softSpeechGroup);
		radioSoftSpeechYes = (RadioButton) findViewById(R.id.softSpeechRadioYes);
		radioSoftSpeechYes.setOnTouchListener(this);
		radioSoftSpeechNo = (RadioButton) findViewById(R.id.softSpeechRadioNo);
		radioSoftSpeechNo.setOnTouchListener(this);
		
		radioCrowdGroup = (RadioGroup) findViewById(R.id.crowdGroup);
		radioCrowdYes = (RadioButton) findViewById(R.id.crowdRadioYes);
		radioCrowdYes.setOnTouchListener(this);
		radioCrowdNo = (RadioButton) findViewById(R.id.crowdRadioNo);
		radioCrowdNo.setOnTouchListener(this);
		
		radioTinnitusGroup = (RadioGroup) findViewById(R.id.tinnitusGroup);
		radioTinnitusYes = (RadioButton) findViewById(R.id.tinnitusRadioYes);
		radioTinnitusYes.setOnTouchListener(this);
		radioTinnitusNo = (RadioButton) findViewById(R.id.tinnitusRadioNo);
		radioTinnitusNo.setOnTouchListener(this);
		
		radioHyperGroup = (RadioGroup) findViewById(R.id.hyperacousisGroup);
		radioHyperYes = (RadioButton) findViewById(R.id.hyperacousisRadioYes);
		radioHyperYes.setOnTouchListener(this);
		radioHyperNo = (RadioButton) findViewById(R.id.hyperacousisRadioNo);
		radioHyperNo.setOnTouchListener(this);
		
		radioSurgeryGroup = (RadioGroup) findViewById(R.id.surgeryGroup);
		radioSurgeryYes = (RadioButton) findViewById(R.id.surgeryRadioYes);
		radioSurgeryYes.setOnTouchListener(this);
		radioSurgeryNo = (RadioButton) findViewById(R.id.surgeryRadioNo);
		radioSurgeryNo.setOnTouchListener(this);
		
		radioColdGroup = (RadioGroup) findViewById(R.id.coldGroup);
		radioColdYes = (RadioButton) findViewById(R.id.coldRadioYes);
		radioColdYes.setOnTouchListener(this);
		radioColdNo = (RadioButton) findViewById(R.id.coldRadioNo);
		radioColdNo.setOnTouchListener(this);
		
		radioPainGroup = (RadioGroup) findViewById(R.id.painGroup);
		radioPainYes = (RadioButton) findViewById(R.id.painRadioYes);
		radioPainYes.setOnTouchListener(this);
		radioPainNo = (RadioButton) findViewById(R.id.painRadioNo);
		radioPainNo.setOnTouchListener(this);
		
		q1ProceedButton = (ImageButton) findViewById(R.id.q1ProceedBtn);
		q1ProceedButton.setOnTouchListener(this);		
		
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
        	
        	Intent qOneBack = new Intent(QuestionOneActivity.this, MainActivity.class);
        	startActivity(qOneBack);		   
	    	
	    	return true;
	    }
	    else
	    	return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.tvRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_1 = 1;
		        	Log.d("q_1", Integer.toString(q_1));
				}
			break;
			case R.id.tvRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_1 = 0;
		        	Log.d("q_1", Integer.toString(q_1));
				}
			break;
			case R.id.mumblingRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_2 = 1;
		        	Log.d("q_2", Integer.toString(q_2));
				}
			break;
			case R.id.mumblingRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_2 = 0;
		        	Log.d("q_2", Integer.toString(q_2));
				}
			break;
			case R.id.repeatRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_3 = 1;
		        	Log.d("q_3", Integer.toString(q_3));
				}
			break;
			case R.id.repeatRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_3 = 0;
		        	Log.d("q_3", Integer.toString(q_3));
				}
			break;
			case R.id.softSpeechRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_4 = 1;
		        	Log.d("q_4", Integer.toString(q_4));
				}
			break;
			case R.id.softSpeechRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_4 = 0;
		        	Log.d("q_4", Integer.toString(q_4));
				}
			break;
			case R.id.crowdRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_5 = 1;
		        	Log.d("q_5", Integer.toString(q_5));
				}
			break;
			case R.id.crowdRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_5 = 0;
		        	Log.d("q_5", Integer.toString(q_5));
				}
			break;
			case R.id.tinnitusRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_6 = 1;
		        	Log.d("q_6", Integer.toString(q_6));
				}
			break;
			case R.id.tinnitusRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_6 = 0;
		        	Log.d("q_6", Integer.toString(q_6));
				}
			break;
			case R.id.hyperacousisRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_7 = 1;
		        	Log.d("q_7", Integer.toString(q_7));
				}
			break;
			case R.id.hyperacousisRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_7 = 0;
		        	Log.d("q_7", Integer.toString(q_7));
				}
			break;
			case R.id.surgeryRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_8 = 1;
		        	Log.d("q_8", Integer.toString(q_8));
				}
			break;
			case R.id.surgeryRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_8 = 0;
		        	Log.d("q_8", Integer.toString(q_8));
				}
			break;
			case R.id.coldRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_9 = 1;
		        	Log.d("q_9", Integer.toString(q_9));
				}
			break;
			case R.id.coldRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_9 = 0;
		        	Log.d("q_9", Integer.toString(q_9));
				}
			break;
			case R.id.painRadioYes:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_10 = 1;
		        	Log.d("q_10", Integer.toString(q_10));
				}
			break;
			case R.id.painRadioNo:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q_10 = 0;
		        	Log.d("q_10", Integer.toString(q_10));
				}
			break;
			case R.id.q1ProceedBtn:
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					q1ProceedButton.setColorFilter(Color.argb(100, 0, 0, 0));
				}
				else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
					q1ProceedButton.setColorFilter(Color.argb(0, 0, 0, 0));
					
					if(q_1 < 100 && q_2 < 100 && q_3 < 100 && q_4 < 100 && q_5 < 100 && 
							q_6 < 100 && q_7 < 100 && q_8 < 100 && q_9 < 100 && q_10 < 100){
						
						
						AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
						if(audioManager.isWiredHeadsetOn()) { // audioManager.isWiredHeadsetOn()
							
							int ambientAmp = (int)getAmplitudeCheckAmbientSoundLevel();
							Log.d("ambientAmp", Integer.toString(ambientAmp));
							
							//double dbSplTH = 20*Math.log(ambientAmp/1);
							
							//Log.d("dbSplTH", Double.toString(dbSplTH));
							
							if(ambientAmp < 736) { // dbSplTH < 132
								//Parse 
						        ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
						        query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
						        query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
						          public void done(ParseObject userDataObject, ParseException e) {
						            if (e == null) {
						            		userDataObject.put("q_1", Integer.toString(q_1));
						            		userDataObject.put("q_2", Integer.toString(q_2));
						            		userDataObject.put("q_3", Integer.toString(q_3));
						            		userDataObject.put("q_4", Integer.toString(q_4));
						            		userDataObject.put("q_5", Integer.toString(q_5));
						            		userDataObject.put("q_6", Integer.toString(q_6));
						            		userDataObject.put("q_7", Integer.toString(q_7));
						            		userDataObject.put("q_8", Integer.toString(q_8));
						            		userDataObject.put("q_9", Integer.toString(q_9));
						            		userDataObject.put("q_10", Integer.toString(q_10));
						            		
						            		userDataObject.put("AmbientNoise","false");

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
						        
						        try{
						        	stopCheckAmbientSoundLevel();
						        }
						        catch(Exception e){
						        	
						        }
						        
								Intent nextQ = new Intent(QuestionOneActivity.this, TestActivity.class);
								nextQ.putExtra("parseDataObjectId", parseDataObjectId);
								//nextQ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(nextQ);
							}
							else{
								//Log.d("ambientAmp",Integer.toString(ambientAmp));
								AlertDialog alertDialog = new AlertDialog.Builder(QuestionOneActivity.this)
						        .setTitle("Ambient noise is too loud!")
						        .setMessage("Please relocate to a quieter area. If you choose to proceed anyway the results can be inaccurate.")
						        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
						        public void onClick(DialogInterface dialog, int which) { 
						            Log.d("mRecorder.toString", mRecorder.toString());
						        	startCheckAmbientSoundLevel();
						    				
						            }
						         })
						         .setNegativeButton("Proceed", new DialogInterface.OnClickListener() {
						         public void onClick(DialogInterface dialog, int which) {
	            	
										//Parse
								        ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
								        query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
								        query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
								          public void done(ParseObject userDataObject, ParseException e) {
								            if (e == null) {
								            		userDataObject.put("q_1", Integer.toString(q_1));
								            		userDataObject.put("q_2", Integer.toString(q_2));
								            		userDataObject.put("q_3", Integer.toString(q_3));
								            		userDataObject.put("q_4", Integer.toString(q_4));
								            		userDataObject.put("q_5", Integer.toString(q_5));
								            		userDataObject.put("q_6", Integer.toString(q_6));
								            		userDataObject.put("q_7", Integer.toString(q_7));
								            		userDataObject.put("q_8", Integer.toString(q_8));
								            		userDataObject.put("q_9", Integer.toString(q_9));
								            		userDataObject.put("q_10", Integer.toString(q_10));
								            		
								            		userDataObject.put("AmbientNoise","true");

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
								        
								        try{
								        	stopCheckAmbientSoundLevel();
								        }
								        catch(Exception e){
								        	
								        }
								        
										Intent nextQ = new Intent(QuestionOneActivity.this, TestActivity.class);
										nextQ.putExtra("parseDataObjectId", parseDataObjectId);
										//nextQ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(nextQ);

						            }
						         })
						    .setIcon(android.R.drawable.ic_dialog_alert)
						    .show();
							}
						}
						else{
							
							AlertDialog alertDialog = new AlertDialog.Builder(QuestionOneActivity.this)
					        .setTitle("Insert earphones!")
					        .setMessage("Please insert the supplied Sennheiser earphones to start the test")
					        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { 
					            	
					    				
					            }
					         })
					         .setIcon(android.R.drawable.ic_dialog_alert)
					         .show();
						}
						
					}
				}
				break;
		}
		return false;
	}
	
public void startCheckAmbientSoundLevel() {
        if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null"); 
                try {
					mRecorder.prepare();
					mRecorder.start();
					Log.d("prepare",".....");
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
         }
}

public void stopCheckAmbientSoundLevel() {
        if (mRecorder != null) {
                mRecorder.stop();       
                mRecorder.release();
                mRecorder = null;
        }
}

public double getAmplitudeCheckAmbientSoundLevel() {
        if (mRecorder != null)
                return  mRecorder.getMaxAmplitude();
        else
                return 0;

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
    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_DTMF, MainActivity.initialDtmfVolume, 0);
    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_ALARM, MainActivity.initialAlarmVolume, 0);
    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, MainActivity.initialNoteVolume, 0);
    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, MainActivity.initialSystemVolume, 0);
    
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
