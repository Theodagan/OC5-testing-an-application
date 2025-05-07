package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserService.class)
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("securepass");
        user.setAdmin(false);
        savedUser = userRepository.save(user);
    }

    @Test
    void testFindById_shouldReturnMatch() {
        User found = userService.findById(savedUser.getId());
        assertNotNull(found);
        assertEquals(savedUser.getEmail(), found.getEmail());
    }

    @Test
    void testFindById_shouldReturnNull_whenNotFound() {
        User notFound = userService.findById(999L);
        assertNull(notFound);
    }
}
