package com.aroundme;

import java.util.ArrayList;
import java.util.Date;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.adapter.ChatArrayAdapter;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ChatMessage;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.aroundme.controller.ImagesController;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.util.DateTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
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

public class ConversationActivity extends ActionBarActivity implements IAppCallBack<Void>, SplashInterface{

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String myFriendMail;
    private boolean side = false;
    private Controller controller;
    private IDataAccess dao;
    private  ArrayList<Message> historyMessages;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageLoader imageLoader = ImagesController.getInstance().getImageLoader();
    private NetworkImageView thumbNail; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        Intent intent = getIntent();
        controller = Controller.getInstance();
        dao = DAO.getInstance(AroundMeApp.getContext());
        // set the tool bar title to the friend name
        myFriendMail = intent.getStringExtra(AppConsts.email_friend);
        //setTitle(controller.getUserNameByMail(myFriendMail));
        setTitle("");
        TextView title = (TextView) findViewById(R.id.friend_name_title);
        title.setText(controller.getUserNameByMail(myFriendMail));
        // set the tool bar icon of friend
        thumbNail = (NetworkImageView) findViewById(R.id.profile_pic_chat_friend);
		thumbNail.setDefaultImageResId(R.drawable.user_default);
        if (imageLoader == null)
            imageLoader = ImagesController.getInstance().getImageLoader();
		String imageUrl = null;
		imageUrl = controller.getImageUrlByMail(myFriendMail);
		if (imageUrl != null)
			thumbNail.setImageUrl(imageUrl, imageLoader);
		//imageLoader.getImageListener(imageView, R.drawable.user_default, R.drawable.user_default));
		/*Bitmap bm_original = imageLoader.get(myFriendMail, new ImageLoader.ImageListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				
			}
			@Override
			public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
				Bitmap bitmap = response.getBitmap();		
				imgBitmapCallback(bitmap);
			}
		}).getBitmap();
	*/
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
    
    public void imgBitmapCallback(Bitmap bitmap) {
    	Bitmap bm = getRoundedShape(bitmap);
    	if (bm != null){
    		thumbNail.setImageBitmap(bm);
    		System.out.println("$$$$$$");
    	}
    }
    
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
			        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased));
			        }
					else{
						side=false;
						chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased));
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
	        boolean locationBased = false;
	        if (message.getLocation() != null)
	        	locationBased = true;
	        if (message.getFrom().equals(myFriendMail)) {
	        	 side = true;
	        	 chatArrayAdapter.add(new ChatMessage(side, message.getContnet(),locationBased));
	        }
	        //  ... react to local broadcast message
	    }
	};

    private boolean sendChatMessage(){
    	if (!chatText.getText().toString().isEmpty()) {
    		String messageContent = chatText.getText().toString();
    		controller.sendMessageToUser(messageContent,myFriendMail,null,this,this);
    		return true;
    	}
    	return false;
    }
    
	@Override
	public void done(Void ret, Exception e) {
		if (e==null) {
			String messageContent = chatText.getText().toString();
			controller.buildMessage(messageContent, myFriendMail, false, null);
	   		//Intent updateIntent = new Intent("updateOpenCoversationsAdapter");
		    //LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(updateIntent);
		    String text = chatText.getText().toString();
			side = false;
			chatArrayAdapter.add(new ChatMessage(side, text,false));
			chatText.setText("");
		}
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
	protected void onPause() {		 // on pause ??
		initializeUnreadMessages();
		super.onPause();
	}
	
	public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
	    int targetWidth = 50;
	    int targetHeight = 50;
	    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                        targetHeight,Bitmap.Config.ARGB_8888);

	    Canvas canvas = new Canvas(targetBitmap);
	    Path path = new Path();
	    path.addCircle(((float) targetWidth - 1) / 2,
	        ((float) targetHeight - 1) / 2,
	        (Math.min(((float) targetWidth), 
	        ((float) targetHeight)) / 2),
	        Path.Direction.CCW);

	    canvas.clipPath(path);
	    Bitmap sourceBitmap = scaleBitmapImage;
	    canvas.drawBitmap(sourceBitmap, 
	        new Rect(0, 0, sourceBitmap.getWidth(),
	        sourceBitmap.getHeight()), 
	        new Rect(0, 0, targetWidth, targetHeight), null);
	    return targetBitmap;
	}

}