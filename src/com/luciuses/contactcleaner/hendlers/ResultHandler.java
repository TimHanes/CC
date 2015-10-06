package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.*;
import com.luciuses.contactcleaner.components.ActionType;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.providers.DbProvider;
import com.luciuses.contactcleaner.treads.*;

import android.net.Uri;

public class ResultHandler 
	{
	
	private DbProvider dbProvider;
	private BaseThread resultHandlerThread;
	private ExecutorThread executor;
	
	public ResultHandler(ExecutorThread executor){		
		this.executor = executor;	
		dbProvider = App.Instance().getDbProvider();
	}
	
	public void ShowList()
	{		
		resultHandlerThread = new ShowAllThread(executor.getMessageHandler());	
		resultHandlerThread.start();
	}
	
	public void ShowDublicates(Dublicates dubl)
	{				
		resultHandlerThread = new ShowFoundDublesThread(dubl, executor.getMessageHandler());
		resultHandlerThread.start();	
	}
	
	public void ShowChooseAction(Uri uri){		
		resultHandlerThread = new RequestActionThread(uri, executor.getMessageHandler());
		resultHandlerThread.start();
	}
	
	public BaseThread getTread(){
		return resultHandlerThread;
	}

	public void Executed(Dublicates dubl, Uri uri) {
		
		ActionType _whichChoos = ActionType.values()[executor.getAction()];

		switch (_whichChoos) {
		
		case None:
			
			break;
			
		case Delete:			
			resultHandlerThread = new DeletedThread(dubl, uri, executor.getMessageHandler());
			resultHandlerThread.start();		
			UpdateContact(dubl, uri);
			break;
			
		case Join:
			
			break;
			
		case Ignore:
			
			break;	
			
		default:
			break;
		}
		
				
	}	
	
	private void UpdateContact(Dublicates dubl, Uri deletedUri) {
		
		Uri uri = null;
		if(dubl.getContactUri() != deletedUri) uri = dubl.getContactUri();
		else {
			if(dubl.getUriDublicatesByPhone() != null)
				uri = dubl.getUriDublicatesByPhone()[0];
			if(dubl.getUriDublicatesByName() != null)
				uri = dubl.getUriDublicatesByName()[0];
		}
		
		SearchDublicateThread searchDublicateThread = new SearchDublicateThread(uri, executor);
		searchDublicateThread.start();
		
		try {
			searchDublicateThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Dublicates dubls = searchDublicateThread.getDublicates();
		if (dubls != null) {
			dbProvider.Save(dubls);
		}			
	}
}
