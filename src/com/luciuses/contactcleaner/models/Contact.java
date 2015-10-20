package com.luciuses.contactcleaner.models;

	public class Contact
	{
		private RegionType region;
		private String Id ;
		private String Name ;
		private String[] Phone ;

		public Contact(String id, String name, String[] phone, RegionType region) {
			
		this.region = region;
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

		public RegionType getRegion() {
			return region;
		}

		public void setRegion(RegionType region) {
			this.region = region;
		}		
	}

