package com.luciuses.contactcleaner;

import android.os.*;
import android.view.View;
import android.view.View.*;
import android.widget.*;

	public class ThreadManager
	{
		
		private ContactsHandler _contactsHandler;		

		public ThreadManager(ContactsHandler contactsHandler)
		{
			_contactsHandler = contactsHandler;			
		}

		private void ShowPopupForChooseAction( Message msg)
		{								
		_contactsHandler.Resume();
		}
			
		
				
	}

