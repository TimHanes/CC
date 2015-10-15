package com.luciuses.contactcleaner.actions;

import java.util.ArrayList;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.models.Options;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.treads.ExecutorThread;

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
		ArrayList<Uri> conUriByName = null ;
		ArrayList<Uri> conUriByPhone = null;
		if (options.isByName()) {
			conUriByName = CheckByName(contact); 
		}

		if (options.isByPhone()) {
			conUriByPhone = CheckByPhone(contact);
		}		
		if ((conUriByPhone != null) || (conUriByName != null)) {
			Uri[] conUriByNameArr = ListToArray(conUriByName);
			Uri[] conUriByPhoneArr = ListToArray(conUriByName);
			return  new Dublicates(contactsProvider.getUriByContactId(contact.getId()), options.getOptions(), conUriByNameArr, conUriByPhoneArr);		
		}		
		return null;		
	}

	private ArrayList<Uri> CheckByPhone(Contact contact) {
		if(contact.getPhones() == null)
			return null;
		ArrayList<Uri> conUris = new ArrayList<Uri>();		
		String[] phones = contact.getPhones().split("\r\n");
		for (int p = 0; p < phones.length; p++) {
			Uri[] foundUris = contactsProvider.getContactUrisByPhone(phones[p]);			
				CheckContacts(contact, foundUris, conUris);
		}
		if (conUris.isEmpty()) {
			return null;
		}		
		return conUris;
	}

	private ArrayList<Uri> CheckByName(Contact contact) {
		Uri[] foundUri = contactsProvider.getContactsUriByName(contact.getName(), false);
		ArrayList<Uri> conUris = new ArrayList<Uri>();
		CheckContacts(contact, foundUri, conUris);
		if (conUris.isEmpty()) {
			return null;
		}				
		return conUris;
	}
	
	private void CheckContacts(Contact contact, Uri[] uris, ArrayList<Uri> conUri) {		
		if (uris != null) {
			for (int i = 0; i <= uris.length - 1; i++) {
				if (uris[i] != null){
					Boolean resultCompare = CompaireContact(contact, uris[i]);	
				if (resultCompare == null){
					conUri.clear();
					return;
				} 										
				if (resultCompare)
					if(!conUri.contains(uris[i]))
					conUri.add(uris[i]);
				}
			}
		}									
	}

	private Boolean CompaireContact(Contact contact, Uri uri) {
		Contact finedContact = contactsProvider.getContactByUri(uri);
		if(finedContact == null)
			return false;
		if (finedContact.getId() < contact.getId())
			return null;
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
	
	private Uri[] ListToArray(ArrayList<Uri> list){
		if (!list.isEmpty()) {					
			Uri[] Arr = new Uri[list.size()];
			list.toArray(Arr);
			return Arr;
		}
		return null;
	}
}

