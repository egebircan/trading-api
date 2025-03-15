package com.example.demo.api.controller;

import com.example.demo.api.request.LoginRequest;
import com.example.demo.api.request.UnblockRequest;
import com.example.demo.api.response.LoginResponse;
import com.example.demo.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return new LoginResponse(token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/unblock")
    public void unblockUser(@RequestBody UnblockRequest unblockRequest) {
        authService.unblockUser(unblockRequest.getUsername());
    }
}
