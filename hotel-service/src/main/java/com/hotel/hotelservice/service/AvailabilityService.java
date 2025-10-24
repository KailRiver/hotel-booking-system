package com.hotel.hotelservice.service;

import com.hotel.hotelservice.dto.AvailabilityRequest;
import com.hotel.hotelservice.dto.AvailabilityResponse;
import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.entity.RoomLock;
import com.hotel.hotelservice.exception.HotelException;
import com.hotel.hotelservice.repository.RoomLockRepository;
import com.hotel.hotelservice.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    private final RoomRepository roomRepository;
    private final RoomLockRepository roomLockRepository;

    public AvailabilityService(RoomRepository roomRepository, RoomLockRepository roomLockRepository) {
        this.roomRepository = roomRepository;
        this.roomLockRepository = roomLockRepository;
    }

    @Transactional
    public AvailabilityResponse confirmAvailability(Long roomId, AvailabilityRequest request, String correlationId) {
        log.info("Confirming availability for room {} with correlationId {}", roomId, correlationId);

        // Check if request was already processed (idempotency)
        Optional<RoomLock> existingLock = roomLockRepository.findByCorrelationId(correlationId);
        if (existingLock.isPresent()) {
            log.info("Request already processed for correlationId {}", correlationId);
            RoomLock lock = existingLock.get();
            return new AvailabilityResponse(
                    lock.getStatus() == RoomLock.LockStatus.TEMPORARY,
                    "Request already processed"
            );
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new HotelException("Room not found"));

        if (!room.getAvailable()) {
            return new AvailabilityResponse(false, "Room is not available");
        }

        // Check for overlapping locks/bookings
        boolean isAvailable = isRoomAvailable(roomId, request.getStartDate(), request.getEndDate());

        if (isAvailable) {
            // Create temporary lock
            RoomLock lock = new RoomLock(
                    roomId,
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getBookingId(),
                    correlationId,
                    RoomLock.LockStatus.TEMPORARY,
                    LocalDateTime.now()
            );
            roomLockRepository.save(lock);
            log.info("Temporary lock created for room {} with correlationId {}", roomId, correlationId);
        }

        return new AvailabilityResponse(
                isAvailable,
                isAvailable ? "Room available" : "Room not available for selected dates"
        );
    }

    @Transactional
    public void releaseRoom(Long roomId, String correlationId) {
        log.info("Releasing room {} with correlationId {}", roomId, correlationId);

        roomLockRepository.findByCorrelationId(correlationId)
                .ifPresent(lock -> {
                    if (lock.getStatus() == RoomLock.LockStatus.TEMPORARY) {
                        roomLockRepository.delete(lock);
                        log.info("Temporary lock released for room {} with correlationId {}", roomId, correlationId);
                    }
                });
    }

    @Transactional
    public void confirmBooking(Long roomId, String correlationId) {
        log.info("Confirming booking for room {} with correlationId {}", roomId, correlationId);

        roomLockRepository.findByCorrelationId(correlationId)
                .ifPresent(lock -> {
                    lock.setStatus(RoomLock.LockStatus.CONFIRMED);
                    roomLockRepository.save(lock);

                    // Increment times_booked counter
                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new HotelException("Room not found"));
                    room.setTimesBooked(room.getTimesBooked() + 1);
                    roomRepository.save(room);

                    log.info("Booking confirmed for room {} with correlationId {}", roomId, correlationId);
                });
    }

    private boolean isRoomAvailable(Long roomId, LocalDateTime startDate, LocalDateTime endDate) {
        List<RoomLock> overlappingLocks = roomLockRepository.findOverlappingLocks(roomId, startDate, endDate);
        return overlappingLocks.isEmpty();
    }

    public List<Room> getAvailableRooms(LocalDateTime startDate, LocalDateTime endDate) {
        return roomRepository.findAvailableRooms(startDate, endDate);
    }

    public List<Room> getRecommendedRooms(LocalDateTime startDate, LocalDateTime endDate) {
        return roomRepository.findRecommendedRooms(startDate, endDate);
    }
}