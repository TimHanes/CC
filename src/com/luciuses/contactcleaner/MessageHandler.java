package com.luciuses.contactcleaner;

import android.os.*;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.app.*;
import android.content.*;
import java.text.*;


	public class MessageHandler extends Handler
	{
		private TextView _logView;
		private ContactsHandler _contactsHandler;
		View.OnClickListener _onButtonJoin;
		View.OnClickListener _onButtonIgnore;
		View.OnClickListener _onButtonDelete;
		private boolean _flagClickButtons;

		public MessageHandler(ContactsHandler contactsHandler)
		{
			_contactsHandler = contactsHandler;
			_logView = (TextView)App.Instance().RelativeLayout.findViewById(R.id.tv);
			App.Instance().Popup.invisible ();
		}

		@Override 
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
			case ShowProgress:
				ShowProgress(msg);
				break;
			case Finally:
				Finally();
				break;
			case SetProgressBar:
				SetProgressBar(msg);
				break;
			}
		}

		private void SetProgressBar(Message msg)
		{
			App.Instance().Popup.pb.setMax(msg.arg1);
			App.Instance().Popup.pb.setProgress(msg.arg2);
		}

		private void Finally()
		{
			App.Instance().Popup.MsgBoxClose ();
//			App.Instance.ProgressShower.Dismiss ();
		}

		private void ShowProgress(Message msg)
		{
			View.OnClickListener cancel = new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					_contactsHandler.Pause();
					Finally();
				}
			};
			
			App.Instance().Popup.MsgBoxProgress("Processing...",msg.obj.toString(),true, msg.arg1, msg.arg2, cancel);
		}

		private void SetTextToLogView(Message msg)
		{
			_logView.setText (msg.obj.toString (), TextView.BufferType.NORMAL);
		}

		private void AddToLogView(Message msg)
		{
			_logView.append (msg.obj.toString ());
		}


		private void ShowPopupForChooseAction(Message msg)
		{
			_flagClickButtons = true;

			OnClickListener _onButtonDelete = new OnClickListener() { 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (_flagClickButtons){
					App.Instance().Popup.MsgBoxClose();
					_contactsHandler.ContactDelete(
						_contactsHandler.FoundUri()[_contactsHandler.CurrentContactIndex()]);
						_contactsHandler.Resume();
						_flagClickButtons = !_flagClickButtons;
				}
			}
			};

			OnClickListener _onButtonJoin = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (_flagClickButtons){

					App.Instance().Popup.MsgBoxClose();
					_contactsHandler.ContactJoin (
						_contactsHandler.FoundUri() [_contactsHandler.CurrentContactIndex()]);
					_contactsHandler.Resume();
						_flagClickButtons = !_flagClickButtons;
					}
				} 							
			};

			OnClickListener _onButtonIgnore = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (_flagClickButtons){

						App.Instance().Popup.MsgBoxClose();
						_contactsHandler.ContactIgnore();
							_contactsHandler.Resume();
							_flagClickButtons = !_flagClickButtons;
						}
				}
				
			};
			App.Instance().Popup.MsgBoxButtons ("What do with:", msg.obj.toString () + " ?", "Delete", "Join", "Ignore", _onButtonDelete, _onButtonJoin, _onButtonIgnore, true);
		}
	}

