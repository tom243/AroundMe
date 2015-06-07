package com.aroundme;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignInActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, OnClickListener, IAppCallBack<User>, SplashInterface {

	private ProgressBar progressBar;

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
	private String regId=null;
	private Controller controller;
	private String email;
	private Person currentPerson;
	private boolean signoutPressed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		controller = Controller.getInstance();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		//findViewById(R.id.sign_out_button).setOnClickListener(this);
		extars = getIntent().getExtras();
		if (extars != null)
			regId = extars.getString("regid");
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(signoutBroadcast, new IntentFilter("Signout"));
	}

	private BroadcastReceiver signoutBroadcast = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
			signoutPressed = true;
			// Prior to disconnecting, run clearDefaultAccount().
			//mGoogleApiClient.connect();
			// finish();
	  }
	};
	
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
			this.findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
			//this.findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
			mSignInClicked = true;
			mGoogleApiClient.connect();
			signoutPressed = false;
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
		//Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		if (!signoutPressed) {
			email = Plus.AccountApi.getAccountName(mGoogleApiClient);
			currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			signoutPressed = false;
			controller.login(email,currentPerson.getId(),regId,this,this);
		}
		else {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status status) {

						}
					});
			mGoogleApiClient.disconnect();
			//mGoogleApiClient.connect();
			signoutPressed = false;
		}
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

	public void moveToMainActivity(){
		// Move to MainActivity
		Intent intent = new Intent(this, MainActivity.class);
		//Intent intent = new Intent(this, MapActivity.class);
		//intent.putExtra("regid",regid);
		startActivity(intent);
		/* Since this is just a wrapper to start the main activity, finish it after launching SignInActivity */
		//finish();
	}

	@Override
	public void done(User user, Exception e) {
		if (e == null) {
			if(user == null) {
				//Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				if (currentPerson != null) {
					String personName = currentPerson.getDisplayName();
					String personPhoto = currentPerson.getImage().getUrl();
					String password =currentPerson.getId();
					// String personGooglePlusProfile = currentPerson.getUrl();
					this.user = new User();
					this.user.setFullName(personName);
					this.user.setMail(email);
					this.user.setPassword(password);
					this.user.setImageUrl(personPhoto);
					this.user.setRegistrationId(regId);
					controller.register(this.user,this,this);
				}
			} else{
				this.user = user;
				moveToMainActivity();
			}
		} else 
			System.out.println("error");
			// handle ??
	}

	@Override
	public void visible(Exception e) {
		if (e == null) {
			this.findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
//			this.findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void unvisible(Exception e) {
		if (e == null) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(signoutBroadcast);
		
	}

}
