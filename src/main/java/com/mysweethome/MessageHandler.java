/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import static java.lang.Thread.sleep;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.entity.Message;
import com.mysweethome.entity.SMS;
import com.mysweethome.entity.User;
import com.mysweethome.entity.Users;
import com.mysweethome.properties.MySweetHomeProperties;

/**
 *
 * @author Ste
 */
public class MessageHandler {
    static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private SMSGateway iSMSGateway;
    private EmailGateway iEmailGateway;
    private Thermostat iThermostat;
    
    public MessageHandler(Thermostat aThermostat){
        iSMSGateway = SMSGateway.getInstance(this);
        iSMSGateway.initialize();
        iEmailGateway = EmailGateway.getInstance();
        iThermostat = aThermostat;
        //TODO as singleton
    }
    
    public void sendMessage(Message aMessage){
        if (aMessage == null || (aMessage.getUser() == null)){
            logger.warn("sendMessage doing nothing. Message invalid:  [{}] ", aMessage);
            return;
        }
        User tUser = aMessage.getUser();
        if (tUser == null) {
            logger.warn("sendMessage doing nothing. user invalid:  [{}] ", tUser);
            return;
        }
        if (MySweetHomeProperties.PREFER_EMAIL_REPLIES_IF_AVAILABLE && tUser.hasValidEmail() && MySweetHomeProperties.A != null && MySweetHomeProperties.B != null){
            try {
                sendEmailMessage(aMessage);
            } catch (RuntimeException e){
                logger.warn("Sending Email failed, trying to send sms instead");
                if (tUser.hasValidMobileNr()){
                    sendSMSMessage(aMessage);
                }
            }
        } else if (tUser.hasValidMobileNr()){
            sendSMSMessage(aMessage);
        }
    }
    
    public void processReceivedSMSS(List<SMS> aSMSs){
        for (SMS tSMS : aSMSs) {
            CommandType tCommand = CommandParser.parse(tSMS);
            User tUser = Users.getUser(tSMS.getSender());
            if (tSMS.isDateValid() && Users.isAuthorized(tUser) && tCommand != null && tCommand.isActive()) {
                logger.info("Date Valid & User Authorized & Command is active. Executing:  [{}] ", tSMS);
                iThermostat.processReceivedCommand(tCommand, tUser, tSMS);
                break; //execute only last command
            } else {
                logger.warn("SMS discarded: [{}] ", tSMS);
            }
        }
    } 

    private void sendEmailMessage(Message aMessage) {
        iEmailGateway.sendEmail(aMessage.getUser().getEmail(), aMessage.getBody());
        
    }

    private void sendSMSMessage(Message aMessage) {
        iSMSGateway.sendSMS(aMessage.getUser().getMobileNr(), aMessage.getBody());
    }
    
    
    /////////////////////
    
    public void stop(){
        if (iSMSGateway != null) {
            logger.warn("MessageHandler: Turning off SMSGateway");
            waitABit(3000);
            iSMSGateway.stop();
            iSMSGateway = null;
        }
        if (iEmailGateway != null){
            iEmailGateway = null;
        }
        if (iThermostat != null){
            iThermostat = null;
        }
    }
    
    private void waitABit(int a) {
        try {
            Thread.sleep(a);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    ///////////
    // TEST
    ///////////
    
    public void testSendSMS() {
        iSMSGateway.sendSMS("+46700447531", "This is anooother test");
    }

    public void testLoopingAT() {
        iSMSGateway.testLoopingAT();
    }

    public String testReadAllMessagesRaw() {
        return iSMSGateway.readAllMessagesRaw();
    }

    public void testReadAllMessages() {
        for (SMS tSMS : iSMSGateway.getAllMessages()) {
            System.out.println(tSMS);
        }
    }

    public void testReadAllMessagesOneByOne() {
        for (SMS tSMS : iSMSGateway.getAllMessages()) {
            System.out.println(iSMSGateway.readMsgAtCertainPosition(tSMS.getPosition()));
        }
    }

    /*   Keep if i want to eliminate the listener
    public void startPollingIncomingCommands(boolean aDeleteReadMessages, int aSeconds) {
        timer = new Timer();
        //every 30 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<SMS> tSMSs = iSMSGateway.getAllMessages();
                Collections.sort(tSMSs);
                Collections.reverse(tSMSs);
                //print the list
                System.out.println("List of all messages on the modem ordered by date:");
                for (SMS tSMS : tSMSs) {
                    System.out.println(tSMS);
                }
                //check for valid commands
                for (SMS tSMS : tSMSs) {
                    if (tSMS.isDateValid() && tSMS.senderAuthorized() && (CommandParser.parse(tSMS)).isValid()) {
                        System.out.println("Date Valid & User Authorized & Command is valid. Executing: -------> " + tSMS);
                        iController.executeCommand(CommandParser.parse(tSMS));
                        break; //execute only last command
                    } else {
                        System.out.println("SMS discarded: " + tSMS);
                    }
                }
                if (aDeleteReadMessages) {
                    //TODO delete all messages
                    for (SMS tSMS : tSMSs) {
                        System.out.println("Delete message "+tSMS);
                        String tResp = iSMSGateway.deleteMsgAtCertainPosition(tSMS.getPosition());
                        System.out.println(tResp);
                    }
                }
            }
        }, 0, aSeconds * 1000);
    }*/
}
