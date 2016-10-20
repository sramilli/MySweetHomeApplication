/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.properties.MySweetHomeProperties;

/**
 *
 * @author Ste
 * https://gist.github.com/m2mIO-gister/5275324
 */
public class SensorDataLogger2 {
    static Logger logger = LoggerFactory.getLogger(SensorDataLogger2.class);
    MqttClient iMqttClient;
    MqttConnectOptions iConnOpt;
    MemoryPersistence iPersistence;

    public SensorDataLogger2(){
        super();
        iConnOpt = new MqttConnectOptions();
        iConnOpt.setCleanSession(true);
        iConnOpt.setKeepAliveInterval(30 * 60);
        iPersistence = new MemoryPersistence();
    }

    public void start() {
        try{
            iMqttClient = new MqttClient(MySweetHomeProperties.MQTT_BROKER, MySweetHomeProperties.LOCAL_CLIENT, iPersistence);
            //set callback before connecting
            iMqttClient.setCallback(new SensorDataLoggerCallback2());
            iMqttClient.connect(iConnOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
            logger.error("Error connecting to local MQTT [{}]", MySweetHomeProperties.MQTT_BROKER);
        }
        logger.info("Connected to  [{}]", MySweetHomeProperties.MQTT_BROKER);
        
        try{
            iMqttClient.subscribe(MySweetHomeProperties.SENSOR_READINGS_TOPIC, MySweetHomeProperties.MQTT_QOS_2);
            logger.info("Subscribed to  [{}]", MySweetHomeProperties.SENSOR_READINGS_TOPIC);
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Error subscribing to  [{}]", MySweetHomeProperties.SENSOR_READINGS_TOPIC);
        }
    }
    
    public void stop(){
        try {
            //client.unsubscribe("#");
            iMqttClient.disconnect();
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
}
