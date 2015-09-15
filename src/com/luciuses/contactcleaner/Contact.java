package com.luciuses.contactcleaner;

import android.database.*;
import android.provider.*;

	public class Contact
	{
		public Contact (Cursor cur)
		{
			Id = cur.getInt (cur.getColumnIndex (ContactsContract.Contacts._ID));
			Name = cur.getString (cur.getColumnIndex (ContactsContract.Contacts.DISPLAY_NAME));
		}

		public int Id ;
		public String Name ;
	}

