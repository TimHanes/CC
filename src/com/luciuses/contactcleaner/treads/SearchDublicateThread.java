package com.luciuses.contactcleaner.treads;

import java.util.ArrayList;

import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.models.Options;
import com.luciuses.contactcleaner.providers.ContactsProvider;

import android.net.Uri;
import android.os.Looper;
import android.os.Message;

public class SearchDublicateThread extends BaseThread {

	private Dublicates dublicates;
	private ContactsProvider contactsProvider;
	private ExecutorThread executor;
	private Contact contact;
	
	
	public SearchDublicateThread( Contact contact, ExecutorThread executor) {		
		this.contact = contact;
		this.executor = executor;
		contactsProvider = new ContactsProvider(executor.getMessageHandler());
		super.setName("SearchDublicateThread");
	}
	
	public SearchDublicateThread( Uri uriContact, ExecutorThread executor) {		
		this.executor = executor;
		contactsProvider = new ContactsProvider(executor.getMessageHandler());
		this.contact = contactsProvider.getContactByUri(uriContact);
		super.setName("SearchDublicateThread");
	}

	public Dublicates getDublicates() {
		return dublicates;
	}

	@Override
	public void run() {		
		Looper.prepare();
		Options options = new Options();
		dublicates = SearchDublicate(contact, options);
	}
	
	private Dublicates SearchDublicate(Contact contact, Options options) {

		if ((options.ByName) & (options.ByPhone)) {
			return CheckByNameAndPhone(contact);
		}

		if (options.ByName) {
			return CheckByName(contact);
		}

		if (options.ByPhone) {
			return CheckByPhone(contact);
		}
		return null;
	}

	private Dublicates CheckByPhone(Contact contact) {
		Uri[] chConUriByPhone = null;
		String[] phones = contact.getPhones().split("\r\n");
		for (int p = 0; p < phones.length; p++) {
			Uri[] foundUri = contactsProvider.getContactUrisByPhone(phones[p]);
			if (foundUri != null)
				chConUriByPhone = CheckContacts(contact, foundUri);
		}
		if (chConUriByPhone == null)
			return null;
		return new Dublicates(contactsProvider.getUriByContactId(contact.getId()), null, chConUriByPhone);
	}

	private Dublicates CheckByName(Contact contact) {
		Uri[] foundUri = contactsProvider.getContactsUriByName(contact.getName(), false);
		Uri[] chConUriByName = CheckContacts(contact, foundUri);
		if (chConUriByName == null)
			return null;
		return new Dublicates(contactsProvider.getUriByContactId(contact.getId()), chConUriByName, null);
	}

	private Dublicates CheckByNameAndPhone(Contact contact) {
		Uri[] chConUriByPhone = null;
		Uri[] foundUriByName = contactsProvider.getContactsUriByName(contact.getName(), false);
		Uri[] chConUriByName = CheckContacts(contact, foundUriByName);
		String[] phones = contact.getPhones().split("\r\n");
		for (int p = 0; p < phones.length; p++) {
			Uri[] foundUriByPhone = contactsProvider.getContactUrisByPhone(phones[p]);
			chConUriByPhone = CheckContacts(contact, foundUriByPhone);
		}
		if ((chConUriByName != null) || (chConUriByPhone != null)) {
			return new Dublicates(contactsProvider.getUriByContactId(contact.getId()), chConUriByName,
				chConUriByPhone);		
		}
		return null;
	}

	private Uri[] CheckContacts(Contact contact, Uri[] uris) {
		ArrayList<Uri> contactsUri = new ArrayList<Uri>();
		if (uris != null) {
			for (int i = 0; i <= uris.length - 1; i++) {
				if (CompaireContact(contact, uris[i])) {
					contactsUri.add(uris[i]);
				}				
			}
		}
		if (contactsUri.isEmpty()) {
			return null;
		}		
		Uri[] contUriArrUri = new Uri[contactsUri.size()];
		contactsUri.toArray(contUriArrUri);		
		return contUriArrUri;
	}

	private boolean CompaireContact(Contact contact, Uri uri) {
		int fid = contactsProvider.getIdByUri(uri);
		if (fid > contact.getId()) {
			String fname = contactsProvider.getNameByUri(uri);
			Message.obtain(executor.getMessageHandler(), MessageType.AddToLogView.ordinal(),
					"find:" + fid + "-" + fname + "\r\n").sendToTarget();
			return true;
		}
		return false;
	}
}

