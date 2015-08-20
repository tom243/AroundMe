package com.aroundme;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.adapter.ConversationsAdapter;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.List;

/**
 * @author Tomer and chen
 * 
 * open conversation TAB
 *
 */
public class OpenConversationsTab extends ListFragment implements OnItemClickListener, 
						IAppCallBack<List<UserAroundMe>>,SplashInterface{

	private Controller controller;
	private Context context;
	private ConversationsAdapter adapter;
	private List<ConversationItem> conversations;
	private IDataAccess dao;
	private List<UserAroundMe> allUsers;
	private ProgressBar progressBar;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context = inflater.getContext();
		progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
		return inflater.inflate(R.layout.open_conversations_tab, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		controller = Controller.getInstance();
		dao = DAO.getInstance(context);
		getConversationListFromDB();
		if (controller.isOnline(AroundMeApp.getContext()))
			getUsers(); // maybe async
		else
			Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show();
		
		LocalBroadcastManager.getInstance(AroundMeApp.getContext()).registerReceiver(newOpenConversation, new IntentFilter("updateOpenCoversationsAdapter"));
		/** Registering context menu for the listview */
        registerForContextMenu(getListView());
	}
	
    /** This will be invoked when an item in the listview is long pressed */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.actions , menu);
    }
    
    
    /** This will be invoked when a menu item is selected */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
 
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        long currentFriend = info.id;
        String friendMail = conversations.get((int)currentFriend).getFriendMail();
        switch(item.getItemId()){
            case R.id.cnt_mnu_delete:
            	removeOpenConversation(friendMail);
                break;
        }
        return true;
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(AroundMeApp.getContext()).unregisterReceiver(newOpenConversation);
		
	}

	private BroadcastReceiver newOpenConversation = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	refreshAdapter();
	    }
	};
	
	private void removeOpenConversation(String friendMail){
		dao.open();
		ConversationItem conv = dao.isConversationExist(controller.getCurrentUser().getMail(),friendMail);
		if (conv != null) {
			dao.removeFromConversationTable(conv);
			dao.close();
			refreshAdapter();
		}
	}
	
	private void getConversationListFromDB(){
		dao.open();
		conversations = dao.getAllOpenConversationsList(controller.getCurrentUser().getMail());
		dao.close();
	}	
		
	public void getUsers() {
	    allUsers = controller.getAllUsersList(); // not going to server
	    if (allUsers.isEmpty())
	    	controller.getAllUsersFromServer(this,this);
	    else
	    	addImageUrlToConversationItem();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	     // ListView Clicked item value
	     String friendMail = conversations.get(position).getFriendMail();
		 Intent i = new Intent(getActivity(), ConversationActivity.class);
	     i.putExtra(AppConsts.email_friend,friendMail);
	     startActivity(i);  
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

	@Override
	public void done(List<UserAroundMe> ret, Exception e) {
		addImageUrlToConversationItem();
	}
	
	private void addImageUrlToConversationItem(){
		HashMap<String, UserAroundMe> allUsers = controller.getAllUsers();
		for (ConversationItem conv: conversations){
			conv.setImageUrl(allUsers.get(conv.getFriendMail()).getImageUrl());
		}
		// create adapter
		adapter = new ConversationsAdapter(getActivity(), conversations);
	    setListAdapter(adapter);
	    getListView().setOnItemClickListener(this); 
		adapter.notifyDataSetChanged();
	}

	private void refreshAdapter(){
		getConversationListFromDB();
		if (controller.isOnline(AroundMeApp.getContext()))
			getUsers();
		else
			Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void visible(Exception e) {
		if (e == null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void unvisible(Exception e) {
		if (e == null) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	
}