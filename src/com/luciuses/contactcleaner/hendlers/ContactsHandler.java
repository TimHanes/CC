package com.luciuses.contactcleaner.hendlers;

import android.os.*;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.*;
import com.luciuses.contactcleaner.treads.ExecutorThread;
import com.luciuses.contactcleaner.treads.SearchDublicateThread;

public class ContactsHandler {
	private ExecutorThread executor;
	private DbProvider dbProvider;
	private int count = 0;
	private ContactsProvider contactsProvider;
	private boolean mFinish;

	public ContactsHandler(ExecutorThread executor) {
		contactsProvider = new ContactsProvider(executor.getMessageHandler());
		dbProvider = App.Instance().getDbProvider();
		this.executor = executor;		
	}
	
	public void setMarkFinish(boolean mFinish) {
		this.mFinish = mFinish;
	}
	
	public void Start() {
				
		dbProvider.Clean();

		int contactCount = contactsProvider.getCount();

		for (int position = 0; position < contactCount; position++) {
			Contact contact = contactsProvider.getContactByPosition(position);							
			SearchDublicateThread searchDublicateThread = new SearchDublicateThread(contact, executor);
			searchDublicateThread.start();
			
			try {
				searchDublicateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Dublicates dubls = searchDublicateThread.getDublicates();
			if (dubls != null) {								
				count++;
				dbProvider.Save(dubls);
			}
			if (mFinish)				
				break;
			ProgressInfo(contactCount, position, contact);	
				
		}
	}

	private void ProgressInfo(int contactCount, int position, Contact contact) {

		Message.obtain(executor.getMessageHandler(), MessageType.SetTextToLogView.ordinal(),
				"work with:" + contact.getId() + "-" + contact.getName() + "\r\n").sendToTarget();
		Message.obtain(executor.getMessageHandler(), MessageType.ShowProgress.ordinal(), contactCount, position,
				contact.getName() + "\r\n" + "Found dublicates:" + count).sendToTarget();
	}

}
