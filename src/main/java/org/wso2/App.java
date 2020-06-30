package org.wso2;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Hello world!
 */
public class App {
    public static final String CONFIGS_PROPERTIES = "configs.properties";
    public static final String LOG4J_PROPERTIES = "log4j.properties";
    public static Properties configs = new Properties();
    private final static Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Hello World!");
        initialize();

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", configs.getProperty("mail.smtp.host"));
        properties.setProperty("mail.smtp.port", configs.getProperty("mail.smtp.port"));
        properties.setProperty("mail.smtp.auth", configs.getProperty("mail.smtp.auth"));
        properties.setProperty("mail.smtp.starttls.enable", configs.getProperty("mail.smtp.starttls.enable"));

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configs.getProperty("mail.smtp.user"), configs.getProperty("mail.smtp.password"));
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(configs.getProperty("from")));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(configs.getProperty("to")));
            message.setSubject("Ping");
            message.setText("Hello, this is a sample mail.");

            Transport.send(message);
            log.info("Message sent successfully");
        } catch (SendFailedException se) {
            log.error("SendFailedException... ", se);
        } catch (MessagingException me){
            log.error("MessagingException...",me);
        } catch (Exception e){
            log.error("Error... ", e);
        }
    }

    public static void initialize() {
        log.info("Initializing the client...");
        String filePath = Paths.get(".", LOG4J_PROPERTIES).toString();
        log.info("Reading log4j.properties file : " + filePath);
        PropertyConfigurator.configure(filePath);

        filePath = Paths.get(".", CONFIGS_PROPERTIES).toString();
        log.info("Reading properties file : " + filePath);
        try {
            InputStream inputStream = new FileInputStream(filePath);
            if (inputStream != null) {
                configs.load(inputStream);
            } else {
                log.error("Input stream null.");
            }
        } catch (FileNotFoundException e) {
            log.error("Cannot find the configs.properties file.", e);
        } catch (IOException e) {
            log.error("Error while loading the properties from file.", e);
        }
        log.info("Finished initialization.");
    }
}
