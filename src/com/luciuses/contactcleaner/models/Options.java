package com.luciuses.contactcleaner.models;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;

import android.widget.*;


	public class Options
	{
		private boolean ByName ;
		private boolean ByPhone ;
		private boolean AutoDelNull ;
		
		public Options()
		{
			CheckBox Byname=(CheckBox)App.Instance().getActivity().findViewById(R.id.byname);
			CheckBox Byphone=(CheckBox)App.Instance().getActivity().findViewById(R.id.byphone);
			CheckBox autoDelNull=(CheckBox)App.Instance().getActivity().findViewById(R.id.autoDelNull);
			setByName(Byname.isChecked());
			setByPhone(Byphone.isChecked());
			setAutoDelNull(autoDelNull.isChecked());
		}	
		
		public Options(int options)
		{
			if(options == 1 | options == 11| options == 101 | options == 111 ) ByName = true;
			if(options == 10 | options == 11| options == 110 | options == 111 ) ByPhone = true;
			if(options == 100 | options == 101| options == 110 | options == 111 ) AutoDelNull = true;
		}
		
		public boolean isByName() {
			return ByName;
		}
		public void setByName(boolean byName) {
			ByName = byName;
		}
		public boolean isByPhone() {
			return ByPhone;
		}
		public void setByPhone(boolean byPhone) {
			ByPhone = byPhone;
		}
		public boolean isAutoDelNull() {
			return AutoDelNull;
		}
		public void setAutoDelNull(boolean autoDelNull) {
			AutoDelNull = autoDelNull;
		}
		
		public int getOptions(){
			
			int result = 0;
			if(ByName) result++;
			if(ByPhone) result += 10;
			if(AutoDelNull) result += 100;
			return result;
		}				
	}

