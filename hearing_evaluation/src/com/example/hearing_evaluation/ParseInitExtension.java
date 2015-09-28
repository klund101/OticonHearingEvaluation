package com.example.hearing_evaluation;

import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;
import android.util.Log;

public class ParseInitExtension extends Application{
	public void onCreate() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "GTBzREvZ0AX9qSeAnMQVW0qVSKqwCULDoHBNJ1GF", "zNjkcwc6jMffc6ABOQkCY0vXrVkxJDaKwuxHF0yI");
        Log.d("init parse", "init parse");
	}
}
