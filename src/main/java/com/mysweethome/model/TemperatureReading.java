package com.mysweethome.model;

public class TemperatureReading {
	//Object Mapper	//http://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
	
	//TEMP Babbo Salotto 17.30
	private String iType;
	private String iGroup;
	private String iLocation;
	private float iTemperature;
	
	public TemperatureReading(){
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

	public float getTemperature() {
		return iTemperature;
	}

	public void setTemperature(float aTemperature) {
		this.iTemperature = aTemperature;
	}
	
}
