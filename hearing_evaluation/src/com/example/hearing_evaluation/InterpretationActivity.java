package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InterpretationActivity extends Activity {
	
	public String parseDataObjectId;
	
	public String recEnt = "";
	public int ans9 = 0;
	
	public int ptaLeft;
	float dBValuesPTALeft;
	public String resultsValuesStringLeft;
	public String resultsValuesStringLeftSubS;
	public TextView diagnosisLeft;
	
	public int ptaRight;
	float dBValuesPTARight;
	public String resultsValuesStringRight;
	public String resultsValuesStringRightSubS;
	public TextView diagnosisRight;
	
	public TextView diagnosisRec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interpretation);
		
		initGui();
		
		Intent iin = getIntent();
	    Bundle b = iin.getExtras();
	    if (b != null) {
	    	parseDataObjectId  = (String) b.get("parseDataObjectId");
	    }
		
	    diagnosisLeft.setText("");
	    diagnosisRight.setText("");
	    diagnosisRec.setText("");
	    
		//Parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 0)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus.";
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 0)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to hyperacousis.";
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to pain in the ear.";
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 0)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus and hyperacousis.";
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 0 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus and hyperacousis.";
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 0 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus and pain in the ear.";
				}
				if((String)(object.get("q_6"))  != null && (String)(object.get("q_7"))  != null && (String)(object.get("q_10"))  != null){
					if(Integer.parseInt((String)(object.get("q_6"))) == 1 && Integer.parseInt((String)(object.get("q_7"))) == 1 && Integer.parseInt((String)(object.get("q_10"))) == 1)
						recEnt = "We recommend that you seek an ear, nose and throat physician due to tinnitus, hyperacousis and pain in the ear.";
				}
				
				if((String)(object.get("q_9"))  != null)
					ans9 = Integer.parseInt((String)(object.get("q_9")));
				
				resultsValuesStringLeft = (String) object.get("HearingDataLeft");
				resultsValuesStringLeftSubS = resultsValuesStringLeft.substring(1);
				resultsValuesStringLeftSubS = resultsValuesStringLeftSubS.replace(']', ' ');
				resultsValuesStringLeftSubS = resultsValuesStringLeftSubS.trim();
			    
		        dBValuesPTALeft = 0;
			    String[] dBValuesReaderLeft = resultsValuesStringLeftSubS.split(",");
		        for(int i=2; i<6; i++){
		        	dBValuesPTALeft += Float.parseFloat(dBValuesReaderLeft[i]);
		        	Log.d("dBValuesPTALeft", Float.toString(dBValuesPTALeft));
		        }
		        ptaLeft = (int)(((dBValuesPTALeft/4)*-1)+0.5f);
		        Log.d("ptaLeft", Float.toString(ptaLeft));
		        

				resultsValuesStringRight = (String) object.get("HearingDataRight");
				resultsValuesStringRightSubS = resultsValuesStringRight.substring(1);
				resultsValuesStringRightSubS = resultsValuesStringRightSubS.replace(']', ' ');
				resultsValuesStringRightSubS = resultsValuesStringRightSubS.trim();
			    
		        dBValuesPTARight = 0;
		        String[] dBValuesReaderRight = resultsValuesStringRightSubS.split(",");
		        for(int i=2; i<6; i++){
		        	dBValuesPTARight += Float.parseFloat(dBValuesReaderRight[i]);
		        	Log.d("dBValuesPTARight", Float.toString(dBValuesPTARight));
		        }
		        ptaRight = (int)(((dBValuesPTARight/4)*-1)+0.5f);
		        Log.d("ptaRight", Float.toString(ptaRight));
		        
		        if(Math.abs(ptaLeft-ptaRight) <= 20){
		        
		        	if(ans9 == 1){
			        	diagnosisRight.setText("The results are unreliable due to that you have a cold. We recommend that you redo the test when you are over it." + "\n\n" + recEnt);
			        	diagnosisRight.setTextColor(Color.parseColor("#c5178b"));
			        	diagnosisLeft.setTextColor(color.transparent);
			        }
		        	else{
		        	
				        if(ptaLeft < 21){
				        	diagnosisLeft.setText("The test results suggest that you do not have a hearing loss on the left ear. ");		        
				        }
				        else if(ptaLeft >= 21 && ptaLeft < 41){
				        	diagnosisLeft.setText("The test results suggest that you have a mild hearing loss on the the left ear. You should contact a hearing professional for a hearing test. ");
				        }
				        else if(ptaLeft >= 41 && ptaLeft < 71){
				        	diagnosisLeft.setText("The test results suggest that you have a moderate hearing loss on the the left ear. You should contact a hearing professional for a hearing test. ");
				        }
				        else if(ptaLeft >= 71){
				        	diagnosisLeft.setText("The test results suggest that you have a severe hearing loss on the the left ear. You should contact a hearing professional for a hearing test. ");
				        }
				        
				        
				        if(ptaRight < 21){
				        	diagnosisRight.setText("The test results suggest that you do not have a hearing loss on the right ear. ");
				        }
				        else if(ptaRight >= 21 && ptaRight < 41){
				        	diagnosisRight.setText("The test results suggest that you have a mild hearing loss on the the right ear. You should contact a hearing professional for a hearing test. ");
				        }
				        else if(ptaRight >= 41 && ptaRight < 71){
				        	diagnosisRight.setText("The test results suggest that you have a moderate hearing loss on the the right ear. You should contact a hearing professional for a hearing test. ");
						}
				        else if(ptaRight >= 71){
				        	diagnosisRight.setText("The test results suggest that you have a severe hearing loss on the the right ear. You should contact a hearing professional for a hearing test. ");
				        }
				        
				        diagnosisRec.setText(recEnt);
		        	}
		        }
		        else{
		        	diagnosisRight.setText("Asymetrical results. Please check that the earplugs are correctly inserted and rerun the test in a quiet environement. " + "\n\n" + recEnt);
		        	diagnosisRight.setTextColor(Color.parseColor("#c5178b"));
		        	diagnosisLeft.setTextColor(color.transparent);
		        }
		    }
		});
		
	}
	
	private void initGui() {
		diagnosisLeft = (TextView) findViewById(R.id.diagnosisTxtLeft);
		diagnosisRight = (TextView) findViewById(R.id.diagnosisTxtRight);
		diagnosisRec = (TextView) findViewById(R.id.diagnosisTxtRec);
	}
	
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnInstructionsThreeProceed:
//			startActivity(new Intent(InstructionsThreeActivity.this, ProfileIdActivity.class)); //
//		break;
//		}
//	}
	
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
}
