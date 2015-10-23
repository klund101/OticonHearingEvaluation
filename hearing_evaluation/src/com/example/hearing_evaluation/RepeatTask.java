package com.example.hearing_evaluation;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;



public class RepeatTask extends TimerTask {
	
	public long startCheckTime, stopCheckTime; 
	public final int nextTestEventTime = 3000;
	public static int[] freqValues = {125, 250, 500, 1000, 2000, 4000, 8000};
	public static int[] freqOrder = {3,4,6,5,2,1,0,3};
	
	public static Timer timer = new Timer();
	
	
    public int duration = 2; // seconds
    private final int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    public double freqOfTone = 1000; // Hz
    private final byte generatedSnd[] = new byte[2 * numSamples];
    public static AudioTrack audioTrack;
    
	
    @Override
    public void run() {
    	
  	
    	Log.d("--------","-------");

		if(TestActivity.yesBtnClicked == true){
			
			
			if(TestActivity.toneLevel > 10){
				TestActivity.toneLevel -= 10;
				System.out.println("-10");
			}
		}
		else{
			
			if(TestActivity.toneLevel<= 60){
				TestActivity.toneLevel += 5;
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
    		TestActivity.toneLevel = 30;
    		TestActivity.hearingThreshold[5] = TestActivity.toneLevel = 30;;
        	        	
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
    		
	        genTone(freqValues[freqOrder[TestActivity.currentFreq % freqOrder.length]]);
	        
	        playSound();
	    	        
	    	int repeatTimeChange = (int)(Math.random()*1000); 	
	    	
	    	
	    	timer.cancel();
	    	timer = new Timer();
	    	
	    	timer.schedule(new RepeatTask(), nextTestEventTime+repeatTimeChange);
    	}
    	
    }
    
    void genTone(double toneFreq){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/toneFreq));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude DIVIDED WITH ?????????
            //final short val = (short) ((dVal * 32767)/((70-TestActivity.toneLevel)*50) - 10);
        	final short val = (short) ((dVal * 32767)*Math.pow((-TestActivity.toneLevel),2)/800000);
            //(1 * 32767)*((-5)^2)/800000
            //Log.d("amp", Short.toString(val));
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
