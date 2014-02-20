package com.wissenways.wordlistbuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class WordDetailActivity extends Activity implements OnClickListener {
	private static final String TAG = "WordDetailActivity";
		
	Cursor c = null;
	TextView wordDetail = null;
	ImageButton addToMyListButton = null;
	ImageButton removefromMyListButton = null;
	ListView listContent;
	SimpleCursorAdapter cursorAdapter;
	String returnList;
	private long id = 0;
	Row row;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.worddetail_main);

		// START getting information from CompleteorMylist activity
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		id = extras.getLong("id");
		Log.d(TAG, String.valueOf(id));
		
		addToMyListButton = (ImageButton) findViewById(R.id.worddetailaddtomylist);
		addToMyListButton.setOnClickListener(this);

		removefromMyListButton = (ImageButton) findViewById(R.id.worddetailremovefrommylist);
		removefromMyListButton.setOnClickListener(this);
		
		ImageButton editButton = (ImageButton) findViewById(R.id.editwordButtonworddetail);
		editButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		
		DbHelper dbHelper = new DbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDataBase();
		
		String queryString = "SELECT * FROM " + this.getString(R.string.table_name)
				+ " WHERE _id=" + String.valueOf(id);
		Log.d(TAG, "query=" + queryString);
		
		Cursor cursor = db.rawQuery(queryString, null);
		
		row = new Row();
		if(cursor.getCount() > 0) {
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
		
		//set the font from assets folder
		Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/sansus.ttf");
		
		TextView word = (TextView) findViewById(R.id.wordworddetail);
		word.setTypeface(titleFont);
		word.setTextSize(34.f); //Set the default text size to the given value, interpreted as "scaled pixel" units(android documentation)
		word.setText(row.word);
		
		Typeface detailsFont = Typeface.createFromAsset(getAssets(), "fonts/rupee.ttf");
		
		TextView meaning = (TextView) findViewById(R.id.meaningworddetail);
		meaning.setTypeface(detailsFont);
		meaning.setTextSize(16.f);
		meaning.setText(row.meaning);
		
		TextView usageOne = (TextView) findViewById(R.id.usageoneworddetail);
		usageOne.setTypeface(detailsFont);
		usageOne.setTextSize(16.f);
		usageOne.setText(row.usageOne);
		
		TextView usageTwo = (TextView) findViewById(R.id.usagetwoworddetail);
		usageTwo.setTypeface(detailsFont);
		usageTwo.setTextSize(16.f);
		usageTwo.setText(row.usageTwo);
		
		TextView synonyms = (TextView) findViewById(R.id.synonymsworddetail);
		synonyms.setTypeface(detailsFont);
		synonyms.setTextSize(16.f);
		synonyms.setText(row.synonyms);
		
		TextView antonyms = (TextView) findViewById(R.id.antonymsworddetail);
		antonyms.setTypeface(detailsFont);
		antonyms.setTextSize(16.f);
		antonyms.setText(row.antonyms);
		
		if (row.favourite.equals("no")) {
			addToMyListButton.setVisibility(View.VISIBLE);
			removefromMyListButton.setVisibility(View.INVISIBLE);
		} else {
			removefromMyListButton.setVisibility(View.VISIBLE);
			addToMyListButton.setVisibility(View.INVISIBLE);
		}
				
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		ContentValues cv=new ContentValues();
		DbHelper dbHelper = new DbHelper(this);
		
		switch (v.getId()) {
		case R.id.worddetailaddtomylist:
			Log.d(TAG,"worddetailaddtomylist");
			removefromMyListButton.setVisibility(View.VISIBLE);
			addToMyListButton.setVisibility(View.INVISIBLE);
			cv.put("favorite", "yes");
			int rowsEffected = dbHelper.updateDatabase(cv,String.valueOf(id));
			dbHelper.close();
			break;
		case R.id.worddetailremovefrommylist:
			Log.d(TAG,"worddetailremovefrommylist");
			addToMyListButton.setVisibility(View.VISIBLE);
			removefromMyListButton.setVisibility(View.INVISIBLE);
			cv.put("favorite", "no");
			rowsEffected = dbHelper.updateDatabase(cv, String.valueOf(id));
			dbHelper.close();
			break;
		case R.id.editwordButtonworddetail:
			Intent i = new Intent(WordDetailActivity.this, EditWordClass.class);
			Bundle bundle = new Bundle();
			bundle.putInt("position",(int) (id-1));
			i.putExtras(bundle);
			startActivity(i);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addword:
			Intent i = new Intent(WordDetailActivity.this, AddWordClass.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		data.putExtra("returnKey1", returnList);
		setResult(RESULT_OK, data);
		super.finish();
	}


	
		
}

