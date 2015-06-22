package org.aweture.wonk.storage;

import org.aweture.wonk.background.UpdateScheduler;
import org.aweture.wonk.storage.DataContract.LogColumns;
import org.aweture.wonk.storage.DataContract.SubjectsColumns;
import org.aweture.wonk.storage.DataContract.SubstitutionColumns;
import org.aweture.wonk.storage.DataContract.TableColumns;
import org.aweture.wonk.storage.DataContract.TeachersColumns;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "wonk.db";
	private static final int DATABASE_VERSION = 4;
	
	private Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		CreateQuery createTables = new CreateQuery(TableColumns.TABLE_NAME);
		for (TableColumns column : TableColumns.values()) {
			createTables.addColumn(column.name(), column.type());
		}
		db.execSQL(createTables.toString());
		
		CreateQuery createLog = new CreateQuery(LogColumns.TABLE_NAME);
		for (LogColumns column : LogColumns.values()) {
			createLog.addColumn(column.name(), column.type());
		}
		db.execSQL(createLog.toString());

		createTeachersTable(db);
		createSubjectsTable(db);
	}
	
	private void createTeachersTable(SQLiteDatabase db) {
		CreateQuery createTeachers = new CreateQuery(TeachersColumns.TABLE_NAME);
		for (TeachersColumns column : TeachersColumns.values()) {
			createTeachers.addColumn(column.name(), column.type());
		}
		db.execSQL(createTeachers.toString());
	}
	
	private void createSubjectsTable(SQLiteDatabase db) {
		CreateQuery createSubjects = new CreateQuery(SubjectsColumns.TABLE_NAME);
		for (SubjectsColumns column : SubjectsColumns.values()) {
			createSubjects.addColumn(column.name(), column.type());
		}
		db.execSQL(createSubjects.toString());
	}
	
	public void resetDatabase(SQLiteDatabase db) {
		String tableName = TableColumns.TABLE_NAME;
		Cursor plansCursor = db.query(tableName, null, null, null, null, null, null);
		
		final int dateIndex = plansCursor.getColumnIndexOrThrow(TableColumns.DATE.name());
		
		while (plansCursor.moveToNext()) {
			String substitutionsTableName = "\"" + plansCursor.getString(dateIndex) + SubstitutionColumns.STUDENT_SUFIX + "\"";
			db.execSQL("DROP TABLE IF EXISTS " + substitutionsTableName);
			substitutionsTableName = "\"" + plansCursor.getString(dateIndex) + SubstitutionColumns.TEACHER_SUFIX + "\"";
			db.execSQL("DROP TABLE IF EXISTS " + substitutionsTableName);
		}
		
		plansCursor.close();
		db.delete(tableName, null, null);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 3) {
			dropPreVersion3Tables(db);
			onCreate(db);
		} else if (oldVersion == 3) {
			createTeachersTable(db);
			createSubjectsTable(db);
			resetDatabase(db);
			new UpdateScheduler(context).updateNow();
		} else {
			throwBecauseOldVersionNotHandled(oldVersion);
		}
	}
	
	private void dropPreVersion3Tables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS substitutes");
		db.execSQL("DROP TABLE IF EXISTS timetable");
	}
	
	private void throwBecauseOldVersionNotHandled(int oldVersion) {
		String message = "Old version " + oldVersion + " is not handled."; 
		RuntimeException exception = new RuntimeException(message);
		throw exception;
	}
}