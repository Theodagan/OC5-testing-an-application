package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TeacherRepositoryIT {

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();
        teacher = new Teacher();
        teacher.setFirstName("firstName");
        teacher.setLastName("lastName");
        teacher.setEmail("email@example.com");
    }

    @Test
    void testFindById() {
        Teacher savedTeacher = teacherRepository.save(teacher);
        Optional<Teacher> foundTeacher = teacherRepository.findById(savedTeacher.getId());
        assertTrue(foundTeacher.isPresent());
        assertEquals(savedTeacher.getId(), foundTeacher.get().getId());
    }

    @Test
    void testFindAll() {
        teacherRepository.save(teacher);
        List<Teacher> teachers = teacherRepository.findAll();
        assertThat(teachers).hasSize(1);
    }

    @Test
    void testSave() {
        Teacher savedTeacher = teacherRepository.save(teacher);
        assertThat(savedTeacher.getId()).isNotNull();
    }

    @Test
    void testUpdate() {
        Teacher savedTeacher = teacherRepository.save(teacher);
        savedTeacher.setFirstName("updatedFirstName");
        Teacher updatedTeacher = teacherRepository.save(savedTeacher);
        assertEquals("updatedFirstName", updatedTeacher.getFirstName());
    }

    @Test
    void testDelete() {
        Teacher savedTeacher = teacherRepository.save(teacher);
        teacherRepository.delete(savedTeacher);
        Optional<Teacher> deletedTeacher = teacherRepository.findById(savedTeacher.getId());
        assertThat(deletedTeacher).isNotPresent();
    }
}