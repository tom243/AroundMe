package com.aroundme;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.AppConsts;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.IAppCallBack2;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.aroundme.controller.ImagesController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback,IAppCallBack<List<UserAroundMe>>,
				IAppCallBack2<ArrayList<BitmapDescriptor>>,ConnectionCallbacks, OnConnectionFailedListener{

	private GoogleApiClient mGoogleApiClient;
	private Controller controller;
	private GoogleMap myMap = null;
	private List<UserAroundMe> usersAroundMe = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
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

		OnMarkerClickListener markersListener = new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				//Toast.makeText(getApplicationContext(), arg0.getTitle(),Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(),	ConversationActivity.class);
				intent.putExtra(AppConsts.email_friend, arg0.getTitle());
				startActivity(intent);
				return true;
			}
		};
		myMap.setOnMarkerClickListener(markersListener);
		
		mGoogleApiClient.connect();
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
			usersAroundMe = users;
			Marker marker = null; 
			for (int i=0; i<users.size(); i++) {
				marker = myMap.addMarker(new MarkerOptions()
	    			.position(new LatLng(users.get(i).getLocation().getLatitude(), users.get(i).getLocation().getLongitude()))
	    			.title(users.get(i).getMail()));
			}
			controller.getImagesUsersAroundMe(users, this);
		}
		else { // exception thrown from function: getUsersAroundMe from server
			Toast.makeText(getApplicationContext(),"Ex' thrown from func getUsersAroundMe",Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		
	}
	
   @Override
    protected void onStart() {
        super.onStart();
  //      if (!mResolvingError) {  // more about this later
            //mGoogleApiClient.connect();
  //      }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

	@Override
	public void done2(ArrayList<BitmapDescriptor> imagesArr, Exception e) {
		if (e == null){
			// delete all previous markers
			myMap.clear();
			// add new markers with the pictures from G+
			Marker marker = null; 
			for (int i=0; i<usersAroundMe.size(); i++) {
				marker = myMap.addMarker(new MarkerOptions()
	    			.position(new LatLng(usersAroundMe.get(i).getLocation().getLatitude(), usersAroundMe.get(i).getLocation().getLongitude()))
	    			.title(usersAroundMe.get(i).getMail())
	    			.icon(imagesArr.get(i)));
			}
		} else {
			// ?
		}
	}
}



/*			LinearLayout picLL = new LinearLayout(this);
	ImageView imageView = new ImageView(this);
	imageView.setImageResource(R.drawable.ic_launcher);
    picLL.addView(imageView);
    setContentView(picLL);
	for (int i=0; i<users.size(); i++) {
		//ImageView imageView = (ImageView)this.findViewById(R.);
		
		ImageLoader imageLoader = ImagesController.getInstance().getImageLoader();
		ImageContainer imageContainer = imageLoader.get(users.get(i).getImageUrl(), imageLoader.getImageListener(imageView, R.drawable.user_default, R.drawable.user_default));
		Bitmap bitmap = imageContainer.getBitmap();
		
		marker = myMap.addMarker(new MarkerOptions()
			.position(new LatLng(users.get(i).getLocation().getLatitude(), users.get(i).getLocation().getLongitude()))
		.title(usersAroundMe.get(i).getDisplayName())
		.icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
		marker.setDraggable(true);
		if (markers != null && !markers.contains(marker))
			markers.add(marker);
	}*/