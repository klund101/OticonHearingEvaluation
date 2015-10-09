package com.example.hearing_evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.os.Bundle;

public class TestActivity extends ActionBarActivity implements OnClickListener {
	
	private Toast toast = null;	
	private static final String TAG = "hearing_evaluation";

	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				}
				toast.setText(TAG + ": " + msg);
				toast.show();
			}
		});
	}
	
	private static final int MIN_SAMPLE_RATE = 44100;
	
	public static Button yesButton;
	
	public int tmpCount = 0; 
	public static float[] testDbResult = new float[RepeatTask.freqValues.length];
	public static int toneLevel = 40;
	//Temperary until unit is defined
	public static int tmpToneLevel = 1000;
	
	public static boolean yesBtnClicked = false;
	public static int[] hearingThreshold = new int[6]; // history of hearing thresholds
	public static int currentFreq;
	public static final int testFlowEnd = RepeatTask.freqValues.length;
    private static Context testActivityContext;


	public static String parseDataObjectId;
	
	RepeatTask repeatTask = new RepeatTask();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TestActivity.testActivityContext = getApplicationContext();
				
		setContentView(R.layout.activity_test);
		
        initGui();
                       
        //Parse
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
        	parseDataObjectId  = (String) b.get("parseDataObjectId");
        }
       
        Log.d("d", parseDataObjectId);
        
		try {
			initPd();
		} catch (IOException e) {
			toast(e.toString());
			finish();
		}
		
		/// TEST FLOW
			currentFreq = 0;
			repeatTask.timer = new Timer();
			repeatTask.timer.schedule(new RepeatTask(), 3000);
			System.out.println(Integer.toString(currentFreq));
			if(currentFreq > testFlowEnd){
				//repeatTask.timer.cancel();
				goToResults();
			}
		
	
			//Log.d("toneLevel", Integer.toString(toneLevel));
			
		//----
	}
	
	//Initialize the PdAudio and it's parameters
		private void initPd() throws IOException {
			Log.d("Initializing...", "PureData Patch");
			File dir = getFilesDir();
			IoUtils.extractZipResource(getResources().openRawResource(R.raw.patch), dir, true);
			Log.d("extracted","");
			File patchFile = new File(dir, "patch_test.pd");
			PdBase.openPatch(patchFile.getAbsolutePath());
		}
	
	private void initGui() {
		yesButton = (Button) findViewById(R.id.btnYes);
		yesButton.setOnClickListener(this);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnYes:
			
			repeatTask.timer.cancel();
			repeatTask.timer = new Timer();
			//repeatTask.timer.purge();
			//tmpCount += 1;
			//Log.d("BUTTOOOON CLICKED", "");
			
			if(AudioTrack.PLAYSTATE_PLAYING == repeatTask.audioTrack.getPlayState())
				repeatTask.audioTrack.stop();
			
			repeatTask = new RepeatTask();
						
			yesBtnClicked = true;
			repeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
			
						        	       	        		 
			//if(tmpCount>=100/*9*/){// number of tested frequencies
//			if(currentFreq > 2){ //Testing 1000 Hz two times
//				currentFreq = 2;
////				
//				goToResults();
////				
//			 }

		break;
		}
	}

    public void initAudio() throws IOException {
		AudioParameters.init(this);
		int srate = Math.max(MIN_SAMPLE_RATE, AudioParameters.suggestSampleRate());
		PdAudio.initAudio(srate, 0, 2, 8, true);
		PdAudio.startAudio(this);
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
		else
			return true;
	}
	
    protected void onResume() {
    super.onResume();
    PdAudio.startAudio(this);
    
		Log.d("PdAudio.isRunning()", String.valueOf(PdAudio.isRunning()));
		if(!PdAudio.isRunning()){
			try {
				initAudio();
				Log.d("PdAudio.isRunning()", String.valueOf(PdAudio.isRunning()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
    }
	
    @Override
    protected void onPause() {
    super.onPause();
    
    }
    
	public void onDestroy() {
		super.onDestroy();
		PdAudio.stopAudio();
		PdAudio.release();
		PdBase.release();
	}
	
	public static void goToResults(){
			
		
		//Parse
	        ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
	        query.getInBackground(parseDataObjectId, new GetCallback<ParseObject>() {
	          public void done(ParseObject userDataObject, ParseException e) {
	            if (e == null) {
	            	userDataObject.put("HearingData", Arrays.toString(testDbResult));
	            	try {
	            		userDataObject.save();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            } else {
	              // something went wrong
	            }
	          }
	        });
		 
		 Intent resultA = new Intent(TestActivity.getTestActivityContext(), ResultActivity.class);
		 resultA.putExtra("parseDataObjectId", parseDataObjectId);
		 resultA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 TestActivity.getTestActivityContext().startActivity(resultA);
		 PdAudio.stopAudio();
		 PdAudio.release();
		 PdBase.release();
		
	}
	public static void clickYesButton(){
		yesButton.performClick();
	}
	
	public static Context getTestActivityContext() {
        return TestActivity.testActivityContext;
    }
}
