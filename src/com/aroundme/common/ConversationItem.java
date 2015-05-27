package com.aroundme.common;

public class ConversationItem {
	private String userMail;
	private String friendMail;
	private int unreadMess;
	private long timeStamp;
	private String contentMess;
	private String imageUrl;
	
	
	public ConversationItem() {
		super();
	}

	public ConversationItem(String userMail, String friendMail, int unreadMess,
			long timeStamp, String contentMess) {
		super();
		this.userMail = userMail;
		this.friendMail = friendMail;
		this.unreadMess = unreadMess;
		this.timeStamp = timeStamp;
		this.contentMess = contentMess;
		this.imageUrl = null;
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

	

}
