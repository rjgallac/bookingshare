package com.bookingshare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "slot_configurations")
public class SlotConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlotSplitStrategy splitStrategy;

    private int durationMinutes;

    private int bufferMinutes = 0;

    private int earliestBookingAheadHours = 24;

    private boolean allowSameDayBookings = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public enum SlotSplitStrategy {
        FIXED_DURATION,
        TIME_BLOCK,
        CUSTOM_INTERVAL
    }

    public SlotConfiguration() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public SlotSplitStrategy getSplitStrategy() { return splitStrategy; }
    public void setSplitStrategy(SlotSplitStrategy splitStrategy) { this.splitStrategy = splitStrategy; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getBufferMinutes() { return bufferMinutes; }
    public void setBufferMinutes(int bufferMinutes) { this.bufferMinutes = bufferMinutes; }

    public int getEarliestBookingAheadHours() { return earliestBookingAheadHours; }
    public void setEarliestBookingAheadHours(int earliestBookingAheadHours) { this.earliestBookingAheadHours = earliestBookingAheadHours; }

    public boolean isAllowSameDayBookings() { return allowSameDayBookings; }
    public void setAllowSameDayBookings(boolean allowSameDayBookings) { this.allowSameDayBookings = allowSameDayBookings; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
