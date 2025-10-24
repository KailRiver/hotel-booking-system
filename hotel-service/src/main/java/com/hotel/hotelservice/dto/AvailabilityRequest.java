package com.hotel.hotelservice.dto;

import java.time.LocalDateTime;

public class AvailabilityRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long bookingId;

    public AvailabilityRequest() {}

    public AvailabilityRequest(LocalDateTime startDate, LocalDateTime endDate, Long bookingId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingId = bookingId;
    }

    // Getters and Setters
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
}