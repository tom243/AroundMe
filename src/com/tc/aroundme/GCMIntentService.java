package com.tc.aroundme;

import java.util.Stack;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.AroundMeApp;
import com.tc.aroundme.controller.Controller;
import com.tc.aroundme.controller.GeoController;
import com.tc.aroundme.controller.NotificationsController;
import com.tc.aroundme.data.DAO;
import com.tc.aroundme.data.IDataAccess;

public class GCMIntentService extends IntentService
{
	private static Stack<String> msgStack;
	private IDataAccess dao;
	private Controller controller;
	private GeoController geoController;
	private NotificationsController notificationsController;

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
		controller = Controller.getInstance();
		geoController = GeoController.getInstance(AroundMeApp.getContext());
		notificationsController = new NotificationsController();
		dao = DAO.getInstance(AroundMeApp.getContext());
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
            } else if (GoogleCloudMessaging.
                MESSAGE_TYPE_DELETED.equals(messageType)) {
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                Log.i("GCMIntentService", "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                Log.i("GCMIntentService", "Received: " + extras.toString());
                String mId = intent.getStringExtra("newMessage");
                if(mId!=null)
                {
                	try {
                    	Aroundmeapi api = EndpointApiCreator.getApi(Aroundmeapi.class);
						Message m = api.getMessage(Long.parseLong(mId)).execute();
						// checking which message we got
						JSONObject jObject = new JSONObject(m.getContnet());
						String content = jObject.getString("content");
						String type = jObject.getString("type");
						System.out.println(type);
						// set the content in the message
						m.setContnet(content);
						
						if (type.equals(AppConsts.TYPE_GEO_MSG)) { 
							System.out.println("GEO MESSAGE WAS RECEIVED!");
							geoController.createGeofence(m);
							Intent geoIntent = new Intent("geoMessage");
							geoIntent.putExtra("geoId", m.getId());
						    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(geoIntent);
						}
						if (type.equals(AppConsts.TYPE_PIN_MSG)) { 
							System.out.println("PIN MESSAGE WAS RECEIVED!");
							// insert to messages table
							Long messageId = controller.addMessageToDB(m,AppConsts.TYPE_PIN_MSG);
							controller.updateConversationTable(m.getTo(), m.getFrom(), m.getId(),true, false, false);
							notificationsController.createNotification(m, AppConsts.TYPE_PIN_MSG);
							Intent pinIntent = new Intent("pinMessage");
							pinIntent.putExtra("pinId", m.getId());
						    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(pinIntent);
						}
						if (type.equals(AppConsts.TYPE_SIMPLE_MSG)) {
							if (AroundMeApp.isChatOpen() && 
									AroundMeApp.getFriendWithOpenChat().equals(m.getFrom())) {
								System.out.println("DONT NEED TO SEND NOTIFICATION");
							} else
								notificationsController.createNotification(m, AppConsts.TYPE_SIMPLE_MSG);

							// insert to messages & conversation table
							Long messageId = controller.addMessageToDB(m,AppConsts.TYPE_SIMPLE_MSG);
							controller.updateConversationTable(m.getTo(), m.getFrom(), m.getId(),true,false,false);
							//send intent with the id from the insert query
							Intent chatIntent = new Intent("chatMessage");
							chatIntent.putExtra("messageId", m.getId());
						    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(chatIntent);
						}
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