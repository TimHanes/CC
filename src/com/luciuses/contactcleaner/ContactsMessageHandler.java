package com.luciuses.contactcleaner;

import android.app.Activity;

import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.os.Handler;
import android.os.Message;

	public class ContactsMessageHandler extends Handler
	{
		private TextView _logView;				
		public boolean _flagClickButtons;
		private ContactsProvider _contactsProvider;
		private boolean mFinished = false;		
		public ContactsMessageHandler()
		{					
			TextView logView = (TextView)App.Instance().activity.findViewById(R.id.tv);
			_contactsProvider = new ContactsProvider();
			_logView = logView;			
			
		}
		
		public boolean getOnFinished(){
			return mFinished;
		}
		
		@Override 
		public void handleMessage(Message msg)
		{
			MessageType _whichChoos = MessageType.values()[msg.what];
			
			switch (_whichChoos) {
			case AddToLogView:
				AddToLogView (msg);
				break;		
			case SetTextToLogView:
				SetTextToLogView (msg);
				break;
			case ShowProgress:
				ShowProgress(msg);
				break;
			case Finally:				
				Finally();				
				break;
			case SetProgressBar:
				SetProgressBar(msg);
				break;
			case ShowListView:
				ShowListView(msg);
				break;
			default:
				break;
			}
		}

		private void ShowListView(Message msg) {
			
		}

		private void SetProgressBar(Message msg)
		{
			App.Instance().Popup.pb.setMax(msg.arg1);
			App.Instance().Popup.pb.setProgress(msg.arg2);
		}

		private void Finally()
		{			
			App.Instance().Popup.MsgBoxClose ();			
		}

		private void ShowProgress(Message msg)
		{
			View.OnClickListener cancel = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mFinished = true;					
					Finally();					
				}				
			};
			
			App.Instance().Popup.MsgBoxProgress("Processing...",msg.obj.toString(),true, msg.arg1, msg.arg2, cancel);
		}

		private void SetTextToLogView(Message msg)
		{
//			_logView.setText (msg.obj.toString (), TextView.BufferType.NORMAL);
		}

		private void AddToLogView(Message msg)
		{
//			_logView.append (msg.obj.toString ());
		}


		
				
	}

