package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("Teacher");
        teacher1.setLastName("One");
        teacher1.setCreatedAt(LocalDateTime.now());
        teacher1.setUpdatedAt(LocalDateTime.now());

        teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setFirstName("Teacher");
        teacher2.setLastName("Two");
        teacher2.setCreatedAt(LocalDateTime.now());
        teacher2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void findById_ShouldReturnTeacher_WhenTeacherExists() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // Act
        Teacher foundTeacher = teacherService.findById(1L);

        // Assert
        assertEquals(teacher1.getId(), foundTeacher.getId());
        assertEquals(teacher1.getFirstName(), foundTeacher.getFirstName());
        assertEquals(teacher1.getLastName(), foundTeacher.getLastName());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnNull_WhenTeacherDoesNotExist() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Teacher foundTeacher = teacherService.findById(1L);

        // Assert
        assertEquals(null, foundTeacher);
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void findAll_ShouldReturnAllTeachers() {
        // Arrange
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // Act
        List<Teacher> foundTeachers = teacherService.findAll();

        // Assert
        assertEquals(2, foundTeachers.size());
        assertEquals(teacher1.getId(), foundTeachers.get(0).getId());
        assertEquals(teacher2.getId(), foundTeachers.get(1).getId());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTeachersExist() {
        // Arrange
        when(teacherRepository.findAll()).thenReturn(List.of());

        // Act
        List<Teacher> foundTeachers = teacherService.findAll();

        // Assert
        assertEquals(0, foundTeachers.size());
        verify(teacherRepository, times(1)).findAll();
    }
}