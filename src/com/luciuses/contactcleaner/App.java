package com.luciuses.contactcleaner;

import com.luciuses.contactcleaner.providers.DbProvider;
import com.luciuses.contactcleaner.screens.Popup;
import com.luciuses.contactcleaner.treads.ExecutorThread;

import android.app.*;
import android.content.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

	public class App
	{		
		private static App instance = new App ();			
		private Popup popup ;
		private Context context;			
		private DbProvider dbProvider;
		private Activity activity;
	
		private App() 
		{}

		public static App Instance()
		{			
				return instance;
		}
		
		public Popup getPopup() {
			return popup;
		}
		
		public Context getContext() {
			return context;
		}

		public DbProvider getDbProvider() {
			return dbProvider;
		}

		public Activity getActivity() {
			return activity;
		}	
		
		public void Init (Activity activity, Context context)
		{
			this.activity = activity;			
			this.context = context;
			popup = new Popup(activity);
			dbProvider = new DbProvider(context);						
			Button buttonStart = (Button)activity.findViewById(R.id.btn);
			final ExecutorThread executor = new ExecutorThread();
			executor.start();
			executor.Pause();
			OnClickListener buttonStartListener = new OnClickListener(){				
				@Override
				public void onClick(View v) {										
					executor.Resume();				
				}
			};
			buttonStart.setOnClickListener(buttonStartListener);
		}											
	}

