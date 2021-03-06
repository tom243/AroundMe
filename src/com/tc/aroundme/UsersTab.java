package com.tc.aroundme;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.tc.aroundme.R;
import com.tc.aroundme.adapter.UsersAdapter;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.AroundMeApp;
import com.tc.aroundme.common.IAppCallBack;
import com.tc.aroundme.common.SplashInterface;
import com.tc.aroundme.controller.Controller;

import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;

/**
 * @author Tomer and chen
 *
 * list of all the users that using the application
 */
public class UsersTab extends ListFragment implements OnItemClickListener, 
				IAppCallBack<List<UserAroundMe>>, SplashInterface{

	private Controller controller;
	private Context context;
	private UsersAdapter adapter;
	private List<UserAroundMe> allUsers;
	private ProgressBar progressBar;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context = inflater.getContext();
		setHasOptionsMenu(true);
		progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
		return inflater.inflate(R.layout.users_tab, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		controller = Controller.getInstance();
	    allUsers = controller.getAllUsersList(); // not going to the server
	    if (allUsers.isEmpty())
	    	if (controller.isOnline(AroundMeApp.getContext()))
	    		controller.getAllUsersFromServer(this,this);
	    	else
	    		Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show();
	    else
			updateUsersList();
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	     // ListView Clicked item value
	     String friendMail = allUsers.get(position).getMail();
		 Intent i = new Intent(getActivity(), ConversationActivity.class);
	     i.putExtra(AppConsts.EMAIL_FRIEND,friendMail);
	     startActivity(i);
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

	@Override
	public void done(List<UserAroundMe> ret, Exception e) {
		if (e==null) {
		   	for (UserAroundMe user : ret) 
				allUsers.add(user);
		   	updateUsersList();
		}
		Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
	    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(updateAdapterIntent);
	}
	
	/**
	 *  update the user list in adapter
	 */
	public void updateUsersList() {
	    adapter = new UsersAdapter(getActivity(), allUsers);
        setListAdapter(adapter);
	    getListView().setOnItemClickListener(this);
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