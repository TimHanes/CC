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
	private Parcelable state;

	public MessageHandler(ExecutorThread executor) {
		this.Executor = executor;
		TextView logView = (TextView) App.Instance().getActivity().findViewById(R.id.tv);
		_logView = logView;
	}

	public void handleMessage(Message msg) {
		MessageType _whichChoos = MessageType.values()[msg.what];

		switch (_whichChoos) {
		
		case SetButtonsApp:
			SetButtonsApp(msg);
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
		case ShowToast:
			ShowToast(msg);
			break;
		case Finally:
			App.Instance().getPopup().MsgBoxClose();
			break;	
		default:
			break;
		}
	}

	private void ShowToast(Message msg) {
		Toast toast = Toast.makeText(App.Instance().getContext(), msg.obj.toString(), Toast.LENGTH_SHORT); 
				toast.show(); 		
	}

	private void ShowPopupForChooseAction(Message msg) {		
		
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
		App.Instance().getPopup().MsgBoxButtons("What do with:", msg.obj.toString() + " ?", "Delete", "Cancel",
				_onButtonDelete, _onButtonIgnore);
	}
	
	
	
	private void ShowListView(Message msg) {
		
		OnItemClickListener listener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(Executor.getCurentHandlerAction() == HandlerActionType.ShowList.ordinal())
					state = App.Instance().getPopup().listView1.onSaveInstanceState();
				App.Instance().getPopup().MsgBoxClose();
				Executor.setClickPosition(position);	
				Executor.NextResultAction();
				Executor.Resume();
			}
		};
		View.OnClickListener cancel = new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				App.Instance().getPopup().MsgBoxClose();
				int a = Executor.getCurentHandlerAction();
				if(a == HandlerActionType.ShowDublicates.ordinal()){
					Executor.FirstResultAction();
					Executor.Resume();
				}
			}
		};
		ShowList showList = (ShowList) msg.obj;		
		App.Instance().getPopup().MsgBoxListView("Choose contact", showList, listener, cancel, state);

	}

	private void ShowProgress(Message msg) {
		View.OnClickListener cancel = new View.OnClickListener() {
			@Override
			public void onClick(View v) {					
				Executor.setmFinish(true);
				App.Instance().getPopup().MsgBoxClose();
			}
		};
		App.Instance().getPopup().MsgBoxProgress("Processing...", msg.obj.toString(), true, msg.arg1, msg.arg2, cancel);
	}

	private void SetButtonsApp(Message msg) {
		Button buttonReScan = (Button)App.Instance().getActivity().findViewById(R.id.btnStart);			
		Button buttonContinue = (Button)App.Instance().getActivity().findViewById(R.id.btnContinue);
		if(App.Instance().getDbProvider().getContactsUri().length > 0){
			buttonReScan.setText("RESCAN");
			buttonContinue.setVisibility(View.VISIBLE);
		}
		else{
			buttonReScan.setText("SCAN");
			buttonContinue.setVisibility(View.GONE);
		}
	}

	private void AddToLogView(Message msg) {
		_logView.append(msg.obj.toString());
	}
}
