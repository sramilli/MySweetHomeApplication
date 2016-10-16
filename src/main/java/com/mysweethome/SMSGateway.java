/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import com.mysweethome.entity.SMS;
import com.mysweethome.properties.ThermostatProperties;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
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

/**
 *
 * @author Ste
 */
public class SMSGateway implements SerialDataListener{
    static Logger logger = LoggerFactory.getLogger(SMSGateway.class);
    private static SMSGateway iInstance = null;
    private static MessageHandler iMessageHandler = null;
    
    Serial serial;
    SerialDataListener iSerialDataListener = null;
    
    static final private char ctrlZ = (char) 26;
    static final private char ctrlD = (char) 4;

    private SMSGateway(){
    }

    public static synchronized SMSGateway getInstance(MessageHandler aMessageHandler) {
        if (iInstance == null) {
            iInstance = new SMSGateway();
        }
        iMessageHandler = aMessageHandler;
        return iInstance;
    }

    public void initialize() {

        iSerialDataListener = this;
        logger.info("... connect to serial MODEM using settings: [{}], N, 8, 1.", ThermostatProperties.GSM_BAUD_RATE);
        // create an instance of the serial communications class
        serial = SerialFactory.createInstance();
        serial.open(Serial.DEFAULT_COM_PORT, ThermostatProperties.GSM_BAUD_RATE);
        waitABit(3000);
        
        sendATCommand();
        readAnswer(); 
        // create and register the serial data listener
        /*serial.addListener(new SerialDataListener() {
             @Override
             public void dataReceived(SerialDataEvent event) {
                 // print out the data received to the console
                 waitABit(2000);
                 System.out.println("");
                 System.out.print("Resp---->"+event.getData()+"<----");
             
         });}*/
        logger.info("Reading all old message present on the SIM at boot");
        List<SMS> tSMSs = this.getAllMessages();
        if (tSMSs.size() > 0){
            printAllMessages(tSMSs);
            deleteAllMessages(tSMSs);
        } else {
            logger.info("No message present on the modem at startup");
        }
        serial.addListener(iSerialDataListener);
    }
    
    ////////////
    // listener from Thermostat
    ////////////
    
     public void dataReceived(SerialDataEvent event) {
         // print out the data received to the console
         //http://www.developershome.com/sms/resultCodes3.asp
         logger.info("Incoming event arrived from the GSM module");
         String response = event.getData();
         this.removeListener(this);
         //String response = iSMSGateway.readAnswer();
         if (GSMDataInterpreter.getCommand(response).equals(GSMCommand.MESSAGE_ARRIVED)){
            logger.info("Data received:  ---->[{}]<----", response);
            waitABit(3000);
            List<SMS> tSMSs = getAllMessages();
            printAllMessages(tSMSs);
            
            iMessageHandler.processReceivedSMSS(tSMSs);
            
            //delete all messages
            this.deleteAllMessages(tSMSs);
         }
         this.addListener(this);
     }



    //
    //Collection utility
    //
    public void deleteAllMessages(Collection<SMS> aMessages){
        logger.info("Deleting all messages");
        for (SMS tSMS : aMessages) {
            logger.info("Delete message [{}] ", tSMS);
            String tResp = this.deleteMsgAtCertainPosition(tSMS.getPosition());
            logger.info("--->[{}] ", tResp);
        }
        logger.info("All messages deleted");
    }
    
    public void printAllMessages(Collection<SMS> aMessages){
        //print the list
        logger.info("List of all messages on the modem ordered by date");
        for (SMS tSMS : aMessages) {
            logger.info("[{}]", tSMS);
        }
    }
    
    //
    //Simple send AT command
    //
    public void sendATCommand (){
        logger.info("---->Sending: AT");
        serial.write("AT\r");
        waitABit(1000); //TODO tweeka
    }
    
    public void sendReadAllMessagesCommand(){
        logger.info("atReadAllMessages:---->Sending: AT+CMGL=\"ALL\"");
        serial.write("AT+CMGL=\"ALL\"\r");
        waitABit(1000);
    }
    
    
    //
    //Send message to user
    //
    public void sendStatusToUser(String aRecipient, String aMessage) {
        logger.info("Sending Status message [{}] to recipient [{}] ", aMessage, aRecipient);
        sendSMS(aRecipient, aMessage);
    }
    
    public void sendHelpMessageToUser(String aRecipient) {
        logger.info("Sendind Help message to [{}]", aRecipient);
        sendSMS(aRecipient, 
                "Examples:\n"
                + "1) on\n"
                + "2) off\n"
                + "3) manual\n"
                + "4) status\n"
                + "5) help\n"
                + "6) register +391234512345\n"
                + "7) ProgramDaily 6:15-7:45"
                + "8) Program 6:15-7:45");
    }
    
    public void sendSMS(String aNumberRecipient, String aMessage) {
        logger.info("---->Sending: AT");
        serial.write("AT\r");
        readAnswerAndPrint();

        logger.info("---->Sending: AT+CMGF=1");
        serial.write("AT+CMGF=1\r");
        readAnswerAndPrint();

        logger.info("---->Sending: AT+CMGS=\"[{}]\"", aNumberRecipient);
        serial.write("AT+CMGS=\""+aNumberRecipient+"\"\r");
        readAnswerAndPrint();

        logger.info("---->Sending:  [{}]", aMessage);
        serial.write(aMessage + ctrlZ);
        //this is needed because sending the sms takes time
        waitABit(2000);
        readAnswerAndPrint();
    }
    
