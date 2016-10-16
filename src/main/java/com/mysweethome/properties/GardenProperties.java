/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.properties;

/**
 *
 * @author Ste
 */
public class GardenProperties {
    
    public static boolean START_GARDEN_APPLICATION = true;
    
    public static String ACTUATOR_CLIENT_ID = "ActuatorMqttClient";
    public static String SENSOR_LOGGER_CLIENT_ID = "SensorLoggerMqttClient";
    public static String MQTT_BROKER = "tcp://192.168.0.25:1883";
    public static int MQTT_QOS_2 = 2;
    public static String SENSOR_DATA_LOGGER_TOPIC_TEMPS = "/temps";
    
    public static String MORNING_WATERING_TIME = "23:30";
    public static String EVENING_WATERING_TIME = "23:33";
    
    public static String TOPIC_PUMP_1 = "/pump1";
    public static String TOPIC_PUMP_2 = "/pump2";
    public static String TOPIC_PUMP_3 = "/pump3";
    public static String TOPIC_PUMP_4 = "/pump4";
    public static String TOPIC_PUMP_5 = "/pump5";
    public static int CALIBRATION_PUMP_1 = 1;
    public static int CALIBRATION_PUMP_2 = 1;
    public static int CALIBRATION_PUMP_3 = 1;
    public static int CALIBRATION_PUMP_4 = 1;
    public static int CALIBRATION_PUMP_5 = 1;
    public static int DEFAULT_QUANTITY_PUMP_1 = 1;
    public static int DEFAULT_QUANTITY_PUMP_2 = 2;
    public static int DEFAULT_QUANTITY_PUMP_3 = 3;
    public static int DEFAULT_QUANTITY_PUMP_4 = 4;
    public static int DEFAULT_QUANTITY_PUMP_5 = 5;
    public static boolean PUMP_1_PLUGGED = true;
    public static boolean PUMP_2_PLUGGED = true;
    public static boolean PUMP_3_PLUGGED = true;
    public static boolean PUMP_4_PLUGGED = true;
    public static boolean PUMP_5_PLUGGED = true;
    
    public static boolean PERSIST_TEMPERATURES = true;
    
    static {
        try{
            PropertiesHandler prop = PropertiesHandler.getInstance();
            
            START_GARDEN_APPLICATION = new Boolean(prop.getProperty("START_GARDEN_APPLICATION"));
    
            ACTUATOR_CLIENT_ID = prop.getProperty("ACTUATOR_CLIENT_ID");
            SENSOR_LOGGER_CLIENT_ID = prop.getProperty("SENSOR_LOGGER_CLIENT_ID");
            MQTT_BROKER = prop.getProperty("MQTT_BROKER");
            MQTT_QOS_2 = new Integer(prop.getProperty("MQTT_QOS_2"));
            SENSOR_DATA_LOGGER_TOPIC_TEMPS = prop.getProperty("SENSOR_DATA_LOGGER_TOPIC");
    
            MORNING_WATERING_TIME = prop.getProperty("MORNING_WATERING_TIME");
            EVENING_WATERING_TIME = prop.getProperty("EVENING_WATERING_TIME");
    
            TOPIC_PUMP_1 = prop.getProperty("TOPIC_PUMP_1");
            TOPIC_PUMP_2 = prop.getProperty("TOPIC_PUMP_2");
            TOPIC_PUMP_3 = prop.getProperty("TOPIC_PUMP_3");
            TOPIC_PUMP_4 = prop.getProperty("TOPIC_PUMP_4");
            TOPIC_PUMP_5 = prop.getProperty("TOPIC_PUMP_5");;
            CALIBRATION_PUMP_1 = new Integer(prop.getProperty("CALIBRATION_PUMP_1"));
            CALIBRATION_PUMP_2 = new Integer(prop.getProperty("CALIBRATION_PUMP_2"));
            CALIBRATION_PUMP_3 = new Integer(prop.getProperty("CALIBRATION_PUMP_3"));
            CALIBRATION_PUMP_4 = new Integer(prop.getProperty("CALIBRATION_PUMP_4"));
            CALIBRATION_PUMP_5 = new Integer(prop.getProperty("CALIBRATION_PUMP_5"));
            DEFAULT_QUANTITY_PUMP_1 = new Integer(prop.getProperty("DEFAULT_QUANTITY_PUMP_1"));
            DEFAULT_QUANTITY_PUMP_2 = new Integer(prop.getProperty("DEFAULT_QUANTITY_PUMP_2"));
            DEFAULT_QUANTITY_PUMP_3 = new Integer(prop.getProperty("DEFAULT_QUANTITY_PUMP_3"));
            DEFAULT_QUANTITY_PUMP_4 = new Integer(prop.getProperty("DEFAULT_QUANTITY_PUMP_4"));
            DEFAULT_QUANTITY_PUMP_5 = new Integer(prop.getProperty("DEFAULT_QUANTITY_PUMP_5"));
            PUMP_1_PLUGGED = new Boolean(prop.getProperty("PUMP_1_PLUGGED"));
            PUMP_2_PLUGGED = new Boolean(prop.getProperty("PUMP_2_PLUGGED"));
            PUMP_3_PLUGGED = new Boolean(prop.getProperty("PUMP_3_PLUGGED"));
            PUMP_4_PLUGGED = new Boolean(prop.getProperty("PUMP_4_PLUGGED"));
            PUMP_5_PLUGGED = new Boolean(prop.getProperty("PUMP_5_PLUGGED"));
    
            PERSIST_TEMPERATURES = new Boolean(prop.getProperty("PERSIST_TEMPERATURES"));
            
            System.out.println("Read Garden Prop measureTemps correctly");
        } catch (Throwable ex){
            System.out.println("ERROR READING GARDEN PROP FILE!!!");
            ex.printStackTrace();
        }
    }
    
}
