package com.luciuses.contactcleaner.actions;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.*;

import android.os.Message;

public class ShowFoundDubles {

	private DbProvider dbProvider;
	private ContactsProvider contactsProvider;
	private MessageHandler messageHandler; 
	private Dublicates dublicates;
	
	public ShowFoundDubles(Dublicates dublicates, MessageHandler messageHandler) {
		this.dublicates = dublicates;
		contactsProvider = new ContactsProvider(messageHandler);
		this.messageHandler = messageHandler;
		dbProvider = App.Instance().getDbProvider();			
	}
	
	public void Run()
	{		
		ShowList(dbProvider);		
	}

	private void ShowList(DbProvider dbProvider){	
		DublicatesContact dublicatesContact = contactsProvider.getDublicatesContact(dublicates);
		Functions func = new Functions();
		int i = 0;
		int countByName = 0;
		int countByPhone = 0;
		if (dublicatesContact.getDublicatesByName() != null) 
			countByName = dublicatesContact.getDublicatesByName().length;
		if (dublicatesContact.getDublicatesByPhone() != null) 
			countByPhone = dublicatesContact.getDublicatesByPhone().length;	
		int count = countByPhone + countByName;
		
		String[] showArray = new String[count + 1];
		
		for(; i < countByName; i++ ){
			showArray[i] = func.ContactToString(dublicatesContact.getDublicatesByName()[i]);
		}
		for(; i < count; i++ ){			
			showArray[i] = func.ContactToString(dublicatesContact.getDublicatesByPhone()[i-countByName]);
		}
		
		showArray[count] = func.ContactToString(dublicatesContact.getContact());
		
		ShowList showList = new ShowList("Dublicates of contact:", showArray);		
		Message.obtain (messageHandler, MessageType.ShowListView.ordinal(), showList).sendToTarget();			
	}			
}

