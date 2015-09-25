package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.DublicatesOfContact;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.net.Uri;
import android.os.Message;

public class RequestActionThread extends BaseThread{
	
	private ContactsProvider contactsProvider;
	private DbProvider dbProvider;
	private int position;
	private MessageHandler messageHandler;
	
	public RequestActionThread(int position, MessageHandler messageHandler){
		
		this.messageHandler = messageHandler;
		contactsProvider = new ContactsProvider(messageHandler);
		this.position = position;
		dbProvider = App.Instance().getDbProvider();		
	}
		
	
	@Override
	public void run(){
		
		RequestAction(position);
	}

	private void RequestAction(int position ){
		
		ContactHandler(position);											
	}

	private void ContactHandler(int position) {
		
		Uri[] uris = dbProvider.getContactsUri();
		DublicatesOfContact dubl = dbProvider.Read(uris[position]);
		
		Uri contactUri = dubl.getContactUri();
		Contact contact = new Contact (contactsProvider.IdByUri(contactUri),contactsProvider.NameByUri(contactUri),contactsProvider.PhonesByUri(contactUri)); 
		Message.obtain (messageHandler, MessageType.AddToLogView.ordinal(), "Work with:" + contact.getName() + "-"+ contact.getPhone()+"\r\n").sendToTarget ();
		int lenght = dubl.getUriDublicatesByName().length;
		for (int a =0; a< lenght ; a++ ){
			
			Uri uri = dubl.getUriDublicatesByName()[a];
			if(uri != null){						
				Message.obtain (messageHandler, MessageType.AddToLogView.ordinal(), "DablName:" + contactsProvider.NameByUri(uri) + "-"+ contactsProvider.PhonesByUri (uri)+ "\r\n").sendToTarget ();				
				Message.obtain (messageHandler, MessageType.ShowPopupForChooseAction.ordinal(), contact.getId(), contactsProvider.IdByUri(uri), "Work with:" + contact.getName() + "\r\n" + contact.getPhone() + "find:" + contactsProvider.NameByUri(uri) + "\r\n" + contactsProvider.PhonesByUri (uri)).sendToTarget ();//		
				super.Pause();
				super.run();
			} 					
		}
		Message.obtain (messageHandler, MessageType.ContactExecuted.ordinal(), dbProvider.getContactsUri().length ).sendToTarget ();
	}	
}
