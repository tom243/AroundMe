package com.aroundme;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author Tomer and chen
 * 
 * sign in activity
 */
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
	private String regId=null;
	private Controller controller;
	private String email;
	private Person currentPerson;
	
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
		regId = controller.getRegistrationId(getApplicationContext());
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	@Override
	public void onClick(View view) {
		if (controller.isOnline(getApplicationContext())){	
			if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
				this.findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
				mSignInClicked = true;
				mGoogleApiClient.connect();
			}
		}
		else
			Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
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
		if (controller.isOnline(getApplicationContext())){	
			email = Plus.AccountApi.getAccountName(mGoogleApiClient);
			currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			if (currentPerson != null && email != null) {
				controller.login(email,currentPerson.getId(),regId,this,this);
			}
		}
		else
			Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();

	}

	@Override
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
	
	@Override
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

	/**
	 *  move to main activity
	 */
	public void moveToMainActivity(){
		// Move to MainActivity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		/* Since this is just a wrapper to start the main activity, finish it after launching SignInActivity */
		finish();
	}

	@Override
	public void done(User user, Exception e) {
		if (e == null) {
			if(user == null) {
				if (currentPerson != null) {
					String personName = currentPerson.getDisplayName();
					String personPhoto = currentPerson.getImage().getUrl();
					String password =currentPerson.getId();
					this.user = new User();
					this.user.setFullName(personName);
					this.user.setMail(email);
					this.user.setPassword(password);
					this.user.setImageUrl(personPhoto);
					this.user.setRegistrationId(regId);
					if (controller.isOnline(getApplicationContext()))	
						controller.register(this.user,this,this);
					else
						Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
				}
			} else{
				this.user = user;
				moveToMainActivity();
			}
		} else {
			System.out.println("error");
		    Toast.makeText(this, "Error while try login.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void visible(Exception e) {
		if (e == null) {
			this.findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
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
	}
}
