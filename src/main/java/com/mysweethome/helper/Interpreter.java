/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mysweethome.helper;

import com.mysweethome.CommandType;
import com.mysweethome.entity.SMS;

/**
 *
 * @author Ste
 */
public class Interpreter {
    
    private Interpreter() {
    }
    
    public static Interpreter getInstance() {
        return InterpreterHolder.INSTANCE;
    }
    
    private static class InterpreterHolder {
        private static final Interpreter INSTANCE = new Interpreter();
    }
    
    public CommandType interprete(SMS aSMS){
        String tText = new String(aSMS.getText());
        String tTemp;
        //simple word commands
        //to cout words try str.split("\\w+").length
        if (tText == null || "".equals(tText)){
            System.out.println("Interpreter: Command = null");
            return CommandType.NOT_VALID;
        }
        
        CommandType tCommandType = null;
        tText = tText.trim();
        if (tText.equalsIgnoreCase(CommandType.ON.toString())) tCommandType = CommandType.ON;
        else if (tText.equalsIgnoreCase(CommandType.OFF.toString())) tCommandType = CommandType.OFF;
        else if (tText.equalsIgnoreCase(CommandType.MANUAL.toString())) tCommandType = CommandType.MANUAL;
        else if (tText.equalsIgnoreCase(CommandType.STATUS.toString())) tCommandType = CommandType.STATUS;
        else if (tText.equalsIgnoreCase(CommandType.HELP.toString())) tCommandType = CommandType.HELP;
        else if (tText.length() >= 8 && (tTemp = tText.substring(0, 8)).equalsIgnoreCase(CommandType.REGISTER_NUMBER.toString())) tCommandType =  CommandType.REGISTER_NUMBER;
        else if (tText.length() >= 12 && (tTemp = tText.substring(0, 12)).equalsIgnoreCase(CommandType.PROGRAM_DAILY.toString())) tCommandType =  CommandType.PROGRAM_DAILY;
        else if (tText.length() >= 7 && (tTemp = tText.substring(0, 7)).equalsIgnoreCase(CommandType.PROGRAM.toString())) tCommandType =  CommandType.PROGRAM;
        
        if (tCommandType != null){
            System.out.println("Interpreter: Command : "+tCommandType.toString());
            return tCommandType;
        }
        else {
            System.out.println("Interpreter: Command not supported: "+tText);
            return CommandType.NOT_VALID;
        }
    }
}
