package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.logging.Handler;

import android.support.v7.app.ActionBarActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends ActionBarActivity implements OnTouchListener {
	
	
	ImageButton newTestButton;
	ImageButton archiveButton;
	
	public static AudioManager audioManager;
	public static AudioManager audioVibManager;
	public static int maxVolume;
	
	public static int initialRingVolume;
	public static int initialMusicVolume;
	public static int initialDtmfVolume;
	public static int initialNoteVolume;
	public static int initialAlarmVolume;
	public static int initialSystemVolume;
	
	
	public static int initialVibNote;
	public static int initialVibRing;
	public static int initRingerMode;
	
	public String parseDataObjectId = "";
	
	static String staticEmailId;
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		getOwner(this);
        
        initGui();
        
        //Parse
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectId  = (String) b.get("parseDataObjectId");
        }
       
        Log.d("parseId_main", parseDataObjectId);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);				// fix audio output volume
        maxVolume = (int)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        
        audioVibManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        
        initRingerMode = audioManager.getRingerMode();
        
        if(initRingerMode == AudioManager.RINGER_MODE_VIBRATE){
        	audioManager.setRingerMode(0);
        }
        else{	
	        initialRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
	        initialMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	        initialDtmfVolume = audioManager.getStreamVolume(AudioManager.STREAM_DTMF);
	        initialAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
	        initialNoteVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
	        initialSystemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        }
        initialVibNote = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
        initialVibRing = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
//        initialVibNote = audioManager.getVibrateSetting(AudioManager.VIBRATE_SETTING_OFF);
//        initialVibRing = audioManager.getVibrateSetting(AudioManager.VIBRATE_SETTING_OFF);

        //Parse
    }

	
	private void initGui() {
//		webViewButton = (Button) findViewById(R.id.webViewMainBtn);
//		webViewButton.setOnClickListener(this);
		
		newTestButton = (ImageButton) findViewById(R.id.btnNewTest);
		newTestButton.setOnTouchListener(this);
		archiveButton = (ImageButton) findViewById(R.id.btnArchive);
		archiveButton.setOnTouchListener(this);
		
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {

		case R.id.btnNewTest:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				newTestButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				newTestButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent opA = new Intent(MainActivity.this, OptionsActivity.class);
				opA.putExtra("parseDataObjectId", parseDataObjectId);
				startActivity(opA);		
			}
		break;
		case R.id.btnArchive:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				archiveButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				archiveButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent archA = new Intent(MainActivity.this, ArchiveActivity.class);
				archA.putExtra("parseDataObjectId", parseDataObjectId);
				startActivity(archA);	
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
		else
			return true;
	}
	    

	// Get user details
	private String getName(Context context) {
        Cursor CR=null;
        CR=getOwner(context);
        String id="",name="";
        while (CR.moveToNext()) {
            name = CR
                    .getString(CR
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        return name;
    }
	
    static String getEmailId(Context context) {

        Cursor CR=null;
        CR=getOwner(context);
        String id="",email="";
        while (CR.moveToNext()) {
            id = CR.getString(CR
                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
            email = CR
                    .getString(CR
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        return email;
    }
	
	static Cursor getOwner(Context context) {

        String accountName = null;
        Cursor emailCur=null;
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts[0].name != null) {
            accountName = accounts[0].name;
            String where = ContactsContract.CommonDataKinds.Email.DATA + " = ?";
            ArrayList<String> what = new ArrayList<String>();
            what.add(accountName);
            staticEmailId = accountName;
            Log.v("Got account", "Account " + accountName);
            for (int i = 1; i < accounts.length; i++) {
                where += " or " + ContactsContract.CommonDataKinds.Email.DATA + " = ?";
                what.add(accounts[i].name);
                Log.v("Got account", "Account " + accounts[i].name);
            }
            String[] whatarr = (String[]) what.toArray(new String[what.size()]);
            ContentResolver cr = context.getContentResolver();
            emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    where,
                    whatarr, null);
        }
        return emailCur;
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
	    
        if(initialRingVolume == 0){
        	audioManager.setRingerMode(0);
        }
        else{
	    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initialMusicVolume, 0);		
		audioManager.setStreamVolume(AudioManager.STREAM_RING, initialRingVolume, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_DTMF, initialDtmfVolume, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_ALARM, initialAlarmVolume, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, initialNoteVolume, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, initialSystemVolume, 0);
		
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, initialVibNote);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, initialVibRing);
        }
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
	    super.onResume();
	    Log.d("onResume","onResume");
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
		audioManager.setRingerMode(0);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
		
		Log.d("musicVolume", Integer.toString(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	    Log.d("onDestroy","onDestroy");
    }
	
	
	
}
