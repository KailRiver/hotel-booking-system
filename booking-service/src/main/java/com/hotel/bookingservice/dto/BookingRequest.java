package com.hotel.bookingservice.dto;

import java.time.LocalDateTime;

public class BookingRequest {
    private Long roomId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoSelect;

    public BookingRequest() {}

    public BookingRequest(Long roomId, LocalDateTime startDate, LocalDateTime endDate, Boolean autoSelect) {
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.autoSelect = autoSelect;
    }

    // Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Boolean getAutoSelect() { return autoSelect; }
    public void setAutoSelect(Boolean autoSelect) { this.autoSelect = autoSelect; }
}