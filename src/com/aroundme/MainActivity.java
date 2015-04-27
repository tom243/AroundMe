package com.aroundme;

import java.util.List;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.IAppCallBack;
import com.aroundme.controller.Controller;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements OnMapReadyCallback,IAppCallBack<List<UserAroundMe>>,
								ConnectionCallbacks, OnConnectionFailedListener{

	GoogleApiClient mGoogleApiClient;
	Controller controller;
	GoogleMap myMap = null;
	
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
	   /* map.addMarker(new MarkerOptions()
	        .position(new LatLng(0, 0))
	        .title("Marker"));
	    */
	}
	
	public void getAllUsers() {

		/*	controller.getAllUsersAroundMe(10, new IAppCallBack<List<UserAroundMe>>() {
		
		@Override
		public void done(List<UserAroundMe> ret, Exception e) {
			if(e==null)
			{
				
			}
			
		}
		});*/
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		 
		Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
        	GeoPt geo = new GeoPt();
        	geo.setLatitude((float) mLastLocation.getLatitude());
        	geo.setLongitude((float) mLastLocation.getLongitude());
        	controller.getUsersAroundMe(100, geo, this);
        }
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
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
	public void done(List<UserAroundMe> ret, Exception e) {
		
		if(e == null) {
			for (int i=0; i < ret.size(); i++) {
				myMap.addMarker(new MarkerOptions()
		    			.position(new LatLng(ret.get(i).getLocation().getLatitude(), ret.get(i).getLocation().getLongitude())));
			}
		}
		else
			System.out.println("error");
		
	}

	
	
}
