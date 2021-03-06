/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome;

import com.mysweethome.devices.Button;
import com.mysweethome.devices.Led;
import com.mysweethome.devices.Relay;
import com.mysweethome.entity.Message;
import com.mysweethome.entity.SMS;
import com.mysweethome.entity.User;
import com.mysweethome.entity.Users;
import com.mysweethome.helper.Helper;
import com.mysweethome.properties.MySweetHomeProperties;
import com.mysweethome.states.State;
import com.mysweethome.states.ThermostatState;
import com.mysweethome.states.ThermostatStateFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import java.util.List;
import java.util.Calendar;
import java.util.Timer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import static java.lang.Thread.sleep;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ste
 */
public class Thermostat implements GpioPinListenerDigital {
    static Logger logger = LoggerFactory.getLogger(Thermostat.class);

    private ThermostatState iThermostatState;
    
    public static Calendar iRunningSince = Calendar.getInstance();
        
    private Led iHeaterStatusLed;
    private Relay iHeaterRelay;
    private Led iGreenLED;
    private Led iYellowLED;
    private Led iRedLED;
    private Led iBlueLED;
    private Button iModeButton;
    private Button iManualTherostat;
    
    private MessageHandler iMessageHandler;

    private Timer iTimer;
    
    ThermostatIgnitionShutdownTimerTask iStartTaskRepeated;
    ThermostatIgnitionShutdownTimerTask iStopTaskRepeated;
    ThermostatIgnitionShutdownTimerTask iStartSingleTask;
    ThermostatIgnitionShutdownTimerTask iStopSingleTask;
    
    public static long REPEAT_DAILY = 24 * 60 * 60 * 1000;
    public static boolean ON = true;
    public static boolean OFF = false;
    
    public final static String HELP_TEXT_USAGE = 
            "Examples:\r\n"
            + "1) on\r\n"
            + "2) off\r\n"
            + "3) manual\r\n"
            + "4) status\r\n"
            + "5) help\r\n"
            + "6) register +391234512345\r\n"
            + "7) ProgramDaily 6:15-7:45\r\n"
            + "8) ProgramDaily\r\n"
            + "9) Program 6:15-7:45\r\n"
            + "10) Program\r\n";

