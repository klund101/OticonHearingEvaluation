package com.example.hearing_evaluation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
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

    public float tmpLeftEar;
    public boolean drawLeftTrue = true;
	
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
	
	mChart.setVisibleXRangeMinimum(1.1f);
	//mChart.setVisibleXRangeMaximum(8000);
//	mChart.setVisibleYRangeMaximum(0f,leftAxis);
//	mChart.setVisibleYRangeMaximum(8000);
	
    //ArrayList<Entry> yVals = new ArrayList<Entry>();
//	LineDataSet set1 = new LineDataSet(yVals, "");
//	LineDataSet set2 =  new LineDataSet(yVals, "");
	
	//mChart.setGridBackgroundColor(R.drawable.chartbg);
	//mChart.setBackgroundResource(R.drawable.chartbg);
	mChart.setDrawGridBackground(false);
	

	
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
//				if(displayValueX[0] == 0.0f){
//					
//					setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Left ear");
//				}
		}
	});
	
	
	
	
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
		            //tmpLeftEar = 0.1f;
		            if(channelLabel.equals("Left ear")){
		            	
			    		Log.d("dBValues[i]", Float.toString(dBValues[i]));

		            
			    		PointF posOfPoint = mChart.getPosition(new Entry(dBValues[i], i), AxisDependency.LEFT);
//			    		String audiogramCrossPos = posOfPoint.toString();
//			    		Log.d("audiogramCrossReader",audiogramCrossPos);
//			    		audiogramCrossPos = audiogramCrossPos.replace("PointF(", "");
//			    		audiogramCrossPos = audiogramCrossPos.replace(" ", "");
//			    		audiogramCrossPos = audiogramCrossPos.replace(")", "");
//			    		String[] audiogramCrossReader = audiogramCrossPos.split(",");
//	
//			    		displayValueX[i] = Float.parseFloat(audiogramCrossReader[0]);
//			    		displayValueY[i] = Float.parseFloat(audiogramCrossReader[1]);
			    		
			    		displayValueX[i] = posOfPoint.x;
			    		displayValueY[i] = posOfPoint.y;
//			    		
			    		Log.d("audiogramCrossReader" + Integer.toString(i), "X: " + Float.toString(displayValueX[i]) + ", Y: " +  Float.toString(displayValueY[i]));
		            }
		            tmpLeftEar = displayValueX[0];
		        }
				if(tmpLeftEar == 0.0f){
					setData(RepeatTask.freqValues.length,1,"HearingDataLeft", "Left ear");
				}
 
		        
		        if(dataChannel == "HearingDataLeft"){
				    if(tmpLeftEar != 0.0){
		        	
	//		        	if(readInvertedEars == "true")
	//		        		dBValuesRight = dBValues.clone();
	//		        	else
			        	dBValuesLeft = dBValues.clone();
			        	
			        	//Log.d("dBValuesLeft", Float.toString(dBValuesLeft[1]));
			        	
			        	LineDataSet set1 = new LineDataSet(yVals, channelLabel);
				        set1.enableDashedLine(10f, 5f, 0f);
				        set1.setColor(Color.BLUE);
					    set1.setCircleColor(Color.TRANSPARENT);
					    
						if(tmpLeftEar != 0.0f){
						}
	
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
			        
			        if(drawLeftTrue == true){
			        	dataSets.add(set1); // add the datasets
			        	drawLeftTrue = false;
			        }
			        
			        for (int i = 0; i < RepeatTask.freqValues.length; i++) {
			            xVals.add(Integer.toString(RepeatTask.freqValues[i]) + " Hz");
			            //Log.d("xval", String.valueOf(xVals));
			        }
	
			        // create a data object with the datasets
			        LineData data = new LineData(xVals, dataSets);
			        
			        // set data

			        mChart.setData(data);
			    	mChart.setDescription(readUserName + ", " + createdAt); // set user name on audiogram
			    	mChart.setDescriptionPosition((float)((mChart.getPosition(new Entry(0, 6), AxisDependency.LEFT)).x), (float)((mChart.getPosition(new Entry(-127, 0), AxisDependency.LEFT)).y));
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
			        set1.setCircleSize(5f);
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
			    	mChart.setDescriptionPosition(100, mChart.getBottom());
			    	mChart.invalidate();
		        }
		         	
			    chartJpgName = readUserName + "_" + object.getObjectId() + ".jpg";
//			    mChart.saveToGallery(chartJpgName, 100);
			    
			    int sBar = 0;
			    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
			    if (resourceId > 0)
			    	sBar = getResources().getDimensionPixelSize(resourceId);
			    
			    Bitmap bitmap;
			    View v1 = mChart.getRootView();
			    v1.setDrawingCacheEnabled(true);
			    bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			    v1.setDrawingCacheEnabled(false);
			    
			    Bitmap lastBitmap = null;
			    lastBitmap = Bitmap.createBitmap(bitmap, 0, mChart.getTop(), v1.getWidth(), (interpretationButton.getTop()-mChart.getTop()) + getStatusBarHeight() + getTitleBarHeight());
			    
			    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			    lastBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

			    File f = new File(Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_PICTURES
			                            + File.separator), chartJpgName + ".jpg");
			    try {
					f.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    //write the bytes in file
			    FileOutputStream fo;
				try {
					fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
			    
			    Log.d("Screenshot!","Screenshot!");
			    			
		    	
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
		        File bitmapFile = new File(Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_PICTURES
                        + File.separator), chartJpgName + ".jpg");
		        
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

		int LimitLineAlpha = 20;
		YAxis leftAxis = mChart.getAxisLeft();
		
		LimitLine l_mild = new LimitLine(-5, "");//Normal Hearing
		l_mild.setLineColor(Color.argb(LimitLineAlpha, 37, 213, 33));
		l_mild.setLineWidth(((float)mChart.getHeight())/(14.79f*(getResources().getDisplayMetrics().density/3)));
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-30, "");
		l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 255, 48));
		l_mild.setLineWidth(((float)mChart.getHeight())/(22.29f*(getResources().getDisplayMetrics().density/3))); //41f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-30, "");//Mild Hearing Loss
		l_mild.setLineColor(Color.argb(0, 255, 255, 48));
		l_mild.setLineWidth(0.2f); //41f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-55, "");
		l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 186, 48));
		l_mild.setLineWidth(((float)mChart.getHeight())/(14.79f*(getResources().getDisplayMetrics().density/3))); //62f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-55, "");//Moderate Hearing Loss
		l_mild.setLineColor(Color.argb(0, 255, 186, 48));
		l_mild.setLineWidth(0.2f); //62f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-80, "");
		l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 117, 48));
		l_mild.setLineWidth(((float)mChart.getHeight())/(22.29f*(getResources().getDisplayMetrics().density/3))); //41f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-80, "");//Severe Hearing Loss
		l_mild.setLineColor(Color.argb(0, 255, 117, 48));
		l_mild.setLineWidth(0.2f); //41f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-105, "");
		l_mild.setLineColor(Color.argb(LimitLineAlpha, 255, 48, 48));
		l_mild.setLineWidth(((float)mChart.getHeight())/(14.79f*(getResources().getDisplayMetrics().density/3))); //62f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		l_mild = new LimitLine(-105, "");//Profound Hearing Loss
		l_mild.setLineColor(Color.argb(0, 255, 48, 48));
		l_mild.setLineWidth(0.2f); //62f
		leftAxis.addLimitLine(l_mild);
		l_mild.setTextColor(Color.BLACK);
		l_mild.setTextSize(10f);
		
		//Draw sound icons
		ImageView iconBird = (ImageView) findViewById(R.id.bird);
		LayoutParams birdParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		birdParams.setMargins((int)((mChart.getPosition(new Entry(0, 6), AxisDependency.LEFT)).x)-(int)((float)iconBird.getMeasuredWidth()/2), (int)((mChart.getPosition(new Entry(-25, 0), AxisDependency.LEFT)).y)+(int)((float)iconBird.getMeasuredHeight()/2), 0, 0);
		iconBird.setLayoutParams(birdParams);
		        
		ImageView iconBus = (ImageView) findViewById(R.id.bus);
		LayoutParams busParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		busParams.setMargins((int)((mChart.getPosition(new Entry(0, 0), AxisDependency.LEFT)).x), (int)((mChart.getPosition(new Entry(-100, 0), AxisDependency.LEFT)).y)+(int)((float)iconBus.getMeasuredHeight()/2), 0, 0);
		iconBus.setLayoutParams(busParams);
		        
		ImageView iconDog = (ImageView) findViewById(R.id.dog);
		LayoutParams dogParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		dogParams.setMargins((int)((mChart.getPosition(new Entry(0, 1), AxisDependency.LEFT)).x)-(int)((float)iconDog.getMeasuredWidth()/2), (int)((mChart.getPosition(new Entry(-70, 0), AxisDependency.LEFT)).y)+(int)((float)iconDog.getMeasuredHeight()/2), 0, 0);
		iconDog.setLayoutParams(dogParams);
		        
		ImageView iconPhone = (ImageView) findViewById(R.id.phone);
		LayoutParams phoneParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		phoneParams.setMargins((int)((mChart.getPosition(new Entry(0, 4), AxisDependency.LEFT)).x), (int)((mChart.getPosition(new Entry(-67, 0), AxisDependency.LEFT)).y)+(int)((float)iconPhone.getMeasuredHeight()/2), 0, 0);
		iconPhone.setLayoutParams(phoneParams);
		        
		ImageView iconPlane = (ImageView) findViewById(R.id.plane);
		LayoutParams planeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		planeParams.setMargins((int)((mChart.getPosition(new Entry(0, 5), AxisDependency.LEFT)).x)-(int)((float)iconPlane.getMeasuredWidth()/2), (int)((mChart.getPosition(new Entry(-115, 0), AxisDependency.LEFT)).y)+(int)((float)iconPlane.getMeasuredHeight()/2), 0, 0);
		iconPlane.setLayoutParams(planeParams);
		        
		ImageView iconTree = (ImageView) findViewById(R.id.tree);
		LayoutParams treeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		treeParams.setMargins((int)((mChart.getPosition(new Entry(0, 3), AxisDependency.LEFT)).x), (int)((mChart.getPosition(new Entry(-10, 0), AxisDependency.LEFT)).y)+(int)((float)iconTree.getMeasuredHeight()/2), 0, 0);
		iconTree.setLayoutParams(treeParams);
		        
		ImageView iconWatertap = (ImageView) findViewById(R.id.watertap);
		LayoutParams watertapParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		watertapParams.setMargins((int)((mChart.getPosition(new Entry(0, 1), AxisDependency.LEFT)).x)-(int)(float)iconWatertap.getMeasuredWidth(), (int)((mChart.getPosition(new Entry(-20, 0), AxisDependency.LEFT)).y)+(int)((float)iconWatertap.getMeasuredHeight()/2), 0, 0);
		iconWatertap.setLayoutParams(watertapParams);
		
		//Draw speech banana
        float dens = getResources().getDisplayMetrics().density;
        Log.d("dens",Float.toString(dens));
        
        ImageView audiogramBanana = (ImageView) findViewById(R.id.audiogrambanana);
        LayoutParams aBParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        aBParams.setMargins((int)((mChart.getPosition(new Entry(0, 0), AxisDependency.LEFT)).x), (int)((mChart.getPosition(new Entry(-7, 0), AxisDependency.LEFT)).y), 0, 0);
        Log.d("topMargin", Integer.toString((int)((mChart.getPosition(new Entry(0, 0), AxisDependency.LEFT)).y)));
        audiogramBanana.setLayoutParams(aBParams);
        audiogramBanana.getLayoutParams().width = (int)((((mChart.getPosition(new Entry(0, 7), AxisDependency.LEFT)).x) - ((mChart.getPosition(new Entry(0, 0), AxisDependency.LEFT)).x)) - (((mChart.getPosition(new Entry(0, 1), AxisDependency.LEFT)).x) - ((mChart.getPosition(new Entry(0, 0), AxisDependency.LEFT)).x)*0.8));
        audiogramBanana.getLayoutParams().height = (int)(((mChart.getPosition(new Entry(0, 0), AxisDependency.LEFT)).y)-((mChart.getPosition(new Entry(51, 0), AxisDependency.LEFT)).y));
        Log.d("width", Integer.toString(9));
        
		//Draw Labels
		Log.d("mChart.getRight()", Integer.toString(mChart.getRight()));

		ImageView normalHearingImg = (ImageView) findViewById(R.id.normalHearing);
        LayoutParams nHParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        nHParams.setMargins(mChart.getRight()-(int)((float)normalHearingImg.getMeasuredWidth()*2.8), (int)((mChart.getPosition(new Entry(-15, 0), AxisDependency.LEFT)).y)-(int)((float)normalHearingImg.getMeasuredHeight()/2), 0, 0);
        normalHearingImg.setLayoutParams(nHParams);
        
		ImageView profoundHearingImg = (ImageView) findViewById(R.id.profoundHearing);
        LayoutParams pHParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pHParams.setMargins(mChart.getRight()-(int)((float)profoundHearingImg.getMeasuredWidth()*2.8), (int)((mChart.getPosition(new Entry(-115, 0), AxisDependency.LEFT)).y)-(int)((float)profoundHearingImg.getMeasuredHeight()/2), 0, 0);
        profoundHearingImg.setLayoutParams(pHParams);
		
		//Draw blue crosses
        int adjustCrossXPos = -18;
        int adjustCrossYPos = 40;

		ImageView audiogramCrossImage1 = (ImageView) findViewById(R.id.audiogramCross1);	
		
		adjustCrossXPos = (int)((float)audiogramCrossImage1.getMeasuredHeight()/2)*-1;
		adjustCrossYPos = (int)((float)audiogramCrossImage1.getMeasuredHeight()/0.88);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[0]+adjustCrossXPos,(int)displayValueY[0]+adjustCrossYPos, 0,0);
		audiogramCrossImage1.setLayoutParams(params);
		
		ImageView audiogramCrossImage2 = (ImageView) findViewById(R.id.audiogramCross2);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[1]+adjustCrossXPos,(int)displayValueY[1]+adjustCrossYPos, 0,0);
		audiogramCrossImage2.setLayoutParams(params);
		
		ImageView audiogramCrossImage3 = (ImageView) findViewById(R.id.audiogramCross3);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[2]+adjustCrossXPos,(int)displayValueY[2]+adjustCrossYPos, 0,0);
		audiogramCrossImage3.setLayoutParams(params);
		
		ImageView audiogramCrossImage4 = (ImageView) findViewById(R.id.audiogramCross4);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[3]+adjustCrossXPos,(int)displayValueY[3]+adjustCrossYPos, 0,0);
		audiogramCrossImage4.setLayoutParams(params);
		
		ImageView audiogramCrossImage5 = (ImageView) findViewById(R.id.audiogramCross5);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[4]+adjustCrossXPos,(int)displayValueY[4]+adjustCrossYPos, 0,0);
		audiogramCrossImage5.setLayoutParams(params);
		
		ImageView audiogramCrossImage6 = (ImageView) findViewById(R.id.audiogramCross6);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[5]+adjustCrossXPos,(int)displayValueY[5]+adjustCrossYPos, 0,0);
		audiogramCrossImage6.setLayoutParams(params);
		
		ImageView audiogramCrossImage7 = (ImageView) findViewById(R.id.audiogramCross7);	
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)displayValueX[6]+adjustCrossXPos,(int)displayValueY[6]+adjustCrossYPos, 0,0);
		audiogramCrossImage7.setLayoutParams(params);
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
	    
        if(MainActivity.initialRingVolume == 0){
        	MainActivity.audioManager.setRingerMode(0);
        }
        else{
        	MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.initialMusicVolume, 0);		
	    MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_RING, MainActivity.initialRingVolume, 0);
		MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, MainActivity.initialVibNote);
		MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, MainActivity.initialVibRing);
        }
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
	    super.onResume();
	    Log.d("onResume","onResume");
	    MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
		MainActivity.audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
		MainActivity.audioManager.setRingerMode(0);
		MainActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.maxVolume, 0);
		
		Log.d("musicVolume", Integer.toString(MainActivity.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	    Log.d("onDestroy","onDestroy");
    }
	
	public int getStatusBarHeight() {
	    Rect r = new Rect();
	    Window w = getWindow();
	    w.getDecorView().getWindowVisibleDisplayFrame(r);
	    return r.top;
	}
	 
	public int getTitleBarHeight() {
	    int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
	    return (viewTop - getStatusBarHeight());
	}

}
