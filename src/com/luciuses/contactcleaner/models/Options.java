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
		
		public boolean isChoosed(){
			return ByName|ByPhone;
		}
		
	}

