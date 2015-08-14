package com.aroundme.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.GCMActivity;
import com.aroundme.R;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;

public class NotificationsController {

	private NotificationManager mNotificationManager;
	private Controller controller;
	private final SharedPreferences mPrefs;
	
	public NotificationsController() {
		controller = Controller.getInstance();
		mPrefs = AroundMeApp.getContext().getSharedPreferences(AppConsts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	}
	
	// Put the message into a notification and post it.
    public void createNotification(Message message) {
    	String isGeo;
    	if (message.getLocation() == null)
    		isGeo = "";
    	else
    		isGeo = "GEO";
        mNotificationManager = (NotificationManager)
                AroundMeApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(AroundMeApp.getContext(), 0,
                new Intent(AroundMeApp.getContext(), GCMActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(AroundMeApp.getContext())
        .setSmallIcon(R.drawable.icon_logo_aroundme)
        .setContentTitle(isGeo+"  "+mPrefs.getString(message.getFrom(), "New message"))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(message.getContnet()))
        .setContentText(message.getContnet());
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(message.getId().intValue(), mBuilder.build());
    }
    
    //.setContentTitle(isGeo+"  "+controller.getUserNameByMail(message.getFrom()))
}
