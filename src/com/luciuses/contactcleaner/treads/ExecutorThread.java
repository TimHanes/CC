package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.hendlers.*;
import com.luciuses.contactcleaner.models.*;
import com.luciuses.contactcleaner.providers.DbProvider;

import android.R;
import android.net.Uri;
import android.os.Message;
import android.widget.Spinner;

public class ExecutorThread extends BaseThread
{
	private Integer clickPosition = null;
	MessageHandler messageHandler;
	ContactsHandler contactsHandler;
	ResultHandler resultHandler;
	private int curentResultHandlerAction = 0;

	private DbProvider dbProvider;
	private ActionType action;
	private boolean mFinish;

	public ExecutorThread(){		
		this.dbProvider = App.Instance().getDbProvider();
		messageHandler = new MessageHandler(this);
		super.setName("ExecutorThread");
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
	public void run() {
		while (true) {
			super.run();
			
			if(dbProvider.getContactsUri().length == 0){
				Options options = new Options();
				if ((options.ByName) | (options.ByPhone))
					StartContactHandler();
				else 
					Message.obtain(messageHandler, MessageType.ShowToast.ordinal(),
						"Set options for choosing please!").sendToTarget();				
				}
			StartResultHandler();
			this.Pause();
			super.run();
		}
	}	

	private void StartContactHandler(){
		contactsHandler =  new ContactsHandler(this);		
		contactsHandler.Start();		
	} 
	
	private void StartResultHandler(){
		
		resultHandler = new ResultHandler(this);
		Uri uri = null;
		Dublicates dubl = null;
		while(dbProvider.getContactsUri().length > 0){				
			
			ResultHandlerActionType _whichChoos = ResultHandlerActionType.values()[curentResultHandlerAction];
		
			switch (_whichChoos) {
			
			case ShowList:
				
				resultHandler.ShowList();
				this.Pause();
				super.run();
				break;
			case ShowDublicates:
				dubl = getDublicatesByPosition(clickPosition);
				resultHandler.ShowDublicates(dubl);
				this.Pause();
				super.run();
				break;
			case ShowChooseAction:
				uri = getUriFromDublicatesByPosition(dubl ,clickPosition);
				resultHandler.ShowChooseAction(uri);				
				this.Pause();
				super.run();
				break;
			case Executed:
				resultHandler.Executed(dubl, uri);	
				FirstResultAction();
				break;			
			default:
				break;				
			}			
		}		
	}

	public void NextResultAction(){
		curentResultHandlerAction++;
	}
	
	public void FirstResultAction(){
		curentResultHandlerAction = 0;
	}
	
	private Uri getUriFromDublicatesByPosition(Dublicates dubl, Integer clickPosition) {
		int countByName = 0;
		int countByPhone = 0;
		if((dubl.getUriDublicatesByName()!= null)) 
			countByName = dubl.getUriDublicatesByName().length;		
		if((dubl.getUriDublicatesByPhone()!= null)) 
			countByName = dubl.getUriDublicatesByPhone().length;
		if (clickPosition < countByName)
			return dubl.getUriDublicatesByName()[clickPosition];	
		if (clickPosition < countByName + countByPhone)
			return dubl.getUriDublicatesByPhone()[clickPosition];
		return dubl.getContactUri();				
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

	public boolean ismFinish() {
		return mFinish;
	}

	public void setmFinish(boolean mFinish) {
		this.mFinish = mFinish;
	}

	
}
	
	