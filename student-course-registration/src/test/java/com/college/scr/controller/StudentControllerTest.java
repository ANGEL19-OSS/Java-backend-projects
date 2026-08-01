package com.college.scr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.college.scr.dto.*;
import com.college.scr.exception.DuplicateEnrollmentException;
import com.college.scr.exception.GlobalExceptionHandler;
import com.college.scr.exception.ResourceNotFoundException;
import com.college.scr.service.CourseRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRegistrationService registrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentRequestDTO studentReq;
    private StudentResponseDTO studentRes;

    @BeforeEach
    void setUp() {
        studentReq = StudentRequestDTO.builder()
                .name("Aarav Sharma")
                .email("aarav@college.edu")
                .department("CS")
                .build();

        studentRes = StudentResponseDTO.builder()
                .id(1L)
                .name("Aarav Sharma")
                .email("aarav@college.edu")
                .department("CS")
                .courses(Set.of())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/students - Success 201")
    void registerStudent_Success() throws Exception {
        when(registrationService.registerStudent(any(StudentRequestDTO.class))).thenReturn(studentRes);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/students/{studentId}/enroll/{courseId} - Success 200")
    void enrollStudent_Success() throws Exception {
        when(registrationService.enrollStudentInCourse(1L, 10L)).thenReturn(studentRes);

        mockMvc.perform(post("/api/v1/students/1/enroll/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/students/{studentId}/enroll/{courseId} - Duplicate Enrollment 409")
    void enrollStudent_Duplicate_Conflict() throws Exception {
        when(registrationService.enrollStudentInCourse(1L, 10L))
                .thenThrow(new DuplicateEnrollmentException("Student 'Aarav' is already enrolled in course 'CS101'."));

        mockMvc.perform(post("/api/v1/students/1/enroll/10"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Student 'Aarav' is already enrolled in course 'CS101'."));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Not Found 404")
    void getStudentById_NotFound() throws Exception {
        when(registrationService.getStudentById(999L))
                .thenThrow(new ResourceNotFoundException("Student not found with ID: 999"));

        mockMvc.perform(get("/api/v1/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
