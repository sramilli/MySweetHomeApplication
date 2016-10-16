/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.entity.TemperatureMeasure;
import com.mysweethome.helper.Helper;

/**
 *
 * @author Ste
 */
public class TemperatureStore {
    static Logger logger = LoggerFactory.getLogger(TemperatureStore.class);
    private static TemperatureStore iInstance = null;
    private Collection<TemperatureMeasure> iTemperatures;
    private TemperatureMeasure iLastTemperatureMeasure;
    
    //TODO temporary solution
    public static String LastTemperatureReadString = "";
    
    public static synchronized TemperatureStore getInstance(){
        if (iInstance == null){
            iInstance = new TemperatureStore();
        }
        return iInstance;
    }

    private TemperatureStore(){
        iTemperatures = Collections.synchronizedList(new ArrayList<TemperatureMeasure>());
        iLastTemperatureMeasure = null;
    }
    
    public int size(){
        if (iTemperatures != null){
            return iTemperatures.size();
        }
        return 0;
    }
    
    void storeTemperature(TemperatureMeasure aTemperatureMeasure) {
        if (this.size() < 40000){
            synchronized (iTemperatures){
                iTemperatures.add(aTemperatureMeasure);
                logger.info("Stored temperature: {} Store size: [{}]", aTemperatureMeasure, this.size());
            }
        }else {
            logger.warn("Exceeded max size: [{}]", this.size());
        }
    }

    Collection<TemperatureMeasure> getTemperatures() {
        Collection<TemperatureMeasure> tTemps = new ArrayList<TemperatureMeasure>(iTemperatures.size());
        synchronized (iTemperatures){
            for (TemperatureMeasure t: iTemperatures){
                tTemps.add(new TemperatureMeasure(t.getLocation(), t.getGroup(), t.getDate(), t.getTemp()));
            }
        }
        return tTemps;
    }
    
    void removeAll(Collection<TemperatureMeasure> aTemps){
        synchronized (iTemperatures){
            iTemperatures.removeAll(aTemps);
        }
    }
    
    public void cancel(){
        iTemperatures = null;
    }

    public void setLastTemperatureRead(TemperatureMeasure aTemperatureMeasure) {
        iLastTemperatureMeasure = aTemperatureMeasure;
        //TODO temporary solution
        LastTemperatureReadString = getLastTemperatureReadInString();
    }
    
    public TemperatureMeasure getLastTemperatureRead() {
        return iLastTemperatureMeasure;
    }
    
    public String getLastTemperatureReadInString() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(iLastTemperatureMeasure.getDate());
        return Helper.getTempAsString(iLastTemperatureMeasure.getTemp()) + " C (" + Helper.calToString(cal)+")";
    }
    
}

