package com.bookingshare.controller;

import com.bookingshare.entity.Booking;
import com.bookingshare.entity.Company;
import com.bookingshare.repository.BookingRepository;
import com.bookingshare.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody BookingCreateRequest request,
            @AuthenticationPrincipal Long companyId) {
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        Booking booking = new Booking();
        booking.setCompany(company);
        booking.setCustomerEmail(request.customerEmail());
        booking.setStartTime(LocalDateTime.parse(request.startTime()));
        booking.setEndTime(LocalDateTime.parse(request.endTime()));
        booking.setServiceType(request.serviceType());
        
        if (request.status() != null) {
            booking.setStatus(Booking.BookingStatus.valueOf(request.status().toUpperCase()));
        }
        
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Booking>> getByCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) Booking.BookingStatus status) {
        
        List<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByCompanyIdAndStatus(companyId, status);
        } else {
            bookings = bookingRepository.findByCompanyId(companyId);
        }
        
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Booking>> getByCustomer(@PathVariable String email) {
        List<Booking> bookings = bookingRepository.findByCustomerEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody BookingUpdateRequest request) {
        
        return bookingRepository.findById(id).map(existing -> {
            if (request.status() != null) {
                existing.setStatus(Booking.BookingStatus.valueOf(request.status().toUpperCase()));
            }
            if (request.endTime() != null) {
                existing.setEndTime(LocalDateTime.parse(request.endTime()));
            }
            
            Booking updated = bookingRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.findById(id).ifPresent(booking -> {
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            });
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookings(
            @AuthenticationPrincipal Long companyId,
            @RequestParam(required = false) Booking.BookingStatus status) {
        
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByCompanyId(companyId).stream()
                .filter(b -> b.getStartTime().isAfter(now))
                .filter(b -> status == null || b.getStatus() == status)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(bookings);
    }

    public record BookingCreateRequest(
            String customerEmail,
            String startTime,
            String endTime,
            String serviceType,
            String status
    ) {}

    public record BookingUpdateRequest(String status, String endTime) {}
}
