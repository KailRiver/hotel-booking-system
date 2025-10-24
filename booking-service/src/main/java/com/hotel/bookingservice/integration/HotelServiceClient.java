package com.hotel.bookingservice.integration;

import com.hotel.bookingservice.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
public class HotelServiceClient {

    private static final Logger log = LoggerFactory.getLogger(HotelServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${hotel.service.url:http://localhost:8082}")
    private String hotelServiceUrl;

    public HotelServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookingService.AvailabilityResponse confirmAvailability(Long roomId,
                                                                   BookingService.AvailabilityRequest request,
                                                                   String correlationId) {
        log.info("Confirming availability for room {} with correlationId {}", roomId, correlationId);

        try {
            String url = hotelServiceUrl + "/api/rooms/" + roomId + "/confirm-availability";

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Correlation-ID", correlationId);

            // Create request body
            AvailabilityRequest availabilityRequest = new AvailabilityRequest(
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getBookingId()
            );

            HttpEntity<AvailabilityRequest> entity = new HttpEntity<>(availabilityRequest, headers);

            // Make REST call
            ResponseEntity<AvailabilityResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, AvailabilityResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                AvailabilityResponse availabilityResponse = response.getBody();
                return new BookingService.AvailabilityResponse(
                        availabilityResponse.isAvailable(),
                        availabilityResponse.getMessage()
                );
            } else {
                throw new RuntimeException("Hotel service returned error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error confirming availability for room {}: {}", roomId, e.getMessage());
            throw new RuntimeException("Hotel service unavailable: " + e.getMessage(), e);
        }
    }

    public void releaseRoom(Long roomId, String correlationId) {
        log.info("Releasing room {} with correlationId {}", roomId, correlationId);

        try {
            String url = hotelServiceUrl + "/api/rooms/" + roomId + "/release";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Correlation-ID", correlationId);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            log.info("Room {} released successfully", roomId);

        } catch (Exception e) {
            log.error("Error releasing room {}: {}", roomId, e.getMessage());
        }
    }

    // DTO classes for communication with hotel service
    public static class AvailabilityRequest {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Long bookingId;

        public AvailabilityRequest() {}

        public AvailabilityRequest(LocalDateTime startDate, LocalDateTime endDate, Long bookingId) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.bookingId = bookingId;
        }

        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    }

    public static class AvailabilityResponse {
        private boolean available;
        private String message;

        public AvailabilityResponse() {}

        public AvailabilityResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}