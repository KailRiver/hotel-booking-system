package com.hotel.bookingservice.config;

import com.hotel.bookingservice.entity.User;
import com.hotel.bookingservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin/admin");
        }

        // Create test user if not exists
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(User.Role.USER);
            userRepository.save(user);
            System.out.println("✅ Test user created: user/user");
        }

        // Create bookinguser if not exists
        if (userRepository.findByUsername("bookinguser").isEmpty()) {
            User bookingUser = new User();
            bookingUser.setUsername("bookinguser");
            bookingUser.setPassword(passwordEncoder.encode("password"));
            bookingUser.setRole(User.Role.USER);
            userRepository.save(bookingUser);
            System.out.println("✅ Booking user created: bookinguser/password");
        }

        // Create adminuser if not exists
        if (userRepository.findByUsername("adminuser").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("adminuser");
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setRole(User.Role.ADMIN);
            userRepository.save(adminUser);
            System.out.println("✅ Admin user created: adminuser/password");
        }

        System.out.println("✅ Data initialization completed! Total users: " + userRepository.count());
    }
}