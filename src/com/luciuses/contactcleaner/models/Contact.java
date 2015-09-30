package com.luciuses.contactcleaner.models;

	public class Contact
	{
		private Integer Id ;
		private String Name ;
		private String Phone ;

		public Contact(Integer id, String name, String phone) {
		
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
	 
	    public String getPhones() {
	        return Phone;
	    }
	    public void setPhone(String phone) {
	        Phone = phone;
	    }		
	}

