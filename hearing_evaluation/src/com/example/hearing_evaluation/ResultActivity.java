package com.example.hearing_evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ResultActivity extends IdentityActivity implements OnTouchListener {
	
	ImageButton closeResultsButton;
	ImageButton sendEmailButton;
	ImageButton interpretationButton;
	EditText uEmail;
	
	private LineChart mChart;
	public Date createdAt;
	public String readUserName;
	public static String readInvertedEars;
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
	
	public String chartJpgName;
	
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
 	
 	rightAxis.setEnabled(false);
 
 	leftAxis.setAxisMaxValue(10f);
 	//rightAxis.setAxisMaxValue(10f);
 	leftAxis.setStartAtZero(false);
 	//rightAxis.setStartAtZero(false);
 	leftAxis.setAxisMinValue(-120f);
 	//rightAxis.setAxisMinValue(-120f);
 	
 	leftAxis.setValueFormatter(new YvalueCustomFormatter());
 	//rightAxis.setValueFormatter(new YvalueCustomFormatter());
 	
	XAxis xAxis = mChart.getXAxis(); 
	xAxis.setLabelsToSkip(0);
	//xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
	//	
	
	mChart.setVisibleXRangeMinimum(1.03f);
	//mChart.setVisibleXRangeMaximum(8000);
//	mChart.setVisibleYRangeMaximum(0f,leftAxis);
//	mChart.setVisibleYRangeMaximum(8000);
	
    //ArrayList<Entry> yVals = new ArrayList<Entry>();
//	LineDataSet set1 = new LineDataSet(yVals, "");
//	LineDataSet set2 =  new LineDataSet(yVals, "");
	
	mChart.setDrawGridBackground(false);

	//Hearing Loss Limit Lines
	leftAxis.removeAllLimitLines();

	int LimitLineAlpha = 60;
	
	LimitLine l_mild = new LimitLine(-23, "");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(12f);
	leftAxis.addLimitLine(l_mild);
	l_mild = new LimitLine(-29, "");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(12f);
	leftAxis.addLimitLine(l_mild);
	l_mild = new LimitLine(-35, "Mild Hearing Loss");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(12f);
	l_mild.setTextColor(Color.BLACK);
	l_mild.setTextSize(10f);
	leftAxis.addLimitLine(l_mild);
	l_mild = new LimitLine(-38.9f, "");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(3.4f);
	leftAxis.addLimitLine(l_mild);
	//
	LimitLine l_mod = new LimitLine(-43, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-49.05f, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-55.1f, "Moderate Hearing Loss");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	l_mod.setTextColor(Color.BLACK);
	l_mod.setTextSize(10f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-61.15f, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-67.2f, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-70.20f, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(0.5f);
	leftAxis.addLimitLine(l_mod);
	//
	LimitLine l_sev = new LimitLine(-73.5f, "");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(12f);
	leftAxis.addLimitLine(l_sev);
	l_sev = new LimitLine(-79.55f, "");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(12f);
	leftAxis.addLimitLine(l_sev);
	l_sev = new LimitLine(-85.6f, "Severe Hearing Loss");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(12f);
	l_sev.setTextColor(Color.BLACK);
	l_sev.setTextSize(10f);
	leftAxis.addLimitLine(l_sev);
	l_sev = new LimitLine(-89.15f, "");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(2.1f);
	leftAxis.addLimitLine(l_sev);
	//
	LimitLine l_prof = new LimitLine(-92.75f, "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-98.8f, "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-104.85f, "Profound Hearing Loss");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	l_prof.setTextColor(Color.BLACK);
	l_prof.setTextSize(10f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-110.9f, "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-116.95f, "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	//
	
  	parseDataObjectId = null;
	pressedObjectId = null;
	
    //Parse
    Intent iin = getIntent();
    Bundle b = iin.getExtras();
    if (b != null) {
    	parseDataObjectId  = (String) b.get("parseDataObjectId");
    	pressedObjectId = (String) b.get("pressedObjectId");
    }
    if(pressedObjectId != null){
    	parseDataObjectId = pressedObjectId;
    }
    
    //mChart.animateXY(2000, 2000); // ANIMATION
	
	//Parse
	ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
	query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
		@Override
		public void done(ParseObject object, ParseException e) {
			readInvertedEars = (String) object.get("invertedEarPhones");
			
			Log.d("readInvertedEars", readInvertedEars);
			if(readInvertedEars.equals("false")){
				setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Left ear");
				setData(RepeatTask.freqValues.length,1,"HearingDataRight", "Right ear");
			}
			else if(readInvertedEars.equals("true")){
				setData(RepeatTask.freqValues.length,1,"HearingDataRight", "Left ear");
				setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Right ear");
			}
		}
	});
			
	}
	
    //////////MPchart
    private void setData(int count, float range, final String dataChannel, final String channelLabel) {
    	

       
        //Log.d("d_result", parseDataObjectId);
        
        
		//Parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", getEmailId(this));
		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
			dBValuesString = (String) object.get(dataChannel);
			readUserName = (String) object.get("Username");
			//readInvertedEars = (String) object.get("invertedEarPhones");
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
		        	
//		        	if(readInvertedEars == "true")
//		        		dBValuesRight = dBValues.clone();
//		        	else
		        		dBValuesLeft = dBValues.clone();
		        	
		        	Log.d("dBValuesLeft", Float.toString(dBValuesLeft[1]));
		        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
			        set1.enableDashedLine(10f, 5f, 0f);
			        if(readInvertedEars.equals("true")){
				        set1.setColor(Color.RED);
				        set1.setCircleColor(Color.RED);	
			        }
			        else{
			        	set1.setColor(Color.BLUE);
				        set1.setCircleColor(Color.BLUE);
			        }
			        	
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
		        	
//		        	if(readInvertedEars == "true")
//		        		dBValuesLeft = dBValues.clone();
//		        	else
		        		dBValuesRight = dBValues.clone();
		        	
		        	Log.d("dBValuesRight", Float.toString(dBValuesRight[1]));
		        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
			        set1.enableDashedLine(10f, 5f, 0f);
			        if(readInvertedEars.equals("true")){
				        set1.setColor(Color.BLUE);
				        set1.setCircleColor(Color.BLUE);	
			        }
			        else{
			        	set1.setColor(Color.RED);
				        set1.setCircleColor(Color.RED);
			        }
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
		            xVals.add(Integer.toString(RepeatTask.freqValues[i]) + " Hz");
		            Log.d("xval", String.valueOf(xVals));
		        }

		        // create a data object with the datasets
		        LineData data = new LineData(xVals, dataSets);
		        
		        // set data
		        mChart.setData(data);
		    	mChart.setDescription(readUserName + ", " + createdAt.toString()); // set user name on audiogram
		    	mChart.invalidate();
		    	
		    	chartJpgName = readUserName + "_" + object.getObjectId() + ".jpg";
		    	mChart.saveToGallery(chartJpgName, 100);
		    	
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
		closeResultsButton = (ImageButton) findViewById(R.id.btnCloseResults);
		closeResultsButton.setOnTouchListener(this);
		sendEmailButton = (ImageButton) findViewById(R.id.btnSendEmail);
		sendEmailButton.setOnTouchListener(this);
		interpretationButton= (ImageButton) findViewById(R.id.btnInterpretation);
		interpretationButton.setOnTouchListener(this);
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnCloseResults:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
					closeResultsButton.setColorFilter(Color.argb(100, 0, 0, 0));
				}
				else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
					closeResultsButton.setColorFilter(Color.argb(0, 0, 0, 0));
				//startActivity(new Intent(ResultActivity.this, MainActivity.class)); // 
		    	if(pressedObjectId != null){
		    		super.onBackPressed();
		    	}
		    	else{
				Intent mainA = new Intent(ResultActivity.this, MainActivity.class);
				mainA.putExtra("parseDataObjectId", parseDataObjectId);
		        startActivity(mainA);
		    	}
			}
		break;
		case R.id.btnSendEmail:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				sendEmailButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				sendEmailButton.setColorFilter(Color.argb(0, 0, 0, 0));
			    Intent emailIntent = new Intent(Intent.ACTION_SEND);
			    emailIntent.setType("message/rfc822");
			    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{uEmail.getText().toString()});
			    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oticon Mobile Hearing Evaluation");
			    
			    if(readInvertedEars.equals("false")){
				    for(int i=0; i<dBValuesLeft.length; i++){
				    	freqsAndDataLeft += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Integer.toString(-(int)dBValuesLeft[i]) + "dB]; ";
				    	Log.d("DATA_LEFT",Float.toString(dBValuesLeft[i]));
				    	freqsAndDataRight += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Integer.toString(-(int)dBValuesRight[i]) + "dB]; ";
				    	Log.d("DATA_RIGHT",Float.toString(dBValuesRight[i]));
				    }
			    }
				else if(readInvertedEars.equals("true")){
					for(int i=0; i<dBValuesLeft.length; i++){
				    	freqsAndDataRight += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Float.toString(dBValuesLeft[i]) + "dB]; ";
				    	Log.d("DATA_RIGHT",Float.toString(dBValuesLeft[i]));
				    	freqsAndDataLeft += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Float.toString(dBValuesRight[i]) + "dB]; ";
				    	Log.d("DATA_LEFT",Float.toString(dBValuesRight[i]));
				    }
			    }
			    
			    
			    emailIntent.putExtra(Intent.EXTRA_TEXT   , "Oticon Mobile Hearing Evaluation data for " + readUserName + ", " + createdAt.toString() + ":" + "\n\n" + "Left ear" + "\n" + freqsAndDataLeft + "\n\n" + "Right ear" + "\n" + freqsAndDataRight);
			    freqsAndDataLeft = "";
			    freqsAndDataRight = ""; 
			    
			    emailIntent.setType("image/jpeg");
		        File bitmapFile = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" + 
		    		    chartJpgName);
		        
		        Log.d("", Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" + 
		    		    chartJpgName);
		        
		        Uri myUri = Uri.fromFile(bitmapFile);
		        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
		        
	
			    
			    try {
				    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				    Log.i("Finished sending email...", "");
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(ResultActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		break;
		case R.id.btnInterpretation:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				interpretationButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				interpretationButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent interpA = new Intent(ResultActivity.this, InterpretationActivity.class);
				interpA.putExtra("parseDataObjectId", parseDataObjectId);
	        	startActivity(interpA);
			}
		break;
		}
		return false;
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
