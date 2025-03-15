package com.example.demo.infrastructure.security;

import com.example.demo.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final AuthService authService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        authService.handleFailedLogin(e.getAuthentication().getName());
    }
}
