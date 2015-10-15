package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.actions.*;
import com.luciuses.contactcleaner.basis.*;
import com.luciuses.contactcleaner.components.ActionType;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.providers.DbProvider;
import com.luciuses.contactcleaner.treads.ExecutorThread;

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
		ShowAll showAll = new ShowAll(executor);	
		showAll.Run();
	}
	
	public void ShowDublicates(Dublicates dubl)
	{				
		ShowFoundDubles showFoundDubles = new ShowFoundDubles(dubl, executor.getMessageHandler());
		showFoundDubles.Run();
	}
	
	public void ShowChooseAction(Uri uri){		
		RequestAction requestAction = new RequestAction(uri, executor.getMessageHandler());
		requestAction.Run();
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
			Deleted deleted = new Deleted(dubl, uri, executor.getMessageHandler());
			deleted.Run();		
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
		if(dubl.getContactUri() != deletedUri){
			uri = dubl.getContactUri();
			searchDublicates(dubl.getOptions(), uri);
			}
		else {			
			if(dubl.getUriDublicatesByName() != null)
				uri = dubl.getUriDublicatesByName()[0];
			searchDublicates(dubl.getOptions(), uri);
			if(dubl.getUriDublicatesByPhone() != null)
				uri = dubl.getUriDublicatesByPhone()[0];
			searchDublicates(dubl.getOptions(), uri);
		}				
	}
	
	private void searchDublicates(int options, Uri uri){
		if(dbProvider.isContainsContact(uri))
			return;
		SearchDublicate searchDublicateThread = new SearchDublicate(uri, options,executor);
		searchDublicateThread.Run();				
		Dublicates dubls = searchDublicateThread.getDublicates();
		if (dubls != null) {
			dbProvider.Save(dubls);
		}			
	}

	
}
