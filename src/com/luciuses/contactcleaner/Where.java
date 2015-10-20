package com.luciuses.contactcleaner;

import java.util.ArrayList;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;

import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.*;


	public class Where
	{
		private boolean phone ;
		private boolean Sim1 ;
		private boolean Sim2 ;
		
		public Where()
		{
			CheckBox phone=(CheckBox)App.Instance().getActivity().findViewById(R.id.phone);
			CheckBox sim1=(CheckBox)App.Instance().getActivity().findViewById(R.id.sim1);
			CheckBox sim2=(CheckBox)App.Instance().getActivity().findViewById(R.id.sim2);
			setPhone(phone.isChecked());
			setSim1(sim1.isChecked());
			setSim2(sim2.isChecked());
		}	
		
		public String getWhere(){
			ArrayList<String> parametrs = new ArrayList<String>();
			if(phone) parametrs.add("-1");
			if(Sim1) parametrs.add("1");
			if(Sim2) parametrs.add("2");
			if(parametrs.isEmpty())
				return null;
			String[] selectionArgs = new String[parametrs.size()];
			parametrs.toArray(selectionArgs);
			
			String selection = "";
			for(int i = 0; i < selectionArgs.length; i++)
			    selection += "indicate_phone_or_sim_contact = '" + selectionArgs[i] + "' OR ";
			selection = selection.substring(0, selection.length() - 4);
			return 	selection;	
		}
		
		public boolean isPhone() {
			return phone;
		}
		public void setPhone(boolean Phone) {
			this.phone = Phone;
		}
		public boolean isSim1() {
			return Sim1;
		}
		public void setSim1(boolean Sim1) {
			this.Sim1 = Sim1;
		}
		public boolean isSim2() {
			return Sim2;
		}
		public void setSim2(boolean Sim2) {
			this.Sim2 = Sim2;
		}
		
		public boolean isChoosed(){
			return phone|Sim1|Sim2;
		}
	}

