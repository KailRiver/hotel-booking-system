package com.hotel.hotelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator endpoints - разрешаем всем
                        .requestMatchers("/actuator/**", "/h2-console/**").permitAll()

                        // Public test endpoints - разрешаем всем
                        .requestMatchers("/test/status", "/test/public").permitAll()

                        // Internal endpoints for service-to-service communication - разрешаем всем
                        .requestMatchers("/api/rooms/*/confirm-availability",
                                "/api/rooms/*/release",
                                "/api/rooms/*/confirm-booking").permitAll()

                        // User endpoints - требуют ROLE_USER или ROLE_ADMIN
                        .requestMatchers("/api/hotels", "/api/rooms", "/api/rooms/recommend").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Admin endpoints - требуют ROLE_ADMIN
                        .requestMatchers("/api/hotels/**", "/api/rooms/**").hasAuthority("ROLE_ADMIN")

                        // Secure test endpoint - требует аутентификации
                        .requestMatchers("/test/secure").authenticated()

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}