package com.ahmed.picturepublishingservice.auth.service;

import com.ahmed.picturepublishingservice.auth.dtos.AuthenticationRequest;
import com.ahmed.picturepublishingservice.auth.dtos.AuthenticationResponse;
import com.ahmed.picturepublishingservice.auth.dtos.RegisterRequest;
import com.ahmed.picturepublishingservice.security.jwt.JwtService;
import com.ahmed.picturepublishingservice.user.Role;
import com.ahmed.picturepublishingservice.user.User;
import com.ahmed.picturepublishingservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}