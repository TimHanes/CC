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
		private DbProvider dbProvider;
		private int count = 0;
		private ContactsProvider _contactsProvider;
		
		
		public ContactsHandler()
		{		
			dbProvider = App.Instance().dbProvider;
			_mhandler = App.Instance().messegeHandler;
			_contactsProvider = new ContactsProvider();	
			
		}
		
		public DbProvider getDbProvider(){			
			return dbProvider;			
		}
		
		@Override
		public void run()
		{
			Looper.prepare();
			Options options = new Options ();
			dbProvider.Clean();
			

			Cursor cur = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			cur.moveToFirst ();			
								
			while (cur.moveToNext () && !_mFinished) 
			{
				DublicatesOfContact dublsOfCont = SearchDublicate (cur, options);
				if (dublsOfCont != null){
					count++;
					dbProvider.Save(dublsOfCont);
				}	
				super.run();
			}
			
			
			Message.obtain(_mhandler,MessageType.Finally.ordinal()).sendToTarget();

		}

		public void OnFinished() {
			_mFinished=true;
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
				String[] phones = _contactsProvider.PhonesByCursor (cur).split ("\r\n");
				for (int p = 0; p < phones.length; p++) {
				Uri[] foundUri = _contactsProvider.GetContactUrisByPhone (phones [p]);
					if (foundUri != null)
						chConUriByPhone = CheckContacts ( cur, foundUri);
				}
			}
			catch (Exception err) {
				Log.e ("byphones", err.getMessage());
			}
			if (chConUriByPhone == null ) return null;
			return new DublicatesOfContact(_contactsProvider.UriByCursor(cur), null, chConUriByPhone);
		}

		private DublicatesOfContact CheckByName( Cursor cur)
		{
			Contact contact = new Contact (cur);
			Uri[] foundUri = _contactsProvider.GetContactsUriByName (contact.getName(), false);
			Uri[] chConUriByName = CheckContacts (cur, foundUri);
			if (chConUriByName == null ) return null;
			return new DublicatesOfContact(_contactsProvider.UriByCursor(cur), chConUriByName, null);
		}

		private DublicatesOfContact CheckByNameAndPhone( Cursor cur)
		{
			Uri[] chConUriByPhone = null;
			Contact contact = new Contact (cur);		
			Uri[] foundUriByName = _contactsProvider.GetContactsUriByName (contact.getName(), false);
			Uri[] chConUriByName = CheckContacts (cur, foundUriByName );
			String[] phones = _contactsProvider.PhonesByCursor (cur).split ("\r\n");
			for (int p = 0; p < phones.length; p++) {
				Uri[]	foundUriByPhone = _contactsProvider.GetContactUrisByPhone (phones [p]);
			chConUriByPhone =	CheckContacts (cur, foundUriByPhone);
			}
			 if((chConUriByName == null)&(chConUriByPhone == null)){
				 return null;
			 }
			return new DublicatesOfContact(_contactsProvider.UriByCursor(cur), chConUriByName, chConUriByPhone);
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
			int fid = _contactsProvider.IdByUri (uri);
			if (fid > contact.getId()) {
				String fname = _contactsProvider.NameByUri (uri);
				Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "find:" + fid + "-" + fname + "\r\n").sendToTarget ();
				return true;
			}
			return false;
		} 
		
		private void DisplayInfo (Cursor cur)
		{
			Contact contact = new Contact (cur);
			Message.obtain (_mhandler, MessageType.SetTextToLogView.ordinal(), "work with:" + contact.getId() + "-" + contact.getName() + "\r\n").sendToTarget ();
			Message.obtain (_mhandler, MessageType.ShowProgress.ordinal(), cur.getCount(), cur.getPosition(), contact.getName() + "\r\n" + "Found dublicates:"+count ).sendToTarget ();
		}
	} 

