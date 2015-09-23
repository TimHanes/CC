package com.luciuses.contactcleaner;


import android.database.Cursor;
import android.net.Uri;
import android.os.Message;

public class ResultHandler 
	{
	
	private ActionType _defaultAction = ActionType.None;
	private ContactsProvider _contactsProvider = new ContactsProvider();
	private DbProvider dbProvider;
	
	
	public ResultHandler(){	
	
			
		dbProvider = App.Instance().dbProvider;		
	}
	
	public void ByOnce(MessageHandler messageHandler)
	{
		
//		ResultHandlerByOnce resultHandlerByOnce = new ResultHandlerByOnce(messageHandler);
		
	}


	public void ContactIgnore()
	{
		_defaultAction=ActionType.Ignore;
	}
}
