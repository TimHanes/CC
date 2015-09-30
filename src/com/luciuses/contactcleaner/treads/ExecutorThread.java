package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.ActionType;
import com.luciuses.contactcleaner.hendlers.*;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.net.Uri;

public class ExecutorThread extends BaseThread
{
	private Integer clickPosition = null;
	MessageHandler messageHandler;
	ContactsHandler contactsHandler;
	ResultHandler resultHandler;
	private boolean flagFinished = false;
	private DbProvider dbProvider;
	private ActionType action;

	public ExecutorThread(){		
		this.dbProvider = App.Instance().getDbProvider();
		messageHandler = new MessageHandler(this);
		super.setName("ExecutorThread");
	}
	
	public boolean isFlagFinished() {
		return flagFinished;
	}

	public void setFlagFinished(boolean flagFinished) {
		this.flagFinished = flagFinished;
	}
	
	public MessageHandler getMessageHandler(){
		return messageHandler;
	}
	
	public ResultHandler getResultHandler(){
		return resultHandler;
	}
	
	public ContactsHandler getContactsHandler(){
		return contactsHandler;
	}
	
	@Override
	public void run()
	{
		StartContactHandler();
		StartResultHandler();				
	}	

	private void StartContactHandler(){
		contactsHandler =  new ContactsHandler(this);		
		contactsHandler.Start();		
	} 
	
	private void StartResultHandler(){
		
		resultHandler = new ResultHandler(this);
		while(!flagFinished){				
			resultHandler.ShowList();
			this.Pause();
			super.run();
			Dublicates dubl = getDublicatesByPosition(clickPosition);
			resultHandler.ShowDublicates(dubl);
			this.Pause();
			super.run();
			Uri uri = getUriFromDublicatesByPosition(dubl ,clickPosition);
			resultHandler.ShowChooseAction(uri);
			this.Pause();
			super.run();
			resultHandler.Executed(dubl, uri);
			
			}		
	}

	private Uri getUriFromDublicatesByPosition(Dublicates dubl, Integer clickPosition) {
		if (dubl.getUriDublicatesByName().length > clickPosition)
			return dubl.getUriDublicatesByName()[clickPosition];		
		return dubl.getUriDublicatesByPhone()[clickPosition - dubl.getUriDublicatesByName().length];				
	}

	private Dublicates getDublicatesByPosition(Integer clickPosition) {
		Uri[] uris = dbProvider.getContactsUri();
		Dublicates dubl = dbProvider.Read(uris[clickPosition]);					
		return dubl;
	}

	public void setClickPosition(int position) {
		clickPosition = position;
		
	}
	
	public int getClickPosition() {
		return clickPosition;		
	}

	public int getAction() {
		return action.ordinal();
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	
}
	
	