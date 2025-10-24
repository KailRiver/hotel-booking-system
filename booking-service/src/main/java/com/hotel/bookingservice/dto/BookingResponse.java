package com.hotel.bookingservice.dto;

import com.hotel.bookingservice.entity.BookingStatus;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long roomId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public BookingResponse() {}

    public BookingResponse(Long id, Long roomId, LocalDateTime startDate, LocalDateTime endDate,
                           BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long roomId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BookingStatus status;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder roomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookingResponse build() {
            return new BookingResponse(id, roomId, startDate, endDate, status, createdAt);
        }
    }
}