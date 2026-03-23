package com.bookingshare.repository;

import com.bookingshare.entity.BusinessHoursPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessHoursPatternRepository extends JpaRepository<BusinessHoursPattern, Long> {
}
