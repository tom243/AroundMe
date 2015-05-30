package com.aroundme;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.adapter.CustomUsersAdapter;
import com.aroundme.common.AppConsts;
import com.aroundme.common.IAppCallBack;
import com.aroundme.controller.Controller;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;

public class UsersTab extends ListFragment implements OnItemClickListener, IAppCallBack<List<UserAroundMe>>{

	private Controller controller;
	private Context context;
	private CustomUsersAdapter adapter;
	private List<UserAroundMe> allUsers;

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context = inflater.getContext();
		return inflater.inflate(R.layout.users_tab, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		controller = Controller.getInstance();
	    allUsers = controller.getAllUsersList(); // not going to server
	    if (allUsers.isEmpty())
	    	controller.getAllUsersFromServer(this);
	    else
			updateUsersList();
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	     // ListView Clicked item value
	     String friendMail = allUsers.get(position).getMail();
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
		if (e==null) {
		   	for (UserAroundMe user : ret) 
				allUsers.add(user);
		   	updateUsersList();
		}
		Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
	    LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(updateAdapterIntent);
	}
	
	public void updateUsersList() {

	    adapter = new CustomUsersAdapter(getActivity(), allUsers);
        setListAdapter(adapter);
	    getListView().setOnItemClickListener(this);
	}
	
}