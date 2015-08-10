package com.aroundme.common;

public class ChatMessage {
	public boolean left;
	public String message;
	public boolean locationBased;

	public ChatMessage(boolean left, String message, boolean locationBased) {
		super();
		this.left = left;
		this.message = message;
		this.locationBased = locationBased;
	}
}