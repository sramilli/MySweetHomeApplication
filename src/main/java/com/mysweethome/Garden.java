/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.entity.Actuator;
import com.mysweethome.entity.Pump;
import com.mysweethome.helper.Helper;
import com.mysweethome.properties.GardenProperties;

/**
 *
 * @author Ste
 */

//TODO erase, just for test
//public class Garden implements Runnable{
public class Garden{
    static Logger logger = LoggerFactory.getLogger(Garden.class);
    
    Actuator iActuator;
    Pump iPump1;
    Pump iPump2;
    Pump iPump3;
    Pump iPump4;
    Pump iPump5;
    List<Pump> iPumps;
    SensorDataLogger iLogger;
    
    private Timer iTimer;
    
    public static long REPEAT_DAILY = 24 * 60 * 60 * 1000;
    
    public Garden(){
        super();
        iActuator = new Actuator();
        iPump1 = new Pump(GardenProperties.TOPIC_PUMP_1, GardenProperties.CALIBRATION_PUMP_1, GardenProperties.DEFAULT_QUANTITY_PUMP_1);
        iPump2 = new Pump(GardenProperties.TOPIC_PUMP_2, GardenProperties.CALIBRATION_PUMP_2, GardenProperties.DEFAULT_QUANTITY_PUMP_2);
        iPump3 = new Pump(GardenProperties.TOPIC_PUMP_3, GardenProperties.CALIBRATION_PUMP_3, GardenProperties.DEFAULT_QUANTITY_PUMP_3);
        iPump4 = new Pump(GardenProperties.TOPIC_PUMP_4, GardenProperties.CALIBRATION_PUMP_4, GardenProperties.DEFAULT_QUANTITY_PUMP_4);
        iPump5 = new Pump(GardenProperties.TOPIC_PUMP_5, GardenProperties.CALIBRATION_PUMP_5, GardenProperties.DEFAULT_QUANTITY_PUMP_5);
        iPumps = new ArrayList<Pump>(Arrays.asList(iPump1, iPump2, iPump3, iPump4, iPump5));
        iTimer = new Timer(true);
        //TODO move to thermostat?
        iLogger = new SensorDataLogger();
    }
    
    //TODO erase, just for test
    //@Override
    public void run() {
        logger.info("Garden Application Started");
        scheduleActivationAtHoursOfDay(GardenProperties.MORNING_WATERING_TIME, GardenProperties.EVENING_WATERING_TIME);
        iLogger.start();
    }
    
    public void stop(){
        iLogger.stop();
        //TODO stop Tascs
    }
    
    private void scheduleActivationAtHoursOfDay(String... hours){
        for (String hour: hours){
            Calendar tActivationInstant;
            try {
                tActivationInstant = Helper.parseTime(hour);
            } catch (ParseException ex) {
                logger.error("Error scheduling activation hour [{}]", hour);
                continue;
            }
            if (tActivationInstant != null){
                if (tActivationInstant.before(Calendar.getInstance())) tActivationInstant.add(Calendar.DAY_OF_MONTH, 1);
                scheduleActivationAtHour(tActivationInstant);
            }
        }
    }
    
    private void scheduleActivationAtHour(Calendar aActivationInstant){
        Helper.printCal("CompleteWatering programmed daily starting", aActivationInstant);
        ExecuteCompleteWateringTimerTask tExecuteCompleteWateringTimerTask = new ExecuteCompleteWateringTimerTask(iActuator, iPumps);
        iTimer.scheduleAtFixedRate(tExecuteCompleteWateringTimerTask, aActivationInstant.getTime(), REPEAT_DAILY);
    }


}
