/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.entity;

/**
 *
 * @author Ste
 */
public class Pump implements IActivable{
    
    private String iTopic;          //individuates the individual pump
    private int iCalibration;       //how many milliseconds to get one unit (dl)
    private int iDefaultQuantity;   //how many units
    private boolean iPlugged;

    
    public Pump(String aTopic, int aCalibration, int aDefaultQuantity){
        iTopic = aTopic;
        iCalibration = aCalibration;
        iDefaultQuantity = aDefaultQuantity;
        iPlugged = true;
    }
    
    public String getTopic(){
        return iTopic;
    }

    public int getCalibration() {
        return iCalibration;
    }

    public int getDefaultQuantity() {
        return iDefaultQuantity;
    }

    public boolean isPlugged() {
        return iPlugged;
    }

    public void setPlugged(boolean aPlugged) {
        this.iPlugged = aPlugged;
    }
    
    
}
