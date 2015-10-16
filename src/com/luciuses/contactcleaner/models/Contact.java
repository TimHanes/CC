package com.luciuses.contactcleaner.models;

	public class Contact
	{
		private String Id ;
		private String Name ;
		private String[] Phone ;

		public Contact(String id, String name, String[] phone) {
		
		Id = id;
		Name = name;
		Phone = phone;			
		}	
		
		public String getId() {
	        return Id;
	    }
	 
	    public void setId(String id) {
	        Id = id;
	    }
	 
	    public String getName() {
	        return Name;
	    }
	 
	    public void setName(String name) {
	        Name = name;
	    }
	 
	    public String[] getPhones() {
	        return Phone;
	    }
	    public void setPhone(String[] phone) {
	        Phone = phone;
	    }		
	}

