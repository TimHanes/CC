package com.luciuses.contactcleaner.providers;

import com.luciuses.contactcleaner.Functions.UriFunctions;
import com.luciuses.contactcleaner.basis.DbHelper;
import com.luciuses.contactcleaner.models.DublicatesOfContact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
 
public class DbProvider {
 
	private SQLiteDatabase sdb;
	private DbHelper dbHelper;
	private UriFunctions uriFunctions = new UriFunctions();
	
	public DbProvider(Context context){
		dbHelper = new DbHelper(context, "database1.db", null, 1);		
		sdb = dbHelper.getReadableDatabase();		
	}
	
	public void Save (DublicatesOfContact dublsOfCont){
		ContentValues newValues = PutInContent(dublsOfCont);	    
	    sdb.insert("UriDublicatesOfContact", null, newValues);
	}
	
	public DublicatesOfContact Read (Uri uri){
		
		Cursor cursor = getCursorByUri(uri);
		cursor.moveToFirst();
		DublicatesOfContact dublsCont = getDublicatesOfContactByCursor(cursor);
		cursor.close();
		return dublsCont;		
	}
		
	public Uri[] getContactsUri() {
		
		Cursor cursor = getCursorContacts();
		cursor.moveToFirst();
		Uri[] contactsUri = new Uri[cursor.getCount()];		
		for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext() ){
			contactsUri[i]=Uri.parse(cursor.getString(cursor.getColumnIndex(DbHelper.CONTACT_URI)));			
		}
		cursor.close();
		return contactsUri;
	}
	
	public void Clean(){
		sdb.delete(DbHelper.DATABASE_TABLE, null, null);
	}	
	
	private ContentValues PutInContent(DublicatesOfContact dublsOfCont){
		ContentValues newValues = new ContentValues();
		 newValues.put(DbHelper.CONTACT_URI, dublsOfCont.getContactUri().toString());
		    
		    Uri[] urisDublArr = dublsOfCont.getUriDublicatesByName();	    
		    if (urisDublArr!=null){
		    	String urisDublStr = uriFunctions.UriSerialization(urisDublArr);
		    	newValues.put(DbHelper.URI_DUBLICATES_BY_NAME, urisDublStr); 	   	
		    }	 
		    
		     urisDublArr = dublsOfCont.getUriDublicatesByPhone();	    
		    if (urisDublArr!=null){	    	
		    	String urisDublStr = uriFunctions.UriSerialization(urisDublArr);
		    	newValues.put(DbHelper.URI_DUBLICATES_BY_PHONE, urisDublStr);	    	
		    }   
		    return newValues;
	}
	
	private Cursor getCursorByUri(Uri uri){
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, new String[] {DbHelper.CONTACT_URI,
                DbHelper.URI_DUBLICATES_BY_NAME, DbHelper.URI_DUBLICATES_BY_PHONE},
				"contactUri = ?",new String[]{ uri.toString()}, null, null, null) ;
		return cursor;
	} 
	
	private DublicatesOfContact getDublicatesOfContactByCursor(Cursor cursor){
		Uri contactUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DbHelper.CONTACT_URI)));
		String DublicatesByName = cursor.getString(cursor.getColumnIndex(DbHelper.URI_DUBLICATES_BY_NAME));
		String DublicatesByPhone = cursor.getString(cursor.getColumnIndex(DbHelper.URI_DUBLICATES_BY_PHONE));
		Uri[] uriDublicatesByName = null;
		Uri[] uriDublicatesByPhone = null;

		if (DublicatesByName!=null) {
			String[] strUris = TextUtils.split( DublicatesByName, " ");
			uriDublicatesByName = uriFunctions.StpingToUri(strUris);}
		if (DublicatesByPhone!=null) uriDublicatesByPhone = uriFunctions.StpingToUri(TextUtils.split( DublicatesByPhone, " "));
		return new DublicatesOfContact(contactUri, uriDublicatesByName, uriDublicatesByPhone);
	}
	
	private Cursor getCursorContacts(){
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, new String[] {DbHelper.CONTACT_URI},
				null, null, null, null, null) ;
		return cursor;
	}

	public void ContactDelete(Uri uri) {
		sdb.delete(DbHelper.DATABASE_TABLE, null, null);
		sdb.delete(DbHelper.DATABASE_TABLE, DbHelper.CONTACT_URI+" = " + uri.toString(), null);					
	} 
}