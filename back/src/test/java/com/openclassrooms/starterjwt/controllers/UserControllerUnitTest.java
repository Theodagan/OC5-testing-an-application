package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAdmin(user.isAdmin());
    }

    @Test
    void testFindById_Valid() {
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void testFindById_NotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.findById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFindById_InvalidFormat() {
        ResponseEntity<?> response = userController.findById("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDelete_ValidUser() {
        when(userService.findById(1L)).thenReturn(user);

        // simulate user authenticated with same email
        UserDetails mockUserDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        ResponseEntity<?> response = userController.save("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).delete(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.save("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_InvalidFormat() {
        ResponseEntity<?> response = userController.save("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDelete_UnauthorizedUser() {
        when(userService.findById(1L)).thenReturn(user);

        // simulate logged-in user with DIFFERENT email
        UserDetails mockUserDetails = new org.springframework.security.core.userdetails.User(
                "unauthorized@test.com", "password", Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        ResponseEntity<?> response = userController.save("1");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService, never()).delete(anyLong());
    }
}
