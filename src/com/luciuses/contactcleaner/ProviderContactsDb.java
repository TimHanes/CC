package com.luciuses.contactcleaner;

import java.util.ArrayList;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.hendlers.MessageHandler;
import com.luciuses.contactcleaner.models.Contact;
import com.luciuses.contactcleaner.models.RegionType;
import android.annotation.SuppressLint;
import android.content.*;
import android.database.*;
import android.provider.*;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts.Data;
import android.telephony.PhoneNumberUtils;
import android.net.*;
import android.os.Message;

public class ProviderContactsDb { 
	private Context context = App.Instance().getContext();
	MessageHandler messageHandler;
	private String where;
	private boolean isAutoDel;
	

	public ProviderContactsDb(MessageHandler messageHandler) {
		
		this.messageHandler = messageHandler;		
	}

	public void setWhere(String string) {
		this.where = string;
	}	

	public int getCountAllContacts() {
		Cursor cursor = getAllContactCursor();
		int count = cursor.getCount();
		cursor.close();
		return count;
	}


	public Contact getContactById(String id) {		
		String[] phones = getContactPhone(id);
		String name = getContactName(id);
		RegionType region = getRegion(id);
		Contact contact = new Contact(id, name, phones, region);
		return contact;
	}
	
	private RegionType getRegion(String id) {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
	    String[] projection = null;
	    String where = ContactsContract.Contacts._ID +" = ?";
	    String[] selectionArgs = new String[] { id };
	    String sortOrder = null;
	    Cursor result = managedQuery(uri, projection, where, selectionArgs, sortOrder);	    
	    if( result == null) return null;
	    result.moveToFirst();	    	
	    	String region = result.getString(result.getColumnIndex("indicate_phone_or_sim_contact"));	     	
	    result.close();
	    if(region.equals("1")) return RegionType.Sim1;
	    if(region.equals("2")) return RegionType.Sim2;
	    return RegionType.Phone;	
	}

	public String getNameByUri(Uri uri) {
		Cursor cur = getCursorByUri(uri);
		cur.moveToFirst();
		String name = getNameByCursor(cur);
		cur.close();
		return name;
	}
	
