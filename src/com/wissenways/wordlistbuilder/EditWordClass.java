package com.wissenways.wordlistbuilder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditWordClass extends Activity implements OnClickListener {
	
	private static final String TAG = "EditTextClass";

	String wordString, meaningString, usageOneString, usageTwoString, synonymsString,
			antonymsString;
	int position;
	
	EditText wordEditText, meaningEditText, usageOneEditText, usageTwoEditText, synonymsEditText,
	antonymsEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_word_layout);

		Bundle bundle = getIntent().getExtras();
		position = (Integer) bundle.get("position");
		Log.d(TAG, "position= " + String.valueOf(position));
		DbHelper dbHelper = new DbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String queryString = "SELECT * FROM " + this.getString(R.string.table_name)
				+ " WHERE _id=" + String.valueOf(position + 1);
		Cursor cursor = db.rawQuery(queryString, null);

		Row row = new Row();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			row.word = cursor.getString(cursor.getColumnIndex("word"));
			row.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
			row.usageOne = cursor.getString(cursor.getColumnIndex("usageone"));
			row.usageTwo = cursor.getString(cursor.getColumnIndex("usagetwo"));
			row.synonyms = cursor.getString(cursor.getColumnIndex("synonyms"));
			row.antonyms = cursor.getString(cursor.getColumnIndex("antonyms"));
			row.favourite = cursor.getString(cursor.getColumnIndex("favorite"));
		}

		db.close();
		cursor.close();

		wordEditText = (EditText) findViewById(R.id.wordedittexteditword);
		wordEditText.setText(row.word, TextView.BufferType.EDITABLE);
		
		meaningEditText = (EditText) findViewById(R.id.meaningedittexteditword);
		meaningEditText.setText(row.meaning, TextView.BufferType.EDITABLE);
				
		usageOneEditText = (EditText) findViewById(R.id.usage1edittexteditword);
		usageOneEditText.setText(row.usageOne, TextView.BufferType.EDITABLE);
		
		usageTwoEditText = (EditText) findViewById(R.id.usage2edittexteditword);
		usageTwoEditText.setText(row.usageTwo, TextView.BufferType.EDITABLE);
		
		synonymsEditText = (EditText) findViewById(R.id.synonymsedittexteditword);
		synonymsEditText.setText(row.synonyms, TextView.BufferType.EDITABLE);
		
		antonymsEditText = (EditText) findViewById(R.id.antonymsedittexteditword);
		antonymsEditText.setText(row.antonyms, TextView.BufferType.EDITABLE);
		
		Button editWordButton = (Button) findViewById(R.id.editwordbuttoneditword);
		editWordButton.setOnClickListener(this);

		Button donteditButton = (Button) findViewById(R.id.dontenterbuttoneditword);
		donteditButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editwordbuttoneditword:
			readFromEditText();
			DbHelper dbHelper = new DbHelper(this);
			SQLiteDatabase db = dbHelper.getWriteableDataBase();
			String updateString = "UPDATE " + this.getString(R.string.table_name)
					+ " SET word='" + wordString + "',meaning='" + meaningString
					+ "',usageone='" + usageOneString + "',usagetwo='" + usageTwoString
					+ "',synonyms='" + synonymsString + "',antonyms='" + antonymsString
					+ "' WHERE _id=" + String.valueOf(position + 1);

			Log.d(TAG, updateString);
			db.execSQL(updateString);
			db.close();
			break;
		case R.id.dontenterbutton:
			break;
		}
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
