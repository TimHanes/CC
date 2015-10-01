package com.luciuses.contactcleaner.models;

	public class ShowList
	{
		private String header;
		private String[] body ;
		
		public ShowList(String header, String[] body)
		{
			this.header = header;
			this.body = body;
		}

		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}

		public String[] getBody() {
			return body;
		}

		public void setBody(String[] body) {
			this.body = body;
		}		
		
	}

