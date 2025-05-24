package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setPassword("hashed-password");
        user.setAdmin(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now.plusMinutes(5));

        // Act
        UserDto dto = mapper.toDto(user);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("hashed-password", dto.getPassword()); // still mapped
        assertTrue(dto.isAdmin());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now.plusMinutes(5), dto.getUpdatedAt());
    }

    @Test
    void testToEntity() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setEmail("admin@example.com");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPassword("admin-pass");
        dto.setAdmin(false);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now.plusMinutes(10));

        // Act
        User user = mapper.toEntity(dto);

        // Assert
        assertNotNull(user);
        assertEquals(2L, user.getId());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("admin-pass", user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now.plusMinutes(10), user.getUpdatedAt());
    }


    @Test
    void testToDtoList() {
        User user = new User()
            .setId(1L)
            .setEmail("list@example.com")
            .setFirstName("John")
            .setLastName("Doe")
            .setPassword("secret")
            .setAdmin(true);

        List<UserDto> dtoList = mapper.toDto(List.of(user));

        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
        UserDto dto = dtoList.get(0);
        assertEquals("list@example.com", dto.getEmail());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("secret", dto.getPassword());
        assertTrue(dto.isAdmin());
    }

    @Test
    void testToEntityList() {
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setEmail("dto@example.com");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPassword("encoded");
        dto.setAdmin(false);

        List<User> entityList = mapper.toEntity(List.of(dto));

        assertNotNull(entityList);
        assertEquals(1, entityList.size());
        User user = entityList.get(0);
        assertEquals("dto@example.com", user.getEmail());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("encoded", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    void testToEntityListWithEmptyList() {
        List<User> result = mapper.toEntity(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //NULL BRANCHES 
    @Test
    void testToEntity_withNull_shouldReturnNull() {
        assertNull(mapper.toEntity((UserDto) null));
    }
    @Test
    void testToDto_withNull_shouldReturnNull() {
        assertNull(mapper.toDto((User) null));
    }
    
    @Test
    void testToEntityList_withNullElement_shouldIncludeNull() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("john@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("strongpass");
        dto.setAdmin(false);

        List<User> result = mapper.toEntity(Arrays.asList(null, dto));

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNotNull(result.get(1));
        assertEquals(1L, result.get(1).getId());
    }

    @Test
    void testToDtoList_withNullElement_shouldIncludeNull() {
        User user = new User()
            .setId(2L)
            .setEmail("john.doe@example.com")
            .setFirstName("John")
            .setLastName("Doe")
            .setPassword("securePassword")
            .setAdmin(false);

        List<UserDto> result = mapper.toDto(Arrays.asList(null, user));

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNotNull(result.get(1));
        assertEquals(2L, result.get(1).getId());
    }

}
