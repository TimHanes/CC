package com.luciuses.contactcleaner;

import android.net.Uri;

	public class CursorModel
	{
		private Uri uri;
		private String[] projection;
		private String where;
		private String[] selectionArgs;
		private String sortOrder;
		
		public CursorModel (Uri uri, String[] projection, String where, String[] selectionArgs, String sortOrder ){
			this.setUri(uri);
			this.setProjection(projection);
			this.setWhere(where);
			this.setSelectionArgs(selectionArgs);
			this.setSortOrder(sortOrder);
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}

		public String[] getProjection() {
			return projection;
		}

		public void setProjection(String[] projection) {
			this.projection = projection;
		}

		public String getWhere() {
			return where;
		}

		public void setWhere(String where) {
			this.where = where;
		}

		public String[] getSelectionArgs() {
			return selectionArgs;
		}

		public void setSelectionArgs(String[] selectionArgs) {
			this.selectionArgs = selectionArgs;
		}

		public String getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}	
	}

