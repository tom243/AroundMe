package com.aroundme.data;

import android.provider.BaseColumns;

/**
 * Defines two tables and column names for the chat database.
 */

public class ChatDbContract {
		 
		 public static final class ConversationsEntry implements BaseColumns {

		        // Table name
		        public static final String TABLE_NAME = "conversations";
		        
		        // Column names
		        public static final String COLUMN_USER_MAIL = "user_mail";
		        public static final String COLUMN_FRIEND_MAIL = "friend_mail";
		        public static final String COLUMN_LAST_MESSAGE_ID = "last_message";
		        public static final String COLUMN_COUNTER_UNREAD_MESSAGES = "unread_messages";

		    }
		 
		 /* Inner class that defines the table contents of all tasks */
		 public static final class MessagesEntry implements BaseColumns {

		        // Table name
		        public static final String TABLE_NAME = "messages";
		        
		        // Column names
		        public static final String COLUMN_CONTENT = "content";
		        public static final String COLUMN_FROM = "from_user";
		        public static final String COLUMN_TO = "to_user";
		        public static final String COLUMN_TIME_STAMP= "time_stamp";
		        public static final String COLUMN_LAT = "latitude";
		        public static final String COLUMN_LONG = "longtitude";
		        public static final String COLUMN_RADIUS = "radius";
		        public static final String COLUMN_TYPE = "type";
		        public static final String COLUMN_IS_ACTIVE = "is_active";

		    }
 }
