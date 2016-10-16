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
class GSMDataInterpreter {
    
    public static GSMCommand getCommand(String aString){
        if (aString == null || "".equals(aString)) return GSMCommand.UNKNOW;
        aString = aString.trim();
        if (aString.startsWith(GSMCommand.MESSAGE_ARRIVED.toString())) return GSMCommand.MESSAGE_ARRIVED;
        else if (aString.startsWith(GSMCommand.READ_ALL_MESSAGES.toString())) return GSMCommand.READ_ALL_MESSAGES;
        
        return GSMCommand.UNKNOW;
    }
    
}
