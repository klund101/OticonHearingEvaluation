package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.provider.ContactsContract;
import android.util.Log;



public class RepeatTask extends TimerTask {
	
	public long startCheckTime, stopCheckTime; 
	public final int nextTestEventTime = 3000;
	public static int[] freqValues = {125, 250, 500, 1000, 2000, 4000, 8000};
	public static int[] freqOrder =  {3,4,5,6,2,1,0,3};// FULL TEST!! QUICK DEMO:  {3,4,5,2,3};
	
	public static Timer timer = new Timer();
	
	
    public final static int duration = 3; // seconds/2
    public final static int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    public double freqOfTone = 1000; // Hz
    private final byte generatedSnd[] = new byte[2 * numSamples];
    public static AudioTrack audioTrack;
    
	public float testToneAmpdB;
		
    @Override
    public void run() {
    	
  	
    	Log.d("--------","-------");

		if(TestActivity.yesBtnClicked == true){
			
			
			if(TestActivity.toneLevel >= 10){
				TestActivity.toneLevel -= 10;
				TestActivity.dBLevelIndex = TestActivity.dBLevelIndex-2;
				System.out.println("-10");
			}
		}
		else{
			
			if(TestActivity.toneLevel<= 70){
				TestActivity.toneLevel += 5;
				TestActivity.dBLevelIndex++;
				System.out.println("+5");
			}
			else if(TestActivity.toneLevel > 70){
				TestActivity.upperThresholdCount++;
				Log.d("upperThresholdCount",Integer.toString(TestActivity.upperThresholdCount));
			}
				
		}
		TestActivity.yesBtnClicked = false;

    	for(int i = 0; i <= 4; i++){
    		TestActivity.hearingThreshold[i] = TestActivity.hearingThreshold[i+1];
    	}
    	TestActivity.hearingThreshold[5] = TestActivity.toneLevel;
    	
    	
    	if (TestActivity.hearingThreshold[5] < TestActivity.hearingThreshold[4] && /// Check for repeating pattern
    		TestActivity.hearingThreshold[4] > TestActivity.hearingThreshold[3] &&
    		TestActivity.hearingThreshold[3] > TestActivity.hearingThreshold[2] &&
    		TestActivity.hearingThreshold[2] < TestActivity.hearingThreshold[1] &&
    		TestActivity.hearingThreshold[1] > TestActivity.hearingThreshold[0] ||
    		TestActivity.upperThresholdCount >= 3){
    		
    		TestActivity.upperThresholdCount = 0;
    		
    		if(TestActivity.isLeftChannel)
    			TestActivity.testDbResultLeft[freqOrder[TestActivity.currentFreq % freqOrder.length]] = -TestActivity.hearingThreshold[4];
    		else
    			TestActivity.testDbResultRight[freqOrder[TestActivity.currentFreq % freqOrder.length]] = -TestActivity.hearingThreshold[4];
    		
    		//Parse
	        ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
	        query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
	        query.getInBackground(TestActivity.parseDataObjectId, new GetCallback<ParseObject>() {
	          public void done(ParseObject userDataObject, ParseException e) {
	            if (e == null) {
	            		userDataObject.put("HearingDataLeft", Arrays.toString(TestActivity.testDbResultLeft));
	            		userDataObject.put("HearingDataRight", Arrays.toString(TestActivity.testDbResultRight));
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
    		
    		TestActivity.currentFreq += 1;
    		TestActivity.progress.setProgress((int)((float)TestActivity.currentFreq/(freqOrder.length*2)*100));
    		Log.d("progress",Integer.toString((int)((float)TestActivity.currentFreq/(freqOrder.length*2)*100)));
    		System.out.println(Integer.toString(freqValues[freqOrder[TestActivity.currentFreq % freqOrder.length]]));
    		
        	if(TestActivity.currentFreq >= freqOrder.length)
        		TestActivity.isLeftChannel = false;
    		
        	for(int i = 0; i <= 5; i++){
        		TestActivity.hearingThreshold[i] = 0;
        	}
        	//TestActivity.dBLevelIndex = 5; // index + 1
        	if(TestActivity.testGender.equals("F")){
        			TestActivity.dBLevelIndex = TestActivity.offset[TestActivity.answerAll] + 
        						TestActivity.expectedFemale[((int)(TestActivity.testAge/10))-2][freqOrder[TestActivity.currentFreq % freqOrder.length]] + 7; //offset + 40 dB (8) - 1
        	}
        	else if(TestActivity.testGender.equals("M")){
    				TestActivity.dBLevelIndex = TestActivity.offset[TestActivity.answerAll] + 
    						TestActivity.expectedMale[((int)(TestActivity.testAge/10))-2][freqOrder[TestActivity.currentFreq % freqOrder.length]] + 7; //offset + 40 dB (8) - 1
        	}
        	
        	if(TestActivity.dBLevelIndex > 14)
        		TestActivity.dBLevelIndex = 14;
        	
    		//TestActivity.toneLevel = 30;
    		//TestActivity.hearingThreshold[5] = TestActivity.toneLevel;
        	TestActivity.toneLevel = (TestActivity.dBLevelIndex*5)+5;
        	TestActivity.hearingThreshold[5] = TestActivity.toneLevel;
        	
        	try{
				if(AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState())
					audioTrack.stop();
        	}
        	catch(Exception e){
        		
        	}
			
			
			
			if(TestActivity.currentFreq >= freqOrder.length*2 && TestActivity.isLeftChannel == false){
				TestActivity.isLeftChannel = true;
				//TestActivity.currentFreq = 2;
		    	timer.cancel();
		    	//timer = new Timer();
		    	
				TestActivity.goToResults();
			}
			else	
				timer.schedule(new RepeatTask(), 500 + (int)(Math.random()*500));
    	}
		
    	if(TestActivity.currentFreq < freqOrder.length*2){
    		
        	for(int i = 0; i <= 5; i++){
        		Log.d("TLarr",Integer.toString((TestActivity.hearingThreshold[i])));
        	}
    		
	        
	        if(TestActivity.dBLevelIndex >= 0 && TestActivity.dBLevelIndex <= 14)
	        	testToneAmpdB = TestActivity.dBhLArray[freqOrder[TestActivity.currentFreq % freqOrder.length]][TestActivity.dBLevelIndex];
	       
	        Log.d("dBLevelIndex",Integer.toString(freqOrder[TestActivity.currentFreq % freqOrder.length]) + ", " + Integer.toString(TestActivity.dBLevelIndex)); 
	        
	        genTone(freqValues[freqOrder[TestActivity.currentFreq % freqOrder.length]], testToneAmpdB);
	        playSound();
	    	        
	    	int repeatTimeChange = (int)(Math.random()*1000); 	
	    	
	    	
	    	timer.cancel();
	    	timer = new Timer();
	    	
	    	//Log.d("interval", Long.toString(System.currentTimeMillis()-TestActivity.initTime));
	    	
	    	timer.schedule(new RepeatTask(), nextTestEventTime+repeatTimeChange);
    	}
    	
    }
    
    void genTone(double toneFreq, float ampdB){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * ((toneFreq/2)/sampleRate) * i);
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        
        
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
        	final short val = (short) ((dVal * ampdB)*TestActivity.env[idx]);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }
    
    void playSound(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
    	audioTrack.stop();
    	
    	if(TestActivity.isLeftChannel)
    		audioTrack.setStereoVolume(1, 0); // 
    	else
    		audioTrack.setStereoVolume(0, 1);
    	
        audioTrack.play();
        Log.d("audioTrack.getPlaybackRate();", Integer.toString(audioTrack.getSampleRate()));
    }
        
}
