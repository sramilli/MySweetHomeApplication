/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Collection;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mysweethome.entity.TemperatureMeasure;
import com.mysweethome.helper.Helper;
import com.mysweethome.properties.MySweetHomeProperties;

import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//http://mongodb.github.io/mongo-java-driver/3.0/driver/getting-started/quick-tour/

/**
 *
 * @author Ste
 */
class TemperaturePersisterTimerTask extends TimerTask {
    //TODO concurrency problems!!!!
    //continue refactoring
    
    static Logger logger = LoggerFactory.getLogger(TemperaturePersisterTimerTask.class);
    
    Collection<TemperatureMeasure> iStoredTemperatures = null;
    Collection<TemperatureMeasure> iPersistedTemperatures = null;
    
    TemperatureStore iTemperatureStore;

    public TemperaturePersisterTimerTask(TemperatureStore aTemperatureStore) {
        iTemperatureStore = aTemperatureStore;
        //need to use a new list to freeze it
        iPersistedTemperatures = new ArrayList<TemperatureMeasure>();
        logger.info("TemperaturePersisterTimerTask instantiated");
    }

    @Override
    public void run() {
        this.persistDataOnMongolab();
    }
    
    public synchronized void persistDataOnMongolab(){
        //disable console logging
        //Logger mongoLogger = Logger.getLogger("org.mongodb.driver"); 
        //mongoLogger.setLevel(Level.SEVERE);

        
        iStoredTemperatures = iTemperatureStore.getTemperatures();
        if (iStoredTemperatures.isEmpty()){
            logger.info("Nothing to persist. Exiting");
            return;
        }
        logger.info("Prepairing to persist [{}] Temps in the cloud", iStoredTemperatures.size());
        MongoCollection<Document> mongoCollection = null;
        MongoClient client = null;
        List<Document> documents = new ArrayList<Document>();
        
        for (TemperatureMeasure tTemp: iStoredTemperatures){ //Exception in thread "Timer-2" java.util.ConcurrentModificationException
            Document doc = new Document();
            doc.put("Location", tTemp.getLocation());      //Location
            doc.put("Group", tTemp.getGroup());         //Group
            doc.put("Date", Helper.getDateAsString(tTemp.getDate()));   //Date
            doc.put("Day", Helper.getDayAsString(tTemp.getDate()));
            doc.put("Time", Helper.getTimeAsString(tTemp.getDate()));
            doc.put("Temp", Helper.getTempAsString(tTemp.getTemp()));   //Temp
            documents.add(doc);
            iPersistedTemperatures.add(tTemp);
        }

        try{
            MongoClientURI uri  = new MongoClientURI(MySweetHomeProperties.ML_URL); 
            client = new MongoClient(uri);
            MongoDatabase database = (MongoDatabase) client.getDatabase(uri.getDatabase());
            mongoCollection = database.getCollection("dailytemps");
            mongoCollection.insertMany(documents);
            //eliminate stored Temps from the collection
            iTemperatureStore.removeAll(iPersistedTemperatures);
            client.close();
            logger.info("Temperatures persisted on mongolab: [{}]. Exiting.", iPersistedTemperatures.size());
            iPersistedTemperatures.clear();
        } catch (Throwable e){
            logger.error("Failed to store Temps in the cloud. Stacktrace: [{}]. Exiting.", e);
            iPersistedTemperatures.clear();
            e.printStackTrace();
        } finally{
            if (client != null){
                client.close();
            }
            iPersistedTemperatures.clear();
        }
    }
    
}
