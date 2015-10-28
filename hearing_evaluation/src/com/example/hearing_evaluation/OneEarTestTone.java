package com.example.hearing_evaluation;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class OneEarTestTone extends TimerTask {
	
    private final int oneEarTestToneLevel = 50;
	public int duration = 2; // seconds
    private final int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    public double freqOfTone = 1000; // Hz
    private final byte generatedSnd[] = new byte[2 * numSamples];
    public static AudioTrack oneEarTestToneAudioTrack;

	public static Timer oneEarTestToneTimer = new Timer();
	
	@Override
	public void run() {

		genTone(freqOfTone);
		playSound();
		
		oneEarTestToneTimer.cancel();
		oneEarTestToneTimer = new Timer();
		
		oneEarTestToneTimer.schedule(new OneEarTestTone(), 3000);
		
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
        	final short val = (short) ((dVal * 32767)*Math.pow((-TestActivity.toneLevel),2)/800000);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }
    
    void playSound(){
    	oneEarTestToneAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
    	oneEarTestToneAudioTrack.write(generatedSnd, 0, generatedSnd.length);
    	oneEarTestToneAudioTrack.stop();
    	
    	oneEarTestToneAudioTrack.setStereoVolume(1, 0); // 
    	
    	oneEarTestToneAudioTrack.play();
    }

}
