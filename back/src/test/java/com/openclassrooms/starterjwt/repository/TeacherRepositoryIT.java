package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
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
        Teacher teacher1 = new Teacher().setFirstName("firstName").setLastName("lastName");
        Teacher teacher2 = new Teacher().setFirstName("firstName").setLastName("lastName");

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);

        List<Teacher> teachers = teacherRepository.findAll();
        assertEquals(2, teachers.size());
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