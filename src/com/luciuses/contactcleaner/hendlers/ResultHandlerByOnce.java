package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.providers.DbProvider;
import com.luciuses.contactcleaner.treads.RequestActionThread;

import android.net.Uri;

public class ResultHandlerByOnce 
	{	
	private MessageHandler messageHandler;
	private RequestActionThread requestActionThread;
	
	public ResultHandlerByOnce(MessageHandler messageHandler){		
		this.messageHandler = messageHandler;	
	}

	public void start(DbProvider dbProvider){		
		Uri[] uris = dbProvider.getContactsUri();
		for (int i =0; i< uris.length; i++){				
			requestActionThread = new RequestActionThread(i, messageHandler);			
		}						
	}
	
	public BaseThread getTread(){
		return requestActionThread;
	}
	
	
}
