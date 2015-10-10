package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.net.Uri;

public class DeletedThread extends BaseThread {
	
	private ContactsProvider contactsProvider;
	private Dublicates dubl;
	private Uri uri;
	private DbProvider dbProvider;

	public DeletedThread(Dublicates dubl, Uri uri, MessageHandler messageHandler) {
		dbProvider = App.Instance().getDbProvider();
		contactsProvider = new ContactsProvider(messageHandler);
		this.dubl = dubl;
		this.uri = uri;
		super.setName("DeletedThread");
	}

	@Override
	public void run()
	{	
		Contact contact = contactsProvider.getContactByUri(uri);			
		contactsProvider.ContactDelete(contact.getId());		
		dbProvider.ContactDelete(dubl.getContactUri());	
	}
}
