package com.aroundme;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.IAppCallBack;
import com.aroundme.controller.Controller;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends ListActivity implements IAppCallBack<List<UserAroundMe>> {
	
    private static final String TAG = "ChatActivity";
    TextView content;
    Button refreshButton;
    private Intent intent;
    GoogleCloudMessaging gcm;
    
    GoogleApiClient mGoogleApiClient;
	Aroundmeapi api;
	Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        content = (TextView)findViewById(R.id.output);
        content.setText("Select user to chat:");
        refreshButton = (Button)findViewById(R.id.refreshButton);
        controller = Controller.getInstance();
        //intent = new Intent(this, GCMNotificationIntentService.class);
        //registerReceiver(broadcastReceiver, new IntentFilter("com.javapapers.android.gcm.chat.userlist"));
        //gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chatMessage"));
        controller.getAllUsers(this);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // get user list
                /*Bundle dataBundle = new Bundle();
                dataBundle.putString("ACTION", "USERLIST");
                controller.sendMessageToUser();
                */
                //controller.getAllUsers(ChatActivity.class);
            }
        });

    }
/*
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getStringExtra("USERLIST"));
            updateUI(intent.getStringExtra("USERLIST"));
        }
    };
*/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        String  message = intent.getStringExtra("message");
	        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	        //  ... react to local broadcast message
	    }
	};

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        String  itemValue    = (String) l.getItemAtPosition(position);

        content.setText("User selected: " +itemValue);


        Intent i = new Intent(getApplicationContext(), ConversationActivity.class);
        i.putExtra("TOUSER",itemValue);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void done(List<UserAroundMe> ret, Exception e) {
		if (e == null) {
			String[] userListArr = new String[ret.size()];
	        for (int i=0; i< ret.size(); i++) {
	        	userListArr[i] = ret.get(i).getDisplayName();
	        }
			ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userListArr);
		    setListAdapter(adapter);
		}
		else 
			; // error
	}
	
}