package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeacherControllerUnitTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("Jane");
        teacherDto.setLastName("Doe");
    }

    @Test
    @DisplayName("Should return teacher DTO when ID is valid")
    void findById_validId_returnsDto() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        ResponseEntity<?> response = teacherController.findById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(teacherDto, response.getBody());

        verify(teacherService).findById(1L);
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should return 404 when teacher not found")
    void findById_notFound() {
        when(teacherService.findById(999L)).thenReturn(null);

        ResponseEntity<?> response = teacherController.findById("999");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return 400 when ID is invalid")
    void findById_invalidId() {
        ResponseEntity<?> response = teacherController.findById("abc");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return list of teacher DTOs")
    void findAll_returnsDtoList() {
        List<Teacher> teachers = Arrays.asList(teacher);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        ResponseEntity<?> response = teacherController.findAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(teacherDtos, response.getBody());

        verify(teacherService).findAll();
        verify(teacherMapper).toDto(teachers);
    }
}
