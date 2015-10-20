package com.luciuses.contactcleaner;

import com.luciuses.contactcleaner.actions.DuplicatesSearcher;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.components.StepType;
import com.luciuses.contactcleaner.models.Contact;

import android.net.Uri;
import android.os.Message;

public class Scaner {

	private Executor executor;
	private DuplicatesSearcher duplicatesSearcher;
	
	public Scaner(Executor executor) {
		this.executor = executor;				
	}

	public void Scan(CursorModel where) {
		
		executor.getContactsProvider().setWhere(where);		
		executor.setFinish(false);
		int contactCount = executor.getContactsProvider().getCountAllContacts();
		duplicatesSearcher = new DuplicatesSearcher(executor);		
		for (int position = 0; position < contactCount; position++) {
			Contact contact = executor.getContactsProvider().getContactByPosition(position);
			if(contact == null)
				continue;	
			duplicatesSearcher.Search(contact);
			if (executor.isFinish())				
				break;
			ProgressInfo(contactCount, position, contact);			
		}	
		Message.obtain(executor.getMessageHandler(), MessageType.SetButtonsApp.ordinal()).sendToTarget();
		if(executor.getList().isEmpty())
			executor.setStep(StepType.Finish);		
	}
	
	private void ProgressInfo(int contactCount, int position, Contact contact) {

		Message.obtain(executor.getMessageHandler(), MessageType.ShowProgress.ordinal(), contactCount, position,
				contact.getName() + "\r\n" + "Found dublicates:" + executor.getList().size()).sendToTarget();
	}
}
