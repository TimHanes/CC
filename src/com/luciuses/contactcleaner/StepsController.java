package com.luciuses.contactcleaner;

import android.os.*;

import java.util.ArrayList;

import android.content.*;
import android.database.*;
import android.provider.*;
import android.util.*;
import android.net.*;

public class StepsController extends BaseThread
{
	MessageHandler messageHandler;
	ResultHandlerByOnce resultHandler;

	public StepsController(){
		
		messageHandler = new MessageHandler(this);
		resultHandler = new ResultHandlerByOnce(this);
		
	}
	
	public MessageHandler getMessageHandler(){
		return messageHandler;
	}
	
	public ResultHandlerByOnce getResultHandlerByOnce(){
		return resultHandler;
	}
	
	@Override
	public void run()
	{
		ContactsHandler contactsHandler =  new ContactsHandler(messageHandler);
		
		contactsHandler.start();
		try {
			contactsHandler.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		resultHandler.start();
		
		
	}	
}
	
	