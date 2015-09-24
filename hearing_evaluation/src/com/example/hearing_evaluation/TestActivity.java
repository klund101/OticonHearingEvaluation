package com.example.hearing_evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.System;

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
	Button noButton;
	
	public int tmpCount = 0;
	public int[] freqValues = {250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 8000}; 
	public long startCheckTime, stopCheckTime; 
	public int toneLevel = 40;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.activity_test);
		
        initGui();
        
		try {
			initPd();
		} catch (IOException e) {
			toast(e.toString());
			finish();
		}
		
		/// TEST FLOW
		
		PdBase.sendFloat("toneLevel", toneLevel);
		PdBase.sendFloat("freqValue", freqValues[2]);
		startCheckTime = java.lang.System.currentTimeMillis();
		Log.d("startCheckTime", Long.toString(startCheckTime));
			
			stopCheckTime = java.lang.System.currentTimeMillis();
			Log.d("stopCheckTime", Long.toString(stopCheckTime));
			
			if(stopCheckTime-startCheckTime<= 3000)
				toneLevel -= 10;
			else
				toneLevel += 5;
			
			Log.d("toneLevel", Integer.toString(toneLevel));
			
			PdBase.sendFloat("toneLevel", toneLevel);
			PdBase.sendFloat("freqValue", freqValues[2]);
			startCheckTime = java.lang.System.currentTimeMillis();
			
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
			    @Override
			    public void run() {
			    	Log.d("toneLevel", Integer.toString(toneLevel));
			    }
			};
			
			timer.scheduleAtFixedRate(task, 0, 3000 - (int)(Math.random()*100));	
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
		noButton = (Button) findViewById(R.id.btnNo);
		noButton.setOnClickListener(this);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnYes:

			 tmpCount += 1;
			//get the path to sdcard 
	        File pathToExternalStorage = Environment.getExternalStorageDirectory();
	        File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/Oticon");
	        // have the object build the directory structure, if needed.
	        appDirectory.mkdirs();
	            
	        //Create a File for the output file data
	        File saveFilePath = new File (appDirectory, "OticonAppData.txt");

	        try{
		        FileOutputStream fos = new FileOutputStream (saveFilePath, true);
		        OutputStreamWriter OutDataWriter  = new OutputStreamWriter(fos);
		        OutDataWriter.append((float)(Math.random()*10)-20 + "; ");
		        // OutDataWriter.append(equipNo.getText() + newline);
		        OutDataWriter.close();
		        fos.flush();
		        fos.close();
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
			 
			 if(tmpCount>=9){// number of tested frequencies
				 tmpCount = 0;
				 Intent resultA = new Intent(TestActivity.this, ResultActivity.class);
		         startActivity(resultA);
				 PdAudio.stopAudio();
				 PdAudio.release();
				 PdBase.release();
			 }
			 PdBase.sendBang("playTestTone");
			 // 
		break;
		case R.id.btnNo:
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
