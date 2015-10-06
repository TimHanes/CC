
package com.luciuses.contactcleaner.screens;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;
import com.luciuses.contactcleaner.models.ShowList;

import android.app.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class Popup
{
	public Dialog dialog;
	public Activity activity;
	public TextView txt1;
	public TextView txt2;
	public Button btn1;
	public Button btn2;
	public Button btn3;
	public EditText edit1;
	public EditText edit2;
	public ProgressBar pb;
	public CheckBox chbsave;
	public TextView chvsave;
	private ListView listView1;
	public Popup (Activity iactivity)
	{
		activity=iactivity;
		dialog = new Dialog(activity);
		dialog.setContentView(R.layout.popup);//popup view is the layout you created
		listView1 = (ListView)dialog.findViewById(R.id.listView1);
		txt1 = (TextView)dialog.findViewById(R.id.mbtext1);
		txt2 = (TextView)dialog.findViewById(R.id.mbtext2);
		btn3=(Button)dialog.findViewById(R.id.mbbtn3);
		btn1=(Button)dialog.findViewById(R.id.mbbtn1);
		btn2=(Button)dialog.findViewById(R.id.mbbtn2);
		chbsave = (CheckBox)dialog.findViewById(R.id.checksave);
		chvsave=(TextView)dialog.findViewById(R.id.chvsave);
		edit1=(EditText)dialog.findViewById(R.id.mbedit1);
		edit2=(EditText)dialog.findViewById(R.id.mbedit2);
		pb=(ProgressBar)dialog.findViewById(R.id.mbpbar);
	}
	public void invisible()
	{
		listView1.setVisibility(View.GONE);
		txt1.setVisibility(View.GONE);
		txt2.setVisibility(View.GONE);
		btn3.setVisibility(View.GONE);
		btn1.setVisibility(View.GONE);//=(Button)dialog.findViewById(R.id.mbbtn1);
		btn2.setVisibility(View.GONE);//=(Button)dialog.findViewById(R.id.mbbtn2);
		chbsave.setVisibility(View.GONE);// = (CheckBox)dialog.findViewById(R.id.checksave);
		chvsave.setVisibility(View.GONE);//=(TextView)dialog.findViewById(R.id.chvsave);
		edit1.setVisibility(View.GONE);//=(EditText)dialog.findViewById(R.id.mbedit1);
		edit2.setVisibility(View.GONE);//=(EditText)dialog.findViewById(R.id.mbedit2);
		pb.setVisibility(View.GONE);//=(ProgressBar)dialog.findViewById(R.id.mbpbar);
	}
	public void MsgBox(String msg)
	{
		invisible();
		txt1.setVisibility(View.VISIBLE);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	public void MsgBox(String title,String msg)
	{
		invisible();
		dialog.setTitle(title);
		txt1.setVisibility(View.VISIBLE);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	public void MsgBoxButtons(String title,String msg,View.OnClickListener btn2lst)
	{
		invisible();
		dialog.setTitle(title);
		btn1.setText("CANCEL");
		btn2.setText("OK");
		txt1.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(new View.OnClickListener(){public void onClick(View v){dialog.cancel();}});
		btn2.setOnClickListener(btn2lst);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}

	}
	public void MsgBoxButtons(String title,String msg,View.OnClickListener btn1lst,View.OnClickListener btn2lst)
	{
		invisible();
		dialog.setTitle(title);
		btn1.setText("CANCEL");
		btn2.setText("OK");
		txt1.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(btn1lst);
		btn2.setOnClickListener(btn2lst);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
		
	}
	public void MsgBoxButtons(String title,String msg,String button1name,String button2name,View.OnClickListener btn1lst,View.OnClickListener btn2lst)
	{
		invisible();
		dialog.setTitle(title);
		btn1.setText(button1name);
		btn2.setText(button2name);
		txt1.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(btn1lst);
		btn2.setOnClickListener(btn2lst);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	public void MsgBoxButtons(String title,String msg,View.OnClickListener btn1lst,View.OnClickListener btn2lst,View.OnClickListener btn3lst)
	{
		invisible();
		dialog.setTitle(title);
		btn1.setText("CANCEL");
		btn2.setText("OK");
		txt1.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(btn1lst);
		btn2.setOnClickListener(btn2lst);
		btn3.setVisibility(View.VISIBLE);
		btn3.setOnClickListener(btn3lst);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}

	}
	public void MsgBoxButtons(String title,String msg,String button1name,String button2name,String button3name,View.OnClickListener btn1lst,View.OnClickListener btn2lst,View.OnClickListener btn3lst)
	{
		invisible();
		dialog.setTitle(title);
		btn1.setText(button1name);
		btn2.setText(button2name);
		btn3.setText(button3name);
		txt1.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		btn3.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(btn1lst);
		btn3.setOnClickListener(btn3lst);
		btn2.setOnClickListener(btn2lst);
		txt1.setText(msg);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	public void MsgBoxButtons(String title,String msg,String button1name,String button2name,String button3name,View.OnClickListener btn1lst,View.OnClickListener btn2lst,View.OnClickListener btn3lst,boolean usesave)
	{
		invisible();
		dialog.setTitle(title);
		btn1.setText(button1name);
		btn2.setText(button2name);
		btn3.setText(button3name);
		txt1.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		btn3.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(btn1lst);
		btn3.setOnClickListener(btn3lst);
		btn2.setOnClickListener(btn2lst);
		txt1.setText(msg);
		if(usesave)
		{
		chvsave.setVisibility(View.VISIBLE);
		chbsave.setVisibility(View.VISIBLE);
		}
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	public void MsgBoxLogin(View.OnClickListener btn2lst)
	{
		invisible();
		dialog.setTitle("Login/Password");
		btn1.setText("CANCEL");
		btn2.setText("OK");
		txt1.setVisibility(View.VISIBLE);
		txt1.setText("Login");
		txt2.setVisibility(View.VISIBLE);
		txt2.setText("Password");
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		edit1.setVisibility(View.VISIBLE);
		edit2.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(new View.OnClickListener(){public void onClick(View v){dialog.cancel();}});
		btn2.setOnClickListener(btn2lst);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	public void MsgBoxLogin(String login, String psw, View.OnClickListener btn2lst)
	{
		invisible();
		dialog.setTitle("Login/Password");
		edit1.setText(login);
		edit2.setText(psw);
		btn1.setText("CANCEL");
		btn2.setText("OK");
		txt1.setVisibility(View.VISIBLE);
		txt1.setText("Login");
		txt2.setVisibility(View.VISIBLE);
		txt2.setText("Password");
		btn1.setVisibility(View.VISIBLE);
		btn2.setVisibility(View.VISIBLE);
		edit1.setVisibility(View.VISIBLE);
		edit2.setVisibility(View.VISIBLE);
		btn1.setOnClickListener(new View.OnClickListener(){public void onClick(View v){dialog.cancel();}});
		btn2.setOnClickListener(btn2lst);
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	
	
	public void MsgBoxListView(String title, ShowList showList, OnItemClickListener listener)
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(App.Instance().getContext(),android.R.layout.simple_list_item_1, showList.getBody());
		invisible();
		dialog.setTitle(title);		
		listView1.setVisibility(View.VISIBLE);
		listView1.setAdapter(adapter);
		listView1.setOnItemClickListener(listener);
		txt1.setVisibility(View.VISIBLE);
		txt1.setText(showList.getHeader());
			
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}
	
	public void MsgBoxProgress(String title,String msg,boolean horizontal, int arg1, int arg2, OnClickListener cancel)
	{
		invisible();
		dialog.setTitle(title);
		txt1.setVisibility(View.VISIBLE);
		txt2.setVisibility(View.VISIBLE);
		txt1.setText(msg);
		txt2.setText("Whole: "+arg1+"/ Checked: "+arg2);
		pb.setVisibility(View.VISIBLE);
		btn1.setVisibility(View.VISIBLE);
		btn1.setText("CANCEL");
		btn1.setOnClickListener(cancel);
		pb.setProgress(arg2);
		pb.setMax(arg1);		
		pb.setIndeterminate(false);
		
		try
		{
			dialog.show();
		}
		catch(Exception err)
		{
			Log.d("xxx",err.getMessage());
		}
	}

	public void MsgBoxCencel()
	{
		dialog.cancel();
		dialog.dismiss();
		dialog=null;
	}
	public void MsgBoxClose()
	{
		dialog.hide();
	}
}
	
