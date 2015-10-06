package com.luciuses.contactcleaner.hendlers;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.R;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.models.ShowList;
import com.luciuses.contactcleaner.treads.ExecutorThread;

import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class MessageHandler extends Handler {
	
	private TextView _logView;
	private ExecutorThread Executor;

	public MessageHandler(ExecutorThread executor) {
		this.Executor = executor;
		TextView logView = (TextView) App.Instance().getActivity().findViewById(R.id.tv);
		_logView = logView;
	}

	public void handleMessage(Message msg) {
		MessageType _whichChoos = MessageType.values()[msg.what];

		switch (_whichChoos) {
		
		case SetTextToLogView:
			SetTextToLogView(msg);
			break;
		case AddToLogView:
			AddToLogView(msg);
			break;
		case ShowProgress:
			ShowProgress(msg);
			break;
		case ShowListView:
			ShowListView(msg);
			break;			
		case ShowPopupForChooseAction:
			ShowPopupForChooseAction(msg);
			break;
		default:
			break;
		}
	}

	private void ShowPopupForChooseAction(Message msg) {		
		OnClickListener _onButtonJoin = new OnClickListener() {
			@Override
			public void onClick(View v) {
				App.Instance().getPopup().MsgBoxClose();
				Executor.setAction(ActionType.Join);
				Executor.NextResultAction();
				Executor.Resume();
			}
		};

		OnClickListener _onButtonDelete = new OnClickListener() {
			@Override
			public void onClick(View v) {									
				App.Instance().getPopup().MsgBoxClose();				
				Executor.setAction(ActionType.Delete);
				Executor.NextResultAction();
				Executor.Resume();
			}
		};
		
		OnClickListener _onButtonIgnore = new OnClickListener() {
			@Override
			public void onClick(View v) {
				App.Instance().getPopup().MsgBoxClose();
				Executor.setAction(ActionType.Ignore);
				Executor.NextResultAction();
				Executor.Resume();				
			}
		};
		App.Instance().getPopup().MsgBoxButtons("What do with:", msg.obj.toString() + " ?", "Delete", "Join", "Ignore",
				_onButtonDelete, _onButtonJoin, _onButtonIgnore, false);
	}
	
	
	
	private void ShowListView(Message msg) {
		OnItemClickListener listener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
				App.Instance().getPopup().MsgBoxClose();
				Executor.setClickPosition(position);	
				Executor.NextResultAction();
				Executor.Resume();
			}
		};
		
		ShowList showList = (ShowList) msg.obj;		
		App.Instance().getPopup().MsgBoxListView("Choose contact", showList, listener);

	}

	private void ShowProgress(Message msg) {
		View.OnClickListener cancel = new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				Executor.getContactsHandler().setMarkFinish(true);
				App.Instance().getPopup().MsgBoxClose();
			}
		};
		App.Instance().getPopup().MsgBoxProgress("Processing...", msg.obj.toString(), true, msg.arg1, msg.arg2, cancel);
	}

	private void SetTextToLogView(Message msg) {
		_logView.setText(msg.obj.toString(), TextView.BufferType.NORMAL);
	}

	private void AddToLogView(Message msg) {
		_logView.append(msg.obj.toString());
	}
}
