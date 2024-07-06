package com.resumeanalysis.resumeanalyser.controller;






import org.apache.commons.io.IOUtils;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

public class ResumeDownloadingController {

    public static void saveAttachment(BodyPart bodyPart, String subject) throws MessagingException, IOException {
        String fileName = bodyPart.getFileName();
        InputStream is = bodyPart.getInputStream();
        

        // Get current date
        LocalDate currentDate = LocalDate.now();
        String dateFolder = currentDate.toString();

        // Ensure the date-based directory exists
        File dateDir = new File("downloads/" + dateFolder);
        if (!dateDir.exists()) {
            boolean dirCreated = dateDir.mkdirs(); // Creates the directory, including any necessary but nonexistent parent directories
            if (dirCreated) {
                System.out.println("Directory 'downloads/" + dateFolder + "' created successfully.");
            } else {
                System.err.println("Failed to create directory 'downloads/" + dateFolder + "'.");
                return; // Exit if the directory cannot be created
            }
        } else {
            System.out.println("Directory 'downloads/" + dateFolder + "' already exists.");
        }
        
        
     // Create subject-based directory inside date-based directory
//        String sanitizedSubject = sanitizeFileName(subject);
        File subjectDir = new File(dateDir, subject);
        if (!subjectDir.exists()) {
            boolean dirCreated = subjectDir.mkdirs();
            if (dirCreated) {
                System.out.println("Directory 'downloads/" + dateFolder + "/" + subject + "' created successfully.");
            } else {
                System.err.println("Failed to create directory 'downloads/" + dateFolder + "/" + subject + "'.");
                return;
            }
        } else {
            System.out.println("Directory 'downloads/" + dateFolder + "/" + subject + "' already exists.");
        }

        // Save attachment to file
        File file = new File(subjectDir, fileName);
        System.out.println("Saving attachment to: " + file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        IOUtils.copy(is, fos);
        fos.close();
        is.close();
        System.out.println("Saved attachment: " + fileName);

        // After saving, you can trigger further processing (e.g., send to ML model module)
        // MLModelIntegration.sendToModel(file.getAbsolutePath()); // Example integration with ML model
    }
}

