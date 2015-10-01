package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.providers.*;

import android.net.Uri;
import android.os.Message;

public class RequestActionThread extends BaseThread{
	
	private ContactsProvider contactsProvider;
	private Uri uri;	
	private Dublicates dubl;
	private MessageHandler messageHandler;
	
	public RequestActionThread(Dublicates dubl, Uri uri, MessageHandler messageHandler){
		
		this.messageHandler = messageHandler;
		contactsProvider = new ContactsProvider(messageHandler);
		this.uri = uri;	
		this.dubl = dubl;	
		super.setName("RequestActionThread");
	}
		
	
	@Override
	public void run(){
		
		RequestAction(dubl, uri);
	}

	private void RequestAction(Dublicates dubl, Uri uri) {
		
		Contact contact = contactsProvider.getContactByUri(dubl.getContactUri());
		Contact contactDul = contactsProvider.getContactByUri(uri);
		AddToLogView("Work with:", contact);
		AddToLogView("Dublicate:", contactDul);
		
		Integer id = contactsProvider.getIdByUri(uri);
		if (id != null) {
			Message.obtain(messageHandler, MessageType.ShowPopupForChooseAction.ordinal(), contact.getId(), id,
					"Work with:" + contact.getName() + "\r\n" + contact.getPhones() + "find:"
							+ contactsProvider.getNameByUri(uri) + "\r\n" + contactsProvider.getPhonesByUri(uri))
					.sendToTarget();//
		}
	}
	
	private void AddToLogView(String text, Contact contact){
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(),
				text+ "ID(" + contact.getId() + ") "+ contact.getName() + " - " + contact.getPhones() + "\r\n").sendToTarget();	
	}
}

		
		

