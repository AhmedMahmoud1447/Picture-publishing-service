package com.ahmed.picturepublishingservice.security.config;

import com.ahmed.picturepublishingservice.user.Role;
import com.ahmed.picturepublishingservice.user.User;
import com.ahmed.picturepublishingservice.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin").isEmpty()) {
            User admin = User.builder()
                    .email("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Built-in Admin Account Created Successfully!");
        }
    }
}