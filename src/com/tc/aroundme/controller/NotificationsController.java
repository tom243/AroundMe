package com.tc.aroundme.controller;

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
import android.widget.RemoteViews;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.tc.aroundme.R;
import com.tc.aroundme.GCMActivity;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.AroundMeApp;

/**
 * @author Tomer and chen
 *
 *	controller for the notifications
 */
public class NotificationsController {

	private NotificationManager mNotificationManager;
	private Controller controller;
	private final SharedPreferences mPrefs;

	/**
	 *  constructor for NotificationsController
	 */
	public NotificationsController() {
		controller = Controller.getInstance();
		mPrefs = AroundMeApp.getContext().getSharedPreferences(AppConsts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	}
	
    /**
     * @param message details of the message
     * @param msgType type of the message
     * 
     * create notification for message
     */
    public void createNotification(Message message, String msgType) {
    	
    	mNotificationManager = (NotificationManager)
                AroundMeApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
       
    	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); 
        PendingIntent pIntent = PendingIntent.getActivity( // Open NotificationView.java Activity
            AroundMeApp.getContext(),
            message.getId().intValue(),
            new Intent(AroundMeApp.getContext(), GCMActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);
     
        NotificationCompat.Builder builder = new NotificationCompat.Builder(AroundMeApp.getContext())
           
            .setSmallIcon(R.drawable.logo)  	// Set Icon
            .setTicker(message.getContnet())    // Set Ticker Message
            .setAutoCancel(true)				// Dismiss Notification
            .setSound(alarmSound)				// set sound 
            .setContentIntent(pIntent);			// Set PendingIntent into Notification
     
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // build a complex notification, with buttons and such
            builder = builder.setContent(getComplexNotificationView(message, msgType));
        } else {
            builder = builder.setContentTitle(mPrefs.getString(message.getFrom(), "New message")) // Build a simpler notification, without buttons
                .setContentText(message.getContnet())
                .setSmallIcon(R.drawable.logo);
        }
        mNotificationManager.notify(message.getId().intValue(), builder.build());
    }
    
    private RemoteViews getComplexNotificationView(Message message, String msgType) { // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
            AroundMeApp.getContext().getPackageName(),
            R.layout.notification
        );
        
        notificationView.setImageViewResource( // Locate and set the Image into customnotificationtext.xml ImageViews
            R.id.imagenotileft, 
            R.drawable.logo);
     
        // Locate and set the Text into customnotificationtext.xml TextViews
        String title;
        String from = mPrefs.getString(message.getFrom(), "New message");
        if (msgType.endsWith(AppConsts.TYPE_SIMPLE_MSG))
        	title = from;
        else 
        	title = msgType + " MESSAGE FROM: " + from;
        notificationView.setTextViewText(R.id.title, title);
        
        String dotMessage;
        if (message.getContnet().length() >= 18) {
      	  dotMessage = message.getContnet().substring(0, 18)+ "...";
        } else 
      	  dotMessage = message.getContnet();
        notificationView.setTextViewText(R.id.text, dotMessage);
        notificationView.setTextViewText(R.id.date, controller.dateToDateString(message.getTimestamp().getValue()));
        notificationView.setTextViewText(R.id.time, controller.dateToTimeString(message.getTimestamp().getValue()));
     
        
        if (message.getLocation() != null) { // checking if it is a GEO/PIN message to show or hide the GEO/PIN icon
        	notificationView.setViewVisibility(R.id.geoMessageIcon, View.VISIBLE);
        	if (msgType.equals(AppConsts.TYPE_PIN_MSG))
        		notificationView.setImageViewResource(R.id.geoMessageIcon, R.drawable.pin_icon);
        	else
        		notificationView.setImageViewResource(R.id.geoMessageIcon, R.drawable.geo_msg);
        } else
        	notificationView.setViewVisibility(R.id.geoMessageIcon, View.GONE);
        
        return notificationView;
    }
	
}
