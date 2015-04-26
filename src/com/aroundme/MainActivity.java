package com.aroundme;

import java.io.IOException;
import java.net.URLEncoder;

import com.aroundme.deviceinfoendpoint.Deviceinfoendpoint;
import com.aroundme.deviceinfoendpoint.model.DeviceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewDebug.IntToString;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * The Main Activity.
 */
public class MainActivity extends Activity {
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	//This is the project ID related to 'AroundMe'.
	String SENDER_ID = "1047488186224";
	private SharedPreferences prefs;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final static String TAG= "MainActivity";
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    GoogleCloudMessaging gcm;
    String regid;
    Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//You must initialize the EndpointApiCreator in order to use it.
		EndpointApiCreator.initialize(null);
		
		prefs = this.getSharedPreferences("com.aroundMe", Context.MODE_PRIVATE);
		context=this.getApplicationContext();
		
      // Check device for Play Services APK. If check succeeds, proceed with
      //  GCM registration.
      if (checkPlayServices()) {
          gcm = GoogleCloudMessaging.getInstance(this);
          regid = getRegistrationId(context); // FROM prefs
          if (regid.isEmpty()) {
        	  registerInBackground();
          } else
        	  startLogin();
      } else {
          Log.i(TAG, "No valid Google Play Services APK found.");
      }
	
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing registration ID is not guaranteed to work with
	    // the new app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the registration ID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
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
                storeRegistrationId(context, regid);
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
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	private void startLogin() {
		// Move to SignInActivity
		Intent intent = new Intent(this, SignInActivity.class);
		intent.putExtra("regid",regid);
		startActivity(intent);
		/* Since this is just a wrapper to start the main activity, finish it after launching SignInActivity */
		finish();  
	}

}
