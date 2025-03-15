package com.example.demo.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.api.request.UnblockRequest;
import com.example.demo.domain.model.User;
import com.example.demo.domain.model.UserRole;
import com.example.demo.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String ADMIN_USERNAME = "admin";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User adminUser = new User();
        adminUser.setUsername(ADMIN_USERNAME);
        adminUser.setPassword(passwordEncoder.encode(PASSWORD));

        UserRole adminRole = new UserRole();
        adminRole.setId(1);
        adminRole.setName("ROLE_ADMIN");
        adminUser.setRoles(Stream.of(adminRole).collect(Collectors.toCollection(HashSet::new)));
        userRepository.save(adminUser);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setBlocked(false);
        user.setFailedLoginAttempts(0);

        UserRole userRole = new UserRole();
        userRole.setId(2);
        userRole.setName("ROLE_USER");
        user.setRoles(Stream.of(userRole).collect(Collectors.toCollection(HashSet::new)));
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = "ADMIN")
    void unblockUser_AsAdmin_ShouldSucceed() throws Exception {
        User user = userRepository.findByUsername(USERNAME);
        user.setBlocked(true);
        userRepository.save(user);

        UnblockRequest unblockRequest = new UnblockRequest();
        unblockRequest.setUsername(USERNAME);

        mockMvc.perform(patch("/auth/unblock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unblockRequest)))
                .andExpect(status().isOk());

        user = userRepository.findByUsername(USERNAME);
        assert !user.getBlocked();
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void unblockUser_AsNonAdmin_ShouldReturnForbidden() throws Exception {
        UnblockRequest unblockRequest = new UnblockRequest();
        unblockRequest.setUsername(USERNAME);

        mockMvc.perform(patch("/auth/unblock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unblockRequest)))
                .andExpect(status().isForbidden());
    }
}
