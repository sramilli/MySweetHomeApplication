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
public class Led {
    static Logger logger = LoggerFactory.getLogger(Led.class);

    private GpioController gpio = GpioFactory.getInstance();
    private GpioPinDigitalOutput iPin;
    
    public Led(int aPin){
        iPin = gpio.provisionDigitalOutputPin(Pi4jHelper.getPin(aPin), "PIN "+aPin, PinState.LOW);
        iPin.setShutdownOptions(true, PinState.LOW);
        logger.info("Initialized Led on pin [{}].", aPin);
    }
    
    public Led(int aPin, boolean aInitialStatusHigh){
        GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(Pi4jHelper.getPin(aPin), "PIN "+aPin, getInitialStatus(aInitialStatusHigh));
        pin.setShutdownOptions(true, getInitialStatus(aInitialStatusHigh));
        logger.info("Initialized Led on pin [{}].", aPin);
    }
    
    public void turnOn(){
        //iPin.high();
        setValue(true);
    }
    
    public void turnOff(){
        //iPin.low();
        setValue(false);
    }
    
    public void setValue(boolean aValue){
        logger.info("Turn led [{}]"+ (aValue ? " on." : " off."), iPin.getName());
        iPin.setState(aValue);
    }
    
    public void close(){
        if (iPin != null){
            iPin.removeAllListeners();
            //NB!! Removes everything! Every pin!
            if (gpio != null){
                gpio.shutdown();  
            }
        }
    }
    
    private PinState getInitialStatus(boolean aInitialStatusHigh) {
        if (aInitialStatusHigh) return PinState.HIGH;
        else return PinState.LOW;
    }
}
