package com.aroundme.common;

import com.google.api.client.util.DateTime;

/**
 * @author Tomer and chen
 * 
 * the chat message
 *
 */
public class ChatMessage {
	private boolean left;
	private String message;
	private String msgType;
	private DateTime dateTime;

	/**
	 * @param left determine if its send or receive message
	 * @param message the content of the message
	 * @param msgType determine which type of message
	 * @param dateTime time of the message
	 */
	public ChatMessage(boolean left, String message, String msgType, DateTime dateTime) {
		super();
		this.left = left;
		this.message = message;
		this.msgType = msgType;
		this.dateTime = dateTime;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
}