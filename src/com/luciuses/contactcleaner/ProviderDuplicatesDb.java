package com.luciuses.contactcleaner;

import java.util.ArrayList;
import java.util.Arrays;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.basis.DbHelper;
import com.luciuses.contactcleaner.components.StepType;
import com.luciuses.contactcleaner.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class ProviderDuplicatesDb{
	 
	private SQLiteDatabase sdb;
	private DbHelper dbHelper;
	
	public ProviderDuplicatesDb(Context context) {
		dbHelper = new DbHelper(context, "database7.db", null, 1);
		sdb = dbHelper.getReadableDatabase();
	}


	public int getCountAllSourses() {		
		return getCursorSourses().getCount();
	}


	public Duplicates getByPosition(int position) {
		Cursor cursor = getCursorSourses();
		cursor.moveToPosition(position);
		Duplicates duplicatesCont = getDuplicatesByCursor(cursor);		
		return duplicatesCont;
	}

	public void Save(Duplicates duplicates) {
		ContentValues newValues = PutInContent(duplicates);
		sdb.insert(DbHelper.DATABASE_TABLE, null, newValues);		
	}

	public int DuplicatesDelete(String sourse) {
		int count = 0;
		Cursor cursor = getCursorBySourse(sourse);
		cursor.moveToFirst();
		Integer id = getIdByCursor(cursor);
			if (id!=null) 
				try {
					count = sdb.delete(DbHelper.DATABASE_TABLE, DbHelper._ID + "=" + id, null);
				} catch (Exception e) {
					return 0;
				}
		return count;
	}
	
private Cursor getCursorBySourse(String sourse) {
		
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, null,
				DbHelper.SOURSE+" = ?", new String[] { sourse }, null, null, null);
		return cursor;
	}

	public void Clean() {
		sdb.delete(DbHelper.DATABASE_TABLE, null, null);
	}	
	public String[] getSourses() {
		Cursor cursor = getCursorSourses();
		cursor.moveToFirst();
		String[] sourses = new String[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {			
			sourses[i] = getSourseByCursor(cursor);
		}
		cursor.close();
		return sourses;
	}
	private Cursor getCursorSourses() {
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, null, null, null, null,
				null, DbHelper.SOURSE);
		return cursor;
	}	
	private Duplicates getDuplicatesByCursor(Cursor cursor) {
		
		String idDuplicatesStr;
		String sourse;
		SourseType type;
		String where;
		try {
			type = SourseType.values()[cursor.getInt(cursor.getColumnIndex(DbHelper.SOURSE_TYPE))];
			sourse = cursor.getString(cursor.getColumnIndex(DbHelper.SOURSE));
			where = cursor.getString(cursor.getColumnIndex(DbHelper.REGION));
			idDuplicatesStr = cursor.getString(cursor.getColumnIndex(DbHelper.ID_DUBLICATES));
		} catch (Exception e) {
			return null;
		}		
		String[] idDuplicates = idDuplicatesStr.split(" ");
		return new Duplicates(type, sourse, where, idDuplicates );
	}

	private String getSourseByCursor(Cursor cursor) {
		String sourse;
		try {
			sourse = cursor.getString(cursor.getColumnIndex(DbHelper.SOURSE));
		} catch (Exception e) {
			return null;
		}
		return sourse;
	}
	private ContentValues PutInContent(Duplicates duplicates) {
		ContentValues newValues = new ContentValues();
		newValues.put(DbHelper.SOURSE_TYPE, duplicates.getType().ordinal());
		newValues.put(DbHelper.SOURSE, duplicates.getSourse());
		newValues.put(DbHelper.REGION, duplicates.getWhere());		
		String[] idDubls = duplicates.getIdDuplicates();
		if (idDubls != null) {			
			String idDubslStr = TextUtils.join(" ", idDubls);				
			newValues.put(DbHelper.ID_DUBLICATES, idDubslStr);
		}
		return newValues;
	}
	private Integer getIdByCursor(Cursor cursor) {
		Integer id;
        int idColIndex;
		try {
			idColIndex = cursor.getColumnIndex(DbHelper._ID);
			id = cursor.getInt(idColIndex);
		} catch (Exception e) {
			return null;
		}		     		
		return id;
	}


	public Duplicates getDuplicatesBySourse(String sourse) {
		Cursor cursor = getCursorBySourse(sourse);
		Duplicates duplicates = getDuplicatesByCursor(cursor);
		return duplicates;
	}
}