package com.luciuses.contactcleaner;

import java.util.ArrayList;

import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.actions.DuplicatesSearcher;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.components.StepType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.ShowList;

import android.os.Message;

public class Shower {
	private ProviderContactsDb contactsProvider;
	private Executor executor;
	private Functions function = new Functions();
	
	public Shower(Executor executor) {
		this.executor = executor;
		this.contactsProvider = executor.getContactsProvider();
	}

	public void ShowList(String[] sourses) {
		
		ShowList showList = new ShowList("All dublicates",sourses);		
		Show(showList);
		
	}

	public void ShowList(Duplicates duplicates) {
		if(duplicates == null)
			return;
		ArrayList<String> show = new ArrayList<String>();
		for(int i = 0; i < duplicates.getIdDuplicates().length; i++){
			Contact contact = contactsProvider.getContactById(duplicates.getIdDuplicates()[i]);
			if(contact == null){
				executor.getList().remove(duplicates.getSourse());
				App.Instance().getDbProvider().DuplicatesDelete(duplicates.getSourse());
				DuplicatesSearcher searcher = new DuplicatesSearcher(executor, duplicates.getWhere() );
				searcher.Search(duplicates.getType(), duplicates.getSourse());								
				continue;
			}
			show.add(function.ContactToString(contact));
		}	
		if(show.size() < 2){
			executor.setStep(StepType.ShowList);
			executor.Resume();
			Message.obtain (executor.getMessageHandler(), MessageType.ShowToast.ordinal(), 
					"Duplicates removed earlier!").sendToTarget();
			return;
		}
		String[] showArr = new String[show.size()];
		show.toArray(showArr);
		ShowList showList = new ShowList("dublicates of " + duplicates.getSourse(), showArr);		
		Show(showList);		
	}
	
	private void Show(ShowList showList){
		Message.obtain (executor.getMessageHandler(), MessageType.ShowListView.ordinal(), 
				showList).sendToTarget();
	}
	
	
	public void ShowChooseAction(String id) {
		Contact contact = contactsProvider.getContactById(id);	
		Message.obtain(executor.getMessageHandler(), MessageType.ShowPopupForChooseAction.ordinal(),
				"Contact:" + new Functions().ContactToString(contact)).sendToTarget();
		
	}

}
