package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserRepository userRepository;

    @Mock
    Authentication authentication;

    @InjectMocks
    AuthController authController;

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "Doe", "John", "encodedPassword", false);
        testUser.setId(1L);

        userDetails = new UserDetailsImpl(
            1L,
            "email@test.com",    // username
            "First",             // firstName
            "Last",              // lastName
            false,                  // admin
            "encodedPassword"    // password
        );
    }

    @Test
    void authenticateUser_validCredentials_shouldReturnJwtResponse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Match the username that will be used inside the controller
        userDetails = new UserDetailsImpl(
            1L,
            "email@test.com",    // this will be used in findByEmail(...)
            "First",
            "Last",
            false,
            "encodedPassword"
    );

    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt");

    when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(testUser));

    ResponseEntity<?> response = authController.authenticateUser(loginRequest);

    assertEquals(200, response.getStatusCodeValue());

    Object body = response.getBody();
    assertNotNull(body);
    assertTrue(body instanceof JwtResponse);

    JwtResponse jwtResponse = (JwtResponse) body;
    assertEquals("mocked-jwt", jwtResponse.getToken());
    assertEquals("email@test.com", jwtResponse.getUsername());
}


@Test
void registerUser_newUser_shouldSucceed() {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("new@example.com");
    signupRequest.setFirstName("New");
    signupRequest.setLastName("User");
    signupRequest.setPassword("plaintext");

    when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(passwordEncoder.encode("plaintext")).thenReturn("encodedPassword");

    ResponseEntity<?> response = authController.registerUser(signupRequest);

    assertEquals(200, response.getStatusCodeValue());

    Object body = response.getBody();
    assertNotNull(body, "Response body should not be null");
    assertTrue(body instanceof MessageResponse, "Expected MessageResponse body");

    MessageResponse message = (MessageResponse) body;
    assertEquals("User registered successfully!", message.getMessage());

    verify(userRepository).save(any(User.class));
}


    @Test
    void registerUser_existingEmail_shouldReturnError() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(400, response.getStatusCodeValue());

        Object body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body instanceof MessageResponse, "Expected body to be of type MessageResponse");

        String message = ((MessageResponse) body).getMessage();
        assertEquals("Error: Email is already taken!", message);

        verify(userRepository, never()).save(any(User.class));
    }

    
}
