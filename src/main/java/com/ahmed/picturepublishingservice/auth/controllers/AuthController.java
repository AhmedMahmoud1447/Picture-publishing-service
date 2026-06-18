package com.ahmed.picturepublishingservice.auth.controllers;

import com.ahmed.picturepublishingservice.auth.dtos.AuthenticationRequest;
import com.ahmed.picturepublishingservice.auth.dtos.AuthenticationResponse;
import com.ahmed.picturepublishingservice.auth.dtos.RegisterRequest;
import com.ahmed.picturepublishingservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}