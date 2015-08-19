package com.aroundme.data;

import java.util.ArrayList;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.ConversationItem;

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

	ConversationItem isConversationExist(String userMail,String friendMail);
	
	 Long addToMessagesTable(Message message, String msgType);
	
	 Message getMessageFromDB(Long id);
	
	 void  addToConversationsTable(String  friendMail, String userMail, Long messageId, int unreadMsgs);
	
	 ArrayList<Message> getAllMessagesForFriend(String userMail,String friendMail);
	 
	 ArrayList<Message> getPinMessages(String userMail, String column);
	 
	 void updateOpenConversation(ConversationItem conv, Long messageId);
	 
	 void updateUnreadMessages(ConversationItem conv);
	 
	 void removeFromConversationTable(ConversationItem conv);
	 
	 void removeFromMessagesTable(Message message);
	 
}
