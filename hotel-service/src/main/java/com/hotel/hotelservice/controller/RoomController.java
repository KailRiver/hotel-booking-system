package com.hotel.hotelservice.controller;

import com.hotel.hotelservice.dto.AvailabilityRequest;
import com.hotel.hotelservice.dto.AvailabilityResponse;
import com.hotel.hotelservice.dto.RoomDto;
import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.service.AvailabilityService;
import com.hotel.hotelservice.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    public RoomController(RoomService roomService, AvailabilityService availabilityService) {
        this.roomService = roomService;
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        RoomDto createdRoom = roomService.createRoom(roomDto);
        return ResponseEntity.ok(createdRoom);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Room> rooms = availabilityService.getAvailableRooms(startDate, endDate);
        List<RoomDto> roomDtos = rooms.stream()
                .map(roomService::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roomDtos);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<RoomDto>> getRecommendedRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Room> rooms = availabilityService.getRecommendedRooms(startDate, endDate);
        List<RoomDto> roomDtos = rooms.stream()
                .map(roomService::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roomDtos);
    }

    @PostMapping("/{id}/confirm-availability")
    public ResponseEntity<AvailabilityResponse> confirmAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        AvailabilityResponse response = availabilityService.confirmAvailability(id, request, correlationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseRoom(
            @PathVariable Long id,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        availabilityService.releaseRoom(id, correlationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirm-booking")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        availabilityService.confirmBooking(id, correlationId);
        return ResponseEntity.ok().build();
    }
}