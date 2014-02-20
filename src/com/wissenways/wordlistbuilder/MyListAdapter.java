package com.wissenways.wordlistbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MyListAdapter extends SimpleCursorAdapter {
	private  static  final  String  TAG  =  "MyListAdapter.java";
	
	private final Context context;
	private final LayoutInflater inflater;
	private DbHelper dbHelper = null;

	public MyListAdapter (Context context, int layout, Cursor c,
			String[] from, int[] to, DbHelper dbHelper) {
		super(context, layout, c, from, to);

		this.context = context;
		//this.cursor = c;
		inflater = LayoutInflater.from(context);
		this.dbHelper = dbHelper;
	}

	static class ViewHolder	{
		TextView word;
		ImageButton removeButton;
		ImageButton editWordButton;
	}
	
	static class RemoveButtonHolder {
		String _id;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		//Log.d("Gaurav","Inside getView & position= " + String.valueOf(position));
		View view = null;
		
		
		if (convertView == null) {
			
			//Log.d("Gaurav",convertView == null");
			//Log.d("Gaurav","position= " + String.valueOf(position));
			
			view = inflater.inflate(R.layout.mylist_adapter, null);
			final ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.word = (TextView) view.findViewById(R.id.mylistword);
			viewHolder.removeButton = (ImageButton) view.findViewById(R.id.mylistremovebutton);
			viewHolder.removeButton.setFocusable(false); //MyList will not respond to clicks
			viewHolder.editWordButton = (ImageButton) view.findViewById(R.id.editwordButtonmylist);
			viewHolder.editWordButton.setFocusable(false);
			getCursor().moveToPosition(position);
			RemoveButtonHolder removeButtonHolder = new RemoveButtonHolder();
			removeButtonHolder._id = getCursor().getString(getCursor().getColumnIndex("_id"));
			viewHolder.removeButton.setTag(removeButtonHolder);
			view.setTag(viewHolder);
			
			try {
				viewHolder.removeButton.setOnClickListener(new OnClickListener()  {      
				    
				    @Override
					public void onClick(View v) {
				    	
				    	Log.d("Gaurav","Inside onClick");
						
				    	RemoveButtonHolder removeButtonHolder = new RemoveButtonHolder();
						
						try {
							removeButtonHolder = (RemoveButtonHolder) viewHolder.removeButton.getTag();
							//Log.d("Gaurav","removeButtonHolder._id= " + removeButtonHolder._id);
						} catch (Exception e) {
							Log.e("Gaurav", e.getMessage());
						}
						
													
						ContentValues cv=new ContentValues();
						cv.put("favorite", "no");
						int rowsEffected = dbHelper.updateDatabase(cv, removeButtonHolder._id );
						//Log.d("Gaurav","Number of rows effected=" + String.valueOf(rowsEffected));
						//frameHolder.myList = "yes";
						//Log.d("Gaurav",frameHolder.myList);
						//viewHolder.removeButton.setTag(frameHolder);
						
						//drop old cursor and find new cursor, this may cause ANR 
						getCursor().deactivate();
						boolean curBoo = getCursor().requery();
						//Log.d("Gaurav","Cursor Requery= " + curBoo);
					} //onclick closes
				});
			} catch (Exception e) {
				Log.e("Gaurav",e.getMessage());
			}
			viewHolder.editWordButton.setOnClickListener(new OnClickListener()  {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(v.getContext(), EditWordClass.class);
					Bundle bundle = new Bundle();
					bundle.putInt("position", position);
					i.putExtras(bundle);
					v.getContext().startActivity(i);
				}
			});
				
		} else	{
			
			//Log.d("Gaurav","converView != null");
			view = convertView;
			getCursor().moveToPosition(position);
			RemoveButtonHolder removeButtonHolder = new RemoveButtonHolder();
			removeButtonHolder._id = getCursor().getString(getCursor().getColumnIndex("_id"));
			((ViewHolder) view.getTag()).removeButton.setTag(removeButtonHolder);
		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		getCursor().moveToPosition(position);
		
		Typeface externalFont = Typeface.createFromAsset(context.getAssets(), "fonts/sansus.ttf");
		holder.word.setTypeface(externalFont);
		holder.word.setTextSize(24.f);
		holder.word.setText(getCursor().getString(getCursor().getColumnIndex("word")));
				
		return view;
		
	}

}	
