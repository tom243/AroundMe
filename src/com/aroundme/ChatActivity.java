package com.aroundme;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
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

public class ChatActivity extends Activity {

	GoogleApiClient mGoogleApiClient;
	Aroundmeapi api;
	Controller controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		controller = Controller.getInstance();
		controller.sendMessageToUser();
		//TextView tv = (TextView)findViewById(R.id.chatMessage);
		/*try {
			api = EndpointApiCreator.getApi(Aroundmeapi.class);
			Message message = new Message();
			message.setContnet("Hi Im Chen! :)");
			api.sendMessage(message).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chatMessage"));
	}
	
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
}
