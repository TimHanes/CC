package com.luciuses.contactcleaner.providers;

import java.util.ArrayList;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.basis.DbHelper;
import com.luciuses.contactcleaner.models.Dublicates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class DbProvider {	
	private SQLiteDatabase sdb;
	private DbHelper dbHelper;
	private Functions uriFunctions = new Functions();

	public DbProvider(Context context) {
		dbHelper = new DbHelper(context, "database2.db", null, 1);
		sdb = dbHelper.getReadableDatabase();
	}	
	
	public void Save(Dublicates dublsOfCont) {
		ContentValues newValues = PutInContent(dublsOfCont);
		sdb.insert("UriDublicatesOfContact", null, newValues);
	}

	public Dublicates Read(Uri uri) {
		Cursor cursor = getCursorByUri(uri);
		cursor.moveToFirst();
		Dublicates dublsCont = getDublicatesOfContactByCursor(cursor);
		cursor.close();
		return dublsCont;
	}

	public Uri[] getContactsUri() {
		Cursor cursor = getCursorContacts();
		cursor.moveToFirst();
		Uri[] contactUris = new Uri[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {			
			contactUris[i] = getUriByCursor(cursor);
		}
		cursor.close();
		return contactUris;
	}

	public void Clean() {
		sdb.delete(DbHelper.DATABASE_TABLE, null, null);
	}
	
	public int ContactDelete(Uri uri) {
		int count = 0;
		Cursor cursor = getCursorByUri(uri);
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
	
	private Integer getIdByCursor(Cursor cursor) {		     
        int idColIndex;
		try {
			idColIndex = cursor.getColumnIndex(DbHelper._ID);
		} catch (Exception e) {
			return null;
		}
		Integer id = cursor.getInt(idColIndex);     		
		return id;
	}

	private Uri getUriByCursor(Cursor cursor) {
		Uri uri;
		try {
			uri = Uri.parse(cursor.getString(cursor.getColumnIndex(DbHelper.CONTACT_URI)));
		} catch (Exception e) {
			return null;
		}
		return uri;
	}
	
	private ContentValues PutInContent(Dublicates dublsOfCont) {
		ContentValues newValues = new ContentValues();
		newValues.put(DbHelper.CONTACT_URI, dublsOfCont.getContactUri().toString());
		
		newValues.put(DbHelper.OPTIONS, dublsOfCont.getOptions());

		Uri[] urisDublArr = dublsOfCont.getUriDublicatesByName();
		if (urisDublArr != null) {
			String urisDublStr = uriFunctions.UriSerialization(urisDublArr);
			newValues.put(DbHelper.URI_DUBLICATES_BY_NAME, urisDublStr);
		}

		urisDublArr = dublsOfCont.getUriDublicatesByPhone();
		if (urisDublArr != null) {
			String urisDublStr = uriFunctions.UriSerialization(urisDublArr);
			newValues.put(DbHelper.URI_DUBLICATES_BY_PHONE, urisDublStr);
		}
		return newValues;
	}

	private Cursor getCursorByUri(Uri uri) {
		
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE,
				new String[] { DbHelper._ID, DbHelper.CONTACT_URI, DbHelper.OPTIONS, DbHelper.URI_DUBLICATES_BY_NAME,
						DbHelper.URI_DUBLICATES_BY_PHONE },
				"contactUri = ?", new String[] { uri.toString() }, null, null, null);
		return cursor;
	}

	private Dublicates getDublicatesOfContactByCursor(Cursor cursor) {
		Uri contactUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DbHelper.CONTACT_URI)));
		int options = cursor.getInt(cursor.getColumnIndex(DbHelper.OPTIONS));
		String DublicatesByName = cursor.getString(cursor.getColumnIndex(DbHelper.URI_DUBLICATES_BY_NAME));
		String DublicatesByPhone = cursor.getString(cursor.getColumnIndex(DbHelper.URI_DUBLICATES_BY_PHONE));
		Uri[] uriDublicatesByName = null;
		Uri[] uriDublicatesByPhone = null;

		if (DublicatesByName != null) {
			String[] strUris = TextUtils.split(DublicatesByName, " ");
			uriDublicatesByName = uriFunctions.StpingToUri(strUris);
		}
		if (DublicatesByPhone != null)
			uriDublicatesByPhone = uriFunctions.StpingToUri(TextUtils.split(DublicatesByPhone, " "));		
		return new Dublicates(contactUri, options, uriDublicatesByName, uriDublicatesByPhone);
	}

	private Cursor getCursorContacts() {
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, null, null, null, null,
				null, null);
		return cursor;
	}

	public boolean isContainsContact(Uri uri) {		
			if(uri == null) 
				return true;		
			Uri[] uris = getContactsUri();					
			if(uris != null)							
			for(int i = 0; i < uris.length; i++){
				int uriSInt1 = uris[i].compareTo(uri);				
				if(uriSInt1 == 0) {
					return true;
				}												
			}			
		return false;
	}
}