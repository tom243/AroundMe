package com.aroundme;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.adapter.CustomConversationsAdapter;
import com.aroundme.common.AppConsts;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.IAppCallBack;
import com.aroundme.controller.Controller;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.List;

public class OpenConversationsTab extends ListFragment implements OnItemClickListener, IAppCallBack<List<UserAroundMe>>{

	private Controller controller;
	private Context context;
	private String[] menutitles;
	private TypedArray menuIcons;
	private CustomConversationsAdapter adapter;
	private List<ConversationItem> conversations;
	private IDataAccess dao;
	private List<UserAroundMe> allUsers;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context = inflater.getContext();
		return inflater.inflate(R.layout.open_conversations_tab, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		controller = Controller.getInstance();
		dao = DAO.getInstance(context);
		getConversationListFromDB();
		adapter = new CustomConversationsAdapter(getActivity(), conversations);
	    setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(newOpenConversation, new IntentFilter("updateOpenCoversationsAdapter"));
	}
	
	
	
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(newOpenConversation);
		
	}


	private BroadcastReceiver newOpenConversation = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	getConversationListFromDB();
			adapter = new CustomConversationsAdapter(getActivity(), conversations); // Im not sure i need to create new adapter every time ask with chen about it 
		    setListAdapter(adapter);
	        adapter.notifyDataSetChanged();
	    }
	};
	
	
	public void getConversationListFromDB(){
		dao.open();
		conversations = dao.getAllOpenConversationsList(controller.getCurrentUser().getMail());
		dao.close();
	    allUsers = controller.getAllUsersList(); // not going to server
	    if (allUsers.isEmpty())
	    	controller.getAllUsersFromServer(this);
	    else
	    	addImageUrlToConversationItem();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	     // ListView Clicked item value
	     String friendMail = conversations.get(position).getFriendMail();
	     Toast.makeText(getActivity(), friendMail , Toast.LENGTH_SHORT).show();
	     
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
	
	
	public void addImageUrlToConversationItem(){
		HashMap<String, UserAroundMe> allUsers = controller.getAllUsers();
		for (ConversationItem conv: conversations){
			conv.setImageUrl(allUsers.get(conv.getFriendMail()).getImageUrl());
		}
	}
	
}