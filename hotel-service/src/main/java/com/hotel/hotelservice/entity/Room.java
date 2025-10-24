package com.hotel.hotelservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private Boolean available;

    @Column(name = "times_booked", nullable = false)
    private Integer timesBooked;

    public Room() {}

    public Room(Hotel hotel, String number, Boolean available, Integer timesBooked) {
        this.hotel = hotel;
        this.number = number;
        this.available = available;
        this.timesBooked = timesBooked;
    }

    @PrePersist
    public void prePersist() {
        if (available == null) {
            available = true;
        }
        if (timesBooked == null) {
            timesBooked = 0;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Integer getTimesBooked() { return timesBooked; }
    public void setTimesBooked(Integer timesBooked) { this.timesBooked = timesBooked; }
}