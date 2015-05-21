package com.aroundme;

import java.io.IOException;
import java.util.ResourceBundle.Control;
import java.util.Stack;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.controller.Controller;
import com.aroundme.data.DAO;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	//private final Deviceinfoendpoint endpoint;
	private NotificationManager mNotificationManager;

	private static int numMessages =0;
	private static Stack<String> msgStack;

	private Boolean alreadySignedIn;
	
	private DAO dao;
	private Controller controller;

	/*
	 * TODO: Set this to a valid project number. See
	 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
	 * information.
	 */

	public static final String PROJECT_NUMBER = "1047488186224";
	private static SharedPreferences prefs;

	public GCMIntentService()
	{
		super("GCMIntentService");
		msgStack = new Stack<String>();
		dao = DAO.getInstance(getApplicationContext());
		controller = Controller.getInstance();
	}
	
	@Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
            	//onMessage(this,intent);
            	sendNotification("Received: " + extras.toString());
                Log.i("GCMIntentService", "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i("GCMIntentService", "Received: " + extras.toString());
                String mId = intent.getStringExtra("newMessage");
                if(mId!=null)
                {
                	try {
                    	Aroundmeapi api = EndpointApiCreator.getApi(Aroundmeapi.class);
						Message m = api.getMessage(Long.parseLong(mId)).execute();
						sendNotification(m.getContnet());
						// insert to conversation table
						dao.open();
						if (dao.isConversationExist(controller.getCurrentUser().getMail(), m.getFrom())) {
							// insert message to messages table
							
							// update last message and counter unread messages to conversations table
							
						}
						else {
							// insert message to messages table
							
							// insert new conversation to conversations table
							
						}
						dao.close();
						
						
						// insert message to the db with DAO object
						
						//send intent with the id from the insert query
						Intent chatIntent = new Intent("chatMessage");
						chatIntent.putExtra("message", m.getContnet());
						chatIntent.putExtra("from", m.getFrom());
						chatIntent.putExtra("time", m.getTimestamp());
					    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(chatIntent);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        Log.i("GCM-iNTENT","type: "+messageType);
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
	
		
	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GCMActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("GCM Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

	private String getWebSampleUrl(String endpointUrl)
	{
		// Not the most elegant solution; we'll improve this in the future
		if (CloudEndpointUtils.LOCAL_ANDROID_RUN)
		{
			return CloudEndpointUtils.LOCAL_APP_ENGINE_SERVER_URL
					+ "index.html";
		}
		return endpointUrl.replace("/_ah/api/", "/index.html");
	}
	
	
}