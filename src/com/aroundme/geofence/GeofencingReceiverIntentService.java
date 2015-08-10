package com.aroundme.geofence;

import java.sql.Timestamp;
import java.util.Date;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;
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

public class GeofencingReceiverIntentService extends ReceiveGeofenceTransitionBaseIntentService {

	private NotificationManager notificationManager;
	private GeoController geoController;
	private Controller controller;
	private IDataAccess dao;

	public GeofencingReceiverIntentService() {
		this.geoController = GeoController.getInstance(AroundMeApp.getContext());
		this.dao = DAO.getInstance(AroundMeApp.getContext());
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
		message.setId(Long.parseLong(messageGeo.getId()));
		message.setContnet(messageGeo.getContent());
		message.setFrom(messageGeo.getFrom());
		message.setTo(messageGeo.getTo());
		message.setTimestamp(new DateTime(messageGeo.getDate()));
		GeoPt geoPt = new GeoPt();
		geoPt.setLatitude((float)messageGeo.getLatitude());
		geoPt.setLongitude((float)messageGeo.getLongitude());
		message.setLocation(geoPt);
		message.setReadRadius((int)messageGeo.getRadius());
		Long messageId = controller.addMessageToDB(message);
		controller.updateConversationTable(message.getTo(), message.getFrom(), messageId,true,true,false);
		//Intent updateIntent = new Intent("updateOpenCoversationsAdapter");
	    //LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(updateIntent);
		// create notification
		// ...
		
		System.out.println("strings[0]" + strings[0]);
		System.out.println("message.getId()" + message.getId());
		// send broadcast to remove geofence
		Intent intent = new Intent("removeGeofence");
		intent.putExtra("geoId",  message.getId());
	    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(intent);
	}

	@Override
	protected void onExitedGeofences(String[] strings) {
		// cancel geofence strings[0]
		//geoController.cancelGeofence(strings[0]);
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
	
/*	private void CreateNotification(String text, int taskId) {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		MessageGeofenceStore mGeofenceStorage = 	new MessageGeofenceStore(this); 
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
