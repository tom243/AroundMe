package com.aroundme;

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
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	//private final Deviceinfoendpoint endpoint;
	private NotificationManager mNotificationManager;

	private static int numMessages =0;
	private static Stack<String> msgStack;

	private Boolean alreadySignedIn;

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
/*		endpoint = EndpointApiCreator
				.<Deviceinfoendpoint> getApi(Deviceinfoendpoint.class);
		wuep = EndpointApiCreator
				.<Wannameetuserendpoint> getApi(Wannameetuserendpoint.class);
		mmep = EndpointApiCreator
				.<MeetingMatchingEndpoint> getApi(MeetingMatchingEndpoint.class);
*/		msgStack = new Stack<String>();

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