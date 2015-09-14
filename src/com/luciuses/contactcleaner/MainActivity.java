package com.luciuses.contactcleaner;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.*;

import java.io.IOException;
import java.io.StringReader;
import android.speech.RecognizerIntent;
import android.view.ViewGroup.LayoutParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.AdapterView.OnItemClickListener;//.OnClickListener;
import android.util.Log;

import android.graphics.*;
import java.sql.*;
import android.text.*;
import android.net.*;
import java.net.*;
//import java.text.*
import java.util.*;
import java.io.*;
//import java.nio.*;
//import java.awt.*;
//import java.beans.*;
//import java.math.*
import android.util.*;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;
//import net.sourceforge.jtds.jdbc.*;
import android.opengl.*;
import android.view.inputmethod.*;
import android.content.pm.*;
import android.net.wifi.*;
import java.security.*;
import android.graphics.drawable.*;
import android.text.format.*;
import java.text.*;
import java.util.Date;
import android.provider.ContactsContract.*;
import android.provider.*;
public class MainActivity extends Activity
{
TextView tv;
ProgressDialog pd;
public Handler mhandler = new Handler()
{
	@Override
	
	public void	handleMessage(Message msg)
	{
		
		switch(msg.what)
		{
			case 0:
				tv.append(msg.obj.toString());
			break;
			case 1:
				pd.cancel();
				p.MsgBoxButtons("What do with:",msg.obj.toString()+" ?","Delete","Join","Ignore",lstbtndel,lstbtnjoin,lstbtnign,true);
			break;
			case 2:
				tv.setText(msg.obj.toString());
			break;
			case 3:
				pd.setMax(msg.arg1);
				pd.setProgress(msg.arg2);
				pd.setMessage(msg.obj.toString());
				pd.show();
				//p.MsgBoxProgress("Processing...",msg.obj.toString(),true);
				
			break;
			case 4:
				p.MsgBoxClose();
				pd.dismiss();
			break;
			case 5:
				p.pb.setMax(msg.arg1);
				p.pb.setProgress(msg.arg2);
			break;
		}
	}
};

public	class contactsWorkRunnable implements Runnable {
		private Object mPauseLock;
		private boolean mPaused;
		private boolean mFinished;
		public Handler handler;
		ProgressDialog pd;
//		private Activity activity;
		Context ctx;
		public Uri[] findUri;
		public int i=0;
		public contactsWorkRunnable() {
			mPauseLock = new Object();
			mPaused = false;
			mFinished = false;
		//	pd=new ProgressDialog(ctx);
		}
	//	Uri[] findUri;
	//	int i=0;
		int lastSel=0;
		String currname="";
		//ContentResolver cr = getContentResolver();
		public void run() 
		{
//			TextView tv=(TextView)activity.findViewById(R.id.tv);
		CheckBox byname=(CheckBox)findViewById(R.id.byname);
		CheckBox byphone=(CheckBox)findViewById(R.id.byphone);
		CheckBox advanced=(CheckBox)findViewById(R.id.advanced);
		//	pd.show();
			Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			if (cur.getCount() > 0) 
			{
//				cur.moveToFirst();
				while (cur.moveToNext()&&!mFinished) 
				{
					// Do stuff.
					int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
					currname = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				//	Message.obtain(mhandler,2,"work with:"+id+"-"+name+"\r\n").sendToTarget();
					Message.obtain(mhandler,3,cur.getCount(),cur.getPosition(),currname+"\r\n"+((lastSel==0)||(!p.chbsave.isChecked())?"":"(default:"+(lastSel==1?"Delete":(lastSel==2?"Join":(lastSel==3?"Ignore":"Unknow")))+")")).sendToTarget();
				//	tv.append("work with:"+name+"\r\n");
				if((byname.isChecked())&(byphone.isChecked()))
				{
					findUri=getContactsUriByName(currname,false);
					checkContacts(id,cur);	
					String[] phones=phonesByCursor(cur).split("\r\n");
					for(int p=0;p<phones.length;p++)
					{
						findUri=getContactUrisByPhone(phones[p]);
						checkContacts(id,cur);	
					}
				}else
				if(byname.isChecked())
				{
					findUri=getContactsUriByName(currname,false);
					checkContacts(id,cur);		
				}else
				if(byphone.isChecked())
				{
				//	strin
					try{
					String[] phones=phonesByCursor(cur).split("\r\n");
					for(int p=0;p<phones.length;p++)
					{
						findUri=getContactUrisByPhone(phones[p]);
						if(findUri!=null)
						checkContacts(id,cur);	
					}
					}
					catch(Exception err)
					{
						Log.e("byphones",err.getMessage());
					}
				}
			
				
				}
				Message.obtain(mhandler,4).sendToTarget();
			}
			
		}
		void checkContacts(int id,Cursor cur)
		{
			
			for(i=0;i<=findUri.length-1;i++)
			{
				int fid=idByUri(findUri[i]);
				if(fid>id)
				{
					String fname=nameByUri(findUri[i]);
					//	Message.obtain(mhandler,0,"find:"+fid+"-"+fname+"\r\n").sendToTarget();//append to tv
					if(!p.chbsave.isChecked())
					{
						Message.obtain(mhandler,1,"Work with:"+currname+"\r\n"+phonesByCursor(cur)+"find:"+ fname+"\r\n"+phonesByUri(findUri[i])).sendToTarget();//
						mPaused=true;
					}
					else
					{
						switch(lastSel)
						{
							case 0:
								break;
							case 1:
								contactDelete(findUri[i]);
								break;
							case 2:
								contactJoin(findUri[i]);
								break;
							case 3:
								break;
						}
					}
				}

				synchronized(mPauseLock) 
				{
					while(mPaused) 
					{
						try 
						{
							mPauseLock.wait();
						} catch(InterruptedException e) 
						{}
					}
				}
			}		
		}
		public String phonesByUri(Uri uri)
		{
			Cursor cur=getContentResolver().query(uri, null, null, null, null);
			cur.moveToFirst();
			String phones="";
			int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
			if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)					 
					{
						Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+id,null, null);
						while (pCur.moveToNext()) 
						{
							phones+=pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))+"\r\n";
						}
						pCur.close();
					}
			return phones;
		}
		public String phonesByCursor(Cursor cur)
		{
			String phones="";
			int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
			if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)					 
			{
				Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+id,null, null);
				while (pCur.moveToNext()) 
				{
					phones+=pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))+"\r\n";
				}
				pCur.close();
			}
			return phones;
		}
		public void contactDelete(Uri uri)
		{
			lastSel=1;
			Message.obtain(mhandler,0,"deleted:"+ nameByUri(uri)+"\r\n").sendToTarget();//
			getContentResolver().delete(uri,null,null);
		}
		public void contactJoin(Uri uri)
		{
			lastSel=2;
				Message.obtain(mhandler,0,"joined:"+ nameByUri(uri)+"\r\n").sendToTarget();//
		//	cr.delete(findUri[i],null,null);
		}
		public void contactIgnore()
		{
			lastSel=3;
			//cr.delete(findUri[i],null,null);
		}
		/**
		 * Call this on pause.
		 */
		public void onPause() {
			synchronized(mPauseLock) {
				mPaused = true;
			}
		}
		/**
		 * Call this on resume.
		 */
		public void onResume() {
			synchronized(mPauseLock) {
				mPaused = false;
				mPauseLock.notifyAll();
			}
		}
		
		public void onFinished() {
			mFinished=true;
		}
	}
	
	public popup p;
	contactsWorkRunnable cwr;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		tv=(TextView)findViewById(R.id.tv);
		p=new popup(this);
		pd=new ProgressDialog(this);
	//	pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setButton("Cancel",dlstcancel);
    }

	DialogInterface.OnClickListener dlstcancel=new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialoginterface,int i)
		{
			cwr.onFinished();
			pd.dismiss();
		}
	};
	View.OnClickListener lstbtndel =new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			try{
				p.MsgBoxClose();
				cwr.contactDelete(cwr.findUri[cwr.i]);
				cwr.onResume();
			}
			catch(Exception err)
			{
				Log.e("xxx",err.getMessage());
			}
		}
	};
	View.OnClickListener lstbtnign =new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			try{
				p.MsgBoxClose();
				cwr.contactIgnore();
				cwr.onResume();
			}
			catch(Exception err)
			{
				Log.e("xxx",err.getMessage());
			}
		}
	};
	View.OnClickListener lstbtnjoin =new View.OnClickListener()
	{
     @Override
	public void onClick(View v)
	{
		try{
		p.MsgBoxClose();
			cwr.contactJoin(cwr.findUri[cwr.i]);
		cwr.onResume();
		}
		catch(Exception err)
		{
			Log.e("xxx",err.getMessage());
		}
	}
	};
	
	public void bstart(View v)
	{
		//readcontacts();
		p.chbsave.setChecked(false);
		tv.setText("");
		cwr=new contactsWorkRunnable();
		try{
		Thread ct=new Thread(cwr);
		ct.setDaemon(true);
		ct.start();
		}
		catch(Exception err)
		{
			Log.e("xxx",err.getMessage());
		}
	}
	
	public String nameByUri(Uri uri)
	{
		Cursor crs=getContentResolver().query(uri,null,null,null,null);
		crs.moveToFirst();
		return crs.getString(crs.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	}
	public int idByUri(Uri uri)
	{
		Cursor crs=getContentResolver().query(uri,null,null,null,null);
		crs.moveToFirst();
		return crs.getInt(crs.getColumnIndex(ContactsContract.Contacts._ID));
	}
	public Uri[] getContactUrisByPhone(String phone)
	{
	//	TextView tv=(TextView)findViewById(R.id.tv);
		Uri contactUri =Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
		Cursor cur =getContentResolver().query(contactUri, null, null, null,null);
		if((cur!=null)&(cur.getCount()>0))
		{
		Uri[] contactsUri=new Uri[cur.getCount()];
	
		try {
			if (cur.moveToFirst())
			{
				int c=0;
				do {
						String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
						Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
						contactsUri[c]=uri;
						c++;
				//		tv.append("\r\n"+cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				} while(cur.moveToNext());
			}
		} catch (Exception e) {
			Log.e("xxx",e.getMessage());
		}
		cur.close();
		return contactsUri;
		}
		else
		{
			cur.close();
			return null;
		}
		
		
	}
	public Uri[] getContactsUriByName(String selname,boolean ignorecase)
	{
		//Uri contactUri =Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(name));
		//TextView tv=(TextView)findViewById(R.id.tv);
		Uri[] contactsUri=null;
		try {
			Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, (!ignorecase)?(ContactsContract.Contacts.DISPLAY_NAME+" LIKE '"+selname+"'"):("UPPER("+ContactsContract.Contacts.DISPLAY_NAME+") LIKE UPPER('"+selname+"')"),null,null);
			contactsUri=new Uri[cur.getCount()];
			
			if (cur.moveToFirst())
			{
				int c=0;
				do {
					String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
					Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
					contactsUri[c]=uri;
					c++;
			//		tv.append("\r\n"+cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				} while(cur.moveToNext());
			}
			cur.close();
		} catch (Exception e) {
			Log.e("xxx",e.getMessage());
		}
		return contactsUri;
	}
}
//	public static boolean deleteContact(Context ctx,String phone, String name) {
//		Uri contactUri =Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
//		Cursor cur =ctx.getContentResolver().query(contactUri, null, null, null,null);
//		try {
//			if (cur.moveToFirst()
//			{
//				do {
//					if(cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
//						String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//						Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//						ctx.getContentResolver().delete(uri, null, null);
//						Log.i("xxx-deleted",name);
//						return true;
//					}
//				//	Log.i("xxx-name",cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)));
//				} while(cur.moveToNext());
//			}
//		} catch (Exception e) {
//			//System.out.println(e.getStackTrace());
//			Log.e("xxx",e.getMessage());
//		}
//		return false;
//	}
//	public static boolean deleteContact(Context ctx,String phone) {
//		Uri contactUri =Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
//		Cursor cur =ctx.getContentResolver().query(contactUri, null, null, null,null);
//		try {
//			if (cur.moveToFirst())
//			{
//				do {
//					if(true)//cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) 
//					{
//						String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//						Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//						ctx.getContentResolver().delete(uri, null, null);
//						Log.i("xxx-deleted",phone);
//						return true;
//					}
//					//	Log.i("xxx-name",cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)));
//				} while(cur.moveToNext());
//			}
//		} catch (Exception e) {
//			//System.out.println(e.getStackTrace());
//			Log.e("xxx",e.getMessage());
//		}
//		return false;
//	}
//	public static boolean deleteContactByName(Context ctx,String name) {
//		Uri contactUri =Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(name));
//		Cursor cur =ctx.getContentResolver().query(contactUri, null, null, null,null);
//		try {
//			if (cur.moveToFirst())
//			{
//				do {
//					if(cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
//						String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//						Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//						ctx.getContentResolver().delete(uri, null, null);
//						Log.i("xxx-deleted",name);
//						return true;
//					}
//					//	Log.i("xxx-name",cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)));
//				} while(cur.moveToNext());
//			}
//		} catch (Exception e) {
//			//System.out.println(e.getStackTrace());
//			Log.e("xxx",e.getMessage());
//		}
//		return false;
//	}
//	void deleteAllContacts()
//	{
//		ContentResolver cr =getContentResolver();
//		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
//		while (cur.moveToNext()) {
//			try{
//				Log.i("contact-",cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)));
//				String lookupKey =cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//				Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//				System.out.println("The uri is " + uri.toString());
//				
//				cr.delete(uri,null, null);
//			}
//			catch(Exception e)
//			{
//				System.out.println(e.getStackTrace());
//			}
//		}
//	}
//	void deleteDoubleContacts(boolean checkname,boolean checkphone, boolean consolid, boolean ask)
//	{
//		try{
//		ContentResolver cr =getContentResolver();
//		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null,checkname?PhoneLookup.DISPLAY_NAME:PhoneLookup.NUMBER);
//		String tempname="temp string for contact name";
//		String tempphone="temp string for contact phone";
//		int del=0;
//		TextView tv=(TextView)findViewById(R.id.tv);
//		
//		while (cur.moveToNext()) {
//			
//			try{
//			
//				String lookupKey =cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//				Uri uri =Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//				//System.out.println("The uri is " + uri.toString());
//				String name=cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME));
//				String phone=cur.getString(cur.getColumnIndex(PhoneLookup.HAS_PHONE_NUMBER));
//				if((checkname|checkphone)&(((name.equals(tempname))|!checkname)&((phone.equals(tempphone))|!checkphone)))//|(((name.equals(tempname))&&checkname)&&((phone.equals(tempphone))&&checkphone)))
//				{
//					tv.append("\r\ndeleted contact-"+cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)));
//					Log.i("deleted contact-",cur.getString(cur.getColumnIndex(PhoneLookup.HAS_PHONE_NUMBER)));
//					//cr.delete(uri,null, null);
//					del++;
//				}
//				else 
//				{
//					tempname=name;
//					tempphone=phone;
//				}
//			}
//			catch(Exception e)
//			{
//				Log.e("xxx-error",e.getMessage());
//			}
//		}
//	
//		tv.append("\r\ndeleted "+del+" contacts from "+cur.getCount());
//		Log.i("xxx","deleted "+del+" contacts from "+cur.getCount());
//		}
//		catch(Exception e)
//		{
//			Log.e("xxx-error",e.getMessage());
//		}
//	}
//	
//	Cursor emailCur = cr.query(
//		ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//		null,
//		ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//		new String[]{id}, null);
//	while (emailCur.moveToNext()) {
//		// This would allow you get several email addresses
//// if the email addresses were stored in an array
//		String email = emailCur.getString(
//			emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//		String emailType = emailCur.getString(
//			emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//	}
//emailCur.close();
	
//	String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	String[] noteWhereParams = new String[]{id,
//		ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//	Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
//	if (noteCur.moveToFirst()) {
//		String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
//	}
//noteCur.close();
//	
//	String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	String[] addrWhereParams = new String[]{id,
//		ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
//	Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
//							  null, where, whereParameters, null);
//	while(addrCur.moveToNext()) {
//		String poBox = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
//		String street = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//		String city = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//		String state = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
//		String postalCode = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//		String country = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
//		String type = addrCur.getString(
//			addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
//	}
//addrCur.close();
//	
//	String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	String[] imWhereParams = new String[]{id,
//		ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
//	Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
//							null, imWhere, imWhereParams, null);
//	if (imCur.moveToFirst()) {
//		String imName = imCur.getString(
//			imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
//		String imType;
//		imType = imCur.getString(
//			imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
//	}
//imCur.close();
//	
//	String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	String[] orgWhereParams = new String[]{id,
//		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
//	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
//							 null, orgWhere, orgWhereParams, null);
//	if (orgCur.moveToFirst()) {
//		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
//		String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
//	}
//orgCur.close();
