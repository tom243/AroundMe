package com.aroundme.controller;

import java.io.IOException;
import android.os.AsyncTask;
import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.aroundme.EndpointApiCreator;
import com.aroundme.common.IAppCallBack;

public class Controller {
	
	private static Controller instance;
	private Aroundmeapi endpoint;
	private User user;
	
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
	
	public void login(final String email, final String pass, final String regId,final IAppCallBack<User> callback) {
		
		new  AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					user = endpoint.login(email,pass,regId).execute();
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
	/*
	public void getAllUsersAroundMe(int rad,IAppCallBack<List<UserAroundMe>> callback)
	{
		Aroundmeapi api;
		try {
			api = EndpointApiCreator.getApi(Aroundmeapi.class);
			UserAroundMeCollection ret = api.getUsersAroundMe(0f, 0f, rad, "cadan").execute();
			if(callback!=null)
				callback.done(ret.getItems(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
	
}
