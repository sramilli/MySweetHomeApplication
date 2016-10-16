/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mysweethome.devices;

import com.mysweethome.helper.Pi4jHelper;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ste
 */
public class Relay {
    static Logger logger = LoggerFactory.getLogger(Relay.class);
    /*private GPIOPin iRelay;
    private boolean iInitialStatus = true;*/
    
    GpioController gpio = GpioFactory.getInstance();
    GpioPinDigitalOutput pin;
    
    public Relay(int aPin){
        pin = gpio.provisionDigitalOutputPin(Pi4jHelper.getPin(aPin), "PIN "+aPin, PinState.HIGH);
        pin.setShutdownOptions(true, PinState.HIGH);
    }
    
    public void turnOn(){
        //gpio.high();
        setValue(false);
    }
    
    public void turnOff(){
        //gpio.low();
        setValue(true);
    }
    
    public void setValue(boolean aValue){
        pin.setState(aValue);
        logger.info("Turn relay "+ (!aValue ? "on." : "off."));
    }
    
    public void close(){
        if (gpio != null){
            gpio.shutdown();
        }
    }
    
}
