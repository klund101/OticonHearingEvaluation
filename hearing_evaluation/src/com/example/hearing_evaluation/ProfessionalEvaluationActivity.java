package com.example.hearing_evaluation;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ProfessionalEvaluationActivity extends Activity implements OnTouchListener, CheckBox.OnCheckedChangeListener{
	
	public ImageButton sendRequestButton;
	public CheckBox emailCheckBox;
	public CheckBox phoneCheckBox;
	
	public String contactMethod;
	
	String parseDataObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_professional_evaluation);
		initGui();
		
		emailCheckBox.setChecked(true);
		contactMethod = "email";
		
		
		Intent iin = getIntent();
	    Bundle b = iin.getExtras();
	    if (b != null) {
	    	parseDataObjectId  = (String) b.get("parseDataObjectId");
	    }
	}

	public void initGui(){
		
		sendRequestButton = (ImageButton) findViewById(R.id.btnSendRequest);
		sendRequestButton.setOnTouchListener(this);
		
		emailCheckBox = (CheckBox) findViewById(R.id.checkBoxEmail);
		emailCheckBox.setOnCheckedChangeListener(this);
		phoneCheckBox = (CheckBox) findViewById(R.id.checkBoxPhone);
		phoneCheckBox.setOnCheckedChangeListener(this);
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnSendRequest:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				sendRequestButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				sendRequestButton.setColorFilter(Color.argb(0, 0, 0, 0));
				
				AlertDialog alertDialog = new AlertDialog.Builder(ProfessionalEvaluationActivity.this)
		        .setTitle("Send request?")
		        .setMessage("I agree that my result data and audiogram will be sent to the audiologist.")
		        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	
		        	AlertDialog alertDialog = new AlertDialog.Builder(ProfessionalEvaluationActivity.this)
			        .setTitle("Request sent!")
			        .setMessage("A local audiologist will contact you as soon as possible by " + contactMethod + " to book an appointment for a hearing evaluation.")
			        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	ProfessionalEvaluationActivity.this.onBackPressed();	
			            }
			         })
			         .setIcon(android.R.drawable.ic_dialog_alert)
			         .show();			        	
		            }
		         })
		        .setPositiveButton("No", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	ProfessionalEvaluationActivity.this.onBackPressed();	
		            }
		         })
		         .setIcon(android.R.drawable.ic_dialog_alert)
		         .show();			
			}
		break;
		}
		return false;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked) {
            if(buttonView == emailCheckBox) {
            	Log.d("emailCheckBox","emailCheckBox");
	            if(emailCheckBox.isChecked()){
	            	phoneCheckBox.setChecked(false);
	            	contactMethod = "email";
            	}
            }	
            if(buttonView == phoneCheckBox) {
            	Log.d("phoneCheckBox","phoneCheckBox");
            	if(emailCheckBox.isChecked()){
	            	emailCheckBox.setChecked(false);
	            	contactMethod = "phone";
            	}
            }
		}
		else{
            if(buttonView == emailCheckBox) {
            	Log.d("emailCheckBox","emailCheckBox");
            	phoneCheckBox.setChecked(true);
	            emailCheckBox.setChecked(false);
            }	
            if(buttonView == phoneCheckBox) {
            	Log.d("phoneCheckBox","phoneCheckBox");
            	emailCheckBox.setChecked(true);
            	phoneCheckBox.setChecked(false);
            }
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
	    		|| keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
	    	return true;
	    }
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
