package com.bookingshare.repository;

import com.bookingshare.entity.SlotConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlotConfigurationRepository extends JpaRepository<SlotConfiguration, Long> {
    Optional<SlotConfiguration> findByServiceType(String serviceType);
}
