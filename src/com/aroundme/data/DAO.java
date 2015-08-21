package com.aroundme.data;


import java.util.ArrayList;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.GeoPt;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.AppConsts;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.ExtendedMessage;
import com.aroundme.data.ChatDbContract.ConversationsEntry;
import com.aroundme.data.ChatDbContract.MessagesEntry;
import com.google.api.client.util.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 *  perform a requests to the DB 
 *
 */
public class DAO implements IDataAccess{

	private static DAO instance;
	private Context context;
	private ChatDBHelper dbHelper;
	
	private String[] messagesColumns = { MessagesEntry._ID, MessagesEntry.COLUMN_CONTENT,
			MessagesEntry.COLUMN_FROM,MessagesEntry.COLUMN_TO,MessagesEntry.COLUMN_TIME_STAMP, 
			MessagesEntry.COLUMN_LAT, MessagesEntry.COLUMN_LONG, MessagesEntry.COLUMN_RADIUS,
			MessagesEntry.COLUMN_TYPE};
	private String[] conversationsColumns = { ConversationsEntry._ID, ConversationsEntry.COLUMN_USER_MAIL,
			ConversationsEntry.COLUMN_FRIEND_MAIL,ConversationsEntry.COLUMN_LAST_MESSAGE_ID,
			ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES}; 

	private SQLiteDatabase db; 	// Database fields
	
	DAO(Context context) {
		this.context = context;
		dbHelper = new ChatDBHelper(context);
	}
	
	/**
	 * singletone of the DAO class 
	 * @param context the context that received 
	 * @return instance of the DAO
	 */
	public static DAO getInstance(Context context)
	{
		if(instance ==  null)
			instance = new DAO(context);
		return instance;
	}
	
	@Override
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	@Override
	public void close() {
		dbHelper.close();
	}
	
