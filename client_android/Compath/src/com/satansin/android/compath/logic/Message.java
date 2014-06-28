package com.satansin.android.compath.logic;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = -501055407252132710L;
	
	private int id;
	private String content;
	private long time;
	private boolean isComingMsg;
	private String from;
	private int groupId;
	private String iconUrl;
	
	public Message() {
		this(0, "", 0, false, "", 0, "");
	}
	
	public Message(int id, String content, long time, boolean isComingMsg, String from, int groupId, String iconUrl) {
		super();
		this.id = id;
		this.content = content;
		this.time = time;
		this.isComingMsg = isComingMsg;
		this.from = from;
		this.groupId = groupId;
		this.setIconUrl(iconUrl);
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

}
