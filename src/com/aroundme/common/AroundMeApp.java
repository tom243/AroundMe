package com.aroundme.common;

import android.app.Application;
import android.content.Context;

public class AroundMeApp extends Application {
	 //private static AroundMeApp instance;
	 private static Context mContext;

	   /* public static AroundMeApp getInstance() {
	        return instance;
	    }*/

	    public static Context getContext() {
	      //  return instance.getApplicationContext();
	      return mContext;
	    }

	    @Override
	    public void onCreate() {
	        super.onCreate();
	        //instance = this;
	        mContext = getApplicationContext();    
	    }
}