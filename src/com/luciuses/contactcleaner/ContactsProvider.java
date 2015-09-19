package com.luciuses.contactcleaner;

import android.os.*;
import android.content.*;
import android.database.*;
import android.provider.*;
import android.util.*;
import android.net.*;

public class ContactsProvider 
	{				
		private Context _context = App.Instance().AppContext;		
		private MessageHandler _mhandler;		
		private DbProvider dbProvider = new DbProvider(_context);
							
		public DbProvider getDbProvider(){			
			return dbProvider;			
		}

		public void ContactDelete(int id)
		{
			Uri uri = UriById(id);
			
			Message.obtain(_mhandler,MessageType.AddToLogView.ordinal(),"deleted:"+ NameByUri(uri)+"\r\n").sendToTarget();//
			_context.getContentResolver().delete(uri,null,null);
		}

		public String PhonesByUri(Uri uri)
		{
			Cursor cur = _context.getContentResolver().query(uri, null, null, null, null);
			cur.moveToFirst();
			String phones="";
			int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
			if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)					 
			{
				Cursor pCur = _context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+id,null, null);
				
				while (pCur.moveToNext()) 
				{
					phones+=pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))+"\r\n";
				}
				pCur.close();
			}
			return phones;
		}

		public String PhonesByCursor(Cursor cur)
		{
			String phones="";
			int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
			if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)					 
			{
				Cursor pCur = _context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+id,null, null);
				
			while (pCur.moveToNext()) 
				{
					phones+=pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))+"\r\n";
				}
				pCur.close();
			}
			return phones;
		} 

		public Uri[] GetContactsUriByName(String selname,boolean ignorecase)
		{
			selname = selname.replace ("'", "''"); 
			Cursor cur = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, (!ignorecase)?(ContactsContract.Contacts.DISPLAY_NAME+" LIKE '"+selname+"'"):("UPPER("+ContactsContract.Contacts.DISPLAY_NAME+") LIKE UPPER('"+selname+"')"),null,null);
				

				Uri[] contactsUri = new Uri[cur.getCount()];

				if (cur.moveToFirst ()) {
					int c = 0;
					do {
						Uri uri = UriByCursor(cur);
						contactsUri [c] = uri;
						c++;
					} while(cur.moveToNext ());
				}
				cur.close ();
			return contactsUri;
		}
	
		public Uri UriByCursor(Cursor cur) {
			String columnName = ContactsContract.Contacts.LOOKUP_KEY;
			int columnIndex = cur.getColumnIndex (columnName);
			 String lookupKey = cur.getString (columnIndex);
				Uri uri = Uri.withAppendedPath (ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
			return uri;
		}

		public int IdByUri(Uri uri)
		{
			 Cursor cur =_context.getContentResolver().query(uri,null,null,null,null);
			cur.moveToFirst();
			return cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
		} 

		public String NameByUri(Uri uri)
		{
			Cursor cur=_context.getContentResolver().query(uri,null,null,null,null);			
			cur.moveToFirst();
			return cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}
		
		public Uri[] GetContactUrisByPhone(String phone)
		{
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
					
					Cursor cur =_context.getContentResolver().query(contactUri, null, null, null,null);

			if((cur!=null)&(cur.getCount()>0))
					{
						Uri[] contactsUri=new Uri[cur.getCount()];
		
						try {
							if (cur.moveToFirst())
							{
								int c=0;
								do {
									String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
									Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
									contactsUri[c]=uri;
									c++;
									} while(cur.moveToNext());
							}
						} catch (Exception e) {
							Log.e("xxx",e.getMessage());
						}
						cur.close();
						return contactsUri;
					}
				 	else
					{
						cur.close();
						return null;
					}
		
		}
		
		public Uri UriById(int id){						
			Cursor cur = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, (ContactsContract.Contacts._ID+" LIKE '"+id+"'"),null,null);				
			cur.moveToFirst ();
			Uri uri = UriByCursor(cur);						
			cur.close ();
			return uri;			
		}
		
		public void ContactJoin(int id)
		{						
			Message.obtain(_mhandler,MessageType.AddToLogView.ordinal(),"joined:"+ NameByUri(UriById(id))+"\r\n").sendToTarget();//
		}
	} 

