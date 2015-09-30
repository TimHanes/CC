package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.net.Uri;
import android.os.Message;

public class RequestsActionsThread extends BaseThread{
	
	private ContactsProvider contactsProvider;
	private DbProvider dbProvider;
	private int position;
	private MessageHandler messageHandler;
	
	public RequestsActionsThread(int position, MessageHandler messageHandler){
		
		this.messageHandler = messageHandler;
		contactsProvider = new ContactsProvider(messageHandler);
		this.position = position;
		dbProvider = App.Instance().getDbProvider();	
		super.setName("RequestActionThread");
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
		Dublicates dubl = dbProvider.Read(uris[position]);		
		Uri contactUri = dubl.getContactUri();
		Contact contact = contactsProvider.getContactByUri(contactUri); 
		Message.obtain (messageHandler, MessageType.AddToLogView.ordinal(), "Work with:" + contact.getName() + "-"+ contact.getPhones()+"\r\n").sendToTarget ();
		int lenght = dubl.getUriDublicatesByName().length;
		for (int a =0; a< lenght ; a++ ){
			
			Uri uri = dubl.getUriDublicatesByName()[a];
			if(uri != null){						
				Message.obtain (messageHandler, MessageType.AddToLogView.ordinal(), "DablName:" + contactsProvider.getNameByUri(uri) + "-"+ contactsProvider.getPhonesByUri (uri)+ "\r\n").sendToTarget ();	
				Integer id = contactsProvider.getIdByUri(uri);
				if (id!=null){
					Message.obtain (messageHandler, MessageType.ShowPopupForChooseAction.ordinal(), contact.getId(), id , "Work with:" + contact.getName() + "\r\n" + contact.getPhones() + "find:" + contactsProvider.getNameByUri(uri) + "\r\n" + contactsProvider.getPhonesByUri (uri)).sendToTarget ();//		
					super.Pause();
					super.run();
				}
			} 					
		}
		Message.obtain (messageHandler, MessageType.ContactExecuted.ordinal(), dbProvider.getContactsUri().length ).sendToTarget ();
	}	
}
