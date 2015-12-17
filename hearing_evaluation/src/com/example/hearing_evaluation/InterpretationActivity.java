package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class InterpretationActivity extends Activity implements OnTouchListener {
	
	public String parseDataObjectId;
	
	public String recEnt = "";
	public int ans9 = 0;
	
	public int ptaLeft;
	float dBValuesPTALeft;
	public String resultsValuesStringLeft;
	public String resultsValuesStringLeftSubS;
	public TextView diagnosisLeft;
	
	public int ptaRight;
	float dBValuesPTARight;
	public String resultsValuesStringRight;
	public String resultsValuesStringRightSubS;
	public TextView diagnosisRight;
	
	public TextView diagnosisRec;
	public ImageButton freeEvalButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interpretation);
		
		initGui();
		
		Intent iin = getIntent();
	    Bundle b = iin.getExtras();
	    if (b != null) {
	    	parseDataObjectId  = (String) b.get("parseDataObjectId");
	    }
		
	    diagnosisLeft.setText("");
	    diagnosisRight.setText("");
	    diagnosisRec.setText("");
	    
		//Parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 0)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 0)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to hyperacousis.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to pain in your ear.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 0)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus and hyperacousis.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus and hyperacousis.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus and pain in your ear.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus, hyperacousis and pain in your ear.";
						freeEvalButton.setVisibility(View.VISIBLE);
				}
				
				if((String)(object.get("q_9"))  != null)
					ans9 = Integer.parseInt((String)(object.get("q_9")));
				Log.d("ans9",Integer.toString(ans9));
				
				resultsValuesStringLeft = (String) object.get("HearingDataLeft");
				resultsValuesStringLeftSubS = resultsValuesStringLeft.substring(1);
				resultsValuesStringLeftSubS = resultsValuesStringLeftSubS.replace(']', ' ');
				resultsValuesStringLeftSubS = resultsValuesStringLeftSubS.trim();
			    
		        dBValuesPTALeft = 0;
			    String[] dBValuesReaderLeft = resultsValuesStringLeftSubS.split(",");
		        for(int i=2; i<6; i++){
		        	dBValuesPTALeft += Float.parseFloat(dBValuesReaderLeft[i]);
		        	Log.d("dBValuesPTALeft", Float.toString(dBValuesPTALeft));
		        }
		        ptaLeft = (int)(((dBValuesPTALeft/4)*-1)+0.5f);
		        Log.d("ptaLeft", Float.toString(ptaLeft));
		        

				resultsValuesStringRight = (String) object.get("HearingDataRight");
				resultsValuesStringRightSubS = resultsValuesStringRight.substring(1);
				resultsValuesStringRightSubS = resultsValuesStringRightSubS.replace(']', ' ');
				resultsValuesStringRightSubS = resultsValuesStringRightSubS.trim();
			    
		        dBValuesPTARight = 0;
		        String[] dBValuesReaderRight = resultsValuesStringRightSubS.split(",");
		        for(int i=2; i<6; i++){
		        	dBValuesPTARight += Float.parseFloat(dBValuesReaderRight[i]);
		        	Log.d("dBValuesPTARight", Float.toString(dBValuesPTARight));
		        }
		        ptaRight = (int)(((dBValuesPTARight/4)*-1)+0.5f);
		        Log.d("ptaRight", Float.toString(ptaRight));
		        
		        if(Math.abs(ptaLeft-ptaRight) <= 20){
		        
		        	if(ans9 == 1){
			        	diagnosisRight.setText("The results are unreliable due to that you have a cold. We recommend that you redo the test when you are over it." + "\n\n" + recEnt);
			        	diagnosisRight.setTextColor(Color.parseColor("#c5178b"));
			        	diagnosisLeft.setTextColor(color.transparent);
			        	freeEvalButton.setVisibility(View.INVISIBLE);
			        }
		        	else{
		        	
				        if(ptaLeft <= 20){
				        	diagnosisLeft.setText("The test results suggest that you do not have a hearing loss on your left ear. ");
				        	if((String)(object.get("q_6")) != null && (String)(object.get("q_7")) != null && (String)(object.get("q_8")) != null && (String)(object.get("q_10")) != null){
								if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_8"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 0)
									freeEvalButton.setVisibility(View.INVISIBLE);
							}
				        }
				        else if(ptaLeft > 20 && ptaLeft <= 40){
				        	diagnosisLeft.setText("The test results suggest that you have a mild hearing loss on your left ear. You should contact a hearing professional for a hearing test. ");
				        	freeEvalButton.setVisibility(View.VISIBLE);
				        }
				        else if(ptaLeft > 40 && ptaLeft <= 70){
				        	diagnosisLeft.setText("The test results suggest that you have a moderate hearing loss on your left ear. You should contact a hearing professional for a hearing test. ");
				        	freeEvalButton.setVisibility(View.VISIBLE);
				        }
				        else if(ptaLeft > 70){
				        	diagnosisLeft.setText("The test results suggest that you have a severe hearing loss on your left ear. You should contact a hearing professional for a hearing test. ");
				        	freeEvalButton.setVisibility(View.VISIBLE);
				        }
				        
				        
				        if(ptaRight <= 20){
				        	diagnosisRight.setText("The test results suggest that you do not have a hearing loss on your right ear. ");
				        	if(ptaLeft <= 20){	
					        	if((String)(object.get("q_6")) != null && (String)(object.get("q_7")) != null && (String)(object.get("q_8")) != null && (String)(object.get("q_10")) != null){
									if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_8"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 0)
										freeEvalButton.setVisibility(View.INVISIBLE);
								}
				        	}
				        }
				        else if(ptaRight > 20 && ptaRight <= 40){
				        	diagnosisRight.setText("The test results suggest that you have a mild hearing loss on your right ear. You should contact a hearing professional for a hearing test. ");
				        	freeEvalButton.setVisibility(View.VISIBLE);
				        }
				        else if(ptaRight > 40 && ptaRight <= 70){
				        	diagnosisRight.setText("The test results suggest that you have a moderate hearing loss on your right ear. You should contact a hearing professional for a hearing test. ");
				        	freeEvalButton.setVisibility(View.VISIBLE);
				        }
				        else if(ptaRight > 70){
				        	diagnosisRight.setText("The test results suggest that you have a severe hearing loss on your right ear. You should contact a hearing professional for a hearing test. ");
				        	freeEvalButton.setVisibility(View.VISIBLE);
				        }
				        
				        diagnosisRec.setText(recEnt);
		        	}
		        }
		        else{
		        	diagnosisRight.setText("Asymetrical results. Please check that the earplugs are correctly inserted and rerun the test in a quiet environement. " + "\n\n" + recEnt);
		        	diagnosisRight.setTextColor(Color.parseColor("#c5178b"));
		        	diagnosisLeft.setTextColor(color.transparent);
		        }
		    }
		});
		
	}
	
	private void initGui() {
		diagnosisLeft = (TextView) findViewById(R.id.diagnosisTxtLeft);
		diagnosisRight = (TextView) findViewById(R.id.diagnosisTxtRight);
		diagnosisRec = (TextView) findViewById(R.id.diagnosisTxtRec);
		
		freeEvalButton = (ImageButton) findViewById(R.id.btnFreeEval);
		freeEvalButton.setOnTouchListener(this);
		freeEvalButton.setVisibility(View.INVISIBLE);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnFreeEval:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				freeEvalButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				freeEvalButton.setColorFilter(Color.argb(0, 0, 0, 0));
				startActivity(new Intent(InterpretationActivity.this, ProfessionalEvaluationActivity.class)); //
			}
		break;
		}
		return false;
	}
}
