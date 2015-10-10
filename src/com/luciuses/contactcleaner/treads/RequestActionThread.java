package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.providers.*;

import android.net.Uri;
import android.os.Message;

public class RequestActionThread extends BaseThread{
	
	private ContactsProvider contactsProvider;
	private Uri uri;	
	private MessageHandler messageHandler;
	
	public RequestActionThread(Uri uri, MessageHandler messageHandler){
		
		this.messageHandler = messageHandler;
		contactsProvider = new ContactsProvider(messageHandler);
		this.uri = uri;	
		super.setName("RequestActionThread");
	}
		
	
	@Override
	public void run(){
		
		RequestAction(uri);
	}

	private void RequestAction(Uri uri) {
		
		Contact contact = contactsProvider.getContactByUri(uri);
		
		Message.obtain(messageHandler, MessageType.ShowPopupForChooseAction.ordinal(),
					"Contact:" + new Functions().ContactToString(contact)).sendToTarget();//		
	}
}

		
		

