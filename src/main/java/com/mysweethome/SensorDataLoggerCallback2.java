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
import com.mysweethome.helper.JsonHelper;
import com.mysweethome.model.TemperatureReading;
import com.mysweethome.properties.MySweetHomeProperties;

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
				
				{"type": "TEMP",
				"group": "Babbo",
				"location": "Salotto",
				"temperature": "17.39"}
				
				{"type": "TEMP", "group": "Babbo", "location": "Salotto", "temperature": "17.39"}
				{"type": "TEMP", "group": "Ste", "location": "Salotttto", "temperature": "17.39"}
				"{\"type\": \"TEMP\", \"group\": \"Ste\", \"location\": \"Salotttto\", \"temperature\": \"21.39\"}"

         */
        // TODO emprove with emum

            try {
                if ("TEMP".equals(JsonHelper.readField(aMessage.toString(), "type"))){
	                logger.info("TEMP reading received. Topic: [{}]. Message: [{}]", aTopic, aMessage.toString());
	                if (MySweetHomeProperties.PERSIST_TEMPERATURES){
		    			iTempReading = iMapper.readValue(aMessage.toString(), TemperatureReading.class);
		    	        Date dateRead = Helper.resetSecMillsDate(new Date());
	    	            storeTemperature(new TemperatureMeasure(iTempReading.getLocation(), iTempReading.getGroup(), dateRead, iTempReading.getTemperature()));
	    	            
	    	            //are there some device info?
	    				if (JsonHelper.nodePresent(aMessage.toString(), "esp8266Status")){
	    					System.out.println("Temperature.esp8266Status.age:: "+iTempReading.getESP8266Status().getAge());
	    				} else {
	    					System.out.println("No ESP8266Status present");
	    				}
	    	        }
                } else if (false){
                	//next type
                } else {
                	logger.warn("Measure not recognized. Topic: [{}]. Message: [{}]", aTopic, aMessage.toString());
                }
    		} catch (Exception e) {
    			logger.error("Error parsing message. Topic: [{}]. Message: [{}]", aTopic, aMessage.toString());
    			e.printStackTrace();
    			return;
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