    public Thermostat() {
        try {
            iHeaterStatusLed = new Led(MySweetHomeProperties.GREEN_HEATER_STATUS_LED);
            iGreenLED = new Led(MySweetHomeProperties.GREEN_STATE_LED);
            iYellowLED = new Led(MySweetHomeProperties.YELLOW_STATE_LED);
            iRedLED = new Led(MySweetHomeProperties.RED_STATE_LED);
            iBlueLED = new Led(MySweetHomeProperties.BLUE_PROGRAM_LED);
            iHeaterRelay = new Relay(MySweetHomeProperties.HEATER_RELAY);
            iModeButton = new Button(MySweetHomeProperties.MODE_BUTTON);
            iModeButton.setInputListener(this);
            iManualTherostat = new Button(MySweetHomeProperties.MANUAL_THERMOSTAT_INPUT);
            iManualTherostat.setInputListener(this);
            
            iMessageHandler = new MessageHandler(this);
            //iSMSGateway.initialize(this);
            
            iTimer = new Timer(true);
            //iStartTask = new ThermostatIgnitionShutdownTimerTask(this, CommandType.ON_CONDITIONAL);
            //iStopTask = new ThermostatIgnitionShutdownTimerTask(this, CommandType.OFF_CONDITIONAL);
            
            iThermostatState = ThermostatStateFactory.createThermostatState(this, State.OFF);
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            logger.error("ERROR instantiating Thermostat", ex);
        }
    }
    


    
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        GpioPin tPin = event.getPin();
        System.out.println("--------------handleGpioPinDigitalStateChangeEvent---------");
        System.out.println(event.getPin());
        System.out.println(tPin.getMode());
        if (PinState.HIGH.equals(event.getState())) System.out.println("High Edge");
        if (PinState.LOW.equals(event.getState())) System.out.println("Low Edge");
        System.out.println("internal "+tPin.getPullResistance());
        System.out.println("-----------------------");
        if (tPin == iModeButton.getPin()) {
            if (PinState.HIGH.equals(event.getState())) {
                this.switchMode();
                logger.info("Thermostat: Mode Button pressed. Switched to MODE [{}]", this.getThermostatState().getState());
            } // ignore releasing button (LOW)

        } else if (tPin == iManualTherostat.getPin()) {
            if (PinState.HIGH.equals(event.getState())) {
                this.activateManualThermostat();
                logger.info("Thermostat: Manual thermostat change ON");
            } else if (PinState.LOW.equals(event.getState())) {
                this.deActivateManualThermostat();
                logger.info("Thermostat: Manual thermostat change OFF");
            }
        } else {
            logger.error("Thermostat ERROR! Detected a non registered input change");
        }

    }

    public String getStatus() {
        StringBuffer tResponse = new StringBuffer();
        tResponse.append("Running since: " + Helper.calToString(this.iRunningSince) + "\n");
        tResponse.append("State: " + this.getThermostatState().getState() + "\n");
        tResponse.append("ProgramDaily: " + this.getProgramDailyTimes() + "\n");
        tResponse.append("Program: " + this.getProgramTimes() + "\n");
        tResponse.append("Last Temp read: " + TemperatureStore.LastTemperatureReadString);
        return tResponse.toString();
    }

    //////////////////////
    // From Controller
    //////////////////////
    
    public void setThermostatState(ThermostatState aThermostatState){
        iThermostatState = aThermostatState;
    }
    
    public ThermostatState getThermostatState(){
        return iThermostatState;
    }
    
    public String getProgramDailyTimes(){
        if (iStartTaskRepeated == null) return "not active";
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar tLastStart = Calendar.getInstance();
        tLastStart.setTimeInMillis(iStartTaskRepeated.scheduledExecutionTime());
        Calendar tLastStop = Calendar.getInstance();
        tLastStop.setTimeInMillis(iStopTaskRepeated.scheduledExecutionTime());
        return sdf.format(tLastStart.getTime()) + "-" + sdf.format(tLastStop.getTime());
    }
    
    public String getProgramTimes(){
        if (iStartSingleTask == null) return "not active";
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar tLastStart = Calendar.getInstance();
        tLastStart.setTimeInMillis(iStartSingleTask.scheduledExecutionTime());
        Calendar tLastStop = Calendar.getInstance();
        tLastStop.setTimeInMillis(iStopSingleTask.scheduledExecutionTime());
        return sdf.format(tLastStart.getTime()) + "-" + sdf.format(tLastStop.getTime());
    }
    
    public void turnOn(){
        iThermostatState.turnON();
    }
    
    public void turnOff(){
        iThermostatState.turnOFF();
    }
    
    public void turnOnConditionally(){
        iThermostatState.turnONConditionally();
    }
    
    public void turnOffConditionally(){
        iThermostatState.turnOFFConditionally();
    }
    
    public void setToManual(){
        iThermostatState.setToManual();
    }
    
    public void switchMode() {
        //Controlled manually by pushing Mode button
        logger.info("Switching mode manually");
        iThermostatState.switchState();
    }

    private State setMode(State aMode) {
        //Used via SMS
        if (aMode != State.ON && aMode != State.MANUAL && aMode != State.OFF) {
            logger.error("Thermostat: setMode error: [{}]", aMode);
            return aMode;
        }
        
        switch (aMode){
            case ON:
                this.turnOn();
                break;
            case OFF:
                this.turnOff();
                break;
            case MANUAL:
                this.setToManual();
        }

        return this.getThermostatState().getState();
    }

    public void activateManualThermostat() {
        iThermostatState.activateManualThermostat();
    }

    public void deActivateManualThermostat() {
        iThermostatState.deActivateManualThermostat();
    }
    
    public void processReceivedCommand(CommandType tCommand, User aUser, SMS aSMS){
                if (CommandType.ON.equals(tCommand) || CommandType.OFF.equals(tCommand) || CommandType.MANUAL.equals(tCommand)){
                    executeCommand(tCommand, aUser, null);
                } else if (CommandType.STATUS.equals(tCommand)){
                    iMessageHandler.sendMessage(new Message(aUser, this.getStatus()), true);
                } else if (CommandType.HELP.equals(tCommand)){
                    iMessageHandler.sendMessage(new Message(aUser, HELP_TEXT_USAGE), true);
                } else if (CommandType.REGISTER_NUMBER.equals(tCommand)){
                    String[] tSplittedStringt = aSMS.getText().split(" ");
                    if (tSplittedStringt.length >= 2){
                        logger.info("REGISTER_NUMBER splitted string: [{}] [{}]", tSplittedStringt[0], tSplittedStringt[1]);
                        Users.addAuthorizedUser(tSplittedStringt[1]);
                    }else {
                        logger.error("REGISTER_NUMBER command not formatted correctly");
                    }
                } else if (CommandType.PROGRAM_DAILY.equals(tCommand)){
                    executeCommand(tCommand, aUser, aSMS.getText());
                }  else if (CommandType.PROGRAM.equals(tCommand)){
                    executeCommand(tCommand, aUser, aSMS.getText());
                }
    }
    
    public void executeCommand(CommandType aCmd, User aUser, String aText) {
        //used via SMS
        if (aCmd == null) 
            return;
        if (aCmd.equals(CommandType.ON)) {
            this.turnOn();
            iMessageHandler.sendMessage(new Message(aUser, "Turned ON remotely"), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        } else if (aCmd.equals(CommandType.ON_CONDITIONAL)) {
            this.turnOnConditionally();
            iMessageHandler.sendMessage(new Message(aUser, "Turned ON by schedule"), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        } else if (aCmd.equals(CommandType.MANUAL)) {
            this.setToManual();
            iMessageHandler.sendMessage(new Message(aUser, "Turned to MANUAL remotely"), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        } else if (aCmd.equals(CommandType.OFF)) {
            this.turnOff();
            iMessageHandler.sendMessage(new Message(aUser, "Turned OFF remotely"), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        } else if (aCmd.equals(CommandType.OFF_CONDITIONAL)) {
            this.turnOffConditionally();
            iMessageHandler.sendMessage(new Message(aUser, "Turned OFF by schedule"), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        }else if (aCmd.equals(CommandType.PROGRAM_DAILY)){
            programRepeatedIgnition(aText, this);
            iMessageHandler.sendMessage(new Message(aUser, "Programmed daily: " + aText), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        }else if (aCmd.equals(CommandType.PROGRAM)){
            programIgnition(aText, this);
            iMessageHandler.sendMessage(new Message(aUser, "Programmed for single event: " + aText), MySweetHomeProperties.ACKNOWLEDGE_REMOTE_COMMAND);
        }else{
            logger.error("Thermostat: Command via SMS not supported");
        }

    }
    
    private void programRepeatedIgnition(String aText, Thermostat aThermostat){
        
        //TODO refactor and put it in the command
        iBlueLED.turnOff();
        
        if (iStartTaskRepeated != null && iStopTaskRepeated != null){
            try {
                iStartTaskRepeated.cancel();
                iStopTaskRepeated.cancel();
                iStartTaskRepeated = null;
                iStopTaskRepeated = null;
            } catch (Throwable e) {
                logger.error("Exception cancelling Daily program. Doing nothing.");
            }
        }

        Calendar[] cal = parseStartStopDate(aText);
        Calendar startDateParsed;
        Calendar stopDateParsed;
        if (cal != null){
            startDateParsed = cal[0];
            stopDateParsed = cal[1];
        } else {
            return;
        }
        //TODO
        Helper.printCal("Scheduling daily ignition from: ", startDateParsed);
        Helper.printCal("Scheduling daily shutdown from: ", stopDateParsed);
        iStartTaskRepeated = new ThermostatIgnitionShutdownTimerTask(aThermostat, CommandType.ON_CONDITIONAL);
        iStopTaskRepeated = new ThermostatIgnitionShutdownTimerTask(aThermostat, CommandType.OFF_CONDITIONAL);
        iTimer.scheduleAtFixedRate(iStartTaskRepeated, startDateParsed.getTime(), REPEAT_DAILY);
        iTimer.scheduleAtFixedRate(iStopTaskRepeated, stopDateParsed.getTime(), REPEAT_DAILY);

        iBlueLED.turnOn();

    }
    
    private void programIgnition(String aText, Thermostat aThermostat){
        if (iStartSingleTask != null && iStopSingleTask != null){
            try {
                iStartSingleTask.cancel();
                iStopSingleTask.cancel();
                iStartSingleTask = null;
                iStopSingleTask = null;
            } catch (Throwable e) {
                logger.error("Exception cancelling Daily program. Doing nothing.");
            }
        }

        Calendar[] cal = parseStartStopDate(aText);
        Calendar startDateParsed;
        Calendar stopDateParsed;
        if (cal != null){
            startDateParsed = cal[0];
            stopDateParsed = cal[1];
        } else {
            return;
        }
        //TODO
        Helper.printCal("Scheduling single ignition from: ", startDateParsed);
        Helper.printCal("Scheduling single shutdown from: ", stopDateParsed);
        iStartSingleTask = new ThermostatIgnitionShutdownTimerTask(aThermostat, CommandType.ON_CONDITIONAL);
        iStopSingleTask = new ThermostatIgnitionShutdownTimerTask(aThermostat, CommandType.OFF_CONDITIONAL);
        iTimer.schedule(iStartSingleTask, startDateParsed.getTime());
        iTimer.schedule(iStopSingleTask, stopDateParsed.getTime());

        iBlueLED.turnOn();

    }
    
    /**
     * 
     * @param aTimeInterval
     * @return Calendar[]
     * 
     *  cal[0] --> startDate
     *  cal[1} --> stopDate
     */
    private Calendar[] parseStartStopDate(String aTimeInterval){
        if (aTimeInterval == null || "".equals(aTimeInterval)) return null;
        aTimeInterval = aTimeInterval.trim();
        aTimeInterval = aTimeInterval.replaceAll("\\s+", " ");
        Calendar[] cal = new Calendar[2];
        String[] tSplittedStringt = aTimeInterval.split(" ");
        if (tSplittedStringt.length == 2){
            String timeInterval = tSplittedStringt[1]; //ex. 6:00-8:30
            String[] tSplittedTime = timeInterval.split("-"); //ex. 6:00
            if (tSplittedTime.length == 2){
                try {Calendar tNow = Helper.getThisInstant();
                    Helper.printCal("Now", tNow);
                    Calendar startDateParsed = Helper.parseTime(tSplittedTime[0]);
                    Calendar stopDateParsed = Helper.parseTime(tSplittedTime[1]);
                    
                    if (startDateParsed == null || stopDateParsed == null) return null;

                    if (tNow.after(startDateParsed)){
                        //schedule ON from tomorrow
                        startDateParsed.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    if (tNow.after(stopDateParsed)){
                        //schedule OFF from tomorrow
                        stopDateParsed.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    Helper.printCal("startDateParse", startDateParsed);
                    Helper.printCal("stopDateParse", stopDateParsed);
                    
                    cal[0] = startDateParsed;
                    cal[1] = stopDateParsed;
                    
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    return null;
                }
            } else{
                logger.error("Program - parse date bad forma");
                return null;
            }
        } else {
            logger.error("Program - parse bad forma");
            return null;
        }

        return cal;
    }

    public Led getHeaterStatusLed() {
        return iHeaterStatusLed;
    }

    public void setHeaterStatusLed(Led aHeaterStatusLed) {
        this.iHeaterStatusLed = aHeaterStatusLed;
    }

    public Relay getHeaterRelay() {
        return iHeaterRelay;
    }

    public void setHeaterRelay(Relay aHeaterRelay) {
        this.iHeaterRelay = aHeaterRelay;
    }

    public Led getLedGreen() {
        return iGreenLED;
    }
    
    public Led getLedBlue() {
        return iBlueLED;
    }

    public void setLedGreen(Led aLedGreen) {
        this.iGreenLED = aLedGreen;
    }

    public Led getLedYellow() {
        return iYellowLED;
    }

    public void setLedYellow(Led aLedYellow) {
        this.iYellowLED = aLedYellow;
    }

    public Led getLedRed() {
        return iRedLED;
    }

    public void setLedRed(Led aLedRed) {
        this.iRedLED = aLedRed;
    }
    
    public void stop() {
        try {
            if (iTimer != null){
                iTimer.cancel();
            }
            if (iHeaterStatusLed != null) {
                logger.warn("Thermostat: Turning off Status LED");
                iHeaterStatusLed.close();
                iHeaterStatusLed = null;
            }
            if (iHeaterRelay != null) {
                logger.warn("Thermostat: Turning off Relay");
                iHeaterRelay.close();
                iHeaterRelay = null;
            }
            if (iGreenLED != null) {
                logger.warn("Thermostat: Turning off green LED");
                iGreenLED.close();
                iGreenLED = null;
            }
            if (iYellowLED != null) {
                logger.warn("Thermostat: Turning off yellow LED");
                iYellowLED.close();
                iYellowLED = null;
            }
            if (iRedLED != null) {
                logger.warn("Thermostat: Turning off red LED");
                iRedLED.close();
                iRedLED = null;
            }
            if (iModeButton != null) {
                logger.warn("Thermostat: Turning off Mode Button");
                iModeButton.setInputListener(null);
                iModeButton.close();
                iModeButton = null;
            }
            if (iManualTherostat != null) {
                logger.warn("Thermostat: Turning off manual thermostat input");
                iManualTherostat.setInputListener(null);
                iManualTherostat.close();
                iManualTherostat = null;
            }
            if (iMessageHandler != null){
                logger.warn("Thermostat: Turning off MessageHandler");
                iMessageHandler.stop();
                iMessageHandler = null;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    
     private void waitABit(int a) {
        try {
            Thread.sleep(a);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
     
    public void testRelay() {
        for (int i = 1; i < 8; i++) {
            try {
                logger.info("Turning on [{}]", i);
                iHeaterRelay.turnOn();
                sleep(500);
                logger.info("Turning off [{}]", i);
                iHeaterRelay.turnOff();
                sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
