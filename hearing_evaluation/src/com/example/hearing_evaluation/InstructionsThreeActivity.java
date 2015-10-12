package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InstructionsThreeActivity extends Activity implements OnClickListener{

	Button instructionThreeProceedButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions_three);
		
		initGui();		
	}

	private void initGui() {
		instructionThreeProceedButton = (Button) findViewById(R.id.btnInstructionsThreeProceed);
		instructionThreeProceedButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInstructionsThreeProceed:
			startActivity(new Intent(InstructionsThreeActivity.this, IdentityActivity.class)); //
		break;
		}
	}
}
