package com.satansin.android.compath.logic;

import java.io.Serializable;
import java.util.Comparator;

public class City implements Serializable {
	
	private static final long serialVersionUID = 4958826565443040503L;
	
	private int id;
	private String name;
	private int latitude;
	private int longitude;
	private String province;
	
	public City() {
		this(0, "", 0, 0, "");
	}
	
	public City(int id, String name, int latitude, int longitude, String province) {
		super();
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.setProvince(province);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	
	public static Comparator<City> getComparator() {
		return new Comparator<City>() {
			@Override
			public int compare(City lhs, City rhs) {
				return lhs.name.compareTo(rhs.getName());
			}
		};
	}
	
	public String toString() {
		return name;
	}
	
}
