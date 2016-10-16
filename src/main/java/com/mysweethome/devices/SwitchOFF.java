/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mysweethome.devices;

import com.mysweethome.devices.Button;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ste
 */
public class SwitchOFF extends Button implements GpioPinListenerDigital{
    static Logger logger = LoggerFactory.getLogger(SwitchOFF.class);
    
    private boolean iJustTerminateApp = false;
    private boolean iShutdownPi = false;
    
    public SwitchOFF(int aPin){
        super(aPin);
        super.getPin().addListener(this);
    }
    
    public boolean justTerminateApp(){
        return iJustTerminateApp;
    }
    
    public boolean shutdownPi(){
        return iShutdownPi;
    }
    
    //TODO do i need these
    //and getPin() ?
    public void close(){
        super.getPin().removeAllListeners();
        super.close();
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if (PinState.HIGH.equals(event.getState())){
            logger.info("Switch OFF detected. Event: [{}], Class: [{}], EventType: [{}], Pin [{}], Source [{}], State [{}] ", 
                    event, event.getClass(), event.getEventType(), event.getPin(), event.getSource(), event.getState());
            //One push shuts down the Pi. Two pushes just exits the java application.
            if (iShutdownPi){
                iJustTerminateApp = true;
            }
            iShutdownPi = true;
        }
    }
    
}
