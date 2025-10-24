package com.hotel.bookingservice.service;

import com.hotel.bookingservice.dto.BookingRequest;
import com.hotel.bookingservice.dto.BookingResponse;
import com.hotel.bookingservice.entity.Booking;
import com.hotel.bookingservice.entity.BookingStatus;
import com.hotel.bookingservice.entity.User;
import com.hotel.bookingservice.exception.BookingException;
import com.hotel.bookingservice.integration.HotelServiceClient;
import com.hotel.bookingservice.repository.BookingRepository;
import com.hotel.bookingservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelServiceClient hotelServiceClient;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
                          HotelServiceClient hotelServiceClient) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.hotelServiceClient = hotelServiceClient;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request, String username) {
        log.info("Creating booking for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BookingException("User not found"));

        String correlationId = UUID.randomUUID().toString();
        log.info("Creating booking with correlationId: {}", correlationId);

        Booking booking = null;

        try {
            // Step 1: Create booking in PENDING status
            booking = new Booking();
            booking.setUser(user);
            booking.setRoomId(request.getRoomId());
            booking.setStartDate(request.getStartDate());
            booking.setEndDate(request.getEndDate());
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setCorrelationId(correlationId);

            booking = bookingRepository.save(booking);
            log.info("Booking {} created in PENDING status", booking.getId());

            // Step 2: Confirm availability with Hotel Service
            AvailabilityRequest availabilityRequest = new AvailabilityRequest(
                    request.getStartDate(),
                    request.getEndDate(),
                    booking.getId()
            );

            AvailabilityResponse response = hotelServiceClient.confirmAvailability(
                    request.getRoomId(), availabilityRequest, correlationId);

            if (response.isAvailable()) {
                // Step 3: Update booking to CONFIRMED
                booking.setStatus(BookingStatus.CONFIRMED);
                booking = bookingRepository.save(booking);
                log.info("Booking {} confirmed successfully", booking.getId());

                return mapToResponse(booking);
            } else {
                throw new BookingException("Room not available for selected dates");
            }

        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());

            // Compensation: Cancel booking if it was created
            if (booking != null && booking.getId() != null) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                // Release room lock
                hotelServiceClient.releaseRoom(request.getRoomId(), correlationId);
                log.info("Booking {} cancelled due to error", booking.getId());
            }

            throw new BookingException("Failed to create booking: " + e.getMessage());
        }
    }

    public List<BookingResponse> getUserBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BookingException("User not found"));

        return bookingRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new BookingException("Access denied");
        }

        return mapToResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new BookingException("Access denied");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Only confirmed bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Release the room
        hotelServiceClient.releaseRoom(booking.getRoomId(), booking.getCorrelationId());
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setRoomId(booking.getRoomId());
        response.setStartDate(booking.getStartDate());
        response.setEndDate(booking.getEndDate());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }

    // Inner classes for availability request/response
    public static class AvailabilityRequest {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Long bookingId;

        public AvailabilityRequest(LocalDateTime startDate, LocalDateTime endDate, Long bookingId) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.bookingId = bookingId;
        }

        public LocalDateTime getStartDate() { return startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public Long getBookingId() { return bookingId; }
    }

    public static class AvailabilityResponse {
        private boolean available;
        private String message;

        public AvailabilityResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        public boolean isAvailable() { return available; }
        public String getMessage() { return message; }
    }
}