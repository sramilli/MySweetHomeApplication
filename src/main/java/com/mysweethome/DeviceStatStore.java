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

import com.mysweethome.entity.StatMeasure;
import com.mysweethome.helper.Helper;

/**
 *
 * @author Ste
 */
public class DeviceStatStore {
    static Logger logger = LoggerFactory.getLogger(DeviceStatStore.class);
    private static DeviceStatStore iInstance = null;
    private Collection<StatMeasure> iMeasures;
    
    public static synchronized DeviceStatStore getInstance(){
        if (iInstance == null){
            iInstance = new DeviceStatStore();
        }
        return iInstance;
    }

    private DeviceStatStore(){
        iMeasures = Collections.synchronizedList(new ArrayList<StatMeasure>());
    }
    
    public int size(){
        if (iMeasures != null){
            return iMeasures.size();
        }
        return 0;
    }
    
    void storeMeasure(StatMeasure aStatMeasure) {
        if (this.size() < 40000){
            synchronized (iMeasures){
                iMeasures.add(aStatMeasure);
                logger.info("Stored stat: {} Store size: [{}]", aStatMeasure, this.size());
            }
        }else {
            logger.warn("Exceeded max size: [{}]", this.size());
        }
    }

    Collection<StatMeasure> getTemperatures() {
        Collection<StatMeasure> tMeasures = new ArrayList<StatMeasure>(iMeasures.size());
        synchronized (iMeasures){
            for (StatMeasure t: iMeasures){
            	tMeasures.add(new StatMeasure(t.getLocation(), t.getGroup(), t.getDate(), t.getAge()));
            }
        }
        return tMeasures;
    }
    
    void removeAll(Collection<StatMeasure> aMeasure){
        synchronized (iMeasures){
            iMeasures.removeAll(aMeasure);
        }
    }
    
    public void cancel(){
        iMeasures = null;
    }

}

