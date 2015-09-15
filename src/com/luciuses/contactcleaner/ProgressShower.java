package com.luciuses.contactcleaner;

import android.app.*;
import android.content.*;

public class ProgressShower extends ProgressDialog
	{
	
	DialogInterface.OnClickListener dlstcancel=new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialoginterface,int i)
		{
//			cwr.onFinished();
//			pd.dismiss();
			dismiss();
		}
	};
	
		public ProgressShower (Activity activity)  
		{	
			super (activity);
			setIndeterminate(true);
			setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			setButton("Cancel",dlstcancel);
		}		
	}

