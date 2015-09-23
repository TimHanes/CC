package com.luciuses.contactcleaner;


import android.database.Cursor;
import android.net.Uri;
import android.os.Message;

public class ResultShower
	{
	private ContactsMessageHandler _mhandler;
	private ContactsProvider _contactsProvider = new ContactsProvider();
	private DbProvider dbProvider;
	
	public ResultShower(DbProvider dbProvider){
		this.dbProvider = dbProvider;
	}
	
	private void ShowListView() {
		
		Uri[] uris = dbProvider.getContactsUri();
		for (int i =0; i< uris.length; i++){				
			DublicatesOfContact dubl = dbProvider.Read(uris[i]);
			Uri contactUri = dubl.getContactUri();
			Contact contact = new Contact (_contactsProvider.IdByUri(contactUri),_contactsProvider.NameByUri(contactUri),_contactsProvider.PhonesByUri(contactUri)); 
			for (int a =0; a< dubl.getUriDublicatesByName().length; a++){
				Uri uri = dubl.getUriDublicatesByName()[a];				 						
			}			
		}
	}
	
									
}
