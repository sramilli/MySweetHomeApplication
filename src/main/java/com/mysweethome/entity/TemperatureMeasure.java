/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.entity;

import java.util.Date;

import com.mysweethome.helper.Helper;

/**
 *
 * @author Ste
 */
public class TemperatureMeasure extends Measure {
    float iTemp;

    public TemperatureMeasure(String aLocation, String aGroup, Date aDate, float aTemp) {
        iLocation = aLocation;
        iGroup = aGroup;
        iDate = aDate;
        iTemp = aTemp;
    }
    
    public String toString(){
        return "[Group: "+getGroup()
                +", Location: "+getLocation()
                +", Date: "+Helper.getDateAsString(getDate())
                +", Temp: "+Helper.getTempAsString(getTemp())
                +"]";
    }

    public float getTemp() {
        return iTemp;
    }

    public void setTemp(float aTemp) {
        this.iTemp = aTemp;
    }
    
    @Override
    public boolean equals (Object o){
        if (o instanceof TemperatureMeasure == false) {return false;}
        if (iDate == null || iLocation == null || iGroup == null) {return false;}
        TemperatureMeasure t = (TemperatureMeasure)o;
        if (iDate.equals(t.getDate()) && iLocation.equalsIgnoreCase(t.getLocation()) && iGroup.equalsIgnoreCase(t.getGroup())) {
            return true;
        }
        return false;
    }
    
}
