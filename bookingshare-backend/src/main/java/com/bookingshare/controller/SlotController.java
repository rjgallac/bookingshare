package com.bookingshare.controller;

import com.bookingshare.entity.*;
import com.bookingshare.repository.BusinessHoursPatternRepository;
import com.bookingshare.repository.ExceptionOverrideRepository;
import com.bookingshare.repository.SlotConfigurationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class SlotController {

    @Autowired
    private BusinessHoursPatternRepository patternRepository;

    @Autowired
    private ExceptionOverrideRepository exceptionRepository;

    @Autowired
    private SlotConfigurationRepository configRepository;

    @PostMapping("/patterns")
    public ResponseEntity<BusinessHoursPattern> createPattern(@RequestBody PatternRequest request) {
        BusinessHoursPattern pattern = new BusinessHoursPattern();
        pattern.setName(request.name());
        pattern.setServiceType(request.serviceType());
        Set<DayOfWeek> days = request.days().stream()
                .map(d -> DayOfWeek.valueOf(d.toUpperCase()))
                .collect(Collectors.toSet());
        pattern.setDays(days);
        pattern.setOpenTime(LocalTime.parse(request.openTime()));
        pattern.setCloseTime(LocalTime.parse(request.closeTime()));
        pattern.setSlotTypeValue(request.slotType().toUpperCase());

        BusinessHoursPattern saved = patternRepository.save(pattern);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/patterns")
    public ResponseEntity<List<BusinessHoursPattern>> getAllPatterns() {
        return ResponseEntity.ok(patternRepository.findAll());
    }

    @PostMapping("/exceptions")
    public ResponseEntity<ExceptionOverride> createException(@RequestBody ExceptionRequest request) {
        ExceptionOverride exception = new ExceptionOverride();
        exception.setDate(LocalDate.parse(request.date()));
        exception.setType(ExceptionOverride.ExceptionType.valueOf(request.type().toUpperCase()));
        exception.setServiceType(request.serviceType());

        if (request.patternId() != null && !request.patternId().isEmpty()) {
            exception.setPatternId(Long.parseLong(request.patternId()));
        }

        if (request.openTime() != null && request.closeTime() != null) {
            exception.setOpenTime(LocalTime.parse(request.openTime()));
            exception.setCloseTime(LocalTime.parse(request.closeTime()));
        }

        ExceptionOverride saved = exceptionRepository.save(exception);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam String serviceType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<SlotDto> slots = calculateAvailableSlots(serviceType, startDate, endDate);
        return ResponseEntity.ok(slots);
    }

    private List<SlotDto> calculateAvailableSlots(String serviceType, LocalDate startDate, LocalDate endDate) {
        var patterns = patternRepository.findAll();
        var exceptions = exceptionRepository.findAll().stream()
                .filter(e -> e.getServiceType() == null || e.getServiceType().equals(serviceType))
                .toList();

        return patterns.stream()
                .filter(p -> p.getServiceType().equals(serviceType))
                .flatMap(pattern -> generateSlotsForPattern(pattern, startDate, endDate, exceptions))
                .sorted((a, b) -> a.startTime().compareTo(b.startTime()))
                .toList();
    }

    private java.util.stream.Stream<SlotDto> generateSlotsForPattern(
            BusinessHoursPattern pattern,
            LocalDate startDate,
            LocalDate endDate,
            List<ExceptionOverride> exceptions) {

        var days = java.util.stream.IntStream.range(0, java.time.Period.between(startDate, endDate).getDays() + 1)
                .mapToObj(i -> startDate.plusDays(i));

        return days.filter(day -> !isClosedOnDay(pattern, day, exceptions))
                .flatMap(day -> generateSlotsForDay(pattern, day));
    }

    private boolean isClosedOnDay(BusinessHoursPattern pattern, LocalDate date, List<ExceptionOverride> exceptions) {
        var dayOfWeek = date.getDayOfWeek();
        if (!pattern.getDays().contains(dayOfWeek)) {
            return true;
        }

        var exceptionForDate = exceptions.stream()
                .filter(e -> e.getDate().equals(date) && 
                        (e.getServiceType() == null || e.getServiceType().equals(pattern.getServiceType())))
                .findFirst();

        if (exceptionForDate.isPresent()) {
            return ExceptionOverride.ExceptionType.CLOSED.equals(exceptionForDate.get().getType());
        }

        return false;
    }

    private java.util.stream.Stream<SlotDto> generateSlotsForDay(BusinessHoursPattern pattern, LocalDate date) {
        var config = configRepository.findByServiceType(pattern.getServiceType())
                .orElse(new SlotConfiguration());

        int durationMinutes = config.getDurationMinutes() > 0 ? config.getDurationMinutes() : 60;
        LocalTime open = pattern.getOpenTime();
        LocalTime close = pattern.getCloseTime();

        return java.util.stream.Stream.iterate(
                java.time.LocalDateTime.of(date, open),
                t -> t.isBefore(java.time.LocalDateTime.of(date, close)),
                t -> t.plusMinutes(durationMinutes)
        )
                .filter(t -> {
                var now = java.time.LocalDateTime.now();
                var minTime = now.minusHours(config.getEarliestBookingAheadHours());
                if (config.isAllowSameDayBookings()) {
                    return true;
                }
                return t.minusMinutes(config.getBufferMinutes()).isAfter(minTime);
            })
                .map(t -> new SlotDto(
                        null,
                        pattern.getServiceType(),
                        date.atTime(open),
                        t.isAfter(java.time.LocalDateTime.of(date, close)) ? 
                                java.time.LocalDateTime.of(date, close) : t
                ));
    }

    public record SlotDto(Long id, String serviceType, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {}

    @PostMapping("/generate-slots")
    public ResponseEntity<Map<String, Object>> generateSlotsForPeriod(
            @RequestParam String serviceType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // This would call a service to generate slots based on patterns and exceptions
        return ResponseEntity.ok(Map.of("status", "generated", "serviceType", serviceType));
    }

    public record PatternRequest(
            String name,
            String serviceType,
            List<String> days,
            String openTime,
            String closeTime,
            String slotType
    ) {}

    public record ExceptionRequest(
            String date,
            String type,
            String serviceType,
            String patternId,
            String openTime,
            String closeTime
    ) {}
}
