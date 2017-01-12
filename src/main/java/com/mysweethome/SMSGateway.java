/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import com.mysweethome.entity.SMS;
import com.mysweethome.helper.Helper;
import com.mysweethome.helper.SMSHelper;
import com.mysweethome.properties.MySweetHomeProperties;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
    
    static final private String SIM900_TERMINAL_STR = "\r\n";
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
        logger.info("... connect to serial MODEM using settings: [{}], N, 8, 1.", MySweetHomeProperties.GSM_BAUD_RATE);
        try {
            serial = SerialFactory.createInstance();
            serial.open(MySweetHomeProperties.UART_PORT_NAME, MySweetHomeProperties.GSM_BAUD_RATE);
        } catch (Throwable e) {
        	logger.error("ERROR opening serial communication to the GPRS module", e);
        }

        //Helper.waitABit(6000);
        
        if (serial.isOpen()) logger.debug("Serial Port Open!");
        if (serial.isClosed()) logger.debug("Serial Port Closed!");
        if (serial.isShutdown()) logger.debug("Serial Port Closed!");
        
        sendAT();
        //readFromSerial(); 
        sendAT();
        //readFromSerial(); 
        sendAT();
        //readFromSerial(); 
        sendAT();
        //readFromSerial(); 
        sendAT();
        //readFromSerial(); 
        sendAT();
        //readFromSerial(); 
        
        sendString("AT\r\n");
        sendString("AT\r\n");
        sendString("AT\r\n");
        sendString("AT\r\n");
        sendString("AT\r\n");
        
        serial.write("AT\r\n");
        serial.write("AT\r\n");
        serial.write("AT\r\n");
        serial.write("AT\r\n");
        
        if (2==2) return;
        
        setTextMode();
        String response = readFromSerial(); 
        if (response == null || response.equals("")) {
        	logger.error("GPRS ERROR. It didnt respond to AT command");
        	
        	//TODO test to add the listener anyway
        	serial.addListener(iSerialDataListener);
        	
        	return;
        }

        logger.info("Reading all old message present on the SIM at boot");
        List<SMS> tSMSs = getAllMessages();
        if (tSMSs.size() > 0){
        	SMSHelper.printAllMessages(tSMSs);
            deleteAllMessages(tSMSs);
        } else {
            logger.info("No message present on the modem at startup");
        }
        
        serial.addListener(iSerialDataListener);
    }
    

    /*
     * Listener
     */
    
     public void dataReceived(SerialDataEvent event) {
    	 removeListener(this);
    	 //Helper.waitABit(3000);
         //http://www.developershome.com/sms/resultCodes3.asp
         logger.info("Incoming event arrived from the GSM module");
         logger.debug("event data: [{}]", event.getData());
         logger.debug("event source: [{}]", event.getSource());
         String response = event.getData();
         
         if (GSMDataInterpreter.getCommand(response).equals(GSMCommand.MESSAGE_ARRIVED)){
            logger.info("Data received:  ---->[{}]<----", response);
            
            List<SMS> tSMSs = getAllMessages();
            SMSHelper.printAllMessages(tSMSs);
            
            iMessageHandler.processReceivedSMSS(tSMSs);
            
            deleteAllMessages(tSMSs);
         }
         addListener(this);
     }


    
    
    /*
     * High level functions
     */

    public List<SMS> getAllMessages() {
        //read all messages and parse them
        String rawMessageString = readAllMessagesRaw();
        return SMSHelper.parseAllMessages(rawMessageString);
    }
    
    public void sendStatusToUser(String aRecipient, String aMessage) {
        logger.info("Sending Status message");
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
                + "8) ProgramDaily"
                + "9) Program 6:15-7:45");
    }
    
    public void deleteAllMessages(Collection<SMS> aMessages){
        logger.info("Deleting all messages");
        for (SMS tSMS : aMessages) {
            logger.info("Delete message [{}] ", tSMS);
            String tResp = deleteMsgAtCertainPosition(tSMS.getPosition());
            logger.debug("--->[{}] ", tResp);
        }
        logger.info("All messages deleted");
    }
    
    public String readAllMessagesRaw() {
        /*
        AT+CMGL="ALL"
        +CMGL: 1,"REC READ","Telia","","15/04/27,21:31:40+08"
        Ditt saldo borjar bli lagt. Sla *120# lur/skicka for att kontrollera saldo. Ladda direkt via kontokort, m.telia.se/snabbladda eller las mer om de andra l
        +CMGL: 2,"REC READ","Telia","","15/04/27,21:31:41+08"
        addningssatten pa www.telia.se/ladda. Halsningar Telia
        +CMGL: 3,"REC READ","+46700447531","","15/05/02,18:01:08+08"
        Sms di prova
        OK
        */
    	
    	//Helper.waitABit(1000);
    	//setTextMode();
    	String msgs = readFromSerial();  //empty the buffer
    	sendReadAllMessages();
        msgs = readFromSerial();
        logger.debug("Raw data from GSM module: [\n{}\n]", msgs);
        
        return msgs;
    }
    
    public void sendSMS(String aNumberRecipient, String aMessage) {
        logger.debug("Send SMS to nr [{}] message [{}]", aNumberRecipient, aMessage);
        
        sendAT();
        readFromSerial();

        setTextMode();
        readFromSerial();
        
        sendNewSMS_number(aNumberRecipient);
        readFromSerial();

        sendNewSMS_message(aMessage);
        sendNewSMS_terminate();
        
        //this is needed because sending the sms takes time
        //Helper.waitABit(4000);
        readFromSerial();
    }
    
    
    
    /*
     * Implementation
     */
    
    public void setTextMode(){
        logger.debug("---->Sending AT Command: AT+CMGF=1");
        sendATCommand("AT+CMGF=1");
    }
    
    public void sendAT(){
        logger.debug("---->Sending AT Command: AT");
        sendATCommand("AT");
    }
    
    public void sendReadAllMessages(){
    	logger.debug("---->Sending AT Command: AT+CMGL=\"ALL\"");
    	sendATCommand("AT+CMGL=\"ALL\"");
    	//Helper.waitABit(4000);
    }
    
    public void sendNewSMS_number(String aNumberRecipient){
    	logger.debug("---->Sending AT Command: AT+CMGS=\"{}\"", aNumberRecipient);
    	sendATCommand("AT+CMGS=\"" + aNumberRecipient + "\"");
    }
    
    public void sendNewSMS_message(String aMessage){
    	sendString(aMessage);
    }
    
    public void sendNewSMS_terminate(){
    	sendChar(ctrlZ);
    }
    
    public String deleteMsgAtCertainPosition(int aPos) {
        logger.debug("---->Sending AT Command: AT+CMGD=[{}]", aPos);
        sendATCommand("AT+CMGD=" + aPos);
        return readFromSerial();
    }
    
    public String readMsgAtCertainPosition(int aPos) {
        logger.info("---->Sending AT Command: AT+CMGR=[{}]", aPos);
        sendATCommand("AT+CMGR=" + aPos);
        return readFromSerial();
    }
    
    /*
     * Serial Commands
     */
    
    private void sendATCommand(String aCommand){
    	logger.debug("---->Sending AT command: " + aCommand);
    	sendString(aCommand + '\r' + '\n');
    }
    
    private void sendString(String aString){
    	logger.debug("---->Sending String: ---->" + aString +"<----");
    	serial.write(aString);
    	serial.flush();
    	//Helper.waitABit(2000);
    }
    
    private void sendChar(char aChar){
    	logger.debug("---->Sending char: " + aChar);
    	serial.write(aChar);
    	serial.flush();
    	//Helper.waitABit(2000);
    }
    
    public String readFromSerial() {
    	logger.debug("Reading answer from GSM module");
        //Helper.waitABit(3000);
        StringBuffer tReply = new StringBuffer();
        while (serial.availableBytes() > 0) {
            tReply.append(serial.read());
        } 
        /*//Helper.waitABit(1000);
        while (serial.availableBytes() > 0) {
            tReply.append(serial.read());
            logger.warn("READIND A SECOND CHUNK FROM GSM");
        } */
        if (tReply.length() < 1) {
        	logger.debug("No bytes available on the serial connection. Resulting string: [{}]", tReply.toString());
        } else {
            logger.debug("Response from GSM module - length: [{}] contetnt:  [{}]", tReply.length(), tReply.toString());
        }
        return tReply.toString();
    }
    

    
    
    /*
     * Test
     */
    public void testLoopingAT() {
        for (int i = 0; i < 10; i++) {
            logger.info("----Sending: AT ([{}]), [{}]", i, new Date().toString());
            serial.write("AT\r");
            readFromSerial();
            //Helper.waitABit(5000);
        }
    }
    
    
    
    
    /*
     * public methods
     */
    
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
