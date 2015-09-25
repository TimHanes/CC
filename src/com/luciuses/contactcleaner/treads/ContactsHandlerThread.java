package com.luciuses.contactcleaner.treads;

import android.os.*;

import java.util.ArrayList;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.DublicatesOfContact;
import com.luciuses.contactcleaner.models.Options;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.content.*;
import android.database.*;
import android.provider.*;
import android.util.*;
import android.net.*;

public class ContactsHandlerThread extends BaseThread
	{						
		private Context _context = App.Instance().getContext();		
		private ExecutorThread executor;		
		private DbProvider dbProvider;
		private int count = 0;
		private ContactsProvider contactsProvider;
		private boolean mFinish;
			
		
		
		public ContactsHandlerThread(ExecutorThread executor)
		{		
			contactsProvider = new ContactsProvider(executor.getMessageHandler());
			dbProvider = App.Instance().getDbProvider();	
			 this.executor = executor;
		}
		
		public void setMarkFinish(boolean mFinish) {
			this.mFinish = mFinish;
		}		
		
		@Override
		public void run()
		{
			Looper.prepare();
			Options options = new Options ();
			dbProvider.Clean();
			
			int contactCount = contactsProvider.getCount();		
			
			for(int i; i < contactCount; i++){
				DublicatesOfContact dublsOfCont = SearchDublicate (options);
				if (dublsOfCont != null){
					count++;
					dbProvider.Save(dublsOfCont);
				}	
				super.run();
				if(!mFinish)break;
			}										
			Message.obtain(executor.getMessageHandler(),MessageType.Finally.ordinal()).sendToTarget();
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
				String[] phones = contactsProvider.PhonesByCursor (cur).split ("\r\n");
				for (int p = 0; p < phones.length; p++) {
				Uri[] foundUri = contactsProvider.GetContactUrisByPhone (phones [p]);
					if (foundUri != null)
						chConUriByPhone = CheckContacts ( cur, foundUri);
				}
			}
			catch (Exception err) {
				Log.e ("byphones", err.getMessage());
			}
			if (chConUriByPhone == null ) return null;
			return new DublicatesOfContact(contactsProvider.UriByCursor(cur), null, chConUriByPhone);
		}

		private DublicatesOfContact CheckByName( Cursor cur)
		{
			Contact contact = new Contact (cur);
			Uri[] foundUri = contactsProvider.GetContactsUriByName (contact.getName(), false);
			Uri[] chConUriByName = CheckContacts (cur, foundUri);
			if (chConUriByName == null ) return null;
			return new DublicatesOfContact(contactsProvider.UriByCursor(cur), chConUriByName, null);
		}

		private DublicatesOfContact CheckByNameAndPhone( Cursor cur)
		{
			Uri[] chConUriByPhone = null;
			Contact contact = new Contact (cur);		
			Uri[] foundUriByName = contactsProvider.GetContactsUriByName (contact.getName(), false);
			Uri[] chConUriByName = CheckContacts (cur, foundUriByName );
			String[] phones = contactsProvider.PhonesByCursor (cur).split ("\r\n");
			for (int p = 0; p < phones.length; p++) {
				Uri[]	foundUriByPhone = contactsProvider.GetContactUrisByPhone (phones [p]);
			chConUriByPhone =	CheckContacts (cur, foundUriByPhone);
			}
			 if((chConUriByName == null)&(chConUriByPhone == null)){
				 return null;
			 }
			return new DublicatesOfContact(contactsProvider.UriByCursor(cur), chConUriByName, chConUriByPhone);
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
			int fid = contactsProvider.IdByUri (uri);
			if (fid > contact.getId()) {
				String fname = contactsProvider.NameByUri (uri);
				Message.obtain (executor.getMessageHandler(), MessageType.AddToLogView.ordinal(), "find:" + fid + "-" + fname + "\r\n").sendToTarget ();
				return true;
			}
			return false;
		} 
		
		private void DisplayInfo (Cursor cur)
		{
			Contact contact = new Contact (cur);
			Message.obtain (executor.getMessageHandler(), MessageType.SetTextToLogView.ordinal(), "work with:" + contact.getId() + "-" + contact.getName() + "\r\n").sendToTarget ();
			Message.obtain (executor.getMessageHandler(), MessageType.ShowProgress.ordinal(), cur.getCount(), cur.getPosition(), contact.getName() + "\r\n" + "Found dublicates:"+count ).sendToTarget ();
		}

		
	} 

