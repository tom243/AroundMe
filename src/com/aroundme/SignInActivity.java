package com.aroundme;

import java.io.IOException;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.aroundme.deviceinfoendpoint.Deviceinfoendpoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SignInActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, OnClickListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;
	
	/**
	 * True if the sign-in button was clicked.  When true, we know to resolve all
	 * issues preventing sign-in without waiting.
	 * 
	 */
	private boolean mSignInClicked;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 * True if we are in the process of resolving a ConnectionResult
	 */
	private boolean mIntentInProgress;

	private User user;
	private Bundle extars=null;
	String regId=null;
	Aroundmeapi endpoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		findViewById(R.id.sign_out_button).setOnClickListener(this);
		extars= getIntent().getExtras();
		regId = extars.getString("regid");
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			mGoogleApiClient.connect();
		}
		if (view.getId() == R.id.sign_out_button) {
		    if (mGoogleApiClient.isConnected()) {
		      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		   // Prior to disconnecting, run clearDefaultAccount().
		      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		      Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
		          .setResultCallback(new ResultCallback<Status>() {
				@Override
				public void onResult(Status status) {
		          // mGoogleApiClient is now disconnected and access has been revoked.
		          // Trigger app logic to comply with the developer policies
				 // I think we need to do actions in server side
					
				/* In the onResult callback, you can respond to the event and trigger any appropriate logic in 
				  your app or your back-end code. For more information, see the deletion rules in the developer policies.*/
				}

		      });
		      Toast.makeText(getApplicationContext(), "User is disconnected!", Toast.LENGTH_SHORT).show();
		      mGoogleApiClient.disconnect();
		      mGoogleApiClient.connect();
		    }
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	  if (!mIntentInProgress) {
	    if (mSignInClicked && result.hasResolution()) {
	      // The user has already clicked 'sign-in' so we attempt to resolve all
	      // errors until the user is signed in, or they cancel.
	      try {
	        result.startResolutionForResult(this, RC_SIGN_IN);
	        mIntentInProgress = true;
	      } catch (SendIntentException e) {
	        // The intent was canceled before it was sent.  Return to the default
	        // state and attempt to connect to get an updated ConnectionResult.
	        mIntentInProgress = false;
	        mGoogleApiClient.connect();
	      }
	    }
	  }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		new  AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					endpoint = EndpointApiCreator.getApi(Aroundmeapi.class);
					
					String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
					Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
					if (endpoint.login(email,currentPerson.getId(),regId).execute() == null) {
						if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
							String personName = currentPerson.getDisplayName();
							String personPhoto = currentPerson.getImage().getUrl();
							String password =currentPerson.getId();
							// String personGooglePlusProfile = currentPerson.getUrl();
							user = new User();
							user.setFullName(personName);
							user.setMail(email);
							user.setPassword(password);
							user.setImageUrl(personPhoto);
							user.setRegistrationId(regId);
						}
						endpoint.register(user).execute();
					};
				}
		
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		  if (requestCode == RC_SIGN_IN) {
		    if (responseCode != RESULT_OK) {
		      mSignInClicked = false;
		    }

		    mIntentInProgress = false;

		    if (!mGoogleApiClient.isConnected()) {
		      mGoogleApiClient.reconnect();
		    }
		  }
		}

	public void onConnectionSuspended(int cause) {
		  mGoogleApiClient.connect();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
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
}
