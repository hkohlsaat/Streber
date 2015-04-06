package org.aweture.wonk.tmp;

import org.aweture.wonk.R;
import org.aweture.wonk.storage.DataContract;
import org.aweture.wonk.storage.DatabaseHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Activity extends android.app.Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tmp);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// WHEN DELETING THE TEST CODE HEREAFTER:
		// MAKE DATABASEHELPER CLASS DEFAULT AGAIN !!!!!!
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		SQLiteDatabase database = helper.getWritableDatabase();
		Cursor c = database.query("queries", null, null, null, null, null, null);
		writeCursor(c);
		c.close();
		database.close();
	}
	
	private void writeCursor(Cursor c) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.container);
		final int columnIndex = c.getColumnIndex(DataContract.TableEntry.COLUMN_QUERIED_NAME);
		
		while (c.moveToNext()) {
			String info = c.getString(columnIndex);
			TextView tv = new TextView(this);
			tv.setText(info);
			ll.addView(tv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}
}