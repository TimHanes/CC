package com.luciuses.contactcleaner;

import android.app.Activity;
import android.os.*;
import android.view.View;
import android.view.View.*;
import android.widget.*;

	public class MessageHandler extends Handler
	{
		private TextView _logView;				
		public boolean _flagClickButtons;
		private ContactsProvider _contactsProvider;
		private boolean mFinished = false;	
		StepsController stepsController;
		
		public MessageHandler(StepsController stepsController)
		{		
			this.stepsController = stepsController;
			TextView logView = (TextView)App.Instance().activity.findViewById(R.id.tv);
			_contactsProvider = new ContactsProvider();
			_logView = logView;			
		}
		
		public boolean getOnFinished(){
			return mFinished;
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
				// TODO Auto-generated method stub
				if (_flagClickButtons){
					App.Instance().Popup.MsgBoxClose();
					_contactsProvider.ContactDelete(contactId);
					
						_flagClickButtons = !_flagClickButtons;
						stepsController.getResultHandlerByOnce().Resume();
				}
			}
			};

			OnClickListener _onButtonJoin = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (_flagClickButtons){

					App.Instance().Popup.MsgBoxClose();
					_contactsProvider.ContactJoin (contactId);
					
						_flagClickButtons = !_flagClickButtons;
						stepsController.getResultHandlerByOnce().Resume();
					}
				} 							
			};

			OnClickListener _onButtonIgnore = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (_flagClickButtons){

						App.Instance().Popup.MsgBoxClose();						
							
							_flagClickButtons = !_flagClickButtons;
							stepsController.getResultHandlerByOnce().Resume();
						}
				}
				
			};
			App.Instance().Popup.MsgBoxButtons ("What do with:", msg.obj.toString () + " ?", "Delete", "Join", "Ignore", _onButtonDelete, _onButtonJoin, _onButtonIgnore, true);
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

