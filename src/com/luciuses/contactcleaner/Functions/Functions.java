package com.luciuses.contactcleaner.Functions;

import com.luciuses.contactcleaner.models.Contact;

import android.net.Uri;
import android.text.TextUtils;
 
 public class Functions {

	public String ContactToString(Contact contact){
		String phonesStr = "";
		if(contact.getPhones() != null)
			phonesStr = TextUtils.join("\r\n", contact.getPhones());		
		return "ID:" + contact.getId() + "  " + contact.getRegion().name() + "\r\n" + "Name:" +contact.getName() + "\r\n" + "Phones:" + phonesStr;		
	}
	
	
}