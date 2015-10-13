package com.luciuses.contactcleaner.providers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.models.DublicatesContact;

import android.content.*;
import android.database.*;
import android.provider.*;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts.Data;
import android.telephony.PhoneNumberUtils;
import android.net.*;
import android.os.Message;

public class ContactsProvider {
	private Context context = App.Instance().getContext();
	MessageHandler messageHandler;

	public ContactsProvider(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public Contact getContactByPosition(int position) {
		Cursor cursor = getAllContactCursor();
		cursor.moveToPosition(position);		
		Integer id = getIdByCursor(cursor);
		if(id == null) 
			return null;
		String name = getNameByCursor(cursor);
		String phones = getContactPhone(id+"");
		Contact contact = new Contact(id, name, phones);
		cursor.close();
		return contact;
	}

	public boolean ContactDelete(int id) {
		Uri uri = getUriByContactId(id);
		if(uri == null) 
			return false;
		ContactDelete(uri);
		return true;
	}
	
	public void ContactDelete(Uri uri) {		
		Contact contact = getContactByUri(uri);	
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(), "Deleted: " 
		+ new Functions().ContactToString(contact)).sendToTarget();//
		context.getContentResolver().delete(uri, null, null);		
	}

	public int getCount() {
		Cursor cursor = getAllContactCursor();
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public String getPhonesByUri(Uri uri) {
		
		Cursor cursor = getCursorByUri(uri);
		cursor.moveToFirst();		
		Integer id = getIdByCursor(cursor);			
		cursor.close();
		if (id == null)
			return null;
		String phones = getContactPhone(id.toString());
		return phones;
	}

	public Uri[] getContactsUriByName(String selname, boolean ignorecase) {
		Cursor cursor = getCursorByName(selname, ignorecase);
		if(cursor == null)
			return null;
		Uri[] contactsUri = new Uri[cursor.getCount()];
		cursor.moveToFirst(); 
		for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext() ){
			Uri uri = getUriByCursor(cursor);
			contactsUri[i] = uri;
		}
		cursor.close();
		return contactsUri;
	}

	public Contact getContactByUri(Uri uri) {
		Integer id = getIdByUri(uri);
		if(id == null) 
			return null;
		Contact contact = new Contact(id, getNameByUri(uri), getPhonesByUri(uri));
		return contact;
	}

	public Integer  getIdByUri(Uri uri) {
		Cursor cur = getCursorByUri(uri);
		cur.moveToFirst();
		Integer id = getIdByCursor(cur);
		cur.close();
		return id;
	}

	public String getNameByUri(Uri uri) {
		Cursor cur = getCursorByUri(uri);
		cur.moveToFirst();
		String name = getNameByCursor(cur);
		cur.close();
		return name;
	}

	public Uri[] getContactUrisByPhone(String phone) {
		Cursor cur = getCursorByPhone(phone);
		if ((cur != null) & (cur.getCount() > 0)) {
			Uri[] contactsUri = getUrisByCursor(cur);
			cur.close();
			return contactsUri;
		}
		cur.close();
		return null;
	}

	public Uri getUriByContactId(int id) {
		Cursor cur = getCursorByContactId(id);
		if(cur == null) 
			return null;
		cur.moveToFirst();
		Uri uri = getUriByCursor(cur);
		cur.close();
		return uri;
	}

