package com.example.hearing_evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
	
	Button yesButton;
	
	public int tmpCount = 0; 
	float[] testDbResult = {0,0,0,0,0,0,0,0,0};
	public static int[] freqValues = {250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 8000};
	public int toneLevel = 40;
	public static boolean yesBtnClicked = false;
	public long startCheckTime, stopCheckTime; 
	public int nextTestEventTime = 3000;
	public Handler testTimeHandler = new Handler();

	public String parseDataObjectId;
	
	//public Runnable testFlowRunnable;
	//RepeatTask repeatTask = new RepeatTask();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
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

		testTimeHandler.postDelayed(testFlowRunnable, 3000);
		
		
		
		//repeatTask.run();
		
		//repeatTask.timer.schedule(new RepeatTask(), 3000);
		
	
			//Log.d("toneLevel", Integer.toString(toneLevel));
			
		//----
	}
	
	public Runnable testFlowRunnable = new Runnable() {

		@Override
		public void run() {
			boolean testFlowRunnableCancel = false;
//			while(!testFlowRunnableCancel){
				int repeatTimeChange = (int)(Math.random()*1000);
	
				PdBase.sendFloat("toneLevel", toneLevel);
				PdBase.sendFloat("freqValue", freqValues[2]);
				startCheckTime = java.lang.System.currentTimeMillis();
				
				//Log.d("toneLeveltimer", Integer.toString(toneLevel));
				nextTestEventTime = 3000;
				PdBase.sendFloat("toneLevel", toneLevel);
				PdBase.sendFloat("freqValue", freqValues[2]);
				startCheckTime = java.lang.System.currentTimeMillis();
				
					if(yesBtnClicked == true){
						Log.d("yesBtnClicked", Boolean.toString(yesBtnClicked));
						toneLevel -= 10;
						startCheckTime = 0;
						yesBtnClicked = false;
//						testFlowRunnableCancel = true;
//						testTimeHandler.postDelayed(this, nextTestEventTime+(int)(Math.random()*1000));
						testTimeHandler.removeCallbacks(testFlowRunnable);
					}		
				
//				if(yesBtnClicked == true)
//					yesBtnClicked = false;
				else
					toneLevel += 5;
		    	
				//testFlowRunnableCancel = true;
				Log.d("repeatTimeChange", Integer.toString(repeatTimeChange+nextTestEventTime));
				testTimeHandler.postDelayed(this, nextTestEventTime+repeatTimeChange);	
//			}
		}};
	
	
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
			
			yesBtnClicked = true;
			tmpCount += 1;
			        	       	        		 
			if(tmpCount>=9){// number of tested frequencies
								
				 tmpCount = 0;
				 
				 testTimeHandler.removeCallbacks(testFlowRunnable);
				 
				 for(int i=0; i<testDbResult.length;i++){
					 testDbResult[i] = ((float)Math.random()*20)-20;
				 }
				 
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
				 
				 Intent resultA = new Intent(TestActivity.this, ResultActivity.class);
				 resultA.putExtra("parseDataObjectId", parseDataObjectId);
		         startActivity(resultA);
				 PdAudio.stopAudio();
				 PdAudio.release();
				 PdBase.release();
			 }
			 PdBase.sendBang("playTestTone");
			 // 
		break;
		}
	}

    public void initAudio() throws IOException {
		AudioParameters.init(this);
		int srate = Math.max(MIN_SAMPLE_RATE, AudioParameters.suggestSampleRate());
		PdAudio.initAudio(srate, 0, 2, 1, true);
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
	
	
	
}
