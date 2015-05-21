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
import com.aroundme.data.DAO;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.content.res.TypedArray;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class OpenConversationsTab extends ListFragment implements OnItemClickListener{

	private Controller controller;
	private Context context;
	private String[] menutitles;
	private TypedArray menuIcons;
	private CustomConversationsAdapter adapter;
	private List<ConversationItem> conversations;
	private DAO dao;

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
		dao.open();
		
		System.out.println();
		conversations = dao.getAllOpenConversationsList(controller.getCurrentUser().getMail());
		dao.close();

		adapter = new CustomConversationsAdapter(getActivity(), conversations);
	    setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 //Toast.makeText(getActivity(), menutitles[position], Toast.LENGTH_SHORT).show();
	     // ListView Clicked item value
/*	     String friendMail = allUsers.get(position).getMail();
	     Toast.makeText(getActivity(), friendMail , Toast.LENGTH_SHORT).show();
	     
		 Intent i = new Intent(getActivity(), ConversationActivity.class);
	     i.putExtra(AppConsts.email_friend,friendMail);
	     startActivity(i);
*/	     
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
}