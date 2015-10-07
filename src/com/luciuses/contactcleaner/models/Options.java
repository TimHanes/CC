package com.luciuses.contactcleaner.models;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;

import android.widget.*;


	public class Options
	{
		public Options()
		{
			CheckBox Byname=(CheckBox)App.Instance().getActivity().findViewById(R.id.byname);
			CheckBox Byphone=(CheckBox)App.Instance().getActivity().findViewById(R.id.byphone);
//			CheckBox Bdvanced=(CheckBox)App.Instance().getActivity().findViewById(R.id.advanced);
			ByName = Byname.isChecked();
			ByPhone = Byphone.isChecked();
//			Advanced = Bdvanced.isChecked();
		}		
		public boolean ByName ;
		public boolean ByPhone ;
		public boolean Advanced ;
	}

