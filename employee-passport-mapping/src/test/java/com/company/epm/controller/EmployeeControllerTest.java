package com.company.epm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.epm.dto.*;
import com.company.epm.exception.DuplicateEmailException;
import com.company.epm.exception.GlobalExceptionHandler;
import com.company.epm.exception.ResourceNotFoundException;
import com.company.epm.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequestDTO validRequest;
    private EmployeeResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        PassportRequestDTO passportReq = PassportRequestDTO.builder()
                .passportNumber("GB1234567")
                .country("UK")
                .expiryDate(LocalDate.now().plusYears(5))
                .build();

        validRequest = EmployeeRequestDTO.builder()
                .name("Oliver Brown")
                .email("oliver.brown@company.com")
                .department("Operations")
                .passport(passportReq)
                .build();

        PassportResponseDTO passportRes = PassportResponseDTO.builder()
                .id(5L)
                .passportNumber("GB1234567")
                .country("UK")
                .expiryDate(LocalDate.now().plusYears(5))
                .build();

        validResponse = EmployeeResponseDTO.builder()
                .id(1L)
                .name("Oliver Brown")
                .email("oliver.brown@company.com")
                .department("Operations")
                .passport(passportRes)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/employees - Success 201")
    void createEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.passport.passportNumber").value("GB1234567"));
    }

    @Test
    @DisplayName("POST /api/v1/employees - Duplicate Email 409")
    void createEmployee_DuplicateEmail() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenThrow(new DuplicateEmailException("Employee with email 'oliver.brown@company.com' already exists."));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Employee with email 'oliver.brown@company.com' already exists."));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Found 200")
    void getEmployeeById_Found() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Not Found 404")
    void getEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID: 999"));

        mockMvc.perform(get("/api/v1/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Employee not found with ID: 999"));
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Success 200")
    void deleteEmployee_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
