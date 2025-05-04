package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacher);
        session.setDescription("description");
        String sessionJson = objectMapper.writeValueAsString(session);

        MvcResult result = mockMvc.perform(post("/api/session/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionJson))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Session returnedSession = objectMapper.readValue(content, Session.class);

        assertEquals(session.getName(), returnedSession.getName());
        assertEquals(session.getDescription(), returnedSession.getDescription());
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "password")
    public void testCreateShouldFailWithBadRequestWhenTeacherDoesntExist() throws Exception {
        Session session = new Session();
        session.setName("yoga session");
        session.setDate(Date.from(Instant.now()));
        session.setTeacher(teacherRepository.getById(123456789L));
        session.setDescription("description");
        String sessionJson = objectMapper.writeValueAsString(session);

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

}