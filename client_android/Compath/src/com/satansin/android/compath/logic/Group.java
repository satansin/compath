package com.satansin.android.compath.logic;

import java.util.Calendar;

public class Group {
	
	private int id;
	private String title;
	private Calendar createTime;
	private String ownerName;
	private int numberOfMembers;
	private String location;

	public Group() {
		this(0, "", Calendar.getInstance(), "", 0, "");
	}
	
	public Group(int id, String title, Calendar createTime,
			String ownerName, int numberOfMembers, String location) {
		super();
		this.id = id;
		this.title = title;
		this.createTime = createTime;
		this.ownerName = ownerName;
		this.numberOfMembers = numberOfMembers;
		this.location = location;
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
	
	public Calendar getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
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

}
