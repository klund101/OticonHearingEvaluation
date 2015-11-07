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
	public static int[] freqOrder = {3,4,5,6,2,1,0,3};
	
	public static Timer timer = new Timer();
	
	
    public int duration = 2; // seconds
    private final int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    public double freqOfTone = 1000; // Hz
    private final byte generatedSnd[] = new byte[2 * numSamples];
    public static AudioTrack audioTrack;
    
	public float[][] dBhLArray = new float[][]{
			  {0f, 19f, 35f, 61f, 105f, 189f, 336f, 600f, 1070f, 1940f, 3450f, 6200f, 1100f, 19500f, 32000f},
			  {0f, 9.05f, 11f, 20.5f, 38f, 68f, 120f, 215f, 380f, 680f, 1220f, 2200f, 3900f, 7000f, 12500f},
			  {0f, 2.1f, 9.05f, 11.1f, 22f, 39f, 72.1f, 126f, 224f, 396f, 705f, 1265f, 2275f, 4070f, 7300f},
			  {0f, 0f, 2.01f, 9.04f, 11.3f, 21.5f, 38.7f, 72.1f, 125f, 222f, 395f, 700f, 1258f, 2265f, 4055f},
			  {0f, 0f, 2.01072f, 9.12f, 12.8f, 26.1f, 45.05f, 80f, 140f, 249f, 441f, 792f, 1410f, 2535f, 4530f},
			  {0f, 0f, 3.2f, 10.2f, 18.1f, 32.02f, 56f, 103f, 179f, 321f, 568f, 1020f, 1835f, 3280f, 5850f},
			  {0f, 0f, 0f, 2.01f, 10.1f, 11.5f, 30.3f, 50.9f, 85.5f, 155f, 273.5f, 485f, 875f, 1555f, 2800f}
			};
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
    		TestActivity.hearingThreshold[1] > TestActivity.hearingThreshold[0]){
    		
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
    		System.out.println(Integer.toString(freqValues[freqOrder[TestActivity.currentFreq % freqOrder.length]]));
    		
        	if(TestActivity.currentFreq >= freqOrder.length)
        		TestActivity.isLeftChannel = false;
    		
        	for(int i = 0; i <= 5; i++){
        		TestActivity.hearingThreshold[i] = 0;
        	}
        	TestActivity.dBLevelIndex = 5; // index + 1
    		TestActivity.toneLevel = 30;
    		TestActivity.hearingThreshold[5] = TestActivity.toneLevel;
        	        	
			if(AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState())
				audioTrack.stop();
			
			
			
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
    		
	        
	        if(TestActivity.dBLevelIndex >= 0)
	        	testToneAmpdB = dBhLArray[freqOrder[TestActivity.currentFreq % freqOrder.length]][TestActivity.dBLevelIndex];
	       
	        Log.d("dBLevelIndex",Integer.toString(freqOrder[TestActivity.currentFreq % freqOrder.length]) + ", " + Integer.toString(TestActivity.dBLevelIndex)); 
	        
	        genTone(freqValues[freqOrder[TestActivity.currentFreq % freqOrder.length]], testToneAmpdB);
	        playSound();
	    	        
	    	int repeatTimeChange = (int)(Math.random()*1000); 	
	    	
	    	
	    	timer.cancel();
	    	timer = new Timer();
	    	
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
        	final short val = (short) (dVal * ampdB);
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
    }
        
}
