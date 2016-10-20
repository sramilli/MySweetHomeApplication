package com.mysweethome.model;

public class TemperatureReading extends Reading {
	//Object Mapper	//http://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
	//TEMP Babbo Salotto 17.30
	
	private float iTemperature;
	
	public TemperatureReading(){
		super();
	}

	public float getTemperature() {
		return iTemperature;
	}

	public void setTemperature(float aTemperature) {
		this.iTemperature = aTemperature;
	}
	
}
