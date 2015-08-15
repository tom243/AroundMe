package com.aroundme.common;

import com.google.api.client.util.DateTime;

public class ChatMessage {
	public boolean left;
	public String message;
	public boolean locationBased;
	public DateTime dateTime;

	public ChatMessage(boolean left, String message, boolean locationBased, DateTime dateTime) {
		super();
		this.left = left;
		this.message = message;
		this.locationBased = locationBased;
		this.dateTime = dateTime;
	}
}