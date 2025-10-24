package com.hotel.bookingservice.controller;

import com.hotel.bookingservice.entity.User;
import com.hotel.bookingservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/simple")
public class SimpleAuthController {

    private static final Logger log = LoggerFactory.getLogger(SimpleAuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SimpleAuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> simpleRegister(@RequestBody Map<String, String> request) {
        try {
            log.info("Simple registration attempt for user: {}", request.get("username"));

            String username = request.get("username");
            String password = request.get("password");
            String role = request.get("role");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }

            if (userRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            User.Role userRole;
            try {
                userRole = User.Role.valueOf(role != null ? role.toUpperCase() : "USER");
            } catch (IllegalArgumentException e) {
                userRole = User.Role.USER;
            }

            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(userRole)
                    .build();

            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "username", savedUser.getUsername(),
                    "role", savedUser.getRole().name()
            ));

        } catch (Exception e) {
            log.error("Error in simple registration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}