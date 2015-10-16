package com.luciuses.contactcleaner;

import com.luciuses.contactcleaner.models.Contact;

public class DuplicatesContact
	{
		private String sourse;
		private Contact[] duplicates;
		
		public DuplicatesContact (String sourse, Contact[] duplicates)
		{
			setDuplicates(duplicates);
			setSourse(sourse);
		}
	 
	    public String getSourse() {
	        return sourse;
	    }
	 
	    public void setSourse(String sourse) {
	    	this.sourse = sourse;
	    }

		public Contact[] getDublicates() {
			return duplicates;
		}

		public void setDuplicates(Contact[] dubls) {
			this.duplicates = dubls;
		}			
	}