package com.luciuses.contactcleaner.providers;

import java.util.Iterator;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;

import android.content.*;
import android.database.*;
import android.provider.*;
import android.util.*;
import android.net.*;
import android.os.Message;

public class ContactsProvider
	{				
		private Context context = App.Instance().getContext();					
		MessageHandler messageHandler;
		
		public ContactsProvider (MessageHandler messageHandler){
			this.messageHandler = messageHandler;
		}
											
		public void ContactDelete(int id)
		{
			Uri uri = UriById(id);
			String name = NameByUri(uri);
			Message.obtain(messageHandler,MessageType.AddToLogView.ordinal(),"deleted:"+ name +"\r\n").sendToTarget();//
			context.getContentResolver().delete(uri,null,null);
			App.Instance().getDbProvider().ContactDelete(uri);
			
		}

		public int getCount(){
			Cursor cur = getAllContactCursor();
			int count = cur.getCount();
			return count;
		}
		
		public Cursor getAllContactCursor() {
			Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			return cur;
		}

		public String PhonesByUri(Uri uri)
		{
			Cursor cur = getCursorByUri(uri);
			cur.moveToFirst();
			String phones="";
			int id = IdByCursor(cur);			
			if (IsHasNamber(cur))					 
			{
				Cursor pCur = getCursorById(id);
				while (pCur.moveToNext()) 
				{
					phones+= getPhoneByCursor(pCur) +"\r\n";
				}
				pCur.close();
			}
			cur.close();
			return phones;
		}		

		public String PhonesByCursor(Cursor cur)
		{
			String phones="";
			int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
			if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)					 
			{
				Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+id,null, null);
				
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
			Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, (!ignorecase)?(ContactsContract.Contacts.DISPLAY_NAME+" LIKE '"+selname+"'"):("UPPER("+ContactsContract.Contacts.DISPLAY_NAME+") LIKE UPPER('"+selname+"')"),null,null);
				

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

		public Contact getContactByUri(Uri uri){
			Contact contact = new Contact(IdByUri(uri),NameByUri(uri),PhonesByUri(uri));
			return contact;
		}
		
		public int IdByUri(Uri uri)
		{
			
			 Cursor cur =context.getContentResolver().query(uri,null,null,null,null);
			cur.moveToFirst();
			int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
			cur.close();
			return id;
		} 

		public String NameByUri(Uri uri)
		{
			Cursor cur=context.getContentResolver().query(uri,null,null,null,null);			
			cur.moveToFirst();
			return cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}
		
		public Uri[] GetContactUrisByPhone(String phone)
		{
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
					
					Cursor cur =context.getContentResolver().query(contactUri, null, null, null,null);

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
			Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, (ContactsContract.Contacts._ID+" LIKE '"+id+"'"),null,null);				
			cur.moveToFirst ();
			Uri uri = UriByCursor(cur);						
			cur.close ();
			return uri;			
		}
		
		public void ContactJoin(int id)
		{						
			Message.obtain(messageHandler,MessageType.AddToLogView.ordinal(),"joined:"+ NameByUri(UriById(id))+"\r\n").sendToTarget();//
		}
		
		public String[] NamesByUris(Uri[] uris){
			String[] strUris = new String[uris.length];
			for(int i = 0; i < uris.length; i++){
				strUris[i] = NameByUri(uris[i]);
			}			
			return strUris;
		}
		
		private int IdByCursor (Cursor cursor){
			int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			return id;
		}
		
		private Cursor getCursorByUri(Uri uri) {
			Cursor cur = context.getContentResolver().query(uri, null, null, null, null);
			return cur;
		}

		private String getPhoneByCursor(Cursor pCur) {
			String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
			return phone;
		}

		private Cursor getCursorById(int id) {
			Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+id,null, null);			
			return  pCur;
		}

		private boolean IsHasNamber(Cursor cur) {
			if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
				return false;
			}			
			return true;
		}

		public Contact getContactByPosition(int position){
			Cursor cursor =	getAllContactCursor();
			cursor.moveToPosition(position);
			Uri uri = UriByCursor(cursor);
			Contact contact = getContactByUri(uri);
			return contact;
		}
		
		public Contact[] getContacts(){
			int count = getCount();
			Contact[] conacts = new Contact[count];
			Cursor cursor =	getAllContactCursor();
			cursor.moveToFirst();
			for (int i = 0 ; i < count; i++, cursor.moveToNext()) {
				Uri uri = UriByCursor(cursor);
				conacts[i] = getContactByUri(uri);
			}
			return conacts;
		}
	} 

