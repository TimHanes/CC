package com.luciuses.contactcleaner.models;

import android.database.*;
import android.provider.*;

	public class Contact
	{
		private int Id ;
		private String Name ;
		private String Phone ;
		
		public Contact (Cursor cur)
		{
			Id = cur.getInt (cur.getColumnIndex (ContactsContract.Contacts._ID));
			Name = cur.getString (cur.getColumnIndex (ContactsContract.Contacts.DISPLAY_NAME));
			Phone = cur.getString (cur.getColumnIndex (ContactsContract.Contacts.HAS_PHONE_NUMBER));
		}

		public Contact(int id, String name, String phone) {
		
		Id = id;
		Name = name;
		Phone = phone;			
		}	
		
		public Integer getId() {
	        return Id;
	    }
	 
	    public void setId(Integer id) {
	        Id = id;
	    }
	 
	    public String getName() {
	        return Name;
	    }
	 
	    public void setName(String name) {
	        Name = name;
	    }
	 
	    public String getPhone() {
	        return Phone;
	    }
	    public void setPhone(String phone) {
	        Phone = phone;
	    }		
	}

