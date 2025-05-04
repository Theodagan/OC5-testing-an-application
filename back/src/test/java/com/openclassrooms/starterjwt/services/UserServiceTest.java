package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
    }

    @Test
    void findById_ExistingId_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(1L);

        assertEquals(user, foundUser);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistingId_ReturnsNull() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        User foundUser = userService.findById(99L);

        assertNull(foundUser);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void findAll_ReturnsAllUsers() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        assertEquals(users.size(), foundUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void delete_ExistingId_DeletesUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void update_ExistingUser_ReturnsUpdatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.update(user.getId(),user);

        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_NewUser_ReturnsSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertEquals(user.getEmail(), savedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }
}