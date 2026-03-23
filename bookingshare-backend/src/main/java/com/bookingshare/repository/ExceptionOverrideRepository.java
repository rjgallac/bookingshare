package com.bookingshare.repository;

import com.bookingshare.entity.ExceptionOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionOverrideRepository extends JpaRepository<ExceptionOverride, Long> {
}
