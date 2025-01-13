package com.jjar.hephaestus.Controllers;

import com.jjar.hephaestus.Dto.SupportRequest;
import com.jjar.hephaestus.Email.ResendMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Value("${support.email}")
    private String supportEmail;

    @Autowired
    private ResendMailSender mailSender;

    @PostMapping
    public ResponseEntity<String> handleSupportRequest(@RequestBody SupportRequest supportRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(supportEmail);
            message.setSubject("Support Request from " + supportRequest.getName());
            message.setText("Message: " + supportRequest.getMessage() + "\n\nEmail: " + supportRequest.getEmail());

            mailSender.send(message);

            return ResponseEntity.ok("Support request sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send support request");
        }
    }
}

