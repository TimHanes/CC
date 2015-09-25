package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.basis.*;
import com.luciuses.contactcleaner.treads.*;

public class ResultHandler 
	{
	
	BaseThread resultHandlerThread;
	ExecutorThread executor;
	
	public ResultHandler(ExecutorThread executor){		
		this.executor = executor;		
	}
	
	public void ByOnce()
	{				
		resultHandlerThread = new ResultHandlerByOnce(executor.getMessageHandler()).getTread();
		resultHandlerThread.start();
	}
	
	public void ShowList()
	{		
		resultHandlerThread = new ShowAllThread(executor.getMessageHandler());	
		resultHandlerThread.start();
	}
	
	public void ByPosition(int position)
	{			
		resultHandlerThread = new RequestActionThread(position, executor.getMessageHandler());	
		resultHandlerThread.start();	
	}
	
	public BaseThread getTread(){
		return resultHandlerThread;
	}	
}
