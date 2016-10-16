/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ste
 */
public class SMS implements Comparable{
    static Logger logger = LoggerFactory.getLogger(SMS.class);

    private int iPosition;
    private String iHeader;
    private String iText;
    private String iSender;
    private Date iDate;
    
    public SMS(){
        super();
    }

    public int getPosition() {
        return iPosition;
    }

    public void setPosition(int aPosition) {
        this.iPosition = aPosition;
    }

    public String getText() {
        return iText == null ? "" : iText;
    }

    public void setText(String aText) {
        this.iText = aText;
    }

    public String getHeader() {
        return iHeader == null ? "" : iHeader;
    }

    public void setHeader(String aHeader) {
        this.iHeader = aHeader;
    }

    public String getSender() {
        return iSender;
    }

    public void setSender(String aSender) {
        this.iSender = aSender;
    }

    public Date getDate() {
        return iDate;
    }

    public void setDate(Date aDate) {
        this.iDate = aDate;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
        return "[SMS]: " + iPosition + " [Sender]: " + iSender + " [Date]: " + sdf.format(iDate) + " [Text]: " + iText;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof SMS) return 0;
        Date tDateObj = ((SMS)o).getDate();
        return iDate.compareTo(tDateObj);
    }

    /*boolean senderAuthorized() {
        if (iSender == null || iSender.equals("")){
            System.out.println("Sender unauthorized: missing!");
            return false;
        }
        if (AuthorizedUsers.getAllUsers().contains(iSender)) {
            return true;
        }
        System.out.println("Sender unauthorized! "+iSender);
        return false;
    }*/

    public boolean isDateValid() {
        if (iSender == null || iSender.equals("") || iDate == null || iDate.equals("") || iText == null || iText.equals("")) return false;
        Date now = new Date();
        Date beginInterval = new Date(now.getTime()-  3 * 60 * 60 * 1000);
        Date endInterval = new Date(now.getTime()+  3 * 60 * 60 * 1000);
        if (iDate.before(beginInterval) || iDate.after(endInterval)){
            logger.info("Date of the message [{}] is not valid! Date: [{}] Message Date: [{}] ", iPosition, now, iDate);
            return false;
        }else
            return true;
    }
    

}
