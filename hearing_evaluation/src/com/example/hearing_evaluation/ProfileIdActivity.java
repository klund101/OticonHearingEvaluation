package com.example.hearing_evaluation;

import java.util.ArrayList;
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
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ProfileIdActivity extends Activity implements OnItemSelectedListener, OnClickListener {

	Spinner spinnerProfileSpinner;
	Button newUserButton;
	Button startTestProfileActButton;
	Button deleteProfileButton;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_id);
		
		initGui();
		
		updateProfileSpinner();
	}

	private void initGui() {
		spinnerProfileSpinner = (Spinner) findViewById(R.id.spinnerProfile);
		spinnerProfileSpinner.setOnItemSelectedListener(this);
		
		newUserButton = (Button) findViewById(R.id.btnNewUser);
		newUserButton.setOnClickListener(this);
		
		startTestProfileActButton = (Button) findViewById(R.id.btnStartTestProfileAct);
		startTestProfileActButton.setOnClickListener(this);
		
		deleteProfileButton= (Button) findViewById(R.id.btnDeleteProfile);
		deleteProfileButton.setOnClickListener(this);
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, final int position,
			long id) {
		//Parse
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewUser:
			startActivity(new Intent(ProfileIdActivity.this, IdentityActivity.class)); //
		break;
		case R.id.btnStartTestProfileAct:

			//Parse
				userDataObject = new ParseObject("hearingEvaluationData");
		        userDataObject.put("Username", profileNameSpinner);
		        userDataObject.put("GoogleId", getEmailId(this));
		        userDataObject.put("HearingDataLeft", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
		        userDataObject.put("HearingDataRight", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
		        userDataObject.put("Age", profileAgeSpinner);
		        userDataObject.put("Gender", profileGenderSpinner);
		        userDataObject.put("invertedEarPhones", "false");

		        if(newProfileObject != null)
		        	newProfileObject.deleteInBackground();
		        
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
		break;
		case R.id.btnDeleteProfile:
			
			//Parse
			
			AlertDialog alertDialog = new AlertDialog.Builder(ProfileIdActivity.this)
	        .setTitle("Delete profile")
	        .setMessage("Are you sure you want to delete this profile?")
	        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) { 
	            	
	            	
	    			ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
	    			query.findInBackground(new FindCallback<ParseObject>() {	        
	    				@Override
	    				public void done(List<ParseObject> objects, ParseException e) {
	    					for(int i = objects.size()-1; i>=0; i--){
	    						if(objects.get(i).get("Username").toString().equals(profileNameSpinner)){
	    							objects.get(i).deleteInBackground();
									objects.get(i).saveInBackground();
									Log.d("Delete Object", Integer.toString(i));
									Intent loadAct =new Intent(ProfileIdActivity.this, LoadingActivity.class);
									startActivity(loadAct);
	    								    							
	    						}
	    														
	    					}
	    					
	    				}
	    			});
	            	
	    				
	            }
	         })
	        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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

	        finish();    	
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
	
	public void updateProfileSpinner(){
		//Parse
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", getEmailId(this));
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

}