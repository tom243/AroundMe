package com.aroundme.controller;

import java.io.IOException;
import java.util.List;

import android.os.AsyncTask;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMeCollection;
import com.aroundme.EndpointApiCreator;
import com.aroundme.common.IAppCallBack;

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
			}

		}.execute();
	}
	
}
