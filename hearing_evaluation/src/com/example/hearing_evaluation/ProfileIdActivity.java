package com.example.hearing_evaluation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class ProfileIdActivity extends Activity implements OnItemSelectedListener, OnTouchListener {

	Spinner spinnerProfileSpinner;
	ImageButton newUserButton;
	ImageButton startTestProfileActButton;
	ImageButton deleteProfileButton;
	public ArrayList<String> arraySpinner = new ArrayList<String>();
	public ArrayList<Integer> arraySpinnerPosition = new ArrayList<Integer>();
	public ArrayAdapter<String> adapter;

	public int deleteCount = 0;
	
	public List<ParseObject> publicParseObjectsList;
	public String parseListObjectId;
	
	public static ParseObject newProfileObject = null;
	public ParseObject userDataObject;
	
	public boolean arrDuplicate = false;
	public int spinnerPosition;
	
	public String profileNameSpinner;
	public String profileAgeSpinner;
	public String profileGenderSpinner;
	
	public MediaRecorder mRecorder = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_id);
		
		Log.d("staticEmailId", MainActivity.staticEmailId);
		
		initGui();
		
		updateProfileSpinner();
		
		startCheckAmbientSoundLevel();
		getAmplitudeCheckAmbientSoundLevel();
		startCheckAmbientSoundLevel();
	}

	private void initGui() {
		spinnerProfileSpinner = (Spinner) findViewById(R.id.spinnerProfile);
		spinnerProfileSpinner.setOnItemSelectedListener(this);
		
		newUserButton = (ImageButton) findViewById(R.id.btnNewUser);
		newUserButton.setOnTouchListener(this);
		
		startTestProfileActButton = (ImageButton) findViewById(R.id.btnStartTestProfileAct);
		startTestProfileActButton.setOnTouchListener(this);
		
		deleteProfileButton= (ImageButton) findViewById(R.id.btnDeleteProfile);
		deleteProfileButton.setOnTouchListener(this);
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, final int position,
			long id) {
		//Parse
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		query.findInBackground(new FindCallback<ParseObject>() {	        
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub

				publicParseObjectsList = objects;
				profileNameSpinner = objects.get((objects.size()-1)-((objects.size()-1)-arraySpinnerPosition.get(position-deleteCount))).get("Username").toString();
				Log.d("arraySpinnerPosition", profileNameSpinner);
				profileAgeSpinner = objects.get((objects.size()-1)-((objects.size()-1)-arraySpinnerPosition.get(position-deleteCount))).get("Age").toString();
				profileGenderSpinner = objects.get((objects.size()-1)-((objects.size()-1)-arraySpinnerPosition.get(position-deleteCount))).get("Gender").toString();
				
			}
		});
		spinnerPosition = position;		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.btnNewUser:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				newUserButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				newUserButton.setColorFilter(Color.argb(0, 0, 0, 0));
				startActivity(new Intent(ProfileIdActivity.this, IdentityActivity.class)); //
			}
		break;
		case R.id.btnStartTestProfileAct:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				startTestProfileActButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				startTestProfileActButton.setColorFilter(Color.argb(0, 0, 0, 0));
				AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
				if(audioManager.isWiredHeadsetOn()) {
					
					int ambientAmp = (int)getAmplitudeCheckAmbientSoundLevel();
					Log.d("ambientAmp", Integer.toString(ambientAmp));
					
					if(ambientAmp < 14000) {
						//Parse 
						userDataObject = new ParseObject("hearingEvaluationData");
				        userDataObject.put("Username", profileNameSpinner);				        
				        userDataObject.put("GoogleId", MainActivity.staticEmailId);
				        userDataObject.put("HearingDataLeft", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
				        userDataObject.put("HearingDataRight", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
				        userDataObject.put("Age", profileAgeSpinner);
				        userDataObject.put("Gender", profileGenderSpinner);
				        userDataObject.put("invertedEarPhones", "false");
				        
				        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
				        String currentDateandTime = sdf.format(new Date());
				        userDataObject.put("timeAndDate", currentDateandTime);
				        
				        //if(newProfileObject != null)
				        	//newProfileObject.deleteInBackground();
				        
				        try {
							userDataObject.save();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						Intent testA = new Intent(ProfileIdActivity.this, TestActivity.class);
						testA.putExtra("parseDataObjectId", userDataObject.getObjectId());
						Log.d("parseDataObjectId idAct", userDataObject.getObjectId());
						startActivity(testA);
					}
					else{
						//Log.d("ambientAmp",Integer.toString(ambientAmp));
						AlertDialog alertDialog = new AlertDialog.Builder(ProfileIdActivity.this)
				        .setTitle("Ambient noise is too loud!")
				        .setMessage("Please relocate to a quieter area.")
				        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            Log.d("mRecorder.toString", mRecorder.toString());
				        	startCheckAmbientSoundLevel();
				    				
				            }
				         })
				         .setIcon(android.R.drawable.ic_dialog_alert)
				         .show();
					}
				}
				else{
					
					AlertDialog alertDialog = new AlertDialog.Builder(ProfileIdActivity.this)
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
		break;
		case R.id.btnDeleteProfile:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				deleteProfileButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				deleteProfileButton.setColorFilter(Color.argb(0, 0, 0, 0));
			
			//Parse
			
			AlertDialog alertDialog = new AlertDialog.Builder(ProfileIdActivity.this)
	        .setTitle("Delete profile?")
	        .setMessage("Are you sure you want to delete this profile?")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) { 
	            	
	            	
	    			ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
	    			query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
	    			query.findInBackground(new FindCallback<ParseObject>() {	        
	    				@Override
	    				public void done(List<ParseObject> objects, ParseException e) {
	    					for(int i = objects.size()-1; i>=0; i--){
	    						if(objects.get(i).get("Username").toString().equals(profileNameSpinner)){
	    							try {
										objects.get(i).delete();
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Log.d("Delete Object", Integer.toString(i));
									Intent loadAct =new Intent(ProfileIdActivity.this, LoadingActivity.class);
									startActivity(loadAct);
	    								    							
	    						}
	    														
	    					}
							try {
								ParseObject.saveAll(objects);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	    					
	    				}
	    			});
	            	
	    				
	            }
	         })
	        .setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            }
	         })
	         .setIcon(android.R.drawable.ic_dialog_alert)
	         .show();
	         
			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {         
			    @Override
			    public void onCancel(DialogInterface dialog) {

			    }
	         });
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

	        finish();    	
	    	return true;
	    }
		else
			return true;
	}
	
	public void updateProfileSpinner(){
		//Parse
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
	    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
		query.findInBackground(new FindCallback<ParseObject>() {	        
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					publicParseObjectsList = objects;
					for(int i = objects.size()-1; i>=0; i--){
						arrDuplicate = false;
						for(int j = arraySpinner.size()-1; j>=0; j--){
							if(objects.get(i).get("Username").toString().equals(arraySpinner.get(j)))
								arrDuplicate = true;
								Log.d("arrDuplicate",Boolean.toString(arrDuplicate));
						}
						if(arrDuplicate == false){
							arraySpinnerPosition.add(i);
							arraySpinner.add(objects.get(i).get("Username").toString());
						}
					}
					adapter = new ArrayAdapter<String>(ProfileIdActivity.this, android.R.layout.simple_spinner_item, arraySpinner);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerProfileSpinner.setAdapter(adapter);
					adapter.notifyDataSetChanged();
		            
		        } else {
		            Log.d("", "" + e.getMessage());
		        }
				
			}
		});
		return;
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

}
