package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SessionControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    private Teacher teacher;
    private User user;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        teacher = new Teacher();
        teacher.setFirstName("teacher");
        teacher.setLastName("teacher");
        teacher = teacherRepository.save(teacher);

        user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("user");
        user.setLastName("user");
        user.setPassword("password");
        user.setAdmin(true);
        user = userRepository.save(user);
    }

    private Session createSession() {
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("description");
        return sessionRepository.save(session);
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Find session by ID")
    void testFindById() throws Exception {
        Session session = createSession();

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Find session by non-existent ID returns 404")
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/session/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Find session by invalid ID format returns 400")
    void testFindById_InvalidFormat() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Create a session")
    void testCreate() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("yoga session");
        dto.setDate(Date.from(Instant.now()));
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("description");

        MvcResult result = mockMvc.perform(post("/api/session/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        SessionDto created = objectMapper.readValue(result.getResponse().getContentAsString(), SessionDto.class);
        assertEquals(dto.getName(), created.getName());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Create session fails if teacher doesn't exist")
    void testCreateShouldFailWithBadRequestWhenTeacherDoesntExist() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("yoga session");
        dto.setDate(Date.from(Instant.now()));
        dto.setTeacher_id(999L);
        dto.setDescription("description");

        mockMvc.perform(post("/api/session/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Delete a session")
    void testDelete() throws Exception {
        Session session = createSession();

        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());

        assertFalse(sessionRepository.existsById(session.getId()));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Delete non-existent session returns 404")
    void testDelete_NotFound() throws Exception {
        mockMvc.perform(delete("/api/session/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Delete with invalid format returns 400")
    void testDelete_InvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Get all sessions")
    void testFindAll() throws Exception {
        createSession();

        mockMvc.perform(get("/api/session/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Update a session")
    void testUpdateSession() throws Exception {
        Session session = createSession();

        SessionDto dto = new SessionDto();
        dto.setId(session.getId());
        dto.setName("updated session");
        dto.setDate(session.getDate());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("updated description");

        MvcResult result = mockMvc.perform(put("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        Session updated = objectMapper.readValue(result.getResponse().getContentAsString(), Session.class);
        assertEquals("updated session", updated.getName());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Update with invalid ID format returns 400")
    void testUpdate_InvalidIdFormat() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("Invalid");
        dto.setDate(new Date());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("desc");

        mockMvc.perform(put("/api/session/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Participate in a session")
    void testParticipateInSession() throws Exception {
        Session session = createSession();

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        Session updated = sessionRepository.findById(session.getId()).orElseThrow(null);
        assertTrue(updated.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Cancel participation")
    void testCancelParticipation() throws Exception {
        Session session = createSession();
        session.setUsers(new ArrayList<>());
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        Session updated = sessionRepository.findById(session.getId()).orElseThrow(null);
        assertFalse(updated.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Participate with invalid ID format returns 400")
    void testParticipateWithInvalidIdFormat() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Cancel participation with invalid ID format returns 400")
    void testCancelParticipation_InvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/session/abc/participate/xyz"))
                .andExpect(status().isBadRequest());
    }
}
