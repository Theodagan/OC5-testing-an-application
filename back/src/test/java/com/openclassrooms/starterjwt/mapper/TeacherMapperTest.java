package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherMapperTest {

    private final TeacherMapper mapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    void testToDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now.plusMinutes(5));

        // Act
        TeacherDto dto = mapper.toDto(teacher);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now.plusMinutes(5), dto.getUpdatedAt());
    }

    @Test
    void testToEntity() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        TeacherDto dto = new TeacherDto();
        dto.setId(2L);
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now.plusMinutes(10));

        // Act
        Teacher entity = mapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("Alice", entity.getFirstName());
        assertEquals("Smith", entity.getLastName());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now.plusMinutes(10), entity.getUpdatedAt());
    }

    @Test
    void testToEntityListAndDtoList() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        TeacherDto dto = new TeacherDto();
        dto.setId(2L);
        dto.setFirstName("Alice");
        dto.setLastName("Smith");

        assertEquals(1, mapper.toDto(List.of(teacher)).size());
        assertEquals(1, mapper.toEntity(List.of(dto)).size());
    }

    //NULL BRANCHES 

    @Test
    void testToDto_withNull_shouldReturnNull() {
        assertNull(mapper.toDto((Teacher) null));
    }

    @Test
    void testToEntity_withNull_shouldReturnNull() {
        assertNull(mapper.toEntity((TeacherDto) null));
    }

    @Test
    void testToDtoList_withNullElement_shouldIncludeNull() {
        List<TeacherDto> result = mapper.toDto(Arrays.asList(null, new Teacher().setId(1L)));

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNotNull(result.get(1));
        assertEquals(1L, result.get(1).getId());
    }

    @Test
    void testToEntityList_withNullElement_shouldIncludeNull() {
        TeacherDto dto = new TeacherDto();
        dto.setId(2L);

        List<Teacher> result = mapper.toEntity(Arrays.asList(null, dto));

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNotNull(result.get(1));
        assertEquals(2L, result.get(1).getId());
    }

}
