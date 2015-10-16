package com.luciuses.contactcleaner;

import java.util.ArrayList;
import java.util.Arrays;

import com.luciuses.contactcleaner.App;
import com.luciuses.contactcleaner.Duplicates;
import com.luciuses.contactcleaner.ProviderDuplicatesDb;
import com.luciuses.contactcleaner.Functions.Functions;
import com.luciuses.contactcleaner.actions.DuplicatesSearcher;
import com.luciuses.contactcleaner.basis.BaseThread;
import com.luciuses.contactcleaner.components.*;
import com.luciuses.contactcleaner.hendlers.*;
import com.luciuses.contactcleaner.models.*;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class Executor extends BaseThread
{
	private ActionType action;
	private StepType step;
	private ProviderDuplicatesDb dbProvider;
	private ProviderContactsDb contactsProvider;
	private ArrayList<String> list;	
	private Scaner scaner;
	private Shower shower;
	private int clickPosition;
	private Duplicates duplicates;
	private MessageHandler messageHandler;
	private boolean Finish;
	
	public Executor(ProviderDuplicatesDb dbProvider) {		
		this.dbProvider = dbProvider;				
		list = new ArrayList<String>(Arrays.asList(dbProvider.getSourses()));
		messageHandler = new MessageHandler(this);
		shower = new Shower(this);
		this.contactsProvider = new ProviderContactsDb(messageHandler);
		scaner = new Scaner(this);
		super.setName("Executor");
	}
	public ArrayList<String> getList() {
		return list;
	}
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public StepType getStep() {
		return step;
	}
	public void setStep(StepType step) {
		this.step = step;
	}	
	
	public int getClickPosition() {
		return clickPosition;
	}
	public void setClickPosition(int clickPosition) {
		this.clickPosition = clickPosition;
	}
	public boolean isFinish() {
		return Finish;
	}
	public void setFinish(boolean finish) {
		Finish = finish;
	}
	public ProviderContactsDb getContactsProvider() {
		return contactsProvider;
	}
	public ProviderDuplicatesDb getDbProvider() {
		return dbProvider;
	}
	
	@Override
	public void run() {	
		
		do{
			switch (step) {
			case Start:		
				this.Pause();
				super.run();
				break;	
			case SearchesDublicates:
				if(new Options().isChoosed()){
					scaner.Scan();					
					break;
				}
				Message.obtain(messageHandler, MessageType.ShowToast.ordinal(),	"Please! Choose options.").sendToTarget();
				setStep(StepType.Start);
				break;			
			case ShowList:
				if(list.isEmpty()){					
					setStep(StepType.Finish);
					break;
					}
				String[] listArray = new String[list.size()];
				shower.ShowList(list.toArray(listArray));
				this.Pause();
				super.run();
				break;
			case ShowDublicates:
				duplicates = dbProvider.getByPosition(clickPosition);
				shower.ShowList(duplicates);
				this.Pause();
				super.run();
				break;
			case ShowChooseAction:
				shower.ShowChooseAction(duplicates.getIdDuplicates()[clickPosition]);
				this.Pause();
				super.run();
				break;
			case Executes:
				Executes(duplicates.getIdDuplicates()[clickPosition], action);	
				if(list.isEmpty()){
					setStep(StepType.Finish);
					break;
					}
				setStep(StepType.ShowList);
				break;	
			case Finish:	
				Message.obtain(messageHandler, MessageType.ShowToast.ordinal(),
						"End processing!").sendToTarget();
				setStep(StepType.Start);
				break;
			default:
				break;				
			}			
		}
		while(true);
	}
	
	private void Executes(String id, ActionType action) {
		
		switch (action) {
		
		case None:
			
			break;
			
		case Delete:			
			Deleted(id);		
			UpdateContact(duplicates);
			break;
			
		case Join:
			
			break;
			
		case Ignore:
			
			break;	
			
		default:
			break;
		}		
		Message.obtain(messageHandler, MessageType.SetButtonsApp.ordinal()).sendToTarget();		
	}
	private void UpdateContact(Duplicates duplicates) {
		DuplicatesSearcher searcher = new DuplicatesSearcher(this);
		searcher.Search(duplicates.getType(), duplicates.getSourse());
	}
	private void Deleted(String id) {
		Contact contact = contactsProvider.getContactById(id);
		list.remove(duplicates.getSourse());
		dbProvider.ContactDelete(duplicates.getSourse());					
		contactsProvider.DeleteContact(id);	
		Message.obtain(messageHandler, MessageType.AddToLogView.ordinal(), "Deleted: " 
		+ new Functions().ContactToString(contact)).sendToTarget();
			
	}
	public void CleanList() {
		list.clear();
	}
}
	
	