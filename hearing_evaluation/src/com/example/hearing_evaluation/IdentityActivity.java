package com.example.hearing_evaluation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class IdentityActivity extends Activity implements OnTouchListener {
	
	ImageButton saveProfileButton;
	EditText uName;
	EditText uAge;
	RadioButton radioButtonF;
	RadioButton radioButtonM;
	RadioGroup radioGenderGroup;
	
	public String uNameString = "";
	public String uAgeString = "";
	public String uGenderString = "";
	public String date;
	
	public static String profileName = "";
	public static String profileAge = "";
	public static String profileGender = "";
	
	public String dataFile = "OticonAppData.txt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identity);
		
		initGui();
		
		uAge = (EditText)findViewById(R.id.userAge);
		uName = (EditText)findViewById(R.id.userName);
		uName.clearComposingText();
		
	}       
	
	private void initGui() {
		saveProfileButton = (ImageButton) findViewById(R.id.btnSaveProfile);
		saveProfileButton.setOnTouchListener(this);
		
		radioGenderGroup = (RadioGroup) findViewById(R.id.genderGroup);
		
		radioButtonF = (RadioButton) findViewById(R.id.genderFemale);
		radioButtonF.setOnTouchListener(this);
		radioButtonM = (RadioButton) findViewById(R.id.genderMale);
		radioButtonM.setOnTouchListener(this);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnSaveProfile:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				saveProfileButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				saveProfileButton.setColorFilter(Color.argb(0, 0, 0, 0));
			
			 boolean isInt = true;

			 try { 
			      Integer.parseInt(uAge.getText().toString());
			 } catch (NumberFormatException e) {
			     isInt = false;
			 }
			
			if(uName.getText().toString().length() >= 1 && uGenderString != "" && isInt){	
				
				profileName = uName.getText().toString();
				profileAge = uAge.getText().toString();
				profileGender = uGenderString;
				
				ProfileIdActivity.newProfileObject = new ParseObject("hearingEvaluationData");
				ProfileIdActivity.newProfileObject.put("Username", profileName);
				ProfileIdActivity.newProfileObject.put("GoogleId", MainActivity.staticEmailId);
				ProfileIdActivity.newProfileObject.put("HearingDataLeft", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
				ProfileIdActivity.newProfileObject.put("HearingDataRight", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
				ProfileIdActivity.newProfileObject.put("Age", profileAge);
				ProfileIdActivity.newProfileObject.put("Gender", profileGender);
				ProfileIdActivity.newProfileObject.put("invertedEarPhones", "false");
				
		        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		        String currentDateandTime = sdf.format(new Date());
		        ProfileIdActivity.newProfileObject.put("timeAndDate", currentDateandTime);
				
				try {
					ProfileIdActivity.newProfileObject.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent saveP = new Intent(IdentityActivity.this, ProfileIdActivity.class);
				startActivity(saveP);
			}
			}
		break;
		case R.id.genderFemale:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
	        	uGenderString = "F";
	        	Log.d("uGenderString", uGenderString);
			}
		break;
		case R.id.genderMale:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
	        	uGenderString = "M";
	        	Log.d("uGenderString", uGenderString);
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
	
}
