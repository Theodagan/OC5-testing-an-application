package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("Test");
        teacher1.setLastName("Teacher");
        teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Test2");
        teacher2.setLastName("Teacher2");
        teacherRepository.save(teacher2);
    }

    @Test
    @DisplayName("Find all teachers")
    void findAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Test"))
                .andExpect(jsonPath("$[1].firstName").value("Test2"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        assertNotNull(content);
        List<Teacher> teachers = teacherRepository.findAll();
        assertEquals(2, teachers.size());
    }

    @Test
    @DisplayName("Find teacher by id")
    void findById() throws Exception {
        Teacher teacher = teacherRepository.findAll().get(0);
        mockMvc.perform(get("/api/teacher/" + teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(teacher.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(teacher.getLastName()))
                .andReturn();
    }
}