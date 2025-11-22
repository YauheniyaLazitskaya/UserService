package com.controllers;

import com.dto.keycloakDTO.AccessTokenResponse;
import com.dto.keycloakDTO.LoginRequest;
import com.dto.keycloakDTO.RefreshTokenRequest;
import com.services.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(keycloakAuthService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(keycloakAuthService.refreshToken(request.getRefreshToken()));
    }
}