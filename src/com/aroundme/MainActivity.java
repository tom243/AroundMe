package com.aroundme;

import com.aroundme.adapter.ViewPagerAdapter;
import com.aroundme.common.SlidingTabLayout;
import com.aroundme.controller.Controller;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
    
    /* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        
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
        	//Toast.makeText(this, "about" , Toast.LENGTH_SHORT).show();
        	// if...
        	Intent intent = new Intent(this, AboutActivity.class);
    		startActivity(intent);
        	return true;
        }
        if (id == R.id.action_map) {
        	//Toast.makeText(this, "map" , Toast.LENGTH_SHORT).show();
        	Intent intent = new Intent(this, MapActivity.class);
    		startActivity(intent);
        	return true;
        }
        
        if (id == R.id.action_signout) {
        	//Toast.makeText(this, "sign-out" , Toast.LENGTH_SHORT).show();
        	//Intent signOutIntent = new Intent("Signout");
		    //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(signOutIntent);
    		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this).addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN).build();
			mGoogleApiClient.connect();
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public void onConnected(Bundle connectionHint) {
		if (controller.isOnline(getApplicationContext())){	
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
		}
		else
			Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
	    Intent intent = new Intent(this, SignInActivity.class);
	    // clear the singletone controller
	    Controller.getInstance().clear(); 
		startActivity(intent);
		finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
	}
	
}