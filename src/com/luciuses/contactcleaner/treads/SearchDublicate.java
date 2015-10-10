package com.luciuses.contactcleaner.treads;

import java.util.ArrayList;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.models.Options;
import com.luciuses.contactcleaner.providers.ContactsProvider;

import android.net.Uri;
import android.os.Message;

public class SearchDublicate {

	private Dublicates dublicates;
	private ContactsProvider contactsProvider;
	private ExecutorThread executor;
	private Contact contact;
	private Options options;
	
	
	public SearchDublicate( Contact contact,  ExecutorThread executor) {	
		this.options = new Options();
		this.contact = contact;
		this.executor = executor;
		contactsProvider = new ContactsProvider(executor.getMessageHandler());		
	}
	
	public SearchDublicate( Uri uriContact, int options, ExecutorThread executor) {
		this.options = new Options(options);
		this.executor = executor;
		contactsProvider = new ContactsProvider(executor.getMessageHandler());
		this.contact = contactsProvider.getContactByUri(uriContact);		
	}

	public Dublicates getDublicates() {
		return dublicates;
	}
	
	public void Run() {		
		this.options = new Options();
		dublicates = SearchDubl(contact);
		if (dublicates!=null){
			Contact contact = contactsProvider.getContactByUri(dublicates.getContactUri());
			if(!CheckDelNull(contact))
				dublicates = null; 
		}
	}
	
	private Dublicates SearchDubl(Contact contact) {

		if ((options.isByName()) & (options.isByPhone())) {
			return CheckByNameAndPhone(contact);
		}

		if (options.isByName()) {
			return CheckByName(contact);
		}

		if (options.isByPhone()) {
			return CheckByPhone(contact);
		}
		return null;
	}

	private Dublicates CheckByPhone(Contact contact) {
		if(contact.getPhones() == null)
			return null;
		Uri[] chConUriByPhone = null;		
		String[] phones = contact.getPhones().split("\r\n");
		for (int p = 0; p < phones.length; p++) {
			Uri[] foundUris = contactsProvider.getContactUrisByPhone(phones[p]);			
				chConUriByPhone = CheckContacts(contact, foundUris);
		}
		if (chConUriByPhone == null)
			return null;
		return new Dublicates(contactsProvider.getUriByContactId(contact.getId()), options.getOptions(), null, chConUriByPhone);
	}

	private Dublicates CheckByName(Contact contact) {
		Uri[] foundUri = contactsProvider.getContactsUriByName(contact.getName(), false);
		Uri[] chConUriByName = CheckContacts(contact, foundUri);
		if (chConUriByName == null)
			return null;
		return new Dublicates(contactsProvider.getUriByContactId(contact.getId()), options.getOptions(), chConUriByName, null);
	}

	private Dublicates CheckByNameAndPhone(Contact contact) {
		Uri[] chConUriByPhone = null;
		Uri[] foundUriByName = contactsProvider.getContactsUriByName(contact.getName(), false);
		Uri[] chConUriByName = CheckContacts(contact, foundUriByName);
		String phonesStr = contact.getPhones();
		String[] phones;
		if (phonesStr != null) {
			phones = phonesStr.split("\r\n");
			for (int p = 0; p < phones.length; p++) {
				Uri[] foundUriByPhone = contactsProvider.getContactUrisByPhone(phones[p]);
				chConUriByPhone = CheckContacts(contact, foundUriByPhone);
			}
		}
		if ((chConUriByName != null) || (chConUriByPhone != null)) {
			return new Dublicates(contactsProvider.getUriByContactId(contact.getId()), options.getOptions(), chConUriByName,
				chConUriByPhone);		
		}		
		return null;
	}

	private Uri[] CheckContacts(Contact contact, Uri[] uris) {
		ArrayList<Uri> contactsUri = new ArrayList<Uri>();
		if (uris != null) {
			for (int i = 0; i <= uris.length - 1; i++) {
				if (uris[i] != null)
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
		Contact finedContact = contactsProvider.getContactByUri(uri);
		if(finedContact == null)
			return false;
		if (finedContact.getId() > contact.getId()) {
			if(!CheckDelNull(finedContact)) 
				return false;
			Message.obtain(executor.getMessageHandler(), MessageType.AddToLogView.ordinal(),
					"find:" + new Functions().ContactToString(finedContact)).sendToTarget();
			return true;
		}
		return false;
	}
	
	private boolean CheckDelNull(Contact contact){
		if (options.isAutoDelNull()) {
			if (contact.getPhones() == null || contact.getPhones() == null) {
				Message.obtain(executor.getMessageHandler(), MessageType.AddToLogView.ordinal(),
						"find and deleted:" + new Functions().ContactToString(contact)).sendToTarget();
				contactsProvider.ContactDelete(contact.getId());
				return false;
			}
		}	
		return true;
	}
}

