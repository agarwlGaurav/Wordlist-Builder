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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CompleteListAdapter extends SimpleCursorAdapter {
	
	private final Context context;
	private final LayoutInflater inflater;
	private DbHelper dbHelper = null;

	
	public CompleteListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, DbHelper dbHelper) {
		super(context, layout, c, from, to);

		this.context = context;
		inflater = LayoutInflater.from(context);
		this.dbHelper = dbHelper;
		
	}

	static class ViewHolder	{
		TextView word;
		ImageView removeView;
		FrameLayout addremoveFrameLayout;
		ImageButton editWordButton;
	}
	
	static class FrameHolder {
		String favorite;
		String _id;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = null;
		if (convertView == null) {
			
			view = inflater.inflate(R.layout.completelist_adapter, null);
			final ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.word = (TextView) view.findViewById(R.id.completelistword);
			viewHolder.removeView = (ImageView) view.findViewById(R.id.removefrommylist);
			viewHolder.addremoveFrameLayout = (FrameLayout) view.findViewById(R.id.addremoveframelayout);
			viewHolder.editWordButton = (ImageButton) view.findViewById(R.id.editwordButtonCompleteList);
			viewHolder.editWordButton.setFocusable(false);
			getCursor().moveToPosition(position);
			FrameHolder frameHolder = new FrameHolder();
			frameHolder.favorite = getCursor().getString(getCursor().getColumnIndex("favorite"));
			frameHolder._id = getCursor().getString(getCursor().getColumnIndex("_id"));
			viewHolder.addremoveFrameLayout.setTag(frameHolder);
			view.setTag(viewHolder);
			try {
				viewHolder.addremoveFrameLayout.setOnClickListener(new OnClickListener()  {      
				    @Override
					public void onClick(View v) {
						FrameHolder frameHolder = new FrameHolder();
						
						try {
							frameHolder = (FrameHolder) viewHolder.addremoveFrameLayout.getTag();
							//Log.d("Gaurav","frameHolder.myList= " + frameHolder.myList);
							//Log.d("Gaurav","frameHolder._id= " + frameHolder._id);
						} catch (Exception e) {
							Log.e("Gaurav", e.getMessage());
						}
						if(frameHolder.favorite.equals("no"))	{
							
							//Log.d("Gaurav","Inside if");
							
							
							ContentValues cv=new ContentValues();
							cv.put("favorite", "yes");
							int rowsEffected = dbHelper.updateDatabase(cv, frameHolder._id);
							
							//Log.d("Gaurav","Number of rows effected=" + String.valueOf(rowsEffected));
							
							frameHolder.favorite = "yes";
							//Log.d("Gaurav",frameHolder.myList);
							viewHolder.addremoveFrameLayout.setTag(frameHolder);
							getCursor().moveToPosition(position);
							//Log.e("Gaurav","Cursor= " + 
							//		getCursor().getString(getCursor().getColumnIndex("MyList")) + "at pos= " + String.valueOf(position));
							
							//drop old cursor and find new cursor, this may cause ANR 
							getCursor().deactivate();
							boolean curBoo = getCursor().requery();
							//Log.d("Gaurav","Cursor Requery= " + curBoo);
							
						}	else	{
							
							//Log.d("Gaurav","Inside else");
							
							ContentValues cv=new ContentValues();
							cv.put("favorite", "no");
							int rowsEffected = dbHelper.updateDatabase(cv, frameHolder._id);
							
							//Log.d("Gaurav","Number of rows effected=" + String.valueOf(rowsEffected));
							frameHolder.favorite = "no";
							viewHolder.addremoveFrameLayout.setTag(frameHolder);
							getCursor().moveToPosition(position);
							//Log.e("Gaurav","Cursor= " + 
							//		getCursor().getString(getCursor().getColumnIndex("favorite")) + "at pos= " + String.valueOf(position));
							
							
							//drop old cursor and use new cursor, this may cause ANR
							getCursor().deactivate();
							boolean curBoo = getCursor().requery();
							//Log.d("Gaurav","Cursor Requery= " + curBoo);
						}
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
			
			Log.d("Gaurav","converView != null");
			view = convertView;
			getCursor().moveToPosition(position);
			FrameHolder frameHolder = new FrameHolder();
			frameHolder.favorite = getCursor().getString(getCursor().getColumnIndex("favorite"));
			frameHolder._id = getCursor().getString(getCursor().getColumnIndex("_id"));
			((ViewHolder) view.getTag()).addremoveFrameLayout.setTag(frameHolder);
		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		getCursor().moveToPosition(position);
		//setting external font from asset folder
		Typeface externalFont = Typeface.createFromAsset(context.getAssets(), "fonts/sansus.ttf");
		holder.word.setTypeface(externalFont);
		holder.word.setTextSize(24.f);
		
		holder.word.setText(getCursor().getString(getCursor().getColumnIndex("word")));
		
		
		//cursors are not updates automatically if underlying data is changed
		FrameHolder frameHolder = new FrameHolder();
		frameHolder = (FrameHolder) holder.addremoveFrameLayout.getTag();
		
		//Log.d("Gaurav","Position= " + String.valueOf(position) + "frameHolder._id= " 
		//		+ frameHolder._id + ".myList= " + frameHolder.myList);
				
		
		if (frameHolder.favorite.equals("no")) {
			holder.removeView.setVisibility(View.INVISIBLE);
		}
		else holder.removeView.setVisibility(View.VISIBLE);
		return view;
	}
}	
