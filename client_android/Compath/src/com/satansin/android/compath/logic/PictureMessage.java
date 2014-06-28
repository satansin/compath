package com.satansin.android.compath.logic;

import android.graphics.Bitmap;

public class PictureMessage {
	
	private int id;
	private Bitmap pic;
	private long time;
	private boolean isComingMsg;
	private String from;
	private int groupId;
	
	public PictureMessage(int id, Bitmap pic,long time, boolean isComingMessage, String from, int groupId){
		this.id = id;
		this.pic = pic;
		this.time = time;
		this.isComingMsg = isComingMessage;
		this.from = from;
		this.groupId = groupId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	
	

}
