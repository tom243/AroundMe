package com.aroundme;

import java.io.IOException;
import java.net.URLEncoder;

import com.aroundme.common.AroundMeApp;
import com.aroundme.controller.Controller;
import com.aroundme.deviceinfoendpoint.Deviceinfoendpoint;
import com.aroundme.deviceinfoendpoint.model.DeviceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * The Main Activity.
 */
public class GCMActivity extends Activity {
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	//private ProgressBar progressBar;
	//This is the project ID related to 'AroundMe'.
	String SENDER_ID = "1047488186224";
	private SharedPreferences prefs;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final static String TAG= "MainActivity";
	public static final String EXTRA_MESSAGE = "message";
	private Controller controller ;
   
    GoogleCloudMessaging gcm;
    String regid;
    Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gcm);
	//	progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		controller = Controller.getInstance();
		//You must initialize the EndpointApiCreator in order to use it.
		EndpointApiCreator.initialize(null);
		
		prefs = this.getSharedPreferences("com.aroundMe", Context.MODE_PRIVATE);
		context = AroundMeApp.getContext();
		
      // Check device for Play Services APK. If check succeeds, proceed with
      //  GCM registration.
      if (checkPlayServices()) {
          gcm = GoogleCloudMessaging.getInstance(this);
          regid = controller.getRegistrationId(context); // FROM prefs
          if (regid.isEmpty()) {
        		if (controller.isOnline(context))
        			registerInBackground();
        		else
        			Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT).show();
          } else
        	  startLogin();
      } else {
          Log.i(TAG, "No valid Google Play Services APK found.");
      }
	
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}	
	
	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() { 
		RegAsyncTask async = new RegAsyncTask();
		async.execute();
	}
	
	private class RegAsyncTask extends AsyncTask<Void, Void, String>{
		
		@Override
		protected void onPreExecute() {
		//	progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
        @Override
        protected String doInBackground(Void... params) {
            Log.i(TAG,"doinBackgroud!!!!!!!");
        	String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                System.out.println("Main acitviyy "+ regid);
                msg = "Device registered, registration ID=" + regid;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                sendRegistrationIdToBackend();

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the registration ID - no need to register again.
                controller.storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
        	Log.i(TAG,msg);
        	//progressBar.setVisibility(View.INVISIBLE);
        	startLogin();
        }
	}
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		try {
			Deviceinfoendpoint endpoint = EndpointApiCreator
					.getApi(Deviceinfoendpoint.class);
			DeviceInfo existingInfo = endpoint.getDeviceInfo(regid).execute();

			boolean alreadyRegisteredWithEndpointServer = false;
			if (existingInfo != null
					&& regid.equals(existingInfo.getDeviceRegistrationID())) {
				alreadyRegisteredWithEndpointServer = true;
			}

			if (!alreadyRegisteredWithEndpointServer) {
				/*
				 * We are not registered as yet. Send an endpoint message
				 * containing the GCM registration id and some of the device's
				 * product information over to the backend. Then, we'll be
				 * registered.
				 */
				DeviceInfo deviceInfo = new DeviceInfo();
				endpoint.insertDeviceInfo(
						deviceInfo
								.setDeviceRegistrationID(regid)
								.setTimestamp(System.currentTimeMillis())
								.setDeviceInformation(
										URLEncoder
												.encode(android.os.Build.MANUFACTURER
														+ " "
														+ android.os.Build.PRODUCT,
														"UTF-8"))).execute();
			}
		} catch (Exception e) {

		}

	}
	
	private void startLogin() {
		// Move to SignInActivity
		Intent intent = new Intent(this, SignInActivity.class);
		//intent.putExtra("regid",regid);
		startActivity(intent);
		/* Since this is just a wrapper to start the main activity, finish it after launching SignInActivity */
		finish();  
	}
}
