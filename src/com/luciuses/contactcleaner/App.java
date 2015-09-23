package com.luciuses.contactcleaner;

import android.app.*;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.*;

import android.widget.*;

	public class App
	{		
		private static App instance = new App ();			
		public Popup Popup ;
		public Context AppContext;			
		public DbProvider dbProvider;
		public Activity activity;		
		
		
		
		
		App() 
		{}

		public static App Instance()
		{			
				return instance;
		}

		public void Init (Activity activity, Context context)
		{
			this.activity = activity;			
			AppContext = context;
			Popup= new Popup(activity);
			dbProvider = new DbProvider(AppContext);						
			Button _buttonStart = (Button)activity.findViewById(R.id.btn);						
			View.OnClickListener buttonStartListener = new View.OnClickListener(){				
				@Override
				public void onClick(View v) {					
					new StepsController().start();					
				}
			};
			_buttonStart.setOnClickListener(buttonStartListener);						
		}		
	}

