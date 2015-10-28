package com.example.hearing_evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.parse.Parse;
import com.parse.ParseObject;

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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnTouchListener {
	
	Button webViewButton;
	Button pureToneTestButton;
	
	ImageButton newTestButton;
	ImageButton archiveButton;
	
	public int initialRingVolume;	
	
	public String parseDataObjectId = "";
	
	static String staticEmailId;
	
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
        
        
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE); // fix audio output volume
        
        initialRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);		
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);		

        //Parse
    }

	
	private void initGui() {
//		webViewButton = (Button) findViewById(R.id.webViewMainBtn);
//		webViewButton.setOnClickListener(this);
		
		newTestButton = (ImageButton) findViewById(R.id.btnNewTest);
		newTestButton.setOnTouchListener(this);
		archiveButton = (ImageButton) findViewById(R.id.btnArchive);
		archiveButton.setOnTouchListener(this);
		
		pureToneTestButton = (Button) findViewById(R.id.btnPureToneTestActivity);
		pureToneTestButton.setOnTouchListener(this);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
//		case R.id.webViewMainBtn:
//			Intent webViewA = new Intent(MainActivity.this, WebViewResultActivity.class);
//	        startActivity(webViewA);
//		break;
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
		case R.id.btnPureToneTestActivity:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){

			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){

				Intent pureToneA = new Intent(MainActivity.this, PureToneDbTestActivity.class);
				startActivity(pureToneA);	
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
	    
	
    protected void onResume() {
    super.onResume();
	
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
	
    

}
