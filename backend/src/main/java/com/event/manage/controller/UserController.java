package com.event.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.manage.model.dto.UserDto.LoginDto;
import com.event.manage.model.dto.UserDto.RegistrationDto;
import com.event.manage.security.JwtResponse;
import com.event.manage.security.JwtTokenProvider;
import com.event.manage.service.UserService;

import jakarta.validation.Valid;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDto registrationDto) {
        userService.register(registrationDto);
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Authenticate a user and generate JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        // This would be implemented with the JWT token provider
        String token = jwtTokenProvider.generateToken(loginDto.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}