package com.luciuses.contactcleaner;

import android.app.*;
import android.content.Context;
import android.view.*;

import android.widget.*;

	public class App
	{		
		private static App instance = new App ();	
		public ViewGroup RelativeLayout ;
		public Popup Popup ;
		public Context AppContext;
		public MessageHandler messegeHandler;
		public ContactsHandler contactsHandler;
		public DbProvider dbProvider;
		
		App() 
		{}

		public static App Instance()
		{			
				return instance;
		}

		public void Init (Activity activity, Context context)
		{
			RelativeLayout = (ViewGroup)activity.findViewById(R.id.ltrel);
			AppContext = context;
			Popup= new Popup(activity);
			dbProvider = new DbProvider(AppContext);
			TextView logView = (TextView)this.RelativeLayout.findViewById(R.id.tv);
			messegeHandler = new MessageHandler(logView);
			contactsHandler = new ContactsHandler();
			
			
			
			
			Button _buttonStart = (Button)activity.findViewById(R.id.btn);						
			View.OnClickListener buttonStartListener = new View.OnClickListener(){				
				@Override
				public void onClick(View v) {					
					contactsHandler.start();
				}
			};
			_buttonStart.setOnClickListener(buttonStartListener);
		}		
	}

