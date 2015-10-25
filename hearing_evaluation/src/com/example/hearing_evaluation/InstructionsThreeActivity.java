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

public class InstructionsThreeActivity extends Activity implements OnTouchListener{

	ImageButton instructionThreeProceedButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions_three);
		
		initGui();		
	}

	private void initGui() {
		instructionThreeProceedButton = (ImageButton) findViewById(R.id.btnInstructionsThreeProceed);
		instructionThreeProceedButton.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
				case R.id.btnInstructionsThreeProceed:
					if (event.getAction() == android.view.MotionEvent.ACTION_DOWN){
						instructionThreeProceedButton.setColorFilter(Color.argb(100, 0, 0, 0));
					}
					else if (event.getAction() == android.view.MotionEvent.ACTION_UP){
						instructionThreeProceedButton.setColorFilter(Color.argb(0, 0, 0, 0));
						startActivity(new Intent(InstructionsThreeActivity.this, ProfileIdActivity.class)); //
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
	    	super.onBackPressed();
	    	return true;
	    }
		else
			return true;
	}

}
