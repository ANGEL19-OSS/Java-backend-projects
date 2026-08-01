package com.college.sms.controller;

import com.college.sms.dto.StudentRequestDTO;
import com.college.sms.dto.StudentResponseDTO;
import com.college.sms.exception.DuplicateEmailException;
import com.college.sms.exception.GlobalExceptionHandler;
import com.college.sms.exception.ResourceNotFoundException;
import com.college.sms.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentRequestDTO validRequest;
    private StudentResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        validRequest = StudentRequestDTO.builder()
                .name("Arjun Sharma")
                .email("arjun.sharma@college.edu")
                .phoneNumber("9876543210")
                .department("Computer Science")
                .yearOfStudy(2)
                .cgpa(8.75)
                .build();

        validResponse = StudentResponseDTO.builder()
                .id(1L)
                .name("Arjun Sharma")
                .email("arjun.sharma@college.edu")
                .phoneNumber("9876543210")
                .department("Computer Science")
                .yearOfStudy(2)
                .cgpa(8.75)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/students - Success 201")
    void createStudent_Success() throws Exception {
        when(studentService.createStudent(any(StudentRequestDTO.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Arjun Sharma"));
    }

    @Test
    @DisplayName("POST /api/v1/students - Validation Error 400")
    void createStudent_ValidationError() throws Exception {
        StudentRequestDTO invalidRequest = StudentRequestDTO.builder()
                .name("")
                .email("invalid-email")
                .phoneNumber("123")
                .department("")
                .yearOfStudy(10)
                .cgpa(15.0)
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/v1/students - Duplicate Email 409")
    void createStudent_DuplicateEmail() throws Exception {
        when(studentService.createStudent(any(StudentRequestDTO.class)))
                .thenThrow(new DuplicateEmailException("Student with email 'arjun.sharma@college.edu' already exists."));

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Student with email 'arjun.sharma@college.edu' already exists."));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Found 200")
    void getStudentById_Found() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Not Found 404")
    void getStudentById_NotFound() throws Exception {
        when(studentService.getStudentById(999L))
                .thenThrow(new ResourceNotFoundException("Student not found with ID: 999"));

        mockMvc.perform(get("/api/v1/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Student not found with ID: 999"));
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Success 200")
    void deleteStudent_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/students/department/{department} - Success 200")
    void getStudentsByDepartment_Success() throws Exception {
        when(studentService.getStudentsByDepartment("Computer Science"))
                .thenReturn(List.of(validResponse));

        mockMvc.perform(get("/api/v1/students/department/Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].department").value("Computer Science"));
    }

    @Test
    @DisplayName("GET /api/v1/students/cgpa?min=8.0 - Success 200")
    void getStudentsByCgpa_Success() throws Exception {
        when(studentService.getStudentsByCgpaGreaterThan(8.0))
                .thenReturn(List.of(validResponse));

        mockMvc.perform(get("/api/v1/students/cgpa").param("min", "8.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].cgpa").value(8.75));
    }
}
