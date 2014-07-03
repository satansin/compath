package com.satansin.android.compath.logic;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = -501055407252132710L;
	
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_PIC = 2;
	
	public static final int STATE_SENDING = 1;
	public static final int STATE_SENT = 2;
	public static final int STATE_FAILED = 3;
	
	
	private int id;
	private int type;
	private String content;
	private long time;
	private boolean isComingMsg;
	private String from;
	private int groupId;
	private String iconUrl;
	private int sendingState;
	
	public Message() {
		this(0, 1, "", 0, false, "", 0, "", 1);
	}
	
	public Message(int id, int type, String content, long time, boolean isComingMsg, String from, int groupId, String iconUrl, int sendingState) {
		super();
		this.id = id;
		this.setType(type);
		this.content = content;
		this.time = time;
		this.isComingMsg = isComingMsg;
		this.from = from;
		this.groupId = groupId;
		this.setIconUrl(iconUrl);
		this.setSendingState(sendingState);
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isComingMsg() {
		return isComingMsg;
	}

	public void setComingMsg(boolean isComingMsg) {
		this.isComingMsg = isComingMsg;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSendingState() {
		return sendingState;
	}

	public void setSendingState(int sendingState) {
		this.sendingState = sendingState;
	}

}
