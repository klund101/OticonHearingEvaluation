package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OptionsActivity extends Activity implements OnClickListener{
	
	Button optionsInstructionsButton;
	Button optionsProceedButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		initGui();
	}
	
	private void initGui() {
		optionsInstructionsButton = (Button) findViewById(R.id.btnOptionsInstructions);
		optionsInstructionsButton.setOnClickListener(this);
		
		optionsProceedButton = (Button) findViewById(R.id.btnOptionsProceed);
		optionsProceedButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOptionsInstructions:
			Intent instrA = new Intent(OptionsActivity.this, InstructionsActivity.class);
	        startActivity(instrA);
		break;
		case R.id.btnOptionsProceed:
			Intent pIdA = new Intent(OptionsActivity.this, ProfileIdActivity.class);
	        startActivity(pIdA);			
		break;
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

	        finish();    	
	    	return true;
	    }
	    else
	    	return true;
	}
}
