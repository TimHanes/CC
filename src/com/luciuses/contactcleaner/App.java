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
			Button buttonReScan = (Button)activity.findViewById(R.id.btnStart);			
			Button buttonContinue = (Button)activity.findViewById(R.id.btnContinue);
			if(dbProvider.getContactsUri().length > 0){
				buttonReScan.setText("RESCAN");
				buttonContinue.setVisibility(View.VISIBLE);
			}
			final ExecutorThread executor = new ExecutorThread();
			executor.Pause();
			executor.FirstResultAction();
			executor.start();
			
			OnClickListener buttonContinueListener = new OnClickListener(){				
				@Override
				public void onClick(View v) {
					executor.FirstResultAction();
					executor.Resume();				
				}
			};
			OnClickListener buttonReScanListener = new OnClickListener(){				
				@Override
				public void onClick(View v) {
					dbProvider.Clean();
					executor.SearchAction();
					executor.Resume();				
				}
			};
			buttonReScan.setOnClickListener(buttonReScanListener);
			buttonContinue.setOnClickListener(buttonContinueListener);
		}											
	}

