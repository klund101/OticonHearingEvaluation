package com.example.hearing_evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.puredata.core.PdBase;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class IdentityActivity extends Activity implements OnClickListener {
	
	Button newStartTestButton;
	EditText uName;
	EditText uAge;
	RadioButton radioButtonF;
	RadioButton radioButtonM;
	RadioGroup radioGenderGroup;
	
	public String uNameString = "";
	public String uAgeString = "";
	public String uGenderString = "";
	public String date;
	
	public String dataFile = "OticonAppData.txt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identity);
		
		initGui();
		
		uAge = (EditText)findViewById(R.id.userAge);
		uName = (EditText)findViewById(R.id.userName);
		uName.clearComposingText();
		Log.d("username", getName(this));
		uName.setText(getName(this));
		
	}       
	
	private void initGui() {
		newStartTestButton = (Button) findViewById(R.id.btnStartTest);
		newStartTestButton.setOnClickListener(this);
		
		radioGenderGroup = (RadioGroup) findViewById(R.id.genderGroup);
		
		radioButtonF = (RadioButton) findViewById(R.id.genderFemale);
		radioButtonF.setOnClickListener(this);
		radioButtonM = (RadioButton) findViewById(R.id.genderMale);
		radioButtonM.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStartTest:
				uNameString = uName.getText().toString();
				uAgeString = uAge.getText().toString();

				 boolean isInt = true;

				 try { 
				      Integer.parseInt(uAgeString);
				 } catch (NumberFormatException e) {
				     isInt = false;
				 }
			
		if(uNameString.length() >= 1 && uGenderString != "" && isInt){	
				//Parse
		        
				String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
				ParseObject userDataObject = new ParseObject("hearingEvaluationData");
		        userDataObject.put("Username", uNameString);
		        userDataObject.put("DeviceId", androidId);
		        userDataObject.put("HearingData", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
		        userDataObject.put("Age", uAgeString);
		        userDataObject.put("Gender", uGenderString);
		        Log.d("uGenderString", uGenderString);
		        
		        try {
					userDataObject.save();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
							
				Intent testA = new Intent(IdentityActivity.this, TestActivity.class);
				PdBase.sendBang("playTestTone");
				testA.putExtra("parseDataObjectId", userDataObject.getObjectId());
				Log.d("parseDataObjectId idAct", userDataObject.getObjectId());
				startActivity(testA);
		}
		break;
		case R.id.genderFemale:
        	uGenderString = "F";
        	Log.d("uGenderString", uGenderString);
		break;
		case R.id.genderMale:
        	uGenderString = "M";
        	Log.d("uGenderString", uGenderString);
		break;
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
	    		|| keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	    	return true;
	    else if (keyCode == KeyEvent.KEYCODE_BACK){
	    	super.onBackPressed();
	    	return true;
	    }
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
