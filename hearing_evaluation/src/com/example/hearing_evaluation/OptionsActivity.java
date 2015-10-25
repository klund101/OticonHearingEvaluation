package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

public class OptionsActivity extends Activity implements OnTouchListener{
	
	ImageButton optionsInstructionsButton;
	ImageButton optionsProceedButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		initGui();
	}
	
	private void initGui() {
		optionsInstructionsButton = (ImageButton) findViewById(R.id.btnOptionsInstructions);
		optionsInstructionsButton.setOnTouchListener(this);
		
		optionsProceedButton = (ImageButton) findViewById(R.id.btnOptionsProceed);
		optionsProceedButton.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnOptionsInstructions:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				optionsInstructionsButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				optionsInstructionsButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent instrA = new Intent(OptionsActivity.this, InstructionsActivity.class);
				startActivity(instrA);
			}
		break;
		case R.id.btnOptionsProceed:
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
				optionsProceedButton.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
				optionsProceedButton.setColorFilter(Color.argb(0, 0, 0, 0));
				Intent pIdA = new Intent(OptionsActivity.this, ProfileIdActivity.class);
				startActivity(pIdA);
			}
		break;
		}
		return false;
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

	        finish();    	
	    	return true;
	    }
	    else
	    	return true;
	}

}
