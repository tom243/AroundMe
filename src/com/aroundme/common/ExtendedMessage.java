package com.aroundme.common;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;

public class ExtendedMessage {

	private String msgType;
	private Message message;
	
	public ExtendedMessage() {
		super();
	}
	public ExtendedMessage(String msgType, Message message) {
		super();
		this.msgType = msgType;
		this.message = message;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
}
