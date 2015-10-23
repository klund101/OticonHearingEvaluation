package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class InterpretationActivity extends Activity {
	
	public String parseDataObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interpretation);
		
		initGui();
		
		Intent iin = getIntent();
	    Bundle b = iin.getExtras();
	    if (b != null) {
	    	parseDataObjectId  = (String) b.get("parseDataObjectId");
	    }
	}
	
	private void initGui() {
		//instructionThreeProceedButton = (Button) findViewById(R.id.btnInstructionsThreeProceed);
		//instructionThreeProceedButton.setOnClickListener(this);
	}
	
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnInstructionsThreeProceed:
//			startActivity(new Intent(InstructionsThreeActivity.this, ProfileIdActivity.class)); //
//		break;
//		}
//	}
	
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
