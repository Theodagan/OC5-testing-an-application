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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Teacher teacher;
    private User user;

    @BeforeEach
    public void setUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
        teacher = new Teacher();
        teacher.setLastName("teacher");
        teacher.setFirstName("teacher");
        teacherRepository.save(teacher);

        user = new User();
        user.setLastName("user");
        user.setFirstName("user");
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setAdmin(true);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testFindById() throws Exception {
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("description");
        sessionRepository.save(session);

        MvcResult result = mockMvc.perform(get("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(session.getId()))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Session returnedSession = objectMapper.readValue(content, Session.class);
        assertEquals(session.getId(), returnedSession.getId());
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testDelete() throws Exception {
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("description");
        sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertFalse(sessionRepository.existsById(session.getId()));
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testCreate() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("yoga session");
        sessionDto.setDate(Date.from(Instant.now()));
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setDescription("description");

        String json = objectMapper.writeValueAsString(sessionDto);

        MvcResult result = mockMvc.perform(post("/api/session/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        SessionDto returned = objectMapper.readValue(content, SessionDto.class);

        assertEquals(sessionDto.getName(), returned.getName());
        assertEquals(sessionDto.getDescription(), returned.getDescription());
    }


    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testCreateShouldFailWithBadRequestWhenTeacherDoesntExist() throws Exception {
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
    
        // FIX: do not use getById on non-existent entity
        Teacher nonExistentTeacher = teacherRepository.findById(123456789L).orElse(null);
        session.setTeacher(nonExistentTeacher); // This will be null
    
        session.setDescription("description");
    
        String sessionJson = objectMapper.writeValueAsString(session); // This still fails if 'teacher' is null and serialization expects it
    
        mockMvc.perform(post("/api/session/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(sessionJson))
        .andExpect(status().isBadRequest());
    }
    

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testFindAll() throws Exception {
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("description");
        sessionRepository.save(session);

        mockMvc.perform(get("/api/session/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }






    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testUpdateSession() throws Exception {
        Session session = new Session();
        session.setName("old session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("old description");
        session = sessionRepository.save(session);

        SessionDto dto = new SessionDto();
        dto.setId(session.getId());
        dto.setName("updated session");
        dto.setDate(session.getDate());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("updated description");

        String json = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(put("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Session updated = objectMapper.readValue(content, Session.class);

        assertEquals("updated session", updated.getName());
        assertEquals("updated description", updated.getDescription());
    }

    
    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testParticipateInSession() throws Exception {
        Session session = new Session();
        session.setName("yoga");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("desc");
        session = sessionRepository.save(session);
    
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
    
        Session updated = sessionRepository.findById(session.getId()).orElseThrow(null);
        assertTrue(updated.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }
    

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testCancelParticipation() throws Exception {
        Session session = new Session();
        session.setName("yoga");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("desc");

        session.setUsers(new ArrayList<>()); // safely initialize users list
        session.getUsers().add(user);

        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        Session updated = sessionRepository.findById(session.getId())
            .orElseThrow(() -> new IllegalStateException("Session not found"));

        assertFalse(updated.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testParticipateWithInvalidIdFormat() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/session/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testFindById_InvalidFormat() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testDelete_NotFound() throws Exception {
        mockMvc.perform(delete("/api/session/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testDelete_InvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testUpdate_InvalidIdFormat() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("Invalid Update");
        dto.setDate(new Date());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("desc");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/session/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testCancelParticipation_InvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/session/abc/participate/xyz"))
                .andExpect(status().isBadRequest());
    }


}