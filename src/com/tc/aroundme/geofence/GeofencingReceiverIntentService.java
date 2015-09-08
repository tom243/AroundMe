package com.tc.aroundme.geofence;


import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.google.api.client.util.DateTime;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.AroundMeApp;
import com.tc.aroundme.common.MessageGeofence;
import com.tc.aroundme.controller.Controller;
import com.tc.aroundme.controller.GeoController;
import com.tc.aroundme.controller.NotificationsController;
import com.tc.aroundme.data.DAO;
import com.tc.aroundme.data.IDataAccess;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GeofencingReceiverIntentService extends ReceiveGeofenceTransitionBaseIntentService {

	private GeoController geoController;
	private Controller controller;
	private NotificationsController notificationsController;
	private IDataAccess dao;

	public GeofencingReceiverIntentService() {
		this.geoController = GeoController.getInstance(AroundMeApp.getContext());
		this.dao = DAO.getInstance(AroundMeApp.getContext());
		this.controller = Controller.getInstance();
		this.notificationsController = new NotificationsController();
	}
	
	@Override
	protected void onEnteredGeofences(String[] strings) {
		Log.d(GeofencingReceiverIntentService.class.getName(), "onEnter");
		Log.d(GeofencingReceiverIntentService.class.getName(), "The id message is: "+strings[0]);
		
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
		Long messageId = controller.addMessageToDB(message,AppConsts.TYPE_GEO_MSG);
		controller.updateConversationTable(message.getTo(), message.getFrom(), messageId,true,true,false);
		Intent chatIntent = new Intent("chatMessage");
		chatIntent.putExtra("messageId", messageId);
	    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(chatIntent);
		// create notification
		notificationsController.createNotification(message, AppConsts.TYPE_GEO_MSG);
		System.out.println("strings[0]" + strings[0]);
		System.out.println("message.getId()" + message.getId());
		// send broadcast to remove geofence
		Intent intent = new Intent("removeGeofence");
		intent.putExtra("geoId",  message.getId());
	    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(intent);
	}

	@Override
	protected void onExitedGeofences(String[] strings) {
		Log.d(GeofencingReceiverIntentService.class.getName(), "onExit");
	}

	@Override
	protected void onError(int i) {
		Log.e(GeofencingReceiverIntentService.class.getName(), "Error: " + i);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
}
