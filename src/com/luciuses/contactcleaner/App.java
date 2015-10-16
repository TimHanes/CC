package com.luciuses.contactcleaner;

import com.luciuses.contactcleaner.components.StepType;
import com.luciuses.contactcleaner.screens.Popup;

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
		private ProviderDuplicatesDb dbProvider;
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

		public ProviderDuplicatesDb getDbProvider() {
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
			dbProvider = new ProviderDuplicatesDb(context);						
			Button buttonReScan = (Button)activity.findViewById(R.id.btnStart);			
			Button buttonContinue = (Button)activity.findViewById(R.id.btnContinue);
			if(dbProvider.getCountAllSourses() > 0){
				buttonReScan.setText("RESCAN");
				buttonContinue.setVisibility(View.VISIBLE);
			}
			final Executor executor = new Executor(dbProvider);
			executor.setStep(StepType.Start);
			executor.Pause();			
			executor.start();
			
			OnClickListener buttonContinueListener = new OnClickListener(){				
				@Override
				public void onClick(View v) {
					executor.setStep(StepType.ShowList);
					executor.Resume();				
				}
			};
			OnClickListener buttonReScanListener = new OnClickListener(){				
				@Override
				public void onClick(View v) {
					dbProvider.Clean();
					executor.CleanList();
					Button buttonReScan = (Button)getActivity().findViewById(R.id.btnStart);			
					Button buttonContinue = (Button)getActivity().findViewById(R.id.btnContinue);
					buttonReScan.setText("SCAN");
					buttonContinue.setVisibility(View.GONE);
					executor.setStep(StepType.SearchesDublicates );
					executor.Resume();				
				}
			};
			buttonReScan.setOnClickListener(buttonReScanListener);
			buttonContinue.setOnClickListener(buttonContinueListener);
		}											
	}

