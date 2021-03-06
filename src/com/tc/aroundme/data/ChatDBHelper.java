package com.tc.aroundme.data;

import com.tc.aroundme.data.ChatDbContract.ConversationsEntry;
import com.tc.aroundme.data.ChatDbContract.MessagesEntry;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class ChatDBHelper extends SQLiteOpenHelper{

	// If you change the database schema, you must increment the database version
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "chatFriends.db";

	public ChatDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create a table to hold the conversations 
		final String SQL_CREATE_CONVERSATIONS_TABLE = "CREATE TABLE "
				+ ConversationsEntry.TABLE_NAME + " (" 
				+ ConversationsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ConversationsEntry.COLUMN_USER_MAIL + " TEXT NOT NULL,"
				+ ConversationsEntry.COLUMN_FRIEND_MAIL + " TEXT NOT NULL,"
				+ ConversationsEntry.COLUMN_LAST_MESSAGE_ID + " LONG,"
				+ ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES+ " INTEGER," 
				+ "UNIQUE(" + ConversationsEntry.COLUMN_USER_MAIL+","+ConversationsEntry.COLUMN_FRIEND_MAIL+"))";
		
		db.execSQL(SQL_CREATE_CONVERSATIONS_TABLE);
		System.out.println("table chat was created.");
		// Create a table to hold the messages
		final String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE "
				+ MessagesEntry.TABLE_NAME + " (" 
				+ MessagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MessagesEntry.COLUMN_CONTENT + " TEXT NOT NULL,"
				+ MessagesEntry.COLUMN_FROM + " TEXT NOT NULL,"
				+ MessagesEntry.COLUMN_TO + " TEXT NOT NULL,"
				+ MessagesEntry.COLUMN_TIME_STAMP+ " LONG,"
				+ MessagesEntry.COLUMN_LAT + " DOUBLE,"
				+ MessagesEntry.COLUMN_LONG + " DOUBLE,"
				+ MessagesEntry.COLUMN_RADIUS + " LONG,"
				+ MessagesEntry.COLUMN_TYPE + " TEXT NOT NULL,"
				+ MessagesEntry.COLUMN_IS_ACTIVE + " INTEGER)" ;
		
		db.execSQL(SQL_CREATE_MESSAGES_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + ConversationsEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MessagesEntry.TABLE_NAME);
		onCreate(db);

	}
	
}
