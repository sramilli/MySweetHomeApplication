package com.mysweethome.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.SMSGateway;
import com.mysweethome.entity.SMS;

public class SMSHelper {
	
	static Logger logger = LoggerFactory.getLogger(SMSHelper.class);
	
    public static void printAllMessages(Collection<SMS> aMessages){
        //print the list
        logger.info("List of all messages on the modem ordered by date");
        for (SMS tSMS : aMessages) {
            logger.info("[{}]", tSMS);
        }
    }
	
    public static Date parseDate(String aDate){
        //"15/05/26 22:59:28+08"
        Date tDate = new Date();
        aDate = aDate.replaceAll("\"", "");
        aDate = aDate.substring(0,14);
        SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
        try {
            tDate = formatter.parse(aDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return tDate;
    }
    
    public static int parsePosition(String aPosition){
        int tPos = 0;
        tPos = Integer.parseInt(aPosition.substring(aPosition.length()-2).trim());
        return tPos;
    }
    
    public static String parseSender(String aSender){
        String tSender = "";
        tSender = aSender.replaceAll("\"", "").trim();
        return tSender;
    }
    
    public static SMS parseHeaderAndSetData(String s) {
        /*
         * +CMGL: 4,"REC READ","+46700447531","","15/05/02,18:01:34+08"
         * Set: Position, Date, Sender
         */
         StringTokenizer st = new StringTokenizer(s, ",");
             SMS tSMS = new SMS();
             
             int tPosition;
             String tSender;
             Date tDate;
             
             String tToken = st.nextToken();
             tPosition = SMSHelper.parsePosition(tToken);
             tToken = st.nextToken();
             //iStatus = parseStatus();
             tToken = st.nextToken();
             tSender = SMSHelper.parseSender(tToken);
             tToken = st.nextToken();
             //blank
             tToken = st.nextToken();
             tToken = tToken+" "+st.nextToken();
             tDate = SMSHelper.parseDate(tToken);
             
             tSMS.setPosition(tPosition);
             tSMS.setSender(tSender);
             tSMS.setDate(tDate);
             
             return tSMS;
     }
    

    public static List<SMS> parseAllMessages(String aMessages) {
        //read all messages
        logger.info("Start parsing string: start string-->[{}]<--end string", aMessages);
        StringTokenizer st = new StringTokenizer(aMessages, "\r\n");
        //parse messages
        List<SMS> tSMSs = new ArrayList<SMS>();
        int i = 1;
        List<String> tRows = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            tRows.add(st.nextToken());
        }
        boolean headClean = false, smsNotAddedYet = false;
        SMS tSMS = new SMS();

        outerLoop:
        for (int j = 0; j < tRows.size() - 1; j++) {
            String s = tRows.get(j);
            while (!headClean && !s.startsWith("+CMGL")) {
                continue outerLoop;
            }
            headClean = true;
            if (s.startsWith("+CMGL")) {
                tSMS = SMSHelper.parseHeaderAndSetData(s);
                continue;
            } else {
                tSMS.setText(tSMS.getText() + s);
                tSMSs.add(tSMS);
                continue;
            }
        }
        logger.info("Parsed [{}] smss", tSMSs.size());
        Collections.sort(tSMSs);
        Collections.reverse(tSMSs);
        return tSMSs;
    }

}
