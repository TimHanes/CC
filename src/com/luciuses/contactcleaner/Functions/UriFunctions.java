package com.luciuses.contactcleaner.Functions;

import android.net.Uri;
import android.text.TextUtils;
 
 public class UriFunctions {
 	
	public Uri[] StpingToUri(String[] uriStr){
		Uri[] uri = new Uri[uriStr.length];
		for (int i = 0; i < uriStr.length; i++){
			uri[i] = Uri.parse(uriStr[i]);
		}		
		return uri;
	}
	
	public String UriSerialization(Uri[] urisDublArr){
		String[] urisDublStrArr =  UriToString(urisDublArr);
    	String urisDublStr = TextUtils.join(" ", urisDublStrArr );
		return urisDublStr;
	}
	
	public String[] UriToString(Uri[] uri){
		String[] uriStr = new String[uri.length];
		for (int i = 0; i < uri.length; i++){
			uriStr[i] = uri[i].toString();
		}		
		return uriStr;
	}	
}