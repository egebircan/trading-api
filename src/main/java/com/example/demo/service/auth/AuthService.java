package com.example.demo.service.auth;

import com.example.demo.api.request.LoginRequest;
import com.example.demo.domain.model.CustomUserDetails;
import com.example.demo.domain.model.User;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenProvider;
    private final UserRepository userRepository;

    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        customUserDetails.getUser().setFailedLoginAttempts(0);
        userRepository.save(customUserDetails.getUser());

        return jwtTokenProvider.generateToken(authentication.getName());
    }

    public void handleFailedLogin(String username) {
        User user = userRepository.findByUsername(username);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= 3) {
            user.setBlocked(true);
        }

        userRepository.save(user);
    }

    public void unblockUser(String username) {
        User user = userRepository.findByUsername(username);
        user.setBlocked(false);
        userRepository.save(user);
    }
}
