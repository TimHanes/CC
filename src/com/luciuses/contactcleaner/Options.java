package com.luciuses.contactcleaner;

import android.widget.*;


	public class Options
	{
		public Options()
		{
			CheckBox Byname=(CheckBox)App.Instance().RelativeLayout.findViewById(R.id.byname);
			CheckBox Byphone=(CheckBox)App.Instance().RelativeLayout.findViewById(R.id.byphone);
			CheckBox Bdvanced=(CheckBox)App.Instance().RelativeLayout.findViewById(R.id.advanced);
			ByName = Byname.isChecked();
			ByPhone = Byphone.isChecked();
			Advanced = Bdvanced.isChecked();
		}		
		public boolean ByName ;
		public boolean ByPhone ;
		public boolean Advanced ;
	}

