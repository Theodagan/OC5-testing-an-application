package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TeacherService.class)
public class TeacherServiceIT {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher savedTeacher;

    @BeforeEach
    void setUp() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        savedTeacher = teacherRepository.save(teacher);
    }

    @Test
    void testFindAll_shouldReturnTeachers() {
        List<Teacher> result = teacherService.findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void testFindById_shouldReturnMatch() {
        Teacher found = teacherService.findById(savedTeacher.getId());
        assertNotNull(found);
        assertEquals(savedTeacher.getLastName(), found.getLastName());
    }

    @Test
    void testFindById_shouldReturnNull_whenNotFound() {
        Teacher found = teacherService.findById(999L);
        assertNull(found);
    }
}
