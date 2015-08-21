package com.aroundme.data;

import java.util.ArrayList;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.ExtendedMessage;

import android.database.SQLException;

/**
 * Interface for the DAO class
 *
 */
public interface IDataAccess {

	/**
	 * open a connection to the data base
	 * 
	 * @throws SQLException
	 */
	void open() throws SQLException;

	/**
	 * close the connection to the data base
	 */
	void close();

	/**
	 * @return the list form the data base
	 * 
	 * the function returns the list from the data base
	 */
	ArrayList<ConversationItem> getAllOpenConversationsList(
			String currentUserMail);

	/**
	 * @param userMail user mail
	 * @param friendMail friend mail
	 * @return conversation item if exists or null
	 * 
	 * check if conversation is exists or not
	 */
	ConversationItem isConversationExist(String userMail, String friendMail);

	/**
	 * @param message details of the message
	 * @param msgType message type
	 * @return message id
	 * 
	 * add message to messages table
	 */
	Long addToMessagesTable(Message message, String msgType);

	/**
	 * @param id id of the message
	 * @return message object
	 * 
	 * get message form DB by id
	 */
	Message getMessageFromDB(Long id);

	/**
	 * @param id id of the message
	 * @return message type
	 * 
	 * get message type by id
	 */
	String getTypeMsg(Long id);

	void addToConversationsTable(String friendMail, String userMail, Long messageId, int unreadMsgs);

	ArrayList<ExtendedMessage> getAllMessagesForFriend(String userMail, String friendMail);

	ArrayList<Message> getPinMessages(String userMail, String column);

	void updateOpenConversation(ConversationItem conv, Long messageId);

	void updateUnreadMessages(ConversationItem conv);

	void removeFromConversationTable(ConversationItem conv);

	void upadteMessageToNonActive(String messageId);
	 
}
