package com.jjar.hephaestus.Email;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class ResendMailSender implements MailSender {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private final String apiKey;
    private final RestTemplate restTemplate;

    public ResendMailSender(String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        Map<String, Object> request = new HashMap<>();
        request.put("from", "support@jjar.lat");
        request.put("to", simpleMessage.getTo());
        request.put("subject", simpleMessage.getSubject());
        request.put("text", simpleMessage.getText());

        try {
            restTemplate.postForEntity(RESEND_API_URL, createHttpEntity(request), String.class);
        } catch (Exception e) {
            throw new MailException("Failed to send email via Resend") {
                @Override
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        for (SimpleMailMessage simpleMessage : simpleMessages) {
            send(simpleMessage);
        }
    }

    private HttpEntity<Map<String, Object>> createHttpEntity(Map<String, Object> requestBody) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        return new org.springframework.http.HttpEntity<>(requestBody, headers);
    }

    @PostConstruct
    public void validateApiKey() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("Resend API key is not set. Please configure 'resend.api.key'.");
        }
    }
}

