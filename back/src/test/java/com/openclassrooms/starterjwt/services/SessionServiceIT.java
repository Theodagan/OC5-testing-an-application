package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(SessionService.class)
public class SessionServiceIT {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setPassword("hashed");
        user.setAdmin(false);
        user = userRepository.save(user);

        session = new Session();
        session.setName("Yoga Class");
        session.setDescription("Morning yoga session");
        session.setDate(new java.util.Date());
        session.setUsers(Collections.emptyList());
        session = sessionRepository.save(session);
    }

    @Test
    void testParticipate_shouldAddUserToSession() {
        sessionService.participate(session.getId(), user.getId());

        Session updated = sessionRepository.findById(session.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(1, updated.getUsers().size());
        assertEquals(user.getId(), updated.getUsers().get(0).getId());
    }

    @Test
    void testParticipate_shouldThrow_whenAlreadyParticipating() {
        sessionService.participate(session.getId(), user.getId());

        assertThrows(BadRequestException.class, () ->
            sessionService.participate(session.getId(), user.getId()));
    }

    @Test
    void testParticipate_shouldThrow_whenSessionNotFound() {
        assertThrows(NotFoundException.class, () ->
            sessionService.participate(999L, user.getId()));
    }

    @Test
    void testParticipate_shouldThrow_whenUserNotFound() {
        assertThrows(NotFoundException.class, () ->
            sessionService.participate(session.getId(), 999L));
    }

    @Test
    void testNoLongerParticipate_shouldRemoveUser() {
        sessionService.participate(session.getId(), user.getId());

        sessionService.noLongerParticipate(session.getId(), user.getId());

        Session updated = sessionRepository.findById(session.getId()).orElse(null);
        assertNotNull(updated);
        assertTrue(updated.getUsers().isEmpty());
    }

    @Test
    void testNoLongerParticipate_shouldThrow_whenUserNotInSession() {
        assertThrows(BadRequestException.class, () ->
            sessionService.noLongerParticipate(session.getId(), user.getId()));
    }

    @Test
    void testNoLongerParticipate_shouldThrow_whenSessionNotFound() {
        assertThrows(NotFoundException.class, () ->
            sessionService.noLongerParticipate(999L, user.getId()));
    }
}
