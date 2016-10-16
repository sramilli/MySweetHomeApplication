/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Ste
 */
public class PropertiesHandler {
    
    private static PropertiesHandler instance = null;
    Properties iProp = null;
    OutputStream iOutput = null;
    InputStream iInput = null;
    
    public static PropertiesHandler getInstance(){
        if (instance == null){
            instance = new PropertiesHandler();
        }
        return instance;
    }
    
    private PropertiesHandler(){
        iProp = new Properties();
        try {
            iInput = new FileInputStream("/home/pi/config.properties");
            iProp.load(iInput);
            iInput = new FileInputStream("/home/pi/garden.properties");
            iProp.load(iInput);
            //System.out.println(iProp.getProperty("database"));
	} catch (IOException ex) {
            ex.printStackTrace();
	} finally {
            if (iInput != null) {
                try {
                    iInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
    }
    
    public String getProperty(String aKey){
        if (aKey == null || "".equals(aKey))
            return "";
        String tProp = iProp.getProperty(aKey);
        if (tProp == null)
            return "unknown";
        return tProp;
    }
    
    public void storeProperty(String aKey, String aValue){
        try{
            iOutput = new FileOutputStream("/home/pi/config.properties");
            iProp.setProperty("measureTemps", "true");

            iProp.store(iOutput, null);

        }catch (IOException io) {
            io.printStackTrace();
        }finally {
            if (iOutput != null) {
                try {
                    iOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
