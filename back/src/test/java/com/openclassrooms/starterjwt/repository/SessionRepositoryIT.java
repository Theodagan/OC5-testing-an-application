package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SessionRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionRepository sessionRepository;

    private Session session;
    private Teacher teacher;

    @BeforeEach
    public void setUp() {
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setEmail("john.doe@example.com");
        teacher.setPassword("password");
        entityManager.persist(teacher);

        session = new Session();
        session.setName("Yoga session");
        session.setDate(LocalDateTime.now());
        session.setTeacher(teacher);
        session.setDescription("Description");
        entityManager.persist(session);
        entityManager.flush();
    }

    @Test
    public void testFindById() {
        Optional<Session> found = sessionRepository.findById(session.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(session.getName());
    }

    @Test
    public void testFindAll() {
        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getName()).isEqualTo(session.getName());
    }

    @Test
    public void testSave() {
        Session newSession = new Session();
        newSession.setName("New Session");
        newSession.setDate(LocalDateTime.now());
        newSession.setTeacher(teacher);
        newSession.setDescription("Description");
        Session savedSession = sessionRepository.save(newSession);
        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getName()).isEqualTo(newSession.getName());
    }

    @Test
    public void testUpdate() {
        session.setName("Updated Session");
        Session updatedSession = sessionRepository.save(session);
        assertThat(updatedSession.getName()).isEqualTo("Updated Session");
    }

    @Test
    public void testDelete() {
        sessionRepository.delete(session);
        Optional<Session> found = sessionRepository.findById(session.getId());
        assertThat(found).isNotPresent();
    }
}