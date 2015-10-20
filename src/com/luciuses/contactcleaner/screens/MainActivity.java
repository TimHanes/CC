package com.luciuses.contactcleaner.screens;

import android.content.ContentUris;
import java.util.List;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;
import com.luciuses.contactcleaner.*;


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
			
		}	
		
		 @Override
		    public void onDestroy(){
			 android.os.Process.killProcess(android.os.Process.myPid());	       
		        super.onDestroy();
		    }
		
	}



