package com.hotel.hotelservice.dto;

public class RoomDto {
    private Long id;
    private Long hotelId;
    private String number;
    private Boolean available;
    private Integer timesBooked;

    public RoomDto() {}

    public RoomDto(Long id, Long hotelId, String number, Boolean available, Integer timesBooked) {
        this.id = id;
        this.hotelId = hotelId;
        this.number = number;
        this.available = available;
        this.timesBooked = timesBooked;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Integer getTimesBooked() { return timesBooked; }
    public void setTimesBooked(Integer timesBooked) { this.timesBooked = timesBooked; }
}