package com.example.hearing_evaluation;

import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;

/**
 * Created by Philipp Jahoda on 14/09/15.
 */
public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

    @Override
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        // original is the original value to use, x-index is the index in your x-values array
        return original + " Hz";
    }
	
	
}