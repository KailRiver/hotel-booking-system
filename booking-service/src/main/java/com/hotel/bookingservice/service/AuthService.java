package com.hotel.bookingservice.service;

import com.hotel.bookingservice.config.JwtUtil;
import com.hotel.bookingservice.dto.AuthRequest;
import com.hotel.bookingservice.dto.AuthResponse;
import com.hotel.bookingservice.dto.UserDto;
import com.hotel.bookingservice.entity.User;
import com.hotel.bookingservice.exception.BookingException;
import com.hotel.bookingservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(UserDto userDto) {
        log.info("Registering user: {}", userDto.getUsername());

        try {
            if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
                throw new BookingException("Username already exists");
            }

            // Validate role
            User.Role role;
            try {
                role = User.Role.valueOf(userDto.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BookingException("Invalid role. Must be USER or ADMIN");
            }

            User user = User.builder()
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .role(role)
                    .build();

            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getUsername());

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("role", user.getRole().name());

            String jwtToken = jwtUtil.generateToken(extraClaims, user);

            return AuthResponse.builder()
                    .token(jwtToken)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            throw e;
        }
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Authenticating user: {}", authRequest.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new BookingException("User not found"));

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("role", user.getRole().name());

            String jwtToken = jwtUtil.generateToken(extraClaims, user);

            return AuthResponse.builder()
                    .token(jwtToken)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage());
            throw new BookingException("Authentication failed: " + e.getMessage());
        }
    }
}