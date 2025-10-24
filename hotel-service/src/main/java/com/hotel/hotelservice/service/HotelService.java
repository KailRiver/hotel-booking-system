package com.hotel.hotelservice.service;

import com.hotel.hotelservice.dto.HotelDto;
import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.exception.HotelException;
import com.hotel.hotelservice.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public HotelDto createHotel(HotelDto hotelDto) {
        Hotel hotel = new Hotel();
        hotel.setName(hotelDto.getName());
        hotel.setAddress(hotelDto.getAddress());

        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToDto(savedHotel);
    }

    public List<HotelDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelException("Hotel not found"));
        return mapToDto(hotel);
    }

    public HotelDto updateHotel(Long id, HotelDto hotelDto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelException("Hotel not found"));

        if (hotelDto.getName() != null) {
            hotel.setName(hotelDto.getName());
        }
        if (hotelDto.getAddress() != null) {
            hotel.setAddress(hotelDto.getAddress());
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        return mapToDto(updatedHotel);
    }

    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new HotelException("Hotel not found");
        }
        hotelRepository.deleteById(id);
    }

    public HotelDto mapToDto(Hotel hotel) {
        HotelDto dto = new HotelDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        return dto;
    }
}