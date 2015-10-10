package com.luciuses.contactcleaner.models;

import android.net.Uri;

	public class Dublicates
	{
		private Uri contactUri;
		private int options;
		private Uri[] uriDublicatesByName ;
		private Uri[] uriDublicatesByPhone ;
		
		public Dublicates (Uri contactUri, int options, Uri[] uriDublsByName, Uri[] uriDublsByPhone)
		{
			setOptions(options);
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


		public int getOptions() {
			return options;
		}


		public void setOptions(int options) {
			this.options = options;
		}				
	}