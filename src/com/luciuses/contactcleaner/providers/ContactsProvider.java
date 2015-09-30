package com.luciuses.contactcleaner.providers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.Dublicates;
import com.luciuses.contactcleaner.models.DublicatesContact;

import android.content.*;
import android.database.*;
import android.provider.*;
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
		String name = getNameByCursor(cursor);
		Uri uri = getUriByCursor(cursor);
		String phones = getPhonesByUri(uri);
		int id = getIdByCursor(cursor);
		Contact contact = new Contact(id, name, phones);
		cursor.close();
		return contact;
	}

	public void ContactDelete(int id) {
		Uri uri = getUriByContactId(id);
		String name = getNameByUri(uri);
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(), "deleted:" + name + "\r\n").sendToTarget();//
		context.getContentResolver().delete(uri, null, null);		
	}
	
	public void ContactDelete(Uri uri) {		
		String name = getNameByUri(uri);
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(), "deleted:" + name + "\r\n").sendToTarget();//
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
		String phones = "";
		Integer id = getIdByCursor(cursor);		
		if (IsHasNamber(cursor)&(id!=null)) {
			Cursor pCur = getCursorByPhoneId(id);
			pCur.moveToFirst();
			while (pCur.moveToNext()) {
				phones += getPhoneByPhoneCursor(pCur) + "\r\n";
			}
			pCur.close();
		}
		cursor.close();
		return phones;
	}

	public Uri[] getContactsUriByName(String selname, boolean ignorecase) {
		Cursor cursor = getCursorByName(selname, ignorecase);
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
		Contact contact = new Contact(getIdByUri(uri), getNameByUri(uri), getPhonesByUri(uri));
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

	private String getPhoneByPhoneCursor(Cursor pCur) {
		String phone;
		try {
			phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
		} catch (Exception e) {
			return "Phone is unable";
		}
		return phone;
	}

	private Cursor getCursorByPhoneId(int id) {
		Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
		return pCur;
	}

	private boolean IsHasNamber(Cursor cur) {
		int nam;
		try {
			nam = Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
		} catch (Exception e) {
			nam = 0;
		}
		if ( nam > 0) {
			return false;
		}
		return true;
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
		if (cur.moveToFirst()) {
			int c = 0;
			do {
				Uri uri = getUriByCursor(cur);
				contactsUri[c] = uri;
				c++;
			} while (cur.moveToNext());
		}
		return contactsUri;
	}

	private String getNameByCursor(Cursor cur) {
		
		String name;
		try {
			name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		} catch (Exception e) {
			
			e.printStackTrace();
			return "This Contact is removed";
		}
		return name;
	}

	private Cursor getCursorByPhone(String phone) {
		Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
		Cursor cur = getCursorByUri(contactUri);
		return cur;
	}

	private Cursor getCursorByContactId(int id) {
		Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
				(ContactsContract.Contacts._ID + " LIKE '" + id + "'"), null, null);
		return cur;
	}
}
