package com.PotYourHoles.potyourholes.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServices {

    // Fetch SendGrid API key from environment
    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    // Fetch sender email from environment
    @Value("${SENDER_EMAIL}")
    private String senderEmail;

    /**
     * Sends a thank-you email to the user after booking an appointment.
     *
     * @param toEmail Recipient's email address
     * @param name    Recipient's name
     */
    public void sendThankYouEmail(String toEmail, String name) {
        Email from = new Email(senderEmail); // Use env variable
        String subject = "Thank You for Booking Your Appointment!";
        Email to = new Email(toEmail);

        String bodyText = String.format(
                "Hi %s,\n\nThank you for booking an appointment with PotYourHoles! " +
                        "Our team will contact you shortly to confirm your inspection details.\n\n" +
                        "Best regards,\nTeam PotYourHoles", name);

        Content content = new Content("text/plain", bodyText);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("✅ Email sent successfully!");
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());
        } catch (IOException ex) {
            System.err.println("❌ Failed to send email: " + ex.getMessage());
        }
    }
}