	public void ContactJoin(int id) {
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(),
				"joined:" + getNameByUri(getUriByContactId(id)) + "\r\n").sendToTarget();//
	}

	public String[] NamesByUris(Uri[] uris) {
		String[] strUris = new String[uris.length];
		for (int i = 0; i < uris.length; i++) {
			strUris[i] = getNameByUri(uris[i]);
		}
		return strUris;
	}
	
	public DublicatesContact getDublicatesContact(Dublicates dublicates){	
		if(dublicates==null)
			return null;
		Uri contactUri = dublicates.getContactUri();
		Uri[] urisByPhone = dublicates.getUriDublicatesByPhone();
		Uri[] urisByName = dublicates.getUriDublicatesByName();
		Contact contact = getContactByUri(contactUri);
		Contact[] dublicatesByPhone = getContactsByUris(urisByPhone);
		Contact[] dublicatesByName = getContactsByUris(urisByName);
		DublicatesContact dublicatesContact = new DublicatesContact(contact, dublicatesByName, dublicatesByPhone);		
		return dublicatesContact;		
	}

	public Contact[] getContactsByUris(Uri[] uris) {
		if (uris == null)
			return null;
		Contact[] contacts = new Contact[uris.length];
		for (int i = 0; i < uris.length; i++ ){
			contacts[i] = getContactByUri(uris[i]);			
		}
		return contacts;
	}

	private Integer  getIdByCursor(Cursor cursor) {
		int id;
		try {
			id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		} catch (Exception e) {
			return null;
		}
		return id;
	}

	private Cursor getCursorByUri(Uri uri) {
		Cursor cur = context.getContentResolver().query(uri, null, null, null, null);
		return cur;
	}

	private Cursor getAllContactCursor() {
		Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		return cur;
	}

	private Uri getUriByCursor(Cursor cur) {		
		String columnName = ContactsContract.Contacts.LOOKUP_KEY;
		int columnIndex = cur.getColumnIndex(columnName);
		Uri uri;
		try {
			String lookupKey = cur.getString(columnIndex);
			uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
		} catch (Exception e) {
			return null;
		}
		return uri;
	}

	private Cursor getCursorByName(String selname, boolean ignorecase) {
		selname = selname.replace("'", "''");
		Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
				(!ignorecase) ? (ContactsContract.Contacts.DISPLAY_NAME + " LIKE '" + selname + "'")
						: ("UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") LIKE UPPER('" + selname + "')"),
				null, null);
		return cur;
	}

	private Uri[] getUrisByCursor(Cursor cur) {
		Uri[] contactsUri = new Uri[cur.getCount()];
		cur.moveToFirst();
		for(int i = 0; i < cur.getCount(); i++,cur.moveToNext()){			
				Uri uri = getUriByCursor(cur);
				contactsUri[i] = uri;							
		}
		return contactsUri;
	}

	private String getNameByCursor(Cursor cur) {
		
		String name;
		try {
			name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		} catch (Exception e) {		
			return null;
		}
		return name;
	}

	private Cursor getCursorByPhone(String sourcePhoneNumber) {
		
		String phoneNumber = PhoneNumberUtils.stripSeparators(sourcePhoneNumber);
		String[] projection = new String[] { ContactsContract.Data.CONTACT_ID, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.STARRED, ContactsContract.Contacts.CONTACT_STATUS, ContactsContract.Contacts.CONTACT_PRESENCE };
		String selection = "PHONE_NUMBERS_EQUAL(" + Phone.NUMBER + ",?) AND " + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";
		String[] selectionArgs = new String[] { phoneNumber };
		Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null);				
		return cursor;
	}

	private Cursor getCursorByContactId(int id) {
		
		Cursor cur;
		try {
			cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
					(ContactsContract.Contacts._ID + " LIKE '" + id + "'"), null, null);
		} catch (Exception e) {
			return null;
		}		
		return cur;
	}
	
	private String getContactPhone(String id) {
	    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	    String[] projection = null;
	    String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?";
	    String[] selectionArgs = new String[] { id };
	    String sortOrder = null;
	    Cursor result = managedQuery(uri, projection, where, selectionArgs, sortOrder);
	    if( result == null) return null;
	    String phones = "";
	    while (result.moveToNext()) {
	    	
	    	String phone = result.getString(result.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	        if (phone != null) {  
	        	phones += phone + "\r\n";	            
	        }	        	       
	    }
	    result.close();
	    if( phones == "") return null;
	    return phones;
	}

	private Cursor managedQuery(Uri uri, String[] projection, String where, String[] selectionArgs, String sortOrder) {
		Cursor cur;
		try {
			cur = context.getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);
		} catch (Exception e) {
			return null;
		}
		return cur;
	}	
}
