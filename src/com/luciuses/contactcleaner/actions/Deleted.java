package com.luciuses.contactcleaner.actions;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.net.Uri;

public class Deleted {
	
	private ContactsProvider contactsProvider;
	private Dublicates dubl;
	private Uri uri;
	private DbProvider dbProvider;

	public Deleted(Dublicates dubl, Uri uri, MessageHandler messageHandler) {
		dbProvider = App.Instance().getDbProvider();
		contactsProvider = new ContactsProvider(messageHandler);
		this.dubl = dubl;
		this.uri = uri;
	}

	public void Run()
	{	
		Contact contact = contactsProvider.getContactByUri(uri);			
		contactsProvider.ContactDelete(contact.getId());		
		dbProvider.ContactDelete(dubl.getContactUri());	
	}
}
