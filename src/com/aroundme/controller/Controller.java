package com.aroundme.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMeCollection;
import com.aroundme.EndpointApiCreator;
import com.aroundme.common.IAppCallBack;
import com.aroundme.common.IAppCallBack2;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Controller {
	
	private static Controller instance;
	private Aroundmeapi endpoint;
	private User currentUser;
	
	public Controller() {
		try {
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
	
	public void login(final String email, final String pass, final String regId,final IAppCallBack<User> callback) {
		
		new  AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					currentUser = endpoint.login(email,pass,regId).execute();
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
				// call callback
				if(callback!=null)
					callback.done(currentUser, null);
			}

		}.execute();
	}

	public void register(final User user,final IAppCallBack<User> callback) {
		
		new  AsyncTask<Void, Void, Void>() {
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
				// call callback
				if(callback!=null)
					callback.done(user, null);
			}

		}.execute();
	}
	
	public void getUsersAroundMe(final int rad,final GeoPt geo,final IAppCallBack<List<UserAroundMe>> callback) {
		
		new  AsyncTask<Void, Void, UserAroundMeCollection>() {
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
				} catch (Exception e) {
					e.printStackTrace();
				}
				return users;
			}
		
			@Override
			protected void onPostExecute(UserAroundMeCollection users) {
				super.onPostExecute(users);
				// call callback
				if(callback!=null)
					callback.done(users.getItems(), null);
				//getImagesUsersAroundMe(users.getItems(),callback);
			}

		}.execute();
	}


	public void getImagesUsersAroundMe(final List<UserAroundMe> users,final IAppCallBack2<ArrayList<BitmapDescriptor>> callback) {
		
		new  AsyncTask<Void, Void,ArrayList<BitmapDescriptor>>() {
			@Override
			protected ArrayList<BitmapDescriptor> doInBackground(Void... params) {
		 	   ArrayList<BitmapDescriptor> imagesArr = new ArrayList<BitmapDescriptor>(users.size());
		       try {
		    	   for(int i=0; i<users.size(); i++) {
			           String urlImage = users.get(i).getImageUrl();
			           if (urlImage != null){
			        	   URL url = new URL(users.get(i).getImageUrl());
				           Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
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
}
