package com.aroundme.common;

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center and radius.
 */
public class MessageGeofence {

    // Instance variables
    private final String id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private final long expirationDuration;
    private final int transitionType;
    private final String content;
    private final String from;
    private final String to;
    private final long date; 

    /**
     * @param geofenceId The Geofence's request ID.
     * @param latitude Latitude of the Geofence's center in degrees.
     * @param longitude Longitude of the Geofence's center in degrees.
     * @param radius Radius of the geofence circle in meters.
     * @param expiration Geofence expiration duration.
     * @param transition Type of Geofence transition.
     */
	public MessageGeofence(String id, double latitude, double longitude,
			float radius, long expirationDuration, int transitionType,
			String content, String from, String to, long date) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.expirationDuration = expirationDuration;
		this.transitionType = transitionType;
		this.content = content;
		this.from = from;
		this.to = to;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getRadius() {
		return radius;
	}

	public long getExpirationDuration() {
		return expirationDuration;
	}

	public int getTransitionType() {
		return transitionType;
	}

	public String getContent() {
		return content;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public long getDate() {
		return date;
	}

	/**
     * Creates a Location Services Geofence object from a SimpleGeofence.
     * @return A Geofence object.
     */
	public Geofence toGeofence() {
        // Build a new Geofence object.
        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(transitionType)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(expirationDuration)
                .build();
    }
}