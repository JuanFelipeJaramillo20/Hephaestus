package com.jjar.hephaestus.Configuration;

import com.jjar.hephaestus.Email.ResendMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendMailSenderConfig {

    private final String apiKey;

    public ResendMailSenderConfig(@Value("${resend.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean
    public ResendMailSender resendMailSender() {
        return new ResendMailSender(apiKey); // Use the injected API key
    }
}
