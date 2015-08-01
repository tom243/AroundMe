package com.aroundme.controller;

import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.AppConsts;
import com.aroundme.common.MessageGeofence;
import com.aroundme.geofence.MessageGeofenceStore;
import com.google.android.gms.location.Geofence;

public class GeoController {

	private static GeoController instance;
	private Context context;
	private List<Geofence> mGeofenceList;
	// Persistent storage for geofences.
	private MessageGeofenceStore mGeofenceStorage;
	
	public GeoController(Context context) {
		this.context = context;
	}
	
	public static GeoController getInstance(Context context) {
		if(instance ==  null)
			instance = new GeoController(context);
		return instance;
	}

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
		mGeofenceList.add(newGeoFence.toGeofence()); // **** toGeofence function is ok ?
	}
	
	public void cancelGeofence(String id) {
		Log.i("CONTROLLER","cancel GEO: "+id);
		MessageGeofence geofence = mGeofenceStorage.getGeofence(id);
		for (Geofence geo : mGeofenceList){
			if(geo.getRequestId().equals(id)) {
				Log.i("",""+geo.getRequestId());
				mGeofenceList.remove(geo); 
			}
		}
		
		// cancel GEO notification
		/*NotificationManager nofiManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nofiManager.cancel("GEO",task.getId());*/
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
