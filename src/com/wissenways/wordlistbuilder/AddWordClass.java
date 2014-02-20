package com.wissenways.wordlistbuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddWordClass extends Activity implements OnClickListener {
	private static final String TAG = "WordlistBuilderActivity.java";
	/** Called when the activity is first created. */
	String wordString, meaningString, usageOneString, usageTwoString, synonymsString,
			antonymsString;
	EditText wordEditText, meaningEditText, usageOneEditText, usageTwoEditText, synonymsEditText,
			antonymsEditText; 
			
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_word_layout);

		wordEditText = (EditText) findViewById(R.id.wordedittext);
		meaningEditText = (EditText) findViewById(R.id.meaningedittext);
		usageOneEditText = (EditText) findViewById(R.id.usage1edittext);
		usageTwoEditText = (EditText) findViewById(R.id.usage2edittext);
		synonymsEditText = (EditText) findViewById(R.id.synonymsedittext);
		antonymsEditText = (EditText) findViewById(R.id.antonymsedittext);
				
		Button enterWordButton = (Button) findViewById(R.id.enterwordbutton);
		enterWordButton.setOnClickListener(this);

		Button dontenterButton = (Button) findViewById(R.id.dontenterbutton);
		dontenterButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.enterwordbutton:
			readFromEditText();
			DbHelper dbHelper = new DbHelper(this);
			SQLiteDatabase db = dbHelper.getReadableDataBase();
			String queryString = "SELECT * FROM " + this.getString(R.string.table_name);
			Cursor cursor = db.rawQuery(queryString, null);
			int idCurrent = cursor.getCount();
			cursor.close();
			db.close();
			db = dbHelper.getWriteableDataBase();
			String insertString = "INSERT INTO " + this.getString(R.string.table_name) + " VALUES("
					+ String.valueOf(idCurrent + 1) + ",'" + wordString + "','"
					+ meaningString + "','" + usageOneString + "','" + usageTwoString
					+ "','" + synonymsString + "','" + antonymsString + "'," + "'no')";
			Log.d(TAG, insertString);
			db.execSQL(insertString);
			db.close();
			break;
		case R.id.dontenterbutton:
			break;
		}
		Intent i = new Intent(AddWordClass.this, WordListTabWidget.class);
		startActivity(i);
		finish();
	}
	
	public void readFromEditText() {
		wordString = wordEditText.getText().toString();
		meaningString = meaningEditText.getText().toString();
		usageOneString = usageOneEditText.getText().toString();
		usageTwoString = usageTwoEditText.getText().toString();
		synonymsString = synonymsEditText.getText().toString();
		antonymsString = antonymsEditText.getText().toString();
	}
}
