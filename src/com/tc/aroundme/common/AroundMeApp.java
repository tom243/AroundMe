package com.tc.aroundme.common;

import android.app.Application;
import android.content.Context;

public class AroundMeApp extends Application {
	private static boolean isChatOpen;
	private static String friendWithOpenChat;
	private static Context mContext;

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

	/**
	 * @return status if chat is open
	 */
	public static boolean isChatOpen() {
		return isChatOpen;
	}

	/**
	 * @param isChatOpen determine if chat is open
	 */
	public static void setChatOpen(boolean isChatOpen) {
		AroundMeApp.isChatOpen = isChatOpen;
	}

	/**
	 * @return the friend that currently open in chat
	 */
	public static String getFriendWithOpenChat() {
		return friendWithOpenChat;
	}

	/**
	 * @param friendWithOpenChat  determine which user is currently is opened in chat
	 */
	public static void setFriendWithOpenChat(String friendWithOpenChat) {
		AroundMeApp.friendWithOpenChat = friendWithOpenChat;
	}
	    
}