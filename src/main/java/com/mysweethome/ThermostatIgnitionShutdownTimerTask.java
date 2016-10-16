/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author Ste
 */
public class ThermostatIgnitionShutdownTimerTask extends TimerTask{
    
    Thermostat iThermostat;
    CommandType iCommandType;
    
    public ThermostatIgnitionShutdownTimerTask(Thermostat aThermostat, CommandType aCommandType){
        iThermostat = aThermostat;
        iCommandType = aCommandType;
    }

    @Override
    public void run() {
        iThermostat.executeCommand(iCommandType, null);
    }
    
}
