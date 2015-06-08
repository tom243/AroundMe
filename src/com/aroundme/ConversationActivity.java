package com.aroundme;

import java.util.ArrayList;
import java.util.Date;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.adapter.ChatArrayAdapter;
import com.aroundme.common.AppConsts;
import com.aroundme.common.ChatMessage;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;
import com.google.api.client.util.DateTime;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ConversationActivity extends Activity implements IAppCallBack<Void>, SplashInterface{

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String myFriendMail;
    private boolean side = false;
    private Controller controller;
    private IDataAccess dao;
    private  ArrayList<Message> historyMessages;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        Intent intent = getIntent();
        controller = Controller.getInstance();
        dao = DAO.getInstance(getApplicationContext());
        myFriendMail = intent.getStringExtra(AppConsts.email_friend);
        buttonSend = (Button) findViewById(R.id.buttonSend);

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.conversation_singlemessage);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                	if (isOnline())
                		return sendChatMessage();
                	else
                		Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (isOnline())
            		sendChatMessage();
            	else
            		Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        getHistoryMessagesFromDB(myFriendMail);
        initializeUnreadMessages();
    }
    
    public void initializeUnreadMessages() {
	    dao.open();
	    ConversationItem conv = dao.isConversationExist(controller.getCurrentUser().getMail(),myFriendMail);
		if (conv != null) {
			conv.setUnreadMess(0);
			dao.updateUnreadMessages(conv);
			Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
		    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateAdapterIntent);
		}
		dao.close();
    }
    
    
    public void getHistoryMessagesFromDB(String friendMail){
		dao.open();
		historyMessages = dao.getAllMessagesForFriend(controller.getCurrentUser().getMail(), friendMail);
		dao.close();
		if (!historyMessages.isEmpty()){
			for (Message message: historyMessages){
				if (message != null){
					if (message.getFrom().equals(myFriendMail)) {
			        	 side = true;
			        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet()));
			        }
					else{
						side=false;
						chatArrayAdapter.add(new ChatMessage(side, message.getContnet()));
					}
				}
			}
		}
		
    }
    
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Long messageId = intent.getLongExtra("messageId",999);
	        dao.open();
	        Message message = dao.getMessageFromDB(messageId);
	        dao.close();
	        if (message.getFrom().equals(myFriendMail)) {
	        	 side = true;
	        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet()));
	        }
	        //Toast.makeText(getApplicationContext(), message.getContnet(), Toast.LENGTH_SHORT).show();
	        //  ... react to local broadcast message
	    }
	};

    private boolean sendChatMessage(){
    	if (!chatText.getText().toString().isEmpty()) {
    		String messageContent = chatText.getText().toString();
    		controller.sendMessageToUser(messageContent,myFriendMail,this,this);
			Message message = new Message();;
			message.setContnet(messageContent);
			message.setFrom(controller.getCurrentUser().getMail());
			message.setTo(myFriendMail);
			message.setTimestamp(new DateTime(new Date()));
	   		Long messageId = addMessageToDB(message);
    		updateConversationTable(message, messageId);
			// Here I need to send broadcast to tell the receiver to refresh the adapter for open conversation list
			Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
		    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateAdapterIntent);
    		return true;
    	}
    	return false;
    }
    
	@Override
	public void done(Void ret, Exception e) {
		if (e==null) {
			String text = chatText.getText().toString();
			side = false;
			chatArrayAdapter.add(new ChatMessage(side, text));
			chatText.setText("");
		}
	}
	
	private Long addMessageToDB(Message message){
		dao.open();
		Long id = dao.addToMessagesTable(message);
		dao.close();
		return id;
	}
	
	private void updateConversationTable(Message message, Long messageId){
		dao.open();
		ConversationItem conv = dao.isConversationExist(message.getFrom(),message.getTo());
		if (conv != null) {
			System.out.println("Conversation  exist");
			// I just send message so I saw for sure the last messages that I've got.
			//conv.setUnreadMess(0); // no actually need to update
			dao.updateOpenConversation(conv, messageId);
		}
		else {
			System.out.println("Conversation not exist");
			dao.addToConversationsTable(message.getTo(), message.getFrom(), messageId);
		}
		dao.close();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chatMessage"));
	}

	@Override
	protected void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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

	@Override
	protected void onPause() {		 // on pause ??
		initializeUnreadMessages();
		super.onPause();
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	

}