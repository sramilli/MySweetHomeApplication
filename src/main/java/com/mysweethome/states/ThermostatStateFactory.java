/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.states;

import com.mysweethome.Thermostat;

/**
 *
 * @author Ste
 */
public class ThermostatStateFactory {
    
    public static ThermostatState createThermostatState(Thermostat aThermostatInstance, State aState){
        
        switch (aState){
            case ON: 
                return new ThermostatStateOn(aThermostatInstance);
            case OFF:
                return new ThermostatStateOff(aThermostatInstance);
            case MANUAL:
                return new ThermostatStateManual(aThermostatInstance);
            default:
                return null;
        }
    }
}
