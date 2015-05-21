package com.aroundme;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.AppConsts;
import com.aroundme.common.IAppCallBack;
import com.aroundme.controller.Controller;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.content.res.TypedArray;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

public class UsersTab extends ListFragment implements OnItemClickListener, IAppCallBack<List<UserAroundMe>>{

	private Controller controller;
	private Context context;
	private String[] menutitles;
	private TypedArray menuIcons;
	private CustomUsersAdapter adapter;
//	private HashMap<String, UserAroundMe> allUsers;
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
		 //Toast.makeText(getActivity(), menutitles[position], Toast.LENGTH_SHORT).show();
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
	}
	
	public void updateUsersList() {

	    adapter = new CustomUsersAdapter(getActivity(), allUsers);
        setListAdapter(adapter);
	    getListView().setOnItemClickListener(this);

/*		menutitles = new String[allUsers.size()];
        for (int i=0; i< allUsers.size(); i++) {
        	menutitles[i] = allUsers.get(i).getDisplayName();
        }
		ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, menutitles);
	    setListAdapter(adapter);

        rowItems = new ArrayList<UserItem>();

        for (int i = 0; i < menutitles.length; i++) {
        	UserItem item = new UserItem(menutitles[i]); //,menuIcons.getResourceId(i, -1)
            rowItems.add(item);
        }

     
//        adapter.notifyDataSetChanged();
*/		
	}

	
}