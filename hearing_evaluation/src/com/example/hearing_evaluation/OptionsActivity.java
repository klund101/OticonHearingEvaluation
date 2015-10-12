package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
			Intent idA = new Intent(OptionsActivity.this, IdentityActivity.class);
	        startActivity(idA);			
		break;
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.options, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
