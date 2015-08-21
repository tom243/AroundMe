package com.aroundme;

import java.util.ArrayList;
import java.util.Date;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.adapter.ChatArrayAdapter;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ChatMessage;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.CustomNetworkImageView;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.aroundme.controller.ImagesController;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;
import com.google.api.client.util.DateTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Tomer and chen
 *
 * Chat between user and friend
 */
public class ConversationActivity extends ActionBarActivity implements IAppCallBack<Void>, SplashInterface{

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String myFriendMail;
    private boolean side = false;
    private Controller controller;
    private IDataAccess dao;
    private ArrayList<Message> historyMessages;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageLoader imageLoader;
    private CustomNetworkImageView thumbNail; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        controller = Controller.getInstance();
        dao = DAO.getInstance(AroundMeApp.getContext());
        // set the tool bar title to the friend name
        Intent intent = getIntent();
        myFriendMail = intent.getStringExtra(AppConsts.EMAIL_FRIEND);
        setTitle("");
        TextView title = (TextView) findViewById(R.id.friend_name_title);
        title.setText(controller.getUserNameByMail(myFriendMail));
        // set the tool bar icon of friend
        thumbNail = (CustomNetworkImageView) findViewById(R.id.profile_pic_chat_friend);
		thumbNail.setDefaultImageResId(R.drawable.user_default);
        imageLoader = ImagesController.getInstance().getImageLoader();
		String imageUrl = null;
		imageUrl = controller.getImageUrlByMail(myFriendMail);
		if (imageUrl != null) {
		   imageLoader.get(imageUrl, new ImageLoader.ImageListener() { // load images form cache
		        @Override
		        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
		        	Bitmap bitmap= response.getBitmap();
		        	if (bitmap != null) {
		        		imgBitmapCallback(bitmap);
		        	}
		        	
		        }

		        @Override
		        public void onErrorResponse(VolleyError error) {

		        }
		   });
		}
	
        buttonSend = (Button) findViewById(R.id.buttonSend);
        listView = (ListView) findViewById(R.id.listView1);
        chatArrayAdapter = new ChatArrayAdapter(AroundMeApp.getContext(), R.layout.conversation_singlemessage);
        listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                	if (controller.isOnline(AroundMeApp.getContext()))
                		return sendChatMessage();
                	else
                		Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (controller.isOnline(AroundMeApp.getContext()))
            		sendChatMessage();
            	else
            		Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
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
    
    /**
     * @param bitmap the image of the user
     * 
     * callBack when asking the image form cache
     */
    private void imgBitmapCallback(Bitmap bitmap) {
    	if (bitmap != null){
    		thumbNail.setLocalImageBitmap(bitmap);
    	}
    }
    
    /**
     *  initialize unread messages for conversation
     */
    public void initializeUnreadMessages() {
	    dao.open();
	    ConversationItem conv = dao.isConversationExist(controller.getCurrentUser().getMail(),myFriendMail);
		if (conv != null) {
			conv.setUnreadMess(0);
			dao.updateUnreadMessages(conv);
			Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
		    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(updateAdapterIntent);
		}
		dao.close();
    }
    
    /**
     * @param friendMail the friend mail
     * 
     * get all messages from DB for specific friend
     */
    public void getHistoryMessagesFromDB(String friendMail){
		dao.open();
		historyMessages = dao.getAllMessagesForFriend(controller.getCurrentUser().getMail(), friendMail);
		dao.close();
		if (!historyMessages.isEmpty()){
			for (Message message: historyMessages){
				if (message != null){
					boolean locationBased = false;
					if (message.getLocation() != null)
						locationBased = true;
				    
					if (message.getFrom().equals(myFriendMail)) {
			        	 side = true;
			        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased,
			        			 message.getTimestamp()));
			        }
					else{
						side=false;
						chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased,
								message.getTimestamp()));
					}
				}
			}
		}
    }
    
    /**
     *  receiver for getting the message from the user
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Long messageId = intent.getLongExtra("messageId",999);
	        dao.open();
	        Message message = dao.getMessageFromDB(messageId);
	        dao.close();
	        boolean locationBased = false;
	        if (message.getLocation() != null)
	        	locationBased = true;
	        if (message.getFrom().equals(myFriendMail)) {
	        	 side = true;
	        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased,
	        	 		message.getTimestamp()));
	        }
	        //  ... react to local broadcast message
	    }
	};

	/**
	 * receiver for getting the  pin message from the user
	 */
	private BroadcastReceiver mPinMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Long messageId = intent.getLongExtra("pinId",999);
	        dao.open();
	        Message message = dao.getMessageFromDB(messageId);
	        dao.close();
	        boolean locationBased = true;
	        if (message.getLocation() != null)
	        	locationBased = true;
	        if (message.getFrom().equals(myFriendMail)) {
	        	 side = true;
	        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased,
	        	 		message.getTimestamp()));
	        }
	        //  ... react to local broadcast message
	    }
	};
	
    /**
     * @return boolean
     * 
     * send new message to friend and return true or false
     */
    private boolean sendChatMessage(){
    	if (!chatText.getText().toString().isEmpty()) {
    		String messageContent = chatText.getText().toString();
    		controller.sendMessageToUser(messageContent,AppConsts.TYPE_SIMPLE_MSG,myFriendMail,null,this,this);
    		return true;
    	}
    	return false;
    }
    
    
    /**
     * callback when the message to the user sent asynchronously to the server
     **/
	@Override
	public void done(Void ret, Exception e) {
		if (e==null) {
			String messageContent = chatText.getText().toString();
			controller.buildMessage(messageContent, myFriendMail, false, null, AppConsts.TYPE_SIMPLE_MSG);
		    String text = chatText.getText().toString();
			side = false;
			DateTime currDate = new DateTime(new Date());
			chatArrayAdapter.add(new ChatMessage(side, text, false, currDate));
			chatText.setText("");
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		AroundMeApp.setChatOpen(true);
		AroundMeApp.setFriendWithOpenChat(myFriendMail);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chatMessage"));
		LocalBroadcastManager.getInstance(this).registerReceiver(mPinMessageReceiver, new IntentFilter("pinMessage"));
	}

	@Override
	protected void onStop() {
		super.onStop();
		AroundMeApp.setChatOpen(false);
		AroundMeApp.setFriendWithOpenChat("");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mPinMessageReceiver);
	}

	@Override
	public void visible(Exception e) {
		if (e == null) {
			progressBar.setVisibility(View.VISIBLE);
			// change the send button to be not clickable 
    		buttonSend.setClickable(false);
		}
	}

	@Override
	public void unvisible(Exception e) {
		if (e == null) {
			progressBar.setVisibility(View.INVISIBLE);
			// change the send button to be not clickable 
    		buttonSend.setClickable(true);
		}
	}

	@Override
	protected void onPause() {	
		initializeUnreadMessages();
		super.onPause();
	}
	
}