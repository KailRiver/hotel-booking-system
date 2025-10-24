package com.hotel.hotelservice.repository;

import com.hotel.hotelservice.entity.RoomLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomLockRepository extends JpaRepository<RoomLock, Long> {

    Optional<RoomLock> findByCorrelationId(String correlationId);

    @Query("SELECT rl FROM RoomLock rl WHERE rl.roomId = :roomId AND " +
            "((rl.startDate < :endDate AND rl.endDate > :startDate))")
    List<RoomLock> findOverlappingLocks(@Param("roomId") Long roomId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    void deleteByCorrelationId(String correlationId);
}