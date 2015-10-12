package com.example.hearing_evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChannelShifterActivity extends Activity implements OnClickListener {
	
	Button channelShifterLeftButton;
	Button channelShifterRightButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_shifter);
		
		initGui();
	}

	private void initGui() {
		channelShifterLeftButton = (Button) findViewById(R.id.btnChannelShifterLeft);
		channelShifterLeftButton.setOnClickListener(this);
		channelShifterRightButton = (Button) findViewById(R.id.btnChannelShifterRight);
		channelShifterRightButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnChannelShifterLeft:
			startActivity(new Intent(ChannelShifterActivity.this, ResultActivity.class)); //
		break;
		case R.id.btnChannelShifterRight:
			startActivity(new Intent(ChannelShifterActivity.this, ResultActivity.class)); //
		break;
		}
		
	}

}
