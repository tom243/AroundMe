package com.aroundme.geofence;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.AppConsts;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.MessageGeofence;
import com.aroundme.controller.Controller;
import com.aroundme.controller.GeoController;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;
import com.google.api.client.util.DateTime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GeofencingReceiverIntentService extends
		ReceiveGeofenceTransitionBaseIntentService {

	private NotificationManager notificationManager;
	private GeoController geoController;
	private Controller controller;
	private IDataAccess dao;

	public GeofencingReceiverIntentService() {
		this.geoController = GeoController.getInstance(getApplicationContext());
		this.dao = DAO.getInstance(getApplicationContext());
		this.controller = Controller.getInstance();
	}
	
	@Override
	protected void onEnteredGeofences(String[] strings) {
		Log.d(GeofencingReceiverIntentService.class.getName(), "onEnter");
		//int geoId = Integer.parseInt(strings[0]);
		Log.d(GeofencingReceiverIntentService.class.getName(), "The id message is: "+strings[0]);
		//CreateNotification("Geofence was received",messageId);
		
		// add the message to db
		MessageGeofence messageGeo = geoController.getmGeofenceStorage().getGeofence(strings[0]);
		Message message = new Message();
		message.setContnet(messageGeo.getContent());
		message.setFrom(messageGeo.getFrom());
		message.setTo(messageGeo.getTo());
		message.setTimestamp(new DateTime(messageGeo.getDate()));
		GeoPt geoPt = new GeoPt();
		geoPt.setLatitude((float)messageGeo.getLatitude());
		geoPt.setLongitude((float)messageGeo.getLongitude());
		message.setLocation(geoPt);
		message.setReadRadius((int)messageGeo.getRadius());
		Long messageId = addMessageToDB(message);
		updateConversationTable(message, messageId);
		// send broadcast to tell the receiver to refresh the adapter for open conversation list
		Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
	    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateAdapterIntent);
	}

	@Override
	protected void onExitedGeofences(String[] strings) {
		// cancel geofence strings[0]
		geoController.cancelGeofence(strings[0]);
		Log.d(GeofencingReceiverIntentService.class.getName(), "onExit");
	}

	@Override
	protected void onError(int i) {
		Log.e(GeofencingReceiverIntentService.class.getName(), "Error: " + i);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private Long addMessageToDB(Message message){
		dao.open();
		Long id = dao.addToMessagesTable(message);
		dao.close();
		return id;
	}
	
	public void updateConversationTable(Message message, Long messageId){
		dao.open();
		ConversationItem conv = dao.isConversationExist(controller.getCurrentUser().getMail(), message.getFrom());
		if (conv != null) {
			System.out.println("Conversation  exist");
			conv.setUnreadMess(conv.getUnreadMess()+1);	// ***
			dao.updateOpenConversation(conv, messageId); // update row in data-base
		}
		else {
			System.out.println("Conversation not exist");
			dao.addToConversationsTable(message.getFrom(), message.getTo(), messageId);
		}
		dao.close();
		Intent chatIntent = new Intent("updateOpenCoversationsAdapter");
	    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(chatIntent);
	}
	
	/*private void CreateNotification(String text, int taskId) {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		SimpleGeofenceStore mGeofenceStorage = 	new SimpleGeofenceStore(this); 
		String strId = Integer.toString(taskId);
		String desc = mGeofenceStorage.getmPrefs().getString(mGeofenceStorage.getGeofenceFieldKey(
				strId, AppConsts.KEY_DESCRIPTION),
         		AppConsts.INVALID_STRING_VALUE);
		
		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n = new Notification.Builder(this)
				.setContentTitle(text)
				.setContentText(desc)
				.setSmallIcon(R.drawable.logo_do_it_simple).setContentIntent(pIntent)
				.setAutoCancel(true).build();
		notificationManager.notify("GEO",taskId, n);
	}*/
	
}
