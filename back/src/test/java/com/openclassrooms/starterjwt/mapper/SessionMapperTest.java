package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionMapperTest {

    private SessionMapperImpl sessionMapper;
    private TeacherService teacherService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        teacherService = mock(TeacherService.class);
        userService = mock(UserService.class);

        sessionMapper = new SessionMapperImpl();
        sessionMapper.teacherService = teacherService;
        sessionMapper.userService = userService;
    }

    @Test
    void testToEntity() {
        // Arrange
        Long teacherId = 1L;
        Long userId = 2L;

        SessionDto dto = new SessionDto();
        dto.setDescription("Session test");
        dto.setTeacher_id(teacherId);
        dto.setUsers(Arrays.asList(userId));

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        User user = new User();
        user.setId(userId);

        when(teacherService.findById(teacherId)).thenReturn(teacher);
        when(userService.findById(userId)).thenReturn(user);

        // Act
        Session entity = sessionMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals("Session test", entity.getDescription());
        assertNotNull(entity.getTeacher());
        assertEquals(teacherId, entity.getTeacher().getId());
        assertNotNull(entity.getUsers());
        assertEquals(1, entity.getUsers().size());
        assertEquals(userId, entity.getUsers().get(0).getId());
    }

    @Test
    void testToDto() {
        // Arrange
        Long teacherId = 1L;
        Long userId = 2L;

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setDescription("Mapped session");
        session.setTeacher(teacher);
        session.setUsers(Collections.singletonList(user));

        // Act
        SessionDto dto = sessionMapper.toDto(session);

        // Assert
        assertNotNull(dto);
        assertEquals("Mapped session", dto.getDescription());
        assertEquals(teacherId, dto.getTeacher_id());
        assertNotNull(dto.getUsers());
        assertEquals(1, dto.getUsers().size());
        assertEquals(userId, dto.getUsers().get(0));
    }




    @Test
    void testToDtoList() {
        Session session = new Session()
            .setId(1L)
            .setDescription("Yoga")
            .setTeacher(new Teacher().setId(10L))
            .setUsers(List.of(new User().setId(100L)));

        List<SessionDto> result = sessionMapper.toDto(List.of(session));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Yoga", result.get(0).getDescription());
        assertEquals(10L, result.get(0).getTeacher_id());
        assertEquals(1, result.get(0).getUsers().size());
    }

    @Test
    void testToEntityList() {
        SessionDto dto = new SessionDto();
        dto.setDescription("Pilates");
        dto.setTeacher_id(20L);
        dto.setUsers(List.of(200L));

        when(teacherService.findById(20L)).thenReturn(new Teacher().setId(20L));
        when(userService.findById(200L)).thenReturn(new User().setId(200L));

        List<Session> result = sessionMapper.toEntity(List.of(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pilates", result.get(0).getDescription());
        assertEquals(20L, result.get(0).getTeacher().getId());
        assertEquals(1, result.get(0).getUsers().size());
        assertEquals(200L, result.get(0).getUsers().get(0).getId());
    }

    @Test
    void testToEntityWithNullUsersAndTeacher() {
        SessionDto dto = new SessionDto();
        dto.setDescription("Nulls test");
        dto.setUsers(null);
        dto.setTeacher_id(null);

        Session entity = sessionMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Nulls test", entity.getDescription());
        assertNull(entity.getTeacher());
        assertNotNull(entity.getUsers()); // should default to empty list
        assertTrue(entity.getUsers().isEmpty());
    }

    //null branches

    @Test
    void testToDto_withEmptyUserList_shouldReturnEmptyList() {
        Session session = new Session();
        session.setDescription("Empty users");
        session.setTeacher(new Teacher().setId(1L));
        session.setUsers(Collections.emptyList());

        SessionDto dto = sessionMapper.toDto(session);

        assertNotNull(dto);
        assertEquals("Empty users", dto.getDescription());
        assertEquals(1L, dto.getTeacher_id());
        assertNotNull(dto.getUsers());
        assertTrue(dto.getUsers().isEmpty());
    }

    @Test
    void testToDto_shouldHandleNullSessionAndNullTeacherAndNullUsers() {
        // ➤ Case: null session
        SessionDto dtoNull = sessionMapper.toDto((Session) null);
        assertNull(dtoNull);

        // ➤ Case: session with null teacher
        Session sessionNoTeacher = new Session();
        sessionNoTeacher.setDescription("test");
        sessionNoTeacher.setTeacher(null);
        sessionNoTeacher.setUsers(null); // to also test empty user branch

        SessionDto dtoNoTeacher = sessionMapper.toDto(sessionNoTeacher);
        assertNotNull(dtoNoTeacher);
        assertNull(dtoNoTeacher.getTeacher_id());
        assertNotNull(dtoNoTeacher.getUsers());
        assertTrue(dtoNoTeacher.getUsers().isEmpty());
    }
}
