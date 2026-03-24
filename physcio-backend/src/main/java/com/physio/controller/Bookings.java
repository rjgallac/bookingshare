package com.physio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.physio.client.BookingServiceClient;
import com.physio.dto.SlotDto;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/bookings")
public class Bookings {

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @GetMapping
    public ResponseEntity<List<SlotDto>> getBookings(){
        // get token
        ResponseEntity<String> tokenResponse =   bookingServiceClient.getAccessToken("booking-service", "my-secret");
        ObjectMapper mapper = new ObjectMapper();
        String token = mapper.readTree(tokenResponse.getBody()).get("token").asText();
        // get slots
        List<SlotDto> slots = bookingServiceClient.getSlotsWithAuth(
            "consultation",
            "2026-03-25", 
            "2026-03-31", 
            token // JWT token from /api/auth/token
        );
        return ResponseEntity.ok(slots);
    }
}
