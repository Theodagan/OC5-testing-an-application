package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SessionControllerUnitTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        session = new Session();
        session.setId(1L);
        session.setName("Yoga");
        session.setDate(new Date());
        session.setDescription("Stretching");

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga");
        sessionDto.setDate(session.getDate());
        sessionDto.setDescription("Stretching");
    }

    @Test
    @DisplayName("Should return session by valid ID")
    void testFindById_Valid() {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.findById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 if session not found")
    void testFindById_NotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return 400 on bad ID format")
    void testFindById_InvalidFormat() {
        ResponseEntity<?> response = sessionController.findById("abc");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return list of sessions")
    void testFindAll() {
        List<Session> sessions = Arrays.asList(session);
        List<SessionDto> dtos = Arrays.asList(sessionDto);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(dtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dtos, response.getBody());
    }

    @Test
    @DisplayName("Should create a session")
    void testCreate() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    @DisplayName("Should update a session with valid ID")
    void testUpdate_Valid() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(eq(1L), eq(session))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    @DisplayName("Should return 400 on update with invalid ID")
    void testUpdate_InvalidId() {
        ResponseEntity<?> response = sessionController.update("abc", sessionDto);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should delete a session with valid ID")
    void testDelete_Valid() {
        when(sessionService.getById(1L)).thenReturn(session);
        doNothing().when(sessionService).delete(1L);

        ResponseEntity<?> response = sessionController.save("1");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return 404 when deleting a non-existent session")
    void testDelete_NotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.save("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return 400 on delete with invalid ID format")
    void testDelete_InvalidIdFormat() {
        ResponseEntity<?> response = sessionController.save("abc");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should participate in session with valid IDs")
    void testParticipate_Valid() {
        doNothing().when(sessionService).participate(1L, 2L);

        ResponseEntity<?> response = sessionController.participate("1", "2");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return 400 on participate with bad ID format")
    void testParticipate_InvalidFormat() {
        ResponseEntity<?> response = sessionController.participate("abc", "xyz");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should cancel participation with valid IDs")
    void testNoLongerParticipate_Valid() {
        doNothing().when(sessionService).noLongerParticipate(1L, 2L);

        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return 400 on cancel participation with bad IDs")
    void testNoLongerParticipate_InvalidFormat() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("abc", "xyz");

        assertEquals(400, response.getStatusCodeValue());
    }
}
