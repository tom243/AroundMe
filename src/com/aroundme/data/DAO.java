package com.aroundme.data;

import java.util.ArrayList;
import com.aroundme.UserItem;
import com.aroundme.data.ChatDbContract.ConversationsEntry;
import com.aroundme.data.ChatDbContract.MessagesEntry;
import android.content.Context;
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
	public ArrayList<UserItem> getAllOpenConversationsList(String currentUserMail) {
		ArrayList<UserItem> openConversations = new ArrayList<UserItem>();
		db.rawQuery("SELECT " + ConversationsEntry.COLUMN_FRIEND_MAIL+ 
				" FROM "+ ConversationsEntry.TABLE_NAME +  
				" INNER JOIN " + MessagesEntry.TABLE_NAME +  
				" ON " + ConversationsEntry.COLUMN_LAST_MESSAGE_ID + "=" + MessagesEntry._ID +   
				" WHERE " + ConversationsEntry.COLUMN_USER_MAIL + "=?" , new String[]{currentUserMail});
		return openConversations;
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
	
	/**
	 * Create task object from the cursor.
	 * @param cursor the cursor the received
	 * @return the task we ask for 
	 */
/*	private Task cursorToTask(Cursor cursor) {
		Task t = new Task();
		t.setId(cursor.getInt(cursor.getColumnIndex(MessagesEntry._ID)));
		Log.i("ID-TASK","IS :   "+cursor.getInt(cursor.getColumnIndex(MessagesEntry._ID)));
		t.setItemDescription(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_TASK_DESC)));
		t.setCalendarInMillis(cursor.getLong(cursor.getColumnIndex(MessagesEntry.COLUMN_DateTime)));
		t.setStatus(cursor.getInt(cursor.getColumnIndex(MessagesEntry.COLUMN_Status)));
		if (cursor.getInt(cursor.getColumnIndex(MessagesEntry.COLUMN_Alarm))==1)
			t.setAlarm(true);
		else 
			t.setAlarm(false);
		if (cursor.getInt(cursor.getColumnIndex(MessagesEntry.COLUMN_Importance))==1)
			t.setImportance(true);
		else 
			t.setImportance(false);
		if (cursor.getInt(cursor.getColumnIndex(MessagesEntry.COLUMN_Geo))==1) {
			t.setLocation(true);
			t.setAddress(cursor.getString(cursor.getColumnIndex(MessagesEntry.COLUMN_Address)));
		}else 
			t.setLocation(false);
		return t;
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