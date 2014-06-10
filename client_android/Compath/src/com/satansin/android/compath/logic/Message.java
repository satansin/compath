package com.satansin.android.compath.logic;

public class Message {
	
	private int id;
	private String content;
	private long time;
	private boolean isComingMsg;
	private String from;
	
	public Message() {
		this(0, "", 0, false, "");
	}
	
	public Message(int id, String content, long time, boolean isComingMsg, String from) {
		super();
		this.id = id;
		this.content = content;
		this.time = time;
		this.isComingMsg = isComingMsg;
		this.from = from;
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

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Message other = (Message) obj;
//		if (content == null) {
//			if (other.content != null)
//				return false;
//		} else if (!content.equals(other.content))
//			return false;
//		if (from == null) {
//			if (other.from != null)
//				return false;
//		} else if (!from.equals(other.from))
//			return false;
//		if (isComingMsg != other.isComingMsg)
//			return false;
//		if (time == null) {
//			if (other.time != null)
//				return false;
//		} else if (!time.equals(other.time))
//			return false;
//		return true;
//	}

}
