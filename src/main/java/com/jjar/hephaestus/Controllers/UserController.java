package com.jjar.hephaestus.Controllers;

import com.jjar.hephaestus.Dto.LoginRequest;
import com.jjar.hephaestus.Dto.UserRequest;
import com.jjar.hephaestus.Entity.User;
import com.jjar.hephaestus.Service.UserService;
import com.jjar.hephaestus.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        User savedUser = userService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Get user by email
     * @param email User email
     * @return User details if found
     */
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Verify login credentials
     * @param loginRequest Contains email and password
     * @return Success message if valid credentials
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.findByEmail(loginRequest.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            try {
                String token = JWTUtil.generateToken(user.get().getEmail());
                return ResponseEntity.ok().body("Bearer " + token);
            } catch (Exception e){
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@RequestBody UserRequest userRequest) {
        Optional<User> user = userService.findByEmail(userRequest.getEmail());
        if (user.isPresent()) {
            return ResponseEntity.status(409).body("User with this email already exists");
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            return ResponseEntity.status(400).body("Password is required");
        }
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            return ResponseEntity.status(400).body("Email is required");
        }

        userService.registerUser(userRequest);

        return ResponseEntity.status(201).body("User registered successfully");
    }


}

