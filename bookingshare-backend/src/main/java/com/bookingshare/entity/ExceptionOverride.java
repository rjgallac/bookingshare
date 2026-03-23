package com.bookingshare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exception_overrides")
public class ExceptionOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExceptionType type;

    @Column(length = 50)
    private String serviceType;

    @Column(name = "pattern_id")
    private Long patternId;

    private LocalTime openTime;

    private LocalTime closeTime;

    public enum ExceptionType {
        CLOSED,
        HOURS_CHANGED
    }

    public ExceptionOverride() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public ExceptionType getType() { return type; }
    public void setType(ExceptionType type) { this.type = type; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Long getPatternId() { return patternId; }
    public void setPatternId(Long patternId) { this.patternId = patternId; }

    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }

    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }
}
