package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.*;
import com.luciuses.contactcleaner.basis.*;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.hendlers.*;
import com.luciuses.contactcleaner.providers.*;

import android.net.Uri;
import android.os.Message;

public class ShowAllThread extends BaseThread
	{
	
	private DbProvider dbProvider;
	private ContactsProvider contactsProvider;
	private MessageHandler messageHandler;
	
	public ShowAllThread(MessageHandler messageHandler){
		contactsProvider = new ContactsProvider(messageHandler);
		this.messageHandler = messageHandler;
		dbProvider = App.Instance().getDbProvider();	
		super.setName("ShowAllThread");
	}
		
	
	@Override
	public void run()
	{		
		ShowList(dbProvider);		
	}

	private void ShowList(DbProvider dbProvider){	
		Uri[] uris = dbProvider.getContactsUri();
		String[] showArray = new String[uris.length];
		for(int i = 0 ; i < uris.length; i++ ){
			String name = contactsProvider.getNameByUri(uris[i]);
			String phones =  contactsProvider.getPhonesByUri(uris[i]);
			showArray[i] = name + phones;
		}
		Message.obtain (messageHandler, MessageType.ShowListView.ordinal(), 
				showArray).sendToTarget();			
	}			
}
