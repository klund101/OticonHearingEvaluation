package com.example.hearing_evaluation;

import java.text.DecimalFormat;

public class YvalueCustomFormatter implements com.github.mikephil.charting.utils.ValueFormatter{

	private DecimalFormat mFormat;
	
	public YvalueCustomFormatter() {
		mFormat = new DecimalFormat("###,###,###"); // use one decimal
	}
	

	@Override
	public String getFormattedValue(float value) {
		// TODO Auto-generated method stub
		if(value == 0)
			return mFormat.format(value) + " dB";
		else
			return mFormat.format(value*(-1)) + " dB"; //  format the y values and append " dB"
		//return null;

	}
	
	
}
