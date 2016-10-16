/*
*
*/
package com.mysweethome;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysweethome.properties.ThermostatProperties;

/**
 *
 * @author Ste
 */
class EmailGateway {
    static Logger logger = LoggerFactory.getLogger(EmailGateway.class);
    
    private static EmailGateway iInstance = null;
        
    private EmailGateway(){
    }

    public static synchronized EmailGateway getInstance() {
        if (iInstance == null) {
            iInstance = new EmailGateway();
        }
        return iInstance;
    }

    void sendEmail(String aEmail, String aBody) {

        //1) setup Mail Server Properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        //2) get Mail Session
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(aEmail));
            msg.setSubject("Status response");
            logger.info("SENDING EMAIL:  [{}] ", aBody);
            aBody = aBody.replaceAll("\n", "<br>");
            msg.setContent(aBody, "text/html");
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", ThermostatProperties.A, ThermostatProperties.B);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    
}
