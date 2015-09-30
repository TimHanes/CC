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
				
//		dbProvider.Clean();

		int contactCount = contactsProvider.getCount();

		for (int position = 0; position < contactCount; position++) {
			Contact contact = contactsProvider.getContactByPosition(position);
			DisplayInfo(contactCount, position, contact);
			Dublicates dublsOfCont = new SearchDublicateThread(contact, executor).getDublicates();
			if (dublsOfCont != null) {
				count++;
				dbProvider.Save(dublsOfCont);
			}
			if (mFinish)
				break;
		}
		Message.obtain(executor.getMessageHandler(), MessageType.Finally.ordinal()).sendToTarget();
	}

	private void DisplayInfo(int contactCount, int position, Contact contact) {

		Message.obtain(executor.getMessageHandler(), MessageType.SetTextToLogView.ordinal(),
				"work with:" + contact.getId() + "-" + contact.getName() + "\r\n").sendToTarget();
		Message.obtain(executor.getMessageHandler(), MessageType.ShowProgress.ordinal(), contactCount, position,
				contact.getName() + "\r\n" + "Found dublicates:" + count).sendToTarget();
	}

}
