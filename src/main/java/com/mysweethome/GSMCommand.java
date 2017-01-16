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
enum GSMCommand {
	MESSAGE_ARRIVED("+CMTI:"),
    MESSAGE_ARRIVED_SIM900("+CMTI:"),
    MESSAGE_ARRIVED_A6("+CMT:"),
    AT("AT"),
    READ_ALL_MESSAGES("AT+CMGL=\"ALL\""),
    READ_MESSAGE(""),
    DELETE_MESSAGE(""),
    UNKNOW("unknown");
    
    String iCommand;
    
    GSMCommand(String aCommand){
        iCommand = aCommand;
    }
    
    public String toString(){
        return iCommand;
    }
    
}

