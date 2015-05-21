package com.aroundme.data;

import java.util.ArrayList;

import com.aroundme.ConversationItem;
import com.aroundme.data.ChatDbContract.ConversationsEntry;
import com.aroundme.data.ChatDbContract.MessagesEntry;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * the function purpose is to perform a requests to the data base 
 *
 */
public class DAO implements IDataAccess{

	private static DAO instance;
	private Context context;
	private ChatDBHelper dbHelper;
	
	private String[] messagesColumns = { MessagesEntry._ID, MessagesEntry.COLUMN_CONTENT,
			MessagesEntry.COLUMN_FROM,MessagesEntry.COLUMN_TO,MessagesEntry.COLUMN_TIME_STAMP, 
			MessagesEntry.COLUMN_LAT, MessagesEntry.COLUMN_LONG, MessagesEntry.COLUMN_RADIUS};
	private String[] conversationsColumns = { ConversationsEntry._ID, ConversationsEntry.COLUMN_USER_MAIL,
			ConversationsEntry.COLUMN_FRIEND_MAIL,ConversationsEntry.COLUMN_LAST_MESSAGE_ID,
			ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES}; 
	// Database fields
	private SQLiteDatabase db;
	
	DAO(Context context) {
		this.context = context;
		dbHelper = new ChatDBHelper(context);
	}
	
	/**
	 * single tone of the DAO class 
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
	public ArrayList<ConversationItem> getAllOpenConversationsList(String currentUserMail) {
		ArrayList<ConversationItem> openConversations = new ArrayList<ConversationItem>();
		Cursor cursor = db.rawQuery("SELECT " + ConversationsEntry.COLUMN_USER_MAIL + "," +
				ConversationsEntry.COLUMN_FRIEND_MAIL + "," + 
				ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES + "," +
				MessagesEntry.COLUMN_TIME_STAMP + "," + MessagesEntry.COLUMN_CONTENT +		
				" FROM "+ ConversationsEntry.TABLE_NAME +  
				" INNER JOIN " + MessagesEntry.TABLE_NAME +  
				" ON " + ConversationsEntry.COLUMN_LAST_MESSAGE_ID + "=" + MessagesEntry.TABLE_NAME +  
				"." + MessagesEntry._ID +   
				" WHERE " + ConversationsEntry.COLUMN_USER_MAIL + "=?" , new String[]{currentUserMail});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ConversationItem conv = cursorToConversationItem(cursor);
			openConversations.add(conv);
			cursor.moveToNext();
		}
		return openConversations;
	}
		
	public ConversationItem cursorToConversationItem(Cursor cursor) {
		ConversationItem conv = new ConversationItem();
		conv.setUserMail(cursor.getString(cursor.getColumnIndex(ConversationsEntry.COLUMN_USER_MAIL)));
		conv.setFriendMail(cursor.getString(cursor.getColumnIndex(ConversationsEntry.COLUMN_FRIEND_MAIL)));
		conv.setUnreadMess(cursor.getInt(cursor.getColumnIndex(ConversationsEntry.COLUMN_COUNTER_UNREAD_MESSAGES)));
		conv.setTimeStamp(cursor.getLong(cursor.getColumnIndex(MessagesEntry.COLUMN_TIME_STAMP)));
		conv.setContentMess(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_CONTENT)));
		return conv;
	}	
	
	public boolean isConversationExist(String userMail,String friendMail) {
	/*	Cursor cursor = db.query(ConversationsEntry.TABLE_NAME, conversationsColumns, 
				conversationsColumns[1] == userMail AND conversationsColumns[2] == friendMail
				, null, null, null, null);
	*/
		Cursor cursor = db.rawQuery("SELECT * FROM " + ConversationsEntry.TABLE_NAME + 
				" WHERE " + ConversationsEntry.COLUMN_USER_MAIL + "=? AND " + 
				ConversationsEntry.COLUMN_FRIEND_MAIL + "=?" , new String[]{userMail,friendMail});
		
		if (cursor == null)
			return false;
		
		return true;
		
		/*
		 if (cursor.moveToFirst())
		  	return true;
		  return false;
		 */
		
	}
	
/*
	@Override
	public ArrayList<Task> getTaskList() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(MessagesEntry.TABLE_NAME, tasksColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
				tasks.add(t);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return tasks;
	}
*/

	
/*	@Override
	public Task addTask(Task task) {
		Log.i("DAO", "addTask");
		
		if (task == null)
			return null;
		//build the content values.
		ContentValues values = putValues(task);
		
		//do the insert.
		long insertId = database.insert(MessagesEntry.TABLE_NAME, null, values);
		Log.i("DAO: addTask","insertId: "+insertId);
		
		//get the entity from the data base - extra validation, entity was insert properly.
		Cursor cursor = database.query(MessagesEntry.TABLE_NAME, tasksColumns,
				MessagesEntry._ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		//create the task object from the cursor.
		Task newTask = cursorToTask(cursor);
		cursor.close();
		return newTask;
	}
*/
	
/*	@Override
	public void removeTask(Task task) {
		long id = task.getId();
		database.delete(MessagesEntry.TABLE_NAME, MessagesEntry._ID + " = " + id,null);
	}
*/
	
/*	@Override
	public void updateTask(Task task) {
		long id= task.getId();
		ContentValues values = putValues(task);
		database.update(MessagesEntry.TABLE_NAME, values, MessagesEntry._ID + " = " + id, null);
	}
*/
	
/*	@Override
	public void changeTaskStatus(Task task){
		long id= task.getId();
		ContentValues values = new ContentValues();
		values.put(MessagesEntry.COLUMN_Status,task.getStatus()); 
		database.update(MessagesEntry.TABLE_NAME, values, MessagesEntry._ID + " = " + id, null);
	}
*/
	/**
	 * crate content values form a task parameters
	 * @param task the task we want  to get the parameters from it
	 * @return the values that we will need to crate in the table 
	 */
/*	public ContentValues putValues(Task task) {
		ContentValues values = new ContentValues();
		values.put(MessagesEntry.COLUMN_TASK_DESC, task.getItemDescription());
		values.put(MessagesEntry.COLUMN_DateTime, task.getCalendarInMillis());
		values.put(MessagesEntry.COLUMN_Status,task.getStatus());
		values.put(MessagesEntry.COLUMN_Alarm, task.isAlarm());
		values.put(MessagesEntry.COLUMN_Importance, task.isImportance());
		values.put(MessagesEntry.COLUMN_Geo, task.isLocation());
		values.put(MessagesEntry.COLUMN_Address, task.getAddress());
		return values;
	}
*/
	
}