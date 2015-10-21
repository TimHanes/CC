package com.luciuses.contactcleaner.actions;

import java.util.ArrayList;

import com.luciuses.contactcleaner.*;
import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Options;
import android.annotation.SuppressLint;
import android.os.Message;
import android.telephony.PhoneNumberUtils;

public class DuplicatesSearcher {

	private ProviderContactsDb contactsProvider;
	private ProviderDuplicatesDb dbProvider;
	private Options options;
	private ArrayList<String> list;
	private String where;
	private MessageHandler messageHandler;
	
	public DuplicatesSearcher( Executor executor, String where) {
		messageHandler = executor.getMessageHandler();
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

	private void CheckByName(Contact contact) {
		if(!list.contains(contact.getName())){			
		String[] duplicatesId = contactsProvider.getContactsIdByName(contact.getName());
		if(duplicatesId == null)
			return;						
		AddtoListDb(SourseType.Name, contact.getName(), duplicatesId);
		}
	}

	private void CheckByName(String name) {		
		if(!list.contains(name)){			
		String[] duplicatesId = contactsProvider.getContactsIdByName(name);
		if(duplicatesId == null)
			return;		
		AddtoListDb(SourseType.Name ,name, duplicatesId);
		}
	}
	
	private void CheckByPhones(Contact contact) {
		if(contact.getPhones()!= null)
		for (int p = 0; p < contact.getPhones().length; p++) {
			CheckByPhone(contact.getPhones()[p]);						
		}
	}

	private void CheckByPhone(String phone) {

		if(!isContainsList(phone)){			
			String[] duplicatesId = contactsProvider.getContactsIdByPhone(phone);
			if(duplicatesId != null){	
				AddtoListDb(SourseType.Phone, phone, duplicatesId);				
			}	
		}		
	}

	private void AddtoListDb(SourseType type, String sourse, String[] duplicatesId) {
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(), "Fond duplicates by: " 
				+ sourse + "\r\n").sendToTarget();
		list.add(sourse);
		Duplicates duplicates = new Duplicates(type, sourse, where, duplicatesId);
		dbProvider.Save(duplicates);
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

