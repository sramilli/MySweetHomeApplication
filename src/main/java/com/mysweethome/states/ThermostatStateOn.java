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
class ThermostatStateOn implements ThermostatState {
    
    Thermostat iThermostat;

    public ThermostatStateOn(Thermostat aThermostatInstance) {
        iThermostat = aThermostatInstance;
        /*
            System.out.println("Switching Thermostat to On");
            iHeaterStatusLed.turnOn();
            iHeaterRelay.turnOn();
            iLedGreen.turnOn();
            iLedYellow.turnOff();
            iLedRed.turnOff();
        */
        System.out.println("Switching Thermostat to On");
        iThermostat.getHeaterStatusLed().turnOn();
        iThermostat.getHeaterRelay().turnOn();
        iThermostat.getLedGreen().turnOn();
        iThermostat.getLedYellow().turnOff();
        iThermostat.getLedRed().turnOff();
    }

    @Override
    public void turnON() {
        System.out.println("Doing nothing, already ON");
    }

    @Override
    public void turnOFF() {
        System.out.println("Turning off");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.OFF));
    }

    @Override
    public void setToManual() {
        System.out.println("Setting to manual");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.MANUAL));
    }

    @Override
    public void switchState() {
        System.out.println("Switching state to manual");
        iThermostat.setThermostatState(ThermostatStateFactory.createThermostatState(iThermostat, State.MANUAL));
    }
    
    @Override
    public void activateManualThermostat() {
        System.out.println("Switching on manual thermostat. Doing nothing because thermostat ON");
    }

    @Override
    public void deActivateManualThermostat() {
        System.out.println("Switching off manual thermostat. Doing nothing because ON");
    }
    
    @Override
    public State getState() {
        return State.ON;
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
