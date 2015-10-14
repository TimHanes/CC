package com.luciuses.contactcleaner.actions;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.providers.*;

import android.net.Uri;
import android.os.Message;

public class RequestAction {
	
	private ContactsProvider contactsProvider;
	private Uri uri;	
	private MessageHandler messageHandler;
	
	public RequestAction(Uri uri, MessageHandler messageHandler){
		
		this.messageHandler = messageHandler;
		contactsProvider = new ContactsProvider(messageHandler);
		this.uri = uri;	
	}		
	
	public void Run(){
		
		Contact contact = contactsProvider.getContactByUri(uri);	
		Message.obtain(messageHandler, MessageType.ShowPopupForChooseAction.ordinal(),
					"Contact:" + new Functions().ContactToString(contact)).sendToTarget();//
	}
}

		
		

