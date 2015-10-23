package com.example.hearing_evaluation;

import java.util.ArrayList;
import com.parse.ParseException;
import com.parse.ParseObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class IdentityActivity extends Activity implements OnClickListener {
	
	Button saveProfileButton;
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
		saveProfileButton = (Button) findViewById(R.id.btnSaveProfile);
		saveProfileButton.setOnClickListener(this);
		
		radioGenderGroup = (RadioGroup) findViewById(R.id.genderGroup);
		
		radioButtonF = (RadioButton) findViewById(R.id.genderFemale);
		radioButtonF.setOnClickListener(this);
		radioButtonM = (RadioButton) findViewById(R.id.genderMale);
		radioButtonM.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSaveProfile:
			
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
				ProfileIdActivity.newProfileObject.put("GoogleId", getEmailId(this));
				ProfileIdActivity.newProfileObject.put("HearingDataLeft", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
				ProfileIdActivity.newProfileObject.put("HearingDataRight", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
				ProfileIdActivity.newProfileObject.put("Age", profileAge);
				ProfileIdActivity.newProfileObject.put("Gender", profileGender);
				ProfileIdActivity.newProfileObject.put("invertedEarPhones", "false");
				
				try {
					ProfileIdActivity.newProfileObject.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent saveP = new Intent(IdentityActivity.this, ProfileIdActivity.class);
				startActivity(saveP);
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
