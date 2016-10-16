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
public class User {
    
    private String iID;
    private String iName;
    private String iMobileNr;
    private String iEmail;
    
    public User(String aID, String aName, String aMobileNr, String aEmail){
        iID = aID;
        iName = aName;
        iMobileNr = aMobileNr;
        iEmail = aEmail;
    }

    public String getID() {
        return iID;
    }
    
    public String getName() {
        return iName;
    }

    public String getMobileNr() {
        return iMobileNr;
    }

    public String getEmail() {
        return iEmail;
    }
    
    public boolean hasValidMobileNr(){
        //TODO to enhance
        if (iMobileNr == null || "".equals(iMobileNr)) return false;
        return true;
    }
    
    public boolean hasValidEmail(){
        //TODO to enhance
        if (iEmail == null || "".equals(iEmail)) return false;
        return true;
    }
    
}
