package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionServiceUnitTests {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @Test
    void testCreate() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);
        assertEquals(session, result);
        verify(sessionRepository).save(session);
    }

    @Test
    void testDelete() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void testFindAll() {
        Session session1 = new Session();
        when(sessionRepository.findAll()).thenReturn(Collections.singletonList(session1));

        assertEquals(1, sessionService.findAll().size());
    }

    @Test
    void testGetById_found() {
        Session session = new Session();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertEquals(session, sessionService.getById(1L));
    }

    @Test
    void testGetById_notFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertNull(sessionService.getById(1L));
    }

    @Test
    void testUpdate() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.update(5L, session);
        assertEquals(session, result);
        assertEquals(5L, session.getId());
    }

    @Test
    void testParticipate_success() {
        Long sessionId = 1L;
        Long userId = 10L;

        Session session = new Session();
        session.setUsers(new ArrayList<User>()); // ✅ Fix: use mutable list

        User user = new User();
        user.setId(userId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(session);

        sessionService.participate(sessionId, userId);
        assertEquals(1, session.getUsers().size());
        assertEquals(userId, session.getUsers().get(0).getId());
    }


    @Test
    void testParticipate_notFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 10L));
    }

    @Test
    void testParticipate_alreadyParticipating() {
        Long userId = 10L;
        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(Arrays.asList(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, userId));
    }

    @Test
    void testNoLongerParticipate_success() {
        Long userId = 10L;
        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(Arrays.asList(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(1L, userId);

        assertTrue(session.getUsers().isEmpty());
    }

    @Test
    void testNoLongerParticipate_notFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 10L));
    }

    @Test
    void testNoLongerParticipate_userNotParticipating() {
        Session session = new Session();
        session.setUsers(Collections.emptyList());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 10L));
    }
}
