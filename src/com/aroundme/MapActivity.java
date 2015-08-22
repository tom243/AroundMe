package com.aroundme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.common.AppConsts;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ExtendedMessage;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.IAppCallBack2;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
				IAppCallBack2<ArrayList<BitmapDescriptor>>,ConnectionCallbacks, OnConnectionFailedListener{
	
	private GoogleApiClient mGoogleApiClient;
	private Controller controller;
	private GoogleMap myMap = null;
	private List<UserAroundMe> usersAroundMe = null;
	private EditText editTextContent;
	private IDataAccess dao;
	private static enum type_msg {TYPE_PIN_MSG,TYPE_GEO_MSG};
	private String allUsersMail [] = null;
	private String allUsersName [] = null;
	private ArrayList<String> mSelectedItems = null;
	private type_msg lastMsgType = null;
	private ArrayList<Message> receivedPinMsgs;
	
	private Map<Marker, Long> markerMap = new HashMap<>();
	
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
		// get all pin messages from dao
		dao.open();
		receivedPinMsgs = dao.getPinMessages(controller.getCurrentUser().getMail());
		dao.close();
	}
	
	/**
	 * build google API client 
	 */
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
			public void onInfoWindowClick(final Marker marker) {
				// click on info window of a marker that represents friend will open chat with him  
				if (!markerMap.containsKey(marker)) {	
					Intent intent = new Intent(AroundMeApp.getContext(), ConversationActivity.class);
					intent.putExtra(AppConsts.EMAIL_FRIEND, marker.getSnippet());
					startActivity(intent);
				}
				// click on info window of a marker that represents pin message will open a dialog with option to delete it from map 
				else {
					removePinMsgDialog(marker).show();
				}
			}
		};
		
		OnMapLongClickListener longClickListener = new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(final LatLng point) {
				// get all users (from controller) and create simple array
				int size = controller.getAllUsersList().size();
				allUsersMail = new String[size];
				allUsersName = new String[size];
				int i = 0;
				for (UserAroundMe user: controller.getAllUsersList()) {
					allUsersMail[i] = user.getMail();
					allUsersName[i] = controller.getUserNameByMail(user.getMail());
					i++;
				}
				// the first dialog to choose the type of message
				AlertDialog.Builder typeBuilder = new AlertDialog.Builder(MapActivity.this)
					.setTitle(R.string.choose_msg_type)
					.setItems(R.array.messages, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// The 'which' argument contains the index position
							// of the selected item
							if (which == 0) { // pin message
								lastMsgType = type_msg.TYPE_PIN_MSG;
							}
							else if (which == 1) { // geofence message
								lastMsgType = type_msg.TYPE_GEO_MSG;
							}
							// the second dialog to choose friends
							mSelectedItems = new ArrayList<String>();
						    AlertDialog.Builder friendsBuilder = new AlertDialog.Builder(MapActivity.this);
						    // Set the dialog title
						    friendsBuilder.setTitle(R.string.choose_friends)
						    // Specify the list array, the items to be selected by default (null for none),
						    // and the listener through which to receive callbacks when items are selected
				           .setMultiChoiceItems(allUsersName, null,
				                      new DialogInterface.OnMultiChoiceClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int which,
				                       boolean isChecked) {
				                   if (isChecked) {
				                       // If the user checked the item, add it to the selected items
				                       mSelectedItems.add(allUsersMail[which]);
				                   } else {
				                	   if (mSelectedItems.contains(allUsersMail[which])) {
				                       // Else, if the item is already in the array, remove it 
				                       mSelectedItems.remove(allUsersMail[which]);
				                	   }
				                	}
				               }
				           })
						   // Set the action buttons
				           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				                    // User clicked OK, so save the mSelectedItems results somewhere
				                    // or return them to the component that opened the dialog
				            	    if (!mSelectedItems.isEmpty()) {
					            	    // the third dialog to send the message
						            	AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MapActivity.this);
						   				// Get the layout inflater
						   			    LayoutInflater inflater = getLayoutInflater();
						   			    final View v = inflater.inflate(R.layout.dialog_geo_message, null);
						   				msgBuilder.setView(v)
						   				.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
						   					@Override
						   					public void onClick(DialogInterface dialog, int id) {
						   						// send message 
						   						editTextContent = (EditText) v.findViewById(R.id.geo_message_content);
						   						// checking the message isn't empty
						   						String str = editTextContent.getText().toString();
						   						System.out.println(str);
						   						if (!editTextContent.getText().toString().equals("")) {
						   							// if it is GEO message
						   							if (lastMsgType == type_msg.TYPE_GEO_MSG) {
							   							for (String friendMail: mSelectedItems) {
							   								Toast.makeText(AroundMeApp.getContext(),"sending location message to " + controller.getUserNameByMail(friendMail), Toast.LENGTH_SHORT).show();
							   								sendLocationBasedMessage(friendMail, editTextContent.getText().toString(), AppConsts.TYPE_GEO_MSG, (float)point.latitude, (float)point.longitude);
							   							}
							   							editTextContent.setText("");
						   							}
						   							// it is PIN message
						   							else if (lastMsgType == type_msg.TYPE_PIN_MSG) {
						   								for (String friendMail: mSelectedItems) {
							   								Toast.makeText(AroundMeApp.getContext(),"sending pin message to " + controller.getUserNameByMail(friendMail), Toast.LENGTH_SHORT).show();
						   									sendLocationBasedMessage(friendMail, editTextContent.getText().toString(), AppConsts.TYPE_PIN_MSG, (float)point.latitude, (float)point.longitude);
							   							}
						   								editTextContent.setText("");
						   							}
						   						}
						   						else
						   							Toast.makeText(AroundMeApp.getContext(), "Can't send empty message", Toast.LENGTH_SHORT).show();
						   					}
						   				})
						   				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						   					public void onClick(DialogInterface dialog, int id) {
						   						
						   					}
						   				});      
						   				msgBuilder.create().show();
				            	    }
				            	    else
				            	    	Toast.makeText(AroundMeApp.getContext(), "You forgot to choose friends. Try again", Toast.LENGTH_SHORT).show();
				               }
				           })
				           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				               }
				           });
						   friendsBuilder.create().show();
						}
					});
				typeBuilder.create().show();
			}
		};
		myMap.setOnMarkerClickListener(markersListener);
		myMap.setOnMapLongClickListener(longClickListener);
		myMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
		mGoogleApiClient.connect();
	}
	
	/**
	 * @param to friend mail
	 * @param content  content of the message 
	 * @param msgType  type of the message
	 * @param lat latitude of the message
	 * @param lon longitude of the message
	 * 
	 * sends location based messages to friends
	 */
	private void sendLocationBasedMessage(final String to,final String content, final String msgType, float lat, float lon) {
		final GeoPt geoPt = new GeoPt();
		geoPt.setLatitude(lat);
		geoPt.setLongitude(lon);
		controller.sendMessageToUser(content,msgType ,to,geoPt,
			new IAppCallBack<Void>() {
				@Override
				public void done(Void ret, Exception e) {
					controller.buildMessage(content, to, true, geoPt,msgType);
					
				}
			}, null);
	}
	
	/**
	 * @param message Id the id of the message
	 * @param to friend mail
	 * @param content content of the message
	 * @param latLng location of the message
	 * 
	 * add marker of location based message to the map
	 */
	private void addPinToMap(Long messageId, String to, String content, LatLng latLng) {
		
		MarkerOptions options =	new MarkerOptions()
			.position(latLng)
			.snippet("\u200e" + content)
			.title("\u200e" + "From: " + to)
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
		Marker marker = myMap.addMarker(options);
		markerMap.put(marker, messageId);
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
		        	.zoom(11)                   // Sets the zoom
		        	.bearing(0)                // Sets the orientation of the camera to east
		        	.tilt(30)                   // Sets the tilt of the camera to 30 degrees
		        	.build();                   // Creates a CameraPosition from the builder
		        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		        controller.getUsersAroundMe(AppConsts.RADIUS_AROUND_ME, geo, this);
	        }
		}
		else
			Toast.makeText(AroundMeApp.getContext(), "No internet connection available", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void done(final List<UserAroundMe> users, Exception e) {
		if(e == null) {
			usersAroundMe = users;
			for (int i=0; i<users.size(); i++) {
				myMap.addMarker(new MarkerOptions()
	    			.position(new LatLng(users.get(i).getLocation().getLatitude(), users.get(i).getLocation().getLongitude()))
	    			.snippet(users.get(i).getMail())
	    			.title("\u200e" + users.get(i).getDisplayName()));
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
			for (int i=0; i<usersAroundMe.size(); i++) {
				myMap.addMarker(new MarkerOptions()
	    			.position(new LatLng(usersAroundMe.get(i).getLocation().getLatitude(), usersAroundMe.get(i).getLocation().getLongitude()))
	    			.snippet(usersAroundMe.get(i).getMail())
	    			.title("\u200e" + usersAroundMe.get(i).getDisplayName())
	    			.icon(imagesArr.get(i)));
			}
			// add pin messages that you received from friends to the map
			for (Message message : receivedPinMsgs) {
				addPinToMap(message.getId(), controller.getUserNameByMail(message.getFrom()), message.getContnet(), 
						new LatLng(message.getLocation().getLatitude(),message.getLocation().getLongitude()));
			}
		} else {
			
		}
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		
	}
	
   @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mPinMessageReceiver, new IntentFilter("pinMessage"));
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	mGoogleApiClient.disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPinMessageReceiver);
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

	/**
	 *  receiver for getting pin message when the user is in map activity
	 */
	private BroadcastReceiver mPinMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	
	        Long messageId = intent.getLongExtra("pinId",999);
	        dao.open();
	        final ExtendedMessage eMessage = dao.getMessageFromDB(messageId);
	        dao.close();
	        final Message message = eMessage.getMessage();
	        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
	            public void onMapLoaded() {
	    	        addPinToMap(message.getId() ,controller.getUserNameByMail(message.getFrom()), message.getContnet(), new LatLng(message.getLocation().getLatitude(),
	        				message.getLocation().getLongitude()));
	            }
	        });
	    }
	};
	
	/**
	 * @param marker marker to remove which represents pin message on the map
	 * 
	 * remove marker, which it is pin message from friend, from the map 
	 * and update the storage that the message was deleted.
	 */
	private void removePinMessage(Marker marker) {
		dao.open();
		dao.upadteMessageToNonActive(markerMap.get(marker).toString());
		dao.close();
		marker.remove();
	}
	
	/**
	 * @param marker marker to remove which represents pin message on the map
	 * 
	 * @return AlertDialog with an option to delete the chosen marker from the map   
	 */
	private AlertDialog removePinMsgDialog (final Marker marker) {
		AlertDialog.Builder removeMarkerbuilder = new AlertDialog.Builder(AroundMeApp.getContext());
        removeMarkerbuilder.setMessage(R.string.remove_pin_msg_question)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   removePinMessage(marker);
                   }
               })
               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return removeMarkerbuilder.create();
	}
}
