package com.luciuses.contactcleaner.actions;

import java.util.ArrayList;

import com.luciuses.contactcleaner.*;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Options;

public class DuplicatesSearcher {

	private ProviderContactsDb contactsProvider;
	private ProviderDuplicatesDb dbProvider;
	private Options options;
	private ArrayList<String> list;
	
	public DuplicatesSearcher( Executor executor) {					
		contactsProvider = executor.getContactsProvider();
		dbProvider = executor.getDbProvider();
		list = executor.getList();
		this.options = new Options();		
	}
	
	public void Search(Contact contact) {
		SearchDupl(contact);
	}
	
	private void SearchDupl(Contact contact) {
		if (options.isByName()) {
			CheckByName(contact.getName()); 
		}

		if (options.isByPhone()) {
			CheckByPhones(contact.getPhones());
		}				
	}

	private void CheckByName(String name) {
		if(!list.contains(name)){			
		String[] duplicatesId = contactsProvider.getContactsIdByName(name);
		if(duplicatesId == null)
			return;
		list.add(name);
		Duplicates duplicates = new Duplicates(SourseType.Name, name, duplicatesId);
		dbProvider.Save(duplicates);
		}
	}
	
	private void CheckByPhones(String[] phones) {
		if(phones!= null)
		for (int p = 0; p < phones.length; p++) {
			CheckByPhone(phones[p]);			
		}
	}

	private void CheckByPhone(String phone) {
		
		;
		
		if(!isContainsList(phone)){			
		String[] duplicatesId = contactsProvider.getContactsIdByPhone(phone);
		if(duplicatesId == null) 
			return;
		list.add(phone);
		Duplicates duplicates = new Duplicates(SourseType.Phone, phone, duplicatesId);
		dbProvider.Save(duplicates);
		}
	}

	private boolean isContainsList(String phone) {
		if(phone == null) return true;
		phone = SimplerPhone(phone, 5);
		for( int i =0; i < list.size(); i++){
			String listPhone = list.get(i);
			listPhone = SimplerPhone(listPhone, 5);
			if(listPhone.equals(phone)) 
				return true;
		}				
		return false;
	}

	private String SimplerPhone(String phone, int numer) {
		
		phone = phone.replaceAll("\\D+","");
    	phone = phone.substring(phone.length()-(phone.length() < numer ? phone.length(): numer ));
		return phone;
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

