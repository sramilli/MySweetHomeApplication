/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import java.util.Date;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.entity.TemperatureMeasure;
import com.mysweethome.helper.Helper;
import com.mysweethome.properties.GardenProperties;

/**
 *
 * @author Ste
 */
public class SensorDataLoggerCallback implements MqttCallback{
    
    static Logger logger = LoggerFactory.getLogger(SensorDataLoggerCallback.class);
    
    TemperatureStore iTemperatureStore;
            
    public SensorDataLoggerCallback(){
        iTemperatureStore = TemperatureStore.getInstance();
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        throw new UnsupportedOperationException("SensorDataLoggerCallback - connectionLost . Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void messageArrived(String aTopic, MqttMessage aMessage) throws Exception {
        logger.info("Message received. Topic: [{}]. Message: [{}]", aTopic, aMessage);
        
        String[] messageSplitted = aMessage.toString().trim().split(" ");
        //TEMP Anna Temp_pumps 23.01
        //TEMP Babbo Salotto 17.30
        if (messageSplitted.length != 4) return;
        Date dateRead = Helper.resetSecMillsDate(new Date());
        //TODO witch temperature to store?
        //iTemperatureStore.setLastTemperatureRead(new TemperatureMeasure(messageSplitted[2], messageSplitted[1], dateRead, new Float(messageSplitted[3])));
        if ("TEMP".equals(messageSplitted[0]) && GardenProperties.PERSIST_TEMPERATURES){
            storeTemperature(new TemperatureMeasure(messageSplitted[2], messageSplitted[1], dateRead, new Float(messageSplitted[3])));
        }
        
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /////////////////////////
    
    private void storeTemperature(TemperatureMeasure aTemperatureMeasure) {
        // 24 byte each. 1 MB -> 41666 measures.
        iTemperatureStore.storeTemperature(aTemperatureMeasure);
    }
    
}
