package com.hotel.hotelservice.repository;

import com.hotel.hotelservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByAvailableTrue();

    @Query("SELECT r FROM Room r WHERE r.available = true AND r.id NOT IN " +
            "(SELECT rl.roomId FROM RoomLock rl WHERE " +
            "((rl.startDate < :endDate AND rl.endDate > :startDate))) " +
            "ORDER BY r.timesBooked ASC, r.id ASC")
    List<Room> findRecommendedRooms(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Room r WHERE r.available = true AND r.id NOT IN " +
            "(SELECT rl.roomId FROM RoomLock rl WHERE " +
            "((rl.startDate < :endDate AND rl.endDate > :startDate)))")
    List<Room> findAvailableRooms(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
}