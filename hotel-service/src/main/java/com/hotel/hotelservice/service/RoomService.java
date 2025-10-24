package com.hotel.hotelservice.service;

import com.hotel.hotelservice.dto.RoomDto;
import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.exception.HotelException;
import com.hotel.hotelservice.repository.HotelRepository;
import com.hotel.hotelservice.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    public RoomDto createRoom(RoomDto roomDto) {
        Hotel hotel = hotelRepository.findById(roomDto.getHotelId())
                .orElseThrow(() -> new HotelException("Hotel not found"));

        Room room = new Room();
        room.setHotel(hotel);
        room.setNumber(roomDto.getNumber());
        room.setAvailable(roomDto.getAvailable() != null ? roomDto.getAvailable() : true);
        room.setTimesBooked(roomDto.getTimesBooked() != null ? roomDto.getTimesBooked() : 0);

        Room savedRoom = roomRepository.save(room);
        return mapToDto(savedRoom);
    }

    public List<RoomDto> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new HotelException("Room not found"));

        if (roomDto.getNumber() != null) {
            room.setNumber(roomDto.getNumber());
        }
        if (roomDto.getAvailable() != null) {
            room.setAvailable(roomDto.getAvailable());
        }
        if (roomDto.getTimesBooked() != null) {
            room.setTimesBooked(roomDto.getTimesBooked());
        }

        Room updatedRoom = roomRepository.save(room);
        return mapToDto(updatedRoom);
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new HotelException("Room not found");
        }
        roomRepository.deleteById(id);
    }

    public RoomDto mapToDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel().getId());
        dto.setNumber(room.getNumber());
        dto.setAvailable(room.getAvailable());
        dto.setTimesBooked(room.getTimesBooked());
        return dto;
    }
}