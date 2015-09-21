package com.example.hearing_evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.puredata.core.PdBase;

import android.support.v7.app.ActionBarActivity;
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

public class IdentityActivity extends Activity implements OnClickListener {
	
	Button newStartTestButton;
	EditText uName;
	public String uNameString;
	
	public String dataFile = "OticonAppData.txt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identity);
		
		initGui();
		
		uName   = (EditText)findViewById(R.id.userName);
		uName.clearComposingText();
		Log.d("username", getName(this));
		uName.setText(getName(this));	

	}       
	
	private void initGui() {
		newStartTestButton = (Button) findViewById(R.id.btnStartTest);
		newStartTestButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStartTest:
			uNameString = uName.getText().toString();
			
            //get the path to sdcard 
            File pathToExternalStorage = Environment.getExternalStorageDirectory();
            File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/documents/Oticon");
            // have the object build the directory structure, if needed.
            appDirectory.mkdirs();
            
            //Create a File for the output file data
            File saveFilePath = new File (appDirectory, dataFile);

            try{
                String newline = "\r\n";
                FileOutputStream fos = new FileOutputStream (saveFilePath, true);
                OutputStreamWriter OutDataWriter  = new OutputStreamWriter(fos);
                OutDataWriter.append(uNameString + newline);
                // OutDataWriter.append(equipNo.getText() + newline);
                OutDataWriter.close();
                fos.flush();
                fos.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        
			
			Intent testA = new Intent(IdentityActivity.this, TestActivity.class);
			testA.putExtra("name", uNameString); // pass username to TestActivity
			PdBase.sendBang("playTestTone");
			startActivity(testA);
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
