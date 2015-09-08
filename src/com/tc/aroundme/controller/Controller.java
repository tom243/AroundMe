package com.tc.aroundme.controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMeCollection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.api.client.util.DateTime;
import com.tc.aroundme.EndpointApiCreator;
import com.tc.aroundme.GCMActivity;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.AroundMeApp;
import com.tc.aroundme.common.ConversationItem;
import com.tc.aroundme.common.IAppCallBack;
import com.tc.aroundme.common.IAppCallBack2;
import com.tc.aroundme.common.SplashInterface;
import com.tc.aroundme.data.DAO;
import com.tc.aroundme.data.IDataAccess;

/**
 * @author Tomer and chen
 * 
 * Main controller of the APP
 *
 */
public class Controller {
	
	private static Controller instance;
	private final SharedPreferences mPrefs;
	private Aroundmeapi endpoint;
	private User currentUser;
	private HashMap<String, UserAroundMe> allUsers = null;
	private List<UserAroundMe> allUsersList = null;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_REG_ID = "registration_id";
    private IDataAccess dao; 
	
	public Controller() {
		allUsers = new HashMap<String, UserAroundMe>();
		allUsersList = new ArrayList<UserAroundMe>();
		dao = DAO.getInstance(AroundMeApp.getContext());
		mPrefs = AroundMeApp.getContext().getSharedPreferences(AppConsts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		try {
			EndpointApiCreator.initialize(null);
			endpoint = EndpointApiCreator.getApi(Aroundmeapi.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Controller getInstance() {
		if(instance ==  null)
			instance = new Controller();
		return instance;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public HashMap<String, UserAroundMe> getAllUsers() {
		return allUsers;
	}
	
	public List<UserAroundMe> getAllUsersList() {
		return allUsersList;
	}

	/**
	 * @param email email of the user
	 * @param pass  password of the user
	 * @param regId registration id of device
	 * @param callback callback 
	 * @param splash  splash
	 * 
	 * login for user
	 */
	public void login(final String email, final String pass, final String regId,final IAppCallBack<User> callback, final SplashInterface splash) {
		

		new AsyncTask<Void, Void, Void>() {
			private Exception e;
			@Override
			protected void onPreExecute() {
				splash.visible(null);
				super.onPreExecute();
			}
			@Override
			protected Void doInBackground(Void... params) {
				try {
					currentUser = endpoint.login(email,pass,regId).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
					this.e=e;;
				} catch (Exception e) {
					e.printStackTrace();
					this.e=e;
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				splash.unvisible(null);
				// call callback
				if(callback!=null)
					callback.done(currentUser, e);
			}
		}.execute();
	}

	/**
	 * @param user user that want to register
	 * @param callback callback
	 * @param splash splash
	 * 
	 * register for user
	 */
	public void register(final User user,final IAppCallBack<User> callback, final SplashInterface splash) {
		
		new  AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				splash.visible(null);
				super.onPreExecute();
			}
			@Override
			protected Void doInBackground(Void... params) {
				try {
					endpoint.register(user).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				splash.unvisible(null);
				// call callback
				if(callback!=null)
					callback.done(user, null);
			}
		}.execute();
	}
	
	/**
	 * @param rad radius of the users around me
	 * @param geo user current location
	 * @param callback callback
	 * 
	 * get all users around the user by specific radios
	 */
	public void getUsersAroundMe(final int rad,final GeoPt geo,final IAppCallBack<List<UserAroundMe>> callback) {
		
		new  AsyncTask<Void, Void, UserAroundMeCollection>() {
			private Exception e;
			@Override
			protected UserAroundMeCollection doInBackground(Void... params) {
				UserAroundMeCollection users = null;
				try {
					// report the current user location
					endpoint.reportUserLocation(currentUser.getMail(), geo).execute();
					// get all users around me
					users = endpoint.getUsersAroundMe(geo.getLatitude(), geo.getLongitude(), rad, currentUser.getMail()).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
					this.e = e;
				} catch (Exception e) {
					e.printStackTrace();
					this.e = e;
				}
				return users;
			}
			@Override
			protected void onPostExecute(UserAroundMeCollection users) {
				super.onPostExecute(users);
				// call callback
				if(callback!=null)
					callback.done(users.getItems(), e);
			}
		}.execute();
	}
	
	/**
	 * @param callback callback
	 * @param splash splash
	 * 
	 * get all friends from server
	 */
	public void getAllUsersFromServer(final IAppCallBack<List<UserAroundMe>> callback, final SplashInterface splash) {
		
		new  AsyncTask<Void, Void, UserAroundMeCollection>() {
			@Override
			protected void onPreExecute() {
				splash.visible(null);
				super.onPreExecute();
			}
			@Override
			protected UserAroundMeCollection doInBackground(Void... params) {
				UserAroundMeCollection allUsers = null;
				try {
					// get all users
					allUsers = endpoint.getAllUsers(currentUser.getMail()).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return allUsers;
			}
			@Override
			protected void onPostExecute(UserAroundMeCollection users) {
				// update all-users hash map
				SharedPreferences.Editor prefs = mPrefs.edit();
				for (UserAroundMe user : users.getItems()) { 
					allUsers.put(user.getMail(),user);
					prefs.putString(user.getMail(), user.getDisplayName());
				}
				prefs.commit();
				allUsersList = users.getItems();		
				if (splash!=null)
					splash.unvisible(null);
				// call callback
				if(callback!=null)
					callback.done(users.getItems(), null);
			}
		}.execute();
	}

	/**
	 * @param users list of all the users
	 * @param callback callback
	 * 
	 * get all the images for the users in map
	 */
	public void getImagesUsersAroundMe(final List<UserAroundMe> users,final IAppCallBack2<ArrayList<BitmapDescriptor>> callback) {
		
		new  AsyncTask<Void, Void,ArrayList<BitmapDescriptor>>() {
			
			@Override
			protected ArrayList<BitmapDescriptor> doInBackground(Void... params) {
		 	   ArrayList<BitmapDescriptor> imagesArr = new ArrayList<BitmapDescriptor>(users.size());
		       try {
		    	   for(int i=0; i<users.size(); i++) {
			           String urlImage = users.get(i).getImageUrl();
			           if (urlImage != null){
				           int index = urlImage.indexOf('=');
				           if (index > 0){
				        	   urlImage = urlImage.substring(0, index+1);
				        	   urlImage= urlImage + "100";
				           }
			        	   URL url = new URL(urlImage);
				           Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				           bmp = getRoundedShape(bmp);
				           imagesArr.add(i,BitmapDescriptorFactory.fromBitmap(bmp));
			           } else
			        	   imagesArr.add(i,null);
		    	   }
		        } catch (IOException e) {
		            // Log exception
		            return null;
		        }
		       return imagesArr;
			}			
			@Override
			protected void onPostExecute(ArrayList<BitmapDescriptor> imagesArr) {
				super.onPostExecute(imagesArr);
				// call callback
				if(callback!=null)
					callback.done2(imagesArr, null);
			}
		}.execute();
	}

	/**
	 * @param content content of the message
	 * @param type type of the message
	 * @param to friend email address to send to
	 * @param geoPt location of the message
	 * @param callback callback
	 * @param splash splash
	 * 
	 * send message to user 
	 */
	public void sendMessageToUser(final String content,final String type,final String to,final GeoPt geoPt,
					final IAppCallBack<Void> callback, final SplashInterface splash) {
		
		new  AsyncTask<Void, Void, Void>(){
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (splash != null)
					splash.visible(null);
			}
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Message message = new Message();
					message.setFrom(currentUser.getMail());
					message.setTo(to);
					message.setTimestamp(new DateTime(new Date()));
					if (geoPt != null){
						message.setLocation(geoPt);
						message.setReadRadius(AppConsts.BUILDING_RADIUS_METERS);
					}
					JSONObject pnObj = new JSONObject();
		            pnObj.put("content", content);
	            	pnObj.put("type", type);
	            	message.setContnet(pnObj.toString());
					endpoint.sendMessage(message).execute();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (splash != null)
					splash.unvisible(null);
				// call callback
				if(callback!=null)
					callback.done(null,null);
			}
		}.execute();
	}
	
	/**
	 * @param mail mail of some friend 
	 * @return friend name
	 * 
	 * get friend name by mail
	 */
	public String getUserNameByMail(String mail) {
		if (!allUsers.isEmpty())
			return allUsers.get(mail).getDisplayName();
		else 
			return null;
	}

	/**
	 * @param mail  mail of some friend 
	 * @return image URL of friend
	 * 
	 *  get the image URL of friend
	 */
	public String getImageUrlByMail(String mail) {
		if (!allUsers.isEmpty())
			return allUsers.get(mail).getImageUrl();
		else 
			return null;
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	public void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i("", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing registration ID is not guaranteed to work with
	    // the new app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i("", "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the registration ID in your app is up to you.
	    return context.getSharedPreferences(GCMActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	public void clear() {
		instance = null;
	}
	/**
	 * @return true if Internet connection is available otherwise it returns
	 *         false
	 */
	
	/**
	 * @param context the received context
	 * @return if connected to the Internet
	 * 
	 * Determine if the user is connected to the Internet
	 */
	public boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	/**
	 * @param message message to be added to DB
	 * @param msgType message type
	 * @return id of the message in DB
	 * 
	 * adds message to DB and returned the id in DB
	 */
	public Long addMessageToDB(Message message, String msgType){
		dao.open();
		Long id = dao.addToMessagesTable(message,msgType);
		dao.close();
		return id;
	}
	
	/**
	 * @param to friend mail
	 * @param from user mail
	 * @param messageId message id
	 * @param incUnreadMsgs determine if need to increment unread messages
	 * @param resetDate determine if need to reset date
	 * @param resetUnreadMsgs  determine if need to reset unread messages
	 * 
	 *  update or add conversation to open conversations
	 */
	public void updateConversationTable(String to, String from, Long messageId, boolean incUnreadMsgs, boolean resetDate, boolean resetUnreadMsgs){
		dao.open();
		ConversationItem conv = dao.isConversationExist(to,from);
		if (conv != null) {
			System.out.println("Conversation exist");
			if (incUnreadMsgs)
				conv.setUnreadMess(conv.getUnreadMess()+1);
			if (resetDate)
				conv.setTimeStamp(new DateTime(new Date()).getValue());
			dao.updateOpenConversation(conv, messageId); // update row in data-base
		}
		else {
			System.out.println("Conversation not exist");
			int unreadMsgs = 1 ;
			if (resetUnreadMsgs) 
				unreadMsgs = 0;
			dao.addToConversationsTable(from, to, messageId, unreadMsgs);
		}
		dao.close();
		Intent updateIntent = new Intent("updateOpenCoversationsAdapter");
	    LocalBroadcastManager.getInstance(AroundMeApp.getContext()).sendBroadcast(updateIntent);
	}
	
	/**
	 * @param mContent content of the message
	 * @param mTo friend mail
	 * @param isLocationBased determine if its location based message 
	 * @param geoPt the location of the message
	 * @param msgType the type of the message
	 * 
	 * build a message and add it to DB
	 */
	public void buildMessage(String mContent, String mTo, boolean isLocationBased, GeoPt geoPt, String msgType) {
		Message message = new Message();
		message.setContnet(mContent);
		message.setFrom(getCurrentUser().getMail());
		message.setTo(mTo);
		message.setTimestamp(new DateTime(new Date()));
		if (isLocationBased) {
			message.setLocation(geoPt);
			message.setReadRadius(80);
		}
		// add the new message to messages table in db 
   		Long messageId = addMessageToDB(message,msgType);	
   		// update the conversations table in db with the last message
   		updateConversationTable(message.getFrom(), message.getTo(), messageId, false, false, isLocationBased);
	}
	
	/**
	 * @param dateTimeInMillis date in milliseconds
	 * @return date as String
	 * 
	 *  convert date in milliseconds to date
	 */
	public String dateToDateString(long dateTimeInMillis) {
		Date date = new Date(dateTimeInMillis);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
	    return formatter.format(dateTimeInMillis);
	}
	
	/**
	 * @param dateTimeInMillis time in milliseconds
	 * @return time as String
	 * 
	 *  convert time in milliseconds to date
	 */
	public String dateToTimeString(long dateTimeInMillis) {
		Date date = new Date(dateTimeInMillis);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
	    return formatter.format(dateTimeInMillis);
	}

	/**
	 * @param bitmap picture of users
	 * @return rounded shape image
	 * 
	 * return rounded shape image
	 */
	private Bitmap getRoundedShape(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
	            .getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 50;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	}
	
	/**
	 * @param json contains the content and the type of the message
	 * @return content of the message
	 * @throws JSONException
	 * 
	 * get the content of the message from JSON
	 */
	public String getContentFromJson(String json) throws JSONException {
		JSONObject jObject = new JSONObject(json);
		return jObject.getString("content");
	}
	
	/**
	 * @param json contains the content and the type of the message
	 * @return type of the message
	 * @throws JSONException
	 * 
	 * get the type of the message from JSON
	 */
	public String getTypeFromJson(String json) throws JSONException {
		JSONObject jObject = new JSONObject(json);
		return jObject.getString("type");
	}
	
}
