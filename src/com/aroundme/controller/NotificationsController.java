package com.aroundme.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

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
	
    public void createNotification(Message message) {
    	
    	mNotificationManager = (NotificationManager)
                AroundMeApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
       
    	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(
            AroundMeApp.getContext(),
            message.getId().intValue(),
            new Intent(AroundMeApp.getContext(), GCMActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);
     
        NotificationCompat.Builder builder = new NotificationCompat.Builder(AroundMeApp.getContext())
            // Set Icon
            .setSmallIcon(R.drawable.logo)
             // Set Ticker Message
            .setTicker(message.getContnet())
            // Dismiss Notification
            .setAutoCancel(true)
            //
            .setSound(alarmSound)
            // Set PendingIntent into Notification
            .setContentIntent(pIntent);
     
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // build a complex notification, with buttons and such
            //
            builder = builder.setContent(getComplexNotificationView(message));
            		//mPrefs.getString(message.getFrom(), "New message"),message.getContnet()));
        } else {
            // Build a simpler notification, without buttons
            //
            builder = builder.setContentTitle(mPrefs.getString(message.getFrom(), "New message"))
                .setContentText(message.getContnet())
                .setSmallIcon(R.drawable.logo);
        }
        //return builder;
        mNotificationManager.notify(message.getId().intValue(), builder.build());
    }
    
    private RemoteViews getComplexNotificationView(Message message) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
            AroundMeApp.getContext().getPackageName(),
            R.layout.notification
        );
     
        // Locate and set the Image into customnotificationtext.xml ImageViews
        notificationView.setImageViewResource(
            R.id.imagenotileft, 
            R.drawable.logo);
     
        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.title, mPrefs.getString(message.getFrom(), "New message"));
        notificationView.setTextViewText(R.id.text, message.getContnet());
        notificationView.setTextViewText(R.id.date, controller.dateToDateString(message.getTimestamp().getValue()));
        notificationView.setTextViewText(R.id.time, controller.dateToTimeString(message.getTimestamp().getValue()));
     
        // checking if it is a geo message to show or hide the geo icon
        if (message.getLocation() != null)
        	notificationView.setViewVisibility(R.id.geoMessageIcon, View.VISIBLE);
        else
        	notificationView.setViewVisibility(R.id.geoMessageIcon, View.GONE);
        
        return notificationView;
    }
	
/*	
	// Put the message into a notification and post it.
    public void createNotification(Message message) {
    	String isGeo;
    	if (message.getLocation() == null)
    		isGeo = "";
    	else
    		isGeo = "GEO ";
    	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	mNotificationManager = (NotificationManager)
                AroundMeApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(AroundMeApp.getContext(), 0,
                new Intent(AroundMeApp.getContext(), GCMActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        
        
        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(AroundMeApp.getContext())
        	.setSmallIcon(R.drawable.logo)
        	.setContentTitle(isGeo+mPrefs.getString(message.getFrom(), "New message"))
        	.setStyle(new NotificationCompat.BigTextStyle()
        					.bigText(message.getContnet())
        			  )
        	.setAutoCancel(true)
        	.setSound(alarmSound)
        	.setContentText(message.getContnet());
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(message.getId().intValue(), mBuilder.build());
    }*/
	
}
