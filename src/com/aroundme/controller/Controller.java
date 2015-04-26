package com.aroundme.controller;

import java.io.IOException;
import android.os.AsyncTask;
import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.aroundme.EndpointApiCreator;
import com.aroundme.common.MyCallback;

public class Controller {
	
	private MyCallback callback;
	private Aroundmeapi endpoint;
	private User user;
	
	public Controller() {
		//callback = new SignInActivity();
		try {
			endpoint = EndpointApiCreator.getApi(Aroundmeapi.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login(final String email, final String pass, final String regId) {
		
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
				callback.loginCallback(user);
			}

		}.execute();
	}

	public void register(final User user) {
		
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
				callback.registerCallback(user);
			}

		}.execute();
	}
	
}
