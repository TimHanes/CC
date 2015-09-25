package com.luciuses.contactcleaner.models;

import android.net.Uri;

	public class DublicatesOfContact
	{
		private Uri contactUri;
		private Uri[] uriDublicatesByName ;
		private Uri[] uriDublicatesByPhone ;
		
		public DublicatesOfContact (Uri contactUri, Uri[] uriDublsByName, Uri[] uriDublsByPhone)
		{
			setUriDublicatesByPhone(uriDublsByPhone);
			setUriDublicatesByName(uriDublsByName);
			setContactUri(contactUri);
		}
						
	 
	    public Uri getContactUri() {
	        return contactUri;
	    }
	 
	    public void setContactUri(Uri contactUri) {
	    	this.contactUri = contactUri;
	    }

		public Uri[] getUriDublicatesByName() {
			return uriDublicatesByName;
		}

		public void setUriDublicatesByName(Uri[] uriDublicatesByName) {
			this.uriDublicatesByName = uriDublicatesByName;
		}

		public Uri[] getUriDublicatesByPhone() {
			return uriDublicatesByPhone;
		}

		public void setUriDublicatesByPhone(Uri[] uriDublicatesByPhone) {
			this.uriDublicatesByPhone = uriDublicatesByPhone;
		}				
	}