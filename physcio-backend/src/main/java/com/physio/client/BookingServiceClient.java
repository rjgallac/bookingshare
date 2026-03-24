package com.physio.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.physio.dto.SlotDto;

@Component
public class BookingServiceClient {

    @Value("${booking.service.url}")
    private String bookingServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> getSlots(String serviceType, String startDate, String endDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        String url = bookingServiceUrl + "/api/bookings/slots?serviceType=" + 
                     serviceType + "&startDate=" + startDate + "&endDate=" + endDate;
        
        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    public ResponseEntity<String> createPattern(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        
        String url = bookingServiceUrl + "/api/bookings/patterns";
        
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity<String> createException(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        
        String url = bookingServiceUrl + "/api/bookings/exceptions";
        
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity<String> getAccessToken(String serviceId, String secret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = "{\"serviceId\":\"" + serviceId + "\",\"secret\":\"" + secret + "\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        
        String url = bookingServiceUrl + "/api/auth/token";
        
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public List<SlotDto> getSlotsWithAuth(String serviceType, String startDate, String endDate, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        String url = bookingServiceUrl + "/api/bookings/slots?serviceType=" + 
                     serviceType + "&startDate=" + startDate + "&endDate=" + endDate;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        List<SlotDto> slots = restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<SlotDto>>() {}).getBody();

        return slots;
    }
}
