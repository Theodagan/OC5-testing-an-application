// Suggested code may be subject to a license. Learn more: ~LicenseLog:4094147493.
// Suggested code may be subject to a license. Learn more: ~LicenseLog:1218328427.
package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private Teacher teacher;

    @BeforeEach
    public void setup() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Teacher");
        teacher.setLastName("Test");
        session = new Session();
        session.setId(1L);
        session.setName("Yoga");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
    }

    @Test
    public void testFindById() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        Optional<Session> foundSession = Optional.ofNullable(sessionService.getById(1L)); // is Session or Null
        assertEquals(session.getName(), foundSession.get().getName());
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindAll() {
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(session);
        when(sessionRepository.findAll()).thenReturn(sessionList);
        List<Session> allSessions = sessionService.findAll();
        assertEquals(1, allSessions.size());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    public void testDelete() {
        doNothing().when(sessionRepository).deleteById(1L);
        sessionService.delete(1L);
        verify(sessionRepository, times(1)).deleteById(1L);
    }

    // @Test
    // public void testSave() {
    //     when(sessionRepository.save(any(Session.class))).thenReturn(session);
    //     Session savedSession = sessionService.save(session);
    //     assertEquals(session.getName(), savedSession.getName());
    //     verify(sessionRepository, times(1)).save(session);
    // }

    // @Test
    // public void testUpdate() {
    //     when(sessionRepository.save(any(Session.class))).thenReturn(session);
    //     Session updatedSession = sessionService.save(session);
    //     assertEquals(session.getName(), updatedSession.getName());
    //     verify(sessionRepository, times(1)).save(session);
    // }
}