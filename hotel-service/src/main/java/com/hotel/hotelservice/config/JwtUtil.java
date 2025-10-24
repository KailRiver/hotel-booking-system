package com.hotel.hotelservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public String extractUsername(String token) {
        try {
            String payload = decodeTokenPayload(token);
            return extractClaim(payload, "sub");
        } catch (Exception e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            String payload = decodeTokenPayload(token);
            return extractClaim(payload, "role");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            String username = extractUsername(token);
            String role = extractRole(token);

            return username != null && !username.isEmpty() &&
                    role != null && !role.isEmpty() &&
                    (role.equals("USER") || role.equals("ADMIN"));
        } catch (Exception e) {
            return false;
        }
    }

    private String decodeTokenPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token");
            }
            return new String(Base64.getUrlDecoder().decode(parts[1]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private String extractClaim(String payload, String claimName) {
        try {
            String searchString = "\"" + claimName + "\":\"";
            int startIndex = payload.indexOf(searchString);
            if (startIndex == -1) {
                return null;
            }
            startIndex += searchString.length();
            int endIndex = payload.indexOf("\"", startIndex);
            return payload.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }
}