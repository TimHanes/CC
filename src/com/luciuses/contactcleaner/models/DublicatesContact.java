package com.luciuses.contactcleaner.models;

public class DublicatesContact
	{
		private Contact contact;
		private Contact[] dublicatesByName ;
		private Contact[] dublicatesByPhone ;
		
		public DublicatesContact (Contact contact, Contact[] dublsByName, Contact[] dublsByPhone)
		{
			setDublicatesByPhone(dublsByPhone);
			setDublicatesByName(dublsByName);
			setContact(contact);
		}
	 
	    public Contact getContact() {
	        return contact;
	    }
	 
	    public void setContact(Contact contact) {
	    	this.contact = contact;
	    }

		public Contact[] getDublicatesByName() {
			return dublicatesByName;
		}

		public void setDublicatesByName(Contact[] dublsByName) {
			this.dublicatesByName = dublsByName;
		}

		public Contact[] getDublicatesByPhone() {
			return dublicatesByPhone;
		}

		public void setDublicatesByPhone(Contact[] DublsByPhone) {
			this.dublicatesByPhone = DublsByPhone;
		}				
	}