package com.wissenways.wordlistbuilder;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = "DbHelper";
	private static final String DB_NAME = "WordlistBuilder";
	private static final int DB_VER = 1;
	String DB_PATH;
	String TABLE_NAME;
	SQLiteDatabase myDatabase;
	Context context;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		this.context = context;
		this.DB_PATH = "/data/data/" + FindPackageName() + "/databases/";
		this.TABLE_NAME = context.getString(R.string.table_name);
		Log.d(TAG, "DB_PATH=" + DB_PATH);
		try {
			createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to Create Database");
		}
	}

	public void createDataBase() throws IOException {
		Log.d(TAG, "Inside CreateDatabase");
		boolean dbExist = checkDataBase();
		Log.d(TAG, "Database Exists= " + String.valueOf(dbExist));
		if (dbExist) {
			// do nothing - database already exist
		} else {

			// this method empty db will create empty db into the default system
			// path of your application later we can overwrite that db
			getReadableDatabase();
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			Log.d(TAG, "DBPATH+DBNAME=" + myPath);
			checkDB = SQLiteDatabase.openDatabase(myPath, null,	SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String dbCreate = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " (_id integer PRIMARY KEY AUTOINCREMENT, "
				+ "word text NOT NULL, meaning text NOT NULL, usageone text NOT NULL, usagetwo text NOT NULL,"
				+ " synonyms text NOT NULL, antonyms text NOT NULL, favorite text NOT NULL)";
		db.execSQL(dbCreate);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public SQLiteDatabase getReadableDataBase() throws SQLException {

		// Open the database
		Log.d(TAG, "Inside openDataBase");
		String myPath = DB_PATH + DB_NAME;
		return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	public SQLiteDatabase getWriteableDataBase() throws SQLException {

		// Open the database
		Log.d(TAG, "Inside openDataBase");
		String myPath = DB_PATH + DB_NAME;
		return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	public int updateDatabase(ContentValues cv, String id) {
		myDatabase = getWriteableDataBase();
		return myDatabase.update(TABLE_NAME, cv, "_id=" + id, null);

	}

	@Override
	public synchronized void close() {
		Log.d(TAG, "Inside close()");
		if (myDatabase != null)
			myDatabase.close();
		super.close();

	}
	
	public String FindPackageName() {
		// START code to ascertain package name
		String packageName = null;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			packageName = info.packageName;
		} catch (Exception e1) {
			e1.printStackTrace();
			Log.d(TAG, "Package_Name_Not_Found");
		}
		// END code to ascertain package name
		return packageName;
	}
}
