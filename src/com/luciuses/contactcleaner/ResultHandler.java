package com.luciuses.contactcleaner;


import android.database.Cursor;
import android.net.Uri;
import android.os.Message;

public class ResultHandler
	{
	
	private ActionType _defaultAction = ActionType.None;
	private ContactsProvider _contactsProvider = new ContactsProvider();
	private DbProvider dbProvider;
	private MessageHandler _mhandler;
	
	public ResultHandler(){
		
		_mhandler = App.Instance().messegeHandler;
		dbProvider = App.Instance().dbProvider;
		
	}

	private void ResultHandlerByOnce(DbProvider dbProvider){
		
		String curentDefalt = ((_defaultAction == ActionType.None) ? "" : "(default:" + _defaultAction.toString () + ")");
		
		Uri[] uris = dbProvider.getContactsUri();
		for (int i =0; i< uris.length; i++){				
			DublicatesOfContact dubl = dbProvider.Read(uris[i]);
			Uri contactUri = dubl.getContactUri();
			Contact contact = new Contact (_contactsProvider.IdByUri(contactUri),_contactsProvider.NameByUri(contactUri),_contactsProvider.PhonesByUri(contactUri)); 
			Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "Work with:" + contact.getName() + "-"+ contact.getPhone()+"\r\n").sendToTarget ();
			for (int a =0; a< dubl.getUriDublicatesByName().length; a++){
				Uri uri = dubl.getUriDublicatesByName()[a];
				if(uri != null){						
					Message.obtain (_mhandler, MessageType.AddToLogView.ordinal(), "DablName:" + _contactsProvider.NameByUri(uri) + "-"+ _contactsProvider.PhonesByUri (uri)+ "\r\n").sendToTarget ();
					if (!App.Instance().Popup.chbsave.isChecked()) {
						Message.obtain (_mhandler, MessageType.ShowPopupForChooseAction.ordinal(), contact.getId(), _contactsProvider.IdByUri(uri), "Work with:" + contact.getName() + "\r\n" + contact.getPhone() + "find:" + _contactsProvider.NameByUri(uri) + "\r\n" + _contactsProvider.PhonesByUri (uri)).sendToTarget ();//																			
					} else {

						switch (_defaultAction) {
						case None:
							break;
						case Delete:
							_contactsProvider.ContactDelete(_contactsProvider.IdByUri(uri));
							break;
						case Join:
							_contactsProvider.ContactJoin(_contactsProvider.IdByUri(uri));
							break;
						case Ignore:
							break;
						}

					}
				} 						
			}
			
		}						
	}
	
	public void ContactIgnore()
	{
		_defaultAction=ActionType.Ignore;
	}
}
