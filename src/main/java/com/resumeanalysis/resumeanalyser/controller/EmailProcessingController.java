package com.resumeanalysis.resumeanalyser.controller;




import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;
//import org.springframework.stereotype.Service;




@RestController
@RequestMapping("/email")
public class EmailProcessingController {
	
	private static Logger log =LoggerFactory.getLogger(EmailProcessingController.class);

	@GetMapping("/process")
    public String processEmail() {
		log.info("In processing email :");
//		System.out.println("inside the email");
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", "resumechecknanalysis@gmail.com", "jtii kzqw yjbw jnip");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Get messages from the last 24 hours
//            Message[] messages = inbox.search(new AndTerm(
//                    new FlagTerm(new Flags(Flags.Flag.SEEN), false), // Unseen messages
//                    new ReceivedDateTerm(ComparisonTerm.GT, new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)) // Last 24 hours
//            ));
            Message[] messages = inbox.getMessages();
            
            for (Message message : messages) {
                if (!message.isSet(Flags.Flag.SEEN)) {
                	String subject = message.getSubject();
                    saveAttachments(message,subject);
                    message.setFlag(Flags.Flag.SEEN, true);
                    System.out.println("after making flag as seen");
                }
            }

            inbox.close(true);
            store.close();
            
            return "Emails processed successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing emails: " + e.getMessage();
        }
    }

    private void saveAttachments(Message message, String subject) throws MessagingException, IOException {
        Object content = message.getContent();
        
        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                        bodyPart.getFileName().endsWith(".pdf")) {
//                	bodyPart.
                    ResumeDownloadingController.saveAttachment(bodyPart,subject);
                }
            }
        }
    }
}


