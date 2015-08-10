package com.aroundme;

import java.util.ArrayList;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.adapter.ViewPagerAdapter;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ChatMessage;
import com.aroundme.common.SlidingTabLayout;
import com.aroundme.controller.Controller;
import com.aroundme.controller.GeoController;
import com.aroundme.geofence.GeofencingReceiverIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
 
public class MainActivity extends ActionBarActivity implements ConnectionCallbacks,
OnConnectionFailedListener { 

	private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[]={"Conversations","Friends"};
    private int Numboftabs =2;
    private Controller controller;
    private GeoController geoController;
    //private Long geoMessageId = null;
	private PendingIntent mGeofenceRequestIntent;
	private boolean signOut = false;
	private ArrayList<Long> addGeoList;
	private ArrayList<Long> removeGeoList;
    
    /* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // initialize the geo's lists
        addGeoList = new ArrayList<Long>();
        removeGeoList = new ArrayList<Long>();
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles for the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		// Assigning the Sliding Tab Layout View
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);
        controller=Controller.getInstance();
        geoController = GeoController.getInstance(AroundMeApp.getContext());
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
        if (id == R.id.action_about) {
        	// if...
        	Intent intent = new Intent(this, AboutActivity.class);
    		startActivity(intent);
        	return true;
        }
        if (id == R.id.action_map) {
        	Intent intent = new Intent(this, MapActivity.class);
    		startActivity(intent);
        	return true;
        }
        if (id == R.id.action_signout) {
    		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this).addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN).build();
    		signOut =true;
			mGoogleApiClient.connect();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public void onConnected(Bundle connectionHint) {
		if (!addGeoList.isEmpty()) {
			Toast.makeText(this, "Start geofence service", Toast.LENGTH_SHORT).show();
			for (Long geoId : addGeoList) {
				// Get the PendingIntent for the geofence monitoring request.
				// Send a request to add the current geofences.
				mGeofenceRequestIntent = getGeofenceTransitionPendingIntent(geoId);
				if (null != mGeofenceRequestIntent) 
					LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geoController.getmGeofenceList(), mGeofenceRequestIntent);
			}
			addGeoList.clear();
		}
		if (!removeGeoList.isEmpty()) {
			for (Long geoId : removeGeoList) {
				Toast.makeText(this, "Remove geofence", Toast.LENGTH_SHORT).show();
				System.out.println("main activity geo id " + geoId);
				mGeofenceRequestIntent = getGeofenceTransitionPendingIntent(geoId);				
				if (null != mGeofenceRequestIntent) 
					LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,mGeofenceRequestIntent);
				// cancel geofence
				System.out.println("main activity geo id string " + geoId.toString());
				geoController.cancelGeofence(geoId.toString());
			}
			removeGeoList.clear();
		}
		if (signOut) {
			if (controller.isOnline(AroundMeApp.getContext())){	
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
			}
			else
				Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
		    Intent intent = new Intent(this, SignInActivity.class);
		    // clear the singletone controller
		    Controller.getInstance().clear();
		    mGoogleApiClient.disconnect();	// check !!
			startActivity(intent);
			finish();
		}
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(mGeoMessageReceiver, new IntentFilter("geoMessage"));
		LocalBroadcastManager.getInstance(this).registerReceiver(mGeoRemoveReceiver, new IntentFilter("removeGeofence"));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
    @Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeoMessageReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeoRemoveReceiver);
	}

	private BroadcastReceiver mGeoMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Long geoMessageId = intent.getLongExtra("geoId",999);
	        if (geoMessageId != null) {
	        	addGeoList.add(geoMessageId);
	        	mGoogleApiClient.connect();
	        }
	    }
	        //  ... react to local broadcast message
	};
	
	private BroadcastReceiver mGeoRemoveReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Long geoMessageId = intent.getLongExtra("geoId",999);
	        if (geoMessageId != null) {
	        	removeGeoList.add(geoMessageId);
	        	mGoogleApiClient.connect();
	        }
	    }
	        //  ... react to local broadcast message
	};
	
	/**
	 * Create a PendingIntent that triggers GeofenceTransitionIntentService when
	 * a geofence transition occurs.
	 */
	private PendingIntent getGeofenceTransitionPendingIntent(Long geoId) {
		Intent intent = new Intent(this, GeofencingReceiverIntentService.class);
		return PendingIntent.getService(this, geoId.intValue(), intent, // need to make sure that casting is OK geoMessageId.intValue()
				PendingIntent.FLAG_UPDATE_CURRENT);

	}
	
}