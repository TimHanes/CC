package com.luciuses.contactcleaner;

import android.app.*;
import android.content.Context;
import android.view.*;

import android.widget.*;

	public class App
	{		
		private static App instance = new App ();
		public ProgressShower ProgressShower ;
		public ViewGroup RelativeLayout ;
		public Popup Popup ;
		public Context AppContext;

		App() 
		{}

//		private App()
//		{}

		public static App Instance()
		{			
				return instance;
		}

		public void Init (Activity activity, Context context)
		{
			AppContext = context;
			Popup= new Popup(activity);
			ProgressShower=new ProgressShower(activity);
			RelativeLayout = (ViewGroup)activity.findViewById(R.id.ltrel);
			Button _buttonStart = (Button)activity.findViewById(R.id.btn);						
			View.OnClickListener buttonStartListener = new View.OnClickListener(){				
				@Override
				public void onClick(View v) {					
					new ContactsHandler().start();
				}
			};
			_buttonStart.setOnClickListener(buttonStartListener);
		}		
	}

