package com.luciuses.contactcleaner;

import android.os.*;
import android.content.*;
import android.app.*;
import android.widget.*;
import android.database.*;
import android.provider.*;
import android.util.*;
import android.net.*;

public class ContactsHandler extends BaseThread
	{
		private Uri[] foundUri;
		private int currentContactIndex;
		private boolean _mFinished = false;
		Context _context = App.Instance().AppContext;		
		private MessageHandler _mhandler;
		ActionType _defaultAction = ActionType.None;

		public ContactsHandler()
		{
			_mhandler = new MessageHandler(this);			
		}
		
		class SomeClass {
		      private int someProperty;  
		      public int getSomeProperty() {return someProperty;}
		      public void setSomeProperty(int newProperty){someProperty = newProperty;}
		}
		
		public int CurrentContactIndex() { 						
				return currentContactIndex;
		}

		public Uri[] FoundUri() { 				
				return	foundUri;			
		}

		@Override
		public void run()
		{
			Looper.prepare();
			Options options = new Options ();

			Cursor cur = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			cur.moveToFirst ();
								
			while (cur.moveToNext () && !_mFinished) 
			{
				SearchDublicate (cur, options);
				super.run();
			}
	
			Message.obtain(_mhandler,MessageType.Finally.ordinal()).sendToTarget();

		}	

		private void SearchDublicate (Cursor cur, Options options)
		{
			

			DisplayInfo (cur);

			if ((options.ByName) & (options.ByPhone)) {
				CheckByNameAndPhone ( cur);
				return;
			}

			if (options.ByName) {
				CheckByName ( cur);
				return;
			}
				
			if (options.ByPhone) {
				CheckByPhone ( cur);
			}
		}

		private void CheckByPhone(Cursor cur)
		{
			Contact contact = new Contact (cur);
			try {
				String[] phones = PhonesByCursor (cur).split ("\r\n");
				for (int p = 0; p < phones.length; p++) {
					foundUri = GetContactUrisByPhone (phones [p]);
					if (foundUri != null)
						CheckContacts ( cur);
				}
			}
			catch (Exception err) {
				Log.e ("byphones", err.getMessage());
			}
		}

		private void CheckByName( Cursor cur)
		{
			Contact contact = new Contact (cur);
			foundUri = GetContactsUriByName (contact.Name, false);
			CheckContacts (cur);
		}

		private void CheckByNameAndPhone( Cursor cur)
		{
			Contact contact = new Contact (cur);
			foundUri = GetContactsUriByName (contact.Name, false);
			CheckContacts ( cur);
			String[] phones = PhonesByCursor (cur).split ("\r\n");
			for (int p = 0; p < phones.length; p++) {
				foundUri = GetContactUrisByPhone (phones [p]);
				CheckContacts (cur);
			}
		}

		private void DisplayInfo (Cursor cur)
		{
			Contact contact = new Contact (cur);
			Message.obtain (_mhandler, MessageType.SetTextToLogView.ordinal(), "work with:" + contact.Id + "-" + contact.Name + "\r\n").sendToTarget ();
			Message.obtain (_mhandler, MessageType.ShowProgress.ordinal(), cur.getCount(), cur.getPosition(), contact.Name + "\r\n" + ((_defaultAction == ActionType.None) ? "" : "(default:" + _defaultAction.toString () + ")")).sendToTarget ();
		}
 
			
		private void CheckContacts(Cursor cur)
		{
			Contact contact = new Contact (cur);
			if (foundUri != null) {
				for ( currentContactIndex = 0; currentContactIndex <= foundUri.length - 1; currentContactIndex++) {
					CompaireContact(cur);	
				}
			}
		}		

		private void CompaireContact( Cursor cur)
		{
			Contact contact = new Contact (cur);
			int fid = IdByUri (foundUri [currentContactIndex]);
			if (fid > contact.Id) {
				String fname = NameByUri (foundUri [currentContactIndex]);
				Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "find:" + fid + "-" + fname + "\r\n").sendToTarget ();//append to tv
				if (!App.Instance().Popup.chbsave.isChecked()) {
					Message.obtain (_mhandler, MessageType.ShowPopupForChooseAction.ordinal(), "Work with:" + contact.Name + "\r\n" + PhonesByCursor (cur) + "find:" + fname + "\r\n" + PhonesByUri (foundUri [currentContactIndex])).sendToTarget ();//
					this.Pause();
					super.run();
				} else {

					switch (_defaultAction) {
					case None:
						break;
					case Delete:
						ContactDelete(foundUri[currentContactIndex]);
						break;
					case Join:
						ContactJoin(foundUri[currentContactIndex]);
						break;
					case Ignore:
						break;
					}

				}

			}
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

		public void ContactDelete(Uri uri)
		{
			_defaultAction=ActionType.Delete;
			Message.obtain(_mhandler,MessageType.AddToLogView.ordinal(),"deleted:"+ NameByUri(uri)+"\r\n").sendToTarget();//
			_context.getContentResolver().delete(uri,null,null);
		}

		public void ContactJoin(Uri uri)
		{
			_defaultAction=ActionType.Join;
			Message.obtain(_mhandler,MessageType.AddToLogView.ordinal(),"joined:"+ NameByUri(uri)+"\r\n").sendToTarget();//
		}

		public void ContactIgnore()
		{
			_defaultAction=ActionType.Ignore;
		}	

		public void OnFinished() {
			_mFinished=true;
		}
	

		public static void DeleteContact(Context context, String lookupKey)
		{
			Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
			context.getContentResolver().delete(uri, null, null);
		}

	
 		public Uri[] GetContactsUriByName(String selname,boolean ignorecase)
		{
			selname = selname.replace ("'", "''"); 
			Cursor cur = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, (!ignorecase)?(ContactsContract.Contacts.DISPLAY_NAME+" LIKE '"+selname+"'"):("UPPER("+ContactsContract.Contacts.DISPLAY_NAME+") LIKE UPPER('"+selname+"')"),null,null);
				

				Uri[] contactsUri = new Uri[cur.getCount()];

				if (cur.moveToFirst ()) {
					int c = 0;
					do {
						String lookupKey = cur.getString (cur.getColumnIndex (ContactsContract.Contacts.LOOKUP_KEY));
						Uri uri = Uri.withAppendedPath (ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
						contactsUri [c] = uri;
						c++;
					} while(cur.moveToNext ());
				}
				cur.close ();
			return contactsUri;
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

		

	} 

