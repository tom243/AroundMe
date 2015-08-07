package com.aroundme.common;

import android.net.Uri;

import com.google.android.gms.location.Geofence;

public class AppConsts {

	public static final int radius_around_me = 5000000;
	public static final String email_friend = "email_friend";
	
	// classes TAG
	public static final String MAP_ACTIVITY_TAG = "MapActivity";
	
	public static final String SHARED_PREFERENCES = "SharedPreferences";
	
	// For the purposes of this demo, the geofences are hard-coded and should not expire.
    // An app with dynamically-created geofences would want to include a reasonable expiration time.
    public static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

    // Geofence parameters 
    public static final float BUILDING_RADIUS_METERS = 8;

    // The constants below are less interesting than those above.

    // Path for the DataItem containing the last geofence id entered.
    public static final String GEOFENCE_DATA_ITEM_PATH = "/geofenceid";
    public static final Uri GEOFENCE_DATA_ITEM_URI =
            new Uri.Builder().scheme("wear").path(GEOFENCE_DATA_ITEM_PATH).build();
    public static final String KEY_GEOFENCE_ID = "geofence_id";

    // Keys for flattened geofences stored in SharedPreferences.
    public static final String KEY_MESSAGE_LATITUDE = "geofencing.KEY_LATITUDE";
    public static final String KEY_MESSAGE_LONGITUDE = "geofencing.KEY_LONGITUDE";
    public static final String KEY_MESSAGE_RADIUS = "geofencing.KEY_RADIUS";
    public static final String KEY_MESSAGE_EXPIRATION_DURATION = "geofencing.KEY_EXPIRATION_DURATION";
    public static final String KEY_MESSAGE_TRANSITION_TYPE = "geofencing.KEY_TRANSITION_TYPE";
    public static final String KEY_MESSAGE_CONTENT = "geofencing.KEY_CONTENT";
    public static final String KEY_MESSAGE_FROM = "geofencing.KEY_FROM";
    public static final String KEY_MESSAGE_TO = "geofencing.KEY_TO";
    public static final String KEY_MESSAGE_DATE = "geofencing.KEY_DATE";
    // The prefix for flattened geofence keys.
    public static final String KEY_PREFIX = "geofencing.KEY";
    
    // Invalid values, used to test geofence storage when retrieving geofences.
    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;
    public static final String INVALID_STRING_VALUE = "";
    
    // type messages
    public static final String TYPE_PIN_MSG = "type_pin_msg";
    public static final String TYPE_GEO_MSG = "type_geo_msg";
	
}