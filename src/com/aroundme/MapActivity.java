package com.aroundme;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.IAppCallBack2;
import com.aroundme.common.SplashInterface;
import com.aroundme.controller.Controller;
import com.aroundme.data.DAO;
import com.aroundme.data.IDataAccess;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.util.DateTime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback,IAppCallBack<List<UserAroundMe>>,
				IAppCallBack2<ArrayList<BitmapDescriptor>>,ConnectionCallbacks, OnConnectionFailedListener,
				SplashInterface{
	
	private GoogleApiClient mGoogleApiClient;
	private Controller controller;
	private GoogleMap myMap = null;
	private List<UserAroundMe> usersAroundMe = null;
	private EditText editTextContent;
	private IDataAccess dao;
	private static enum type_msg {TYPE_PIN_MSG,TYPE_GEO_MSG};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		if (!isGooglePlayServicesAvailable()) {
			Log.e(AppConsts.MAP_ACTIVITY_TAG, "Google Play services unavailable.");
			Toast.makeText(this, "The application cannot work without google play service" , Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		buildGoogleApiClient();
		controller = Controller.getInstance();
		dao = DAO.getInstance(AroundMeApp.getContext());
		MapFragment mapFragment = (MapFragment) getFragmentManager()
			    .findFragmentById(R.id.map);
		if (controller.isOnline(AroundMeApp.getContext()))	
			mapFragment.getMapAsync(this);
		else
			Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
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
				// when a marker clicked the info window will be shown
				arg0.showInfoWindow();
				return true;
			}
		};
		OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				// when an info window clicked the chat with him will be opened 
				Intent intent = new Intent(AroundMeApp.getContext(),	ConversationActivity.class);
				intent.putExtra(AppConsts.email_friend, marker.getSnippet());
				startActivity(intent);
			}
		};
		OnMapLongClickListener longClickListener = new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(final LatLng point) {
				Toast.makeText(AroundMeApp.getContext(), "long click on a map", Toast.LENGTH_SHORT).show();
				// the first dialog to choose the type of message
				AlertDialog.Builder type_builder = new AlertDialog.Builder(MapActivity.this)
					.setTitle(R.string.choose_msg_type)
					.setItems(R.array.messages, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// The 'which' argument contains the index position
							// of the selected item
							Toast.makeText(AroundMeApp.getContext(), "index: "+which, Toast.LENGTH_SHORT).show();
							final type_msg lastMsgType;
							if (which == 0) { // pin message
								lastMsgType = type_msg.TYPE_PIN_MSG;
							}
							else if (which == 1) { // geofence message
								lastMsgType = type_msg.TYPE_GEO_MSG;
							}
							// the second dialog to choose friends
							final ArrayList mSelectedItems = new ArrayList(); 
						    AlertDialog.Builder friends_builder = new AlertDialog.Builder(MapActivity.this);
						    // Set the dialog title
						    friends_builder.setTitle(R.string.choose_friends)
						    // Specify the list array, the items to be selected by default (null for none),
						    // and the listener through which to receive callbacks when items are selected
				           .setMultiChoiceItems(R.array.friends, null,
				                      new DialogInterface.OnMultiChoiceClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int which,
				                       boolean isChecked) {
				                   if (isChecked) {
				                       // If the user checked the item, add it to the selected items
				                       mSelectedItems.add(which);
				                   } else if (mSelectedItems.contains(which)) {
				                       // Else, if the item is already in the array, remove it 
				                       mSelectedItems.remove(Integer.valueOf(which));
				                   }
				               }
				           })
						   // Set the action buttons
				           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				                    // User clicked OK, so save the mSelectedItems results somewhere
				                    // or return them to the component that opened the dialog
				                   
				            	    // the third dialog to send the message
					            	AlertDialog.Builder msg_builder = new AlertDialog.Builder(MapActivity.this);
					   				// Get the layout inflater
					   			    LayoutInflater inflater = getLayoutInflater();
					   			    final View v = inflater.inflate(R.layout.dialog_geo_message, null);
					   				msg_builder.setView(v)
					   				.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
					   					@Override
					   					public void onClick(DialogInterface dialog, int id) {
					   						//Toast.makeText(AroundMeApp.getContext(), id, Toast.LENGTH_SHORT).show();
					   						// send message 
					   						editTextContent = (EditText) v.findViewById(R.id.geo_message_content);
					   						if (editTextContent.getText().toString() != null) {
					   							String to = "tomer.luster@gmail.com";
					   							// for... 
					   								sendGeoMessage(to,editTextContent.getText().toString(), (float)point.latitude, (float)point.longitude);
					   						}
					   					}
					   				})
					   				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					   					public void onClick(DialogInterface dialog, int id) {
					   						//LoginDialogFragment.this.getDialog().cancel();
					   					}
					   				});      
					   				msg_builder.create().show();
				               }
				           })
				           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				                   // are you sure dialog  ??
				               }
				           });
						   friends_builder.create().show();
						}
					});
				type_builder.create().show();
			}
		};
		myMap.setOnMarkerClickListener(markersListener);
		myMap.setOnMapLongClickListener(longClickListener);
		myMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
		mGoogleApiClient.connect();
	}
	
	public void sendGeoMessage(String to,String content, float lat, float lon) {
		GeoPt geoPt = new GeoPt();
		geoPt.setLatitude(lat);
		geoPt.setLongitude(lon);
		controller.sendMessageToUser(content,to,geoPt,
				new IAppCallBack<Void>() {
					@Override
					public void done(Void ret, Exception e) {
						// TODO Auto-generated method stub
					}
				}, this);
		Message message = new Message();
		message.setContnet(content);
		message.setFrom(controller.getCurrentUser().getMail());
		message.setTo(to);
		message.setTimestamp(new DateTime(new Date()));
		message.setLocation(geoPt);
		message.setReadRadius(80);
		// add the new message to messages table in db 
   		Long messageId = addMessageToDB(message);	
   		// update the conversations table in db with the last message
		updateConversationTable(message, messageId);
		// send broadcast to tell the receiver to refresh the adapter for open conversation list
		Intent updateAdapterIntent = new Intent("updateOpenCoversationsAdapter");
	    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(updateAdapterIntent);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (controller.isOnline(AroundMeApp.getContext())){	  
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
		else
			Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void done(final List<UserAroundMe> users, Exception e) {
		if(e == null) {
			usersAroundMe = users;
			Marker marker = null; 
			for (int i=0; i<users.size(); i++) {
				marker = myMap.addMarker(new MarkerOptions()
	    			.position(new LatLng(users.get(i).getLocation().getLatitude(), users.get(i).getLocation().getLongitude()))
	    			.snippet(users.get(i).getMail())
	    			.title(users.get(i).getDisplayName()));
			}
			if (controller.isOnline(AroundMeApp.getContext()))				
				controller.getImagesUsersAroundMe(users, this);
			else
				Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
		}
		else { // exception thrown from function: getUsersAroundMe from server
			Toast.makeText(AroundMeApp.getContext(),"Ex' thrown from func getUsersAroundMe",Toast.LENGTH_SHORT).show();
		}
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
	    			.snippet(usersAroundMe.get(i).getMail())
	    			.title(usersAroundMe.get(i).getDisplayName())
	    			.icon(imagesArr.get(i)));
			}
		} else {
			// ?
		}
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		
	}
	
   @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    
    /**
	 * Checks if Google Play services is available.
	 * 
	 * @return true if it is.
	 */
	private boolean isGooglePlayServicesAvailable() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == resultCode) {
			if (Log.isLoggable(AppConsts.MAP_ACTIVITY_TAG, Log.DEBUG)) {
				Log.d(AppConsts.MAP_ACTIVITY_TAG, "Google Play services is available.");
			}
			return true;
		} else {
			Log.e(AppConsts.MAP_ACTIVITY_TAG, "Google Play services is unavailable.");
			return false;
		}
	}

	public Long addMessageToDB(Message message){
		dao.open();
		Long id = dao.addToMessagesTable(message);
		dao.close();
		return id;
	}
	
	public void updateConversationTable(Message message, Long messageId){
		dao.open();
		ConversationItem conv = dao.isConversationExist(controller.getCurrentUser().getMail(), message.getFrom());
		if (conv != null) {
			System.out.println("Conversation  exist");
			dao.updateOpenConversation(conv, messageId); // update row in data-base
		}
		else {
			System.out.println("Conversation not exist");
			dao.addToConversationsTable(message.getFrom(), message.getTo(), messageId);
		}
		dao.close();
	}
	
	@Override
	public void visible(Exception e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unvisible(Exception e) {
		// TODO Auto-generated method stub
	}

}
