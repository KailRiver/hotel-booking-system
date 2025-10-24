package com.hotel.bookingservice.controller;

import com.hotel.bookingservice.entity.User;
import com.hotel.bookingservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/status")
    public String status() {
        log.info("Test endpoint called");
        return "Booking Service is running! Database connection: OK";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    @GetMapping("/db-check")
    public String dbCheck() {
        try {
            long userCount = userRepository.count();
            return "Database connection: OK, Users count: " + userCount;
        } catch (Exception e) {
            return "Database connection: FAILED - " + e.getMessage();
        }
    }
}