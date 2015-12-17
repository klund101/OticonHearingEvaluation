package com.example.hearing_evaluation;

import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;


public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

    @Override
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        
        return original + " Hz";
    }
	
	
}