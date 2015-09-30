package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.*;

import android.os.Message;

public class ShowFoundDublesThread extends BaseThread {

	private DbProvider dbProvider;
	private ContactsProvider contactsProvider;
	private MessageHandler messageHandler; 
	private Dublicates dublicates;
	
	public ShowFoundDublesThread(Dublicates dublicates, MessageHandler messageHandler) {
		this.dublicates = dublicates;
		contactsProvider = new ContactsProvider(messageHandler);
		this.messageHandler = messageHandler;
		dbProvider = App.Instance().getDbProvider();	
		super.setName("ShowFoundDublesThread");
	}
	
	@Override
	public void run()
	{		
		ShowList(dbProvider);		
	}

	private void ShowList(DbProvider dbProvider){	
		DublicatesContact dublicatesContact = contactsProvider.getDublicatesContact(dublicates);
		int count = dublicatesContact.getDublicatesByName().length + dublicatesContact.getDublicatesByPhone().length;
		String[] showArray = new String[count];
		int i = 0;
		for(; i < dublicatesContact.getDublicatesByName().length; i++ ){
			String name = dublicatesContact.getDublicatesByName()[i].getName();
			String phones =  dublicatesContact.getDublicatesByName()[i].getPhones();
			showArray[i] = name + phones;
		}
		for(; i < count; i++ ){
			String name = dublicatesContact.getDublicatesByName()[i].getName();
			String phones =  dublicatesContact.getDublicatesByName()[i].getPhones();
			showArray[i] = name + phones;
		}
				
		Message.obtain (messageHandler, MessageType.ShowListView.ordinal(), showArray).sendToTarget();			
	}	
	
	

}
