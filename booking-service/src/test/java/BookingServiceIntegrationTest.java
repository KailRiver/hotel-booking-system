package com.hotel.bookingservice.integration;

import com.hotel.bookingservice.BookingServiceApplication;
import com.hotel.bookingservice.dto.BookingRequest;
import com.hotel.bookingservice.entity.Booking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = BookingServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateBookingFlow() {
        // 1. Аутентификация
        String token = authenticateUser();

        // 2. Создание заголовков с JWT
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Создание запроса на бронирование
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setStartDate(LocalDate.now().plusDays(1));
        bookingRequest.setEndDate(LocalDate.now().plusDays(3));
        bookingRequest.setAutoSelect(false);

        HttpEntity<BookingRequest> request = new HttpEntity<>(bookingRequest, headers);

        // 4. Вызов API
        ResponseEntity<Booking> response = restTemplate.exchange(
                "/api/bookings",
                HttpMethod.POST,
                request,
                Booking.class
        );

        // 5. Проверки
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    private String authenticateUser() {
        // Реализация аутентификации для получения JWT токена
        return "mock-jwt-token";
    }
}