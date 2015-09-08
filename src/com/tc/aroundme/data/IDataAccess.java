package com.tc.aroundme.data;

import java.util.ArrayList;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.tc.aroundme.common.ConversationItem;
import com.tc.aroundme.common.ExtendedMessage;

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
	ExtendedMessage getMessageFromDB(Long id);

	/**
	 * @param friendMail friend mail
	 * @param userMail user mail
	 * @param messageId message id
	 * @param unreadMsgs unread messages
	 * 
	 * add conversation to conversation table
	 */
	void addToConversationsTable(String friendMail, String userMail, Long messageId, int unreadMsgs);

	/**
	 * @param userMail user mail
	 * @param friendMail friend mail
	 * @return array with all the messages for specific friend
	 * 
	 * get all the messages for specific friend
	 */
	ArrayList<ExtendedMessage> getAllMessagesForFriend(String userMail, String friendMail);

	/**
	 * @param userMail user mail
	 * @return array list of pin messages
	 * 
	 * get all pin messages from list
	 */
	ArrayList<Message> getPinMessages(String userMail);

	/**
	 * @param conv conversation item
	 * @param messageId message id
	 * 
	 * update conversation with new data
	 */
	void updateOpenConversation(ConversationItem conv, Long messageId);

	/**
	 * @param conv conversation item
	 * 
	 * update the number of unread messages
	 */
	void updateUnreadMessages(ConversationItem conv);

	/**
	 * @param conv conversation item
	 * 
	 *  remove conversation from conversations table
	 */
	void removeFromConversationTable(ConversationItem conv);

	/**
	 * @param messageId message id
	 * 
	 * update message to non active
	 */
	void upadteMessageToNonActive(String messageId);
	 
}
