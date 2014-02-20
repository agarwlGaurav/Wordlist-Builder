package com.wissenways.wordlistbuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class StartActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean dialogShown = settings.getBoolean("dialogShown", false);

		if (!dialogShown) {
		// AlertDialog code here
			SharedPreferences.Editor editor = settings.edit();
		    editor.putBoolean("dialogShown", true);
		    editor.commit(); 
		    Intent i = new Intent(StartActivity.this, AddWordClass.class);
		    startActivity(i);
		} else {
			Intent i = new Intent(StartActivity.this, WordListTabWidget.class);
		    startActivity(i);
		}
	}
	
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

}
	


