package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.treads.RequestsActionsThread;

import android.net.Uri;

public class ResultHandlerByOnce 
	{	
	private MessageHandler messageHandler;
	private RequestsActionsThread requestsActionsThread;
	
	public ResultHandlerByOnce(MessageHandler messageHandler){		
		this.messageHandler = messageHandler;	
	}

	public void Start(){		
		Uri[] uris = App.Instance().getDbProvider().getContactsUri();
		for (int position =0; position< uris.length; position++){				
			requestsActionsThread = new RequestsActionsThread(position, messageHandler);			
		}						
	}
	
	public BaseThread getTread(){
		return requestsActionsThread;
	}
	
	
}
