package com.example.hearing_evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.puredata.core.PdBase;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis; 
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

public class ArchiveResultActivity extends IdentityActivity implements OnClickListener {
	
	Button sendEmailButton;
	EditText uEmail;
	
	private LineChart mChart;
	public String date;
	public String passedNamePos;
	public String passedDataPos;
	public int passedNamePosInt;
	public int passedDataPosInt;
	public String readUserName;
	public int[] freqValues = {250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 8000};
	ArrayList<String> lineList = new ArrayList<String>();
	float[] dBValues;
	public String freqsAndData = ""; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive_result);
		
        initGui();
        
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	passedNamePos  = (String) b.get("namePos");
        	passedDataPos = (String) b.get("hearingDataPos");
        }
        
        Log.d("passedDataPos", passedDataPos);
        
        passedNamePosInt = Integer.valueOf(passedNamePos);
        passedDataPosInt = Integer.valueOf(passedDataPos);
        
        uEmail = (EditText)findViewById(R.id.archiveUserEmail);
        uEmail.clearComposingText();
           
		Log.d("user email", getEmailId(this));
		uEmail.setText(getEmailId(this));	
        
        
//////////MPchart
    	
	//LineChart chart = (LineChart) findViewById(R.id.chart);
	mChart = (LineChart) findViewById(R.id.chartArchive);
	// enable value highlighting
	mChart.setHighlightEnabled(true);

	// enable touch gestures
	mChart.setTouchEnabled(false);

	// enable scaling and dragging
	mChart.setDragEnabled(true);
	mChart.setScaleEnabled(true);
	// mChart.setScaleXEnabled(true);
	// mChart.setScaleYEnabled(true);

 	// if disabled, scaling can be done on x- and y-axis separately
 	mChart.setPinchZoom(true);
 
 	// x-axis limit line
 	// LimitLine llXAxis = new LimitLine(10f, "Index 10");
 	// llXAxis.setLineWidth(4f);
	// llXAxis.enableDashedLine(10f, 10f, 0f);
	// llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
 	// llXAxis.setTextSize(10f);
 	//
	// XAxis xAxis = mChart.getXAxis();
	// xAxis.addLimitLine(llXAxis);
 
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
	
	//-----READ FROM TEXT FILE
	try {  
        File pathToExternalStorage = Environment.getExternalStorageDirectory();
        File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/Oticon");        
        //Create a File for the output file data
        File saveFilePath = new File (appDirectory, "OticonAppData.txt");
        FileInputStream fIn = new FileInputStream(saveFilePath);  
        BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));  
        String line = ""; 
        while ((line = myReader.readLine()) != null) {
        	lineList.add(line);
        }  
        readUserName = lineList.get(passedNamePosInt);        
        String[] readUserData = lineList.get(passedDataPosInt).split("; ");
        myReader.close(); 
        dBValues = new float[readUserData.length];
        for(int i=0; i<dBValues.length; i++){
        	dBValues[i] = Float.parseFloat(readUserData[i]);
        	
        	Log.d("dBValue",  "[" + Integer.toString(freqValues[i]) + ", " + Float.toString(dBValues[i]) + "]");
        	freqsAndData +=  "[" + Integer.toString(freqValues[i]) + ", " + Float.toString(dBValues[i]) + "]" + "\t";
        }
        
          
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
	
	setData(freqValues.length,1);
	
	//date = new SimpleDateFormat("MM/dd-yyyy  HH:mm").format(new Date());
	mChart.setDescription(readUserName/* + ", " + date*/); // set user name on audiogram
	//////////	
		
	}
	
    //////////MPchart
    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(Integer.toString(freqValues[i]));
            Log.d("xval", String.valueOf(xVals));
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        
        Log.d("yval", String.valueOf(dBValues[0]));
        Log.d("yval", String.valueOf(dBValues[1]));
        Log.d("yval", String.valueOf(dBValues[2]));

        for (int i = 0; i < count; i++) {

            //float mult = (range + 1);
            //float val = (float) (Math.random() * mult) - 20;
            //Log.d("yval", String.valueOf(val));
            yVals.add(new Entry(dBValues[i], i));
            Log.d("yval", String.valueOf(dBValues[i]));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Normal hearing");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
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
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        
        // set data
        mChart.setData(data);
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
	
	private void initGui() {
		sendEmailButton = (Button) findViewById(R.id.btnArchiveSendEmail);
		sendEmailButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnArchiveSendEmail:
		    Intent emailIntent = new Intent(Intent.ACTION_SEND);
		    emailIntent.setType("message/rfc822");
		    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{uEmail.getText().toString()});
		    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oticon Mobile Hearing Evaluation");
		    emailIntent.putExtra(Intent.EXTRA_TEXT   , "Oticon Mobile Hearing Evaluation data for " + readUserName + ":" + "\n\n" + freqsAndData);
			try {
			    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			    Log.i("Finished sending email...", "");
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(ArchiveResultActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
