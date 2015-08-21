package com.aroundme.controller;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.AppConsts;
import com.aroundme.common.MessageGeofence;
import com.aroundme.geofence.MessageGeofenceStore;
import com.google.android.gms.location.Geofence;

/**
 * @author Tomer and chen
 *
 */
public class GeoController {

	private static GeoController instance;
	private Context context;
	private List<Geofence> mGeofenceList;
	private MessageGeofenceStore mGeofenceStorage; 	// Persistent storage for geofences.
	
	/**
	 * @param context context that received 
	 */
	public GeoController(Context context) {
		this.context = context;
		// Instantiate a new geofence storage area.
		mGeofenceStorage = new MessageGeofenceStore(context);
		// Instantiate the current List of geofences.
		mGeofenceList = new ArrayList<Geofence>();
	}
	
	/**
	 * @param context context that received 
	 * @return instance of the GEO controller
	 * 
	 */
	public static GeoController getInstance(Context context) {
		if(instance ==  null) {
			instance = new GeoController(context);
		}
		return instance;
	}

	/**
	 * @param message contains all details about the message
	 * 
	 * create a geofence  and add it to the list of geofences
	 */
	public void createGeofence(Message message) {
		MessageGeofence newGeoFence;
		float rad = AppConsts.BUILDING_RADIUS_METERS; 
		if (message.getReadRadius() > 0)
			rad = message.getReadRadius(); 
		// Create internal "flattened" objects containing the geofence data.
		newGeoFence = new MessageGeofence(
				message.getId().toString(), 
				message.getLocation().getLatitude(),
				message.getLocation().getLongitude(),
				rad, AppConsts.GEOFENCE_EXPIRATION_TIME,
				Geofence.GEOFENCE_TRANSITION_ENTER| Geofence.GEOFENCE_TRANSITION_EXIT,
				message.getContnet(),message.getFrom(),message.getTo(),message.getTimestamp().getValue());
		// Store these flat versions in SharedPreferences and add them to the geofence list.
		mGeofenceStorage.setGeofence(newGeoFence);
		mGeofenceList.add(newGeoFence.toGeofence()); 
	}
	
	/**
	 * @param id id of the geofence
	 * 
	 * cancel a geofence by delete it from the list of geofences
	 */
	public void cancelGeofence(String id) {
		Log.i("CONTROLLER","cancel GEO: "+id);
		MessageGeofence geofence = mGeofenceStorage.getGeofence(id);
		for (Geofence geo : mGeofenceList){
			if(geo.getRequestId().equals(id)) {
				Log.i("",""+geo.getRequestId());
				mGeofenceList.remove(geo); 
			}
		}
	}
	
	public List<Geofence> getmGeofenceList() {
		return mGeofenceList;
	}

	public void setmGeofenceList(List<Geofence> mGeofenceList) {
		this.mGeofenceList = mGeofenceList;
	}

	public MessageGeofenceStore getmGeofenceStorage() {
		return mGeofenceStorage;
	}

	public void setmGeofenceStorage(MessageGeofenceStore mGeofenceStorage) {
		this.mGeofenceStorage = mGeofenceStorage;
	}
	
}
