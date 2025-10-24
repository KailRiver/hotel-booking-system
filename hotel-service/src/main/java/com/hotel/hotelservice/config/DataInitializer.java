package com.hotel.hotelservice.config;

import com.hotel.hotelservice.entity.Hotel;
import com.hotel.hotelservice.entity.Room;
import com.hotel.hotelservice.repository.HotelRepository;
import com.hotel.hotelservice.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public DataInitializer(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create sample hotels
        Hotel hotel1 = new Hotel("Grand Hotel", "123 Main Street, City Center");
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel("Seaside Resort", "456 Beach Road, Coastal Area");
        hotelRepository.save(hotel2);

        // Create sample rooms for hotel1
        Room room1 = new Room(hotel1, "101", true, 2);
        roomRepository.save(room1);

        Room room2 = new Room(hotel1, "102", true, 1);
        roomRepository.save(room2);

        Room room3 = new Room(hotel1, "103", true, 0);
        roomRepository.save(room3);

        // Create sample rooms for hotel2
        Room room4 = new Room(hotel2, "201", true, 3);
        roomRepository.save(room4);

        Room room5 = new Room(hotel2, "202", true, 1);
        roomRepository.save(room5);

        System.out.println("Sample data initialized successfully!");
    }
}