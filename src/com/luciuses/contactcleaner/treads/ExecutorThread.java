package com.luciuses.contactcleaner.treads;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.hendlers.ResultHandler;

public class ExecutorThread extends BaseThread
{
	MessageHandler messageHandler;
	ContactsHandlerThread contactsHandler;
	ResultHandler resultHandler;

	public ExecutorThread(){		
		messageHandler = new MessageHandler(this);
	}
	
	public MessageHandler getMessageHandler(){
		return messageHandler;
	}
	
	public ResultHandler getResultHandler(){
		return resultHandler;
	}
	
	public ContactsHandlerThread getContactsHandler(){
		return contactsHandler;
	}
	
	@Override
	public void run()
	{
		StartContactHandler();
		whiteEndContactHandler();
		StartResultHandler();				
	}	
	
	private void whiteEndContactHandler() {
		try {
			contactsHandler.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}

	private void StartContactHandler(){
		contactsHandler =  new ContactsHandlerThread(this);		
		contactsHandler.start();		
	} 
	
	private void StartResultHandler(){
		
		resultHandler = new ResultHandler(this);
		while(App.Instance().getDbProvider().getContactsUri().length > 0){
			resultHandler.ShowList();
			this.Pause();
			super.run();
			}		
//		resultHandler.ByOnce();	
	}
}
	
	