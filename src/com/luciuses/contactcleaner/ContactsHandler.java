package com.luciuses.contactcleaner;

import android.os.*;

import java.util.ArrayList;

import android.content.*;
import android.database.*;
import android.provider.*;
import android.util.*;
import android.net.*;

public class ContactsHandler extends BaseThread
	{
				
		private boolean _mFinished = false;
		private Context _context = App.Instance().AppContext;		
		private MessageHandler _mhandler;
		private ActionType _defaultAction = ActionType.None;
		private DbProvider dbProvider = new DbProvider(_context);

		public ContactsHandler()
		{
			_mhandler = new MessageHandler(this);			
		}
		
		public DbProvider getDbProvider(){
			
			return dbProvider;			
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
				DublicatesOfContact dublsOfCont = SearchDublicate (cur, options);
				if (dublsOfCont != null){
					dbProvider.Save(dublsOfCont);
				}	
				super.run();
			}
			ShowResult(dbProvider);
			Message.obtain(_mhandler,MessageType.Finally.ordinal()).sendToTarget();

		}	

		private DublicatesOfContact SearchDublicate (Cursor cur, Options options)
		{

			DisplayInfo (cur);

			if ((options.ByName) & (options.ByPhone)) {				
				return CheckByNameAndPhone ( cur);
			}

			if (options.ByName) {
				
				return CheckByName ( cur);
			}
				
			if (options.ByPhone) {
				return CheckByPhone ( cur);
			}
			return null;
		}

		private DublicatesOfContact CheckByPhone(Cursor cur)
		{		
			Uri[] chConUriByPhone = null;
			try {
				String[] phones = PhonesByCursor (cur).split ("\r\n");
				for (int p = 0; p < phones.length; p++) {
				Uri[] foundUri = GetContactUrisByPhone (phones [p]);
					if (foundUri != null)
						chConUriByPhone = CheckContacts ( cur, foundUri);
				}
			}
			catch (Exception err) {
				Log.e ("byphones", err.getMessage());
			}
			if (chConUriByPhone == null ) return null;
			return new DublicatesOfContact(UriByCursor(cur), null, chConUriByPhone);
		}

		private DublicatesOfContact CheckByName( Cursor cur)
		{
			Contact contact = new Contact (cur);
			Uri[] foundUri = GetContactsUriByName (contact.getName(), false);
			Uri[] chConUriByName = CheckContacts (cur, foundUri);
			if (chConUriByName == null ) return null;
			return new DublicatesOfContact(UriByCursor(cur), chConUriByName, null);
		}

		private DublicatesOfContact CheckByNameAndPhone( Cursor cur)
		{
			Uri[] chConUriByPhone = null;
			Contact contact = new Contact (cur);		
			Uri[] foundUriByName = GetContactsUriByName (contact.getName(), false);
			Uri[] chConUriByName = CheckContacts (cur, foundUriByName );
			String[] phones = PhonesByCursor (cur).split ("\r\n");
			for (int p = 0; p < phones.length; p++) {
				Uri[]	foundUriByPhone = GetContactUrisByPhone (phones [p]);
			chConUriByPhone =	CheckContacts (cur, foundUriByPhone);
			}
			 if((chConUriByName == null)&(chConUriByPhone == null)){
				 return null;
			 }
			return new DublicatesOfContact(UriByCursor(cur), chConUriByName, chConUriByPhone);
		}

		private void DisplayInfo (Cursor cur)
		{
			Contact contact = new Contact (cur);
			Message.obtain (_mhandler, MessageType.SetTextToLogView.ordinal(), "work with:" + contact.getId() + "-" + contact.getName() + "\r\n").sendToTarget ();
			Message.obtain (_mhandler, MessageType.ShowProgress.ordinal(), cur.getCount(), cur.getPosition(), contact.getName() + "\r\n" + ((_defaultAction == ActionType.None) ? "" : "(default:" + _defaultAction.toString () + ")")).sendToTarget ();
		}
 
			
		private Uri[] CheckContacts(Cursor cur, Uri[] foundUri)
		{
			ArrayList<Uri> contactsUri = new ArrayList<Uri>();
			if ( foundUri != null) {
				for ( int currentContactIndex = 0; currentContactIndex <= foundUri.length - 1; currentContactIndex++) {
					if (CompaireContact(cur, foundUri[currentContactIndex])){
						contactsUri.add(foundUri[currentContactIndex]);
					};	
				}
			}
			if (contactsUri.isEmpty()){
				return null;
			}
			Object[] contUriArrObj = contactsUri.toArray();
			Uri[] contUriArrUri = new Uri[contUriArrObj.length];
			for (int i = 0; i < contUriArrObj.length; i++){
				contUriArrUri[i] = (Uri)contUriArrObj[i];
			}
			return contUriArrUri;
			
		}		

		private boolean CompaireContact( Cursor cur, Uri uri)
		{
			Contact contact = new Contact (cur);
			int fid = IdByUri (uri);
			if (fid > contact.getId()) {
				String fname = NameByUri (uri);
				Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "find:" + fid + "-" + fname + "\r\n").sendToTarget ();//append to tv
				if (!App.Instance().Popup.chbsave.isChecked()) {
					Message.obtain (_mhandler, MessageType.ShowPopupForChooseAction.ordinal(), contact.getId(), fid, "Work with:" + contact.getName() + "\r\n" + PhonesByCursor (cur) + "find:" + fname + "\r\n" + PhonesByUri (uri)).sendToTarget ();//
					
					
					this.Pause();
					super.run();
				} else {

					switch (_defaultAction) {
					case None:
						break;
					case Delete:
						ContactDelete(uri);
						break;
					case Join:
						ContactJoin(uri);
						break;
					case Ignore:
						break;
					}

				}
				return true;
			}
			return false;
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
						Uri uri = UriByCursor(cur);
						contactsUri [c] = uri;
						c++;
					} while(cur.moveToNext ());
				}
				cur.close ();
			return contactsUri;
		}
	

		 private Uri UriByCursor(Cursor cur) {
			 String lookupKey = cur.getString (cur.getColumnIndex (ContactsContract.Contacts.LOOKUP_KEY));
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

		public void ShowResult(DbProvider dbProvider){
			
			Uri[] uris = dbProvider.getContactsUri();
			for (int i =0; i< uris.length; i++){				
				DublicatesOfContact dubl = dbProvider.Read(uris[i]);			
				Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "Name:" + NameByUri(uris[i]) + "-"+ "\r\n").sendToTarget ();
				for (int a =0; a< dubl.getUriDublicatesByName().length; a++){
					Uri uri = dubl.getUriDublicatesByName()[a];
					if(uri != null) Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "DablName:" + NameByUri(uri) + "-"+ "\r\n").sendToTarget ();						
				}
				
			}
			
			
		}

	} 

