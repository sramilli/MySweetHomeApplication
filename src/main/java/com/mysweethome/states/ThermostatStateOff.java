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
class ThermostatStateOff implements ThermostatState {
    
    Thermostat iThermostat;

    public ThermostatStateOff(Thermostat aThermostatInstance) {
        iThermostat = aThermostatInstance;
        /*
            System.out.println("Switching Thermostat to Off");
            iHeaterStatusLed.turnOff();
            iHeaterRelay.turnOff();
            iLedGreen.turnOff();
            iLedYellow.turnOff();
            iLedRed.turnOn();
        */
        System.out.println("Switching Thermostat to Off");
        iThermostat.getHeaterStatusLed().turnOff();
        iThermostat.getHeaterRelay().turnOff();
        iThermostat.getLedGreen().turnOff();
        iThermostat.getLedYellow().turnOff();
        iThermostat.getLedRed().turnOn();
        
    }

    @Override
    public void turnON() {
        System.out.println("Turning on");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.ON));
    }

    @Override
    public void turnOFF() {
        System.out.println("Doing nothing, already OFF");
    }

    @Override
    public void setToManual() {
        System.out.println("Setting to manual");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.MANUAL));
    }
    
    @Override
    public void switchState() {
        System.out.println("Switching state to ON");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.ON));
    }

    @Override
    public void activateManualThermostat() {
        System.out.println("Switching on manual thermostat. Doing nothing because thermostat OFF");
    }

    @Override
    public void deActivateManualThermostat() {
        System.out.println("Switching off manual thermostat. Doing nothing because OFF");
    }
    
    @Override
    public State getState() {
        return State.OFF;
    }

    @Override
    public void turnONConditionally() {
        turnON();
    }

    @Override
    public void turnOFFConditionally() {
        turnOFF();
    }
}
