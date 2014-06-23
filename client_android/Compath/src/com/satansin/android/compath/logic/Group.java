package com.satansin.android.compath.logic;

public class Group {
	
	private int id;
	private String title;
	private long lastActiveTime;
	private String ownerName;
	private int numberOfMembers;
	private String location;
	/**
	 * ÓÃ»§Í·ÏñURL
	 */
	private String iconUrl;

	public Group() {
		this(0, "", 0, "", 0, "", "");
	}
	
	public Group(int id, String title, long lastActiveTime,
			String ownerName, int numberOfMembers, String location, String iconUrl) {
		super();
		this.id = id;
		this.title = title;
		this.lastActiveTime = lastActiveTime;
		this.ownerName = ownerName;
		this.numberOfMembers = numberOfMembers;
		this.location = location;
		this.setIconUrl(iconUrl);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public long getLastActiveTime() {
		return lastActiveTime;
	}
	
	public void setLastActiveTime(long createTime) {
		this.lastActiveTime = createTime;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public int getNumberOfMembers() {
		return numberOfMembers;
	}
	
	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
