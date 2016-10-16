/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

/**
 *
 * @author Ste
 */
public enum CommandType {

    //one word
    ON("on"),
    ON_CONDITIONAL("onConditional"),
    OFF("off"),
    OFF_CONDITIONAL("offConditional"),
    MANUAL("manual"),
    STATUS("status"),
    HELP("help"),
    REGISTER_NUMBER("register"),
    PROGRAM_DAILY("programDaily"),
    PROGRAM("program"),
    //multiple word
    //TODO
    PROGRAM_WEEK("programWeekly"),
    PROGRAM_OFF("program"),
    NOT_VALID("notValid");

    String iCommand;

    CommandType(String aString) {
        iCommand = aString;
    }
    
    public String toString(){
        return iCommand;
    }
    
    public boolean isActive(){
        boolean valid = false;
        if (this.equals(ON) || 
            this.equals(MANUAL) || 
            this.equals(OFF) || 
            this.equals(STATUS) || 
            this.equals(HELP) || 
            this.equals(REGISTER_NUMBER) ||
            this.equals(PROGRAM_DAILY) ||
            this.equals(PROGRAM))
            valid = true;
        return valid;
    }
}
