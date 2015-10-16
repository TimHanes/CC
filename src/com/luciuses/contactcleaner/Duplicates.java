package com.luciuses.contactcleaner;

	public class Duplicates
	{
		private SourseType type;
		private String sourse;
		private String[] idDuplicates;
		
		public Duplicates (SourseType type, String source, String[] dubls)
		{
			this.setType(type);
			this.sourse = source;
			this.idDuplicates = dubls;
		}

		public String getSourse() {
			return sourse;
		}

		public void setSourse(String duplBy) {
			this.sourse = duplBy;
		}

		public String[] getIdDuplicates() {
			return idDuplicates;
		}

		public void setIdDuplicates(String[] idDuplicates) {
			this.idDuplicates = idDuplicates;
		}

		public SourseType getType() {
			return type;
		}

		public void setType(SourseType type) {
			this.type = type;
		}			
	}