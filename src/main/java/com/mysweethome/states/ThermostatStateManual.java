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
class ThermostatStateManual implements ThermostatState {
    
    Thermostat iThermostat;

    public ThermostatStateManual(Thermostat aThermostatInstance) {
        iThermostat = aThermostatInstance;
        /*
            System.out.println("Switching Thermostat to Manual");
            iHeaterStatusLed.turnOff();
            iHeaterRelay.turnOff();
            iLedGreen.turnOff();
            iLedYellow.turnOn();
            iLedRed.turnOff();
        */
        System.out.println("Switching Thermostat to Manual");
        //TODO . Correct the initial status
        iThermostat.getHeaterStatusLed().turnOff();
        iThermostat.getHeaterRelay().turnOff();
        
        iThermostat.getLedGreen().turnOff();
        iThermostat.getLedYellow().turnOn();
        iThermostat.getLedRed().turnOff();
    }

    @Override
    public void turnON() {
        System.out.println("Turning on");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.ON));
    }

    @Override
    public void turnOFF() {
        System.out.println("Turning off");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.OFF));
    }

    @Override
    public void setToManual() {
        System.out.println("Doing nothing, already in manual mode");
    }
    
    @Override
    public void switchState() {
        System.out.println("Switching state to OFF");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.OFF));
    }
    
    @Override
    public void activateManualThermostat() {
        System.out.println("Switching on manual thermostat");
        iThermostat.getHeaterStatusLed().turnOn();
        iThermostat.getHeaterRelay().turnOn();
    }

    @Override
    public void deActivateManualThermostat() {
        System.out.println("Switching off manual thermostat");
        iThermostat.getHeaterStatusLed().turnOff();
        iThermostat.getHeaterRelay().turnOff();
    }

    @Override
    public State getState() {
        return State.MANUAL;
    }

    @Override
    public void turnONConditionally() {
        System.out.println("Doing nothing, turnONConditionally received while in manual mode");
    }

    @Override
    public void turnOFFConditionally() {
        System.out.println("Doing nothing, turnOFFConditionally received while in manual mode");
    }
    
}
