package com.jjar.hephaestus.Service;

import com.jjar.hephaestus.Entity.NewsletterSubscriber;
import com.jjar.hephaestus.Repository.NewsletterSubscriberRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

@Service
public class NewsletterService {
    private final NewsletterSubscriberRepository repository;
    private final MailSender mailSender; // Inject the ResendMailSender

    public NewsletterService(NewsletterSubscriberRepository repository, MailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    public void subscribe(String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already subscribed.");
        }

        // Save the subscriber to the database
        NewsletterSubscriber subscriber = new NewsletterSubscriber();
        subscriber.setEmail(email);
        repository.save(subscriber);

        // Send a confirmation email
        sendConfirmationEmail(email);
    }

    private void sendConfirmationEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@jjar.lat");
        message.setTo(email);
        message.setSubject("Subscription Confirmation");
        message.setText("Thank you for subscribing to our newsletter! We're excited to keep you updated.");

        mailSender.send(message);
    }
}
