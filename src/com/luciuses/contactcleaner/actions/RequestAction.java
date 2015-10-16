package com.luciuses.contactcleaner.actions;

import com.luciuses.contactcleaner.ProviderContactsDb;
import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.providers.*;

import android.net.Uri;
import android.os.Message;

public class RequestAction {
	
	private ProviderContactsDb contactsProvider;
	private String id;	
	private MessageHandler messageHandler;
	
	public RequestAction(MessageHandler messageHandler){		
		this.messageHandler = messageHandler;
		contactsProvider = new ProviderContactsDb(messageHandler);
		this.id = id;	
	}		
	
	public void Run(String id){
		
		Contact contact = contactsProvider.getContactById(id);	
		Message.obtain(messageHandler, MessageType.ShowPopupForChooseAction.ordinal(),
					"Contact:" + new Functions().ContactToString(contact)).sendToTarget();//
	}
}

		
		

