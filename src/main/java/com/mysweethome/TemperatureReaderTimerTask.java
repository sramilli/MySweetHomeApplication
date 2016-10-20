/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.devices.AdafruitBMP180;
import com.mysweethome.devices.BMP280;
import com.mysweethome.entity.TemperatureMeasure;
import com.mysweethome.helper.Helper;
import com.mysweethome.properties.MySweetHomeProperties;

/**
 *
 * @author Ste
 */
public class TemperatureReaderTimerTask extends TimerTask {
    
    static Logger logger = LoggerFactory.getLogger(TemperatureReaderTimerTask.class);

    AdafruitBMP180 tempSensor;
    BMP280 bmp280;
    float temp;
    float temp_2;
    Date dateRead;
    String iLocation;
    String iGroup;
    TemperatureStore iTemperatureStore;
    private static int BUS_1 = 1;

    public TemperatureReaderTimerTask(String aLocation, String aGroup, TemperatureStore aTemperatureStore){
        logger.info("TemperatureReaderTimerTask INSTANTIATED");
        tempSensor = new AdafruitBMP180();
        if (MySweetHomeProperties.BMP280_TEMP_SENSOR_PRESENT_AT_76){
            try {
                bmp280 = new BMP280(BMP280.Protocol.I2C, BMP280.ADDR_SDO_2_GND, BUS_1);
                bmp280.setIndoorNavigationMode();
                bmp280.setMode(BMP280.Mode.NORMAL, true);
                bmp280.setTemperatureSampleRate(BMP280.Temperature_Sample_Resolution.TWO, true);
                bmp280.setPressureSampleRate(BMP280.Pressure_Sample_Resolution.SIXTEEN, true);
                bmp280.setIIRFilter(BMP280.IIRFilter.SIXTEEN, true);
                bmp280.setStandbyTime(BMP280.Standby_Time.MS_POINT_5, true);
            } catch (Exception ex) {
                logger.error("Error instantiating BMP280");
            }
        }

        temp = 0;
        temp_2 = 0;
        iLocation = aLocation;
        iGroup = aGroup;
        iTemperatureStore = aTemperatureStore;
    }

    @Override
    public void run(){
        try {
            temp = tempSensor.readTemperature();
            if (MySweetHomeProperties.BMP280_TEMP_SENSOR_PRESENT_AT_76){
                double[] results = bmp280.sampleDeviceReads();
                temp_2 = (float) results[BMP280.TEMP_VAL_C];
            }
            
        } catch (Exception ex) {
            temp = 0;
            temp_2 = 0;
            ex.printStackTrace();
        }
        dateRead = Helper.resetSecMillsDate(new Date());
        logger.info("Temp measure: [{}], [{}] C", Helper.getDateAsString(dateRead), Helper.getTempAsString(temp));
        if (MySweetHomeProperties.BMP280_TEMP_SENSOR_PRESENT_AT_76){
            logger.info("Temp measure_2: [{}], [{}] C", Helper.getDateAsString(dateRead), Helper.getTempAsString(temp_2));
        }
        iTemperatureStore.setLastTemperatureRead(new TemperatureMeasure(iLocation, iGroup, dateRead, temp));
        //iTemperatureStore.setLastTemperatureRead(new TemperatureMeasure("280", iGroup, dateRead, temp_2));
        if (MySweetHomeProperties.PERSIST_TEMPERATURES){
            storeTemperature(new TemperatureMeasure(iLocation, iGroup, dateRead, temp));
            if (MySweetHomeProperties.BMP280_TEMP_SENSOR_PRESENT_AT_76){
                storeTemperature(new TemperatureMeasure("280", iGroup, dateRead, temp_2));
            }
        }
    }
    
    private void storeTemperature(TemperatureMeasure aTemperatureMeasure) {
        // 24 byte each. 1 MB -> 41666 measures.
        iTemperatureStore.storeTemperature(aTemperatureMeasure);
    }

}