	private Cursor getCursorByUri(Uri uri) {
		Cursor cur = context.getContentResolver().query(uri, null, null, null, null);
		return cur;
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
	
	public String[] getContactsIdByName(String name) {
		Cursor cursor = getCursorByName(name, true);
		if(cursor.getCount() < 2) 
			return null;
		String[] contactsId = getContactsId(cursor);
		cursor.close();
		return contactsId;
	}


	public String[] getContactsIdByPhone(String pnone) {
		Cursor cursor = getCursorByPhone(pnone);
		if(cursor.getCount() < 2) 
			return null;
		String[] contactsId = getContactsIdByPhoneCursor(cursor);
		cursor.close();
		return contactsId;		
	}


	private String[] getContactsIdByPhoneCursor(Cursor cursor) {
		if(cursor.getCount() < 2) return null;
		ArrayList<String> contactId = new ArrayList<String>();
		cursor.moveToFirst();
		for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()){
			String id = null;
			try {
				id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));				
			} catch (Exception e) {
				
			}
			if(isAutoDel && (getContactPhone(id)==null | getContactName(id) == null)){
				DeleteContact(id);
				continue;
			}			
			if(id!= null && !contactId.contains(id)) contactId.add(id);
		}
		if(contactId.size()<2)return null;
		String[] contactIdArr = new String[contactId.size()];
		contactId.toArray(contactIdArr);
		return contactIdArr;
	}


	public void DeleteContact(String id) {
		Contact contact = getContactById(id);
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(), "Deleted: " 
				+ new Functions().ContactToString(contact)).sendToTarget();
		Uri uri = getUriByContactId(id);
		ContactDelete(uri);	
	}


	public Contact getContactByPosition(int position) {
		Cursor cursor = getAllContactCursor();
		cursor.moveToPosition(position);		
		String id = getIdByCursor(cursor);
		if(id == null) 
			return null;
		String name = getContactName(id);
		String[] phones = getContactPhone(id);
		RegionType region = getRegion(id);
		Contact contact = new Contact(id, name, phones, region);
		cursor.close();
		return contact;
	}
	
	private Cursor getAllContactCursor() {
			
			String where = this.where;
			String[] selectionArgs = null;	
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
	    String[] projection = null;	   
	    String sortOrder = null;
	    Cursor result = managedQuery(uri, projection, where, selectionArgs, sortOrder);	 
		
		return result;
	}
	private Cursor getCursorByContactId(String id) {
		
		Cursor cur;
		try {
			cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
					(ContactsContract.Contacts._ID + " LIKE '" + id + "'"), null, null);
		} catch (Exception e) {
			return null;
		}		
		return cur;
	}
	
	@SuppressLint("DefaultLocale")
	private Cursor getCursorByName(String selname, boolean ignorecase) {
		if(selname == null) return null;
		selname = selname.replace("'", "''").toUpperCase();		
		Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
				"(UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") LIKE '" + selname + "') AND (" + where +")",
						null, null);
		return cur;
	}
	private Cursor getCursorByPhone(String sourcePhoneNumber) {
		
		String phoneNumber = PhoneNumberUtils.stripSeparators(sourcePhoneNumber);
		String[] projection = null;
		String selection = "PHONE_NUMBERS_EQUAL(" + Phone.NUMBER + ", ?) AND (" + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "') AND (" + where +")";
		String[] selectionArgs = new String[]{phoneNumber};
		Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null);				
		return cursor;
	}
	private String  getIdByCursor(Cursor cursor) {
		String id = null;
		try {
			id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		} catch (Exception e) {
			return null;
		}
		return id;
	}
	private String[] getContactsId(Cursor cursor){
		ArrayList<String> contactsId = new ArrayList<String>();	
		cursor.moveToFirst();
		for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()){
			String id = getIdByCursor(cursor);
			if(isAutoDel && (getContactPhone(id)==null | getContactName(id) == null)){
				DeleteContact(id);
				continue;
			}
			if(id!=null)
				contactsId.add(id);
		}
		if(contactsId.size()<2) 
			return null;
		String[] contactsIdArr = new String[contactsId.size()];
		contactsId.toArray(contactsIdArr);
		return contactsIdArr;
	}
	
	public void ContactDelete(Uri uri) {				
		context.getContentResolver().delete(uri, null, null);		
	}
	public Uri getUriByContactId(String id) {
		Cursor cur = getCursorByContactId(id);
		if(cur == null) 
			return null;
		cur.moveToFirst();
		Uri uri = getUriByCursor(cur);
		cur.close();
		return uri;
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
	
	private Cursor managedQuery(Uri uri, String[] projection, String where, String[] selectionArgs, String sortOrder) {
		Cursor cur;
		try {
			cur = context.getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);
		} catch (Exception e) {
			return null;
		}
		return cur;
	}
	
	public String[] getContactPhone(String id) {
		 ArrayList<String> phones = new ArrayList<String>();
		 
		 Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		
	    String[] projection = null;	
	    String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?";
	    String[] selectionArgs = new String[] { id };
	    String sortOrder = null;
	    Cursor result = managedQuery(uri, projection, where, selectionArgs, sortOrder);	    
	    if( result == null) return null;
	    while (result.moveToNext()) {	    	
	    	String phone = result.getString(result.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));	    		    	
	        if (phone != null) {  
	        	phones.add(phone);	  
	        }	        	       
	    }
	    result.close();
	    if( phones.isEmpty()) return null;
	    String[] phonesArr = new String[phones.size()] ;
	    phones.toArray(phonesArr);
	    return phonesArr;
	}	
	
	private String getContactName(String id) {
	    Uri uri = ContactsContract.Contacts.CONTENT_URI;
	    String[] projection = null;
	    String where = ContactsContract.Contacts._ID +" = ?";
	    String[] selectionArgs = new String[] { id };
	    String sortOrder = null;
	    Cursor result = managedQuery(uri, projection, where, selectionArgs, sortOrder);	    
	    if( result == null) return null;
	    result.moveToFirst();	    	
	    	String name = null;
			try {
				name = result.getString(result.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			} catch (Exception e) {
				return null;
			}	     	
	    result.close();	 
	    return name;
	}

	public boolean isAutoDel() {
		return isAutoDel;
	}

	public void setAutoDel(boolean isAutoDel) {
		this.isAutoDel = isAutoDel;
	}
}
