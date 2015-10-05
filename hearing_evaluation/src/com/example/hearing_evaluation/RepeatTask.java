package com.example.hearing_evaluation;

import java.util.Timer;
import java.util.TimerTask;

import org.puredata.core.PdBase;

import android.os.Handler;
import android.util.Log;



public class RepeatTask extends TimerTask {
	
	public long startCheckTime, stopCheckTime; 
	public int toneLevel = 40;
	public int nextTestEventTime = 3000;
	public static int[] freqValues = {250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 8000};
	public static boolean yesBtnClicked = false;
	public Runnable testFlowRunnable;
	//public Runnable testFlowRunnable;
	//public static Timer timer = new Timer();
	
    @Override
    public void run() {
    	final Handler testTimeHandler = new Handler();
		testTimeHandler.postDelayed(testFlowRunnable, 1000);

			testFlowRunnable = new Runnable() {

				@Override
				public void run() {
					int repeatTimeChange = (int)(Math.random()*1000);

					PdBase.sendFloat("toneLevel", toneLevel);
					PdBase.sendFloat("freqValue", freqValues[2]);
					startCheckTime = java.lang.System.currentTimeMillis();
					
					//Log.d("toneLeveltimer", Integer.toString(toneLevel));
					nextTestEventTime = 3000;
					PdBase.sendFloat("toneLevel", toneLevel);
					PdBase.sendFloat("freqValue", freqValues[2]);
					startCheckTime = java.lang.System.currentTimeMillis();
					
					//while(java.lang.System.currentTimeMillis()-startCheckTime <= 3000){//redo while - too much processing
						
						if(yesBtnClicked == true){
							Log.d("yesBtnClicked", Boolean.toString(yesBtnClicked));
							toneLevel -= 10;
							startCheckTime = 0;
							//nextTestEventTime = 0;
			//				Timer timerNext = new Timer();
			//				timerNext.schedule(new RepeatTask(), nextTestEventTime+repeatTimeChange);
							yesBtnClicked = false;
							//timer.cancel();
						}		
					//}
					
					if(yesBtnClicked = true)
						yesBtnClicked = false;
					else
						toneLevel += 5;
			    	
					Log.d("repeatTimeChange", Integer.toString(repeatTimeChange+nextTestEventTime));
			        //timer.schedule(new RepeatTask(), nextTestEventTime+repeatTimeChange);
					testTimeHandler.postDelayed(this, 1000);					
				}};
	  }
    
    void resetRepeatingTask() {
    	//testTimeHandler.removeCallbacks(testFlowRunnable);
    	//testTimeHandler.postDelayed(testFlowRunnable, 3000);         
    }
}
