package com.mysweethome.model;

public class Reading {

	private String iType;
	private String iGroup;
	private String iLocation;
	private ESP8266Status iESP8266Status;

	public Reading() {
		super();
	}

	public String getType() {
		return iType;
	}

	public void setType(String aType) {
		this.iType = aType;
	}

	public String getGroup() {
		return iGroup;
	}

	public void setGroup(String aGroup) {
		this.iGroup = aGroup;
	}

	public String getLocation() {
		return iLocation;
	}

	public void setLocation(String aLocation) {
		this.iLocation = aLocation;
	}

	public ESP8266Status getESP8266Status() {
		return iESP8266Status;
	}

	public void setESP8266Status(ESP8266Status aESP8266Status) {
		this.iESP8266Status = aESP8266Status;
	}

}