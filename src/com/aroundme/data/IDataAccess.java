package com.aroundme.data;

import java.util.ArrayList;

import com.aroundme.ConversationItem;
import com.aroundme.UserItem;

import android.database.SQLException;

/**
 * Interface for the DAO class
 *
 */
public interface IDataAccess {

	/**
	 * open a connection to the data base
	 * @throws SQLException
	 */
	void open() throws SQLException;
	
	/**
	 * close the connection to the data base
	 */
	void close();
	
	/**
	 * the function retruns the list from the data base 
	 * @return the list form the data base 
	 */
	ArrayList<ConversationItem> getAllOpenConversationsList(String currentUserMail);

	public boolean isConversationExist(String userMail,String friendMail);
	
	
	
	
	
	
	
	/**
	 * add task to the data base 
	 * @param task the task we want to add to the data base 
	 * @return the task we added
	 */
//	Task addTask(Task task);
	
	/**
	 * remove task to the data base 
	 * @param task  the task we want to remove from the data base
	 */
//	void removeTask(Task task);
	
	/**
	 * update the task in the data base 
	 * @param task the task that we want to update 
	 */
//	void updateTask(Task task);
	
	/** 
	 * change the status of the task to done or undone in the data base
	 * @param task the task that we want to update 
	 */
//	void changeTaskStatus(Task task);
}
