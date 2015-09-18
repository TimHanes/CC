package com.luciuses.contactcleaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
 
public class DbProvider {
 
	SQLiteDatabase sdb;
	DbHelper dbHelper;
	public DbProvider(Context context){
		dbHelper = new DbHelper(context, "database1.db", null, 1);		
		sdb = dbHelper.getReadableDatabase();
		
	}
	
	public void Save (DublicatesOfContact dublsOfCont){
		ContentValues newValues = new ContentValues();
	    // Задайте значения для каждого столбца		
	    newValues.put(DbHelper.CONTACT_URI, dublsOfCont.getContactUri().toString());
	    
	    Uri[] urisDublArr = dublsOfCont.getUriDublicatesByName();	    
	    if (urisDublArr!=null){
	    	String urisDublStr = UriSerialization(urisDublArr);
	    	newValues.put(DbHelper.URI_DUBLICATES_BY_NAME, urisDublStr); 	   	
	    }	 
	    
	     urisDublArr = dublsOfCont.getUriDublicatesByPhone();	    
	    if (urisDublArr!=null){	    	
	    	String urisDublStr = UriSerialization(urisDublArr);
	    	newValues.put(DbHelper.URI_DUBLICATES_BY_PHONE, urisDublStr);	    	
	    }   
	    
	    sdb.insert("UriDublicatesOfContact", null, newValues);
	}
	
	public DublicatesOfContact Read (Uri uri){
		
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, new String[] {DbHelper.CONTACT_URI,
                DbHelper.URI_DUBLICATES_BY_NAME, DbHelper.URI_DUBLICATES_BY_PHONE},
				"contactUri = ?",new String[]{ uri.toString()},
        null, null, null) ;

cursor.moveToFirst();

Uri contactUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DbHelper.CONTACT_URI)));
String DublicatesByName = cursor.getString(cursor.getColumnIndex(DbHelper.URI_DUBLICATES_BY_NAME));
String DublicatesByPhone = cursor.getString(cursor.getColumnIndex(DbHelper.URI_DUBLICATES_BY_PHONE));
Uri[] uriDublicatesByName = null;
Uri[] uriDublicatesByPhone = null;

if (DublicatesByName!=null) {
	String[] strUris = TextUtils.split( DublicatesByName, " ");
	uriDublicatesByName = StpingToUri(strUris);}
if (DublicatesByPhone!=null) uriDublicatesByPhone = StpingToUri(TextUtils.split( DublicatesByPhone, " "));

// не забываем закрывать курсор
cursor.close();
		return new DublicatesOfContact(contactUri, uriDublicatesByName, uriDublicatesByPhone);		
	}
	
	private Uri[] StpingToUri(String[] uriStr){
		Uri[] uri = new Uri[uriStr.length];
		for (int i = 0; i < uriStr.length; i++){
			uri[i] = Uri.parse(uriStr[i]);
		}
		
		return uri;
	}
	
	private String UriSerialization(Uri[] urisDublArr){
		String[] urisDublStrArr =  UriToString(urisDublArr);
    	String urisDublStr = TextUtils.join(" ", urisDublStrArr );
		return urisDublStr;
	}
	
	private String[] UriToString(Uri[] uri){
		String[] uriStr = new String[uri.length];
		for (int i = 0; i < uri.length; i++){
			uriStr[i] = uri[i].toString();
		}
		
		return uriStr;
	}

	public Uri[] getContactsUri() {
		
		Cursor cursor = sdb.query(DbHelper.DATABASE_TABLE, new String[] {DbHelper.CONTACT_URI},
				null,null,
        null, null, null) ;

		cursor.moveToFirst();
		Uri[] contactsUri = new Uri[cursor.getCount()];
		
		for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext() ){
			contactsUri[i]=Uri.parse(cursor.getString(cursor.getColumnIndex(DbHelper.CONTACT_URI)));			
		}				
		return contactsUri;
	}
	
}