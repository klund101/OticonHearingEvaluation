package com.example.hearing_evaluation;

import java.io.File;
import java.util.ArrayList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.PointD;
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
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class ResultActivity extends IdentityActivity implements OnTouchListener {
	
	ImageButton closeResultsButton;
	ImageButton sendEmailButton;
	ImageButton interpretationButton;
	EditText uEmail;
	
	private LineChart mChart;
	public String createdAt;
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
	
	public String chartJpgName;
	
    public ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
    
    public RelativeLayout resultRelativeLayout;
    
    public float[] displayValueX = new float[7];
    public float[] displayValueY = new float[7];

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_result);
		
        initGui();
        
        uEmail = (EditText)findViewById(R.id.userEmail);
        uEmail.clearComposingText();
           
		Log.d("user email", MainActivity.staticEmailId);
		uEmail.setText(MainActivity.staticEmailId);	  
        
        
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
	l_mild = new LimitLine(-28.8f, "");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(12f);
	leftAxis.addLimitLine(l_mild);
	l_mild = new LimitLine(-34.7f, "Mild Hearing Loss");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(12f);
	l_mild.setTextColor(Color.BLACK);
	l_mild.setTextSize(10f);
	leftAxis.addLimitLine(l_mild);
	l_mild = new LimitLine(-38.7f, "");
	l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
	l_mild.setLineWidth(4.0f);
	leftAxis.addLimitLine(l_mild);
	//
	float adjustLine = 0.11f;
	
	LimitLine l_mod = new LimitLine(-43, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-48.9f, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-54.9f+(adjustLine), "Moderate Hearing Loss");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	l_mod.setTextColor(Color.BLACK);
	l_mod.setTextSize(10f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-60.78f+(adjustLine), "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-67.0f+(adjustLine*4), "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(12f);
	leftAxis.addLimitLine(l_mod);
	l_mod = new LimitLine(-70.0f, "");
	l_mod.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
	l_mod.setLineWidth(1.95f);
	leftAxis.addLimitLine(l_mod);
	//
	LimitLine l_sev = new LimitLine(-73.5f, "");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(12f);
	leftAxis.addLimitLine(l_sev);
	l_sev = new LimitLine(-79.5f+(adjustLine*1), "");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(12f);
	leftAxis.addLimitLine(l_sev);
	l_sev = new LimitLine(-85.51f+(adjustLine*2), "Severe Hearing Loss");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(12f);
	l_sev.setTextColor(Color.BLACK);
	l_sev.setTextSize(10f);
	leftAxis.addLimitLine(l_sev);
	l_sev = new LimitLine(-89.35f+(adjustLine*3), "");
	l_sev.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
	l_sev.setLineWidth(3.1f);
	leftAxis.addLimitLine(l_sev);
	//
	LimitLine l_prof = new LimitLine(-92.75f, "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-98.73f+(adjustLine*1), "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-104.6f+(adjustLine*1), "Profound Hearing Loss");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	l_prof.setTextColor(Color.BLACK);
	l_prof.setTextSize(10f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-110.56f+(adjustLine*2), "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-116.52f+(adjustLine*3), "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(12f);
	leftAxis.addLimitLine(l_prof);
	l_prof = new LimitLine(-120.0f+(adjustLine*5), "");
	l_prof.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
	l_prof.setLineWidth(1.3f);
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
	ParseQuery<ParseObject> queryRight = ParseQuery.getQuery("hearingEvaluationData");
	queryRight.whereEqualTo("GoogleId", MainActivity.staticEmailId);
	queryRight.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
		@Override
		public void done(ParseObject object, ParseException e) {
			setData(RepeatTask.freqValues.length,1,"HearingDataRight", "Right ear");
		}
	});
	
	//Parse
	ParseQuery<ParseObject> queryLeft = ParseQuery.getQuery("hearingEvaluationData");
	queryLeft.whereEqualTo("GoogleId", MainActivity.staticEmailId);
	queryLeft.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
		@Override
		public void done(ParseObject object, ParseException e) {
			setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Left ear");
				if(displayValueX[0] == 0.0f){
					
					setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Left ear");
				}
		}
	});
		
	//drawAudiogramCrosses();
	
	}
	
    //////////MPchart
    private void setData(int count, float range, final String dataChannel, final String channelLabel) {
    	
    	
		//Parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
			dBValuesString = (String) object.get(dataChannel);
			readUserName = (String) object.get("Username");
			//readInvertedEars = (String) object.get("invertedEarPhones");
			createdAt = (String) object.get("timeAndDate");
			//Log.d("dBValuesString", dBValuesString);
		    dBValuesStringSubS = dBValuesString.substring(1);
		    dBValuesStringSubS = dBValuesStringSubS.replace(']', ' ');
		    dBValuesStringSubS = dBValuesStringSubS.trim();
		    
	        ArrayList<Entry> yVals = new ArrayList<Entry>();
	        ArrayList<String> xVals = new ArrayList<String>();
		    
		    String[] dBValuesReader = dBValuesStringSubS.split(",");
		        for(int i=0; i<dBValues.length; i++){
		        	dBValues[i] = Float.parseFloat(dBValuesReader[i]);
		        	//Log.d("dBValues", Float.toString(dBValues[i]));
		            yVals.add(new Entry(dBValues[i], i));
		            //Log.d("yVals", yVals.toString());
		            
		            Log.d("ear", channelLabel);
		            if(channelLabel.equals("Left ear")){
		            
			    		PointF posOfPoint = mChart.getPosition(new Entry(dBValues[i], i), AxisDependency.LEFT);
			    		String audiogramCrossPos = posOfPoint.toString();
			    		audiogramCrossPos = audiogramCrossPos.replace("PointF(", "");
			    		audiogramCrossPos = audiogramCrossPos.replace(" ", "");
			    		audiogramCrossPos = audiogramCrossPos.replace(")", "");
			    		String[] audiogramCrossReader = audiogramCrossPos.split(",");
	
			    		displayValueX[i] = Float.parseFloat(audiogramCrossReader[0]);
			    		displayValueY[i] = Float.parseFloat(audiogramCrossReader[1]);
			    		
			    		Log.d("audiogramCrossReader" + Integer.toString(i), "X: " + audiogramCrossReader[0] + ", Y: " +  audiogramCrossReader[1]);
		            }
		        }
 
		        
		        if(dataChannel == "HearingDataLeft"){
				    if(displayValueX[0] != 0.0){
		        	
	//		        	if(readInvertedEars == "true")
	//		        		dBValuesRight = dBValues.clone();
	//		        	else
			        	dBValuesLeft = dBValues.clone();
			        	
			        	//Log.d("dBValuesLeft", Float.toString(dBValuesLeft[1]));
			        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
				        set1.enableDashedLine(10f, 5f, 0f);
				        set1.setColor(Color.BLUE);
					    set1.setCircleColor(Color.TRANSPARENT);
					    
	
				        //mChart.getLegend().setEnabled(true);
			        
				    	drawAudiogramCrosses();
				        //mChart.getLegend().setEnabled(false);

				    
				    
			        set1.setLineWidth(1f);
			        set1.setCircleSize(3f);
			        set1.setDrawCircleHole(false);
			        set1.setValueTextSize(9f);
			        set1.setDrawValues(false);
			        set1.setFillAlpha(65);
			        set1.setFillColor(Color.BLACK);
			        dataSets.add(set1); // add the datasets
			        
			        for (int i = 0; i < RepeatTask.freqValues.length; i++) {
			            xVals.add(Integer.toString(RepeatTask.freqValues[i]) + " Hz");
			            //Log.d("xval", String.valueOf(xVals));
			        }
	
			        // create a data object with the datasets
			        LineData data = new LineData(xVals, dataSets);
			        
			        // set data
			        
			        mChart.setData(data);
			    	mChart.setDescription(readUserName + ", " + createdAt); // set user name on audiogram
			    	mChart.invalidate();
				}
		        }
		        else if(dataChannel == "HearingDataRight"){
		        	
//		        	if(readInvertedEars == "true")
//		        		dBValuesLeft = dBValues.clone();
//		        	else
		        		dBValuesRight = dBValues.clone();
		        	
		        	//Log.d("dBValuesRight", Float.toString(dBValuesRight[1]));
		        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
			        set1.enableDashedLine(10f, 5f, 0f);
			        set1.setColor(Color.RED);
				    set1.setCircleColor(Color.RED);
			        set1.setLineWidth(1f);
			        set1.setCircleSize(4f);
			        set1.setDrawCircleHole(true);
			        set1.setValueTextSize(9f);
			        set1.setDrawValues(false);
			        set1.setFillAlpha(65);
			        set1.setFillColor(Color.BLACK);
			        dataSets.add(set1); // add the datasets
			        
			        for (int i = 0; i < RepeatTask.freqValues.length; i++) {
			            xVals.add(Integer.toString(RepeatTask.freqValues[i]) + " Hz");
			            //Log.d("xval", String.valueOf(xVals));
			        }
	
			        // create a data object with the datasets
			        LineData data = new LineData(xVals, dataSets);
			        
			        // set data
			        
			        mChart.setData(data);
			    	mChart.setDescription(readUserName + ", " + createdAt); // set user name on audiogram
			    	mChart.invalidate();
		        }
		         	
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
			    
				    for(int i=0; i<dBValuesLeft.length; i++){
				    	freqsAndDataLeft += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Integer.toString(-(int)dBValuesLeft[i]) + "dB]; ";
				    	Log.d("DATA_LEFT",Float.toString(dBValuesLeft[i]));
				    	freqsAndDataRight += "[" + Integer.toString(RepeatTask.freqValues[i]) + "Hz, " + Integer.toString(-(int)dBValuesRight[i]) + "dB]; ";
				    	Log.d("DATA_RIGHT",Float.toString(dBValuesRight[i]));
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
	
	void drawAudiogramCrosses(){
		
		//Draw blue crosses
        int adjustCrossYPos = 40;
        
		ImageView audiogramCrossImage1 = (ImageView) findViewById(R.id.audiogramCross1);	
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[0]-18,(int)displayValueY[0]+adjustCrossYPos, 0,0);
		audiogramCrossImage1.setLayoutParams(params);
		
		ImageView audiogramCrossImage2 = (ImageView) findViewById(R.id.audiogramCross2);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[1]-18,(int)displayValueY[1]+adjustCrossYPos, 0,0);
		audiogramCrossImage2.setLayoutParams(params);
		
		ImageView audiogramCrossImage3 = (ImageView) findViewById(R.id.audiogramCross3);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[2]-18,(int)displayValueY[2]+adjustCrossYPos, 0,0);
		audiogramCrossImage3.setLayoutParams(params);
		
		ImageView audiogramCrossImage4 = (ImageView) findViewById(R.id.audiogramCross4);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[3]-18,(int)displayValueY[3]+adjustCrossYPos, 0,0);
		audiogramCrossImage4.setLayoutParams(params);
		
		ImageView audiogramCrossImage5 = (ImageView) findViewById(R.id.audiogramCross5);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[4]-18,(int)displayValueY[4]+adjustCrossYPos, 0,0);
		audiogramCrossImage5.setLayoutParams(params);
		
		ImageView audiogramCrossImage6 = (ImageView) findViewById(R.id.audiogramCross6);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[5]-18,(int)displayValueY[5]+adjustCrossYPos, 0,0);
		audiogramCrossImage6.setLayoutParams(params);
		
		ImageView audiogramCrossImage7 = (ImageView) findViewById(R.id.audiogramCross7);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[6]-18,(int)displayValueY[6]+adjustCrossYPos, 0,0);
		audiogramCrossImage7.setLayoutParams(params);
	}

}
