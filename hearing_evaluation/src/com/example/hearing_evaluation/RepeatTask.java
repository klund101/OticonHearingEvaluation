package com.example.hearing_evaluation;

import java.util.Timer;
import java.util.TimerTask;

import org.puredata.core.PdBase;

import android.media.AudioFormat;
import android.util.Log;



public class RepeatTask extends TimerTask {
	
	public long startCheckTime, stopCheckTime; 
	public int nextTestEventTime = 3000;
	public static int[] freqValues = {250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 8000};

	public static Timer timer = new Timer();
	
    @Override
    public void run() {
    	
    	for(int i = 0; i <= 4; i++){
    		TestActivity.hearingThreshold[i] = TestActivity.hearingThreshold[i+1];
    	}
    	TestActivity.hearingThreshold[5] = TestActivity.toneLevel;
    	
    	for(int i = 0; i <= 5; i++){
    		Log.d("TLarr",Integer.toString(TestActivity.hearingThreshold[i]));
    	}
    	
    	if (TestActivity.hearingThreshold[5] < TestActivity.hearingThreshold[4] &&
    		TestActivity.hearingThreshold[4] > TestActivity.hearingThreshold[3] &&
    		TestActivity.hearingThreshold[3] > TestActivity.hearingThreshold[2] &&
    		TestActivity.hearingThreshold[2] < TestActivity.hearingThreshold[1] &&
    		TestActivity.hearingThreshold[1] > TestActivity.hearingThreshold[0]){
    		
    		TestActivity.currentFreq += 1;
    		System.out.println(Integer.toString(freqValues[TestActivity.currentFreq]));
    		TestActivity.toneLevel = 30;
    		
        	for(int i = 0; i <= 4; i++){
        		TestActivity.hearingThreshold[i] = 0;
        	}
        	TestActivity.hearingThreshold[5] = TestActivity.toneLevel;
    	}
    	Log.d("--------","-------");

		if(TestActivity.yesBtnClicked == true){
			if(TestActivity.toneLevel >= 10){
				TestActivity.toneLevel -= 10;
				System.out.println("-10");
			}
		}
		else{
			if(TestActivity.toneLevel<= 60)
			TestActivity.toneLevel += 5;
			System.out.println("+5");
		}
		TestActivity.yesBtnClicked = false;
		
    	int repeatTimeChange = (int)(Math.random()*1000);
    	
    	PdBase.sendBang("stopPdTone");
    	
		PdBase.sendFloat("toneLevel", TestActivity.toneLevel);
		PdBase.sendFloat("freqValue", freqValues[TestActivity.currentFreq]);

        timer.schedule(new RepeatTask(), nextTestEventTime+repeatTimeChange);
    }
}
