package com.jjar.hephaestus.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "newsletter_subscribers")
public class NewsletterSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean subscribed = true;
}
