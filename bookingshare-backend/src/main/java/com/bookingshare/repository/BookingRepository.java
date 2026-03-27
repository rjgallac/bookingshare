package com.bookingshare.repository;

import com.bookingshare.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCompanyId(Long companyId);
    List<Booking> findByCustomerEmail(String customerEmail);
    List<Booking> findByCompanyIdAndStatus(Long companyId, Booking.BookingStatus status);
    List<Booking> findByStartTimeBetweenAndCompany(LocalDateTime start, LocalDateTime end, com.bookingshare.entity.Company company);
}
