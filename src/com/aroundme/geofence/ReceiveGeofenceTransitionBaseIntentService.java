package com.aroundme.geofence;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Listens for geofence transition changes.
 */
public abstract class ReceiveGeofenceTransitionBaseIntentService extends IntentService {

	/**
	 * Sets an identifier for this class' background thread
	 */
	public ReceiveGeofenceTransitionBaseIntentService() {
		super("ReceiveGeofenceTransitionBaseIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		GeofencingEvent event = GeofencingEvent.fromIntent(intent);
		if (event != null) {

			if (event.hasError()) {
				onError(event.getErrorCode());
			} else {
				int transition = event.getGeofenceTransition();
				if (transition == Geofence.GEOFENCE_TRANSITION_ENTER
						|| transition == Geofence.GEOFENCE_TRANSITION_DWELL
						|| transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
					String[] geofenceIds = new String[event
							.getTriggeringGeofences().size()];
					for (int index = 0; index < event.getTriggeringGeofences()
							.size(); index++) {
						geofenceIds[index] = event.getTriggeringGeofences()
								.get(index).getRequestId();
					}

					if (transition == Geofence.GEOFENCE_TRANSITION_ENTER
							|| transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
						onEnteredGeofences(geofenceIds);
					} else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
						onExitedGeofences(geofenceIds);
					}
				}
			}

		}
	}

	/**
	 * @param geofenceIds array of geofences id's
	 * 
	 * operate when person entered geofence zone
	 */
	protected abstract void onEnteredGeofences(String[] geofenceIds);

	/**
	 * @param geofenceIds array of geofences id's
	 * 
	 * operate when person exited geofence zone
	 */
	protected abstract void onExitedGeofences(String[] geofenceIds);

	/**
	 * @param errorCode error code 
	 */
	protected abstract void onError(int errorCode);
}