	@Override
	public ArrayList<ExtendedMessage> getAllMessagesForFriend(String userMail,String friendMail) {
		// get history of conversation
		ArrayList<ExtendedMessage> messages = new ArrayList<ExtendedMessage>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + MessagesEntry.TABLE_NAME + 
				" WHERE (" + MessagesEntry.COLUMN_TO + "=? AND " + 
				MessagesEntry.COLUMN_FROM+ "=?) OR (" + MessagesEntry.COLUMN_FROM + "=? AND " + 
				MessagesEntry.COLUMN_TO + "=?) ORDER BY " + MessagesEntry.COLUMN_TIME_STAMP ,new String[]{userMail,friendMail,userMail,friendMail});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ExtendedMessage eMessage = cursorToExtendedMessage(cursor);
			messages.add(eMessage);
			cursor.moveToNext();
		}
		cursor.close();
		return messages;
	}

	@Override
	public ArrayList<Message> getPinMessages(String userMail) {
		ArrayList<Message> messages = new ArrayList<Message>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + MessagesEntry.TABLE_NAME + 
				" WHERE (" + MessagesEntry.COLUMN_TO + "=? AND " +
				MessagesEntry.COLUMN_TYPE + "=? AND " +
				MessagesEntry.COLUMN_IS_ACTIVE + "=1) ORDER BY " + MessagesEntry.COLUMN_LAT + " DESC"
				,new String[]{userMail,AppConsts.TYPE_PIN_MSG});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message message = cursorToMessage(cursor);
			messages.add(message);
			cursor.moveToNext();
		}
		cursor.close();
		return messages;
	}
	
	
	/**
	 * @param cursor cursor for DB
	 * @return message object
	 * 
	 * build message for columns
	 */
	private Message cursorToMessage(Cursor cursor) {
		Message message = new Message();
		GeoPt geoPt = null;
		message.setId(cursor.getLong(cursor.getColumnIndex(MessagesEntry._ID)));
		message.setContnet(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_CONTENT)));
		message.setFrom(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_FROM)));
		message.setTo(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_TO)));
		message.setTimestamp(new DateTime(cursor.getLong(cursor.getColumnIndex(MessagesEntry.COLUMN_TIME_STAMP))));
		/*********** TALK WITH TOMER ************/
		Double latitude = cursor.getDouble(cursor.getColumnIndex(MessagesEntry.COLUMN_LAT));
		Double longitude = cursor.getDouble(cursor.getColumnIndex(MessagesEntry.COLUMN_LONG));
		if (latitude != 0 && longitude != 0) {
			geoPt = new GeoPt();
			geoPt.setLatitude(latitude.floatValue());
			geoPt.setLongitude(longitude.floatValue());
		}
		message.setLocation(geoPt);
		message.setReadRadius(cursor.getInt(cursor.getColumnIndex(MessagesEntry.COLUMN_RADIUS)));
		return message;
	}

	/**
	 * @param cursor cursor for DB
	 * @return extended message object
	 * 
	 * build extended message for columns
	 */
	private ExtendedMessage cursorToExtendedMessage(Cursor cursor) {
		ExtendedMessage eMessage = new ExtendedMessage();
		Message message = cursorToMessage(cursor);
		eMessage.setMessage(message);
		eMessage.setMsgType(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_TYPE)));
		return eMessage;
	}
	
	@Override
	public ArrayList<ConversationItem> getAllOpenConversationsList(String currentUserMail) {
		ArrayList<ConversationItem> openConversations = new ArrayList<ConversationItem>();
		Cursor cursor = db.rawQuery("SELECT " + ConversationsEntry.COLUMN_USER_MAIL + "," +
				ConversationsEntry.COLUMN_FRIEND_MAIL + "," + 
				ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES + "," +
				MessagesEntry.COLUMN_TIME_STAMP + "," + MessagesEntry.COLUMN_CONTENT + "," +  
				MessagesEntry.COLUMN_LAT + "," + MessagesEntry.COLUMN_TYPE +
				" FROM "+ ConversationsEntry.TABLE_NAME +  
				" INNER JOIN " + MessagesEntry.TABLE_NAME +  
				" ON " + ConversationsEntry.COLUMN_LAST_MESSAGE_ID + "=" + MessagesEntry.TABLE_NAME +  
				"." + MessagesEntry._ID +   
				" WHERE " + ConversationsEntry.COLUMN_USER_MAIL + "=?" + "ORDER BY " + MessagesEntry.COLUMN_TIME_STAMP + " DESC" , new String[]{currentUserMail});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ConversationItem conv = cursorToConversationItem(cursor);
			openConversations.add(conv);
			cursor.moveToNext();
		}
		cursor.close();
		return openConversations;
	}
		
	/**
	 * @param cursor cursor cursor for DB
	 * @return conversation item object
	 * 
	 * build a conversation item from columns
	 */
	private ConversationItem cursorToConversationItem(Cursor cursor) {
		ConversationItem conv = new ConversationItem();
		conv.setUserMail(cursor.getString(cursor.getColumnIndex(ConversationsEntry.COLUMN_USER_MAIL)));
		conv.setFriendMail(cursor.getString(cursor.getColumnIndex(ConversationsEntry.COLUMN_FRIEND_MAIL)));
		conv.setUnreadMess(cursor.getInt(cursor.getColumnIndex(ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES)));
		conv.setTimeStamp(cursor.getLong(cursor.getColumnIndex(MessagesEntry.COLUMN_TIME_STAMP)));
		conv.setContentMess(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_CONTENT)));
		Long latitude = cursor.getLong(cursor.getColumnIndex(MessagesEntry.COLUMN_LAT));
		String lastMsgType = cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_TYPE));
		conv.setMsgType(lastMsgType);
		return conv;
	}	
	
	@Override
	public ConversationItem isConversationExist(String userMail,String friendMail) {
		Cursor cursor = db.rawQuery("SELECT * FROM " + ConversationsEntry.TABLE_NAME + 
				" WHERE " + ConversationsEntry.COLUMN_USER_MAIL + "=? AND " + 
				ConversationsEntry.COLUMN_FRIEND_MAIL + "=?" , new String[]{userMail,friendMail});

		System.out.println("the cursor count is: " + cursor.getCount());
		if(cursor.getCount() <= 0){
            return null;
        }
		else{
			cursor.moveToFirst();
			ConversationItem conv = new ConversationItem();
			conv.setUserMail(cursor.getString(cursor.getColumnIndex(ConversationsEntry.COLUMN_USER_MAIL)));
			conv.setFriendMail(cursor.getString(cursor.getColumnIndex(ConversationsEntry.COLUMN_FRIEND_MAIL)));
			conv.setUnreadMess(cursor.getInt(cursor.getColumnIndex(ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES)));
			return conv;
		}
	}
	
	@Override
	public Long addToMessagesTable(Message message, String msgType) {	
		
		if (message == null)
			return null;
		
		ContentValues values = putMessagesValues(message, msgType); //build the content values.
		long insertId = db.insert(MessagesEntry.TABLE_NAME, null, values); //do the insert.
		
		return insertId;
	}

	@Override
	public ExtendedMessage getMessageFromDB(Long id){
		Cursor cursor =  db.rawQuery("select * from " + MessagesEntry.TABLE_NAME + " where " + MessagesEntry._ID + "='" + id + "'" , null);
		cursor.moveToFirst();
		ExtendedMessage eMessage = cursorToExtendedMessage(cursor);
		
		cursor.close();
		return eMessage;
	}
	
	@Override
	public void  addToConversationsTable(String  friendMail, String userMail, Long messageId, int unreadMsgs) {		
		
		ContentValues values = putConversationsValues(friendMail, userMail, messageId,unreadMsgs); //build the content values.
		long insertId = db.insert(ConversationsEntry.TABLE_NAME, null, values); //do the insert.
		
		//get the entity from the data base - extra validation, entity was insert properly.
		Cursor cursor = db.query(ConversationsEntry.TABLE_NAME, conversationsColumns,
				ConversationsEntry._ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		//create the task object from the cursor.
		cursor.close();
	}
	
	@Override
	public void updateOpenConversation(ConversationItem conv , Long messageId) {
		ContentValues values = putConversationsValues(conv.getFriendMail(), conv.getUserMail(), messageId, conv.getUnreadMess());
		db.update(ConversationsEntry.TABLE_NAME, values, ConversationsEntry.COLUMN_USER_MAIL + "=? AND " + 
				ConversationsEntry.COLUMN_FRIEND_MAIL + "=?",  new String[] {conv.getUserMail(), conv.getFriendMail()});
	}
	
	@Override
	public void updateUnreadMessages(ConversationItem conv) {
		ContentValues values = new ContentValues();
		values.put(ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES, conv.getUnreadMess());
		db.update(ConversationsEntry.TABLE_NAME, values, ConversationsEntry.COLUMN_USER_MAIL + "=? AND " + 
				ConversationsEntry.COLUMN_FRIEND_MAIL + "=?",  new String[] {conv.getUserMail(), conv.getFriendMail()});
	}
	
	@Override
	public void removeFromConversationTable(ConversationItem conv) {
		db.delete(ConversationsEntry.TABLE_NAME, ConversationsEntry.COLUMN_USER_MAIL + "=? AND " + 
				ConversationsEntry.COLUMN_FRIEND_MAIL + "=?",  new String[] {conv.getUserMail(), conv.getFriendMail()});
	}
	
	@Override
	public void upadteMessageToNonActive(String messageId) {
		ContentValues values = new ContentValues();
		values.put(MessagesEntry.COLUMN_IS_ACTIVE, 0);
		db.update(MessagesEntry.TABLE_NAME, values, MessagesEntry._ID + "=?", new String[] {messageId});
	}
	
	/**
	 * 
	 * @param message the message we want  to get the parameters from it
	 * @return the values that we will need to crate in the table 
	 * 
	 * create content values form a message parameters
	 */
	private ContentValues putMessagesValues(Message message, String msgType) {
		ContentValues values = new ContentValues();
		values.put(MessagesEntry._ID, message.getId());
		values.put(MessagesEntry.COLUMN_CONTENT, message.getContnet());
		values.put(MessagesEntry.COLUMN_FROM, message.getFrom());
		values.put(MessagesEntry.COLUMN_TO,message.getTo());
		values.put(MessagesEntry.COLUMN_TIME_STAMP, message.getTimestamp().getValue());
		if (message.getLocation() != null){ 
			values.put(MessagesEntry.COLUMN_LAT, message.getLocation().getLatitude());
			values.put(MessagesEntry.COLUMN_LONG, message.getLocation().getLongitude());
		}
		values.put(MessagesEntry.COLUMN_RADIUS, message.getReadRadius());
		values.put(MessagesEntry.COLUMN_TYPE, msgType);
		
		values.put(MessagesEntry.COLUMN_IS_ACTIVE, 1);
		return values;
	}
	
	/**
	 * @return the values that we will need to crate in the table 
	 * 
	 * crate content values form a conversation parameters
	 */
	private ContentValues putConversationsValues(String  friendMail, String userMail, Long messageId, int unreadMessages) {
		ContentValues values = new ContentValues();
		values.put(ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES, unreadMessages);
		values.put(ConversationsEntry.COLUMN_FRIEND_MAIL, friendMail);
		values.put(ConversationsEntry.COLUMN_LAST_MESSAGE_ID,messageId);
		values.put(ConversationsEntry.COLUMN_USER_MAIL,  userMail);
		return values;
	}

}