package com.aroundme.common;

import com.google.api.client.util.DateTime;

public class ChatMessage {
	private boolean left;
	private String message;
	private boolean locationBased;
	private DateTime dateTime;

	public ChatMessage(boolean left, String message, boolean locationBased, DateTime dateTime) {
		super();
		this.left = left;
		this.message = message;
		this.locationBased = locationBased;
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

	public boolean isLocationBased() {
		return locationBased;
	}

	public void setLocationBased(boolean locationBased) {
		this.locationBased = locationBased;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
}