package com.hotel.hotelservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/status")
    public String status() {
        log.info("Hotel Service test endpoint called");
        return "Hotel Service is running! Database connection: OK";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint - no auth required";
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "This is a secure endpoint - auth required";
    }
}