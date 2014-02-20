package com.wissenways.wordlistbuilder;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class WordListTabWidget extends TabActivity implements OnTabChangeListener,
		AdapterView.OnItemClickListener, OnClickListener {
	public static final String TAG = "WordListTabWidget";
	private static final int REQUEST_CODE = 10;
	
	// The two views in our tabbed example
	private ListView completeListView;
	private ListView myListView;
	private TabHost tabHost;
	Cursor completeListCursor = null;
	Cursor myListCursor = null;
	SQLiteDatabase sql = null;
	DbHelper dbHelper = null;
	CompleteListAdapter completeListAdapter = null;
	MyListAdapter myListAdapter = null;
	Dialog addWordRemainderDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wordlisttabwidget_main);

		tabHost = getTabHost();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		tabHost.setOnTabChangedListener(this);

		// getReadable database
		dbHelper = new DbHelper(this);
		sql = dbHelper.getReadableDataBase();

		// query result will be whole database for completeListAdapter
		String queryString = "SELECT * FROM " + this.getString(R.string.table_name);
		completeListCursor = sql.rawQuery(queryString, null);
		startManagingCursor(completeListCursor); // this method is deprecated
		Log.d(TAG, "Cursor Set");

		// set adapter for completeListView
		int[] completeListTo = new int[] { R.id.completelistword };
		
		completeListAdapter = new CompleteListAdapter(this,
				R.layout.completelist_adapter, completeListCursor, new String[] { "word",
						"_id", "meaning", "usageone", "usagetwo", "synonyms", "antonyms", "favorite" },
						completeListTo, dbHelper);
		completeListView = (ListView) findViewById(R.id.completelistview);
		completeListView.setAdapter(completeListAdapter);

		// add onclicklistener for completeListView
		completeListView.setOnItemClickListener(this);
				
		myListView = (ListView) findViewById(R.id.mylistview);

		// query for myListAdapter on MyList="yes" only
		queryString = "SELECT * FROM " + this.getString(R.string.table_name)
				+ " WHERE favorite='yes'";
		myListCursor = sql.rawQuery(queryString, null);
		startManagingCursor(myListCursor);

		// set adapter for MyListView
		int[] myListTo = new int[] { R.id.mylistword };
		myListAdapter = new MyListAdapter(this, R.layout.mylist_adapter, myListCursor, new String[] { "word",
				"_id", "meaning", "usageone", "usagetwo", "synonyms", "antonyms", "favorite" },
				 myListTo, dbHelper);
		myListView.setAdapter(myListAdapter);

		// add click listener for MyListView

		myListView.setOnItemClickListener(this);

		// add views to tab host
		
		tabHost.addTab(tabHost.newTabSpec(this.getString(R.string.complete_list))
				.setIndicator(createTabView(this, this.getString(R.string.complete_list))).setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return completeListView;
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(this.getString(R.string.my_list)).setIndicator(createTabView(this, this.getString(R.string.my_list)))
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return myListView;
					}
				}));
		tabHost.setCurrentTabByTag(this.getString(R.string.my_list));
		tabHost.setCurrentTabByTag(this.getString(R.string.complete_list));
		
		addWordRemainderDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		addWordRemainderDialog.setContentView(R.layout.addwordremainder_dialog);
	       
        ImageView addWordRemainderImage = (ImageView) addWordRemainderDialog.findViewById(R.id.addwordremainderImage);
        addWordRemainderImage.setImageResource(R.drawable.addwordremainder);
        
        addWordRemainderDialog.show();
        
        addWordRemainderImage.setOnClickListener(this);
		
	}
	
	
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_text, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		//tv.setTypeface(null, Typeface.BOLD);
		tv.setText(text);
		return view;
	}
		
	//Implement logic here when a tab is selected
	public void onTabChanged(String tabName) {
		if (tabName.equals(this.getString(R.string.my_list))) {
			// let cursor do requery eveytime tab is changed
			myListCursor.deactivate();
			myListCursor.requery();

		} else if (tabName.equals(this.getString(R.string.complete_list))) {
			// let cursor do requery eveytime tab is changed
			completeListCursor.deactivate();
			completeListCursor.requery();

		}
	}

	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("returnKey1")) {
				tabHost.setCurrentTabByTag(data.getExtras().getString("returnKey1"));
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addword:
			Intent i = new Intent(WordListTabWidget.this, AddWordClass.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "OnDestroy Called");
		super.onDestroy();
		dbHelper.close();
		// sql.close();
		myListCursor.close();
		completeListCursor.close();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		Log.d(TAG, "Inside onListItemClick_id= " + String.valueOf(id));
		Intent i = new Intent(WordListTabWidget.this, WordDetailActivity.class);
		i.putExtra("id", id);
		startActivityForResult(i, REQUEST_CODE);
	}


	@Override
	public void onClick(View v) {
		addWordRemainderDialog.dismiss();
	}
}
