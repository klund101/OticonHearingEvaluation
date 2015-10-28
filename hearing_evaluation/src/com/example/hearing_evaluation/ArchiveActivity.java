package com.example.hearing_evaluation;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ArchiveActivity extends ListActivity {
	
	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<String> lineList = new ArrayList<String>();
	
	public List<ParseObject> publicParseObjectsList;
	public String parseListObjectId;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive);
		
		//Parse
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("hearingEvaluationData");
		query.whereEqualTo("GoogleId", MainActivity.staticEmailId);
		adapter = new ArrayAdapter<String>(this, R.layout.custom_textview, listItems);
        setListAdapter(adapter);
		query.findInBackground(new FindCallback<ParseObject>() {	        
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					publicParseObjectsList = objects;
					for(int i = objects.size()-1; i>=0; i--){
			        listItems.add("\n" + objects.get(i).get("Username").toString() + ", " + objects.get(i).get("timeAndDate").toString() + "\n");
					Log.d("object", objects.get(i).get("Username").toString());
			        adapter.notifyDataSetChanged();
					}
		            
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
				
			}
		});
				
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
		Log.d("position", publicParseObjectsList.get(publicParseObjectsList.size()-1-position).getObjectId().toString());
		Intent archiveResultA = new Intent(ArchiveActivity.this, ResultActivity.class);
		archiveResultA.putExtra("pressedObjectId", publicParseObjectsList.get(publicParseObjectsList.size()-1-position).getObjectId().toString());
        startActivity(archiveResultA);
    }

}
