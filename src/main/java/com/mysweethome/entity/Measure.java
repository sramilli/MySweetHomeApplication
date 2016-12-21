package com.mysweethome.entity;

import java.util.Date;

public class Measure {

	protected String iLocation;
	protected String iGroup;
	protected Date iDate;

	public Measure() {
		super();
	}

	public String getLocation() {
	    return iLocation;
	}

	public void setLocation(String aLocation) {
	    this.iLocation = aLocation;
	}

	public String getGroup() {
	    return iGroup;
	}

	public void setGroup(String aGroup) {
	    this.iGroup = aGroup;
	}

	public Date getDate() {
	    return iDate;
	}

	public void setDate(Date aDate) {
	    this.iDate = aDate;
	}

}