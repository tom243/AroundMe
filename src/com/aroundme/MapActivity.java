package com.aroundme;

import java.util.ArrayList;
import java.util.List;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.AppConsts;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.IAppCallBack2;
import com.aroundme.controller.Controller;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapActivity extends Activity implements OnMapReadyCallback,IAppCallBack<List<UserAroundMe>>,
				IAppCallBack2<ArrayList<BitmapDescriptor>>,ConnectionCallbacks, OnConnectionFailedListener{

	GoogleApiClient mGoogleApiClient;
	Controller controller;
	GoogleMap myMap = null;
	ArrayList<Marker> markers = null;
	List<UserAroundMe> usersAroundMe= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		buildGoogleApiClient();
		controller = Controller.getInstance();
		MapFragment mapFragment = (MapFragment) getFragmentManager()
			    .findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);
	}
	
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		myMap = map;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		 
		Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
        	GeoPt geo = new GeoPt();
        	geo.setLatitude((float) mLastLocation.getLatitude());
        	geo.setLongitude((float) mLastLocation.getLongitude());
        	myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
	                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13));
	        CameraPosition cameraPosition = new CameraPosition.Builder()
	        	.target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to location user
	        	.zoom(17)                   // Sets the zoom
	        	.bearing(90)                // Sets the orientation of the camera to east
	        	.tilt(40)                   // Sets the tilt of the camera to 30 degrees
	        	.build();                   // Creates a CameraPosition from the builder
	        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	        controller.getUsersAroundMe(AppConsts.radius_around_me, geo, this);
        }
	}

	@Override
	public void done(final List<UserAroundMe> users, Exception e) {
		if(e == null) {
			controller.getImagesUsersAroundMe(users, this);
			usersAroundMe = users;
		}
		else
			; // error
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		
	}
	
   @Override
    protected void onStart() {
        super.onStart();
  //      if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
  //      }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

	@Override
	public void done2(ArrayList<BitmapDescriptor> imagesArr, Exception e) {
		Marker marker = null; 
		for (int i=0; i<usersAroundMe.size(); i++) {
			marker = myMap.addMarker(new MarkerOptions()
    			.position(new LatLng(usersAroundMe.get(i).getLocation().getLatitude(), usersAroundMe.get(i).getLocation().getLongitude()))
    			.title(usersAroundMe.get(i).getDisplayName())
    			.icon(imagesArr.get(i)));
			marker.setDraggable(true);
			if (markers != null && !markers.contains(marker))
				markers.add(marker);
		}
	}
}