    //
    //Simple read
    //
    public String readAnswer() {
        waitABit(1000);
        StringBuffer tReply = new StringBuffer();
        while (serial.availableBytes() > 0) {
            tReply.append(serial.read());
        }
        return tReply.toString();
    }
    

    //
    //Commands + answers
    //
    public String readAllMessagesRaw() {
        logger.info("---->Sending: AT+CMGL=\"ALL\"");
        serial.write("AT+CMGL=\"ALL\"\r");
        waitABit(1000); //TODO tweeka
        String msgs = readAnswer();
        logger.info("Raw data from GSM module:\n [{}]", msgs);
        return msgs;
        /*
         AT+CMGL="ALL"
         +CMGL: 1,"REC READ","Telia","","15/04/27,21:31:40+08"
         Ditt saldo borjar bli lagt. Sla *120# lur/skicka for att kontrollera saldo. Ladda direkt via kontokort, m.telia.se/snabbladda eller las mer om de andra l
         +CMGL: 2,"REC READ","Telia","","15/04/27,21:31:41+08"
         addningssatten pa www.telia.se/ladda. Halsningar Telia
         +CMGL: 3,"REC READ","+46700447531","","15/05/02,18:01:08+08"
         Sms di prova
         +CMGL: 4,"REC READ","+46700447531","","15/05/02,18:01:34+08"
         Andra prova
         +CMGL: 5,"REC READ","+46700447531","","15/05/02,18:01:51+08"
         Terza prova
         +CMGL: 6,"REC READ","Telia","","15/05/14,19:32:43+08"
         Du har nu fatt 25 kr i bonus. Bonusen far du i samband med dina fyra forsta laddningar efter din registrering. Total bonus blir 100 kr. Mvh Telia
         +CMGL: 7,"REC READ","Telia","","15/05/14,19:32:44+08"
         Ditt kort ar nu laddat. For att se ditt saldo tryck *120# lur/skicka. For mer information om priser se www.telia.se/refill
         +CMGL: 8,"REC READ","+46700447531","","15/05/21,22:37:34+08"
         . 
         +CMGL: 9,"REC READ","+46700447531","","15/05/21,22:38:21+08"
         00510075006500730074006F000A00C80020000A0055006E00200073006F006C006F0020006D0065007300730061006700670069006F
         +CMGL: 10,"REC READ","+46700447531","","15/05/26,22:59:28+08"
         On
         OK
         */
    }

    public List<SMS> getAllMessages() {
        //read all messages and parse them
        String rawMessageString = readAllMessagesRaw();
        return parseAllMessages(rawMessageString);
    }
    
    public String readMsgAtCertainPosition(int aPos) {
        logger.info("---->Sending: AT+CMGR=[{}]", aPos);
        serial.write("AT+CMGR=" + aPos + "\r");
        waitABit(1000); //TODO tweeka
        return readAnswer();
    }
    
    public String deleteMsgAtCertainPosition(int aPos) {
        logger.info("---->Sending: AT+CMGD=", aPos);
        serial.write("AT+CMGD=" + aPos + "\r");
        waitABit(1000); //TODO tweeka
        return readAnswer();
    }
    
    private void readAnswerAndPrint() {
        waitABit(1000);
        StringBuffer reply = new StringBuffer();
        while (serial.availableBytes() > 0) {
            reply.append(serial.read());
        }
        if (reply.length() > 0) {
            logger.info("//////:\n [{}] //////", reply);
        } else {
            logger.warn("<---->NO ANSWER FROM GSM MODULE!");
        }
        //whaitABit(1000);
    }
    
    //
    //Parse utility
    //
    public List<SMS> parseAllMessages(String aMessages) {
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
                tSMS = parseHeaderAndSetData(s);
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
    
    public SMS parseHeaderAndSetData(String s) {
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
            tPosition = parsePosition(tToken);
            tToken = st.nextToken();
            //iStatus = parseStatus();
            tToken = st.nextToken();
            tSender = parseSender(tToken);
            tToken = st.nextToken();
            //blank
            tToken = st.nextToken();
            tToken = tToken+" "+st.nextToken();
            tDate = parseDate(tToken);
            
            tSMS.setPosition(tPosition);
            tSMS.setSender(tSender);
            tSMS.setDate(tDate);
            
            return tSMS;
    }
        
    private int parsePosition(String aPosition){
        int tPos = 0;
        tPos = Integer.parseInt(aPosition.substring(aPosition.length()-2).trim());
        return tPos;
    }
    
    private String parseSender(String aSender){
        String tSender = "";
        tSender = aSender.replaceAll("\"", "").trim();
        return tSender;
    }

    private Date parseDate(String aDate){
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
    
    //
    //Test
    //
    public void testLoopingAT() {
        for (int i = 0; i < 10; i++) {
            logger.info("----Sending: AT ([{}]), [{}]", i, new Date().toString());
            serial.write("AT\r");
            readAnswerAndPrint();
            waitABit(5000);
        }
    }
    
    
    
    
    private void waitABit(int a) {
        try {
            // wait 1 second before continuing
            Thread.sleep(a);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removeListener(SerialDataListener aListener){
        serial.removeListener(aListener);
    }
    
    public void addListener(SerialDataListener aListener){
        serial.addListener(aListener);
    }

    public void stop() {
        if (serial != null) {
            serial.removeListener(iSerialDataListener);
            serial.close();
            serial = null;
        }
    }



}
