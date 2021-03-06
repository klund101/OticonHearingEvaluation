package com.example.hearing_evaluation;

import com.parse.ParseObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class PureToneDbTestActivity extends Activity implements OnClickListener {
	
	public Button pureToneStartButton;
	public Button pureToneStopButton;
	public Button pureToneSaveButton;
	public EditText pureToneFreqTxt;
	public EditText pureToneAmpTxt;
	public EditText pureToneDBSPLTxt;
	public CheckBox pureToneCheckBoxLeft;
	public CheckBox pureToneCheckBoxRight;
	
	public double pureToneFreq;
	public double pureToneAmp;
	public String pureToneDBSPL;
	
	public int leftChannelState = 0;
	public int rightChannelState = 0;
	
	public ParseObject ampToDbSplObject;
	
	public int duration = 120; // seconds
    private final int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final byte generatedSnd[] = new byte[2 * numSamples];
    public static AudioTrack pureToneTestAudioTrack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pure_tone_db_test);
		initGui();
		pureToneFreqTxt.clearComposingText();
		pureToneAmpTxt.clearComposingText();
		
		genTone(0,0);
		playSound();
		pureToneTestAudioTrack.stop();
		
		//Log.d("", android.os.Build.MODEL.toString());
	}

	void initGui(){
		pureToneStartButton = (Button) findViewById(R.id.btnStartTestTone);
		pureToneStartButton.setOnClickListener(this);
		pureToneStopButton = (Button) findViewById(R.id.btnStopTestTone);
		pureToneStopButton.setOnClickListener(this);
		pureToneSaveButton = (Button) findViewById(R.id.btnSaveTestTone);
		pureToneSaveButton.setOnClickListener(this);
		pureToneFreqTxt = (EditText)findViewById(R.id.txtFieldPureToneTestFreq);
		pureToneAmpTxt = (EditText)findViewById(R.id.txtFieldPureToneTestAmp);
		pureToneDBSPLTxt = (EditText)findViewById(R.id.txtFieldPureToneTestDBSPL);
		pureToneCheckBoxLeft = (CheckBox) findViewById(R.id.leftEarphoneCheckBox);
		pureToneCheckBoxLeft.setOnClickListener(this);
		pureToneCheckBoxRight = (CheckBox) findViewById(R.id.rightEarphoneCheckBox);
		pureToneCheckBoxRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) throws RuntimeException {
		switch (v.getId()) {
		case R.id.btnStartTestTone:
			
	         if (pureToneCheckBoxLeft.isChecked())
	        	 leftChannelState = 1;
	         else
	        	 leftChannelState = 0;
	         
	         if (pureToneCheckBoxRight.isChecked())
	        	 rightChannelState = 1;
	         else
	        	 rightChannelState = 0;
			
			if(pureToneFreqTxt.getText().toString().length() >= 1 && pureToneAmpTxt.getText().toString().length() >= 1){
			
				pureToneFreq = Double.valueOf((pureToneFreqTxt.getText().toString()));
				pureToneAmp = Double.valueOf((pureToneAmpTxt.getText().toString()));
				try{
				pureToneTestAudioTrack.stop();
				}
				catch(Exception e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e);
				}
				genTone(pureToneFreq,pureToneAmp);
				playSound();
			
			}
			
		break;
		case R.id.btnStopTestTone:
			if(AudioTrack.PLAYSTATE_PLAYING == pureToneTestAudioTrack.getPlayState())
				pureToneTestAudioTrack.stop();
		break;
		case R.id.btnSaveTestTone:
			AlertDialog alertDialog = new AlertDialog.Builder(PureToneDbTestActivity.this)
	        .setTitle("Save dB-SPL?")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					pureToneDBSPL = pureToneDBSPLTxt.getText().toString();
					
					//Parse
					ampToDbSplObject = new ParseObject("dBSPLcalibration");
					Log.d("ampToDbSplObject","ampToDbSplObject");
					ampToDbSplObject.put("Frequency", Double.toString(pureToneFreq));
					ampToDbSplObject.put("DigitalAmplitude", Double.toString(pureToneAmp));
					ampToDbSplObject.put("dBSPL", pureToneDBSPL);
					ampToDbSplObject.put("Device", android.os.Build.MODEL.toString());
					
			        ampToDbSplObject.saveInBackground();
				}
	         })
	         	.setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            }
	         })
	         .setIcon(android.R.drawable.ic_dialog_alert)
	         .show();
	         
			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {         
			    @Override
			    public void onCancel(DialogInterface dialog) {
			    }
	         });

		break;
		}
	}
	
    void genTone(double toneFreq, double toneAmp){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * ((toneFreq/2)/sampleRate) * i);
        }

        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
        	final short val = (short) (dVal * toneAmp);//Max: 32767
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }
    
    void playSound(){
    	pureToneTestAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
    	pureToneTestAudioTrack.write(generatedSnd, 0, generatedSnd.length);
    	try{
			pureToneTestAudioTrack.stop();
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
    	
    	pureToneTestAudioTrack.setStereoVolume(leftChannelState, rightChannelState); // 
    	
    	pureToneTestAudioTrack.play();
    }
    
    protected void onDestroy() throws RuntimeException{
    	super.onDestroy();
    
		try{
		pureToneTestAudioTrack.stop();
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	
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
    		super.onBackPressed();
        	return true;
	    }
		else
			return true;
	}

}
