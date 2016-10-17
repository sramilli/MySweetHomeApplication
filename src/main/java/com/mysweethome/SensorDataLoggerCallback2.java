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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysweethome.entity.TemperatureMeasure;
import com.mysweethome.helper.Helper;
import com.mysweethome.model.TemperatureReading;
import com.mysweethome.properties.GardenProperties;

/**
 *
 * @author Ste
 */
public class SensorDataLoggerCallback2 implements MqttCallback{
    
    static Logger logger = LoggerFactory.getLogger(SensorDataLoggerCallback2.class);
    
    TemperatureStore iTemperatureStore;
    ObjectMapper iMapper = new ObjectMapper();
    TemperatureReading iTempReading = null;
            
    public SensorDataLoggerCallback2(){
        iTemperatureStore = TemperatureStore.getInstance();
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        logger.warn("SensorDataLoggerCallback - connectionLost . Not supported yet.");
    }

    @Override
    public void messageArrived(String aTopic, MqttMessage aMessage) throws Exception {
        logger.info("Message received. Topic: [{}]. Message: [{}]", aTopic, aMessage.toString());
        
        //TEMP Babbo Salotto 17.30
        
        /*
         * 		String message = "{"
				+"\"type\": \"TEMP\","
				+"\"group\": \"Babbo\","
				+"\"location\": \"Salotto\","
				+"\"temperature\": \"17.30\""
				+ "}";
				
				{"Type": "TEMP",
				"Group": "Babbo",
				"Location": "Salotto",
				"Temperature": "17.39"}
				
				{"Type": "TEMP", "Group": "Babbo", "Location": "Salotto", "Temperature": "17.39"}

         */

        try {
			iTempReading = iMapper.readValue(aMessage.toString(), TemperatureReading.class);
		} catch (Exception e) {
			logger.error("Error parsing message. Topic: [{}]. Message: [{}]", aTopic, aMessage.toString());
			e.printStackTrace();
			return;
		}

        Date dateRead = Helper.resetSecMillsDate(new Date());
        //TODO witch temperature to store?
        //iTemperatureStore.setLastTemperatureRead(new TemperatureMeasure(messageSplitted[2], messageSplitted[1], dateRead, new Float(messageSplitted[3])));
        if (GardenProperties.PERSIST_TEMPERATURES){
            storeTemperature(new TemperatureMeasure(iTempReading.getLocation(), iTempReading.getGroup(), dateRead, iTempReading.getTemperature()));
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
