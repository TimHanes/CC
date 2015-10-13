package com.luciuses.contactcleaner.hendlers;

import android.os.*;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.*;
import com.luciuses.contactcleaner.treads.ExecutorThread;
import com.luciuses.contactcleaner.treads.SearchDublicate;

public class ContactsHandler {
	private ExecutorThread executor;
	private DbProvider dbProvider;
	private int count = 0;
	private ContactsProvider contactsProvider;
	

	public ContactsHandler(ExecutorThread executor) {
		contactsProvider = new ContactsProvider(executor.getMessageHandler());
		dbProvider = App.Instance().getDbProvider();
		this.executor = executor;		
	}
	
	public void Start() {
		
		executor.setmFinish(false);
		int contactCount = contactsProvider.getCount();
		for (int position = 0; position < contactCount; position++) {
			Contact contact = contactsProvider.getContactByPosition(position);
			if(contact == null)
				return;						
			SearchDublicate searchDublicate = new SearchDublicate(contact, executor);
			searchDublicate.Run();
				
			if (executor.ismFinish())				
				break;
			ProgressInfo(contactCount, position, contact);
			
			Dublicates dubls = searchDublicate.getDublicates();			
			if (dubls != null) {								
				count++;
				dbProvider.Save(dubls);
			}								
		}
	}

	private void ProgressInfo(int contactCount, int position, Contact contact) {

		Message.obtain(executor.getMessageHandler(), MessageType.ShowProgress.ordinal(), contactCount, position,
				contact.getName() + "\r\n" + "Found dublicates:" + count).sendToTarget();
	}

}
