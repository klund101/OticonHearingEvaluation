package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InstructionsTwoActivity extends Activity implements OnClickListener{

	Button instructionTwoProceedButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions_two);
		
		initGui();
	}
	
	private void initGui() {
		instructionTwoProceedButton = (Button) findViewById(R.id.btnInstructionsTwoProceed);
		instructionTwoProceedButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInstructionsTwoProceed:
			startActivity(new Intent(InstructionsTwoActivity.this, InstructionsThreeActivity.class)); //
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
	    	super.onBackPressed();
	    	return true;
	    }
		else
			return true;
	}

}
