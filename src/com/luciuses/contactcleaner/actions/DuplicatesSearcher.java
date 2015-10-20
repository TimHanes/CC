package com.luciuses.contactcleaner.actions;

import java.util.ArrayList;

import com.luciuses.contactcleaner.*;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Options;
import android.annotation.SuppressLint;
import android.telephony.PhoneNumberUtils;

public class DuplicatesSearcher {

	private ProviderContactsDb contactsProvider;
	private ProviderDuplicatesDb dbProvider;
	private Options options;
	private ArrayList<String> list;
	private String where;
	
	public DuplicatesSearcher( Executor executor, String where) {	
		this.where = where;
		contactsProvider = executor.getContactsProvider();
		dbProvider = executor.getDbProvider();
		list = executor.getList();
		this.options = new Options();	
		contactsProvider.setAutoDel(options.isAutoDel());
	}
	
	public void Search(Contact contact) {
		SearchDupl(contact);
	}
	
	private void SearchDupl(Contact contact) {
		if (options.isByName()) {
			CheckByName(contact); 
		}

		if (options.isByPhone()) {
			CheckByPhones(contact);
		}				
	}

	@SuppressLint("DefaultLocale")
	private void CheckByName(Contact contact) {
		String name = contact.getName().toUpperCase();
		if(!list.contains(name)){			
		String[] duplicatesId = contactsProvider.getContactsIdByName(name);
		if(duplicatesId == null)
			return;						
		list.add(name);
		Duplicates duplicates = new Duplicates(SourseType.Name, name, where, duplicatesId);
		dbProvider.Save(duplicates);
		}
	}

	@SuppressLint("DefaultLocale")
	private void CheckByName(String name) {
		name = name.toUpperCase();
		if(!list.contains(name)){			
		String[] duplicatesId = contactsProvider.getContactsIdByName(name);
		if(duplicatesId == null)
			return;		
		list.add(name);
		Duplicates duplicates = new Duplicates(SourseType.Name, name, where, duplicatesId);
		dbProvider.Save(duplicates);
		}
	}
	
	private void CheckByPhones(Contact contact) {
		if(contact.getPhones()!= null)
		for (int p = 0; p < contact.getPhones().length; p++) {
			String[] duplicatesId = CheckByPhone(contact.getPhones()[p]);
			if(duplicatesId != null){					
				list.add(contact.getPhones()[p]);
				Duplicates duplicates = new Duplicates(SourseType.Phone, contact.getPhones()[p], where, duplicatesId);
				dbProvider.Save(duplicates);
			}			
		}
	}

	private String[] CheckByPhone(String phone) {

		if(!isContainsList(phone)){			
		String[] duplicatesId = contactsProvider.getContactsIdByPhone(phone);
		if(duplicatesId == null) 
			return null;
		return duplicatesId;
		}
		return null;
	}

	private boolean isContainsList(String phone) {
		if(phone == null) return true;
		
		for( int i =0; i < list.size(); i++){
			String listPhone = list.get(i);		
			if(PhoneNumberUtils.compare(listPhone, phone)) 
				return true;			
		}				
		return false;
	}

	public void Search(SourseType sourseType, String sourse) {

		switch (sourseType) {
		
		case Name:
			CheckByName(sourse);
			break;
		case Phone:
			CheckByPhone(sourse);
			break;	
		}
	}
}

