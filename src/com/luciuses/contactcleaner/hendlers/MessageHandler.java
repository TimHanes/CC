package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;
import com.luciuses.contactcleaner.components.MessageType;
import com.luciuses.contactcleaner.providers.ContactsProvider;
import com.luciuses.contactcleaner.treads.ExecutorThread;

import android.net.Uri;
import android.os.*;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

	public class MessageHandler extends Handler
	{
		private TextView _logView;				
		private boolean _flagClickButtons;
		private ContactsProvider contactsProvider;
		ExecutorThread Executor;
		
		public MessageHandler(ExecutorThread executor)
		{		
			this.Executor = executor;
			TextView logView = (TextView)App.Instance().getActivity().findViewById(R.id.tv);
			contactsProvider = new ContactsProvider(this);
			_logView = logView;			
		}
		
		public void handleMessage(Message msg)
		{
			MessageType _whichChoos = MessageType.values()[msg.what];
			
			switch (_whichChoos) {
			case AddToLogView:
				AddToLogView (msg);
				break;
			case ShowPopupForChooseAction:
				ShowPopupForChooseAction (msg);
				break;
			case SetTextToLogView:
				SetTextToLogView (msg);
				break;		
			case Finally:				
				Finally();				
				break;
			case ShowProgress:				
				ShowProgress(msg);				
				break;
			case SetProgressBar:
				SetProgressBar(msg);
				break;
			case ShowListView:
				ShowListView(msg);
				break;
			case ContactExecuted:
				ContactExecuted(msg);
				break;
			default:
				break;				
			}
		}
		
		

		private void ShowPopupForChooseAction(Message msg)
		{
			_flagClickButtons = true;
			final int contactId = msg.arg2;
			OnClickListener _onButtonDelete = new OnClickListener() { 
			@Override
			public void onClick(View v) {
				if (_flagClickButtons){
					Uri uri = contactsProvider.UriById(contactId);
					App.Instance().getPopup().MsgBoxClose();
					App.Instance().getDbProvider().ContactDelete(uri);
					contactsProvider.ContactDelete(contactId);					
						_flagClickButtons = !_flagClickButtons;
						Executor.getResultHandler().getTread().Resume();
				}
			}
			};

			OnClickListener _onButtonJoin = new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (_flagClickButtons){

					App.Instance().getPopup().MsgBoxClose();
					contactsProvider.ContactJoin (contactId);
					
						_flagClickButtons = !_flagClickButtons;
						Executor.getResultHandler().getTread().Resume();
					}
				} 							
			};

			OnClickListener _onButtonIgnore = new OnClickListener() {

				@Override
				public void onClick(View v) {					
					if (_flagClickButtons){

						App.Instance().getPopup().MsgBoxClose();						
							
							_flagClickButtons = !_flagClickButtons;						
							Executor.getResultHandler().getTread().Resume();
						}
				}
				
			};
			App.Instance().getPopup().MsgBoxButtons ("What do with:", msg.obj.toString () + " ?", "Delete", "Join", "Ignore", _onButtonDelete, _onButtonJoin, _onButtonIgnore, true);
		}

		private void ShowListView(Message msg) {

			OnItemClickListener listener = new OnItemClickListener() {
			      public void onItemClick(AdapterView<?> parent, View view,
			          int position, long id) {
			    	  Executor.getResultHandler().ByPosition(position);			    	 
			      }
			    };
						
			App.Instance().getPopup().MsgBoxListView("Duble contacts", (String[])msg.obj, listener);
			
		}

		private void SetProgressBar(Message msg)
		{
			App.Instance().getPopup().pb.setMax(msg.arg1);
			App.Instance().getPopup().pb.setProgress(msg.arg2);
		}

		private void Finally()
		{			
			App.Instance().getPopup().MsgBoxClose ();			
		}

		private void ShowProgress(Message msg)
		{
			View.OnClickListener cancel = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Executor.getContactsHandler().setMarkFinish(true);			
				}				
			};						
			App.Instance().getPopup().MsgBoxProgress("Processing...",msg.obj.toString(),true, msg.arg1, msg.arg2, cancel);
		}

		private void SetTextToLogView(Message msg)
		{
			_logView.setText (msg.obj.toString (), TextView.BufferType.NORMAL);
		}

		private void AddToLogView(Message msg)
		{
			_logView.append (msg.obj.toString ());
		}	
		
		private void ContactExecuted(Message msg) {
			Executor.Resume();
		}
	}

