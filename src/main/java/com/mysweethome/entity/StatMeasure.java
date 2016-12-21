package com.mysweethome.entity;

import java.util.Date;

import com.mysweethome.helper.Helper;

public class StatMeasure extends Measure{
	
	private long iAge;

	public StatMeasure(String aLocation, String aGroup, Date aDate, long aAge) {
		super();
        iLocation = aLocation;
        iGroup = aGroup;
        iDate = aDate;
        iAge = aAge;
    }
	
	public long getAge() {
		return iAge;
	}

	public void setAge(long aAge) {
		this.iAge = aAge;
	}
	
    public String toString(){
        return "[Group: "+getGroup()
                +", Location: "+getLocation()
                +", Date: "+Helper.getDateAsString(getDate())
                +", Age: "+getAge()
                +"]";
    }
	
    @Override
    public boolean equals (Object o){
        if (o instanceof StatMeasure == false) {return false;}
        if (iDate == null || iLocation == null || iGroup == null) {return false;}
        StatMeasure tMeasure = (StatMeasure)o;
        if (iDate.equals(tMeasure.getDate()) && iLocation.equalsIgnoreCase(tMeasure.getLocation()) && iGroup.equalsIgnoreCase(tMeasure.getGroup())) {
            return true;
        }
        return false;
    }

}
