package com.luciuses.contactcleaner;

import android.app.*;
import android.os.*;


		
public class MainActivity extends Activity
	{
		
		@Override 
		protected void onCreate(Bundle bundle)
		{			
			super.onCreate(bundle);
			setContentView(R.layout.main);
			App.Instance().Init(this,getApplicationContext());
			new AppContext();
		}				
	}



