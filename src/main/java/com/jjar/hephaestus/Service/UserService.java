package com.jjar.hephaestus.Service;

import com.jjar.hephaestus.Dto.UserRequest;
import com.jjar.hephaestus.Email.ResendMailSender;
import com.jjar.hephaestus.Entity.User;
import com.jjar.hephaestus.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResendMailSender resendMailSender;

    public User registerUser(UserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        User createdUser = userRepository.save(user);
        sendConfirmationEmail(createdUser.getEmail());
        return createdUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private void sendConfirmationEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@jjar.lat");
        message.setTo(email);
        message.setSubject("Sign-up Confirmation");
        message.setText("Thank you for signing up to our website! We're excited to have you onboard. Now you can login in and enjoy the benefits!");

        resendMailSender.send(message);
    }
}
