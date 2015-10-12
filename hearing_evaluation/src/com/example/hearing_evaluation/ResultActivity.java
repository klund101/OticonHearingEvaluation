package com.example.hearing_evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.puredata.core.PdBase;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis; 
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.support.v7.app.ActionBarActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

public class ResultActivity extends IdentityActivity implements OnClickListener {
	
	Button closeResultsButton;
	Button sendEmailButton;
	EditText uEmail;
	
	private LineChart mChart;
	public Date createdAt;
	public String readUserName;
	//ArrayList<String> lineList = new ArrayList<String>();
	float[] dBValues = new float[RepeatTask.freqValues.length];
	float[] dBValuesLeft = new float[RepeatTask.freqValues.length];
	float[] dBValuesRight = new float[RepeatTask.freqValues.length];
	public String dBValuesString;
	public String dBValuesStringSubS;
	public String freqsAndDataLeft = "";
	public String freqsAndDataRight = "";
	public String parseDataObjectId;
	public String pressedObjectId;
	
    public ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
        initGui();
        
        uEmail = (EditText)findViewById(R.id.userEmail);
        uEmail.clearComposingText();
           
		Log.d("user email", getEmailId(this));
		uEmail.setText(getEmailId(this));	  
        
        
//////////MPchart
    	
	//LineChart chart = (LineChart) findViewById(R.id.chart);
	mChart = (LineChart) findViewById(R.id.chart);
	// enable value highlighting
	mChart.setHighlightEnabled(true);

	// enable touch gestures
	mChart.setTouchEnabled(false);

	// enable scaling and dragging
	mChart.setDragEnabled(true);
	mChart.setScaleEnabled(true);

 	mChart.setPinchZoom(true);
 
 	YAxis leftAxis = mChart.getAxisLeft();
 	YAxis rightAxis = mChart.getAxisRight();
 
 	leftAxis.setAxisMaxValue(10f);
 	rightAxis.setAxisMaxValue(10f);
 	leftAxis.setStartAtZero(false);
 	rightAxis.setStartAtZero(false);
 	leftAxis.setAxisMinValue(-120f);
 	rightAxis.setAxisMinValue(-120f);
 
	// XAxis xAxis = mChart.getXAxis();
 
 	// xAxis.setAxisMaxValue(120f);
	// xAxis.setAxisMinValue(0f);
	//	
	
	mChart.setVisibleXRangeMinimum(0);
	mChart.setVisibleXRangeMaximum(8000);
//	mChart.setVisibleYRangeMaximum(0f,leftAxis);
//	mChart.setVisibleYRangeMaximum(8000);
	
    //ArrayList<Entry> yVals = new ArrayList<Entry>();
//	LineDataSet set1 = new LineDataSet(yVals, "");
//	LineDataSet set2 =  new LineDataSet(yVals, "");
	

	setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Left ear");
	//dBValuesLeft = dBValues;
	setData(RepeatTask.freqValues.length,1,"HearingDataRight", "Right ear");
	//dBValuesRight = dBValues;
	//////////	
		
	}
	
    //////////MPchart
    private void setData(int count, float range, final String dataChannel, final String channelLabel) {
    	
      	parseDataObjectId = null;
    	pressedObjectId = null;
    	
        //Parse
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectId  = (String) b.get("parseDataObjectId");
        	pressedObjectId = (String) b.get("pressedObjectId");
        }
       
        //Log.d("d_result", parseDataObjectId);
        
        if(pressedObjectId != null){
        	parseDataObjectId = pressedObjectId;
        }
        
		//Parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
			dBValuesString = (String) object.get(dataChannel);
			readUserName = (String) object.get("Username");
			createdAt = object.getCreatedAt();
			//Log.d("dBValuesString", dBValuesString);
		    dBValuesStringSubS = dBValuesString.substring(1);
		    dBValuesStringSubS = dBValuesStringSubS.replace(']', ' ');
		    dBValuesStringSubS = dBValuesStringSubS.trim();
		    
	        ArrayList<Entry> yVals = new ArrayList<Entry>();
	        ArrayList<String> xVals = new ArrayList<String>();
		    
		    String[] dBValuesReader = dBValuesStringSubS.split(",");
		        for(int i=0; i<dBValues.length; i++){
		        	dBValues[i] = Float.parseFloat(dBValuesReader[i]);
		        	Log.d("dBValues", Float.toString(dBValues[i]));
		            yVals.add(new Entry(dBValues[i], i));
		            Log.d("yVals", Float.toString(dBValues[i]));
		        }
		        
		        if(dataChannel == "HearingDataLeft"){
		        	dBValuesLeft = dBValues.clone();;
		        	Log.d("dBValuesLeft", Float.toString(dBValuesLeft[0]));
		        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
			        set1.enableDashedLine(10f, 5f, 0f);
			        set1.setColor(Color.RED);
			        set1.setCircleColor(Color.RED);		        
			        set1.setLineWidth(1f);
			        set1.setCircleSize(3f);
			        set1.setDrawCircleHole(false);
			        set1.setValueTextSize(9f);
			        set1.setDrawValues(false);
			        set1.setFillAlpha(65);
			        set1.setFillColor(Color.BLACK);
			        dataSets.add(set1); // add the datasets
		        }
		        else if(dataChannel == "HearingDataRight"){
		        	dBValuesRight = dBValues.clone();
		        	Log.d("dBValuesRight", Float.toString(dBValuesRight[0]));
		        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
			        set1.enableDashedLine(10f, 5f, 0f);
			        set1.setColor(Color.BLUE);
			        set1.setCircleColor(Color.BLUE);		        
			        set1.setLineWidth(1f);
			        set1.setCircleSize(3f);
			        set1.setDrawCircleHole(false);
			        set1.setValueTextSize(9f);
			        set1.setDrawValues(false);
			        set1.setFillAlpha(65);
			        set1.setFillColor(Color.BLACK);
			        dataSets.add(set1); // add the datasets
		        }

		        for (int i = 0; i < RepeatTask.freqValues.length; i++) {
		            xVals.add(Integer.toString(RepeatTask.freqValues[i]));
		            Log.d("xval", String.valueOf(xVals));
		        }

		        // create a data object with the datasets
		        LineData data = new LineData(xVals, dataSets);
		        
		        // set data
		        mChart.setData(data);
		    	mChart.setDescription(readUserName + ", " + createdAt.toString()); // set user name on audiogram
		    	mChart.invalidate();
		    	
			}
		});
    	
        
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
	    	if(pressedObjectId != null){
	    		super.onBackPressed();
	    	}
	    	return true;
	    }
		else
			return true;
	}
	
	private void initGui() {
		closeResultsButton = (Button) findViewById(R.id.btnCloseResults);
		closeResultsButton.setOnClickListener(this);
		sendEmailButton = (Button) findViewById(R.id.btnSendEmail);
		sendEmailButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCloseResults:
			//startActivity(new Intent(ResultActivity.this, MainActivity.class)); // 
	    	if(pressedObjectId != null){
	    		super.onBackPressed();
	    	}
	    	else{
			Intent mainA = new Intent(ResultActivity.this, MainActivity.class);
			mainA.putExtra("parseDataObjectId", parseDataObjectId);
	        startActivity(mainA);
	    	}
		break;
		case R.id.btnSendEmail:
		    Intent emailIntent = new Intent(Intent.ACTION_SEND);
		    emailIntent.setType("message/rfc822");
		    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{uEmail.getText().toString()});
		    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oticon Mobile Hearing Evaluation");
		    
		    
		    for(int i=0; i<dBValuesLeft.length; i++){
		    	freqsAndDataLeft += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Float.toString(dBValuesLeft[i]) + "dB]; ";
		    	Log.d("DATA_LEFT",Float.toString(dBValuesLeft[i]));
		    	freqsAndDataRight += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Float.toString(dBValuesRight[i]) + "dB]; ";
		    	Log.d("DATA_RIGHT",Float.toString(dBValuesRight[i]));
		    }
		    
		    
		    emailIntent.putExtra(Intent.EXTRA_TEXT   , "Oticon Mobile Hearing Evaluation data for " + readUserName + ", " + createdAt.toString() + ":" + "\n\n" + "Left ear" + "\n" + freqsAndDataLeft + "\n\n" + "Right ear" + "\n" + freqsAndDataRight);
		    freqsAndDataLeft = "";
		    freqsAndDataRight = "";
		    try {
			    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			    Log.i("Finished sending email...", "");
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(ResultActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		break;
		}
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
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, where, whatarr, null);
        }
        //return emailCur;
        return emailCur;
    }

}
