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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	public static float[] testDbResultLeft = new float[RepeatTask.freqValues.length];
	public static float[] testDbResultRight = new float[RepeatTask.freqValues.length];
	public static int toneLevel = 40;
	//Temperary until unit is defined
	public static int tmpToneLevel = 1000;
	public final int startTestDelay = 3000;
	
	public static boolean yesBtnClicked = false;
	public static int[] hearingThreshold = new int[6]; // history of hearing thresholds
	public static int currentFreq;
	public static final int testFlowEnd = RepeatTask.freqValues.length;
	public static boolean isLeftChannel = true;
    private static Context testActivityContext;
    
    public long initTime;

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
		
		initTime = System.currentTimeMillis();
		
		currentFreq = 0;
		
    	for(int i = 0; i <= 5; i++){
    		hearingThreshold[i] = 0;
    	}
		toneLevel = 30;
		
		RepeatTask.timer = new Timer();
		RepeatTask.timer.schedule(new RepeatTask(), startTestDelay);
		System.out.println(Integer.toString(currentFreq));
//			if(currentFreq > RepeatTask.freqOrder.length && isLeftChannel == false){
//				//repeatTask.timer.cancel();
//				goToResults();
//			}
		
	
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
			
			RepeatTask.timer.cancel();
			RepeatTask.timer = new Timer(); 
			//repeatTask.timer.purge();
			//tmpCount += 1;
			//Log.d("BUTTOOOON CLICKED", "");
			
			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
				RepeatTask.audioTrack.stop();
			
			repeatTask = new RepeatTask();
						
			yesBtnClicked = true;
			RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
			
						        	       	        		 
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
	    else if (keyCode == KeyEvent.KEYCODE_BACK){
	    	
			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState()){
				RepeatTask.audioTrack.stop();
				RepeatTask.timer.cancel();
	    	
			AlertDialog alertDialog = new AlertDialog.Builder(TestActivity.this)
	        .setTitle("Leave test?")
	        .setMessage("Are you sure you want to leave the current hearing test? The incomplete test result will be discarded.")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) { 
	    			if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
	    				RepeatTask.audioTrack.stop();
	    				RepeatTask.timer.cancel();
	    				
		            	ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		            	try {
							Log.d("PARSE", parseDataObjectId);
		            	    query.get(parseDataObjectId).deleteInBackground();
		            	    query.get(parseDataObjectId).saveInBackground();
						} 
		            	catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	onBackPressed();
	            }
	         })
	        .setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	    			RepeatTask.timer = new Timer();
	    			toneLevel -= 5;
	    			RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
	            }
	         })
	         .setIcon(android.R.drawable.ic_dialog_alert)
	         .show();
	         
			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {         
			    @Override
			    public void onCancel(DialogInterface dialog) {
	    			RepeatTask.timer = new Timer(); 
	    			toneLevel -= 5;
	    			RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
			    }
	         });
			}
	    	return true;
	    }
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
		RepeatTask.timer = new Timer();
		toneLevel -= 5;
		RepeatTask.timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
    }
	
    @Override
    protected void onPause() {
    super.onPause();
	if(System.currentTimeMillis() > initTime + startTestDelay && AudioTrack.PLAYSTATE_PLAYING == RepeatTask.audioTrack.getPlayState())
		RepeatTask.audioTrack.stop();
		RepeatTask.timer.cancel();
    
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
	            	userDataObject.put("HearingDataLeft", Arrays.toString(testDbResultLeft));
	            	userDataObject.put("HearingDataRight", Arrays.toString(testDbResultRight));
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
		 
		 Intent ChannelSA = new Intent(TestActivity.getTestActivityContext(), ChannelShifterActivity.class);
		 ChannelSA.putExtra("parseDataObjectId", parseDataObjectId);
		 ChannelSA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 TestActivity.getTestActivityContext().startActivity(ChannelSA);
		 PdAudio.stopAudio();
		 PdAudio.release();
		 PdBase.release();
		
	}
	
	public static Context getTestActivityContext() {
        return TestActivity.testActivityContext;
    }
}
