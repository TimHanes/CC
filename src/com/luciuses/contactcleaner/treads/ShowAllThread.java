package com.luciuses.contactcleaner.treads;

import java.util.ArrayList;

import com.luciuses.contactcleaner.*;
import com.luciuses.contactcleaner.basis.*;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.*;

import android.net.Uri;
import android.os.Message;

public class ShowAllThread extends BaseThread
	{
	
	private DbProvider dbProvider;
	private ContactsProvider contactsProvider;
	private ExecutorThread executor;
	
	public ShowAllThread(ExecutorThread executor){
		this.executor = executor;		
		contactsProvider = new ContactsProvider(executor.getMessageHandler());		
		dbProvider = App.Instance().getDbProvider();	
		super.setName("ShowAllThread");
	}
		
	
	@Override
	public void run()
	{		
		ShowList(dbProvider);		
	}

	private void ShowList(DbProvider dbProvider){	
		Uri[] uris = dbProvider.getContactsUri();
		
		ArrayList<String> contacts = new ArrayList<String>();
		executor.setmFinish(false);
		for(int i = 0 ; i < uris.length; i++ ){			
			Contact contact = contactsProvider.getContactByUri(uris[i]);			
			contacts.add(contact.getId()+"\r\n" +contact.getName() + "\r\n" + contact.getPhones() + "\r\n");
			Message.obtain(executor.getMessageHandler(), MessageType.ShowProgress.ordinal(), uris.length, i,
					"Create List. Please wait").sendToTarget();
			if(executor.ismFinish())
				break;
		}
		String[] showArray = new String[contacts.size()];
		contacts.toArray(showArray);
		
		ShowList showList = new ShowList("All Contacts with dublicates",showArray);		
		Message.obtain (executor.getMessageHandler(), MessageType.ShowListView.ordinal(), 
				showList).sendToTarget();			
	}			
}
