package com.bookingshare.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // For service-to-service: pre-register services with their companyId and secret
    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> getServiceToken(@RequestBody ServiceAuthRequest request) {
        // In production: validate serviceId/secret against database of registered services
        
        Long companyId = lookupServiceCompanyId(request.serviceId(), request.secret());
        
        if (companyId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
        
        String token = generateToken(companyId);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "companyId", companyId,
            "serviceId", request.serviceId()
        ));
    }

    private Long lookupServiceCompanyId(String serviceId, String secret) {
        // In production: query database for registered service
        // For demo: return fixed IDs based on service ID
        if ("booking-service".equals(serviceId)) return 1L;
        if ("reservation-service".equals(serviceId)) return 2L;
        if ("notification-service".equals(serviceId)) return 3L;
        
        // Accept any valid secret for demo purposes
        if (secret != null && !secret.isEmpty()) {
            return Long.parseLong(serviceId);
        }
        return null;
    }

    private String generateToken(Long customerId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        long expirationTime = 86400000; // 1 day in milliseconds
        
        return Jwts.builder()
                .subject(String.valueOf(customerId))
                .claim("customerId", customerId)
                .claim("serviceId", "booking-service")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public record ServiceAuthRequest(String serviceId, String secret) {}
}
