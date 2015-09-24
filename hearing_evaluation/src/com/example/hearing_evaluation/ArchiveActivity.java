package com.example.hearing_evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ArchiveActivity extends ListActivity {
	
	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<String> lineList = new ArrayList<String>();
	private String readDataName;
	private String dBValues;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive);
		
		//-----READ FROM TEXT FILE
		try {  
	        File pathToExternalStorage = Environment.getExternalStorageDirectory();
	        File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/Oticon");        
	        //Create a File for the output file data
	        File saveFilePath = new File (appDirectory, "OticonAppData.txt");
	        FileInputStream fIn = new FileInputStream(saveFilePath);  
	        BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));  
	        String line = ""; 
	        while ((line = myReader.readLine()) != null) {
	        	lineList.add(line);
	        }        
	        
	        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
	        setListAdapter(adapter);
	        
	        for(int i=lineList.size()-1; i>0; i = i-2){	// Set archive item names from text file
	        	readDataName = lineList.get(lineList.size()-i);	
		        listItems.add(readDataName);
		        adapter.notifyDataSetChanged();
	        }
	        myReader.close();
	          
	    } catch (IOException e) {  
	        e.printStackTrace();  
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
	
	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("position", Integer.toString(position));
		Log.d("username", lineList.get((position*2)+1));
		Intent archiveResultA = new Intent(ArchiveActivity.this, ArchiveResultActivity.class);
		archiveResultA.putExtra("namePos", Integer.toString(((position*2)+1)));
		archiveResultA.putExtra("hearingDataPos", Integer.toString((position*2)+2));
        startActivity(archiveResultA);
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.archive, menu);
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
