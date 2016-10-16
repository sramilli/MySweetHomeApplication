/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.entity;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.mysweethome.properties.GardenProperties;

/**
 *
 * @author Ste
 * http://www.eclipse.org/paho/files/javadoc/index.html
 * http://www.hivemq.com/blog/mqtt-client-library-encyclopedia-eclipse-paho-java
 * http://dev.solacesystems.com/get-started/mqtt-tutorials/publish-subscribe_mqtt/
 * http://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service
 * 
 */
public class Actuator {
    MemoryPersistence iPersistence;
    
    public Actuator(){
        super();
        iPersistence = new MemoryPersistence();
    }
    
    public void activate(IActivable aActivable){
        Pump tPump = (Pump)aActivable;
        if (tPump.isPlugged() == false) return;
        
        String topic = aActivable.getTopic();
        int tDefaultQuantity = tPump.getDefaultQuantity();
        int tCalibration = tPump.getCalibration();
        String tActivatingTime = "" + (tDefaultQuantity + tCalibration);
        String content = tActivatingTime;

        try {
            MqttClient mqttClient = new MqttClient(GardenProperties.MQTT_BROKER, GardenProperties.ACTUATOR_CLIENT_ID, iPersistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " +GardenProperties.MQTT_BROKER);
            mqttClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " +topic +"    " +content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(GardenProperties.MQTT_QOS_2);
            mqttClient.publish(topic, message);
            System.out.println("Message published");
            mqttClient.disconnect();
            System.out.println("Disconnected");
            System.out.println("");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
            System.out.println("");
        }
    }
}
