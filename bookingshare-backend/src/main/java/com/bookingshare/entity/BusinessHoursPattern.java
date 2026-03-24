package com.bookingshare.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "business_hours_patterns")
public class BusinessHoursPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    private String serviceType;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "pattern_days", joinColumns = @JoinColumn(name = "pattern_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> days = new HashSet<>();

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false, length = 20)
    private String slotTypeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public BusinessHoursPattern() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Set<DayOfWeek> getDays() { return days; }
    public void setDays(Set<DayOfWeek> days) { this.days = days; }

    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }

    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }

    public String getSlotTypeValue() { return slotTypeValue; }
    public void setSlotTypeValue(String slotTypeValue) { this.slotTypeValue = slotTypeValue; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
