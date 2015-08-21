package com.aroundme.common;

public class ConversationItem {
	private String userMail;
	private String friendMail;
	private int unreadMess;
	private long timeStamp;
	private String contentMess;
	private String imageUrl;
	private String msgType;
	
	
	public ConversationItem() {
		super();
	}

	/**
	 * @param userMail user mail
	 * @param friendMail friend mail
	 * @param unreadMess number of unread messages
	 * @param timeStamp  date and time of the message
	 * @param contentMess content of the message 
	 * @param msgType determine which type of message
	 */
	public ConversationItem(String userMail, String friendMail, int unreadMess,
			long timeStamp, String contentMess, String msgType) {
		super();
		this.userMail = userMail;
		this.friendMail = friendMail;
		this.unreadMess = unreadMess;
		this.timeStamp = timeStamp;
		this.contentMess = contentMess;
		this.imageUrl = null;
		this.msgType = msgType;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getFriendMail() {
		return friendMail;
	}

	public void setFriendMail(String friendMail) {
		this.friendMail = friendMail;
	}

	public int getUnreadMess() {
		return unreadMess;
	}

	public void setUnreadMess(int unreadMess) {
		this.unreadMess = unreadMess;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getContentMess() {
		return contentMess;
	}

	public void setContentMess(String contentMess) {
		this.contentMess = contentMess;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

}
