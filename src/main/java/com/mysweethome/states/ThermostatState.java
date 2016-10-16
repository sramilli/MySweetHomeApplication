package com.mysweethome.states;

/**
 * States: ON, OFF, MANUAL
 */

public interface ThermostatState {
    
    void turnON();
    void turnONConditionally();
    void turnOFF();
    void turnOFFConditionally();
    void setToManual();
    void switchState();
    void activateManualThermostat();
    void deActivateManualThermostat();
    State getState();
    
}
